package com.perigrine3.createcybernetics.common.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class ModDamageSources {
    private ModDamageSources() {}

    public static DamageSource cyberwareSurgery(Level level, @Nullable Entity direct, @Nullable Entity cause) {
        return level.damageSources().source(ModDamageTypes.SURGERY, direct, cause);
    }

    public static DamageSource cyberwareRejection(Level level, @Nullable Entity direct, @Nullable Entity cause) {
        return level.damageSources().source(ModDamageTypes.CYBERWARE_REJECTION, direct, cause);
    }

    public static DamageSource brainDamage(Level level, @Nullable Entity direct, @Nullable Entity cause) {
        return level.damageSources().source(ModDamageTypes.BRAIN_DAMAGE, direct, cause);
    }

    public static DamageSource heartAttack(Level level, @Nullable Entity direct, @Nullable Entity cause) {
        return level.damageSources().source(ModDamageTypes.HEART_ATTACK, direct, cause);
    }

    public static DamageSource liverFailure(Level level, @Nullable Entity direct, @Nullable Entity cause) {
        return level.damageSources().source(ModDamageTypes.LIVER_FAILURE, direct, cause);
    }

    public static DamageSource missingSkin(Level level, @Nullable Entity direct, @Nullable Entity cause) {
        return level.damageSources().source(ModDamageTypes.MISSING_SKIN, direct, cause);
    }

    public static DamageSource missingLungs(Level level, @Nullable Entity direct, @Nullable Entity cause) {
        return level.damageSources().source(ModDamageTypes.MISSING_LUNGS, direct, cause);
    }

    public static DamageSource boneless(Level level, @Nullable Entity direct, @Nullable Entity cause) {
        return level.damageSources().source(ModDamageTypes.BONELESS, direct, cause);
    }

    public static DamageSource missingMuscle(Level level, @Nullable Entity direct, @Nullable Entity cause) {
        return level.damageSources().source(ModDamageTypes.MISSING_MUSCLE, direct, cause);
    }

    public static DamageSource davidsDemise(Level level, @Nullable Entity direct, @Nullable Entity cause) {
        return level.damageSources().source(ModDamageTypes.DAVIDS_DEMISE, direct, cause);
    }

}
