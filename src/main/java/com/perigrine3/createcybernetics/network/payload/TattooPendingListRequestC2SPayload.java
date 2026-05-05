package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.tattoo.ServerTattooRegistry;
import com.perigrine3.createcybernetics.tattoo.TattooEntry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record TattooPendingListRequestC2SPayload() implements CustomPacketPayload {
    private static final int ADMIN_PERMISSION_LEVEL = 2;

    public static final Type<TattooPendingListRequestC2SPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "tattoo_pending_list_request_c2s"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TattooPendingListRequestC2SPayload> STREAM_CODEC =
            StreamCodec.unit(new TattooPendingListRequestC2SPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TattooPendingListRequestC2SPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            if (!player.hasPermissions(ADMIN_PERMISSION_LEVEL)) {
                player.sendSystemMessage(Component.literal("You do not have permission to view pending tattoos."));
                PacketDistributor.sendToPlayer(player, new TattooPendingListS2CPayload(List.of()));
                return;
            }

            List<TattooPendingListS2CPayload.PendingTattooListEntry> entries = ServerTattooRegistry.pendingTattoos()
                    .stream()
                    .map(TattooPendingListRequestC2SPayload::toPayloadEntry)
                    .toList();

            PacketDistributor.sendToPlayer(player, new TattooPendingListS2CPayload(entries));
        });
    }

    private static TattooPendingListS2CPayload.PendingTattooListEntry toPayloadEntry(TattooEntry entry) {
        return new TattooPendingListS2CPayload.PendingTattooListEntry(
                entry.id(),
                entry.displayName(),
                entry.fileName(),
                entry.sha256(),
                entry.sizeBytes(),
                entry.width(),
                entry.height()
        );
    }
}