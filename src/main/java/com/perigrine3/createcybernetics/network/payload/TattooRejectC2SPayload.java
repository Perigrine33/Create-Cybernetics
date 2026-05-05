package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.tattoo.ServerTattooRegistry;
import com.perigrine3.createcybernetics.tattoo.TattooEntry;
import com.perigrine3.createcybernetics.tattoo.TattooModerationService;
import com.perigrine3.createcybernetics.tattoo.TattooUploadResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record TattooRejectC2SPayload(ResourceLocation tattooId) implements CustomPacketPayload {
    private static final int ADMIN_PERMISSION_LEVEL = 2;

    public static final Type<TattooRejectC2SPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "tattoo_reject_c2s"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TattooRejectC2SPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> buf.writeResourceLocation(payload.tattooId()),
                    buf -> new TattooRejectC2SPayload(buf.readResourceLocation())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TattooRejectC2SPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            if (!player.hasPermissions(ADMIN_PERMISSION_LEVEL)) {
                player.sendSystemMessage(Component.literal("You do not have permission to reject tattoos."));
                return;
            }

            TattooUploadResult result = TattooModerationService.reject(payload.tattooId());

            if (!result.success()) {
                player.sendSystemMessage(Component.literal(result.message()));
                sendPendingListTo(player);
                return;
            }

            sendPendingListTo(player);
            player.sendSystemMessage(Component.literal("Rejected tattoo: " + payload.tattooId()));
        });
    }

    private static void sendPendingListTo(ServerPlayer player) {
        List<TattooPendingListS2CPayload.PendingTattooListEntry> entries = ServerTattooRegistry.pendingTattoos()
                .stream()
                .map(TattooRejectC2SPayload::toPayloadEntry)
                .toList();

        PacketDistributor.sendToPlayer(player, new TattooPendingListS2CPayload(entries));
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