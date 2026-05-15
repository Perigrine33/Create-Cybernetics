package com.perigrine3.createcybernetics.api;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class InstalledCyberware {

    private ItemStack item = ItemStack.EMPTY;
    private CyberwareSlot slot = null;
    private int index = -1;
    private int humanityCost = 0;
    private boolean powered = true;

    public InstalledCyberware() {}

    public InstalledCyberware(ItemStack item, CyberwareSlot slot, int index, int humanityCost) {
        this.item = item == null ? ItemStack.EMPTY : item.copy();
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

    public CompoundTag save(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        if (item != null && !item.isEmpty()) {
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

    public static InstalledCyberware load(CompoundTag tag, HolderLookup.Provider provider) {
        InstalledCyberware installed = new InstalledCyberware();

        if (tag.contains("Item", Tag.TAG_COMPOUND)) {
            installed.item = ItemStack.parse(provider, tag.getCompound("Item")).orElse(ItemStack.EMPTY);
        } else {
            installed.item = ItemStack.EMPTY;
        }

        if (tag.contains("Slot", Tag.TAG_STRING)) {
            try {
                installed.slot = CyberwareSlot.valueOf(tag.getString("Slot"));
                installed.index = tag.getInt("Index");
            } catch (IllegalArgumentException ignored) {
                installed.slot = null;
                installed.index = -1;
            }
        } else {
            installed.slot = null;
            installed.index = -1;
        }

        installed.humanityCost = tag.getInt("Humanity");
        installed.powered = !tag.contains("Powered", Tag.TAG_BYTE) || tag.getBoolean("Powered");

        return installed;
    }
}