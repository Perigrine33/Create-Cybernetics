package com.perigrine3.createcybernetics.api;

import net.minecraft.core.HolderLookup;
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

    public CompoundTag save(net.minecraft.core.HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        if (!item.isEmpty()) {
            tag.put("Item", item.save(provider));
        }

        if (slot != null) {
            tag.putString("Slot", slot.name());
            tag.putInt("Index", index);
        }

        tag.putInt("Humanity", humanityCost);
        tag.putBoolean("Powered", powered);
        return tag;
    }

    public static InstalledCyberware load(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        InstalledCyberware c = new InstalledCyberware();

        if (tag.contains("Item", net.minecraft.nbt.Tag.TAG_COMPOUND)) {
            c.item = ItemStack.parse(provider, tag.getCompound("Item"))
                    .orElse(ItemStack.EMPTY);
        } else {
            c.item = ItemStack.EMPTY;
        }

        if (tag.contains("Slot", net.minecraft.nbt.Tag.TAG_STRING)) {
            c.slot = CyberwareSlot.valueOf(tag.getString("Slot"));
            c.index = tag.getInt("Index");
        } else {
            c.slot = null;
            c.index = -1;
        }

        c.humanityCost = tag.getInt("Humanity");
        c.powered = tag.getBoolean("Powered");

        return c;
    }
}
