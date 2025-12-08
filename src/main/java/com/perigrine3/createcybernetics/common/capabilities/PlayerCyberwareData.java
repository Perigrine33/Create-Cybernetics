package com.perigrine3.createcybernetics.common.capabilities;

import com.perigrine3.createcybernetics.api.ICyberwareData;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerCyberwareData implements ICyberwareData {
    private final List<InstalledCyberware> installed = new ArrayList<>();
    private boolean dirty = false;
    private int humanity = 100;

    @Override
    public List<InstalledCyberware> getInstalled() {
        return Collections.unmodifiableList(installed);
    }

    @Override
    public boolean install(InstalledCyberware c) {
        installed.removeIf(existing -> existing.getSlot().equals(c.getSlot()));

        installed.add(c);
        dirty = true;
        return true;
    }

    @Override
    public InstalledCyberware removeFromSlot(String slot) {
        for (int i = 0; i < installed.size(); i++) {
            InstalledCyberware c = installed.get(i);
            if (c.getSlot().equals(slot)) {
                installed.remove(i);
                dirty = true;
                return c;
            }
        }
        return null;
    }

    @Override
    public void clear() {
        installed.clear();
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

    public int getHumanity() {
        return humanity;
    }

    public void setHumanity(int humanity) {
        this.humanity = humanity;
        dirty = true;
    }

    public ListTag saveNBT() {
        ListTag list = new ListTag();
        for (InstalledCyberware c : installed) {
            list.add(c.save());
        }
        return list;
    }

    public void loadNBT(ListTag list) {
        installed.clear();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            installed.add(InstalledCyberware.load(tag));
        }
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        ListTag list = new ListTag();
        for (InstalledCyberware c : installed) {
            list.add(c.save());
        }

        tag.put("Cyberware", list);
        tag.putInt("Humanity", humanity);

        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        installed.clear();
        ListTag list = tag.getList("Cyberware", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            installed.add(InstalledCyberware.load(list.getCompound(i)));
        }

        humanity = tag.getInt("Humanity");
    }
}
