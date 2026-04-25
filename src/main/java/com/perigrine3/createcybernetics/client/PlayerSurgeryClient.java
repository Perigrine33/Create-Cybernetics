package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.network.payload.PlayerSurgeryEndPayload;
import com.perigrine3.createcybernetics.network.payload.PlayerSurgeryResultPayload;
import com.perigrine3.createcybernetics.network.payload.PlayerSurgeryRoundPayload;
import com.perigrine3.createcybernetics.network.payload.PlayerSurgeryStartPayload;
import com.perigrine3.createcybernetics.screen.custom.PlayerSurgeryMinigameScreen;
import net.minecraft.client.Minecraft;

public final class PlayerSurgeryClient {

    private PlayerSurgeryClient() {
    }

    public static void handleStart(PlayerSurgeryStartPayload payload) {
        Minecraft.getInstance().setScreen(new PlayerSurgeryMinigameScreen(payload));
    }

    public static void handleRound(PlayerSurgeryRoundPayload payload) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.screen instanceof PlayerSurgeryMinigameScreen screen && screen.matches(payload.sessionId())) {
            screen.setRound(payload);
        }
    }

    public static void handleResult(PlayerSurgeryResultPayload payload) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.screen instanceof PlayerSurgeryMinigameScreen screen && screen.matches(payload.sessionId())) {
            screen.setResult(payload);
        }
    }

    public static void handleEnd(PlayerSurgeryEndPayload payload) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.screen instanceof PlayerSurgeryMinigameScreen screen && screen.matches(payload.sessionId())) {
            screen.setEnd(payload);
        }
    }
}