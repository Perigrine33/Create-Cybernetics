package com.perigrine3.createcybernetics.compat.corpse;

import com.perigrine3.createcybernetics.client.skin.CybereyeOverlayHandler;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public final class CorpseCyberwareData {

    private static final String NBT_SNAPSHOT = "Snapshot";
    private static final String NBT_CYBEREYE_ROOT = "CybereyeRoot";

    private CompoundTag snapshot = new CompoundTag();
    private CompoundTag cybereyeRoot = new CompoundTag();

    public CorpseCyberwareData() {
    }

    public void captureFromPlayer(Player player, HolderLookup.Provider provider) {
        if (player == null) {
            snapshot = new CompoundTag();
            cybereyeRoot = new CompoundTag();
            return;
        }

        snapshot = PlayerCyberwareData.createSnapshotTagFor(player, provider);

        CompoundTag eyeRoot = player.getPersistentData().getCompound(CybereyeOverlayHandler.NBT_ROOT);
        cybereyeRoot = eyeRoot == null ? new CompoundTag() : eyeRoot.copy();
    }

    public void setSnapshot(CompoundTag tag) {
        snapshot = tag == null ? new CompoundTag() : tag.copy();
    }

    public CompoundTag getSnapshot() {
        return snapshot == null ? new CompoundTag() : snapshot.copy();
    }

    public void setCybereyeRoot(CompoundTag tag) {
        cybereyeRoot = tag == null ? new CompoundTag() : tag.copy();
    }

    public CompoundTag getCybereyeRoot() {
        return cybereyeRoot == null ? new CompoundTag() : cybereyeRoot.copy();
    }

    public boolean hasCybereyeRoot() {
        return cybereyeRoot != null && !cybereyeRoot.isEmpty();
    }

    public boolean isEmpty() {
        return (snapshot == null || snapshot.isEmpty())
                && (cybereyeRoot == null || cybereyeRoot.isEmpty());
    }

    public PlayerCyberwareData toPlayerCyberwareData(HolderLookup.Provider provider) {
        return PlayerCyberwareData.fromSnapshotTag(getSnapshot(), provider);
    }

    public CompoundTag serializeNBT() {
        CompoundTag out = new CompoundTag();
        out.put(NBT_SNAPSHOT, getSnapshot());
        out.put(NBT_CYBEREYE_ROOT, getCybereyeRoot());
        return out;
    }

    public void deserializeNBT(CompoundTag tag) {
        if (tag == null || tag.isEmpty()) {
            snapshot = new CompoundTag();
            cybereyeRoot = new CompoundTag();
            return;
        }

        if (tag.contains(NBT_SNAPSHOT)) {
            snapshot = tag.getCompound(NBT_SNAPSHOT).copy();
        } else {
            snapshot = new CompoundTag();
        }

        if (tag.contains(NBT_CYBEREYE_ROOT)) {
            cybereyeRoot = tag.getCompound(NBT_CYBEREYE_ROOT).copy();
        } else {
            cybereyeRoot = new CompoundTag();
        }
    }
}