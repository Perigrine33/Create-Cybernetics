package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.item.cyberware.MetalDetectorItem;
import com.perigrine3.createcybernetics.sound.MetalDetectorLoopSound;
import com.perigrine3.createcybernetics.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class MetalDetectorClientTicker {
    private static MetalDetectorLoopSound activeLoop;

    private MetalDetectorClientTicker() {}

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        Minecraft mc = Minecraft.getInstance();
        Level level = player.level();

        if (!MetalDetectorItem.isAnyMetalDetectorPowered(player)) {
            stop(mc);
            return;
        }

        MetalDetectorItem.DetectionResult r = MetalDetectorItem.scanForMetal(level, player);
        if (!r.detected()) {
            stop(mc);
            return;
        }

        startIfNeeded(mc, player);

        float maxVol = 1.0F;
        float minVol = 0.2F;
        float t = 1.0F - (r.dy() / 15.0F);
        float volume = minVol + (maxVol - minVol) * t;
        if (!r.direct()) volume *= 0.5F;

        if (activeLoop != null) activeLoop.setTargetVolume(volume);
    }

    private static void startIfNeeded(Minecraft mc, LocalPlayer player) {
        if (mc.getSoundManager() == null) return;
        if (activeLoop != null && mc.getSoundManager().isActive(activeLoop)) return;

        activeLoop = new MetalDetectorLoopSound(player, ModSounds.METAL_DETECTOR_BEEPS.get());
        mc.getSoundManager().play(activeLoop);
    }

    private static void stop(Minecraft mc) {
        if (mc.getSoundManager() == null) return;
        if (activeLoop != null) {
            mc.getSoundManager().stop(activeLoop);
            activeLoop = null;
        }
    }
}
