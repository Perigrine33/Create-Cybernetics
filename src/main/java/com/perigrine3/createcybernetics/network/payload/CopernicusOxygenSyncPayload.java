package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.compat.northstar.CopernicusClientHooks;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CopernicusOxygenSyncPayload(int oxygen) implements CustomPacketPayload {

    public static final Type<CopernicusOxygenSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "copernicus_oxygen_sync"));

    public static final StreamCodec<ByteBuf, CopernicusOxygenSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    CopernicusOxygenSyncPayload::oxygen,
                    CopernicusOxygenSyncPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CopernicusOxygenSyncPayload payload, IPayloadContext ctx) {
        // Always enqueue onto the main thread.
        ctx.enqueueWork(() -> RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                CopernicusClientHooks.setOxygenHud(payload.oxygen())
        ));
    }
}
