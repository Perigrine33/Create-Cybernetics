package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OpenArmCannonPayload() implements CustomPacketPayload {

    public static final Type<OpenArmCannonPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "open_arm_cannon"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenArmCannonPayload> STREAM_CODEC =
            StreamCodec.unit(new OpenArmCannonPayload());

    @Override
    public Type<OpenArmCannonPayload> type() {
        return TYPE;
    }
}
