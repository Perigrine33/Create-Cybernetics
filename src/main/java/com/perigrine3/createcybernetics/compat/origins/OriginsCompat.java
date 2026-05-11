package com.perigrine3.createcybernetics.compat.origins;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

public final class OriginsCompat {
    private static final String ORIGINS_MODID = "origins-neoforge";

    private OriginsCompat() {}

    public static boolean isLoaded() {
        return ModList.get().isLoaded(ORIGINS_MODID);
    }

    public static ResourceLocation getPrimaryOrigin(Player player) {
        if (player == null) {
            return null;
        }

        if (!isLoaded()) {
            return null;
        }

        return OriginsReflectionHooks.getPrimaryOrigin(player);
    }
}