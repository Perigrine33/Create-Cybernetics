package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.screen.custom.TattooMenu;
import com.perigrine3.createcybernetics.tattoo.TattooApplicationService;
import com.perigrine3.createcybernetics.tattoo.TattooLayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TattooApplyC2SPayload(
        ResourceLocation tattooId,
        TattooLayer layer
) implements CustomPacketPayload {
    public static final Type<TattooApplyC2SPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "tattoo_apply_c2s"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TattooApplyC2SPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeResourceLocation(payload.tattooId());
                        buf.writeEnum(payload.layer());
                    },
                    buf -> new TattooApplyC2SPayload(
                            buf.readResourceLocation(),
                            buf.readEnum(TattooLayer.class)
                    )
            );

    public TattooApplyC2SPayload {
        layer = layer == null ? TattooLayer.UNDER_CYBERWARE : layer;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TattooApplyC2SPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            if (!(player.containerMenu instanceof TattooMenu menu)) {
                player.sendSystemMessage(Component.literal("Tattoo menu is not open."));
                return;
            }

            TattooApplicationService.ApplyResult result = TattooApplicationService.apply(
                    player,
                    menu,
                    payload.tattooId(),
                    payload.layer()
            );

            player.sendSystemMessage(result.message());
        });
    }
}