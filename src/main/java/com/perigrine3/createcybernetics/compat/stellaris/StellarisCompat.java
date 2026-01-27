package com.perigrine3.createcybernetics.compat.stellaris;

import com.perigrine3.createcybernetics.compat.ModCompats;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public final class StellarisCompat {
    private StellarisCompat() {}

    public static final String MODID = "stellaris";

    public static boolean isLoaded() {
        return ModCompats.isInstalled(MODID);
    }

    public static boolean isStellarisLevel(Level level) {
        if (level == null) return false;
        ResourceLocation dim = level.dimension().location();
        return MODID.equals(dim.getNamespace());
    }
}
