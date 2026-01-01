package com.perigrine3.createcybernetics.network.handler;

import com.perigrine3.createcybernetics.item.cyberware.ArmCannonItem;
import com.perigrine3.createcybernetics.network.payload.ArmCannonFirePayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ArmCannonFireHandler {
    private ArmCannonFireHandler() {}

    public static void handle(ArmCannonFirePayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sp)) return;
            ArmCannonItem.fireLoaded(sp);
        });
    }
}
