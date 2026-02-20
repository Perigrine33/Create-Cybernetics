package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.client.skin.CybereyeOverlayHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

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
        if (mc.level == null) return;

        Player target = mc.level.getPlayerByUUID(payload.playerId);
        if (target == null) return;

        CompoundTag root = target.getPersistentData().getCompound(CybereyeOverlayHandler.NBT_ROOT);

        CompoundTag left = new CompoundTag();
        left.putInt(CybereyeOverlayHandler.NBT_X, payload.leftX);
        left.putInt(CybereyeOverlayHandler.NBT_Y, payload.leftY);
        left.putInt(CybereyeOverlayHandler.NBT_VARIANT, payload.leftVariant);
        root.put(CybereyeOverlayHandler.NBT_LEFT, left);

        CompoundTag right = new CompoundTag();
        right.putInt(CybereyeOverlayHandler.NBT_X, payload.rightX);
        right.putInt(CybereyeOverlayHandler.NBT_Y, payload.rightY);
        right.putInt(CybereyeOverlayHandler.NBT_VARIANT, payload.rightVariant);
        root.put(CybereyeOverlayHandler.NBT_RIGHT, right);

        target.getPersistentData().put(CybereyeOverlayHandler.NBT_ROOT, root);

        CybereyeOverlayHandler.invalidate(target);
    }
}
