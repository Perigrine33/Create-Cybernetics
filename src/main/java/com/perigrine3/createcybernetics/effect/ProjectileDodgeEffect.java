package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.effect.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
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
            if (!(ehr.getEntity() instanceof ServerPlayer player)) return;

            if (!player.hasEffect(ModEffects.PROJECTILE_DODGE_EFFECT)) return;
            if (player.isPassenger()) return;

            Projectile projectile = event.getProjectile();
            if (projectile == null) return;

            long gameTime = player.serverLevel().getGameTime();
            var tag = player.getPersistentData();
            if (tag.getLong(NBT_NEXT_DODGE_TICK) > gameTime) return;

            if (tryEndermanStyleTeleport(player, TELEPORT_ATTEMPTS, TELEPORT_RANGE)) {
                event.setCanceled(true);
                projectile.discard();

                tag.putLong(NBT_NEXT_DODGE_TICK, gameTime + DODGE_COOLDOWN_TICKS);
            }
        }

        @SubscribeEvent
        public static void onIncomingDamage(LivingIncomingDamageEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player)) return;
            if (!player.hasEffect(ModEffects.PROJECTILE_DODGE_EFFECT)) return;
            if (player.isPassenger()) return;
            if (!(event.getSource().getDirectEntity() instanceof Projectile proj)) return;

            long gameTime = player.serverLevel().getGameTime();
            var tag = player.getPersistentData();
            if (tag.getLong(NBT_NEXT_DODGE_TICK) > gameTime) return;

            if (tryEndermanStyleTeleport(player, TELEPORT_ATTEMPTS, TELEPORT_RANGE)) {
                event.setCanceled(true);
                // Optional: also discard here, for consistency
                proj.discard();

                tag.putLong(NBT_NEXT_DODGE_TICK, gameTime + DODGE_COOLDOWN_TICKS);
            }
        }
    }

    private static boolean tryEndermanStyleTeleport(ServerPlayer player, int attempts, double cubeSize) {
        ServerLevel level = player.serverLevel();
        RandomSource random = player.getRandom();

        final double startX = player.getX();
        final double startY = player.getY();
        final double startZ = player.getZ();

        for (int i = 0; i < attempts; i++) {
            final double fromX = player.getX();
            final double fromY = player.getY();
            final double fromZ = player.getZ();

            double x = startX + (random.nextDouble() - 0.5) * cubeSize;
            double y = startY + (double) (random.nextInt((int) cubeSize) - ((int) cubeSize / 2));
            double z = startZ + (random.nextDouble() - 0.5) * cubeSize;

            if (tryTeleportToCandidate(player, level, x, y, z)) {
                spawnEndermanTeleportParticles(level, fromX, fromY, fromZ);
                spawnEndermanTeleportParticles(level, player.getX(), player.getY(), player.getZ());

                level.playSound(null, fromX, fromY, fromZ, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
                return true;
            }
        }
        return false;
    }

    private static boolean tryTeleportToCandidate(ServerPlayer player, ServerLevel level, double x, double y, double z) {
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

        AABB moved = player.getBoundingBox().move(tx - player.getX(), ty - player.getY(), tz - player.getZ());
        if (!level.noCollision(player, moved)) return false;
        if (level.containsAnyLiquid(moved)) return false;

        return player.teleportTo(level, tx, ty, tz, Set.<RelativeMovement>of(), player.getYRot(), player.getXRot());
    }

    public static void spawnEndermanTeleportParticles(ServerLevel level, double x, double y, double z) {
        level.sendParticles(ParticleTypes.PORTAL, x, y + 1.0, z, 96, 0.6, 1.2, 0.6, 0.0);
    }

    private static boolean blocksMotion(BlockState state) {
        return state.blocksMotion();
    }
}
