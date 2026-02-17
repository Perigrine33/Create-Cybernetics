package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.screen.custom.hud.CyberwareHudLayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EnergyHudSyncPayload(
        int generatedPerTick,
        int consumedPerTick,
        int storedBefore,
        int storedAfter,
        int capacity,
        int netDeltaPerTick
) implements CustomPacketPayload {

    public static final Type<EnergyHudSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "energy_hud_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnergyHudSyncPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, msg) -> {
                        buf.writeInt(msg.generatedPerTick);
                        buf.writeInt(msg.consumedPerTick);
                        buf.writeInt(msg.storedBefore);
                        buf.writeInt(msg.storedAfter);
                        buf.writeInt(msg.capacity);
                        buf.writeInt(msg.netDeltaPerTick);
                    },
                    buf -> new EnergyHudSyncPayload(
                            buf.readInt(),
                            buf.readInt(),
                            buf.readInt(),
                            buf.readInt(),
                            buf.readInt(),
                            buf.readInt()
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(EnergyHudSyncPayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            CyberwareHudLayer.ClientEnergyState.update(new CyberwareHudLayer.TickSnapshot(
                    msg.generatedPerTick,
                    msg.consumedPerTick,
                    msg.storedBefore,
                    msg.storedAfter,
                    msg.capacity,
                    msg.netDeltaPerTick
            ));
        });
    }
}
