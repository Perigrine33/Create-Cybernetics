package com.perigrine3.createcybernetics.compat.corpse;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record CorpseVisualSnapshotPayload(UUID corpseEntityUuid, CompoundTag visualData) implements CustomPacketPayload {

    public static final Type<CorpseVisualSnapshotPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "corpse_visual_snapshot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CorpseVisualSnapshotPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeUUID(payload.corpseEntityUuid());
                        buf.writeNbt(payload.visualData() == null ? new CompoundTag() : payload.visualData());
                    },
                    buf -> new CorpseVisualSnapshotPayload(
                            buf.readUUID(),
                            buf.readNbt()
                    )
            );

    @Override
    public Type<CorpseVisualSnapshotPayload> type() {
        return TYPE;
    }

    public static void handle(CorpseVisualSnapshotPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (payload == null || payload.corpseEntityUuid() == null) return;
            CorpseVisualSnapshotClientCache.put(payload.corpseEntityUuid(), payload.visualData());
            CorpseVisualSnapshotRequestClientCache.clear(payload.corpseEntityUuid());
        });
    }
}