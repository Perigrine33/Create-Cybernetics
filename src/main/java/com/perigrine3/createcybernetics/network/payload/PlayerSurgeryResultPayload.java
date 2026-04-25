package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record PlayerSurgeryResultPayload(
        UUID sessionId,
        int roundIndex,
        boolean success,
        int damage
) implements CustomPacketPayload {

    public static final Type<PlayerSurgeryResultPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "player_surgery_result"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerSurgeryResultPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeUUID(payload.sessionId());
                        buf.writeVarInt(payload.roundIndex());
                        buf.writeBoolean(payload.success());
                        buf.writeVarInt(payload.damage());
                    },
                    buf -> new PlayerSurgeryResultPayload(
                            buf.readUUID(),
                            buf.readVarInt(),
                            buf.readBoolean(),
                            buf.readVarInt()
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}