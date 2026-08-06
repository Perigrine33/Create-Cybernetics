package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;

import java.util.Comparator;
import java.util.List;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class CyberpsychosisInputSuppressor {

    private static final double TARGET_SEARCH_RADIUS = 24.0D;
    private static final double TARGET_ABOVE_JUMP_THRESHOLD = 0.55D;
    private static final double TARGET_JUMP_MAX_DISTANCE = 8.0D;

    private static final int WANDER_RESELECT_MIN = 35;
    private static final int WANDER_RESELECT_MAX = 90;

    private static LivingEntity currentTarget = null;
    private static float wanderYaw = 0.0F;
    private static int wanderTicks = 0;
    private static boolean forcedJump = false;
    private static boolean wasInFugue = false;

    private CyberpsychosisInputSuppressor() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null || mc.level == null) {
            resetState();
            return;
        }

        boolean inFugue = hasEffect(player, ModEffects.CYBERPSYCHOSIS_FUGUE);

        if (!inFugue) {
            if (wasInFugue) resetState();

            wasInFugue = false;
            return;
        }

        wasInFugue = true;

        if (isIntegratedServerPaused(mc)) {
            forcedJump = false;
            return;
        }

        currentTarget = findTarget(player);

        if (isValidTarget(player, currentTarget)) {
            aimAt(player, currentTarget.position().add(0.0D, currentTarget.getBbHeight() * 0.65D, 0.0D), 16.0F, 12.0F);
            forcedJump = shouldAutoJump(player, currentTarget);
        } else {
            tickWander(player);
            forcedJump = shouldAutoJump(player, null);
        }

        player.setSprinting(true);
    }

    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null) return;
        if (!hasEffect(player, ModEffects.CYBERPSYCHOSIS_FUGUE)) return;
        if (isIntegratedServerPaused(mc)) return;

        event.getInput().leftImpulse = 0.0F;
        event.getInput().forwardImpulse = 1.0F;
        event.getInput().jumping = forcedJump;
        event.getInput().shiftKeyDown = false;

        event.getInput().up = true;
        event.getInput().down = false;
        event.getInput().left = false;
        event.getInput().right = false;

        player.setSprinting(true);
    }

    private static boolean isIntegratedServerPaused(Minecraft mc) {
        return mc.hasSingleplayerServer() && mc.isPaused();
    }

    private static void tickWander(LocalPlayer player) {
        if (wanderTicks <= 0) {
            wanderYaw = player.getRandom().nextFloat() * 360.0F;
            wanderTicks = Mth.nextInt(player.getRandom(), WANDER_RESELECT_MIN, WANDER_RESELECT_MAX);
        }

        wanderTicks--;

        float yaw = rotateToward(player.getYRot(), wanderYaw, 8.0F);

        player.setYRot(yaw);
        player.yHeadRot = yaw;
        player.yBodyRot = yaw;
        player.yHeadRotO = yaw;
        player.yBodyRotO = yaw;
    }

    private static LivingEntity findTarget(LocalPlayer player) {
        if (player.level() == null) return null;

        List<LivingEntity> candidates = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(TARGET_SEARCH_RADIUS), entity -> isCandidateTarget(player, entity));

        if (candidates.isEmpty()) return null;

        return candidates.stream().min(Comparator.comparingInt(CyberpsychosisInputSuppressor::targetPriority).thenComparingDouble(player::distanceToSqr)).orElse(null);
    }

    private static boolean isCandidateTarget(LocalPlayer player, LivingEntity entity) {
        if (entity == player) return false;
        if (!entity.isAlive()) return false;
        if (!EntitySelector.NO_SPECTATORS.test(entity)) return false;

        if (entity instanceof Player otherPlayer) return !otherPlayer.isCreative() && !otherPlayer.isSpectator();

        return player.distanceToSqr(entity) <= TARGET_SEARCH_RADIUS * TARGET_SEARCH_RADIUS;
    }

    private static boolean isValidTarget(LocalPlayer player, LivingEntity target) {
        if (target == null || !target.isAlive()) return false;
        if (target == player) return false;
        if (target.level() != player.level()) return false;

        if (target instanceof Player otherPlayer && (otherPlayer.isCreative() || otherPlayer.isSpectator())) return false;

        return player.distanceToSqr(target) <= TARGET_SEARCH_RADIUS * TARGET_SEARCH_RADIUS;
    }

    private static int targetPriority(LivingEntity entity) {
        if (entity instanceof Player) return 0;
        if (entity instanceof AbstractVillager) return 1;
        if (entity instanceof Animal) return 2;
        if (entity instanceof Enemy) return 3;

        return 4;
    }

    private static boolean shouldAutoJump(LocalPlayer player, LivingEntity target) {
        if (!player.onGround()) return false;
        if (player.horizontalCollision) return true;

        Vec3 movement = player.getDeltaMovement();
        double horizontalMovementSqr = movement.x * movement.x + movement.z * movement.z;

        if (horizontalMovementSqr < 0.0004D && player.zza > 0.0F) return true;
        if (target == null) return false;

        double targetDeltaY = target.getY() - player.getY();
        if (targetDeltaY < TARGET_ABOVE_JUMP_THRESHOLD) return false;

        return horizontalDistanceSqr(player.position(), target.position()) <= TARGET_JUMP_MAX_DISTANCE * TARGET_JUMP_MAX_DISTANCE;
    }

    private static double horizontalDistanceSqr(Vec3 a, Vec3 b) {
        double dx = a.x - b.x;
        double dz = a.z - b.z;

        return dx * dx + dz * dz;
    }

    private static void aimAt(LocalPlayer player, Vec3 targetPos, float maxYawChange, float maxPitchChange) {
        Vec3 eye = player.getEyePosition();
        Vec3 delta = targetPos.subtract(eye);

        double horizontal = Math.sqrt(delta.x * delta.x + delta.z * delta.z);

        float wantedYaw = (float) (Mth.atan2(delta.z, delta.x) * (180.0D / Math.PI)) - 90.0F;
        float wantedPitch = (float) -(Mth.atan2(delta.y, horizontal) * (180.0D / Math.PI));

        float yaw = rotateToward(player.getYRot(), wantedYaw, maxYawChange);
        float pitch = rotateToward(player.getXRot(), wantedPitch, maxPitchChange);

        pitch = Mth.clamp(pitch, -89.0F, 89.0F);

        player.setYRot(yaw);
        player.setXRot(pitch);

        player.yHeadRot = yaw;
        player.yBodyRot = yaw;
        player.yHeadRotO = yaw;
        player.yBodyRotO = yaw;
    }

    private static float rotateToward(float current, float target, float maxChange) {
        float delta = Mth.wrapDegrees(target - current);
        delta = Mth.clamp(delta, -maxChange, maxChange);

        return current + delta;
    }

    private static boolean hasEffect(LocalPlayer player, Holder<MobEffect> effect) {
        for (MobEffectInstance instance : player.getActiveEffects()) {
            if (instance != null && instance.is(effect)) return true;
        }

        return false;
    }

    private static void resetState() {
        currentTarget = null;
        wanderYaw = 0.0F;
        wanderTicks = 0;
        forcedJump = false;
    }
}