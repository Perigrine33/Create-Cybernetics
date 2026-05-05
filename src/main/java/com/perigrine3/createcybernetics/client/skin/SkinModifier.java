package com.perigrine3.createcybernetics.client.skin;

import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.HumanoidArm;

import java.util.EnumSet;

public class SkinModifier {

    public enum HideVanilla {
        HAT,
        JACKET,
        LEFT_SLEEVE,
        RIGHT_SLEEVE,
        LEFT_PANTS,
        RIGHT_PANTS
    }

    public enum OverlayPart {
        HEAD,
        HAT,
        BODY,
        JACKET,
        LEFT_ARM,
        LEFT_SLEEVE,
        RIGHT_ARM,
        RIGHT_SLEEVE,
        LEFT_LEG,
        LEFT_PANTS,
        RIGHT_LEG,
        RIGHT_PANTS
    }

    private final ResourceLocation wideTexture;
    private final ResourceLocation slimTexture;
    private final int color;
    private final EnumSet<HideVanilla> hideMask;
    private final EnumSet<OverlayPart> overlayParts;
    private final EnumSet<HumanoidArm> replaceVanillaArms;
    private final boolean needsPlayerSkinUnderlay;
    private final boolean glint;

    public SkinModifier(ResourceLocation wideTexture, ResourceLocation slimTexture) {
        this(
                wideTexture,
                slimTexture,
                FastColor.ARGB32.color(255, 255, 255, 255),
                false,
                false,
                EnumSet.noneOf(HideVanilla.class),
                EnumSet.allOf(OverlayPart.class),
                EnumSet.noneOf(HumanoidArm.class),
                false
        );
    }

    public SkinModifier(ResourceLocation wideTexture, ResourceLocation slimTexture, int color, boolean hideVanillaLayers) {
        this(
                wideTexture,
                slimTexture,
                color,
                false,
                hideVanillaLayers,
                EnumSet.noneOf(HideVanilla.class),
                EnumSet.allOf(OverlayPart.class),
                EnumSet.noneOf(HumanoidArm.class),
                false
        );
    }

    public SkinModifier(ResourceLocation wideTexture, ResourceLocation slimTexture, int color, boolean hideVanillaLayers,
                        EnumSet<HideVanilla> hideMask) {
        this(
                wideTexture,
                slimTexture,
                color,
                false,
                hideVanillaLayers,
                hideMask,
                EnumSet.allOf(OverlayPart.class),
                EnumSet.noneOf(HumanoidArm.class),
                false
        );
    }

    public SkinModifier(ResourceLocation wideTexture, ResourceLocation slimTexture, int color, boolean hideVanillaLayers,
                        EnumSet<HideVanilla> hideMask, EnumSet<HumanoidArm> replaceVanillaArms) {
        this(
                wideTexture,
                slimTexture,
                color,
                false,
                hideVanillaLayers,
                hideMask,
                EnumSet.allOf(OverlayPart.class),
                replaceVanillaArms,
                false
        );
    }

    public SkinModifier(ResourceLocation wideTexture, ResourceLocation slimTexture, int color, boolean glint,
                        boolean hideVanillaLayers, EnumSet<HideVanilla> hideMask,
                        EnumSet<HumanoidArm> replaceVanillaArms, boolean needsPlayerSkinUnderlay) {
        this(
                wideTexture,
                slimTexture,
                color,
                glint,
                hideVanillaLayers,
                hideMask,
                EnumSet.allOf(OverlayPart.class),
                replaceVanillaArms,
                needsPlayerSkinUnderlay
        );
    }

    public SkinModifier(ResourceLocation wideTexture, ResourceLocation slimTexture, int color, boolean glint,
                        boolean hideVanillaLayers, EnumSet<HideVanilla> hideMask,
                        EnumSet<OverlayPart> overlayParts,
                        EnumSet<HumanoidArm> replaceVanillaArms, boolean needsPlayerSkinUnderlay) {
        this.wideTexture = wideTexture;
        this.slimTexture = slimTexture;
        this.color = color;
        this.glint = glint;
        this.hideMask = normalizeHideMask(hideVanillaLayers, hideMask);
        this.overlayParts = normalizeOverlayParts(overlayParts);
        this.replaceVanillaArms = normalizeArms(replaceVanillaArms);
        this.needsPlayerSkinUnderlay = needsPlayerSkinUnderlay;
    }

    public static SkinModifier fullBody(ResourceLocation wideTexture, ResourceLocation slimTexture, int color) {
        return new SkinModifier(
                wideTexture,
                slimTexture,
                color,
                false,
                true,
                EnumSet.allOf(HideVanilla.class),
                EnumSet.allOf(OverlayPart.class),
                EnumSet.noneOf(HumanoidArm.class),
                false
        );
    }

    public static SkinModifier head(ResourceLocation wideTexture, ResourceLocation slimTexture, int color) {
        return new SkinModifier(
                wideTexture,
                slimTexture,
                color,
                false,
                false,
                EnumSet.of(HideVanilla.HAT),
                EnumSet.of(OverlayPart.HEAD, OverlayPart.HAT),
                EnumSet.noneOf(HumanoidArm.class),
                false
        );
    }

    public static SkinModifier body(ResourceLocation wideTexture, ResourceLocation slimTexture, int color) {
        return new SkinModifier(
                wideTexture,
                slimTexture,
                color,
                false,
                false,
                EnumSet.of(HideVanilla.JACKET),
                EnumSet.of(OverlayPart.BODY, OverlayPart.JACKET),
                EnumSet.noneOf(HumanoidArm.class),
                false
        );
    }

