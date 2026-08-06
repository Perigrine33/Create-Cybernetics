package com.perigrine3.createcybernetics.client.render.rejection;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class CyberwareRejectionPostState {

    private static int forcedPulseTicks = 0;
    private static int forcedPulseLength = 1;

    private static int burstTicksLeft = 0;
    private static int burstLength = 1;
    private static int burstCooldownTicks = 0;
    private static int previousRejectionLevel = 0;

    private CyberwareRejectionPostState() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (mc.isPaused()) {
            return;
        }

        tickForcedPulse();

        if (player == null) {
            clearBurst();
            return;
        }

        MobEffectInstance rejection = player.getEffect(ModEffects.CYBERWARE_REJECTION);
        int rejectionLevel = rejection == null ? 0 : Mth.clamp(rejection.getAmplifier() + 1, 1, 3);

        if (rejectionLevel <= 0) {
            clearBurst();
            return;
        }

        if (previousRejectionLevel != rejectionLevel) {
            previousRejectionLevel = rejectionLevel;
            burstTicksLeft = 0;
            burstCooldownTicks = 0;
            burstLength = 1;
        }

        if (player.hasEffect(ModEffects.CYBERPSYCHOSIS_FUGUE)) {
            burstTicksLeft = 20;
            burstLength = 20;
            burstCooldownTicks = 0;
            return;
        }

        if (burstTicksLeft > 0) {
            burstTicksLeft--;
            return;
        }

        if (burstCooldownTicks > 0) {
            burstCooldownTicks--;
            return;
        }

        beginBurst(player, rejectionLevel);
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        clear();
    }

    private static void tickForcedPulse() {
        if (forcedPulseTicks > 0) {
            forcedPulseTicks--;
        }

        if (forcedPulseTicks <= 0) {
            forcedPulseTicks = 0;
            forcedPulseLength = 1;
        }
    }

    private static void beginBurst(LocalPlayer player, int rejectionLevel) {
        int minimumBurst;
        int maximumBurst;
        int minimumCooldown;
        int maximumCooldown;

        if (rejectionLevel == 1) {
            minimumBurst = 18;
            maximumBurst = 35;
            minimumCooldown = 420;
            maximumCooldown = 1200;
        } else if (rejectionLevel == 2) {
            minimumBurst = 35;
            maximumBurst = 65;
            minimumCooldown = 90;
            maximumCooldown = 600;
        } else {
            minimumBurst = 70;
            maximumBurst = 130;
            minimumCooldown = 4;
            maximumCooldown = 50;
        }

        burstTicksLeft = Mth.nextInt(player.getRandom(), minimumBurst, maximumBurst);
        burstLength = burstTicksLeft;
        burstCooldownTicks = Mth.nextInt(player.getRandom(), minimumCooldown, maximumCooldown);
    }

    public static void triggerForcedPulse(int duration) {
        int pulseDuration = Math.max(1, duration);

        if (pulseDuration >= forcedPulseTicks) {
            forcedPulseTicks = pulseDuration;
            forcedPulseLength = pulseDuration;
        }
    }

    public static float getForcedPulse(float partialTick) {
        if (forcedPulseTicks <= 0) {
            return 0.0F;
        }

        float remaining = Math.max(0.0F, forcedPulseTicks - partialTick);
        float progress = 1.0F - remaining / Math.max(1.0F, forcedPulseLength);
        float envelope = 1.0F - progress;

        return Mth.clamp(envelope * envelope, 0.0F, 1.0F);
    }

    public static float getBurstStrength(float partialTick) {
        if (burstTicksLeft <= 0) {
            return 0.0F;
        }

        float elapsed = Math.max(0.0F, burstLength - burstTicksLeft + partialTick);
        float progress = Mth.clamp(elapsed / Math.max(1.0F, burstLength), 0.0F, 1.0F);

        float fadeIn = Mth.clamp(progress / 0.12F, 0.0F, 1.0F);
        float fadeOut = Mth.clamp((1.0F - progress) / 0.18F, 0.0F, 1.0F);

        return smoothstep(Math.min(fadeIn, fadeOut));
    }

    public static float getScreenIntensity(LocalPlayer player) {
        if (player == null) {
            return 0.0F;
        }

        MobEffectInstance rejection = player.getEffect(ModEffects.CYBERWARE_REJECTION);
        if (rejection == null) {
            return 0.0F;
        }

        int level = Mth.clamp(rejection.getAmplifier() + 1, 1, 3);

        return switch (level) {
            case 1 -> 0.38F;
            case 2 -> 0.70F;
            default -> 1.0F;
        };
    }

    public static float getFugueIntensity(LocalPlayer player) {
        if (player == null || !player.hasEffect(ModEffects.CYBERPSYCHOSIS_FUGUE)) {
            return 0.0F;
        }

        MobEffectInstance fugue = player.getEffect(ModEffects.CYBERPSYCHOSIS_FUGUE);
        if (fugue == null) {
            return 0.0F;
        }

        return switch (Mth.clamp(fugue.getAmplifier(), 0, 2)) {
            case 0 -> 0.82F;
            case 1 -> 0.94F;
            default -> 1.0F;
        };
    }

    private static float smoothstep(float value) {
        value = Mth.clamp(value, 0.0F, 1.0F);
        return value * value * (3.0F - 2.0F * value);
    }

    private static void clearBurst() {
        burstTicksLeft = 0;
        burstLength = 1;
        burstCooldownTicks = 0;
        previousRejectionLevel = 0;
    }

    public static void clear() {
        forcedPulseTicks = 0;
        forcedPulseLength = 1;

        clearBurst();
    }
}