package com.perigrine3.createcybernetics.screen.slot;

import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.BooleanSupplier;

public class DataShardSlot extends Slot {

    private final BooleanSupplier activeCheck;

    public DataShardSlot(Container container, int index, int x, int y, BooleanSupplier activeCheck) {
        super(container, index, x, y);
        this.activeCheck = activeCheck;
    }

    @Override
    public boolean isActive() {
        return activeCheck.getAsBoolean();
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return isActive() && !stack.isEmpty() && stack.is(ModTags.Items.DATA_SHARDS);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
