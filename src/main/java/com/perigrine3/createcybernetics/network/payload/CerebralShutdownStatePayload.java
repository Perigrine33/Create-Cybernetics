package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.item.cyberware.CerebralProcessingUnitItem;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CerebralShutdownStatePayload(boolean active) implements CustomPacketPayload {

    public static final Type<CerebralShutdownStatePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cerebral_shutdown_state"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CerebralShutdownStatePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, CerebralShutdownStatePayload::active,
                    CerebralShutdownStatePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CerebralShutdownStatePayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            // Only matters clientside
            if (Minecraft.getInstance().player == null) return;
            CerebralProcessingUnitItem.setClientShutdownActive(msg.active());
        });
    }
}
