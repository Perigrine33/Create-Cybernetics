package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.common.damage.ModDamageSources;
import com.perigrine3.createcybernetics.common.toggle.CyberwareToggleController;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class CyberpsychosisFugueController {

    private static final Map<UUID, FugueState> STATES = new HashMap<>();

    private static final double TARGET_SEARCH_RADIUS = 24.0D;
    private static final double TARGET_KEEP_RADIUS = 32.0D;

    private static final double ATTACK_RANGE = 3.15D;
    private static final int ATTACK_COOLDOWN_TICKS = 12;

    private static final int RETARGET_INTERVAL_TICKS = 10;
    private static final int FUGUE_SYNC_DURATION_TICKS = 12;

    private static final int MAX_BLOCKED_TARGET_TICKS = 30;
    private static final int STUCK_CHECK_INTERVAL_TICKS = 20;
    private static final int MAX_STUCK_CHECKS = 2;

    private static final double MIN_STUCK_MOVEMENT_SQR = 0.25D;
    private static final double CHASE_SPEED = 0.18D;
    private static final double CHASE_ACCELERATION = 0.22D;
    private static final double JUMP_VELOCITY = 0.42D;

    private CyberpsychosisFugueController() {
    }

    public static boolean isInFugue(Player player) {
        if (player == null) {
            return false;
        }

        FugueState state = STATES.get(player.getUUID());
        return state != null && state.inFugue;
    }

    public static void clear(Player player) {
        if (player == null) {
            return;
        }

        FugueState removed = STATES.remove(player.getUUID());

        if (removed != null) {
            player.setSprinting(false);
        }

        player.removeEffect(ModEffects.CYBERPSYCHOSIS_FUGUE);
    }

    public static void tick(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (serverPlayer.isCreative() || serverPlayer.isSpectator() || !serverPlayer.isAlive()) {
            clear(serverPlayer);
            return;
        }

        CyberpsychosisSeverity severity = CyberpsychosisSeverity.fromPlayer(serverPlayer);
        if (severity != CyberpsychosisSeverity.LEVEL_3) {
            clear(serverPlayer);
            return;
        }

        float negativeProgress = CyberpsychosisSeverity.getNegativeProgress(serverPlayer);
        FugueState state = STATES.computeIfAbsent(serverPlayer.getUUID(), id -> new FugueState());

        state.tick(serverPlayer, negativeProgress);

        if (!state.inFugue) {
            serverPlayer.setSprinting(false);
            serverPlayer.removeEffect(ModEffects.CYBERPSYCHOSIS_FUGUE);
            return;
        }

        CyberwareToggleController.forceAllToggleablesActiveForFugue(serverPlayer);

        syncFugueEffect(serverPlayer, negativeProgress);
        forceCloseScreens(serverPlayer);

        if (serverPlayer.tickCount % RETARGET_INTERVAL_TICKS == 0 || !isValidTarget(serverPlayer, state.target)) {
            state.setTarget(findTarget(serverPlayer), serverPlayer.position());
        }

        if (isValidTarget(serverPlayer, state.target)) {
            tickAttackTarget(serverPlayer, state, state.target);
        } else {
            serverPlayer.setSprinting(false);
            state.setTarget(null, serverPlayer.position());
        }
    }

    private static void syncFugueEffect(ServerPlayer player, float negativeProgress) {
        int amplifier = negativeProgress >= 0.66F ? 2 : negativeProgress >= 0.33F ? 1 : 0;

        MobEffectInstance existing = player.getEffect(ModEffects.CYBERPSYCHOSIS_FUGUE);
        if (existing != null && existing.getDuration() > 5 && existing.getAmplifier() == amplifier) {
            return;
        }

        player.addEffect(new MobEffectInstance(ModEffects.CYBERPSYCHOSIS_FUGUE, FUGUE_SYNC_DURATION_TICKS, amplifier, false, false, false));
    }

    private static void forceCloseScreens(ServerPlayer player) {
        if (player.containerMenu != player.inventoryMenu) {
            player.closeContainer();
        }
    }

    private static void tickAttackTarget(ServerPlayer player, FugueState state, LivingEntity target) {
        Vec3 targetLookPos = getTargetLookPosition(target);
        lookAt(player, targetLookPos, 35.0F, 22.0F);

        double distanceSqr = player.distanceToSqr(target);
        double attackRangeSqr = ATTACK_RANGE * ATTACK_RANGE;
        boolean clearLine = hasClearLine(player, targetLookPos);

        if (!clearLine) {
            state.blockedTargetTicks++;
            player.setSprinting(false);

            if (state.blockedTargetTicks >= MAX_BLOCKED_TARGET_TICKS) {
                state.setTarget(null, player.position());
            }

            tickAttackCooldown(state);
            return;
        }

        state.blockedTargetTicks = 0;

        if (distanceSqr > attackRangeSqr) {
            player.setSprinting(true);
            moveTowardTarget(player, target);
            checkStuck(player, state);
        } else {
            player.setSprinting(false);
            state.stuckChecks = 0;
            state.lastStuckCheckPosition = player.position();
        }

        if (distanceSqr <= attackRangeSqr && state.attackCooldown <= 0 && canAttackTarget(player, target)) {
            forceAttack(player, target);
            state.attackCooldown = ATTACK_COOLDOWN_TICKS;
        }

        tickAttackCooldown(state);
    }

    private static void moveTowardTarget(ServerPlayer player, LivingEntity target) {
        Vec3 horizontalDirection = new Vec3(target.getX() - player.getX(), 0.0D, target.getZ() - player.getZ());

        if (horizontalDirection.lengthSqr() <= 0.0001D) {
            return;
        }

        horizontalDirection = horizontalDirection.normalize();

        Vec3 currentMovement = player.getDeltaMovement();
        double wantedX = horizontalDirection.x * CHASE_SPEED;
        double wantedZ = horizontalDirection.z * CHASE_SPEED;

        double movementX = Mth.lerp(CHASE_ACCELERATION, currentMovement.x, wantedX);
        double movementZ = Mth.lerp(CHASE_ACCELERATION, currentMovement.z, wantedZ);
        double movementY = currentMovement.y;

        if (player.horizontalCollision && player.onGround()) {
            movementY = Math.max(movementY, JUMP_VELOCITY);
        }

        player.setDeltaMovement(movementX, movementY, movementZ);
        player.hurtMarked = true;
    }

    private static void checkStuck(ServerPlayer player, FugueState state) {
        if (player.tickCount % STUCK_CHECK_INTERVAL_TICKS != 0) {
            return;
        }

        double movedSqr = player.position().distanceToSqr(state.lastStuckCheckPosition);
        state.lastStuckCheckPosition = player.position();

        if (movedSqr >= MIN_STUCK_MOVEMENT_SQR) {
            state.stuckChecks = 0;
            return;
        }

        state.stuckChecks++;

        if (state.stuckChecks >= MAX_STUCK_CHECKS) {
            state.setTarget(null, player.position());
        }
    }

    private static void tickAttackCooldown(FugueState state) {
        if (state.attackCooldown > 0) {
            state.attackCooldown--;
        }
    }

    private static boolean canAttackTarget(ServerPlayer player, LivingEntity target) {
        if (!isValidTarget(player, target)) {
            return false;
        }

        if (player.distanceToSqr(target) > ATTACK_RANGE * ATTACK_RANGE) {
            return false;
        }

        Vec3 targetLookPos = getTargetLookPosition(target);

        return player.hasLineOfSight(target) && hasClearLine(player, targetLookPos);
    }

    private static boolean hasClearLine(ServerPlayer player, Vec3 targetPos) {
        HitResult result = player.level().clip(new ClipContext(player.getEyePosition(), targetPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        return result.getType() == HitResult.Type.MISS;
    }

    private static Vec3 getTargetLookPosition(LivingEntity target) {
        return target.position().add(0.0D, target.getBbHeight() * 0.65D, 0.0D);
    }

    private static void forceAttack(ServerPlayer player, LivingEntity target) {
        if (!target.isAlive()) {
            return;
        }

        player.swing(InteractionHand.MAIN_HAND, true);
        player.attack(target);
        player.resetAttackStrengthTicker();

        if (target.isAlive() && player.getRandom().nextFloat() < 0.10F) {
            target.hurt(ModDamageSources.cyberwareRejection(player.level(), player, null), 1.0F);
        }
    }

    private static LivingEntity findTarget(ServerPlayer player) {
        Level level = player.level();

        List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(TARGET_SEARCH_RADIUS), entity -> isCandidateTarget(player, entity));

        if (candidates.isEmpty()) {
            return null;
        }

        return candidates.stream().min(Comparator.comparingInt(CyberpsychosisFugueController::targetPriority).thenComparingDouble(player::distanceToSqr)).orElse(null);
    }

    private static boolean isCandidateTarget(ServerPlayer player, LivingEntity entity) {
        if (entity == player) {
            return false;
        }

        if (!entity.isAlive()) {
            return false;
        }

        if (!EntitySelector.NO_SPECTATORS.test(entity)) {
            return false;
        }

        if (entity instanceof Player otherPlayer && (otherPlayer.isCreative() || otherPlayer.isSpectator())) {
            return false;
        }

        if (player.distanceToSqr(entity) > TARGET_SEARCH_RADIUS * TARGET_SEARCH_RADIUS) {
            return false;
        }

        return player.hasLineOfSight(entity) && hasClearLine(player, getTargetLookPosition(entity));
    }

    private static boolean isValidTarget(ServerPlayer player, LivingEntity target) {
        if (target == null || !target.isAlive()) {
            return false;
        }

        if (target == player) {
            return false;
        }

        if (target.level() != player.level()) {
            return false;
        }

        if (!EntitySelector.NO_SPECTATORS.test(target)) {
            return false;
        }

        if (target instanceof Player otherPlayer && (otherPlayer.isCreative() || otherPlayer.isSpectator())) {
            return false;
        }

        return player.distanceToSqr(target) <= TARGET_KEEP_RADIUS * TARGET_KEEP_RADIUS;
    }

    private static int targetPriority(LivingEntity entity) {
        if (entity instanceof Player) {
            return 0;
        }

        if (entity instanceof AbstractVillager) {
            return 1;
        }

        if (entity instanceof Animal) {
            return 2;
        }

        if (entity instanceof Enemy) {
            return 3;
        }

        return 4;
    }

    private static void lookAt(ServerPlayer player, Vec3 targetPos, float maxYawChange, float maxPitchChange) {
        Vec3 eye = player.getEyePosition();
        Vec3 delta = targetPos.subtract(eye);

        double horizontal = Math.sqrt(delta.x * delta.x + delta.z * delta.z);

        float wantedYaw = (float) (Mth.atan2(delta.z, delta.x) * (180.0D / Math.PI)) - 90.0F;
        float wantedPitch = (float) (-(Mth.atan2(delta.y, horizontal) * (180.0D / Math.PI)));

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

    private static final class FugueState {
        private boolean inFugue = false;

        private int fugueTicksLeft = 0;
        private int controlTicksLeft = 0;

        private int attackCooldown = 0;
        private int blockedTargetTicks = 0;
        private int stuckChecks = 0;

        private Vec3 lastStuckCheckPosition = Vec3.ZERO;
        private LivingEntity target = null;

        private void tick(ServerPlayer player, float negativeProgress) {
            if (inFugue) {
                fugueTicksLeft--;

                if (fugueTicksLeft <= 0) {
                    inFugue = false;
                    setTarget(null, player.position());
                    attackCooldown = 0;
                    controlTicksLeft = rollControlTicks(player, negativeProgress);
                }

                return;
            }

            if (controlTicksLeft > 0) {
                controlTicksLeft--;
                return;
            }

            inFugue = true;
            fugueTicksLeft = rollFugueTicks(player, negativeProgress);
            setTarget(null, player.position());
            attackCooldown = 0;
        }

        private void setTarget(LivingEntity target, Vec3 playerPosition) {
            this.target = target;
            blockedTargetTicks = 0;
            stuckChecks = 0;
            lastStuckCheckPosition = playerPosition;
        }

        private static int rollFugueTicks(ServerPlayer player, float negativeProgress) {
            int min = Mth.floor(Mth.lerp(negativeProgress, 80.0F, 240.0F));
            int max = Mth.floor(Mth.lerp(negativeProgress, 160.0F, 500.0F));
            return Mth.nextInt(player.getRandom(), min, max);
        }

        private static int rollControlTicks(ServerPlayer player, float negativeProgress) {
            int min = Mth.floor(Mth.lerp(negativeProgress, 120.0F, 25.0F));
            int max = Mth.floor(Mth.lerp(negativeProgress, 300.0F, 80.0F));
            return Mth.nextInt(player.getRandom(), min, max);
        }
    }
}