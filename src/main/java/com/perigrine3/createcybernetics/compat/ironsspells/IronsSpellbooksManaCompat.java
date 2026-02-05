package com.perigrine3.createcybernetics.compat.ironsspells;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.ModList;

public final class IronsSpellbooksManaCompat {
    private IronsSpellbooksManaCompat() {}

    public static final String MODID = "irons_spellbooks";

    public static boolean isLoaded() {
        return ModList.get().isLoaded(MODID);
    }

    public static boolean drainMana(LivingEntity target, float amount) {
        if (!isLoaded()) return false;
        if (target == null || amount <= 0) return false;

        MagicData data = MagicData.getPlayerMagicData(target);
        if (data == null) return false;

        if (target instanceof ServerPlayer sp) {
            data.setServerPlayer(sp);
        }

        float oldMana = data.getMana();
        float newMana = Math.max(0.0F, oldMana - amount);
        if (newMana == oldMana) return false;

        data.setMana(newMana);
        return true;
    }
}
