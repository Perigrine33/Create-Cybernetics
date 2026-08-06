package com.perigrine3.createcybernetics.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.client.render.CyberpsychosisShadowRenderer;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.screen.custom.visual_overlays.CyberwareRejectionOverlay;
import com.perigrine3.createcybernetics.sound.ModSounds;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class CyberpsychosisShadowController {

    private static final int WATCHER_MIN_INTERVAL_LEVEL_1 = 20 * 60;
    private static final int WATCHER_MAX_INTERVAL_LEVEL_1 = 20 * 90;

    private static final int WATCHER_INTERVAL_LEVEL_2 = 20 * 10;
    private static final int WATCHER_INTERVAL_LEVEL_3 = 20 * 3;

    private static final int JUMPSCARE_MIN_INTERVAL_LEVEL_2 = 20 * 35;
    private static final int JUMPSCARE_MAX_INTERVAL_LEVEL_2 = 20 * 60;

    private static final int JUMPSCARE_MIN_INTERVAL_LEVEL_3 = 20 * 18;
    private static final int JUMPSCARE_MAX_INTERVAL_LEVEL_3 = 20 * 35;

    private static final int WATCHER_MIN_LIFETIME_LEVEL_1 = 20 * 16;
    private static final int WATCHER_MAX_LIFETIME_LEVEL_1 = 20 * 35;

    private static final int WATCHER_MIN_LIFETIME_LEVEL_2 = 20 * 8;
    private static final int WATCHER_MAX_LIFETIME_LEVEL_2 = 20 * 16;

    private static final int WATCHER_MIN_LIFETIME_LEVEL_3 = 20 * 3;
    private static final int WATCHER_MAX_LIFETIME_LEVEL_3 = 20 * 6;

    private static final int JUMPSCARE_MIN_LIFETIME = 20 * 4;
    private static final int JUMPSCARE_MAX_LIFETIME = 20 * 7;

    private static final double WATCHER_MIN_DISTANCE_LEVEL_1 = 18.0D;
    private static final double WATCHER_MAX_DISTANCE_LEVEL_1 = 30.0D;

    private static final double WATCHER_MIN_DISTANCE_LEVEL_2 = 5.0D;
    private static final double WATCHER_MAX_DISTANCE_LEVEL_2 = 9.0D;

    private static final double WATCHER_MIN_DISTANCE_LEVEL_3 = 4.0D;
    private static final double WATCHER_MAX_DISTANCE_LEVEL_3 = 8.0D;

    private static final double WATCHER_DISAPPEAR_DISTANCE_LEVEL_1 = 15.0D;
    private static final double WATCHER_DISAPPEAR_DISTANCE_LEVEL_2 = 2.75D;
    private static final double WATCHER_DISAPPEAR_DISTANCE_LEVEL_3 = 2.75D;

    private static final double JUMPSCARE_MIN_DISTANCE = 3.0D;
    private static final double JUMPSCARE_MAX_DISTANCE = 5.0D;

    private static final int JUMPSCARE_MAX_LIGHT = 7;

    private static final float WATCHER_LOOK_DOT_LEVEL_1 = 0.96F;
    private static final float WATCHER_LOOK_DOT_LEVEL_2 = 0.91F;
    private static final float WATCHER_LOOK_DOT_LEVEL_3 = 0.88F;
    private static final float JUMPSCARE_LOOK_DOT = 0.70F;

    private static final int WATCHER_SEEN_TICKS_LEVEL_1 = 9;
    private static final int WATCHER_SEEN_TICKS_LEVEL_2 = 5;
    private static final int WATCHER_SEEN_TICKS_LEVEL_3 = 3;
    private static final int JUMPSCARE_SEEN_TICKS = 2;

    private static ShadowFigure shadow = null;

    private static int nextWatcherAttemptTick = -1;
    private static int nextJumpscareAttemptTick = -1;
    private static int previousRejectionLevel = 0;

    private CyberpsychosisShadowController() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        if (mc.isPaused() || mc.level == null) {
            return;
        }

        MobEffectInstance rejection = player.getEffect(ModEffects.CYBERWARE_REJECTION);
        int rejectionLevel = rejection == null ? 0 : Mth.clamp(rejection.getAmplifier() + 1, 1, 3);

        if (rejectionLevel <= 0) {
            clear();
            return;
        }

        if (previousRejectionLevel != rejectionLevel) {
            shadow = null;
            nextWatcherAttemptTick = -1;
            nextJumpscareAttemptTick = -1;
            previousRejectionLevel = rejectionLevel;
        }

        initializeTimers(player, rejectionLevel);

        if (shadow != null) {
            tickExistingShadow(player);
            return;
        }

        if (rejectionLevel >= 2 && player.tickCount >= nextJumpscareAttemptTick) {
            scheduleNextJumpscare(player, rejectionLevel);

            if (tryCreateJumpscare(player, rejectionLevel)) {
                return;
            }
        }

        if (player.tickCount >= nextWatcherAttemptTick) {
            scheduleNextWatcher(player, rejectionLevel);
            tryCreateWatcher(player, rejectionLevel);
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }

        ShadowFigure current = shadow;
        if (current == null) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null || mc.level == null) {
            clear();
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        if (poseStack == null) {
            return;
        }

        renderShadow(event, poseStack, player, current);
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        clear();
    }

    private static void initializeTimers(LocalPlayer player, int rejectionLevel) {
        if (nextWatcherAttemptTick < 0) {
            scheduleNextWatcher(player, rejectionLevel);
        }

        if (rejectionLevel >= 2) {
            if (nextJumpscareAttemptTick < 0) {
                scheduleNextJumpscare(player, rejectionLevel);
            }
        } else {
            nextJumpscareAttemptTick = -1;
        }
    }

    private static void tickExistingShadow(LocalPlayer player) {
        ShadowFigure current = shadow;

        if (current == null) {
            return;
        }

        current.age++;

        if (current.age >= current.maximumLifetime) {
            shadow = null;
            return;
        }

        if (current.type == ShadowType.WATCHER) {
            tickWatcher(player, current);
        } else {
            tickJumpscare(player, current);
        }
    }

    private static void tickWatcher(LocalPlayer player, ShadowFigure current) {
        double disappearDistance = getWatcherDisappearDistance(current.rejectionLevel);
        double distanceSqr = player.position().distanceToSqr(current.position);

        if (distanceSqr <= disappearDistance * disappearDistance) {
            disappearWithGlitch(player, current.rejectionLevel == 1 ? 6 : 8, current.rejectionLevel == 1 ? 0.60F : 0.85F);
            return;
        }

        float lookDot = getWatcherLookDot(current.rejectionLevel);

        if (isLookingAtShadow(player, current, lookDot) && hasClearView(player, current.getVisiblePosition())) {
            current.seenTicks++;

            if (current.seenTicks >= getWatcherSeenTicks(current.rejectionLevel)) {
                disappearWithGlitch(player, current.rejectionLevel == 1 ? 5 : 7, current.rejectionLevel == 1 ? 0.50F : 0.75F);
            }
        } else {
            current.seenTicks = Math.max(0, current.seenTicks - 1);
        }
    }

    private static void tickJumpscare(LocalPlayer player, ShadowFigure current) {
        if (isLookingAtShadow(player, current, JUMPSCARE_LOOK_DOT) && hasClearView(player, current.getVisiblePosition())) {
            current.seenTicks++;

            if (current.seenTicks >= JUMPSCARE_SEEN_TICKS) {
                disappearWithGlitch(player, 12, 1.20F);
            }
        } else {
            current.seenTicks = 0;
        }
    }

    private static boolean tryCreateWatcher(LocalPlayer player, int rejectionLevel) {
        ClientLevel level = player.clientLevel;
        RandomSource random = player.getRandom();

        Vec3 horizontalLook = getHorizontalLook(player);
        if (horizontalLook == null) {
            return false;
        }

        Vec3 side = new Vec3(-horizontalLook.z, 0.0D, horizontalLook.x);

        double minimumDistance = getWatcherMinimumDistance(rejectionLevel);
        double maximumDistance = getWatcherMaximumDistance(rejectionLevel);
        int attempts = rejectionLevel == 1 ? 32 : 16;

        for (int attempt = 0; attempt < attempts; attempt++) {
            double distance = Mth.lerp(random.nextDouble(), minimumDistance, maximumDistance);
            double forwardAmount;
            double sideAmount;

            if (rejectionLevel == 1) {
                forwardAmount = Mth.nextDouble(random, 0.30D, 1.0D);
                sideAmount = Mth.nextDouble(random, -1.25D, 1.25D);

                if (Math.abs(sideAmount) < 0.35D) {
                    sideAmount = sideAmount < 0.0D ? -0.35D : 0.35D;
                }
            } else {
                forwardAmount = Mth.nextDouble(random, 0.35D, 1.0D);
                sideAmount = Mth.nextDouble(random, -0.85D, 0.85D);

                if (Math.abs(sideAmount) < 0.25D) {
                    sideAmount = sideAmount < 0.0D ? -0.25D : 0.25D;
                }
            }

            Vec3 direction = horizontalLook.scale(forwardAmount).add(side.scale(sideAmount));

            if (direction.lengthSqr() <= 0.0001D) {
                continue;
            }

            direction = direction.normalize();

            Vec3 wanted = player.position().add(direction.scale(distance));
            BlockPos feet = findStandingPosition(level, BlockPos.containing(wanted.x, player.getY(), wanted.z), rejectionLevel == 1 ? 10 : 6);

            if (feet == null) {
                continue;
            }

            Vec3 position = Vec3.atBottomCenterOf(feet);
            Vec3 visiblePosition = position.add(0.0D, 1.45D, 0.0D);

            if (!hasClearView(player, visiblePosition)) {
                continue;
            }

            float bodyYaw = getYawToward(position, player.position());
            int lifetime = getWatcherLifetime(random, rejectionLevel);
            float alpha = rejectionLevel == 1 ? 0.76F : rejectionLevel == 2 ? 0.92F : 1.0F;

            shadow = new ShadowFigure(position, ShadowType.WATCHER, bodyYaw, lifetime, alpha, rejectionLevel);

            playAppearanceSound(player, rejectionLevel == 1 ? 0.10F : rejectionLevel == 2 ? 0.20F : 0.30F);
            return true;
        }

        return false;
    }

    private static boolean tryCreateJumpscare(LocalPlayer player, int rejectionLevel) {
        ClientLevel level = player.clientLevel;
        RandomSource random = player.getRandom();

        if (getCombinedLight(level, player.blockPosition()) > JUMPSCARE_MAX_LIGHT) {
            return false;
        }

        Vec3 horizontalLook = getHorizontalLook(player);
        if (horizontalLook == null) {
            return false;
        }

        Vec3 side = new Vec3(-horizontalLook.z, 0.0D, horizontalLook.x);

        for (int attempt = 0; attempt < 20; attempt++) {
            double distance = Mth.lerp(random.nextDouble(), JUMPSCARE_MIN_DISTANCE, JUMPSCARE_MAX_DISTANCE);
            double sideOffset = Mth.nextDouble(random, -1.25D, 1.25D);

            Vec3 wanted = player.position().subtract(horizontalLook.scale(distance)).add(side.scale(sideOffset));
            BlockPos feet = findStandingPosition(level, BlockPos.containing(wanted.x, player.getY(), wanted.z), 5);

            if (feet == null) {
                continue;
            }

            if (getCombinedLight(level, feet) > JUMPSCARE_MAX_LIGHT) {
                continue;
            }

            Vec3 position = Vec3.atBottomCenterOf(feet);
            Vec3 visiblePosition = position.add(0.0D, 1.45D, 0.0D);

            if (!hasClearView(player, visiblePosition)) {
                continue;
            }

            if (isPositionInFront(player, visiblePosition)) {
                continue;
            }

            float bodyYaw = getYawToward(position, player.position());
            int lifetime = Mth.nextInt(random, JUMPSCARE_MIN_LIFETIME, JUMPSCARE_MAX_LIFETIME);

            shadow = new ShadowFigure(position, ShadowType.JUMPSCARE, bodyYaw, lifetime, 1.0F, rejectionLevel);

            playAppearanceSound(player, rejectionLevel >= 3 ? 0.50F : 0.40F);
            return true;
        }

        return false;
    }

    private static void renderShadow(RenderLevelStageEvent event, PoseStack poseStack, LocalPlayer player, ShadowFigure current) {
        Minecraft mc = Minecraft.getInstance();
        Camera camera = event.getCamera();
        Vec3 cameraPosition = camera.getPosition();

        double renderX = current.position.x - cameraPosition.x;
        double renderY = current.position.y - cameraPosition.y;
        double renderZ = current.position.z - cameraPosition.z;

        float partialTick = mc.getTimer().getGameTimeDeltaPartialTick(false);
        float fadeIn = Mth.clamp(current.age / 5.0F, 0.0F, 1.0F);
        float fadeOut = Mth.clamp((current.maximumLifetime - current.age) / 8.0F, 0.0F, 1.0F);
        float alpha = current.alpha * Math.min(fadeIn, fadeOut);

        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();

        poseStack.pushPose();

        poseStack.translate(renderX, renderY, renderZ);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - current.bodyYaw));

        CyberpsychosisShadowRenderer.renderHallucination(poseStack, buffers, player, partialTick, alpha);

        poseStack.popPose();

        buffers.endBatch();
    }

    private static Vec3 getHorizontalLook(LocalPlayer player) {
        Vec3 look = player.getLookAngle();
        Vec3 horizontalLook = new Vec3(look.x, 0.0D, look.z);

        if (horizontalLook.lengthSqr() <= 0.0001D) {
            return null;
        }

        return horizontalLook.normalize();
    }

    private static boolean isLookingAtShadow(LocalPlayer player, ShadowFigure current, float requiredDot) {
        Vec3 towardShadow = current.getVisiblePosition().subtract(player.getEyePosition());

        if (towardShadow.lengthSqr() <= 0.0001D) {
            return true;
        }

        return player.getViewVector(1.0F).dot(towardShadow.normalize()) >= requiredDot;
    }

    private static boolean isPositionInFront(LocalPlayer player, Vec3 targetPosition) {
        Vec3 towardTarget = targetPosition.subtract(player.getEyePosition());

        if (towardTarget.lengthSqr() <= 0.0001D) {
            return true;
        }

        return player.getViewVector(1.0F).dot(towardTarget.normalize()) > 0.0D;
    }

    private static boolean hasClearView(LocalPlayer player, Vec3 targetPosition) {
        HitResult result = player.level().clip(new ClipContext(player.getEyePosition(), targetPosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        return result.getType() == HitResult.Type.MISS;
    }

    private static boolean canStandAt(ClientLevel level, BlockPos feet) {
        BlockPos ground = feet.below();

        BlockState groundState = level.getBlockState(ground);
        BlockState feetState = level.getBlockState(feet);
        BlockState headState = level.getBlockState(feet.above());

        if (groundState.getCollisionShape(level, ground).isEmpty()) {
            return false;
        }

        if (!feetState.getCollisionShape(level, feet).isEmpty()) {
            return false;
        }

        return headState.getCollisionShape(level, feet.above()).isEmpty();
    }

    private static BlockPos findStandingPosition(ClientLevel level, BlockPos origin, int verticalRange) {
        for (int offset = 0; offset <= verticalRange; offset++) {
            BlockPos below = origin.below(offset);

            if (canStandAt(level, below)) {
                return below;
            }

            if (offset > 0) {
                BlockPos above = origin.above(offset);

                if (canStandAt(level, above)) {
                    return above;
                }
            }
        }

        return null;
    }

    private static int getCombinedLight(ClientLevel level, BlockPos pos) {
        return level.getRawBrightness(pos, level.getSkyDarken());
    }

    private static double getWatcherMinimumDistance(int rejectionLevel) {
        return switch (rejectionLevel) {
            case 1 -> WATCHER_MIN_DISTANCE_LEVEL_1;
            case 2 -> WATCHER_MIN_DISTANCE_LEVEL_2;
            default -> WATCHER_MIN_DISTANCE_LEVEL_3;
        };
    }

    private static double getWatcherMaximumDistance(int rejectionLevel) {
        return switch (rejectionLevel) {
            case 1 -> WATCHER_MAX_DISTANCE_LEVEL_1;
            case 2 -> WATCHER_MAX_DISTANCE_LEVEL_2;
            default -> WATCHER_MAX_DISTANCE_LEVEL_3;
        };
    }

    private static double getWatcherDisappearDistance(int rejectionLevel) {
        return switch (rejectionLevel) {
            case 1 -> WATCHER_DISAPPEAR_DISTANCE_LEVEL_1;
            case 2 -> WATCHER_DISAPPEAR_DISTANCE_LEVEL_2;
            default -> WATCHER_DISAPPEAR_DISTANCE_LEVEL_3;
        };
    }

    private static float getWatcherLookDot(int rejectionLevel) {
        return switch (rejectionLevel) {
            case 1 -> WATCHER_LOOK_DOT_LEVEL_1;
            case 2 -> WATCHER_LOOK_DOT_LEVEL_2;
            default -> WATCHER_LOOK_DOT_LEVEL_3;
        };
    }

    private static int getWatcherSeenTicks(int rejectionLevel) {
        return switch (rejectionLevel) {
            case 1 -> WATCHER_SEEN_TICKS_LEVEL_1;
            case 2 -> WATCHER_SEEN_TICKS_LEVEL_2;
            default -> WATCHER_SEEN_TICKS_LEVEL_3;
        };
    }

    private static int getWatcherLifetime(RandomSource random, int rejectionLevel) {
        return switch (rejectionLevel) {
            case 1 -> Mth.nextInt(random, WATCHER_MIN_LIFETIME_LEVEL_1, WATCHER_MAX_LIFETIME_LEVEL_1);
            case 2 -> Mth.nextInt(random, WATCHER_MIN_LIFETIME_LEVEL_2, WATCHER_MAX_LIFETIME_LEVEL_2);
            default -> Mth.nextInt(random, WATCHER_MIN_LIFETIME_LEVEL_3, WATCHER_MAX_LIFETIME_LEVEL_3);
        };
    }

    private static float getYawToward(Vec3 source, Vec3 target) {
        double x = target.x - source.x;
        double z = target.z - source.z;

        return (float) (Mth.atan2(z, x) * (180.0D / Math.PI)) - 90.0F;
    }

    private static void disappearWithGlitch(LocalPlayer player, int glitchTicks, float volume) {
        shadow = null;

        CyberwareRejectionOverlay.triggerForcedPulse(glitchTicks);

        player.level().playLocalSound(player.getX(), player.getY(), player.getZ(), ModSounds.GLITCHY.get(), SoundSource.PLAYERS, volume, 0.80F + player.getRandom().nextFloat() * 0.35F, false);
    }

    private static void playAppearanceSound(LocalPlayer player, float volume) {
        player.level().playLocalSound(player.getX(), player.getY(), player.getZ(), ModSounds.GLITCHY.get(), SoundSource.PLAYERS, volume, 0.65F + player.getRandom().nextFloat() * 0.20F, false);
    }

    private static void scheduleNextWatcher(LocalPlayer player, int rejectionLevel) {
        int delay = switch (rejectionLevel) {
            case 1 -> Mth.nextInt(player.getRandom(), WATCHER_MIN_INTERVAL_LEVEL_1, WATCHER_MAX_INTERVAL_LEVEL_1);
            case 2 -> WATCHER_INTERVAL_LEVEL_2;
            default -> WATCHER_INTERVAL_LEVEL_3;
        };

        nextWatcherAttemptTick = player.tickCount + delay;
    }

    private static void scheduleNextJumpscare(LocalPlayer player, int rejectionLevel) {
        int min = rejectionLevel >= 3 ? JUMPSCARE_MIN_INTERVAL_LEVEL_3 : JUMPSCARE_MIN_INTERVAL_LEVEL_2;
        int max = rejectionLevel >= 3 ? JUMPSCARE_MAX_INTERVAL_LEVEL_3 : JUMPSCARE_MAX_INTERVAL_LEVEL_2;

        nextJumpscareAttemptTick = player.tickCount + Mth.nextInt(player.getRandom(), min, max);
    }

    public static boolean hasActiveShadow() {
        return shadow != null;
    }

    private static void clear() {
        shadow = null;
        nextWatcherAttemptTick = -1;
        nextJumpscareAttemptTick = -1;
        previousRejectionLevel = 0;
    }

    private enum ShadowType {
        WATCHER,
        JUMPSCARE
    }

    private static final class ShadowFigure {
        private final Vec3 position;
        private final ShadowType type;
        private final float bodyYaw;
        private final int maximumLifetime;
        private final float alpha;
        private final int rejectionLevel;

        private int age = 0;
        private int seenTicks = 0;

        private ShadowFigure(Vec3 position, ShadowType type, float bodyYaw, int maximumLifetime, float alpha, int rejectionLevel) {
            this.position = position;
            this.type = type;
            this.bodyYaw = bodyYaw;
            this.maximumLifetime = maximumLifetime;
            this.alpha = alpha;
            this.rejectionLevel = rejectionLevel;
        }

        private Vec3 getVisiblePosition() {
            return position.add(0.0D, 1.45D, 0.0D);
        }
    }
}