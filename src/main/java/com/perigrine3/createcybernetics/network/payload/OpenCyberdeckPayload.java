package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OpenCyberdeckPayload() implements CustomPacketPayload {
    public static final Type<OpenCyberdeckPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "open_cyberdeck"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenCyberdeckPayload> STREAM_CODEC =
            StreamCodec.unit(new OpenCyberdeckPayload());

    @Override
    public Type<OpenCyberdeckPayload> type() {
        return TYPE;
    }
}