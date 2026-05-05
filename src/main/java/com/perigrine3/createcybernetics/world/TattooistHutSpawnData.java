package com.perigrine3.createcybernetics.world;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;

public class TattooistHutSpawnData extends SavedData {

    private static final String DATA_NAME = "createcybernetics_tattooist_hut_spawns";
    private static final String TAG_ANCHORS = "Anchors";

    private final Set<Long> spawnedHutAnchors = new HashSet<>();

    public static TattooistHutSpawnData create() {
        return new TattooistHutSpawnData();
    }

    public static TattooistHutSpawnData load(CompoundTag tag, HolderLookup.Provider provider) {
        TattooistHutSpawnData data = create();

        if (tag.contains(TAG_ANCHORS, CompoundTag.TAG_LONG_ARRAY)) {
            long[] values = tag.getLongArray(TAG_ANCHORS);
            for (long value : values) {
                data.spawnedHutAnchors.add(value);
            }
        }

        return data;
    }

    public static TattooistHutSpawnData get(ServerLevel level) {
        return level.getServer()
                .overworld()
                .getDataStorage()
                .computeIfAbsent(new Factory<>(TattooistHutSpawnData::create, TattooistHutSpawnData::load), DATA_NAME);
    }

    public boolean hasSpawnedAt(long anchorPos) {
        return spawnedHutAnchors.contains(anchorPos);
    }

    public void markSpawned(long anchorPos) {
        if (spawnedHutAnchors.add(anchorPos)) {
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        long[] values = new long[spawnedHutAnchors.size()];
        int i = 0;

        for (long value : spawnedHutAnchors) {
            values[i++] = value;
        }

        tag.put(TAG_ANCHORS, new LongArrayTag(values));
        return tag;
    }
}