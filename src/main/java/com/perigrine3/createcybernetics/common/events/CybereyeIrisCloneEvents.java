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
        sendCurrentToClients(newPlayer);
    }

    private static void sendCurrentToClients(ServerPlayer player) {
        CompoundTag root = player.getPersistentData().getCompound(CybereyeOverlayHandler.NBT_ROOT);
        if (root == null || root.isEmpty()) return;

        CompoundTag left = root.getCompound(CybereyeOverlayHandler.NBT_LEFT);
        CompoundTag right = root.getCompound(CybereyeOverlayHandler.NBT_RIGHT);

        int lx = left.getInt(CybereyeOverlayHandler.NBT_X);
        int ly = left.getInt(CybereyeOverlayHandler.NBT_Y);
        int lv = left.getInt(CybereyeOverlayHandler.NBT_VARIANT);

        int rx = right.getInt(CybereyeOverlayHandler.NBT_X);
        int ry = right.getInt(CybereyeOverlayHandler.NBT_Y);
        int rv = right.getInt(CybereyeOverlayHandler.NBT_VARIANT);

        CybereyeIrisSyncS2CPayload out =
                new CybereyeIrisSyncS2CPayload(player.getUUID(), lx, ly, lv, rx, ry, rv);

        PacketDistributor.sendToPlayer(player, out);
        PacketDistributor.sendToPlayersTrackingEntity(player, out);
    }
}