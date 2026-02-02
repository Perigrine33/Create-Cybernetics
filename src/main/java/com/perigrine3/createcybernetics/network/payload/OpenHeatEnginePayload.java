package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OpenHeatEnginePayload() implements CustomPacketPayload {
    public static final Type<OpenHeatEnginePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "open_heat_engine"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenHeatEnginePayload> STREAM_CODEC =
            StreamCodec.unit(new OpenHeatEnginePayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
