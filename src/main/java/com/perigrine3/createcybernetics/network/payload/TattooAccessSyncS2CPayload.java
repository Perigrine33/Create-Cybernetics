package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.ConfigValues;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.tattoo.client.ClientTattooAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TattooAccessSyncS2CPayload(
        ConfigValues.TattooUploadMode uploadMode,
        boolean canOpenBrowser,
        boolean canUpload,
        boolean canModerate
) implements CustomPacketPayload {
    public static final Type<TattooAccessSyncS2CPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "tattoo_access_sync_s2c"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TattooAccessSyncS2CPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeEnum(payload.uploadMode());
                        buf.writeBoolean(payload.canOpenBrowser());
                        buf.writeBoolean(payload.canUpload());
                        buf.writeBoolean(payload.canModerate());
                    },
                    buf -> new TattooAccessSyncS2CPayload(
                            buf.readEnum(ConfigValues.TattooUploadMode.class),
                            buf.readBoolean(),
                            buf.readBoolean(),
                            buf.readBoolean()
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TattooAccessSyncS2CPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientTattooAccess.update(
                payload.uploadMode(),
                payload.canOpenBrowser(),
                payload.canUpload(),
                payload.canModerate()
        ));
    }
}