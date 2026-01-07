package com.perigrine3.createcybernetics.entity.projectile;

import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.entity.ModEntities;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.joml.Vector3f;

public class EmpGrenadeProjectile extends ThrowableItemProjectile {

    /* ===================== GAMEPLAY ===================== */

    private static final double EFFECT_RADIUS = 6.0D;
    private static final int EMP_DURATION_TICKS = 200; // initial pulse

    // Aura reapply while the sphere exists
    private static final int AURA_EVERY_TICKS = 5;
    private static final int AURA_REFRESH_TICKS = 40;

    /* ===================== VISUALS ===================== */

    private static final double VISUAL_RADIUS = 6.0D;

    // Total time the grenade stays to render FX (30 seconds)
    private static final int TOTAL_FX_TICKS = 20 * 30;

    // Primary sphere: expands once, then lingers
    private static final int PRIMARY_BURST_TICKS = 6;

    // Secondary sphere: expands 0->6, optionally holds briefly, then loops
    private static final int SECONDARY_BURST_TICKS = 10;
    private static final int SECONDARY_HOLD_TICKS = 6;
    private static final int SECONDARY_CYCLE_TICKS = SECONDARY_BURST_TICKS + SECONDARY_HOLD_TICKS;

    private static final DustParticleOptions BLUE_DUST =
            new DustParticleOptions(new Vector3f(0.65F, 0.85F, 1.00F), 1.15F);

    /* ===================== STATE ===================== */

    private boolean fxRunning = false;
    private boolean empApplied = false;

    // Persisted timing so unload/reload does not break despawn
    private long fxStartGameTime = -1L;
    private long fxEndGameTime = -1L;

    public EmpGrenadeProjectile(Level level, LivingEntity owner) {
        super(ModEntities.EMP_GRENADE_PROJECTILE.get(), owner, level);
    }

    public EmpGrenadeProjectile(EntityType<? extends EmpGrenadeProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.EMP_GRENADE.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (result.getType() == HitResult.Type.MISS) return;
        detonateNoExplosion();
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) return;
        if (!fxRunning) return;
        if (!(level() instanceof ServerLevel sl)) return;

        long now = level().getGameTime();

        // If we reloaded without proper times (older saves), recover safely.
        if (fxStartGameTime < 0L) {
            fxStartGameTime = now;
        }
        if (fxEndGameTime < 0L) {
            fxEndGameTime = fxStartGameTime + TOTAL_FX_TICKS;
        }

        // If time has already elapsed (e.g., player logged out and came back later), despawn immediately.
        if (now >= fxEndGameTime) {
            discard();
            return;
        }

        // Derive fxAge from gameTime so it survives chunk unload/reload.
        int fxAge = (int) Math.max(0L, now - fxStartGameTime);

