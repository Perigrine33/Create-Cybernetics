package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record PlayerSurgeryStartPayload(
        UUID sessionId,
        UUID patientId,
        String patientName,
        int countdownTicks,
        boolean implantBonus
) implements CustomPacketPayload {

    public static final Type<PlayerSurgeryStartPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "player_surgery_start"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerSurgeryStartPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeUUID(payload.sessionId());
                        buf.writeUUID(payload.patientId());
                        buf.writeUtf(payload.patientName());
                        buf.writeVarInt(payload.countdownTicks());
                        buf.writeBoolean(payload.implantBonus());
                    },
                    buf -> new PlayerSurgeryStartPayload(
                            buf.readUUID(),
                            buf.readUUID(),
                            buf.readUtf(),
                            buf.readVarInt(),
                            buf.readBoolean()
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}