package com.perigrine3.createcybernetics.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class CyberpsychosisMobHallucinationController {

    private static final double SEARCH_RADIUS = 32.0D;
    private static final double FAKE_ATTACK_RANGE = 2.75D;
    private static final double FAKE_ATTACK_RANGE_SQR = FAKE_ATTACK_RANGE * FAKE_ATTACK_RANGE;

    private static final int SCAN_INTERVAL_TICKS = 10;

    private static final int LEVEL_2_MIN_ACTIVE_TICKS = 20 * 5;
    private static final int LEVEL_2_MAX_ACTIVE_TICKS = 20 * 12;
    private static final int LEVEL_2_MIN_CALM_TICKS = 20 * 18;
    private static final int LEVEL_2_MAX_CALM_TICKS = 20 * 45;

    private static final int LEVEL_3_MIN_ACTIVE_TICKS = 20 * 16;
    private static final int LEVEL_3_MAX_ACTIVE_TICKS = 20 * 35;
    private static final int LEVEL_3_MIN_CALM_TICKS = 10;
    private static final int LEVEL_3_MAX_CALM_TICKS = 20 * 3;

    private static final int LEVEL_2_MAX_REPLACEMENTS = 2;
    private static final int LEVEL_3_MAX_REPLACEMENTS = 10;

    private static final float LEVEL_2_SELECTION_CHANCE = 0.24F;
    private static final float LEVEL_3_SELECTION_CHANCE = 0.82F;

    private static final int LEVEL_2_MIN_ATTACK_DELAY = 20 * 3;
    private static final int LEVEL_2_MAX_ATTACK_DELAY = 20 * 7;
    private static final int LEVEL_3_MIN_ATTACK_DELAY = 16;
    private static final int LEVEL_3_MAX_ATTACK_DELAY = 20 * 3;

    private static final Map<Integer, HallucinatedMob> HALLUCINATIONS = new HashMap<>();

    private static boolean renderingReplacement = false;
    private static boolean episodeActive = false;

    private static int activeTicksRemaining = 0;
    private static int calmTicksRemaining = 0;
    private static int previousRejectionLevel = 0;

    private CyberpsychosisMobHallucinationController() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        Minecraft mc = Minecraft.getInstance();

        if (mc.isPaused() || mc.level == null) return;

        int rejectionLevel = getRejectionLevel(player);
        boolean fugue = player.hasEffect(ModEffects.CYBERPSYCHOSIS_FUGUE);

        if (rejectionLevel < 2 && !fugue) {
            clear();
            return;
        }

        if (previousRejectionLevel != rejectionLevel) {
            resetEpisode(player, rejectionLevel, fugue);
            previousRejectionLevel = rejectionLevel;
        }

        if (fugue) {
            episodeActive = true;
            activeTicksRemaining = 0;
            calmTicksRemaining = 0;
        } else {
            tickEpisode(player, rejectionLevel);
        }

        if (!episodeActive) {
            if (!HALLUCINATIONS.isEmpty()) HALLUCINATIONS.clear();
            CyberpsychosisFakeDamageController.clearFakeDamage();
            return;
        }

        if (player.tickCount % SCAN_INTERVAL_TICKS == 0) refreshHallucinations(player, rejectionLevel, fugue);

        tickHallucinations(player, rejectionLevel, fugue);
    }

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Pre<?, ?> event) {
        if (renderingReplacement) return;

        LivingEntity realEntity = event.getEntity();
        HallucinatedMob hallucination = HALLUCINATIONS.get(realEntity.getId());

        if (hallucination == null || hallucination.replacement == null) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null || !episodeActive) return;

        event.setCanceled(true);

        updateReplacementForRender(realEntity, hallucination, player, event.getPartialTick());

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource buffers = event.getMultiBufferSource();

        float attackProgress = hallucination.getAttackProgress();
        float lungeScale = 1.0F + attackProgress * 0.08F;

        poseStack.pushPose();

        poseStack.scale(lungeScale, lungeScale, lungeScale);
        poseStack.translate(0.0F, -attackProgress * 0.05F, 0.0F);

        renderingReplacement = true;

        try {
            mc.getEntityRenderDispatcher().render(hallucination.replacement, 0.0D, 0.0D, 0.0D, hallucination.replacement.getYRot(), event.getPartialTick(), poseStack, buffers, event.getPackedLight());
        } finally {
            renderingReplacement = false;
            poseStack.popPose();
        }
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        clear();
    }

    public static boolean isHallucinated(Entity entity) {
        return entity != null && episodeActive && HALLUCINATIONS.containsKey(entity.getId());
    }

    public static HallucinationKind getHallucinationKind(Entity entity) {
        HallucinatedMob hallucination = entity == null ? null : HALLUCINATIONS.get(entity.getId());
        return hallucination == null ? null : hallucination.kind;
    }

    private static void tickEpisode(LocalPlayer player, int rejectionLevel) {
        if (episodeActive) {
            if (activeTicksRemaining > 0) activeTicksRemaining--;

            if (activeTicksRemaining <= 0) {
                episodeActive = false;
                HALLUCINATIONS.clear();
                CyberpsychosisFakeDamageController.clearFakeDamage();
                scheduleCalmPeriod(player.getRandom(), rejectionLevel);
            }

            return;
        }

        if (calmTicksRemaining > 0) calmTicksRemaining--;

        if (calmTicksRemaining <= 0) {
            episodeActive = true;
            scheduleActivePeriod(player.getRandom(), rejectionLevel);
        }
    }

    private static void resetEpisode(LocalPlayer player, int rejectionLevel, boolean fugue) {
        HALLUCINATIONS.clear();
        CyberpsychosisFakeDamageController.clearFakeDamage();

        activeTicksRemaining = 0;
        calmTicksRemaining = 0;
        episodeActive = fugue || rejectionLevel >= 3;

        if (episodeActive) {
            if (!fugue) scheduleActivePeriod(player.getRandom(), rejectionLevel);
        } else {
            scheduleCalmPeriod(player.getRandom(), rejectionLevel);
        }
    }

    private static void refreshHallucinations(LocalPlayer player, int rejectionLevel, boolean fugue) {
        ClientLevel level = player.clientLevel;
        AABB searchArea = player.getBoundingBox().inflate(SEARCH_RADIUS);

        HALLUCINATIONS.entrySet().removeIf(entry -> {
            Entity entity = level.getEntity(entry.getKey());
            return entity == null || !entity.isAlive() || !isPassiveMob(entity) || entity.distanceToSqr(player) > SEARCH_RADIUS * SEARCH_RADIUS;
        });

        int maximumReplacements = fugue ? 16 : rejectionLevel >= 3 ? LEVEL_3_MAX_REPLACEMENTS : LEVEL_2_MAX_REPLACEMENTS;
        float selectionChance = fugue ? 1.0F : rejectionLevel >= 3 ? LEVEL_3_SELECTION_CHANCE : LEVEL_2_SELECTION_CHANCE;

        if (HALLUCINATIONS.size() >= maximumReplacements) return;

        List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class, searchArea, entity -> entity != player && entity.isAlive() && isPassiveMob(entity) && !HALLUCINATIONS.containsKey(entity.getId()));

        for (LivingEntity candidate : candidates) {
            if (HALLUCINATIONS.size() >= maximumReplacements) break;
            if (player.getRandom().nextFloat() > selectionChance) continue;

            createHallucination(player, candidate, rejectionLevel, fugue);
        }
    }

    private static void createHallucination(LocalPlayer player, LivingEntity realEntity, int rejectionLevel, boolean fugue) {
        HallucinationKind kind = chooseKind(realEntity, player.getRandom());
        Mob replacement = kind.create(player.clientLevel);

        if (replacement == null) return;

        int minimumDelay = fugue || rejectionLevel >= 3 ? LEVEL_3_MIN_ATTACK_DELAY : LEVEL_2_MIN_ATTACK_DELAY;
        int maximumDelay = fugue || rejectionLevel >= 3 ? LEVEL_3_MAX_ATTACK_DELAY : LEVEL_2_MAX_ATTACK_DELAY;
        int nextAttackTick = player.tickCount + Mth.nextInt(player.getRandom(), minimumDelay, maximumDelay);

        HALLUCINATIONS.put(realEntity.getId(), new HallucinatedMob(kind, replacement, nextAttackTick));
    }

    private static void tickHallucinations(LocalPlayer player, int rejectionLevel, boolean fugue) {
        Iterator<Map.Entry<Integer, HallucinatedMob>> iterator = HALLUCINATIONS.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Integer, HallucinatedMob> entry = iterator.next();
            Entity entity = player.clientLevel.getEntity(entry.getKey());

            if (!(entity instanceof LivingEntity realEntity) || !realEntity.isAlive()) {
                iterator.remove();
                continue;
            }

            HallucinatedMob hallucination = entry.getValue();

            if (hallucination.attackTicks > 0) hallucination.attackTicks--;

            if (realEntity.distanceToSqr(player) > FAKE_ATTACK_RANGE_SQR) continue;
            if (player.tickCount < hallucination.nextAttackTick) continue;
            if (!hasClearView(player, realEntity)) continue;

            performFakeAttack(player, realEntity, hallucination, rejectionLevel, fugue);
        }
    }

    public static boolean hasActiveHallucinations() {
        return episodeActive && !HALLUCINATIONS.isEmpty();
    }

    private static void performFakeAttack(LocalPlayer player, LivingEntity realEntity, HallucinatedMob hallucination, int rejectionLevel, boolean fugue) {
        float fakeDamage;

        if (fugue) {
            fakeDamage = Mth.nextFloat(player.getRandom(), 2.5F, 5.5F);
        } else if (rejectionLevel >= 3) {
            fakeDamage = Mth.nextFloat(player.getRandom(), 2.0F, 4.5F);
        } else {
            fakeDamage = Mth.nextFloat(player.getRandom(), 1.0F, 2.5F);
        }

        CyberpsychosisFakeDamageController.applyFakeDamage(fakeDamage);

        hallucination.attackTicks = 10;

        int minimumDelay = fugue || rejectionLevel >= 3 ? LEVEL_3_MIN_ATTACK_DELAY : LEVEL_2_MIN_ATTACK_DELAY;
        int maximumDelay = fugue || rejectionLevel >= 3 ? LEVEL_3_MAX_ATTACK_DELAY : LEVEL_2_MAX_ATTACK_DELAY;

        hallucination.nextAttackTick = player.tickCount + Mth.nextInt(player.getRandom(), minimumDelay, maximumDelay);

        SoundEvent attackSound = hallucination.kind.getAttackSound();

        player.level().playLocalSound(realEntity.getX(), realEntity.getY(), realEntity.getZ(), attackSound, SoundSource.HOSTILE, 1.0F, 0.82F + player.getRandom().nextFloat() * 0.28F, false);
        player.level().playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_HURT, SoundSource.PLAYERS, 0.85F, 0.92F + player.getRandom().nextFloat() * 0.16F, false);
    }

    private static void updateReplacementForRender(LivingEntity realEntity, HallucinatedMob hallucination, LocalPlayer player, float partialTick) {
        Mob replacement = hallucination.replacement;

        replacement.tickCount = realEntity.tickCount;
        replacement.setPos(realEntity.getX(), realEntity.getY(), realEntity.getZ());
        replacement.xOld = realEntity.xOld;
        replacement.yOld = realEntity.yOld;
        replacement.zOld = realEntity.zOld;
        replacement.setDeltaMovement(realEntity.getDeltaMovement());

        float bodyYaw = Mth.rotLerp(partialTick, realEntity.yBodyRotO, realEntity.yBodyRot);
        Vec3 towardPlayer = player.getEyePosition(partialTick).subtract(realEntity.getEyePosition(partialTick));

        float headYaw = bodyYaw;
        float headPitch = 0.0F;

        if (towardPlayer.lengthSqr() > 0.0001D) {
            double horizontalDistance = Math.sqrt(towardPlayer.x * towardPlayer.x + towardPlayer.z * towardPlayer.z);

            headYaw = (float) (Mth.atan2(towardPlayer.z, towardPlayer.x) * Mth.RAD_TO_DEG) - 90.0F;
            headPitch = (float) -(Mth.atan2(towardPlayer.y, horizontalDistance) * Mth.RAD_TO_DEG);
        }

        replacement.setYRot(bodyYaw);
        replacement.yRotO = bodyYaw;
        replacement.yBodyRot = bodyYaw;
        replacement.yBodyRotO = bodyYaw;
        replacement.setYHeadRot(headYaw);
        replacement.yHeadRotO = headYaw;
        replacement.setXRot(headPitch);
        replacement.xRotO = headPitch;
        replacement.setOnGround(realEntity.onGround());

        updateReplacementWalkAnimation(realEntity, hallucination, partialTick);
    }

    private static void updateReplacementWalkAnimation(LivingEntity realEntity, HallucinatedMob hallucination, float partialTick) {
        Mob replacement = hallucination.replacement;

        double movementX = realEntity.getX() - realEntity.xOld;
        double movementZ = realEntity.getZ() - realEntity.zOld;
        double horizontalMovement = Math.sqrt(movementX * movementX + movementZ * movementZ);

        float targetSpeed = Mth.clamp((float) horizontalMovement * 4.0F, 0.0F, 1.0F);

        if (!realEntity.onGround()) targetSpeed *= 0.35F;

        hallucination.walkSpeed = Mth.lerp(0.35F, hallucination.walkSpeed, targetSpeed);

        if (hallucination.attackTicks > 0) hallucination.walkSpeed = Math.max(hallucination.walkSpeed, 0.65F);

        replacement.walkAnimation.update(hallucination.walkSpeed, 0.45F);
    }

    private static boolean hasClearView(LocalPlayer player, LivingEntity entity) {
        HitResult result = player.level().clip(new ClipContext(player.getEyePosition(), entity.getEyePosition(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        return result.getType() == HitResult.Type.MISS;
    }

    private static boolean isPassiveMob(Entity entity) {
        if (entity instanceof AbstractVillager) return true;
        if (entity instanceof Animal) return true;
        if (entity instanceof WaterAnimal) return true;

        return entity instanceof AmbientCreature;
    }

    private static HallucinationKind chooseKind(LivingEntity entity, RandomSource random) {
        if (entity.getBbWidth() > 1.15F) return random.nextBoolean() ? HallucinationKind.SPIDER : HallucinationKind.HUSK;
        if (entity.getBbHeight() < 1.0F) return random.nextBoolean() ? HallucinationKind.SPIDER : HallucinationKind.ZOMBIE;

        return HallucinationKind.values()[random.nextInt(HallucinationKind.values().length)];
    }

    private static int getRejectionLevel(LocalPlayer player) {
        if (!player.hasEffect(ModEffects.CYBERWARE_REJECTION)) return 0;

        return Mth.clamp(player.getEffect(ModEffects.CYBERWARE_REJECTION).getAmplifier() + 1, 1, 3);
    }

    private static void scheduleActivePeriod(RandomSource random, int rejectionLevel) {
        activeTicksRemaining = rejectionLevel >= 3
                ? Mth.nextInt(random, LEVEL_3_MIN_ACTIVE_TICKS, LEVEL_3_MAX_ACTIVE_TICKS)
                : Mth.nextInt(random, LEVEL_2_MIN_ACTIVE_TICKS, LEVEL_2_MAX_ACTIVE_TICKS);
    }

    private static void scheduleCalmPeriod(RandomSource random, int rejectionLevel) {
        calmTicksRemaining = rejectionLevel >= 3
                ? Mth.nextInt(random, LEVEL_3_MIN_CALM_TICKS, LEVEL_3_MAX_CALM_TICKS)
                : Mth.nextInt(random, LEVEL_2_MIN_CALM_TICKS, LEVEL_2_MAX_CALM_TICKS);
    }

    private static void clear() {
        HALLUCINATIONS.clear();

        renderingReplacement = false;
        episodeActive = false;

        activeTicksRemaining = 0;
        calmTicksRemaining = 0;
        previousRejectionLevel = 0;

        CyberpsychosisFakeDamageController.clearFakeDamage();
    }

    public enum HallucinationKind {
        ZOMBIE(EntityType.ZOMBIE, SoundEvents.ZOMBIE_AMBIENT, SoundEvents.ZOMBIE_HURT),
        HUSK(EntityType.HUSK, SoundEvents.HUSK_AMBIENT, SoundEvents.HUSK_HURT),
        SKELETON(EntityType.SKELETON, SoundEvents.SKELETON_AMBIENT, SoundEvents.SKELETON_HURT),
        STRAY(EntityType.STRAY, SoundEvents.STRAY_AMBIENT, SoundEvents.STRAY_HURT),
        SPIDER(EntityType.SPIDER, SoundEvents.SPIDER_AMBIENT, SoundEvents.SPIDER_HURT);

        private final EntityType<? extends Mob> entityType;
        private final SoundEvent ambientSound;
        private final SoundEvent attackSound;

        HallucinationKind(EntityType<? extends Mob> entityType, SoundEvent ambientSound, SoundEvent attackSound) {
            this.entityType = entityType;
            this.ambientSound = ambientSound;
            this.attackSound = attackSound;
        }

        private Mob create(ClientLevel level) {
            return entityType.create(level);
        }

        public SoundEvent getAmbientSound() {
            return ambientSound;
        }

        public SoundEvent getAttackSound() {
            return attackSound;
        }
    }

    private static final class HallucinatedMob {
        private final HallucinationKind kind;
        private final Mob replacement;

        private int nextAttackTick;
        private int attackTicks = 0;

        private float walkSpeed = 0.0F;

        private HallucinatedMob(HallucinationKind kind, Mob replacement, int nextAttackTick) {
            this.kind = kind;
            this.replacement = replacement;
            this.nextAttackTick = nextAttackTick;
        }

        private float getAttackProgress() {
            if (attackTicks <= 0) return 0.0F;

            float normalized = attackTicks / 10.0F;
            return Mth.sin((1.0F - normalized) * Mth.PI);
        }
    }
}