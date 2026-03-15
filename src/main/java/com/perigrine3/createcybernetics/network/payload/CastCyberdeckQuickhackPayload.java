package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CastCyberdeckQuickhackPayload(int cyberdeckSlot, int targetEntityId) implements CustomPacketPayload {
    public static final Type<CastCyberdeckQuickhackPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cast_cyberdeck_quickhack"));

    public static final StreamCodec<ByteBuf, CastCyberdeckQuickhackPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    CastCyberdeckQuickhackPayload::cyberdeckSlot,
                    ByteBufCodecs.VAR_INT,
                    CastCyberdeckQuickhackPayload::targetEntityId,
                    CastCyberdeckQuickhackPayload::new
            );

    @Override
    public Type<CastCyberdeckQuickhackPayload> type() {
        return TYPE;
    }
}