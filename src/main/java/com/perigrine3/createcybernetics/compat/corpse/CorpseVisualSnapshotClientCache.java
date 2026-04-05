package com.perigrine3.createcybernetics.compat.corpse;

import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CorpseVisualSnapshotClientCache {

    private static final Map<UUID, CompoundTag> SNAPSHOTS = new ConcurrentHashMap<>();

    private CorpseVisualSnapshotClientCache() {
    }

    public static void put(UUID corpseEntityUuid, CompoundTag snapshot) {
        if (corpseEntityUuid == null) return;

        if (snapshot == null || snapshot.isEmpty()) {
            SNAPSHOTS.remove(corpseEntityUuid);
            return;
        }

        SNAPSHOTS.put(corpseEntityUuid, snapshot.copy());
    }

    public static CompoundTag get(UUID corpseEntityUuid) {
        if (corpseEntityUuid == null) return new CompoundTag();

        CompoundTag tag = SNAPSHOTS.get(corpseEntityUuid);
        return tag == null ? new CompoundTag() : tag.copy();
    }

    public static void remove(UUID corpseEntityUuid) {
        if (corpseEntityUuid == null) return;
        SNAPSHOTS.remove(corpseEntityUuid);
    }

    public static void clearAll() {
        SNAPSHOTS.clear();
    }

    public static void applyToPlayer(Player visualPlayer, UUID corpseEntityUuid) {
        if (visualPlayer == null) return;

        CompoundTag pd = visualPlayer.getPersistentData();
        pd.remove(PlayerCyberwareData.HOLO_SNAPSHOT_FLAG);
        pd.remove(PlayerCyberwareData.HOLO_SNAPSHOT_CYBERWARE);

        if (corpseEntityUuid == null) return;

        CompoundTag snapshot = get(corpseEntityUuid);
        if (snapshot.isEmpty()) return;

        pd.putBoolean(PlayerCyberwareData.HOLO_SNAPSHOT_FLAG, true);
        pd.put(PlayerCyberwareData.HOLO_SNAPSHOT_CYBERWARE, snapshot.copy());
    }
}