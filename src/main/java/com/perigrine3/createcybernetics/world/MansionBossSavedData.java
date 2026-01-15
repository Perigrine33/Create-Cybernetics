package com.perigrine3.createcybernetics.world;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class MansionBossSavedData extends SavedData {
    private static final String NAME = "createcybernetics_mansion_bosses";

    private final LongSet rolledFail = new LongOpenHashSet();
    private final LongSet pending    = new LongOpenHashSet();
    private final LongSet spawned    = new LongOpenHashSet();

    public MansionBossSavedData() {}

    public static MansionBossSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(MansionBossSavedData::new, MansionBossSavedData::load),
                NAME
        );
    }

    public boolean isRolledFail(long key) { return rolledFail.contains(key); }
    public boolean isPending(long key)    { return pending.contains(key); }
    public boolean isSpawned(long key)    { return spawned.contains(key); }

    public void markRolledFail(long key) {
        boolean changed = rolledFail.add(key) | pending.remove(key);
        if (changed) setDirty();
    }

    public void markPending(long key) {
        // Only meaningful if it hasn't failed/spawned
        if (spawned.contains(key) || rolledFail.contains(key)) return;
        if (pending.add(key)) setDirty();
    }

    public void markSpawned(long key) {
        boolean changed = spawned.add(key) | pending.remove(key);
        if (changed) setDirty();
    }

    public static MansionBossSavedData load(CompoundTag tag, HolderLookup.Provider provider) {
        MansionBossSavedData data = new MansionBossSavedData();

        if (tag.contains("RolledFail")) {
            for (long v : tag.getLongArray("RolledFail")) data.rolledFail.add(v);
        }
        if (tag.contains("Pending")) {
            for (long v : tag.getLongArray("Pending")) data.pending.add(v);
        }
        if (tag.contains("Spawned")) {
            for (long v : tag.getLongArray("Spawned")) data.spawned.add(v);
        }
        if (!tag.contains("RolledFail") && tag.contains("Attempted")) {
            for (long v : tag.getLongArray("Attempted")) data.rolledFail.add(v);
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putLongArray("RolledFail", rolledFail.toLongArray());
        tag.putLongArray("Pending", pending.toLongArray());
        tag.putLongArray("Spawned", spawned.toLongArray());
        return tag;
    }
}
