package com.perigrine3.createcybernetics.screen.custom;

import com.perigrine3.createcybernetics.block.ModBlocks;
import com.perigrine3.createcybernetics.block.entity.RobosurgeonBlockEntity;
import com.perigrine3.createcybernetics.screen.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.SlotItemHandler;

public class RobosurgeonMenu extends AbstractContainerMenu {
    public final RobosurgeonBlockEntity blockEntity;
    private final Level level;
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

    }

    private void addSlotColumn(int startIndex, int count, int x, int startY) {
        for (int i = 0; i < count; i++) {
            this.addSlot(new RobosurgeonSlotItemHandler(
                    blockEntity.inventory,
                    startIndex + i,
                    x + 1,
                    startY - (i * 18) + 1));
        }
    }

    private void addRobosurgeonSlots() {
    // HEAD
            addSlotColumn(0,  5, 151, 110); // BRAIN
            addSlotColumn(5,  5, 151, 110); // EYES
    // TORSO
            addSlotColumn(10, 6, 151, 110); // HEART
            addSlotColumn(16, 6, 151, 110); // LUNGS
            addSlotColumn(22, 6, 151, 110); // ORGANS
    // ARMS
            addSlotColumn(28, 6, 43,  110); // RIGHT ARM
            addSlotColumn(34, 6, 115, 110); // LEFT ARM
    // LEGS
            addSlotColumn(40, 5, 43,  110); // RIGHT LEG
            addSlotColumn(45, 5, 115, 110); // LEFT LEG
    // SKIN / STRUCTURE
            addSlotColumn(50, 5, 79,  110); // MUSCLE
            addSlotColumn(55, 5, 106, 110); // BONE
            addSlotColumn(60, 5, 52,  110); // SKIN
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
    private static final int TE_INVENTORY_SLOT_COUNT = 65;  // must be the number of slots you have!


    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
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
