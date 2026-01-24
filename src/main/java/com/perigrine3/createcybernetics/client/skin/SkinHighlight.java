package com.perigrine3.createcybernetics.client.skin;

import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

public final class SkinHighlight {

    private final ResourceLocation wideTexture;
    private final ResourceLocation slimTexture;
    private final int color;
    private final boolean emissive;
    private final boolean tintOnEmissive;

    public SkinHighlight(ResourceLocation wideTexture, ResourceLocation slimTexture) {
        this(wideTexture, slimTexture, FastColor.ARGB32.color(255, 255, 255, 255), false, false);
    }

    public SkinHighlight(ResourceLocation wideTexture, ResourceLocation slimTexture, int color, boolean emissive) {
        this(wideTexture, slimTexture, color, emissive, false);
    }

    public SkinHighlight(ResourceLocation wideTexture, ResourceLocation slimTexture, int color, boolean emissive, boolean tintOnEmissive) {
        this.wideTexture = wideTexture;
        this.slimTexture = slimTexture;
        this.color = color;
        this.emissive = emissive;
        this.tintOnEmissive = tintOnEmissive;
    }

    public ResourceLocation getTexture(PlayerSkin.Model modelType) {
        return modelType == PlayerSkin.Model.SLIM ? slimTexture : wideTexture;
    }

    public int getColor() {
        return color;
    }

    public boolean isEmissive() {
        return emissive;
    }

    public boolean tintOnEmissive() {
        return tintOnEmissive;
    }
}
