package com.perigrine3.createcybernetics.screen.container;

import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class CyberdeckInventoryHandler implements IItemHandlerModifiable {
    private final PlayerCyberwareData data;

    public CyberdeckInventoryHandler(PlayerCyberwareData data) {
        this.data = data;
    }

    @Override
    public int getSlots() {
        return PlayerCyberwareData.CYBERDECK_SLOT_COUNT;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return data.getCyberdeckStack(slot);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        data.setCyberdeckStack(slot, stack);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        if (!isItemValid(slot, stack)) return stack;

        ItemStack existing = getStackInSlot(slot);
        if (!existing.isEmpty()) return stack;

        ItemStack toInsert = stack.copy();
        toInsert.setCount(1);

        if (!simulate) {
            setStackInSlot(slot, toInsert);
        }

        ItemStack remainder = stack.copy();
        remainder.shrink(1);
        return remainder;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) return ItemStack.EMPTY;

        ItemStack existing = getStackInSlot(slot);
        if (existing.isEmpty()) return ItemStack.EMPTY;

        ItemStack extracted = existing.copy();
        extracted.setCount(Math.min(amount, existing.getCount()));

        if (!simulate) {
            setStackInSlot(slot, ItemStack.EMPTY);
        }

        return extracted;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return stack.is(ModTags.Items.QUICKHACK_SHARDS);
    }
}