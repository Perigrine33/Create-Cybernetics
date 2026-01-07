package com.perigrine3.createcybernetics.screen.custom;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.screen.ModMenuTypes;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;
import java.util.Optional;

public class ExpandedInventoryMenu extends AbstractContainerMenu {

    public static final int SLOT_RESULT = 0;

    private static final int SLOT_CRAFT_START = 1;
    private static final int SLOT_CRAFT_COUNT = 9;

    private static final int SLOT_ARMOR_START = SLOT_CRAFT_START + SLOT_CRAFT_COUNT; // 10
    private static final int SLOT_ARMOR_COUNT = 4;

    private static final int SLOT_INV_START = SLOT_ARMOR_START + SLOT_ARMOR_COUNT;   // 14
    private static final int SLOT_INV_COUNT = 27;

    private static final int SLOT_HOTBAR_START = SLOT_INV_START + SLOT_INV_COUNT;    // 41
    private static final int SLOT_HOTBAR_COUNT = 9;

    private static final int SLOT_OFFHAND = SLOT_HOTBAR_START + SLOT_HOTBAR_COUNT;   // 50

    private final Player player;
    private final ContainerLevelAccess access;

    private final CraftingContainer craftMatrix;
    private final ResultContainer craftResult = new ResultContainer();

    /* ===================== DATA SHARD SLOTS (PERSISTED) ===================== */

    private static final int DATA_SHARD_SLOT_COUNT = PlayerCyberwareData.CHIPWARE_SLOT_COUNT;

    // Screen-space coordinates (must match what you render in ExpandedInventoryScreen)
    public static final int DATA_SHARD_X = 77;
    public static final int DATA_SHARD_Y = 8;
    public static final int DATA_SHARD_SPACING = 18;

    private final boolean hasChipwareSlots;
    private final Container dataShardInv;

    private int dataShardStart = -1;
    private int dataShardEnd = -1;

    public ExpandedInventoryMenu(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        this(containerId, playerInv, ContainerLevelAccess.NULL);
    }

    public ExpandedInventoryMenu(int containerId, Inventory playerInv) {
        this(containerId, playerInv, ContainerLevelAccess.NULL);
    }

