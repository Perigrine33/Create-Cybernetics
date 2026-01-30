package com.perigrine3.createcybernetics.client;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.armortrim.TrimMaterial;

import java.util.Map;

public final class TrimColorPresets {
    private TrimColorPresets() {}

    // ARGB (0xAARRGGBB)
    private static final Map<ResourceLocation, Integer> BY_ID = Map.ofEntries(
            Map.entry(ResourceLocation.withDefaultNamespace("amethyst"), 0xFF9A5CC6),
            Map.entry(ResourceLocation.withDefaultNamespace("copper"),   0xFFB87333),
            Map.entry(ResourceLocation.withDefaultNamespace("diamond"),  0xFF3DD9D6),
            Map.entry(ResourceLocation.withDefaultNamespace("emerald"),  0xFF17DD62),
            Map.entry(ResourceLocation.withDefaultNamespace("gold"),     0xFFF2D34F),
            Map.entry(ResourceLocation.withDefaultNamespace("iron"),     0xFFD8D8D8),
            Map.entry(ResourceLocation.withDefaultNamespace("lapis"),    0xFF2A4DD3),
            Map.entry(ResourceLocation.withDefaultNamespace("netherite"),0xFF4A3F42),
            Map.entry(ResourceLocation.withDefaultNamespace("quartz"),   0xFFEAE3D8),
            Map.entry(ResourceLocation.withDefaultNamespace("redstone"), 0xFFB00000));

    public static int colorFor(Holder<TrimMaterial> material) {
        if (material == null) return 0xFFFFFFFF;

        ResourceLocation id = material.unwrapKey().map(k -> k.location()).orElse(null);
        if (id == null) return 0xFFFFFFFF;

        return BY_ID.getOrDefault(id, 0xFFFFFFFF);
    }
}
