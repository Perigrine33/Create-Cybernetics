package com.perigrine3.createcybernetics.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class InstalledCyberware {

    private ItemStack item = ItemStack.EMPTY;
    private int humanityCost = 0;
    private boolean powered = true;
    private String slot = "none";

    public InstalledCyberware(ItemStack item, String slot, int humanityCost) {
        this.item = item.copy();
        this.slot = slot;
        this.humanityCost = humanityCost;
    }

    public InstalledCyberware() {}

    public ItemStack getItem() { return item; }
    public String getSlot() { return slot; }
    public int getHumanity() { return humanityCost; }
    public boolean isPowered() { return powered; }

    public void setPowered(boolean powered) { this.powered = powered; }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        if (!item.isEmpty()) {
            tag.put("Item", item.save(null)); // null provider works
        }
        tag.putString("Slot", slot);
        tag.putInt("Humanity", humanityCost);
        tag.putBoolean("Powered", powered);
        return tag;
    }

    public static InstalledCyberware load(CompoundTag tag) {
        InstalledCyberware c = new InstalledCyberware();
        if (tag.contains("Item")) {
            c.item = ItemStack.parse(null, tag.getCompound("Item")).orElse(ItemStack.EMPTY);
        }
        c.slot = tag.getString("Slot");
        c.humanityCost = tag.getInt("Humanity");
        c.powered = tag.getBoolean("Powered");
        return c;
    }
}
