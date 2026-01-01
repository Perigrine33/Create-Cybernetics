package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OpenSpinalInjectorPayload() implements CustomPacketPayload {

    public static final Type<OpenSpinalInjectorPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "open_spinal_injector"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenSpinalInjectorPayload> STREAM_CODEC =
            StreamCodec.unit(new OpenSpinalInjectorPayload());

    @Override
    public Type<OpenSpinalInjectorPayload> type() {
        return TYPE;
    }
}
