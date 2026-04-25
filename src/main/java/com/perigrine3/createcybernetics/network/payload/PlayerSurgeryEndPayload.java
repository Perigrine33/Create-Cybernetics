package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record PlayerSurgeryEndPayload(UUID sessionId, boolean completed) implements CustomPacketPayload {

    public static final Type<PlayerSurgeryEndPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "player_surgery_end"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerSurgeryEndPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeUUID(payload.sessionId());
                        buf.writeBoolean(payload.completed());
                    },
                    buf -> new PlayerSurgeryEndPayload(buf.readUUID(), buf.readBoolean())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}