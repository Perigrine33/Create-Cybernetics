package com.perigrine3.createcybernetics.world;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;

public class MansionBossSpawnData extends SavedData {

    private static final String DATA_NAME = "createcybernetics_mansion_boss_spawns";
    private static final String TAG_ANCHORS = "Anchors";

    private final Set<Long> spawnedMansionAnchors = new HashSet<>();

    public static MansionBossSpawnData create() {
        return new MansionBossSpawnData();
    }

    public static MansionBossSpawnData load(CompoundTag tag, HolderLookup.Provider provider) {
        MansionBossSpawnData data = create();

        if (tag.contains(TAG_ANCHORS, CompoundTag.TAG_LONG_ARRAY)) {
            long[] values = tag.getLongArray(TAG_ANCHORS);
            for (long value : values) {
                data.spawnedMansionAnchors.add(value);
            }
        }

        return data;
    }

    public static MansionBossSpawnData get(ServerLevel level) {
        return level.getServer()
                .overworld()
                .getDataStorage()
                .computeIfAbsent(new Factory<>(MansionBossSpawnData::create, MansionBossSpawnData::load), DATA_NAME);
    }

    public boolean hasSpawnedAt(long anchorPos) {
        return spawnedMansionAnchors.contains(anchorPos);
    }

    public void markSpawned(long anchorPos) {
        if (spawnedMansionAnchors.add(anchorPos)) {
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        long[] values = new long[spawnedMansionAnchors.size()];
        int i = 0;

        for (long value : spawnedMansionAnchors) {
            values[i++] = value;
        }

        tag.put(TAG_ANCHORS, new LongArrayTag(values));
        return tag;
    }
}