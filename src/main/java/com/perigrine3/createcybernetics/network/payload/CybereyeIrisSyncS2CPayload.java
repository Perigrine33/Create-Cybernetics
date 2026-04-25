package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.client.skin.CybereyeOverlayHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record CybereyeIrisSyncS2CPayload(
        UUID playerId,
        int leftX, int leftY, int leftVariant,
        int rightX, int rightY, int rightVariant
) implements CustomPacketPayload {

    public static final Type<CybereyeIrisSyncS2CPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cybereye_iris_sync_s2c"));

    public static final StreamCodec<FriendlyByteBuf, CybereyeIrisSyncS2CPayload> STREAM_CODEC =
            StreamCodec.of(CybereyeIrisSyncS2CPayload::encode, CybereyeIrisSyncS2CPayload::decode);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static void encode(FriendlyByteBuf buf, CybereyeIrisSyncS2CPayload p) {
        buf.writeUUID(p.playerId);
        buf.writeVarInt(p.leftX);
        buf.writeVarInt(p.leftY);
        buf.writeVarInt(p.leftVariant);
        buf.writeVarInt(p.rightX);
        buf.writeVarInt(p.rightY);
        buf.writeVarInt(p.rightVariant);
    }

    private static CybereyeIrisSyncS2CPayload decode(FriendlyByteBuf buf) {
        UUID id = buf.readUUID();

        int lx = buf.readVarInt();
        int ly = buf.readVarInt();
        int lv = buf.readVarInt();

        int rx = buf.readVarInt();
        int ry = buf.readVarInt();
        int rv = buf.readVarInt();

        return new CybereyeIrisSyncS2CPayload(id, lx, ly, lv, rx, ry, rv);
    }

    public static void handle(CybereyeIrisSyncS2CPayload payload) {
        Minecraft mc = Minecraft.getInstance();

        mc.execute(() -> {
            CybereyeOverlayHandler.applySyncedConfig(
                    payload.playerId,
                    payload.leftX,
                    payload.leftY,
                    payload.leftVariant,
                    payload.rightX,
                    payload.rightY,
                    payload.rightVariant
            );
        });
    }
}