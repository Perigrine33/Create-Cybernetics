package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.tattoo.client.ClientTattooRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record TattooListS2CPayload(
        List<TattooListEntry> tattoos
) implements CustomPacketPayload {

    public static final Type<TattooListS2CPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "tattoo_list_s2c"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TattooListS2CPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeVarInt(payload.tattoos().size());

                        for (TattooListEntry entry : payload.tattoos()) {
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
                        List<TattooListEntry> tattoos = new ArrayList<>(count);

                        for (int i = 0; i < count; i++) {
                            tattoos.add(new TattooListEntry(
                                    buf.readResourceLocation(),
                                    buf.readUtf(128),
                                    buf.readUtf(255),
                                    buf.readUtf(64),
                                    buf.readLong(),
                                    buf.readVarInt(),
                                    buf.readVarInt()
                            ));
                        }

                        return new TattooListS2CPayload(tattoos);
                    }
            );

    public TattooListS2CPayload {
        tattoos = tattoos == null ? List.of() : List.copyOf(tattoos);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TattooListS2CPayload payload, IPayloadContext context) {
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

            ClientTattooRegistry.replaceApproved(entries);
        });
    }

    public record TattooListEntry(
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