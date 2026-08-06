package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.UUID;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class CyberpsychosisAmbientScareSound {

    private static final int PRESSURE_ROLL_INTERVAL_TICKS = 20;

    private static final int LEVEL_1_MIN_QUIET_TICKS = 5 * 60 * 20;
    private static final int LEVEL_1_MAX_QUIET_TICKS = 8 * 60 * 20;
    private static final int LEVEL_1_FORCE_MIN_TICKS = 12 * 60 * 20;
    private static final int LEVEL_1_FORCE_MAX_TICKS = 18 * 60 * 20;

    private static final int LEVEL_2_MIN_QUIET_TICKS = 3 * 60 * 20;
    private static final int LEVEL_2_MAX_QUIET_TICKS = 5 * 60 * 20;
    private static final int LEVEL_2_FORCE_MIN_TICKS = 8 * 60 * 20;
    private static final int LEVEL_2_FORCE_MAX_TICKS = 12 * 60 * 20;

    private static final int LEVEL_3_MIN_QUIET_TICKS = 2 * 60 * 20;
    private static final int LEVEL_3_MAX_QUIET_TICKS = 3 * 60 * 20;
    private static final int LEVEL_3_FORCE_MIN_TICKS = 5 * 60 * 20;
    private static final int LEVEL_3_FORCE_MAX_TICKS = 8 * 60 * 20;

    private static final float LEVEL_1_BASE_CHANCE = 0.0040F;
    private static final float LEVEL_2_BASE_CHANCE = 0.0075F;
    private static final float LEVEL_3_BASE_CHANCE = 0.0125F;

    private static final int STATIONARY_THRESHOLD_TICKS = 5 * 20;

    private static UUID trackedPlayerId;
    private static int trackedSeverity;
    private static int nextEligibleTick = -1;
    private static int forcedSoundTick = -1;
    private static int stationaryTicks;
    private static int recentlyHurtTicks;

    private CyberpsychosisAmbientScareSound() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        if (mc.isPaused()) {
            return;
        }

        if (trackedPlayerId == null || !trackedPlayerId.equals(player.getUUID())) {
            reset();
            trackedPlayerId = player.getUUID();
        }

        MobEffectInstance rejection = getEffect(player, ModEffects.CYBERWARE_REJECTION);
        if (rejection == null || !player.isAlive()) {
            resetSoundCycle();
            return;
        }

        int severity = Mth.clamp(rejection.getAmplifier() + 1, 1, 3);

        updatePlayerState(player);

        if (trackedSeverity != severity || nextEligibleTick < 0 || forcedSoundTick < 0) {
            trackedSeverity = severity;
            scheduleNextSound(player, severity);
            return;
        }

        int now = player.tickCount;

        if (now < nextEligibleTick) {
            return;
        }

        if (now >= forcedSoundTick) {
            playScare(player, severity);
            scheduleNextSound(player, severity);
            return;
        }

        if (now % PRESSURE_ROLL_INTERVAL_TICKS != 0) {
            return;
        }

        float chance = getBaseChance(severity);
        chance *= getEnvironmentalMultiplier(mc, player, severity);
        chance *= getTimePressureMultiplier(now);

        if (player.getRandom().nextFloat() < Mth.clamp(chance, 0.0F, 0.20F)) {
            playScare(player, severity);
            scheduleNextSound(player, severity);
        }
    }

    private static void updatePlayerState(LocalPlayer player) {
        Vec3 movement = player.getDeltaMovement();
        double horizontalSpeedSqr = movement.x * movement.x + movement.z * movement.z;

        if (horizontalSpeedSqr < 0.0004D) {
            stationaryTicks++;
        } else {
            stationaryTicks = 0;
        }

        if (player.hurtTime > 0) {
            recentlyHurtTicks = 5 * 20;
        } else if (recentlyHurtTicks > 0) {
            recentlyHurtTicks--;
        }
    }

    private static float getEnvironmentalMultiplier(Minecraft mc, LocalPlayer player, int severity) {
        BlockPos playerPos = player.blockPosition();
        int localBrightness = player.level().getMaxLocalRawBrightness(playerPos);
        boolean canSeeSky = player.level().canSeeSky(playerPos);

        float multiplier = 1.0F;

        if (localBrightness <= 2) {
            multiplier *= 2.15F;
        } else if (localBrightness <= 5) {
            multiplier *= 1.65F;
        } else if (localBrightness <= 8) {
            multiplier *= 1.25F;
        }

        if (!canSeeSky) {
            multiplier *= 1.35F;
        }

        if (player.getY() < player.level().getSeaLevel() - 12) {
            multiplier *= 1.30F;
        }

        float healthPercent = player.getHealth() / Math.max(1.0F, player.getMaxHealth());

        if (healthPercent <= 0.25F) {
            multiplier *= 1.80F;
        } else if (healthPercent <= 0.50F) {
            multiplier *= 1.35F;
        }

        if (player.getAirSupply() < player.getMaxAirSupply() / 3) {
            multiplier *= 1.50F;
        }

        if (recentlyHurtTicks > 0 && player.hurtTime == 0) {
            multiplier *= 1.35F;
        }

        if (stationaryTicks >= STATIONARY_THRESHOLD_TICKS) {
            multiplier *= 1.35F;
        }

        if (mc.screen != null) {
            multiplier *= 1.40F;
        }

        if (player.isSleeping()) {
            multiplier *= 1.60F;
        }

        if (severity >= 3 && player.isSprinting()) {
            multiplier *= 1.20F;
        }

        return multiplier;
    }

    private static float getTimePressureMultiplier(int now) {
        if (nextEligibleTick < 0 || forcedSoundTick <= nextEligibleTick) {
            return 1.0F;
        }

        float progress = (now - nextEligibleTick) / (float) (forcedSoundTick - nextEligibleTick);
        progress = Mth.clamp(progress, 0.0F, 1.0F);

        return 1.0F + progress * progress * 3.0F;
    }

    private static void playScare(LocalPlayer player, int severity) {
        Vec3 soundPosition = getSoundPosition(player, severity);

        float volume = switch (severity) {
            case 1 -> 0.70F;
            case 2 -> 0.88F;
            default -> 1.0F;
        };

        float pitch = switch (severity) {
            case 1 -> 0.97F + player.getRandom().nextFloat() * 0.06F;
            case 2 -> 0.94F + player.getRandom().nextFloat() * 0.12F;
            default -> 0.90F + player.getRandom().nextFloat() * 0.20F;
        };

        player.level().playLocalSound(
                soundPosition.x,
                soundPosition.y,
                soundPosition.z,
                ModSounds.CYBERPSYCHOSIS_SCARES.get(),
                SoundSource.AMBIENT,
                volume,
                pitch,
                false
        );
    }

    private static Vec3 getSoundPosition(LocalPlayer player, int severity) {
        Vec3 look = player.getLookAngle();
        Vec3 horizontalLook = new Vec3(look.x, 0.0D, look.z);

        if (horizontalLook.lengthSqr() < 0.0001D) {
            float yawRadians = player.getYRot() * Mth.DEG_TO_RAD;
            horizontalLook = new Vec3(-Mth.sin(yawRadians), 0.0D, Mth.cos(yawRadians));
        } else {
            horizontalLook = horizontalLook.normalize();
        }

        Vec3 backward = horizontalLook.scale(-1.0D);
        Vec3 right = new Vec3(-horizontalLook.z, 0.0D, horizontalLook.x);

        double minDistance;
        double maxDistance;

        if (severity <= 1) {
            minDistance = 7.0D;
            maxDistance = 13.0D;
        } else if (severity == 2) {
            minDistance = 4.5D;
            maxDistance = 9.0D;
        } else {
            minDistance = 2.5D;
            maxDistance = 7.0D;
        }

        double distance = Mth.lerp(player.getRandom().nextDouble(), minDistance, maxDistance);
        double sideOffset = (player.getRandom().nextDouble() * 2.0D - 1.0D) * distance * 0.55D;
        double verticalOffset = player.getRandom().nextDouble() * 2.5D - 0.75D;

        Vec3 primaryDirection;

        if (player.getRandom().nextFloat() < 0.82F) {
            primaryDirection = backward;
        } else {
            double angle = (player.getRandom().nextDouble() * 2.0D - 1.0D) * Math.PI;
            primaryDirection = horizontalLook.yRot((float) angle);
        }

        return player.position()
                .add(0.0D, player.getEyeHeight() * 0.75D, 0.0D)
                .add(primaryDirection.scale(distance))
                .add(right.scale(sideOffset))
                .add(0.0D, verticalOffset, 0.0D);
    }

    private static void scheduleNextSound(LocalPlayer player, int severity) {
        int now = player.tickCount;

        int minimumQuietTicks;
        int maximumQuietTicks;
        int forceMinimumTicks;
        int forceMaximumTicks;

        if (severity <= 1) {
            minimumQuietTicks = LEVEL_1_MIN_QUIET_TICKS;
            maximumQuietTicks = LEVEL_1_MAX_QUIET_TICKS;
            forceMinimumTicks = LEVEL_1_FORCE_MIN_TICKS;
            forceMaximumTicks = LEVEL_1_FORCE_MAX_TICKS;
        } else if (severity == 2) {
            minimumQuietTicks = LEVEL_2_MIN_QUIET_TICKS;
            maximumQuietTicks = LEVEL_2_MAX_QUIET_TICKS;
            forceMinimumTicks = LEVEL_2_FORCE_MIN_TICKS;
            forceMaximumTicks = LEVEL_2_FORCE_MAX_TICKS;
        } else {
            minimumQuietTicks = LEVEL_3_MIN_QUIET_TICKS;
            maximumQuietTicks = LEVEL_3_MAX_QUIET_TICKS;
            forceMinimumTicks = LEVEL_3_FORCE_MIN_TICKS;
            forceMaximumTicks = LEVEL_3_FORCE_MAX_TICKS;
        }

        nextEligibleTick = now + Mth.nextInt(player.getRandom(), minimumQuietTicks, maximumQuietTicks);
        forcedSoundTick = now + Mth.nextInt(player.getRandom(), forceMinimumTicks, forceMaximumTicks);

        if (forcedSoundTick <= nextEligibleTick) {
            forcedSoundTick = nextEligibleTick + 60 * 20;
        }
    }

    private static float getBaseChance(int severity) {
        return switch (severity) {
            case 1 -> LEVEL_1_BASE_CHANCE;
            case 2 -> LEVEL_2_BASE_CHANCE;
            default -> LEVEL_3_BASE_CHANCE;
        };
    }

    private static MobEffectInstance getEffect(LocalPlayer player, Holder<MobEffect> effect) {
        for (MobEffectInstance instance : player.getActiveEffects()) {
            if (instance != null && instance.is(effect)) {
                return instance;
            }
        }

        return null;
    }

    private static void reset() {
        trackedPlayerId = null;
        resetSoundCycle();
    }

    private static void resetSoundCycle() {
        trackedSeverity = 0;
        nextEligibleTick = -1;
        forcedSoundTick = -1;
        stationaryTicks = 0;
        recentlyHurtTicks = 0;
    }
}
