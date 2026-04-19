package com.perigrine3.createcybernetics.compat.ironsspells;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.ModList;

import java.lang.reflect.Method;

public final class IronsSpellbooksManaCompat {
    private IronsSpellbooksManaCompat() {}

    public static final String MODID = "irons_spellbooks";
    private static final boolean LOADED = ModList.get().isLoaded(MODID);

    private static final String MAGIC_DATA_CLASS = "io.redspace.ironsspellbooks.api.magic.MagicData";

    private static boolean lookedUp;
    private static Class<?> magicDataClass;
    private static Method getPlayerMagicDataMethod;
    private static Method setServerPlayerMethod;
    private static Method getManaMethod;
    private static Method setManaMethod;

    public static boolean isLoaded() {
        return LOADED;
    }

    private static boolean resolve() {
        if (!LOADED) return false;
        if (lookedUp) {
            return magicDataClass != null
                    && getPlayerMagicDataMethod != null
                    && getManaMethod != null
                    && setManaMethod != null;
        }

        lookedUp = true;

        try {
            magicDataClass = Class.forName(MAGIC_DATA_CLASS);

            getPlayerMagicDataMethod = magicDataClass.getMethod("getPlayerMagicData", LivingEntity.class);
            getManaMethod = magicDataClass.getMethod("getMana");
            setManaMethod = magicDataClass.getMethod("setMana", float.class);

            try {
                setServerPlayerMethod = magicDataClass.getMethod("setServerPlayer", ServerPlayer.class);
            } catch (NoSuchMethodException ignored) {
                setServerPlayerMethod = null;
            }

            return true;
        } catch (Throwable ignored) {
            magicDataClass = null;
            getPlayerMagicDataMethod = null;
            setServerPlayerMethod = null;
            getManaMethod = null;
            setManaMethod = null;
            return false;
        }
    }

    public static boolean drainMana(LivingEntity target, float amount) {
        if (target == null || amount <= 0f) return false;
        if (!resolve()) return false;

        try {
            Object data = getPlayerMagicDataMethod.invoke(null, target);
            if (data == null) return false;

            if (target instanceof ServerPlayer sp && setServerPlayerMethod != null) {
                setServerPlayerMethod.invoke(data, sp);
            }

            float oldMana = ((Number) getManaMethod.invoke(data)).floatValue();
            float newMana = Math.max(0.0F, oldMana - amount);
            if (Float.compare(newMana, oldMana) == 0) return false;

            setManaMethod.invoke(data, newMana);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}