package com.perigrine3.createcybernetics.entity.ai.goal;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.EntityCyberwareData;
import com.perigrine3.createcybernetics.common.capabilities.ModMobAttachments;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class CyberentityDynamicBowAttackGoal<T extends Mob & RangedAttackMob> extends Goal {
    private static final int NORMAL_SKELETON_INTERVAL = 20;

    private static final int BASE_INTERVAL = 16;
    private static final int FLYWHEEL_INTERVAL = 13;
    private static final int SANDEVISTAN_INTERVAL = 11;

    private final T mob;
    private final double speedModifier;
    private final float attackRadiusSqr;

    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public CyberentityDynamicBowAttackGoal(T mob, double speedModifier, float attackRadius) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.attackRadiusSqr = attackRadius * attackRadius;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return mob.getTarget() != null && isHoldingBow();
    }

    @Override
    public boolean canContinueToUse() {
        return (canUse() || !mob.getNavigation().isDone()) && isHoldingBow();
    }

    @Override
    public void start() {
        super.start();
        mob.setAggressive(true);
    }

    @Override
    public void stop() {
        super.stop();
        mob.setAggressive(false);
        seeTime = 0;
        attackTime = -1;
        mob.stopUsingItem();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;

        double distanceSqr = mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
        boolean canSee = mob.getSensing().hasLineOfSight(target);
        boolean hadSight = seeTime > 0;

        if (canSee != hadSight) {
            seeTime = 0;
        }

        if (canSee) {
            seeTime++;
        } else {
            seeTime--;
        }

        if (distanceSqr <= attackRadiusSqr && seeTime >= 20) {
            mob.getNavigation().stop();
            strafingTime++;
        } else {
            mob.getNavigation().moveTo(target, speedModifier);
            strafingTime = -1;
        }

        if (strafingTime >= 20) {
            if (mob.getRandom().nextFloat() < 0.3D) {
                strafingClockwise = !strafingClockwise;
            }

            if (mob.getRandom().nextFloat() < 0.3D) {
                strafingBackwards = !strafingBackwards;
            }

            strafingTime = 0;
        }

        if (strafingTime > -1) {
            if (distanceSqr > attackRadiusSqr * 0.75F) {
                strafingBackwards = false;
            } else if (distanceSqr < attackRadiusSqr * 0.25F) {
                strafingBackwards = true;
            }

            mob.getMoveControl().strafe(strafingBackwards ? -0.5F : 0.5F, strafingClockwise ? 0.5F : -0.5F);
            mob.lookAt(target, 30.0F, 30.0F);
        } else {
            mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }

        if (mob.isUsingItem()) {
            if (!canSee && seeTime < -60) {
                mob.stopUsingItem();
                return;
            }

            if (canSee) {
                int usedTicks = mob.getTicksUsingItem();

                if (usedTicks >= getCurrentAttackInterval()) {
                    mob.stopUsingItem();

                    float power = BowItem.getPowerForTime(usedTicks);
                    mob.performRangedAttack(target, power);

                    attackTime = getCurrentAttackInterval();
                }
            }

            return;
        }

        if (--attackTime <= 0 && seeTime >= -60) {
            mob.startUsingItem(getBowHand());
        }
    }

    private int getCurrentAttackInterval() {
        if (isSandevistanActive()) {
            return SANDEVISTAN_INTERVAL;
        }

        if (hasFlywheelInstalled()) {
            return FLYWHEEL_INTERVAL;
        }

        return BASE_INTERVAL;
    }

    private boolean isHoldingBow() {
        return mob.getMainHandItem().getItem() instanceof BowItem || mob.getOffhandItem().getItem() instanceof BowItem;
    }

    private InteractionHand getBowHand() {
        ItemStack main = mob.getMainHandItem();
        if (main.getItem() instanceof BowItem) {
            return InteractionHand.MAIN_HAND;
        }

        return InteractionHand.OFF_HAND;
    }

    private boolean isSandevistanActive() {
        return mob.getPersistentData().getLong(CyberentitySandevistanGoal.NBT_SANDY_ACTIVE_UNTIL) > mob.level().getGameTime();
    }

    private boolean hasFlywheelInstalled() {
        if (!mob.hasData(ModMobAttachments.CYBERENTITY_CYBERWARE)) return false;

        EntityCyberwareData data = mob.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
        if (data == null) return false;

        return hasFlywheelInSlot(data, CyberwareSlot.RARM) || hasFlywheelInSlot(data, CyberwareSlot.LARM);
    }

    private static boolean hasFlywheelInSlot(EntityCyberwareData data, CyberwareSlot slot) {
        InstalledCyberware[] installed = data.getAll().get(slot);
        if (installed == null) return false;

        for (InstalledCyberware cyberware : installed) {
            if (cyberware == null) continue;

            ItemStack stack = cyberware.getItem();
            if (stack == null || stack.isEmpty()) continue;

            if (stack.is(ModItems.ARMUPGRADES_FLYWHEEL.get())) {
                return true;
            }
        }

        return false;
    }
}