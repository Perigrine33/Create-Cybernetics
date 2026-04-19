package com.perigrine3.createcybernetics.compat.corpse;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record RequestCorpseVisualSnapshotPayload(UUID corpseEntityUuid) implements CustomPacketPayload {

    public static final Type<RequestCorpseVisualSnapshotPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    com.perigrine3.createcybernetics.CreateCybernetics.MODID,
                    "request_corpse_visual_snapshot"
            ));

    public static final StreamCodec<RegistryFriendlyByteBuf, RequestCorpseVisualSnapshotPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> buf.writeUUID(payload.corpseEntityUuid()),
                    buf -> new RequestCorpseVisualSnapshotPayload(buf.readUUID())
            );

    @Override
    public Type<RequestCorpseVisualSnapshotPayload> type() {
        return TYPE;
    }

    public static void handle(RequestCorpseVisualSnapshotPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (payload == null || payload.corpseEntityUuid() == null) return;
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            Entity entity = player.serverLevel().getEntity(payload.corpseEntityUuid());
            if (entity == null) return;
            if (!CorpseCompat.isCorpseEntityForCompat(entity)) return;

            CorpseCompat.ensureCorpseCyberwareLoaded(entity);

            CorpseCyberwareData data = CorpseCompat.getStoredCorpseCyberwareData(entity);
            if (data.isEmpty()) return;

            PacketDistributor.sendToPlayer(
                    player,
                    new CorpseVisualSnapshotPayload(entity.getUUID(), data.serializeNBT())
            );
        });
    }
}