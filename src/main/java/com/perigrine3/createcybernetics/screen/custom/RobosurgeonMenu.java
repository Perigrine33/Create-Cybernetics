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
        addRobosurgeonSlots();
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

    private void setMarkedForRemoval(int invIndex, boolean value) {
        boolean current = isMarkedForRemoval(invIndex);
        if (current != value) {
            toggleMarkedForRemoval(invIndex);
        }
    }

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
        addSlotColumn(0,  5, 151, 110, CyberwareSlot.BRAIN);
        addSlotColumn(5,  5, 151, 110, CyberwareSlot.EYES);
        addSlotColumn(10, 6, 151, 110, CyberwareSlot.HEART);
        addSlotColumn(16, 6, 151, 110, CyberwareSlot.LUNGS);
        addSlotColumn(22, 6, 151, 110, CyberwareSlot.ORGANS);
        addSlotColumn(28, 6, 43,  110, CyberwareSlot.RARM);
        addSlotColumn(34, 6, 115, 110, CyberwareSlot.LARM);
        addSlotColumn(40, 5, 43,  110, CyberwareSlot.RLEG);
        addSlotColumn(45, 5, 115, 110, CyberwareSlot.LLEG);
        addSlotColumn(50, 5, 79,  110, CyberwareSlot.MUSCLE);
        addSlotColumn(55, 5, 106, 110, CyberwareSlot.BONE);
        addSlotColumn(60, 5, 52,  110, CyberwareSlot.SKIN);
    }

    private void populateFromPlayer(Player player) {
        if (player.level().isClientSide) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        boolean hasContents = false;
        for (int i = 0; i < blockEntity.inventory.getSlots(); i++) {
            if (!blockEntity.inventory.getStackInSlot(i).isEmpty()) {
                hasContents = true;
                break;
            }
        }

        if (hasContents) {
            if (!keepInventoryActive()
                    && playerHasOnlyDefaultOrgans(data)
                    && robosurgeonHasAnyNonDefaultStacks()) {

                clearRobosurgeonInventoryAndFlags();
            } else {
                for (int i = 0; i < blockEntity.inventory.getSlots(); i++) {
                    if (!blockEntity.inventory.getStackInSlot(i).isEmpty()) {
                        setInstalled(i, true);
                    }
                }
                return;
            }
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
                }
            }
        }
    }

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

            if (clickType == ClickType.QUICK_MOVE) {
                return;
            }

            if (clickType == ClickType.PICKUP && button == 1) {
                if (carried.isEmpty() && rsSlot.hasItem() && isStaged(handlerIndex)) {

                    ItemStack stagedStack = rsSlot.getItem().copy();

                    rsSlot.set(ItemStack.EMPTY);
                    setStaged(handlerIndex, false);

                    if (isMarkedForRemoval(handlerIndex)) {
                        setMarkedForRemoval(handlerIndex, false);
                    }

                    if (isInstalled(handlerIndex)) {
                        ItemStack restore = getInstalledOrDefault(player, handlerIndex);
                        rsSlot.set(restore);
                    }

                    if (!player.getInventory().add(stagedStack)) {
                        player.drop(stagedStack, false);
                    }
                    return;
                }

                return;
            }

            if (clickType == ClickType.PICKUP && button == 0) {

                if (carried.isEmpty()
                        && rsSlot.hasItem()
                        && isInstalled(handlerIndex)
                        && !isStaged(handlerIndex)) {

                    toggleMarkedForRemoval(handlerIndex);
                    return;
                }

                if (!carried.isEmpty()
                        && rsSlot.hasItem()
                        && isInstalled(handlerIndex)
                        && !isStaged(handlerIndex)
                        && rsSlot.mayPlace(carried)) {

                    ItemStack stagedOne = carried.split(1);
                    rsSlot.set(stagedOne);

                    setStaged(handlerIndex, true);
                    setMarkedForRemoval(handlerIndex, true);

                    setInstalled(handlerIndex, true);

                    return;
                }

                if (!carried.isEmpty()
                        && !rsSlot.hasItem()
                        && !isInstalled(handlerIndex)
                        && rsSlot.mayPlace(carried)) {

                    super.clicked(slotId, button, clickType, player);

                    if (rsSlot.hasItem()) {
                        setStaged(handlerIndex, true);
                    }
                    return;
                }

                return;
            }
        }

        super.clicked(slotId, button, clickType, player);
    }

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

        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {

            CyberwareSlot forcedSide = null;
            if (sourceStack.is(com.perigrine3.createcybernetics.util.ModTags.Items.LEFTLEG_ITEMS)) forcedSide = CyberwareSlot.LLEG;
            else if (sourceStack.is(com.perigrine3.createcybernetics.util.ModTags.Items.RIGHTLEG_ITEMS)) forcedSide = CyberwareSlot.RLEG;
            else if (sourceStack.is(com.perigrine3.createcybernetics.util.ModTags.Items.LEFTARM_ITEMS)) forcedSide = CyberwareSlot.LARM;
            else if (sourceStack.is(com.perigrine3.createcybernetics.util.ModTags.Items.RIGHTARM_ITEMS)) forcedSide = CyberwareSlot.RARM;

            for (int pass = 0; pass < 2; pass++) {
                for (RobosurgeonSlotItemHandler targetSlot : robosurgeonSlots) {
                    int handlerIndex = targetSlot.getSlotIndex();

                    if (!targetSlot.isActive()) continue;

                    CyberwareSlot targetType = targetSlot.getSlotType();

                    if (forcedSide != null && targetType != forcedSide) continue;

                    if (!sideMatches(sourceStack, targetType)) continue;

                    if (!targetSlot.mayPlace(sourceStack)) continue;

                    int maxStacks = getMaxStacksPerSlotType(sourceStack, targetType);
                    int alreadyInType = countItemInSlotType(targetType, sourceStack);
                    boolean hasInType = alreadyInType > 0;

                    if (pass == 0 && hasInType) continue;

                    if (pass == 1 && hasInType && alreadyInType >= maxStacks) continue;

                    boolean installed = isInstalled(handlerIndex);
                    boolean staged = isStaged(handlerIndex);

                    if (!installed) {
                        if (targetSlot.hasItem()) continue;

                        ItemStack moved = sourceStack.split(1);
                        targetSlot.set(moved);
                        setStaged(handlerIndex, true);

                        if (sourceStack.isEmpty()) sourceSlot.set(ItemStack.EMPTY);
                        else sourceSlot.setChanged();

                        return copy;
                    }

                    if (installed) {
                        if (!targetSlot.hasItem()) continue;
                        if (staged) continue;

                        if (!stackReplacesOrgan(sourceStack)) {
                            SlotRef ref = resolveSlotRef(handlerIndex);
                            if (ref != null && isDefaultOrganStack(targetSlot.getItem(), ref.slot(), ref.idx())) {
                                continue;
                            }
                        }

                        ItemStack moved = sourceStack.split(1);
                        targetSlot.set(moved);
                        setStaged(handlerIndex, true);
                        setMarkedForRemoval(handlerIndex, true);

                        if (sourceStack.isEmpty()) sourceSlot.set(ItemStack.EMPTY);
                        else sourceSlot.setChanged();

                        return copy;
                    }
                }
            }

            return ItemStack.EMPTY;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.ROBOSURGEON.get());
    }

    private boolean sideMatches(ItemStack stack, CyberwareSlot targetType) {

        if (stack.is(com.perigrine3.createcybernetics.util.ModTags.Items.LEFTARM_ITEMS)) {
            return targetType == CyberwareSlot.LARM;
        }
        if (stack.is(com.perigrine3.createcybernetics.util.ModTags.Items.RIGHTARM_ITEMS)) {
            return targetType == CyberwareSlot.RARM;
        }

        if (stack.is(com.perigrine3.createcybernetics.util.ModTags.Items.LEFTLEG_ITEMS)) {
            return targetType == CyberwareSlot.LLEG;
        }
        if (stack.is(com.perigrine3.createcybernetics.util.ModTags.Items.RIGHTLEG_ITEMS)) {
            return targetType == CyberwareSlot.RLEG;
        }

        return true;
    }

    private static boolean isDefaultOrganStack(ItemStack stack, CyberwareSlot slot, int idx) {
        if (stack.isEmpty()) return false;
        ItemStack def = DefaultOrgans.get(slot, idx);
        if (def == null || def.isEmpty()) return false;
        return ItemStack.isSameItemSameComponents(stack, def);
    }

    private static boolean stackReplacesOrgan(ItemStack stack) {
        if (!(stack.getItem() instanceof com.perigrine3.createcybernetics.api.ICyberwareItem cw)) return true;
        return cw.replacesOrgan();
    }

    private static int getMaxStacksPerSlotType(ItemStack stack, CyberwareSlot slotType) {
        if (stack.getItem() instanceof com.perigrine3.createcybernetics.api.ICyberwareItem cw) {
            return Math.max(1, cw.maxStacksPerSlotType(stack, slotType));
        }
        return 1;
    }

    private int countItemInSlotType(CyberwareSlot slotType, ItemStack stack) {
        int count = 0;

        for (int i = 0; i < RobosurgeonSlotMap.mappedSize(slotType); i++) {
            int invIndex = RobosurgeonSlotMap.toInventoryIndex(slotType, i);
            if (invIndex < 0) continue;

            ItemStack inTe = blockEntity.inventory.getStackInSlot(invIndex);
            if (!inTe.isEmpty() && ItemStack.isSameItemSameComponents(inTe, stack)) {
                count++;
            }
        }

        return count;
    }

    private boolean slotTypeHasItem(CyberwareSlot slotType, ItemStack stack) {
        return countItemInSlotType(slotType, stack) > 0;
    }

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

    private boolean keepInventoryActive() {
        return level.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_KEEPINVENTORY);
    }

    private void clearRobosurgeonInventoryAndFlags() {
        for (int i = 0; i < blockEntity.inventory.getSlots(); i++) {
            blockEntity.inventory.setStackInSlot(i, ItemStack.EMPTY);
            setInstalled(i, false);
            setStaged(i, false);
            setMarkedForRemoval(i, false);
        }
    }

    private boolean playerHasOnlyDefaultOrgans(PlayerCyberwareData data) {
        for (CyberwareSlot slot : CyberwareSlot.values()) {
            InstalledCyberware[] installed = data.getAll().get(slot);
            int mappedSize = RobosurgeonSlotMap.mappedSize(slot);

            for (int i = 0; i < mappedSize; i++) {
                ItemStack def = DefaultOrgans.get(slot, i);
                if (def == null) def = ItemStack.EMPTY;

                ItemStack actual = ItemStack.EMPTY;
                if (installed != null && i < installed.length && installed[i] != null) {
                    actual = installed[i].getItem();
                    if (actual == null) actual = ItemStack.EMPTY;
                }

                if (actual.isEmpty()) continue;

                if (def.isEmpty()) return false;

                if (!ItemStack.isSameItemSameComponents(actual, def)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean robosurgeonHasAnyNonDefaultStacks() {
        for (CyberwareSlot slot : CyberwareSlot.values()) {
            int mappedSize = RobosurgeonSlotMap.mappedSize(slot);

            for (int i = 0; i < mappedSize; i++) {
                int invIndex = RobosurgeonSlotMap.toInventoryIndex(slot, i);
                if (invIndex < 0) continue;

                ItemStack inTe = blockEntity.inventory.getStackInSlot(invIndex);
                if (inTe.isEmpty()) continue;

                ItemStack def = DefaultOrgans.get(slot, i);
                if (def == null) def = ItemStack.EMPTY;

                if (def.isEmpty()) return true;
                if (!ItemStack.isSameItemSameComponents(inTe, def)) return true;
            }
        }
        return false;
    }

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
