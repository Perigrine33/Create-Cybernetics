package com.perigrine3.createcybernetics.compat.bettercombat;

import net.neoforged.fml.ModList;

public final class BetterCombatCompat {
    private BetterCombatCompat() {}

    // Better Combat's modid is typically "bettercombat"
    public static final boolean LOADED = ModList.get().isLoaded("bettercombat");
}
