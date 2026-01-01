package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.screen.custom.hud.CyberwareHudLayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EnergyHudSnapshotPayload(int stored, int capacity, int netDeltaPerTick) implements CustomPacketPayload {

    public static final Type<EnergyHudSnapshotPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "energy_hud_snapshot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnergyHudSnapshotPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, EnergyHudSnapshotPayload::stored,
                    ByteBufCodecs.INT, EnergyHudSnapshotPayload::capacity,
                    ByteBufCodecs.INT, EnergyHudSnapshotPayload::netDeltaPerTick,
                    EnergyHudSnapshotPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(EnergyHudSnapshotPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            CyberwareHudLayer.ClientEnergyState.update(
                    new CyberwareHudLayer.TickSnapshot(
                            0, 0,
                            payload.stored(), payload.stored(),
                            payload.capacity(),
                            payload.netDeltaPerTick()
                    )
            );
        });
    }
}
