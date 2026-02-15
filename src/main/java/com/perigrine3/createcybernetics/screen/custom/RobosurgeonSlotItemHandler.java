package com.perigrine3.createcybernetics.screen.custom;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.surgery.DefaultOrgans;
import com.perigrine3.createcybernetics.common.surgery.RobosurgeonSlotMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class RobosurgeonSlotItemHandler extends SlotItemHandler {

    private boolean active = true;
    private final CyberwareSlot slotType;

    public RobosurgeonSlotItemHandler(
            ItemStackHandler handler,
            int index,
            int x,
            int y,
            CyberwareSlot slotType
    ) {
        super(handler, index, x, y);
        this.slotType = slotType;
    }

    public CyberwareSlot getSlotType() {
        return slotType;
    }

    @Override
    public void set(ItemStack stack) {
        super.set(stack);

        if (!stack.isEmpty()) {
            if (container instanceof ItemStackHandler handler) {

            }
        }
    }

    public void setActiveFlag(boolean active) {
        this.active = active;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {

        CyberwareSlot slotType = this.slotType;
        if (slotType == null) return false;

        if (DefaultOrgans.isOrganForSlot(stack, slotType)) {
            return true;
        }

        if (stack.getItem() instanceof ICyberwareItem cyberware) {
            return cyberware.supportsSlot(slotType);
        }

        return false;
    }

    @Override
    public boolean mayPickup(Player player) {
        return true;
    }


    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

}
