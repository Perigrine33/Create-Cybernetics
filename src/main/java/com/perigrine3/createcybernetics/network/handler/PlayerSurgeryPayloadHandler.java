package com.perigrine3.createcybernetics.network.handler;

import com.perigrine3.createcybernetics.client.PlayerSurgeryClient;
import com.perigrine3.createcybernetics.common.surgery.PlayerSurgeryMinigameManager;
import com.perigrine3.createcybernetics.network.payload.PlayerSurgeryCancelPayload;
import com.perigrine3.createcybernetics.network.payload.PlayerSurgeryClickPayload;
import com.perigrine3.createcybernetics.network.payload.PlayerSurgeryEndPayload;
import com.perigrine3.createcybernetics.network.payload.PlayerSurgeryResultPayload;
import com.perigrine3.createcybernetics.network.payload.PlayerSurgeryRoundPayload;
import com.perigrine3.createcybernetics.network.payload.PlayerSurgeryStartPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class PlayerSurgeryPayloadHandler {

    private PlayerSurgeryPayloadHandler() {
    }

    public static void handleClick(PlayerSurgeryClickPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                PlayerSurgeryMinigameManager.handleClick(serverPlayer, payload.sessionId());
            }
        });
    }

    public static void handleCancel(PlayerSurgeryCancelPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                PlayerSurgeryMinigameManager.handleCancel(serverPlayer, payload.sessionId());
            }
        });
    }

    public static void handleStart(PlayerSurgeryStartPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                PlayerSurgeryClient.handleStart(payload);
            }
        });
    }

    public static void handleRound(PlayerSurgeryRoundPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                PlayerSurgeryClient.handleRound(payload);
            }
        });
    }

    public static void handleResult(PlayerSurgeryResultPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                PlayerSurgeryClient.handleResult(payload);
            }
        });
    }

    public static void handleEnd(PlayerSurgeryEndPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                PlayerSurgeryClient.handleEnd(payload);
            }
        });
    }
}