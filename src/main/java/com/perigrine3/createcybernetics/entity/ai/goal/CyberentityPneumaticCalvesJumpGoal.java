package com.perigrine3.createcybernetics.entity.ai.goal;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.EntityCyberwareData;
import com.perigrine3.createcybernetics.common.capabilities.ModMobAttachments;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class CyberentityPneumaticCalvesJumpGoal extends Goal {

    private static final String NBT_LAST_PNEUMATIC_JUMP_TICK = "cc_last_pneumatic_jump_tick";
    private static final String NBT_PENDING_PNEUMATIC_JUMP = "cc_pending_pneumatic_jump";
    private static final String NBT_PENDING_PNEUMATIC_X = "cc_pending_pneumatic_x";
    private static final String NBT_PENDING_PNEUMATIC_Z = "cc_pending_pneumatic_z";

    private static final int JUMP_COOLDOWN_TICKS = 10;

    private static final double MIN_TARGET_HEIGHT_DELTA = 0.6D;
    private static final double MAX_TARGET_HEIGHT_DELTA = 6.0D;

    private static final double MIN_HORIZONTAL_DIST_SQ = 1.0D;
    private static final double MAX_HORIZONTAL_DIST_SQ = 100.0D;

    private static final double FORWARD_BOOST = 0.50D;
    private static final double VERTICAL_BOOST = 0.85D;

    private static final double PARTICLE_BEHIND_DIST = 0.6D;
    private static final double PARTICLE_Y_OFFSET = 0.15D;

    private final Mob mob;

    public CyberentityPneumaticCalvesJumpGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!mob.isAlive()) return false;
        if (mob.level().isClientSide) return false;
        if (!hasPneumaticCalvesInstalled()) return false;

        LivingEntity target = mob.getTarget();
        return isValidTarget(target);
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = mob.getTarget();
        return mob.isAlive() && hasPneumaticCalvesInstalled() && isValidTarget(target);
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (!isValidTarget(target)) return;

        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (tryApplyPendingBoost()) {
            return;
        }

        if (!mob.onGround()) return;
        if (mob.isPassenger()) return;
        if (mob.isInWaterOrBubble()) return;
        if (mob.onClimbable()) return;
        if (!mob.getSensing().hasLineOfSight(target)) return;

        double dy = target.getY() - mob.getY();
        if (dy < MIN_TARGET_HEIGHT_DELTA) return;
        if (dy > MAX_TARGET_HEIGHT_DELTA) return;

        double dx = target.getX() - mob.getX();
        double dz = target.getZ() - mob.getZ();
        double horizontalDistSq = dx * dx + dz * dz;

        if (horizontalDistSq < MIN_HORIZONTAL_DIST_SQ) return;
        if (horizontalDistSq > MAX_HORIZONTAL_DIST_SQ) return;

        long now = mob.level().getGameTime();
        long last = mob.getPersistentData().getLong(NBT_LAST_PNEUMATIC_JUMP_TICK);
        if (now - last < JUMP_COOLDOWN_TICKS) return;

        Vec3 towardTarget = new Vec3(dx, 0.0D, dz);
        if (towardTarget.lengthSqr() < 1.0E-6D) {
            towardTarget = Vec3.directionFromRotation(0.0F, mob.getYRot());
        } else {
            towardTarget = towardTarget.normalize();
        }

        mob.getPersistentData().putLong(NBT_LAST_PNEUMATIC_JUMP_TICK, now);
        mob.getPersistentData().putBoolean(NBT_PENDING_PNEUMATIC_JUMP, true);
        mob.getPersistentData().putDouble(NBT_PENDING_PNEUMATIC_X, towardTarget.x);
        mob.getPersistentData().putDouble(NBT_PENDING_PNEUMATIC_Z, towardTarget.z);

        mob.getJumpControl().jump();
        mob.getNavigation().moveTo(target, 1.2D);
    }

    private boolean tryApplyPendingBoost() {
        if (!mob.getPersistentData().getBoolean(NBT_PENDING_PNEUMATIC_JUMP)) return false;
        if (mob.onGround()) return false;

        double dirX = mob.getPersistentData().getDouble(NBT_PENDING_PNEUMATIC_X);
        double dirZ = mob.getPersistentData().getDouble(NBT_PENDING_PNEUMATIC_Z);

        mob.getPersistentData().remove(NBT_PENDING_PNEUMATIC_JUMP);
        mob.getPersistentData().remove(NBT_PENDING_PNEUMATIC_X);
        mob.getPersistentData().remove(NBT_PENDING_PNEUMATIC_Z);

        Vec3 current = mob.getDeltaMovement();
        mob.setDeltaMovement(
                current.x + dirX * FORWARD_BOOST,
                Math.max(current.y, VERTICAL_BOOST),
                current.z + dirZ * FORWARD_BOOST
        );

        mob.hasImpulse = true;
        mob.hurtMarked = true;

        mob.level().playSound(
                null,
                mob.getX(),
                mob.getY(),
                mob.getZ(),
                SoundEvents.PISTON_EXTEND,
                SoundSource.HOSTILE,
                0.25F,
                1.35F
        );

        if (mob.level() instanceof ServerLevel sl) {
            Vec3 behind = new Vec3(dirX, 0.0D, dirZ).scale(-PARTICLE_BEHIND_DIST);
            Vec3 particlePos = mob.position().add(behind).add(0.0D, PARTICLE_Y_OFFSET, 0.0D);

            sl.sendParticles(
                    ParticleTypes.CLOUD,
                    particlePos.x,
                    particlePos.y,
                    particlePos.z,
                    8,
                    0.18D,
                    0.10D,
                    0.18D,
                    0.02D
            );
        }

        return true;
    }

    private boolean hasPneumaticCalvesInstalled() {
        if (!mob.hasData(ModMobAttachments.CYBERENTITY_CYBERWARE)) return false;

        EntityCyberwareData data = mob.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
        if (data == null) return false;

        InstalledCyberware[] rightLeg = data.getAll().get(CyberwareSlot.RLEG);
        if (hasEnabledItem(rightLeg, data, CyberwareSlot.RLEG)) return true;

        InstalledCyberware[] leftLeg = data.getAll().get(CyberwareSlot.LLEG);
        return hasEnabledItem(leftLeg, data, CyberwareSlot.LLEG);
    }

    private boolean hasEnabledItem(InstalledCyberware[] arr, EntityCyberwareData data, CyberwareSlot slot) {
        if (arr == null) return false;

        for (int i = 0; i < arr.length; i++) {
            InstalledCyberware installed = arr[i];
            if (installed == null) continue;
            if (installed.getItem() == null || installed.getItem().isEmpty()) continue;
            if (!installed.getItem().is(ModItems.LEGUPGRADES_JUMPBOOST.get())) continue;
            if (!data.isEnabled(slot, i)) continue;
            return true;
        }

        return false;
    }

    private static boolean isValidTarget(LivingEntity target) {
        return target != null && target.isAlive();
    }
}