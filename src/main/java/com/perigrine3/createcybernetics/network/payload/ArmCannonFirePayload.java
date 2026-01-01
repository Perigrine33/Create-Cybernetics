package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ArmCannonFirePayload() implements CustomPacketPayload {

    public static final Type<ArmCannonFirePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "arm_cannon_fire"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ArmCannonFirePayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public ArmCannonFirePayload decode(RegistryFriendlyByteBuf buf) {
                    return new ArmCannonFirePayload();
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, ArmCannonFirePayload value) {
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
