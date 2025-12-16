package com.perigrine3.createcybernetics.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class InstalledCyberware {

    private ItemStack item = ItemStack.EMPTY;
    private CyberwareSlot slot = null;
    private int index = -1;
    private int humanityCost = 0;
    private boolean powered = true;

    public InstalledCyberware() {}

    public InstalledCyberware(ItemStack item, CyberwareSlot slot, int index, int humanityCost) {
        this.item = item.copy();
        this.slot = slot;
        this.index = index;
        this.humanityCost = humanityCost;
    }

    public ItemStack getItem() {
        return item;
    }

    public CyberwareSlot getSlot() {
        return slot;
    }

    public int getIndex() {
        return index;
    }

    public int getHumanityCost() {
        return humanityCost;
    }

    public boolean isPowered() {
        return powered;
    }

    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    /* ---------------- NBT ---------------- */

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();

        if (!item.isEmpty()) {
            tag.put("Item", item.save(null));
        }

        if (slot != null) {
            tag.putString("Slot", slot.name());
            tag.putInt("Index", index);
        }

        tag.putInt("Humanity", humanityCost);
        tag.putBoolean("Powered", powered);
        return tag;
    }

    public static InstalledCyberware load(CompoundTag tag) {
        InstalledCyberware c = new InstalledCyberware();

        if (tag.contains("Item")) {
            c.item = ItemStack.parse(null, tag.getCompound("Item"))
                    .orElse(ItemStack.EMPTY);
        }

        if (tag.contains("Slot")) {
            c.slot = CyberwareSlot.valueOf(tag.getString("Slot"));
            c.index = tag.getInt("Index");
        }

        c.humanityCost = tag.getInt("Humanity");
        c.powered = tag.getBoolean("Powered");

        return c;
    }
}
