package com.perigrine3.createcybernetics.entity.ai.goal;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.EntityCyberwareData;
import com.perigrine3.createcybernetics.common.capabilities.ModMobAttachments;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.item.ItemStack;

public class CyberentityFirestarterAttackGoal extends MeleeAttackGoal {

    private static final float IGNITE_CHANCE = 0.50F;
    private static final int IGNITE_SECONDS = 4;

    private final PathfinderMob mob;

    public CyberentityFirestarterAttackGoal(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.mob = mob;
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target) {
        if (this.mob.isWithinMeleeAttackRange(target) && this.isTimeToAttack()) {
            this.resetAttackCooldown();
            this.mob.swing(this.mob.getUsedItemHand());
            this.mob.doHurtTarget(target);

            tryApplyFirestarter(target);
        }
    }

    private void tryApplyFirestarter(LivingEntity target) {
        if (target == null || !target.isAlive()) return;
        if (!mob.getMainHandItem().isEmpty()) return;
        if (!hasInstalledFirestarter()) return;
        if (mob.getRandom().nextFloat() >= IGNITE_CHANCE) return;

        target.igniteForSeconds(IGNITE_SECONDS);
    }

    private boolean hasInstalledFirestarter() {
        if (!mob.hasData(ModMobAttachments.CYBERENTITY_CYBERWARE)) return false;

        EntityCyberwareData data = mob.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
        if (data == null) return false;

        for (var entry : data.getAll().entrySet()) {
            CyberwareSlot slot = entry.getKey();
            InstalledCyberware[] arr = entry.getValue();
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {
                InstalledCyberware installed = arr[i];
                if (installed == null) continue;

                ItemStack stack = installed.getItem();
                if (stack == null || stack.isEmpty()) continue;
                if (!stack.is(ModItems.ARMUPGRADES_FIRESTARTER.get())) continue;
                if (!data.isEnabled(slot, i)) continue;

                return true;
            }
        }

        return false;
    }
}