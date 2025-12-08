package com.perigrine3.createcybernetics.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoodProperties {

    public static final FoodProperties NEUROPOZYNE = new FoodProperties.Builder().alwaysEdible()
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 130), 0.5f).build();

}
