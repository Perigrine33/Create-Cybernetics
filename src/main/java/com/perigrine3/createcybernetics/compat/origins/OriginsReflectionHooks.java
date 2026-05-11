package com.perigrine3.createcybernetics.compat.origins;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public final class OriginsReflectionHooks {
    private OriginsReflectionHooks() {}

    public static ResourceLocation getPrimaryOrigin(Player player) {
        return null;
    }
}