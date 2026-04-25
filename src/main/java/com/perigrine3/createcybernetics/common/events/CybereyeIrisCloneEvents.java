package com.perigrine3.createcybernetics.common.events;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.client.skin.CybereyeOverlayHandler;
import com.perigrine3.createcybernetics.network.payload.CybereyeIrisSyncS2CPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class CybereyeIrisCloneEvents {

    private CybereyeIrisCloneEvents() {}

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!(event.getEntity() instanceof ServerPlayer newPlayer)) return;
        if (!(event.getOriginal() instanceof ServerPlayer oldPlayer)) return;

        CompoundTag oldRoot = oldPlayer.getPersistentData().getCompound(CybereyeOverlayHandler.NBT_ROOT);
        if (oldRoot == null || oldRoot.isEmpty()) return;

        newPlayer.getPersistentData().put(CybereyeOverlayHandler.NBT_ROOT, oldRoot.copy());
        sendCurrentToSelfAndTrackers(newPlayer);
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (!(event.getEntity() instanceof ServerPlayer viewer)) return;
        if (!(event.getTarget() instanceof ServerPlayer target)) return;

        sendCurrentToViewer(target, viewer);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        sendCurrentToSelfAndTrackers(player);
    }

    private static void sendCurrentToSelfAndTrackers(ServerPlayer player) {
        CybereyeIrisSyncS2CPayload out = makePayload(player);
        if (out == null) return;

        PacketDistributor.sendToPlayer(player, out);
        PacketDistributor.sendToPlayersTrackingEntity(player, out);
    }

    private static void sendCurrentToViewer(ServerPlayer target, ServerPlayer viewer) {
        CybereyeIrisSyncS2CPayload out = makePayload(target);
        if (out == null) return;

        PacketDistributor.sendToPlayer(viewer, out);
    }

    private static CybereyeIrisSyncS2CPayload makePayload(ServerPlayer player) {
        CompoundTag root = player.getPersistentData().getCompound(CybereyeOverlayHandler.NBT_ROOT);
        if (root == null || root.isEmpty()) return null;

        CompoundTag left = root.getCompound(CybereyeOverlayHandler.NBT_LEFT);
        CompoundTag right = root.getCompound(CybereyeOverlayHandler.NBT_RIGHT);

        if (left == null || left.isEmpty()) return null;
        if (right == null || right.isEmpty()) return null;

        int lx = left.getInt(CybereyeOverlayHandler.NBT_X);
        int ly = left.getInt(CybereyeOverlayHandler.NBT_Y);
        int lv = left.getInt(CybereyeOverlayHandler.NBT_VARIANT);

        int rx = right.getInt(CybereyeOverlayHandler.NBT_X);
        int ry = right.getInt(CybereyeOverlayHandler.NBT_Y);
        int rv = right.getInt(CybereyeOverlayHandler.NBT_VARIANT);

        return new CybereyeIrisSyncS2CPayload(player.getUUID(), lx, ly, lv, rx, ry, rv);
    }
}