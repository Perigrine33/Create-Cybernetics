package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OpenChipwareMiniPayload() implements CustomPacketPayload {

    public static final Type<OpenChipwareMiniPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "open_chipware_mini"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenChipwareMiniPayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public OpenChipwareMiniPayload decode(RegistryFriendlyByteBuf buf) {
                    return new OpenChipwareMiniPayload();
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, OpenChipwareMiniPayload value) {

                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
