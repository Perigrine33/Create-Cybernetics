package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.client.skin.CybereyeOverlayHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.UUID;

public record CybereyeIrisSyncC2SPayload(
        int leftX, int leftY, int leftVariant,
        int rightX, int rightY, int rightVariant
) implements CustomPacketPayload {

    public static final Type<CybereyeIrisSyncC2SPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cybereye_iris_sync_c2s"));

    public static final StreamCodec<FriendlyByteBuf, CybereyeIrisSyncC2SPayload> STREAM_CODEC =
            StreamCodec.of(CybereyeIrisSyncC2SPayload::encode, CybereyeIrisSyncC2SPayload::decode);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static void encode(FriendlyByteBuf buf, CybereyeIrisSyncC2SPayload p) {
        buf.writeVarInt(p.leftX);
        buf.writeVarInt(p.leftY);
        buf.writeVarInt(p.leftVariant);
        buf.writeVarInt(p.rightX);
        buf.writeVarInt(p.rightY);
        buf.writeVarInt(p.rightVariant);
    }

    private static CybereyeIrisSyncC2SPayload decode(FriendlyByteBuf buf) {
        int lx = buf.readVarInt();
        int ly = buf.readVarInt();
        int lv = buf.readVarInt();
        int rx = buf.readVarInt();
        int ry = buf.readVarInt();
        int rv = buf.readVarInt();
        return new CybereyeIrisSyncC2SPayload(lx, ly, lv, rx, ry, rv);
    }

    public static void handle(CybereyeIrisSyncC2SPayload payload, ServerPlayer sender) {
        if (sender == null) return;

        UUID id = sender.getUUID();

        int lv = clamp(payload.leftVariant, 0, 2);
        int rv = clamp(payload.rightVariant, 0, 2);

        int lw = variantW(lv);
        int lh = variantH(lv);
        int rw = variantW(rv);
        int rh = variantH(rv);

        int lx = clamp(payload.leftX, 8, 16 - lw);
        int ly = clamp(payload.leftY, 8, 16 - lh);
        int rx = clamp(payload.rightX, 8, 16 - rw);
        int ry = clamp(payload.rightY, 8, 16 - rh);

        CompoundTag root = sender.getPersistentData().getCompound(CybereyeOverlayHandler.NBT_ROOT);

        CompoundTag left = new CompoundTag();
        left.putInt(CybereyeOverlayHandler.NBT_X, lx);
        left.putInt(CybereyeOverlayHandler.NBT_Y, ly);
        left.putInt(CybereyeOverlayHandler.NBT_VARIANT, lv);
        root.put(CybereyeOverlayHandler.NBT_LEFT, left);

        CompoundTag right = new CompoundTag();
        right.putInt(CybereyeOverlayHandler.NBT_X, rx);
        right.putInt(CybereyeOverlayHandler.NBT_Y, ry);
        right.putInt(CybereyeOverlayHandler.NBT_VARIANT, rv);
        root.put(CybereyeOverlayHandler.NBT_RIGHT, right);

        sender.getPersistentData().put(CybereyeOverlayHandler.NBT_ROOT, root);

        CybereyeIrisSyncS2CPayload out = new CybereyeIrisSyncS2CPayload(id, lx, ly, lv, rx, ry, rv);

        PacketDistributor.sendToPlayer(sender, out);
        PacketDistributor.sendToPlayersTrackingEntity(sender, out);
    }

    private static int variantW(int variant) {
        return (variant == 2) ? 2 : 1;
    }

    private static int variantH(int variant) {
        return (variant == 0) ? 1 : 2;
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}
