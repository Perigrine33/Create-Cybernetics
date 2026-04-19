package com.perigrine3.createcybernetics.compat.corpse;

import com.perigrine3.createcybernetics.client.skin.CybereyeOverlayHandler;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CorpseVisualSnapshotClientCache {

    private static final String NBT_SNAPSHOT = "Snapshot";
    private static final String NBT_CYBEREYE_ROOT = "CybereyeRoot";

    private static final Map<UUID, CompoundTag> SNAPSHOTS = new ConcurrentHashMap<>();

    private CorpseVisualSnapshotClientCache() {
    }

    public static void put(UUID corpseEntityUuid, CompoundTag snapshotBundle) {
        if (corpseEntityUuid == null) return;

        if (snapshotBundle == null || snapshotBundle.isEmpty()) {
            SNAPSHOTS.remove(corpseEntityUuid);
            return;
        }

        SNAPSHOTS.put(corpseEntityUuid, snapshotBundle.copy());
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
        pd.remove(CybereyeOverlayHandler.NBT_ROOT);

        if (corpseEntityUuid == null) {
            CybereyeOverlayHandler.invalidate(visualPlayer);
            return;
        }

        CompoundTag bundle = get(corpseEntityUuid);
        if (bundle.isEmpty()) {
            CybereyeOverlayHandler.invalidate(visualPlayer);
            return;
        }

        CompoundTag snapshot = bundle.contains(NBT_SNAPSHOT)
                ? bundle.getCompound(NBT_SNAPSHOT).copy()
                : new CompoundTag();

        if (!snapshot.isEmpty()) {
            pd.putBoolean(PlayerCyberwareData.HOLO_SNAPSHOT_FLAG, true);
            pd.put(PlayerCyberwareData.HOLO_SNAPSHOT_CYBERWARE, snapshot);
        }

        CompoundTag eyeRoot = bundle.contains(NBT_CYBEREYE_ROOT)
                ? bundle.getCompound(NBT_CYBEREYE_ROOT).copy()
                : new CompoundTag();

        if (!eyeRoot.isEmpty()) {
            pd.put(CybereyeOverlayHandler.NBT_ROOT, eyeRoot);
        }

        CybereyeOverlayHandler.invalidate(visualPlayer);
    }
}