package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record PlayerSurgeryRoundPayload(
        UUID sessionId,
        int roundIndex,
        int roundCount,
        int roundDurationTicks,
        float successCenter,
        float successWidth,
        boolean implantBonus
) implements CustomPacketPayload {

    public static final Type<PlayerSurgeryRoundPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "player_surgery_round"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerSurgeryRoundPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeUUID(payload.sessionId());
                        buf.writeVarInt(payload.roundIndex());
                        buf.writeVarInt(payload.roundCount());
                        buf.writeVarInt(payload.roundDurationTicks());
                        buf.writeFloat(payload.successCenter());
                        buf.writeFloat(payload.successWidth());
                        buf.writeBoolean(payload.implantBonus());
                    },
                    buf -> new PlayerSurgeryRoundPayload(
                            buf.readUUID(),
                            buf.readVarInt(),
                            buf.readVarInt(),
                            buf.readVarInt(),
                            buf.readFloat(),
                            buf.readFloat(),
                            buf.readBoolean()
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}