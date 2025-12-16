package com.perigrine3.createcybernetics.common.capabilities;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareData;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

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

    /* ---------------- NBT ---------------- */

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();

        for (var entry : slots.entrySet()) {
            CyberwareSlot slot = entry.getKey();
            InstalledCyberware[] arr = entry.getValue();

            for (int i = 0; i < arr.length; i++) {
                if (arr[i] != null) {
                    CompoundTag c = arr[i].save();
                    c.putString("SlotGroup", slot.name());
                    c.putInt("Index", i);
                    list.add(c);
                }
            }
        }

        tag.put("Cyberware", list);
        tag.putInt("Humanity", humanity);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        clear();

        ListTag list = tag.getList("Cyberware", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag c = list.getCompound(i);

            CyberwareSlot slot = CyberwareSlot.valueOf(c.getString("SlotGroup"));
            int index = c.getInt("Index");

            slots.get(slot)[index] = InstalledCyberware.load(c);
        }

        humanity = tag.getInt("Humanity");
    }
}
