package com.perigrine3.createcybernetics.effect;

import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.damagesource.DamageSource;


import java.util.Random;

public class CyberwareRejectionEffect extends MobEffect {
    private final Random random = new Random();

    public CyberwareRejectionEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 600, 1, false, false, false));
        livingEntity.hurt(livingEntity.damageSources().generic(), (float)(1 << amplifier));

        return super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return super.shouldApplyEffectTickThisTick(duration, amplifier);
    }
}
