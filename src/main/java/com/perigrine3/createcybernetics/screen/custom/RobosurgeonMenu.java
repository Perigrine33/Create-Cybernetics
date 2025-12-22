package com.perigrine3.createcybernetics.screen.custom;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.block.ModBlocks;
import com.perigrine3.createcybernetics.block.entity.RobosurgeonBlockEntity;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.common.surgery.DefaultOrgans;
import com.perigrine3.createcybernetics.common.surgery.RobosurgeonSlotMap;
import com.perigrine3.createcybernetics.screen.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

public class RobosurgeonMenu extends AbstractContainerMenu {
    public final RobosurgeonBlockEntity blockEntity;
    private final Level level;
    private final List<RobosurgeonSlotItemHandler> robosurgeonSlots = new ArrayList<>();
    public int getTeInventoryFirstSlotIndex() {
        return TE_INVENTORY_FIRST_SLOT_INDEX;
    }
    private static final int INVENTORY_Y_OFFSET = 56;

    public RobosurgeonMenu(int containerId, Inventory inv, FriendlyByteBuf buf) {
        super(ModMenuTypes.ROBOSURGEON_MENU.get(), containerId);

        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inv.player.level().getBlockEntity(pos);

        if (!(be instanceof RobosurgeonBlockEntity rs)) {
            throw new IllegalStateException("Invalid block entity");
        }

        this.blockEntity = rs;
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addRobosurgeonSlots(); // IMPORTANT: only once
    }

