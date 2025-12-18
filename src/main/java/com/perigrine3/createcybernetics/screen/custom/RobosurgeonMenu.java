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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;


public class RobosurgeonMenu extends AbstractContainerMenu {
    public final RobosurgeonBlockEntity blockEntity;
    private final Level level;
    private final List<RobosurgeonSlotItemHandler> robosurgeonSlots = new ArrayList<>();
    public int getTeInventoryFirstSlotIndex() {
        return TE_INVENTORY_FIRST_SLOT_INDEX;
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

            // ----------------------------------------
            // BLOCK SHIFT-CLICK FROM ROBOSURGEON SLOTS
            // ----------------------------------------
            if (clickType == ClickType.QUICK_MOVE) {
                return;
            }

            // ----------------------------------------
            // RIGHT CLICK (button == 1)
            // Remove STAGED items only
            // ----------------------------------------
            if (clickType == ClickType.PICKUP && button == 1) {

                if (carried.isEmpty()
                        && rsSlot.hasItem()
                        && isStaged(handlerIndex)) {

                    ItemStack stack = rsSlot.getItem().copy();

                    rsSlot.set(ItemStack.EMPTY);
                    setStaged(handlerIndex, false);

                    if (!player.getInventory().add(stack)) {
                        player.drop(stack, false);
                    }

                    return;
                }

                return; // block vanilla
            }

            // ----------------------------------------
            // LEFT CLICK (button == 0)
            // Mark INSTALLED items only
            // ----------------------------------------
            if (clickType == ClickType.PICKUP && button == 0) {

                if (carried.isEmpty()
                        && rsSlot.hasItem()
                        && isInstalled(handlerIndex)
                        && !isStaged(handlerIndex)) {

                    toggleMarkedForRemoval(handlerIndex);
                    return;
                }

                // ----------------------------------------
                // MANUAL PLACEMENT DETECTION
                // ----------------------------------------
                if (!carried.isEmpty()
                        && !rsSlot.hasItem()
                        && !isInstalled(handlerIndex)
                        && rsSlot.mayPlace(carried)) {

                    // Let vanilla place ONE item
                    super.clicked(slotId, button, clickType, player);

                    // If placement succeeded, mark staged
                    if (rsSlot.hasItem()) {
                        setStaged(handlerIndex, true);
                    }

                    return;
                }

                return; // block vanilla pickup
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

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 65;


    @Override
    public ItemStack quickMoveStack(Player player, int index) {

        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copy = sourceStack.copy();

        // PLAYER INVENTORY → ROBOSURGEON (STAGED)
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {

            for (RobosurgeonSlotItemHandler targetSlot : robosurgeonSlots) {

                int handlerIndex = targetSlot.getSlotIndex();

                if (!targetSlot.isActive()) continue;
                if (isInstalled(handlerIndex)) continue;
                if (targetSlot.hasItem()) continue;
                if (!targetSlot.mayPlace(sourceStack)) continue;

                ItemStack moved = sourceStack.split(1);
                targetSlot.set(moved);
                setStaged(handlerIndex, true);

                if (sourceStack.isEmpty()) {
                    sourceSlot.set(ItemStack.EMPTY);
                } else {
                    sourceSlot.setChanged();
                }

                return copy;
            }

            return ItemStack.EMPTY;
        }

        // ROBOSURGEON → PLAYER (DISABLED BY DESIGN)
        return ItemStack.EMPTY;
    }


    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.ROBOSURGEON.get());
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
