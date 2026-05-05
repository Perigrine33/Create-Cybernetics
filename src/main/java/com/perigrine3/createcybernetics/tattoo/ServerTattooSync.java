package com.perigrine3.createcybernetics.tattoo;

import com.perigrine3.createcybernetics.network.payload.TattooListS2CPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public final class ServerTattooSync {
    private ServerTattooSync() {
    }

    public static void sendTo(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, createPayload());
    }

    public static void sendToAll(MinecraftServer server) {
        if (server == null) {
            return;
        }

        TattooListS2CPayload payload = createPayload();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PacketDistributor.sendToPlayer(player, payload);
        }
    }

    private static TattooListS2CPayload createPayload() {
        List<TattooListS2CPayload.TattooListEntry> entries = ServerTattooRegistry.approvedTattoos()
                .stream()
                .map(entry -> new TattooListS2CPayload.TattooListEntry(
                        entry.id(),
                        entry.displayName(),
                        entry.fileName(),
                        entry.sha256(),
                        entry.sizeBytes(),
                        entry.width(),
                        entry.height()
                ))
                .toList();

        return new TattooListS2CPayload(entries);
    }
}