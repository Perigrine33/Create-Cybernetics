package com.perigrine3.createcybernetics.item;

import net.minecraft.world.food.FoodProperties;

public class ModFoods  {
    private ModFoods() {}

    public static final FoodProperties RAW_BRAIN = new FoodProperties.Builder()
            .nutrition(1).saturationModifier(0.1f).build();
    public static final FoodProperties RAW_LIVER = new FoodProperties.Builder()
            .nutrition(1).saturationModifier(0.07f).build();
    public static final FoodProperties RAW_HEART = new FoodProperties.Builder()
            .nutrition(1).saturationModifier(0.12f).build();
    public static final FoodProperties ANDOUILLE_SAUSAGE = new FoodProperties.Builder()
            .nutrition(3).saturationModifier(0.15f).build();
    public static final FoodProperties GROUND_OFFAL = new FoodProperties.Builder()
            .nutrition(2).saturationModifier(0.1f).build();

    public static final FoodProperties COOKED_BRAIN = new FoodProperties.Builder()
            .nutrition(4).saturationModifier(0.25f).build();
    public static final FoodProperties COOKED_LIVER = new FoodProperties.Builder()
            .nutrition(8).saturationModifier(0.3f).build();
    public static final FoodProperties COOKED_HEART = new FoodProperties.Builder()
            .nutrition(7).saturationModifier(0.5f).build();
    public static final FoodProperties BONE_MARROW = new FoodProperties.Builder()
            .nutrition(5).saturationModifier(0.75f).build();
    public static final FoodProperties ROASTED_ANDOUILLE = new FoodProperties.Builder()
            .nutrition(7).saturationModifier(0.65f).build();
}