    public ExpandedInventoryMenu(int containerId, Inventory playerInv, ContainerLevelAccess access) {
        super(ModMenuTypes.EXPANDED_INVENTORY_MENU.get(), containerId);
        this.player = playerInv.player;
        this.access = access;

        this.craftMatrix = new SimpleCraftingContainer(this, 3, 3);

        this.hasChipwareSlots = hasChipwareInstalled(this.player);

        // IMPORTANT CHANGE: use persisted backing container
        this.dataShardInv = this.hasChipwareSlots ? new ChipwareContainer(this.player, this) : null;

        addSlot(new ResultSlot(playerInv.player, craftMatrix, craftResult, SLOT_RESULT, 154, 28));

        int craftX = 98;
        int craftY = 18;
        int idx = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlot(new Slot((Container) craftMatrix, idx++, craftX + col * 18, craftY + row * 18));
            }
        }

        addArmorSlots(playerInv);
        addPlayerInventory(playerInv);
        addPlayerHotbar(playerInv);
        addOffhandSlot(playerInv);

        // IMPORTANT: add AFTER offhand so your existing slot index layout remains unchanged.
        addDataShardSlotsIfPresent();

        if (this.player instanceof ServerPlayer sp) {
            updateCraftingResult(sp, sp.serverLevel());

            // Push initial chipware slot contents immediately (avoids “empty until first interaction”)
            if (hasDataShardSlots()) {
                for (int i = 0; i < DATA_SHARD_SLOT_COUNT; i++) {
                    ItemStack st = dataShardInv.getItem(i);
                    sp.connection.send(new ClientboundContainerSetSlotPacket(
                            this.containerId,
                            this.incrementStateId(),
                            dataShardStart + i,
                            st
                    ));
                }
            }
        }
    }

    public boolean hasDataShardSlots() {
        return hasChipwareSlots && dataShardStart >= 0 && dataShardEnd > dataShardStart;
    }

    private void addDataShardSlotsIfPresent() {
        if (!hasChipwareSlots || dataShardInv == null) return;

        this.dataShardStart = this.slots.size();
        addSlot(new DataShardSlot(dataShardInv, 0, DATA_SHARD_X, DATA_SHARD_Y));
        addSlot(new DataShardSlot(dataShardInv, 1, DATA_SHARD_X, DATA_SHARD_Y + DATA_SHARD_SPACING));
        this.dataShardEnd = this.slots.size();
    }

    @Override
    public void slotsChanged(Container changed) {
        super.slotsChanged(changed);

        if (changed != craftMatrix) return;
        if (!(player instanceof ServerPlayer sp)) return;

        access.execute((level, pos) -> {
            if (level instanceof ServerLevel sl) {
                updateCraftingResult(sp, sl);
            }
        });
    }

    private void updateCraftingResult(ServerPlayer sp, ServerLevel level) {
        CraftingInput input = craftMatrix.asCraftInput();

        ItemStack result = ItemStack.EMPTY;
        Optional<RecipeHolder<CraftingRecipe>> match =
                level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level);

        if (match.isPresent()) {
            RecipeHolder<CraftingRecipe> holder = match.get();

            if (craftResult.setRecipeUsed(level, sp, holder)) {
                result = holder.value().assemble(input, level.registryAccess());
            }
        } else {
            craftResult.setRecipeUsed(null);
        }

        craftResult.setItem(0, result);

        sp.connection.send(new ClientboundContainerSetSlotPacket(
                this.containerId,
                this.incrementStateId(),
                SLOT_RESULT,
                result
        ));
    }

    private void addArmorSlots(Inventory playerInv) {
        int x = 8;
        int y = 8;

        addSlot(new EquipmentSlotSlot(playerInv, 39, x, y + 0 * 18, player, EquipmentSlot.HEAD, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET));
        addSlot(new EquipmentSlotSlot(playerInv, 38, x, y + 1 * 18, player, EquipmentSlot.CHEST, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE));
        addSlot(new EquipmentSlotSlot(playerInv, 37, x, y + 2 * 18, player, EquipmentSlot.LEGS, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS));
        addSlot(new EquipmentSlotSlot(playerInv, 36, x, y + 3 * 18, player, EquipmentSlot.FEET, InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS));
    }

    private void addOffhandSlot(Inventory playerInv) {
        Slot s = new Slot(playerInv, 40, 77, 62);
        s.setBackground(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
        addSlot(s);
    }

    private void addPlayerInventory(Inventory inv) {
        int invX = 8;
        int invY = 84;

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                addSlot(new Slot(inv, col + row * 9 + 9, invX + col * 18, invY + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inv) {
        int invX = 8;
        int hotbarY = 84 + 58;

        for (int col = 0; col < 9; ++col) {
            addSlot(new Slot(inv, col, invX + col * 18, hotbarY));
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide) {
            clearContainer(player, (Container) craftMatrix);
            craftResult.clearContent();

            // IMPORTANT CHANGE: DO NOT clear chipware slots on close.
            // They are now persisted in PlayerCyberwareData and should remain installed.
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        int craftEnd = SLOT_CRAFT_START + SLOT_CRAFT_COUNT;
        int armorEnd = SLOT_ARMOR_START + SLOT_ARMOR_COUNT;
        int invEnd = SLOT_INV_START + SLOT_INV_COUNT;
        int hotbarEnd = SLOT_HOTBAR_START + SLOT_HOTBAR_COUNT;

        int allEnd = hasDataShardSlots() ? dataShardEnd : (SLOT_OFFHAND + 1);

        // Result slot
        if (index == SLOT_RESULT) {
            if (!moveItemStackTo(stack, SLOT_INV_START, allEnd, true)) return ItemStack.EMPTY;
            slot.onQuickCraft(stack, copy);

            // Crafting grid
        } else if (index >= SLOT_CRAFT_START && index < craftEnd) {
            if (!moveItemStackTo(stack, SLOT_INV_START, allEnd, false)) return ItemStack.EMPTY;

            // Armor
        } else if (index >= SLOT_ARMOR_START && index < armorEnd) {
            if (!moveItemStackTo(stack, SLOT_INV_START, allEnd, false)) return ItemStack.EMPTY;

            // Offhand
        } else if (index == SLOT_OFFHAND) {
            if (!moveItemStackTo(stack, SLOT_INV_START, hotbarEnd, false)) return ItemStack.EMPTY;

            // Data shard slots (new)
        } else if (hasDataShardSlots() && index >= dataShardStart && index < dataShardEnd) {
            if (!moveItemStackTo(stack, SLOT_INV_START, hotbarEnd, false)) return ItemStack.EMPTY;

            // Player inv/hotbar
        } else if (index >= SLOT_INV_START && index < hotbarEnd) {
            // If shard item and shard slots exist, try those first
            if (hasDataShardSlots() && stack.is(ModTags.Items.DATA_SHARDS)) {
                moveItemStackTo(stack, dataShardStart, dataShardEnd, false);
            }

            EquipmentSlot eq = player.getEquipmentSlotForItem(stack);

            if (eq.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                int target = armorTargetIndex(eq);
                if (target != -1 && moveItemStackTo(stack, target, target + 1, false)) {
                } else if (index < invEnd) {
                    if (!moveItemStackTo(stack, SLOT_HOTBAR_START, hotbarEnd, false)) return ItemStack.EMPTY;
                } else {
                    if (!moveItemStackTo(stack, SLOT_INV_START, invEnd, false)) return ItemStack.EMPTY;
                }
            } else if (eq == EquipmentSlot.OFFHAND) {
                if (moveItemStackTo(stack, SLOT_OFFHAND, SLOT_OFFHAND + 1, false)) {
                } else if (index < invEnd) {
                    if (!moveItemStackTo(stack, SLOT_HOTBAR_START, hotbarEnd, false)) return ItemStack.EMPTY;
                } else {
                    if (!moveItemStackTo(stack, SLOT_INV_START, invEnd, false)) return ItemStack.EMPTY;
                }
            } else {
                if (index < invEnd) {
                    if (!moveItemStackTo(stack, SLOT_HOTBAR_START, hotbarEnd, false)) return ItemStack.EMPTY;
                } else {
                    if (!moveItemStackTo(stack, SLOT_INV_START, invEnd, false)) return ItemStack.EMPTY;
                }
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        if (stack.getCount() == copy.getCount()) return ItemStack.EMPTY;

        slot.onTake(player, stack);
        return copy;
    }

    private static int armorTargetIndex(EquipmentSlot eq) {
        return switch (eq) {
            case HEAD -> SLOT_ARMOR_START + 0;
            case CHEST -> SLOT_ARMOR_START + 1;
            case LEGS -> SLOT_ARMOR_START + 2;
            case FEET -> SLOT_ARMOR_START + 3;
            default -> -1;
        };
    }

    public Container getCraftMatrix() {
        return (Container) craftMatrix;
    }

    public Container getCraftResult() {
        return craftResult;
    }

    public Player getPlayer() {
        return player;
    }

    public ContainerLevelAccess getAccess() {
        return access;
    }

    private static boolean hasChipwareInstalled(Player player) {
        if (player == null) return false;
        if (!player.hasData(ModAttachments.CYBERWARE)) return false;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        return data.hasSpecificItem(ModItems.BRAINUPGRADES_CHIPWARESLOTS.get(), CyberwareSlot.BRAIN);
    }

    private static final class DataShardSlot extends Slot {
        private DataShardSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return !stack.isEmpty() && stack.is(ModTags.Items.DATA_SHARDS);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }

    /**
     * Persisted backing container for the 2 chipware/data-shard slots.
     * This writes directly into PlayerCyberwareData so:
     * - closing the menu does not wipe the items
     * - relogging restores them via attachment NBT
     */
    private static final class ChipwareContainer implements Container {
        private final Player player;
        private final AbstractContainerMenu menu;

        private ChipwareContainer(Player player, AbstractContainerMenu menu) {
            this.player = player;
            this.menu = menu;
        }

        private PlayerCyberwareData data() {
            if (player == null) return null;
            if (!player.hasData(ModAttachments.CYBERWARE)) return null;
            return player.getData(ModAttachments.CYBERWARE);
        }

        @Override
        public int getContainerSize() {
            return DATA_SHARD_SLOT_COUNT;
        }

        @Override
        public boolean isEmpty() {
            for (int i = 0; i < getContainerSize(); i++) {
                if (!getItem(i).isEmpty()) return false;
            }
            return true;
        }

        @Override
        public ItemStack getItem(int slot) {
            PlayerCyberwareData d = data();
            if (d == null) return ItemStack.EMPTY;
            return d.getChipwareStack(slot);
        }

        @Override
        public ItemStack removeItem(int slot, int count) {
            if (count <= 0) return ItemStack.EMPTY;

            ItemStack cur = getItem(slot);
            if (cur.isEmpty()) return ItemStack.EMPTY;

            // Max stack size is 1, so any remove is effectively "take it"
            setItem(slot, ItemStack.EMPTY);
            return cur;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            ItemStack cur = getItem(slot);
            if (cur.isEmpty()) return ItemStack.EMPTY;

            PlayerCyberwareData d = data();
            if (d != null) {
                d.setChipwareStack(slot, ItemStack.EMPTY);
            }
            return cur;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            PlayerCyberwareData d = data();
            if (d == null) return;

            d.setChipwareStack(slot, stack);
            setChanged();
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public void setChanged() {
            // Ensure menu/network sees the change
            menu.slotsChanged(this);
            menu.broadcastChanges();
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        @Override
        public void clearContent() {
            for (int i = 0; i < getContainerSize(); i++) {
                setItem(i, ItemStack.EMPTY);
            }
        }
    }

    private static final class EquipmentSlotSlot extends Slot {
        private final Player owner;
        private final EquipmentSlot equip;

        private EquipmentSlotSlot(Container container, int index, int x, int y, Player owner, EquipmentSlot equip, net.minecraft.resources.ResourceLocation emptySprite) {
            super(container, index, x, y);
            this.owner = owner;
            this.equip = equip;
            this.setBackground(InventoryMenu.BLOCK_ATLAS, emptySprite);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return owner.getEquipmentSlotForItem(stack) == equip;
        }

        @Override
        public boolean mayPickup(Player player) {
            ItemStack stack = this.getItem();
            if (stack.isEmpty()) return true;
            if (player.isCreative()) return true;

            var enchReg = player.level().registryAccess()
                    .lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);

            var binding = enchReg.getOrThrow(Enchantments.BINDING_CURSE);

            return EnchantmentHelper.getItemEnchantmentLevel(binding, stack) <= 0;
        }
    }

    private static final class SimpleCraftingContainer implements CraftingContainer {

        private final AbstractContainerMenu menu;
        private final int w;
        private final int h;
        private final NonNullList<ItemStack> items;

        private SimpleCraftingContainer(AbstractContainerMenu menu, int w, int h) {
            this.menu = menu;
            this.w = w;
            this.h = h;
            this.items = NonNullList.withSize(w * h, ItemStack.EMPTY);
        }

        @Override
        public void fillStackedContents(StackedContents stackedContents) {}

        @Override
        public void clearContent() {
            for (int i = 0; i < items.size(); i++) items.set(i, ItemStack.EMPTY);
            setChanged();
        }

        @Override
        public int getContainerSize() {
            return items.size();
        }

        @Override
        public boolean isEmpty() {
            for (ItemStack s : items) if (!s.isEmpty()) return false;
            return true;
        }

        @Override
        public ItemStack getItem(int i) {
            return (i >= 0 && i < items.size()) ? items.get(i) : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int i, int count) {
            ItemStack out = ContainerHelper.removeItem(items, i, count);
            if (!out.isEmpty()) setChanged();
            return out;
        }

        @Override
        public ItemStack removeItemNoUpdate(int i) {
            ItemStack out = ContainerHelper.takeItem(items, i);
            if (!out.isEmpty()) setChanged();
            return out;
        }

        @Override
        public void setItem(int i, ItemStack stack) {
            if (i >= 0 && i < items.size()) {
                items.set(i, stack);
                setChanged();
            }
        }

        @Override
        public void setChanged() {
            menu.slotsChanged((Container) this);
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        @Override
        public int getWidth() {
            return w;
        }

        @Override
        public int getHeight() {
            return h;
        }

        @Override
        public List<ItemStack> getItems() {
            return items;
        }
    }
}
