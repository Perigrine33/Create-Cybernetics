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

import java.nio.file.Files;

public record TattooImageRequestC2SPayload(
        ResourceLocation tattooId,
        String expectedSha256
) implements CustomPacketPayload {
    private static final int ADMIN_PERMISSION_LEVEL = 2;

    public static final Type<TattooImageRequestC2SPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "tattoo_image_request_c2s"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TattooImageRequestC2SPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeResourceLocation(payload.tattooId());
                        buf.writeUtf(payload.expectedSha256(), 64);
                    },
                    buf -> new TattooImageRequestC2SPayload(
                            buf.readResourceLocation(),
                            buf.readUtf(64)
                    )
            );

    public TattooImageRequestC2SPayload {
        expectedSha256 = expectedSha256 == null ? "" : expectedSha256;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TattooImageRequestC2SPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            TattooEntry entry = ServerTattooRegistry.getApproved(payload.tattooId());

            if (entry == null && player.hasPermissions(ADMIN_PERMISSION_LEVEL)) {
                entry = ServerTattooRegistry.getPending(payload.tattooId());
            }

            if (entry == null) {
                player.sendSystemMessage(Component.literal("Tattoo does not exist or is not visible: " + payload.tattooId()));
                return;
            }

            if (!payload.expectedSha256().isEmpty() && !payload.expectedSha256().equals(entry.sha256())) {
                player.sendSystemMessage(Component.literal("Tattoo hash mismatch: " + payload.tattooId()));
                return;
            }

            try {
                byte[] bytes = Files.readAllBytes(entry.path());

                PacketDistributor.sendToPlayer(player, new TattooImageDataS2CPayload(
                        entry.id(),
                        entry.displayName(),
                        entry.fileName(),
                        entry.sha256(),
                        entry.width(),
                        entry.height(),
                        bytes
                ));
            } catch (Exception ex) {
                CreateCybernetics.LOGGER.warn("Failed to read tattoo image '{}'", payload.tattooId(), ex);
                player.sendSystemMessage(Component.literal("Failed to load tattoo image: " + payload.tattooId()));
            }
        });
    }
}