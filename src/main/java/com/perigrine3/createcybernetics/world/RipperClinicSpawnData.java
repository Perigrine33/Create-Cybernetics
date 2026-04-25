package com.perigrine3.createcybernetics.world;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;

public class RipperClinicSpawnData extends SavedData {

    private static final String DATA_NAME = "createcybernetics_ripper_clinic_spawns";
    private static final String TAG_ANCHORS = "Anchors";

    private final Set<Long> spawnedClinicAnchors = new HashSet<>();

    public static RipperClinicSpawnData create() {
        return new RipperClinicSpawnData();
    }

    public static RipperClinicSpawnData load(CompoundTag tag, HolderLookup.Provider provider) {
        RipperClinicSpawnData data = create();

        if (tag.contains(TAG_ANCHORS, CompoundTag.TAG_LONG_ARRAY)) {
            long[] values = tag.getLongArray(TAG_ANCHORS);
            for (long value : values) {
                data.spawnedClinicAnchors.add(value);
            }
        }

        return data;
    }

    public static RipperClinicSpawnData get(ServerLevel level) {
        return level.getServer()
                .overworld()
                .getDataStorage()
                .computeIfAbsent(new Factory<>(RipperClinicSpawnData::create, RipperClinicSpawnData::load), DATA_NAME);
    }

    public boolean hasSpawnedAt(long anchorPos) {
        return spawnedClinicAnchors.contains(anchorPos);
    }

    public void markSpawned(long anchorPos) {
        if (spawnedClinicAnchors.add(anchorPos)) {
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        long[] values = new long[spawnedClinicAnchors.size()];
        int i = 0;

        for (long value : spawnedClinicAnchors) {
            values[i++] = value;
        }

        tag.put(TAG_ANCHORS, new LongArrayTag(values));
        return tag;
    }
}