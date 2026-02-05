package com.perigrine3.createcybernetics.client.skin;

import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.HumanoidArm;

import java.util.EnumSet;

public class SkinModifier {
    public enum HideVanilla {
        HAT, JACKET, LEFT_SLEEVE, RIGHT_SLEEVE, LEFT_PANTS, RIGHT_PANTS
    }

    private final ResourceLocation wideTexture;
    private final ResourceLocation slimTexture;
    private final int color;
    private final boolean hideVanillaLayers;
    private final EnumSet<HideVanilla> hideMask;
    private final EnumSet<HumanoidArm> replaceVanillaArms;
    private final boolean needsPlayerSkinUnderlay;
    private final boolean glint;

    public SkinModifier(ResourceLocation wideTexture, ResourceLocation slimTexture) {
        this(wideTexture, slimTexture, FastColor.ARGB32.color(255, 255, 255, 255),
                false, false, EnumSet.noneOf(HideVanilla.class), EnumSet.noneOf(HumanoidArm.class), false);
    }

    public SkinModifier(ResourceLocation wideTexture, ResourceLocation slimTexture, int color, boolean hideVanillaLayers) {
        this(wideTexture, slimTexture, color, false, hideVanillaLayers, EnumSet.noneOf(HideVanilla.class), EnumSet.noneOf(HumanoidArm.class), false);
    }

    public SkinModifier(ResourceLocation wideTexture, ResourceLocation slimTexture, int color, boolean hideVanillaLayers,
                        EnumSet<HideVanilla> hideMask) {
        this(wideTexture, slimTexture, color, false, hideVanillaLayers, hideMask, EnumSet.noneOf(HumanoidArm.class), false);
    }

    public SkinModifier(ResourceLocation wideTexture, ResourceLocation slimTexture, int color, boolean hideVanillaLayers,
                        EnumSet<HideVanilla> hideMask, EnumSet<HumanoidArm> replaceVanillaArms) {
        this(wideTexture, slimTexture, color, false, hideVanillaLayers, hideMask, replaceVanillaArms, false);
    }


    public SkinModifier(ResourceLocation wideTexture, ResourceLocation slimTexture, int color, boolean glint, boolean hideVanillaLayers,
                        EnumSet<HideVanilla> hideMask, EnumSet<HumanoidArm> replaceVanillaArms, boolean needsPlayerSkinUnderlay) {
        this.wideTexture = wideTexture;
        this.slimTexture = slimTexture;
        this.color = color;
        this.glint = glint;
        this.hideVanillaLayers = hideVanillaLayers;
        this.hideMask = (hideMask == null) ? EnumSet.noneOf(HideVanilla.class) : hideMask.clone();
        this.replaceVanillaArms = (replaceVanillaArms == null) ? EnumSet.noneOf(HumanoidArm.class) : replaceVanillaArms.clone();
        this.needsPlayerSkinUnderlay = needsPlayerSkinUnderlay;
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

    public EnumSet<HideVanilla> getHideMask() {
        return hideMask.clone();
    }

    public boolean replacesVanillaArm(HumanoidArm arm) {
        return replaceVanillaArms.contains(arm);
    }

    public boolean needsPlayerSkinUnderlay() {
        return needsPlayerSkinUnderlay;
    }

    public boolean hasGlint() {
        return glint;
    }
}
