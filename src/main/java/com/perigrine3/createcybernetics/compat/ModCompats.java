package com.perigrine3.createcybernetics.compat;

import net.neoforged.fml.ModList;

public final class ModCompats {
    private ModCompats() {}

    public static boolean isInstalled(String modid) {
        return ModList.get().isLoaded(modid);
    }
}
