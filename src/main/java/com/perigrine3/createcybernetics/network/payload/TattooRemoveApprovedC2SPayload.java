package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.tattoo.ServerTattooSync;
import com.perigrine3.createcybernetics.tattoo.TattooModerationService;
import com.perigrine3.createcybernetics.tattoo.TattooUploadResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TattooRemoveApprovedC2SPayload(ResourceLocation tattooId) implements CustomPacketPayload {
    private static final int ADMIN_PERMISSION_LEVEL = 2;

    public static final Type<TattooRemoveApprovedC2SPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "tattoo_remove_approved_c2s"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TattooRemoveApprovedC2SPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> buf.writeResourceLocation(payload.tattooId()),
                    buf -> new TattooRemoveApprovedC2SPayload(buf.readResourceLocation())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TattooRemoveApprovedC2SPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            if (!player.hasPermissions(ADMIN_PERMISSION_LEVEL)) {
                player.sendSystemMessage(Component.literal("You do not have permission to remove approved tattoos."));
                return;
            }

            TattooUploadResult result = TattooModerationService.removeApproved(payload.tattooId());

            if (!result.success()) {
                player.sendSystemMessage(Component.literal(result.message()));
                return;
            }

            ServerTattooSync.sendToAll(player.server);
            player.sendSystemMessage(Component.literal("Removed approved tattoo: " + payload.tattooId()));
        });
    }
}