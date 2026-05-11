package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.item.cyberware.arm.ElectricArcCannonItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ArcCannonFirePayload() implements CustomPacketPayload {
    public static final Type<ArcCannonFirePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "arc_cannon_fire"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ArcCannonFirePayload> STREAM_CODEC =
            StreamCodec.unit(new ArcCannonFirePayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ArcCannonFirePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ElectricArcCannonItem.tryFireAnyEnabled(player);
            }
        });
    }
}