package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class ScrambleQuickhackMovementHandler {
    private ScrambleQuickhackMovementHandler() {
    }

    @SubscribeEvent
    public static void onMovementInputUpdate(MovementInputUpdateEvent event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) {
            return;
        }

        if (!player.hasEffect(ModEffects.SCRAMBLE_HACK)) {
            return;
        }

        Input input = player.input;
        if (input == null) {
            return;
        }

        float forward = input.forwardImpulse;
        float sideways = input.leftImpulse;

        if (forward == 0.0F && sideways == 0.0F) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }

        double angle = mc.level.getGameTime() * 0.35D;
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);

        float rotatedForward = (forward * cos) - (sideways * sin);
        float rotatedSideways = (forward * sin) + (sideways * cos);

        input.forwardImpulse = rotatedForward;
        input.leftImpulse = rotatedSideways;
    }
}