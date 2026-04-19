package com.perigrine3.createcybernetics.compat.ironsspells;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class IronsSpellbooksCompat {
    private IronsSpellbooksCompat() {}

    public static final String MODID = "irons_spellbooks";
    private static final boolean LOADED = ModList.get().isLoaded(MODID);

    private static final Map<ResourceLocation, Optional<Holder<Attribute>>> ATTRIBUTE_CACHE = new ConcurrentHashMap<>();

    // DamageTypes
    public static final ResourceLocation DT_LIGHTNING_MAGIC = rl("lightning_magic");
    public static final ResourceKey<DamageType> DTK_LIGHTNING_MAGIC =
            ResourceKey.create(Registries.DAMAGE_TYPE, DT_LIGHTNING_MAGIC);

    // Core attributes
    public static final ResourceLocation ATTR_MAX_MANA            = rl("max_mana");
    public static final ResourceLocation ATTR_MANA_REGEN          = rl("mana_regen");
    public static final ResourceLocation ATTR_COOLDOWN_REDUCTION  = rl("cooldown_reduction");
    public static final ResourceLocation ATTR_SPELL_POWER         = rl("spell_power");
    public static final ResourceLocation ATTR_SPELL_RESIST        = rl("spell_resist");
    public static final ResourceLocation ATTR_CAST_TIME_REDUCTION = rl("cast_time_reduction");
    public static final ResourceLocation ATTR_SUMMON_DAMAGE       = rl("summon_damage");
    public static final ResourceLocation ATTR_CASTING_MOVESPEED   = rl("casting_movespeed");

    // Resist (schools)
    public static final ResourceLocation ATTR_FIRE_MAGIC_RESIST      = rl("fire_magic_resist");
    public static final ResourceLocation ATTR_ICE_MAGIC_RESIST       = rl("ice_magic_resist");
    public static final ResourceLocation ATTR_LIGHTNING_MAGIC_RESIST = rl("lightning_magic_resist");
    public static final ResourceLocation ATTR_HOLY_MAGIC_RESIST      = rl("holy_magic_resist");
    public static final ResourceLocation ATTR_ENDER_MAGIC_RESIST     = rl("ender_magic_resist");
    public static final ResourceLocation ATTR_BLOOD_MAGIC_RESIST     = rl("blood_magic_resist");
    public static final ResourceLocation ATTR_EVOCATION_MAGIC_RESIST = rl("evocation_magic_resist");
    public static final ResourceLocation ATTR_NATURE_MAGIC_RESIST    = rl("nature_magic_resist");
    public static final ResourceLocation ATTR_ELDRITCH_MAGIC_RESIST  = rl("eldritch_magic_resist");

    // Power (schools)
    public static final ResourceLocation ATTR_FIRE_SPELL_POWER      = rl("fire_spell_power");
    public static final ResourceLocation ATTR_ICE_SPELL_POWER       = rl("ice_spell_power");
    public static final ResourceLocation ATTR_LIGHTNING_SPELL_POWER = rl("lightning_spell_power");
    public static final ResourceLocation ATTR_HOLY_SPELL_POWER      = rl("holy_spell_power");
    public static final ResourceLocation ATTR_ENDER_SPELL_POWER     = rl("ender_spell_power");
    public static final ResourceLocation ATTR_BLOOD_SPELL_POWER     = rl("blood_spell_power");
    public static final ResourceLocation ATTR_EVOCATION_SPELL_POWER = rl("evocation_spell_power");
    public static final ResourceLocation ATTR_NATURE_SPELL_POWER    = rl("nature_spell_power");
    public static final ResourceLocation ATTR_ELDRITCH_SPELL_POWER  = rl("eldritch_spell_power");

    public static boolean isLoaded() {
        return LOADED;
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    @Nullable
    public static Holder<Attribute> getAttributeHolder(ResourceLocation id) {
        if (!LOADED) return null;
        return ATTRIBUTE_CACHE.computeIfAbsent(id, key ->
                BuiltInRegistries.ATTRIBUTE.getOptional(key)
                        .map(BuiltInRegistries.ATTRIBUTE::wrapAsHolder)
        ).orElse(null);
    }

    public static Optional<Holder<DamageType>> resolveDamageTypeHolder(RegistryAccess access, ResourceLocation id) {
        if (!LOADED) return Optional.empty();

        ResourceKey<DamageType> key = ResourceKey.create(Registries.DAMAGE_TYPE, id);
        return access.registry(Registries.DAMAGE_TYPE).flatMap(reg -> reg.getHolder(key));
    }

    public static DamageSource lightningMagic(Level level, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
        if (!LOADED) {
            if (directEntity != null || causingEntity != null) {
                return level.damageSources().source(DamageTypes.LIGHTNING_BOLT, directEntity, causingEntity);
            }
            return level.damageSources().source(DamageTypes.LIGHTNING_BOLT);
        }

        boolean hasIronsLightning = resolveDamageTypeHolder(level.registryAccess(), DT_LIGHTNING_MAGIC).isPresent();
        ResourceKey<DamageType> key = hasIronsLightning ? DTK_LIGHTNING_MAGIC : DamageTypes.LIGHTNING_BOLT;

        if (directEntity != null || causingEntity != null) {
            return level.damageSources().source(key, directEntity, causingEntity);
        }
        return level.damageSources().source(key);
    }

    public static boolean hurtLightningMagic(LivingEntity target, float amount,
                                             @Nullable Entity directEntity, @Nullable Entity causingEntity) {
        return target.hurt(lightningMagic(target.level(), directEntity, causingEntity), amount);
    }

    public static boolean isLightningMagic(DamageSource src) {
        if (!LOADED || src == null) return false;
        return src.typeHolder().unwrapKey().filter(DTK_LIGHTNING_MAGIC::equals).isPresent();
    }

    public static boolean isAnyLightning(DamageSource src) {
        if (src == null) return false;
        return src.is(DamageTypes.LIGHTNING_BOLT) || isLightningMagic(src);
    }
}