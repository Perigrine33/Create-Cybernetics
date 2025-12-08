package com.perigrine3.createcybernetics.screen.custom;

import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class RobosurgeonSlotItemHandler extends SlotItemHandler {
    private boolean active = true;

    public RobosurgeonSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    public void setActiveFlag(boolean active) {
        this.active = active;
    }

    @Override
    public boolean isActive() {
        return active;
    }
}
