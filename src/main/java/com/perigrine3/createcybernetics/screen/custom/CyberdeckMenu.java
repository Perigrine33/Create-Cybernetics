package com.perigrine3.createcybernetics.screen.custom;

import com.perigrine3.createcybernetics.screen.ModMenuTypes;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class CyberdeckMenu extends AbstractContainerMenu {
    public static final int QUICKHACK_SLOT_COUNT = 4;

    private static final int SLOT_SIZE = 18;

    private static final int QUICKHACK_Y = 26;
    private static final int QUICKHACK_X_0 = 52;

    private static final int PLAYER_INV_X = 8;
    private static final int PLAYER_INV_Y = 84;

    private static final int HOTBAR_X = 8;
    private static final int HOTBAR_Y = 142;

    private static final int PLAYER_INV_START = QUICKHACK_SLOT_COUNT;
    private static final int PLAYER_INV_END = PLAYER_INV_START + 27;
    private static final int HOTBAR_START = PLAYER_INV_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final IItemHandler cyberdeckInventory;

    public CyberdeckMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, new ItemStackHandler(QUICKHACK_SLOT_COUNT));
    }

    public CyberdeckMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new ItemStackHandler(QUICKHACK_SLOT_COUNT));
    }

    public CyberdeckMenu(int containerId, Inventory playerInventory, IItemHandler cyberdeckInventory) {
        super(ModMenuTypes.CYBERDECK_MENU.get(), containerId);
        this.cyberdeckInventory = cyberdeckInventory;

        addQuickhackSlots();
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    private void addQuickhackSlots() {
        for (int i = 0; i < QUICKHACK_SLOT_COUNT; i++) {
            int x = QUICKHACK_X_0 + (i * SLOT_SIZE);
            int y = QUICKHACK_Y;
            addSlot(new QuickhackSlot(cyberdeckInventory, i, x, y));
        }
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                int slotIndex = column + row * 9 + 9;
                int x = PLAYER_INV_X + column * SLOT_SIZE;
                int y = PLAYER_INV_Y + row * SLOT_SIZE;
                addSlot(new Slot(playerInventory, slotIndex, x, y));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; i++) {
            int x = HOTBAR_X + i * SLOT_SIZE;
            int y = HOTBAR_Y;
            addSlot(new Slot(playerInventory, i, x, y));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack copiedStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = slot.getItem();
        copiedStack = stackInSlot.copy();

        if (index < QUICKHACK_SLOT_COUNT) {
            if (!moveItemStackTo(stackInSlot, PLAYER_INV_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (stackInSlot.is(ModTags.Items.QUICKHACK_SHARDS)) {
                if (!moveItemStackTo(stackInSlot, 0, QUICKHACK_SLOT_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (stackInSlot.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stackInSlot.getCount() == copiedStack.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stackInSlot);
        return copiedStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    private static class QuickhackSlot extends SlotItemHandler {
        public QuickhackSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.is(ModTags.Items.QUICKHACK_SHARDS);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}