    public RobosurgeonMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(ModMenuTypes.ROBOSURGEON_MENU.get(), containerId);
        this.blockEntity = ((RobosurgeonBlockEntity) blockEntity);
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addRobosurgeonSlots();
        populateFromPlayer(inv.player);
    }

    // -------------------------------------------------
    // STATE HELPERS (backed by block entity arrays)
    // -------------------------------------------------

    public boolean isInstalled(int index) {
        return blockEntity.isInstalled(index);
    }

    public boolean isStaged(int index) {
        return blockEntity.isStaged(index);
    }

    public boolean isMarkedForRemoval(int index) {
        return blockEntity.isMarkedForRemoval(index);
    }

    public void toggleMarkedForRemoval(int index) {
        blockEntity.toggleMarkedForRemoval(index);
    }

    public void setStaged(int index, boolean value) {
        blockEntity.setStaged(index, value);
    }

    public void setInstalled(int index, boolean value) {
        blockEntity.setInstalled(index, value);
    }

    // Used to force marked/unmarked without needing a dedicated setter in BE
    private void setMarkedForRemoval(int invIndex, boolean value) {
        boolean current = isMarkedForRemoval(invIndex);
        if (current != value) {
            toggleMarkedForRemoval(invIndex);
        }
    }

    // -------------------------------------------------
    // SLOT LAYOUT
    // -------------------------------------------------

    private void addSlotColumn(int startIndex, int count, int x, int startY, CyberwareSlot type) {
        for (int i = 0; i < count; i++) {
            RobosurgeonSlotItemHandler slot =
                    new RobosurgeonSlotItemHandler(
                            blockEntity.inventory,
                            startIndex + i,
                            x + 1,
                            startY - (i * 18) + 1,
                            type
                    );

            robosurgeonSlots.add(slot);
            this.addSlot(slot);
        }
    }

    private void addRobosurgeonSlots() {
        // HEAD
        addSlotColumn(0,  5, 151, 110, CyberwareSlot.BRAIN); // BRAIN
        addSlotColumn(5,  5, 151, 110, CyberwareSlot.EYES); // EYES
        // TORSO
        addSlotColumn(10, 6, 151, 110, CyberwareSlot.HEART); // HEART
        addSlotColumn(16, 6, 151, 110, CyberwareSlot.LUNGS); // LUNGS
        addSlotColumn(22, 6, 151, 110, CyberwareSlot.ORGANS); // ORGANS
        // ARMS
        addSlotColumn(28, 6, 43,  110, CyberwareSlot.RARM); // RIGHT ARM
        addSlotColumn(34, 6, 115, 110, CyberwareSlot.LARM); // LEFT ARM
        // LEGS
        addSlotColumn(40, 5, 43,  110, CyberwareSlot.RLEG); // RIGHT LEG
        addSlotColumn(45, 5, 115, 110, CyberwareSlot.LLEG); // LEFT LEG
        // SKIN / STRUCTURE
        addSlotColumn(50, 5, 79,  110, CyberwareSlot.MUSCLE); // MUSCLE
        addSlotColumn(55, 5, 106, 110, CyberwareSlot.BONE); // BONE
        addSlotColumn(60, 5, 52,  110, CyberwareSlot.SKIN); // SKIN
    }

    // -------------------------------------------------
    // POPULATE FROM PLAYER ATTACHMENT
    // -------------------------------------------------

    private void populateFromPlayer(Player player) {
        if (player.level().isClientSide) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        // If TE already has contents, treat them as installed (do not overwrite).
        boolean hasContents = false;
        for (int i = 0; i < blockEntity.inventory.getSlots(); i++) {
            if (!blockEntity.inventory.getStackInSlot(i).isEmpty()) {
                hasContents = true;
                break;
            }
        }

        if (hasContents) {
            for (int i = 0; i < blockEntity.inventory.getSlots(); i++) {
                if (!blockEntity.inventory.getStackInSlot(i).isEmpty()) {
                    setInstalled(i, true);
                }
            }
            return;
        }

        for (CyberwareSlot slot : CyberwareSlot.values()) {
            InstalledCyberware[] installedData = data.getAll().get(slot);
            if (installedData == null) continue;

            int mappedSize = RobosurgeonSlotMap.mappedSize(slot);

            for (int i = 0; i < mappedSize; i++) {
                int invIndex = RobosurgeonSlotMap.toInventoryIndex(slot, i);
                if (invIndex < 0) continue;

                ItemStack stack;
                if (i < installedData.length && installedData[i] != null) {
                    stack = installedData[i].getItem().copy();
                } else {
                    stack = DefaultOrgans.get(slot, i);
                }

                if (!stack.isEmpty()) {
                    blockEntity.inventory.setStackInSlot(invIndex, stack);
                    setInstalled(invIndex, true);
                    setStaged(invIndex, false);
                    // marked stays false by default
                }
            }
        }
    }

    // -------------------------------------------------
    // CLICK HANDLING (THIS IS THE REPLACEMENT FEATURE)
    // -------------------------------------------------

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId < 0 || slotId >= slots.size()) {
            super.clicked(slotId, button, clickType, player);
            return;
        }

        Slot slot = slots.get(slotId);

        if (slot instanceof RobosurgeonSlotItemHandler rsSlot) {
            int handlerIndex = rsSlot.getSlotIndex();
            ItemStack carried = getCarried();

            // Block vanilla shift-click behavior for TE slots (we handle via quickMoveStack from inventory)
            if (clickType == ClickType.QUICK_MOVE) {
                return;
            }

            // ----------------------------------------
            // RIGHT CLICK: remove STAGED item only
            // If this was a replacement staging, restore installed/default to slot.
            // ----------------------------------------
            if (clickType == ClickType.PICKUP && button == 1) {
                if (carried.isEmpty() && rsSlot.hasItem() && isStaged(handlerIndex)) {

                    ItemStack stagedStack = rsSlot.getItem().copy();

                    // clear staged item from GUI
                    rsSlot.set(ItemStack.EMPTY);
                    setStaged(handlerIndex, false);

                    // If replacement staging set marked=true, clear it now (cancel replacement)
                    if (isMarkedForRemoval(handlerIndex)) {
                        setMarkedForRemoval(handlerIndex, false);
                    }

                    // If it was installed originally, restore original installed/default visuals
                    if (isInstalled(handlerIndex)) {
                        ItemStack restore = getInstalledOrDefault(player, handlerIndex);
                        rsSlot.set(restore);
                    }

                    // return staged item to player
                    if (!player.getInventory().add(stagedStack)) {
                        player.drop(stagedStack, false);
                    }
                    return;
                }

                return; // block vanilla
            }

            // ----------------------------------------
            // LEFT CLICK
            // ----------------------------------------
            if (clickType == ClickType.PICKUP && button == 0) {

                // A) Empty hand + installed + not staged => toggle mark for removal
                if (carried.isEmpty()
                        && rsSlot.hasItem()
                        && isInstalled(handlerIndex)
                        && !isStaged(handlerIndex)) {

                    toggleMarkedForRemoval(handlerIndex);
                    return;
                }

                // B) Replacement staging:
                // Carried item placed onto an INSTALLED slot (that isn't already staged)
                if (!carried.isEmpty()
                        && rsSlot.hasItem()
                        && isInstalled(handlerIndex)
                        && !isStaged(handlerIndex)
                        && rsSlot.mayPlace(carried)) {

                    ItemStack stagedOne = carried.split(1);
                    rsSlot.set(stagedOne);

                    setStaged(handlerIndex, true);
                    setMarkedForRemoval(handlerIndex, true);

                    // IMPORTANT: keep installed=true so surgery knows this is a replacement slot visually
                    setInstalled(handlerIndex, true);

                    return;
                }

                // C) Normal staging: place into empty, uninstalled slot
                if (!carried.isEmpty()
                        && !rsSlot.hasItem()
                        && !isInstalled(handlerIndex)
                        && rsSlot.mayPlace(carried)) {

                    // Let vanilla place one
                    super.clicked(slotId, button, clickType, player);

                    if (rsSlot.hasItem()) {
                        setStaged(handlerIndex, true);
                    }
                    return;
                }

                // Otherwise block vanilla pickup from TE
                return;
            }
        }

        super.clicked(slotId, button, clickType, player);
    }


    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    private static final int TE_INVENTORY_SLOT_COUNT = 65;

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) {
            return ItemStack.EMPTY;
        }

        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copy = sourceStack.copy();

        // PLAYER INVENTORY -> ROBOSURGEON
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {

            for (RobosurgeonSlotItemHandler targetSlot : robosurgeonSlots) {
                int handlerIndex = targetSlot.getSlotIndex();

                if (!targetSlot.isActive()) continue;

                CyberwareSlot targetType = targetSlot.getSlotType();
                if (!sideMatches(sourceStack, targetType)) continue;

                if (!targetSlot.mayPlace(sourceStack)) continue;

                boolean installed = isInstalled(handlerIndex);
                boolean staged = isStaged(handlerIndex);

                // Case A: normal staging (empty, uninstalled)
                if (!installed) {
                    if (targetSlot.hasItem()) continue;

                    ItemStack moved = sourceStack.split(1);
                    targetSlot.set(moved);
                    setStaged(handlerIndex, true);

                    if (sourceStack.isEmpty()) sourceSlot.set(ItemStack.EMPTY);
                    else sourceSlot.setChanged();

                    return copy;
                }

                // Case B: replacement staging (installed, has item, not already staged)
                if (installed) {
                    if (!targetSlot.hasItem()) continue;
                    if (staged) continue;

                    ItemStack moved = sourceStack.split(1);
                    targetSlot.set(moved);
                    setStaged(handlerIndex, true);
                    setMarkedForRemoval(handlerIndex, true);

                    if (sourceStack.isEmpty()) sourceSlot.set(ItemStack.EMPTY);
                    else sourceSlot.setChanged();

                    return copy;
                }
            }

            return ItemStack.EMPTY;
        }

        // ROBOSURGEON -> PLAYER (disabled)
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.ROBOSURGEON.get());
    }

    private boolean sideMatches(ItemStack stack, CyberwareSlot targetType) {

        // Arms: if the item is explicitly left/right, enforce it
        if (stack.is(com.perigrine3.createcybernetics.util.ModTags.Items.LEFTARM_ITEMS)) {
            return targetType == CyberwareSlot.LARM;
        }
        if (stack.is(com.perigrine3.createcybernetics.util.ModTags.Items.RIGHTARM_ITEMS)) {
            return targetType == CyberwareSlot.RARM;
        }

        // Legs
        if (stack.is(com.perigrine3.createcybernetics.util.ModTags.Items.LEFTLEG_ITEMS)) {
            return targetType == CyberwareSlot.LLEG;
        }
        if (stack.is(com.perigrine3.createcybernetics.util.ModTags.Items.RIGHTLEG_ITEMS)) {
            return targetType == CyberwareSlot.RLEG;
        }

        // Not a side-specific item: allow either side
        return true;
    }


    // -------------------------------------------------
    // Restore helpers (used when canceling staged replacement)
    // -------------------------------------------------

    private record SlotRef(CyberwareSlot slot, int idx) {}

    private SlotRef resolveSlotRef(int invIndex) {
        for (CyberwareSlot slot : CyberwareSlot.values()) {
            int mapped = RobosurgeonSlotMap.mappedSize(slot);
            for (int i = 0; i < mapped; i++) {
                int check = RobosurgeonSlotMap.toInventoryIndex(slot, i);
                if (check == invIndex) {
                    return new SlotRef(slot, i);
                }
            }
        }
        return null;
    }

    private ItemStack getInstalledOrDefault(Player player, int invIndex) {
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return ItemStack.EMPTY;

        SlotRef ref = resolveSlotRef(invIndex);
        if (ref == null) return ItemStack.EMPTY;

        InstalledCyberware[] arr = data.getAll().get(ref.slot());
        if (arr != null && ref.idx() >= 0 && ref.idx() < arr.length) {
            InstalledCyberware installed = arr[ref.idx()];
            if (installed != null && installed.getItem() != null && !installed.getItem().isEmpty()) {
                return installed.getItem().copy();
            }
        }

        ItemStack def = DefaultOrgans.get(ref.slot(), ref.idx());
        return def == null ? ItemStack.EMPTY : def.copy();
    }

    // -------------------------------------------------
    // Player inventory slots
    // -------------------------------------------------

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(
                        playerInventory,
                        l + i * 9 + 9,
                        8 + l * 18,
                        84 + i * 18 + INVENTORY_Y_OFFSET));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(
                    playerInventory,
                    i, 8 + i * 18,
                    142 + INVENTORY_Y_OFFSET));
        }
    }
}