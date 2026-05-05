package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.tattoo.TattooPngValidator;
import com.perigrine3.createcybernetics.tattoo.client.ClientTattooTextureCache;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Arrays;

public record TattooImageDataS2CPayload(
        ResourceLocation tattooId,
        String displayName,
        String fileName,
        String sha256,
        int width,
        int height,
        byte[] pngBytes
) implements CustomPacketPayload {

    public static final Type<TattooImageDataS2CPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "tattoo_image_data_s2c"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TattooImageDataS2CPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeResourceLocation(payload.tattooId());
                        buf.writeUtf(payload.displayName(), 128);
                        buf.writeUtf(payload.fileName(), 255);
                        buf.writeUtf(payload.sha256(), 64);
                        buf.writeVarInt(payload.width());
                        buf.writeVarInt(payload.height());
                        buf.writeByteArray(payload.pngBytes());
                    },
                    buf -> new TattooImageDataS2CPayload(
                            buf.readResourceLocation(),
                            buf.readUtf(128),
                            buf.readUtf(255),
                            buf.readUtf(64),
                            buf.readVarInt(),
                            buf.readVarInt(),
                            buf.readByteArray(TattooPngValidator.MAX_FILE_SIZE_BYTES)
                    )
            );

    public TattooImageDataS2CPayload {
        displayName = displayName == null ? "" : displayName;
        fileName = fileName == null ? "" : fileName;
        sha256 = sha256 == null ? "" : sha256;
        pngBytes = pngBytes == null ? new byte[0] : Arrays.copyOf(pngBytes, pngBytes.length);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TattooImageDataS2CPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientTattooTextureCache.acceptImage(payload));
    }
}