    public static SkinModifier leftArm(ResourceLocation wideTexture, ResourceLocation slimTexture, int color) {
        return new SkinModifier(
                wideTexture,
                slimTexture,
                color,
                false,
                false,
                EnumSet.of(HideVanilla.LEFT_SLEEVE),
                EnumSet.of(
                        OverlayPart.BODY,
                        OverlayPart.JACKET,
                        OverlayPart.LEFT_ARM,
                        OverlayPart.LEFT_SLEEVE
                ),
                EnumSet.of(HumanoidArm.LEFT),
                false
        );
    }

    public static SkinModifier rightArm(ResourceLocation wideTexture, ResourceLocation slimTexture, int color) {
        return new SkinModifier(
                wideTexture,
                slimTexture,
                color,
                false,
                false,
                EnumSet.of(HideVanilla.RIGHT_SLEEVE),
                EnumSet.of(
                        OverlayPart.BODY,
                        OverlayPart.JACKET,
                        OverlayPart.RIGHT_ARM,
                        OverlayPart.RIGHT_SLEEVE
                ),
                EnumSet.of(HumanoidArm.RIGHT),
                false
        );
    }

    public static SkinModifier leftLeg(ResourceLocation wideTexture, ResourceLocation slimTexture, int color) {
        return new SkinModifier(
                wideTexture,
                slimTexture,
                color,
                false,
                false,
                EnumSet.of(HideVanilla.LEFT_PANTS),
                EnumSet.of(OverlayPart.LEFT_LEG, OverlayPart.LEFT_PANTS),
                EnumSet.noneOf(HumanoidArm.class),
                false
        );
    }

    public static SkinModifier rightLeg(ResourceLocation wideTexture, ResourceLocation slimTexture, int color) {
        return new SkinModifier(
                wideTexture,
                slimTexture,
                color,
                false,
                false,
                EnumSet.of(HideVanilla.RIGHT_PANTS),
                EnumSet.of(OverlayPart.RIGHT_LEG, OverlayPart.RIGHT_PANTS),
                EnumSet.noneOf(HumanoidArm.class),
                false
        );
    }

    public static SkinModifier overlayOnly(ResourceLocation wideTexture, ResourceLocation slimTexture, int color,
                                           EnumSet<OverlayPart> overlayParts) {
        return new SkinModifier(
                wideTexture,
                slimTexture,
                color,
                false,
                false,
                EnumSet.noneOf(HideVanilla.class),
                overlayParts,
                EnumSet.noneOf(HumanoidArm.class),
                false
        );
    }

    public static SkinModifier custom(ResourceLocation wideTexture, ResourceLocation slimTexture, int color,
                                      EnumSet<HideVanilla> hideMask,
                                      EnumSet<OverlayPart> overlayParts) {
        return new SkinModifier(
                wideTexture,
                slimTexture,
                color,
                false,
                false,
                hideMask,
                overlayParts,
                EnumSet.noneOf(HumanoidArm.class),
                false
        );
    }

    public static SkinModifier custom(ResourceLocation wideTexture, ResourceLocation slimTexture, int color,
                                      EnumSet<HideVanilla> hideMask,
                                      EnumSet<OverlayPart> overlayParts,
                                      EnumSet<HumanoidArm> replaceVanillaArms) {
        return new SkinModifier(
                wideTexture,
                slimTexture,
                color,
                false,
                false,
                hideMask,
                overlayParts,
                replaceVanillaArms,
                false
        );
    }

    public ResourceLocation getTexture(PlayerSkin.Model modelType) {
        return modelType == PlayerSkin.Model.SLIM ? slimTexture : wideTexture;
    }

    public int getColor() {
        return color;
    }

    public boolean shouldHideVanillaLayers() {
        return !hideMask.isEmpty();
    }

    public EnumSet<HideVanilla> getHideMask() {
        return hideMask.clone();
    }

    public EnumSet<OverlayPart> getOverlayParts() {
        return overlayParts.clone();
    }

    public boolean rendersOverlayPart(OverlayPart part) {
        return part != null && overlayParts.contains(part);
    }

    public boolean replacesVanillaArm(HumanoidArm arm) {
        return arm != null && replaceVanillaArms.contains(arm);
    }

    public boolean needsPlayerSkinUnderlay() {
        return needsPlayerSkinUnderlay;
    }

    public boolean hasGlint() {
        return glint;
    }

    private static EnumSet<HideVanilla> normalizeHideMask(boolean hideVanillaLayers, EnumSet<HideVanilla> hideMask) {
        if (hideMask != null && !hideMask.isEmpty()) {
            return hideMask.clone();
        }

        if (hideVanillaLayers) {
            return EnumSet.allOf(HideVanilla.class);
        }

        return EnumSet.noneOf(HideVanilla.class);
    }

    private static EnumSet<OverlayPart> normalizeOverlayParts(EnumSet<OverlayPart> overlayParts) {
        if (overlayParts == null || overlayParts.isEmpty()) {
            return EnumSet.allOf(OverlayPart.class);
        }

        return overlayParts.clone();
    }

    private static EnumSet<HumanoidArm> normalizeArms(EnumSet<HumanoidArm> arms) {
        if (arms == null || arms.isEmpty()) {
            return EnumSet.noneOf(HumanoidArm.class);
        }

        return arms.clone();
    }
}