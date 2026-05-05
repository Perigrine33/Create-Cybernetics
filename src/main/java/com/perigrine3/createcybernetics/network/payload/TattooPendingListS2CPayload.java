package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.tattoo.client.ClientPendingTattooRegistry;
import com.perigrine3.createcybernetics.tattoo.client.ClientTattooRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record TattooPendingListS2CPayload(
        List<PendingTattooListEntry> tattoos
) implements CustomPacketPayload {

    public static final Type<TattooPendingListS2CPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "tattoo_pending_list_s2c"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TattooPendingListS2CPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeVarInt(payload.tattoos().size());

                        for (PendingTattooListEntry entry : payload.tattoos()) {
                            buf.writeResourceLocation(entry.id());
                            buf.writeUtf(entry.displayName(), 128);
                            buf.writeUtf(entry.fileName(), 255);
                            buf.writeUtf(entry.sha256(), 64);
                            buf.writeLong(entry.sizeBytes());
                            buf.writeVarInt(entry.width());
                            buf.writeVarInt(entry.height());
                        }
                    },
                    buf -> {
                        int count = buf.readVarInt();
                        List<PendingTattooListEntry> entries = new ArrayList<>(count);

                        for (int i = 0; i < count; i++) {
                            entries.add(new PendingTattooListEntry(
                                    buf.readResourceLocation(),
                                    buf.readUtf(128),
                                    buf.readUtf(255),
                                    buf.readUtf(64),
                                    buf.readLong(),
                                    buf.readVarInt(),
                                    buf.readVarInt()
                            ));
                        }

                        return new TattooPendingListS2CPayload(entries);
                    }
            );

    public TattooPendingListS2CPayload {
        tattoos = tattoos == null ? List.of() : List.copyOf(tattoos);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TattooPendingListS2CPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            List<ClientTattooRegistry.ClientTattooEntry> entries = payload.tattoos()
                    .stream()
                    .map(entry -> new ClientTattooRegistry.ClientTattooEntry(
                            entry.id(),
                            entry.displayName(),
                            entry.fileName(),
                            entry.sha256(),
                            entry.sizeBytes(),
                            entry.width(),
                            entry.height()
                    ))
                    .toList();

            ClientPendingTattooRegistry.replacePending(entries);
        });
    }

    public record PendingTattooListEntry(
            ResourceLocation id,
            String displayName,
            String fileName,
            String sha256,
            long sizeBytes,
            int width,
            int height
    ) {
    }
}