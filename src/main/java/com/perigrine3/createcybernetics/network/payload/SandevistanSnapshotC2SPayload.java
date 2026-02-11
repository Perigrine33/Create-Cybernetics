package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SandevistanSnapshotC2SPayload(
        double x, double y, double z,
        float bodyYaw,
        boolean crouching,

        float headX, float headY, float headZ,
        float bodyX, float bodyY, float bodyZ,
        float rArmX, float rArmY, float rArmZ,
        float lArmX, float lArmY, float lArmZ,
        float rLegX, float rLegY, float rLegZ,
        float lLegX, float lLegY, float lLegZ,

        float ageInTicks
) implements CustomPacketPayload {

    public static final Type<SandevistanSnapshotC2SPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "sandevistan_snapshot_c2s"));

    public static final StreamCodec<FriendlyByteBuf, SandevistanSnapshotC2SPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, p) -> {
                        buf.writeDouble(p.x()); buf.writeDouble(p.y()); buf.writeDouble(p.z());
                        buf.writeFloat(p.bodyYaw());
                        buf.writeBoolean(p.crouching());

                        buf.writeFloat(p.headX()); buf.writeFloat(p.headY()); buf.writeFloat(p.headZ());
                        buf.writeFloat(p.bodyX()); buf.writeFloat(p.bodyY()); buf.writeFloat(p.bodyZ());
                        buf.writeFloat(p.rArmX()); buf.writeFloat(p.rArmY()); buf.writeFloat(p.rArmZ());
                        buf.writeFloat(p.lArmX()); buf.writeFloat(p.lArmY()); buf.writeFloat(p.lArmZ());
                        buf.writeFloat(p.rLegX()); buf.writeFloat(p.rLegY()); buf.writeFloat(p.rLegZ());
                        buf.writeFloat(p.lLegX()); buf.writeFloat(p.lLegY()); buf.writeFloat(p.lLegZ());

                        buf.writeFloat(p.ageInTicks());
                    },
                    buf -> new SandevistanSnapshotC2SPayload(
                            buf.readDouble(), buf.readDouble(), buf.readDouble(),
                            buf.readFloat(),
                            buf.readBoolean(),

                            buf.readFloat(), buf.readFloat(), buf.readFloat(),
                            buf.readFloat(), buf.readFloat(), buf.readFloat(),
                            buf.readFloat(), buf.readFloat(), buf.readFloat(),
                            buf.readFloat(), buf.readFloat(), buf.readFloat(),
                            buf.readFloat(), buf.readFloat(), buf.readFloat(),
                            buf.readFloat(), buf.readFloat(), buf.readFloat(),

                            buf.readFloat()
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
