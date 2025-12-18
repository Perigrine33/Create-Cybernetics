package com.perigrine3.createcybernetics.world;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class MansionBossSavedData extends SavedData {
    private static final String NAME = "createcybernetics_mansion_bosses";
    private final LongSet spawnedMansions = new LongOpenHashSet(); // store ChunkPos.toLong()

    public MansionBossSavedData() {}

    public static MansionBossSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(MansionBossSavedData::new, MansionBossSavedData::load),
                NAME
        );
    }

    public boolean hasSpawned(long mansionStartChunkLong) {
        return spawnedMansions.contains(mansionStartChunkLong);
    }

    public void markSpawned(long mansionStartChunkLong) {
        if (spawnedMansions.add(mansionStartChunkLong)) {
            setDirty();
        }
    }

    public static MansionBossSavedData load(CompoundTag tag, HolderLookup.Provider provider) {
        MansionBossSavedData data = new MansionBossSavedData();
        long[] arr = tag.getLongArray("Spawned");
        for (long v : arr) data.spawnedMansions.add(v);
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putLongArray("Spawned", spawnedMansions.toLongArray());
        return tag;
    }
}
