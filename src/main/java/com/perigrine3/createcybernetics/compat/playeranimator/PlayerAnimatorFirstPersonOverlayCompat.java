package com.perigrine3.createcybernetics.compat.playeranimator;

import com.perigrine3.createcybernetics.client.skin.SkinHighlight;
import com.perigrine3.createcybernetics.client.skin.SkinModifier;
import com.perigrine3.createcybernetics.client.skin.SkinModifierManager;
import com.perigrine3.createcybernetics.client.skin.SkinModifierState;
import com.perigrine3.createcybernetics.client.skin.SkinRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Method;

public final class PlayerAnimatorFirstPersonOverlayCompat {

    private static final String FIRST_PERSON_MODE_CLASS =
            "dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode";

    private static Method isFirstPersonPassMethod;
    private static boolean reflectionFailed;

    private PlayerAnimatorFirstPersonOverlayCompat() {}

    public static boolean shouldRenderFirstPersonPlayerOverlay(AbstractClientPlayer player) {
        if (player == null) return false;
        if (Minecraft.getInstance().getCameraEntity() != player) return false;
        return isFirstPersonPass();
    }

    public static void renderFirstPersonPlayerOverlays(
            AbstractClientPlayer player,
            PlayerModel<AbstractClientPlayer> model,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        if (player == null || model == null || poseStack == null || buffer == null) return;
        if (!shouldRenderFirstPersonPlayerOverlay(player)) return;

        SkinModifierState state = SkinModifierManager.getPlayerSkinState(player);
        if (state == null) return;
        if (!state.hasModifiers() && !state.hasHighlights()) return;

        PlayerSkin.Model modelType = player.getSkin().model();

        renderModifiers(state, modelType, model, poseStack, buffer, packedLight);
        renderHighlights(state, modelType, model, poseStack, buffer, packedLight);
    }

    private static void renderModifiers(
            SkinModifierState state,
            PlayerSkin.Model modelType,
            PlayerModel<AbstractClientPlayer> model,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        if (!state.hasModifiers()) return;

        for (SkinModifier modifier : state.getModifiers()) {
            if (modifier == null) continue;

            ResourceLocation texture = modifier.getTexture(modelType);
            int color = modifier.getColor();

            var vc = buffer.getBuffer(RenderType.entityTranslucent(texture));
            model.renderToBuffer(
                    poseStack,
                    vc,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    color
            );

            if (modifier.hasGlint()) {
                var glintVc = buffer.getBuffer(RenderType.entityGlint());
                model.renderToBuffer(
                        poseStack,
                        glintVc,
                        packedLight,
                        OverlayTexture.NO_OVERLAY,
                        0xFFFFFFFF
                );
            }
        }
    }

    private static void renderHighlights(
            SkinModifierState state,
            PlayerSkin.Model modelType,
            PlayerModel<AbstractClientPlayer> model,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        if (!state.hasHighlights()) return;

        for (SkinHighlight highlight : state.getHighlights()) {
            if (highlight == null) continue;

            ResourceLocation texture = highlight.getTexture(modelType);

            RenderType renderType;
            int light;
            int color;

            if (highlight.isEmissive()) {
                light = 0x00F000F0;

                if (highlight.tintOnEmissive()) {
                    renderType = SkinRenderTypes.emissiveTinted(texture);
                    color = highlight.getColor();
                } else {
                    renderType = RenderType.entityTranslucent(texture);
                    color = 0xFFFFFFFF;
                }
            } else {
                light = packedLight;
                renderType = RenderType.entityTranslucent(texture);
                color = highlight.getColor();
            }

            var vc = buffer.getBuffer(renderType);
            model.renderToBuffer(
                    poseStack,
                    vc,
                    light,
                    OverlayTexture.NO_OVERLAY,
                    color
            );
        }
    }

    private static boolean isFirstPersonPass() {
        if (reflectionFailed) return false;

        try {
            if (isFirstPersonPassMethod == null) {
                Class<?> modeClass = Class.forName(
                        FIRST_PERSON_MODE_CLASS,
                        false,
                        PlayerAnimatorFirstPersonOverlayCompat.class.getClassLoader()
                );

                isFirstPersonPassMethod = modeClass.getMethod("isFirstPersonPass");
                isFirstPersonPassMethod.setAccessible(true);
            }

            Object result = isFirstPersonPassMethod.invoke(null);
            return result instanceof Boolean value && value;
        } catch (Throwable ignored) {
            reflectionFailed = true;
            return false;
        }
    }
}