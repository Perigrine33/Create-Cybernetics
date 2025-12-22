package com.perigrine3.createcybernetics.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

public class SkinModifier {
    private final ResourceLocation texture;
    private final int color;
    private final boolean hideVanillaLayers;

    public SkinModifier(ResourceLocation texture) {
        this(texture, FastColor.ARGB32.color(255, 255, 255, 255), true);
    }

    public SkinModifier(ResourceLocation texture, int color, boolean hideVanillaLayers) {
        this.texture = texture;
        this.color = color;
        this.hideVanillaLayers = hideVanillaLayers;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public int getColor() {
        return color;
    }

    public boolean shouldHideVanillaLayers() {
        return hideVanillaLayers;
    }
}