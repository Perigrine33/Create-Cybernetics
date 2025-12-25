package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class InkedEffect extends MobEffect {
    private static final ResourceLocation FOLLOW_RANGE_MOD_ID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "inked_follow_range");

    public InkedEffect() {
        super(MobEffectCategory.HARMFUL, 0x000000);
        addAttributeModifier(Attributes.FOLLOW_RANGE, FOLLOW_RANGE_MOD_ID, -0.90D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Mob mob) {
            mob.getNavigation().stop();
            if (mob.getTarget() != null) {
                mob.setTarget(null);
            }
        }
        return true;
    }
}