        runPrimarySphereFx(sl, fxAge);
        runSecondaryLoopSphereFx(sl, fxAge);
        applyEmpAura();
    }

    private void detonateNoExplosion() {
        if (level().isClientSide) return;

        // Prevent double detonation
        if (fxRunning) return;

        long now = level().getGameTime();

        // Apply gameplay effect once, immediately (initial pulse)
        if (!empApplied) {
            empApplied = true;

            level().playSound(null, blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.0F, 1.2F);

            for (LivingEntity target : level().getEntitiesOfClass(
                    LivingEntity.class,
                    getBoundingBox().inflate(EFFECT_RADIUS),
                    LivingEntity::isAlive
            )) {
                target.addEffect(new MobEffectInstance(ModEffects.EMP, EMP_DURATION_TICKS, 0));
            }
        }

        // Start visuals + timed lifetime (absolute times)
        fxRunning = true;
        fxStartGameTime = now;
        fxEndGameTime = now + TOTAL_FX_TICKS;

        // Park the projectile
        setDeltaMovement(0.0D, 0.0D, 0.0D);
        setNoGravity(true);
    }

    /* ===================== EMP AURA ===================== */

    private void applyEmpAura() {
        if (level().isClientSide) return;

        if ((tickCount % AURA_EVERY_TICKS) != 0) return;

        for (LivingEntity target : level().getEntitiesOfClass(
                LivingEntity.class,
                getBoundingBox().inflate(EFFECT_RADIUS),
                LivingEntity::isAlive
        )) {
            // Always refresh while inside.
            target.addEffect(new MobEffectInstance(ModEffects.EMP, AURA_REFRESH_TICKS, 0));
        }
    }

    /* ===================== PRIMARY SPHERE ===================== */

    private void runPrimarySphereFx(ServerLevel sl, int fxAge) {
        final double r;
        if (fxAge < PRIMARY_BURST_TICKS) {
            double t = (fxAge + 1) / (double) PRIMARY_BURST_TICKS; // 0..1
            r = VISUAL_RADIUS * t;
        } else {
            r = VISUAL_RADIUS;
        }

        final int count = (fxAge < PRIMARY_BURST_TICKS) ? 90 : 45;
        final double speed = (fxAge < PRIMARY_BURST_TICKS) ? 0.08D : 0.015D;

        spawnShell(sl, r, count, speed, true, true);
    }

    /* ===================== SECONDARY LOOPING SPHERE ===================== */

    private void runSecondaryLoopSphereFx(ServerLevel sl, int fxAge) {
        int cycleAge = fxAge % SECONDARY_CYCLE_TICKS;

        final double r;
        final int count;
        final double speed;

        if (cycleAge < SECONDARY_BURST_TICKS) {
            double t = (cycleAge + 1) / (double) SECONDARY_BURST_TICKS; // 0..1
            r = VISUAL_RADIUS * t;

            count = 42;
            speed = 0.045D;
        } else {
            r = VISUAL_RADIUS;

            count = 18;
            speed = 0.010D;
        }

        spawnShell(sl, r, count, speed, true, true);
    }

    /* ===================== PARTICLE SHELL ===================== */

    private void spawnShell(ServerLevel sl, double r, int count, double outwardSpeed, boolean spawnSpark, boolean spawnBlueDust) {
        final double cx = getX();
        final double cy = getY() + 0.1D;
        final double cz = getZ();

        final RandomSource rand = sl.getRandom();

        for (int i = 0; i < count; i++) {
            double u = rand.nextDouble();
            double v = rand.nextDouble();

            double theta = 2.0D * Math.PI * u;
            double phi = Math.acos(2.0D * v - 1.0D);

            double sinPhi = Math.sin(phi);

            double dx = sinPhi * Math.cos(theta);
            double dy = Math.cos(phi);
            double dz = sinPhi * Math.sin(theta);

            double px = cx + dx * r;
            double py = cy + dy * r;
            double pz = cz + dz * r;

            if (spawnBlueDust) {
                sl.sendParticles(BLUE_DUST, px, py, pz, 1,
                        0.0D, 0.0D, 0.0D, 0.0D);
            }

            if (spawnSpark) {
                sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, px, py, pz, 1,
                        0.0D, 0.0D, 0.0D, 0.0D);

                sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, px, py, pz, 1,
                        dx, dy, dz, outwardSpeed);
            }
        }
    }

    /* ===================== NBT PERSISTENCE ===================== */

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putBoolean("cc_fxRunning", fxRunning);
        tag.putBoolean("cc_empApplied", empApplied);
        tag.putLong("cc_fxStart", fxStartGameTime);
        tag.putLong("cc_fxEnd", fxEndGameTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        fxRunning = tag.getBoolean("cc_fxRunning");
        empApplied = tag.getBoolean("cc_empApplied");
        fxStartGameTime = tag.contains("cc_fxStart") ? tag.getLong("cc_fxStart") : -1L;
        fxEndGameTime = tag.contains("cc_fxEnd") ? tag.getLong("cc_fxEnd") : -1L;

        if (fxRunning) {
            setDeltaMovement(0.0D, 0.0D, 0.0D);
            setNoGravity(true);
        }
    }
}
