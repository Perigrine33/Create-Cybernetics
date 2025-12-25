package com.perigrine3.createcybernetics.client;

import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

public class SkinModifier {
    private final ResourceLocation wideTexture;
    private final ResourceLocation slimTexture;
    private final int color;
    private final boolean hideVanillaLayers;

    public SkinModifier(ResourceLocation wideTexture, ResourceLocation slimTexture) {
        this(wideTexture, slimTexture, FastColor.ARGB32.color(255, 255, 255, 255), true);
    }

    public SkinModifier(ResourceLocation wideTexture, ResourceLocation slimTexture, int color, boolean hideVanillaLayers) {
        this.wideTexture = wideTexture;
        this.slimTexture = slimTexture;
        this.color = color;
        this.hideVanillaLayers = hideVanillaLayers;
    }

    public ResourceLocation getTexture(PlayerSkin.Model modelType) {
        return modelType == PlayerSkin.Model.SLIM ? slimTexture : wideTexture;
    }

    public int getColor() {
        return color;
    }

    public boolean shouldHideVanillaLayers() {
        return hideVanillaLayers;
    }
}