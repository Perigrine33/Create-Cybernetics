package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.tattoo.TattooPngValidator;
import com.perigrine3.createcybernetics.tattoo.TattooUploadResult;
import com.perigrine3.createcybernetics.tattoo.TattooUploadService;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Arrays;

public record TattooUploadC2SPayload(
        String fileName,
        byte[] pngBytes
) implements CustomPacketPayload {

    public static final Type<TattooUploadC2SPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "tattoo_upload_c2s"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TattooUploadC2SPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeUtf(payload.fileName(), 255);
                        buf.writeByteArray(payload.pngBytes());
                    },
                    buf -> new TattooUploadC2SPayload(
                            buf.readUtf(255),
                            buf.readByteArray(TattooPngValidator.MAX_FILE_SIZE_BYTES)
                    )
            );

    public TattooUploadC2SPayload {
        fileName = fileName == null ? "tattoo.png" : fileName;
        pngBytes = pngBytes == null ? new byte[0] : Arrays.copyOf(pngBytes, pngBytes.length);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TattooUploadC2SPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            TattooUploadResult result = TattooUploadService.upload(
                    player,
                    payload.fileName(),
                    payload.pngBytes()
            );

            if (!result.success()) {
                player.sendSystemMessage(Component.literal(result.message()));
                return;
            }

            if (result.tattooId() != null) {
                player.sendSystemMessage(Component.literal(result.message() + " " + result.tattooId()));
            } else {
                player.sendSystemMessage(Component.literal(result.message()));
            }
        });
    }
}