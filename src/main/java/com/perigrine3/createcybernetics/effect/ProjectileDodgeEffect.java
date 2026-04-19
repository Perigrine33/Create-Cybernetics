package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.Set;

public class ProjectileDodgeEffect extends MobEffect {
    private static final int TELEPORT_ATTEMPTS = 64;
    private static final double TELEPORT_RANGE = 64.0;

    private static final String NBT_NEXT_DODGE_TICK = "cc_projectile_dodge_nextTick";
    private static final int DODGE_COOLDOWN_TICKS = 0;

    public ProjectileDodgeEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x000000);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return false;
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID)
    public static final class Events {

        @SubscribeEvent
        public static void onProjectileImpact(ProjectileImpactEvent event) {
            HitResult hit = event.getRayTraceResult();
            if (hit.getType() != HitResult.Type.ENTITY) return;

            EntityHitResult ehr = (EntityHitResult) hit;
            if (!(ehr.getEntity() instanceof LivingEntity living)) return;
            if (!(living.level() instanceof ServerLevel level)) return;

            if (!living.hasEffect(ModEffects.PROJECTILE_DODGE_EFFECT)) return;
            if (living.isPassenger()) return;

            Projectile projectile = event.getProjectile();
            if (projectile == null) return;

            long gameTime = level.getGameTime();
            var tag = living.getPersistentData();
            if (tag.getLong(NBT_NEXT_DODGE_TICK) > gameTime) return;

            if (tryEndermanStyleTeleport(living, TELEPORT_ATTEMPTS, TELEPORT_RANGE)) {
                event.setCanceled(true);
                projectile.discard();
                tag.putLong(NBT_NEXT_DODGE_TICK, gameTime + DODGE_COOLDOWN_TICKS);
            }
        }

        @SubscribeEvent
        public static void onIncomingDamage(LivingIncomingDamageEvent event) {
            LivingEntity living = event.getEntity();
            if (!(living.level() instanceof ServerLevel level)) return;

            if (!living.hasEffect(ModEffects.PROJECTILE_DODGE_EFFECT)) return;
            if (living.isPassenger()) return;
            if (!(event.getSource().getDirectEntity() instanceof Projectile proj)) return;

            long gameTime = level.getGameTime();
            var tag = living.getPersistentData();
            if (tag.getLong(NBT_NEXT_DODGE_TICK) > gameTime) return;

            if (tryEndermanStyleTeleport(living, TELEPORT_ATTEMPTS, TELEPORT_RANGE)) {
                event.setCanceled(true);
                proj.discard();
                tag.putLong(NBT_NEXT_DODGE_TICK, gameTime + DODGE_COOLDOWN_TICKS);
            }
        }

        private Events() {}
    }

    private static boolean tryEndermanStyleTeleport(LivingEntity entity, int attempts, double cubeSize) {
        if (!(entity.level() instanceof ServerLevel level)) return false;

        RandomSource random = entity.getRandom();

        final double startX = entity.getX();
        final double startY = entity.getY();
        final double startZ = entity.getZ();

        for (int i = 0; i < attempts; i++) {
            final double fromX = entity.getX();
            final double fromY = entity.getY();
            final double fromZ = entity.getZ();

            double x = startX + (random.nextDouble() - 0.5) * cubeSize;
            double y = startY + (double)(random.nextInt((int) cubeSize) - ((int) cubeSize / 2));
            double z = startZ + (random.nextDouble() - 0.5) * cubeSize;

            if (tryTeleportToCandidate(entity, level, x, y, z)) {
                spawnEndermanTeleportParticles(level, fromX, fromY, fromZ);
                spawnEndermanTeleportParticles(level, entity.getX(), entity.getY(), entity.getZ());

                SoundSource source = entity instanceof ServerPlayer ? SoundSource.PLAYERS : SoundSource.HOSTILE;
                level.playSound(null, fromX, fromY, fromZ, SoundEvents.ENDERMAN_TELEPORT, source, 1.0f, 1.0f);
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENDERMAN_TELEPORT, source, 1.0f, 1.0f);
                return true;
            }
        }

        return false;
    }

    private static boolean tryTeleportToCandidate(LivingEntity entity, ServerLevel level, double x, double y, double z) {
        BlockPos pos = BlockPos.containing(x, y, z);

        if (!level.hasChunkAt(pos)) return false;
        if (!level.getWorldBorder().isWithinBounds(pos)) return false;

        int minY = level.getMinBuildHeight();
        while (pos.getY() > minY && blocksMotion(level.getBlockState(pos))) {
            pos = pos.below();
        }

        if (pos.getY() <= minY) return false;
        if (blocksMotion(level.getBlockState(pos))) return false;

        BlockPos below = pos.below();
        if (!blocksMotion(level.getBlockState(below))) return false;
        if (!level.getFluidState(pos).isEmpty()) return false;

        double tx = pos.getX() + 0.5;
        double ty = pos.getY();
        double tz = pos.getZ() + 0.5;

        AABB moved = entity.getBoundingBox().move(tx - entity.getX(), ty - entity.getY(), tz - entity.getZ());
        if (!level.noCollision(entity, moved)) return false;
        if (level.containsAnyLiquid(moved)) return false;

        if (entity instanceof ServerPlayer player) {
            return player.teleportTo(level, tx, ty, tz, Set.<RelativeMovement>of(), player.getYRot(), player.getXRot());
        }

        entity.teleportTo(tx, ty, tz);
        return true;
    }

    public static void spawnEndermanTeleportParticles(ServerLevel level, double x, double y, double z) {
        level.sendParticles(ParticleTypes.PORTAL, x, y + 1.0, z, 96, 0.6, 1.2, 0.6, 0.0);
    }

    private static boolean blocksMotion(BlockState state) {
        return state.blocksMotion();
    }
}