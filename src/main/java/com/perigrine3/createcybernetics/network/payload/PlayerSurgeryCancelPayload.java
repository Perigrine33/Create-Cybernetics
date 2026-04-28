package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record PlayerSurgeryCancelPayload(UUID sessionId) implements CustomPacketPayload {

    public static final Type<PlayerSurgeryCancelPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "player_surgery_cancel"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerSurgeryCancelPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> buf.writeUUID(payload.sessionId()),
                    buf -> new PlayerSurgeryCancelPayload(buf.readUUID())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}