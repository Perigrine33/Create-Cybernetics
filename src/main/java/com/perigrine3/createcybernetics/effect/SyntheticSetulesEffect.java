package com.perigrine3.createcybernetics.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class SyntheticSetulesEffect extends MobEffect {
    public SyntheticSetulesEffect(MobEffectCategory category, int color) {
        super(category, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity.horizontalCollision) {
            if (!livingEntity.isShiftKeyDown()) {
                Vec3 initialVec = livingEntity.getDeltaMovement();
                Vec3 climbVector = new Vec3(initialVec.x, 0.2, initialVec.z);
                livingEntity.setDeltaMovement(climbVector.scale(0.96));
                return true;
            }
        }

        return super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
