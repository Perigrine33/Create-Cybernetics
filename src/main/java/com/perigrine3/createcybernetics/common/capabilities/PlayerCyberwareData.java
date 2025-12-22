package com.perigrine3.createcybernetics.common.capabilities;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareData;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.surgery.DefaultOrgans;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.EnumMap;
import java.util.Map;

public class PlayerCyberwareData implements ICyberwareData {

    private final EnumMap<CyberwareSlot, InstalledCyberware[]> slots =
            new EnumMap<>(CyberwareSlot.class);

    private boolean dirty = false;
    private int humanity = 100;

    public PlayerCyberwareData() {
        for (CyberwareSlot slot : CyberwareSlot.values()) {
            slots.put(slot, new InstalledCyberware[slot.size]);
        }
    }

    @Override
    public InstalledCyberware get(CyberwareSlot slot, int index) {
        return slots.get(slot)[index];
    }

    @Override
    public void set(CyberwareSlot slot, int index, InstalledCyberware cyberware) {
        slots.get(slot)[index] = cyberware;
        dirty = true;
    }

    @Override
    public InstalledCyberware remove(CyberwareSlot slot, int index) {
        InstalledCyberware old = slots.get(slot)[index];
        slots.get(slot)[index] = null;
        dirty = true;
        return old;
    }

    @Override
    public Map<CyberwareSlot, InstalledCyberware[]> getAll() {
        return slots;
    }

    @Override
    public int getHumanity() {
        return humanity;
    }

    @Override
    public void setHumanity(int value) {
        humanity = value;
        dirty = true;
    }

    public boolean hasOrgan(CyberwareSlot slot) {
        InstalledCyberware[] arr = slots.get(slot);
        for (InstalledCyberware cw : arr) {
            if (cw != null && cw.getItem() != null && !cw.getItem().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean isOrganReplaced(CyberwareSlot slot) {
        InstalledCyberware[] arr = slots.get(slot);
        for (InstalledCyberware cw : arr) {
            if (cw != null && !cw.getItem().isEmpty() && cw.getItem().getItem() instanceof ICyberwareItem item) {
                if (item.replacesOrgan() && item.getReplacedOrgans().contains(slot)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasAnyInSlots(CyberwareSlot slot) {

        InstalledCyberware[] arr = slots.get(slot);
        if (arr == null) return false;

        for (InstalledCyberware cw : arr) {
            if (cw == null) continue;
            if (!cw.getItem().isEmpty()) return true;
        }

        return false;
    }

    public boolean hasAnyTagged(TagKey<Item> tag, CyberwareSlot... slotsToCheck) {
        for (CyberwareSlot slot : slotsToCheck) {
            InstalledCyberware[] arr = slots.get(slot);
            if (arr == null) continue;

            for (InstalledCyberware cw : arr) {
                if (cw == null) continue;

                ItemStack stack = cw.getItem();
                if (stack.isEmpty()) continue;

                if (stack.is(tag)) return true;
            }
        }
        return false;
    }

    public boolean hasSpecificItem(Item item, CyberwareSlot... slotsToCheck) {
        for (CyberwareSlot slot : slotsToCheck) {
            InstalledCyberware[] arr = slots.get(slot);
            if (arr == null) continue;

            for (InstalledCyberware cw : arr) {
                if (cw == null) continue;

                ItemStack stack = cw.getItem();
                if (stack.isEmpty()) continue;

                if (stack.is(item)) return true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        for (CyberwareSlot slot : CyberwareSlot.values()) {
            slots.put(slot, new InstalledCyberware[slot.size]);
        }
        dirty = true;
    }

    @Override
    public void setDirty() {
        dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clean() {
        dirty = false;
    }





    public void resetToDefaultOrgans() {

        for (CyberwareSlot slot : CyberwareSlot.values()) {

            InstalledCyberware[] arr = getAll().get(slot);
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {

                ItemStack def = DefaultOrgans.get(slot, i);

                if (def == null || def.isEmpty()) {
                    arr[i] = null;
                    continue;
                }

                int humanityCost = 0;

                if (def.getItem() instanceof ICyberwareItem cyberwareItem) {
                    humanityCost = cyberwareItem.getHumanityCost();
                }

                arr[i] = new InstalledCyberware(def, slot, i, humanityCost);
                arr[i].setPowered(true);
            }
        }

        dirty = true;
    }




    /* ---------------- NBT ---------------- */

    public CompoundTag serializeNBT(net.minecraft.core.HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();

        for (var entry : slots.entrySet()) {
            CyberwareSlot slot = entry.getKey();
            InstalledCyberware[] arr = entry.getValue();
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {
                InstalledCyberware installed = arr[i];
                if (installed == null) continue;

                ItemStack stack = installed.getItem();
                if (stack == null || stack.isEmpty()) continue;

                CompoundTag c = installed.save(provider);

                c.putString("SlotGroup", slot.name());
                c.putInt("Index", i);

                list.add(c);
            }
        }

        tag.put("Cyberware", list);
        tag.putInt("Humanity", humanity);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        clear();

        ListTag list = tag.getList("Cyberware", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag c = list.getCompound(i);

            if (!c.contains("SlotGroup", Tag.TAG_STRING)) continue;

            CyberwareSlot slot = CyberwareSlot.valueOf(c.getString("SlotGroup"));
            int index = c.getInt("Index");

            InstalledCyberware loaded = InstalledCyberware.load(c, provider);

            InstalledCyberware[] arr = slots.get(slot);
            if (arr == null) continue;
            if (index < 0 || index >= arr.length) continue;

            arr[index] = loaded;
        }

        humanity = tag.getInt("Humanity");

        dirty = false;
    }
}