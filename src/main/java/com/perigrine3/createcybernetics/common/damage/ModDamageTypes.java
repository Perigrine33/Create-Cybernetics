package com.perigrine3.createcybernetics.common.damage;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public final class ModDamageTypes {
    private ModDamageTypes() {}

    public static final ResourceKey<DamageType> SURGERY =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "surgery"));
    public static final ResourceKey<DamageType> CYBERWARE_REJECTION =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware_rejection"));
    public static final ResourceKey<DamageType> BRAIN_DAMAGE =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "brain_damage"));
    public static final ResourceKey<DamageType> HEART_ATTACK =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "heart_attack"));
    public static final ResourceKey<DamageType> LIVER_FAILURE =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "liver_failure"));
    public static final ResourceKey<DamageType> MISSING_SKIN =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "missing_skin"));
    public static final ResourceKey<DamageType> MISSING_LUNGS =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "missing_lungs"));
    public static final ResourceKey<DamageType> BONELESS =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "boneless"));
    public static final ResourceKey<DamageType> MISSING_MUSCLE =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "missing_muscle"));
    public static final ResourceKey<DamageType> DAVIDS_DEMISE =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "davids_demise"));
}
