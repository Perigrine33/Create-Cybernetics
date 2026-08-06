package com.perigrine3.createcybernetics.client.render.rejection;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.perigrine3.createcybernetics.ConfigValues;
import com.perigrine3.createcybernetics.client.skin.*;
import com.perigrine3.createcybernetics.effect.ModEffects;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.PlayerModelPart;

public final class CyberwareRejectionHeadLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final Minecraft MC = Minecraft.getInstance();
    private static final float TAU = Mth.TWO_PI;

    public CyberwareRejectionHeadLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (ConfigValues.EPILEPSY_MODE) return;
        if (!shouldRender(player)) return;

        MobEffectInstance rejection = player.getEffect(ModEffects.CYBERWARE_REJECTION);
        if (rejection == null) return;

        int level = Mth.clamp(rejection.getAmplifier() + 1, 1, 3);
        boolean fugue = player.hasEffect(ModEffects.CYBERPSYCHOSIS_FUGUE);
        float time = player.tickCount + partialTick;
        float intensity = getIntensity(level, fugue);

        PlayerModel<AbstractClientPlayer> model = getParentModel();
        boolean renderHat = player.isModelPartShown(PlayerModelPart.HAT);

        SkinModifierState skinState = SkinModifierManager.getPlayerSkinState(player);

        renderContinuousAberration(poseStack, bufferSource, model, renderHat, packedLight, player, skinState, time, level, fugue);
        renderSpikeGlitch(poseStack, bufferSource, model, renderHat, packedLight, player, skinState, time, level, intensity, fugue);
    }

    private static void renderContinuousAberration(PoseStack poseStack, MultiBufferSource bufferSource, PlayerModel<AbstractClientPlayer> model, boolean renderHat, int packedLight, AbstractClientPlayer player, SkinModifierState skinState, float time, int level, boolean fugue) {        float slowWave = Mth.sin(time * 0.24F + player.getId() * 0.37F);
        float fastWave = Mth.sin(time * 2.65F + player.getId() * 1.91F);
        float verticalWave = Mth.sin(time * 4.73F + player.getId() * 0.83F);

        float baseSplit = switch (level) {
            case 1 -> 0.0045F;
            case 2 -> 0.0090F;
            default -> 0.0150F;
        };

        if (fugue) baseSplit *= 1.25F;

        float horizontalJitter = fastWave * baseSplit * 0.28F;
        float verticalJitter = verticalWave * baseSplit * 0.08F;

        float redAlpha = switch (level) {
            case 1 -> 0.24F;
            case 2 -> 0.40F;
            default -> 0.56F;
        };

        float cyanAlpha = switch (level) {
            case 1 -> 0.22F;
            case 2 -> 0.38F;
            default -> 0.54F;
        };

        float pulse = 0.5F + 0.5F * slowWave;

        float centerAlpha = switch (level) {
            case 1 -> 0.08F + pulse * 0.04F;
            case 2 -> 0.14F + pulse * 0.07F;
            default -> 0.20F + pulse * 0.10F;
        };

        if (fugue) {
            redAlpha += 0.10F;
            cyanAlpha += 0.10F;
            centerAlpha += 0.08F;
        }

        renderHeadCopy(poseStack, bufferSource, player, skinState, model, renderHat, packedLight, horizontalJitter + baseSplit, verticalJitter, 0.0F, color(redAlpha, 1.0F, 0.06F, 0.13F));
        renderHeadCopy(poseStack, bufferSource, player, skinState, model, renderHat, packedLight, horizontalJitter - baseSplit, verticalJitter, 0.0F, color(cyanAlpha, 0.04F, 0.95F, 1.0F));
        renderHeadCopy(poseStack, bufferSource, player, skinState, model, renderHat, packedLight, horizontalJitter, verticalJitter, -0.0015F, color(centerAlpha, 0.70F, 0.22F, 0.90F));
    }

    private static void renderSpikeGlitch(PoseStack poseStack, MultiBufferSource bufferSource, PlayerModel<AbstractClientPlayer> model, boolean renderHat, int packedLight, AbstractClientPlayer player, SkinModifierState skinState, float time, int level, float intensity, boolean fugue) {        int burstPhase = Mth.floor(time / getSpikePhaseLength(level, fugue));
        int rapidPhase = Mth.floor(time * getRapidPhaseSpeed(level, fugue));

        float burstRoll = hash(player.getId() + 419, burstPhase);
        float rapidRoll = hash(player.getId() + 811, rapidPhase);
        boolean spikeBurst = fugue || burstRoll > getSpikeThreshold(level) || rapidRoll > getRapidSpikeThreshold(level);

        if (!spikeBurst) return;

        float primaryDistance = switch (level) {
            case 1 -> 0.020F;
            case 2 -> 0.043F;
            default -> 0.078F;
        };

        float secondaryDistance = switch (level) {
            case 1 -> 0.012F;
            case 2 -> 0.030F;
            default -> 0.056F;
        };

        if (fugue) {
            primaryDistance *= 1.30F;
            secondaryDistance *= 1.25F;
        }

        primaryDistance *= 0.72F + hash(player.getId() + 271, rapidPhase) * 0.65F;
        secondaryDistance *= 0.65F + hash(player.getId() + 353, burstPhase) * 0.55F;

        float primaryAlpha = switch (level) {
            case 1 -> 0.30F;
            case 2 -> 0.48F;
            default -> 0.68F;
        };

        float secondaryAlpha = switch (level) {
            case 1 -> 0.20F;
            case 2 -> 0.38F;
            default -> 0.58F;
        };

        if (fugue) {
            primaryAlpha = 0.78F;
            secondaryAlpha = 0.68F;
        }

        renderRandomSpike(poseStack, bufferSource, player, skinState, model, renderHat, packedLight, rapidPhase, 0, primaryDistance, primaryAlpha, 1.0F, 0.02F, 0.09F);
        renderRandomSpike(poseStack, bufferSource, player, skinState, model, renderHat, packedLight, rapidPhase, 1, primaryDistance * 0.92F, primaryAlpha, 0.00F, 0.92F, 1.0F);
        renderRandomSpike(poseStack, bufferSource, player, skinState, model, renderHat, packedLight, burstPhase, 2, secondaryDistance, secondaryAlpha, 0.92F, 0.08F, 1.0F);
        renderRandomSpike(poseStack, bufferSource, player, skinState, model, renderHat, packedLight, burstPhase, 3, secondaryDistance, secondaryAlpha, 0.10F, 0.45F, 1.0F);

        if (level >= 2 || fugue) {
            renderRandomSpike(poseStack, bufferSource, player, skinState, model, renderHat, packedLight, rapidPhase, 4, primaryDistance * 1.42F, secondaryAlpha * 0.72F, 1.0F, 0.14F, 0.24F);
            renderRandomSpike(poseStack, bufferSource, player, skinState, model, renderHat, packedLight, burstPhase, 5, primaryDistance * 1.42F, secondaryAlpha * 0.72F, 0.02F, 0.82F, 1.0F);
        }

        if (level >= 3 || fugue) renderLevelThreeSpikes(poseStack, bufferSource, player, skinState, model, renderHat, packedLight, rapidPhase, primaryDistance, intensity);
    }

    private static void renderLevelThreeSpikes(PoseStack poseStack, MultiBufferSource bufferSource, AbstractClientPlayer player, SkinModifierState skinState, PlayerModel<AbstractClientPlayer> model, boolean renderHat, int packedLight, int phase, float baseDistance, float intensity) {
        for (int index = 0; index < 6; index++) {
            float roll = hash(player.getId() + 1009 + index * 67, phase);
            float distance = baseDistance * (0.50F + index * 0.18F + roll * 0.45F);
            float alpha = (0.16F + roll * 0.20F) * intensity;

            if ((index & 1) == 0) {
                renderRandomSpike(poseStack, bufferSource, player, skinState, model, renderHat, packedLight, phase, 20 + index, distance, alpha, 1.0F, 0.02F, 0.10F);
            } else {
                renderRandomSpike(poseStack, bufferSource, player, skinState, model, renderHat, packedLight, phase, 20 + index, distance, alpha, 0.00F, 0.86F, 1.0F);
            }
        }
    }

    private static void renderRandomSpike(PoseStack poseStack, MultiBufferSource bufferSource, AbstractClientPlayer player, SkinModifierState skinState, PlayerModel<AbstractClientPlayer> model, boolean renderHat, int packedLight, int phase, int spikeIndex, float distance, float alpha, float red, float green, float blue) {
        float angle = hash(player.getId() + 137 * spikeIndex, phase + spikeIndex * 31) * TAU;
        float verticalScale = 0.45F + hash(player.getId() + 241 * spikeIndex, phase + 83) * 0.55F;
        float depthScale = hash(player.getId() + 359 * spikeIndex, phase + 157) - 0.5F;

        float offsetX = Mth.cos(angle) * distance;
        float offsetY = Mth.sin(angle) * distance * verticalScale;
        float offsetZ = depthScale * distance * 0.35F;

        renderHeadCopy(poseStack, bufferSource, player, skinState, model, renderHat, packedLight, offsetX, offsetY, offsetZ, color(alpha, red, green, blue));
    }

    private static void renderHeadCopy(PoseStack poseStack, MultiBufferSource bufferSource, AbstractClientPlayer player, SkinModifierState skinState, PlayerModel<AbstractClientPlayer> model, boolean renderHat, int packedLight, float offsetX, float offsetY, float offsetZ, int color) {
        poseStack.pushPose();
        poseStack.translate(offsetX, offsetY, offsetZ);

        VertexConsumer skinConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(player.getSkin().texture()));

        model.head.render(poseStack, skinConsumer, packedLight, OverlayTexture.NO_OVERLAY, color);

        if (renderHat) model.hat.render(poseStack, skinConsumer, packedLight, OverlayTexture.NO_OVERLAY, color);

        renderHeadModifiers(poseStack, bufferSource, player, skinState, model, renderHat, packedLight, color);
        renderHeadHighlights(poseStack, bufferSource, player, skinState, model, renderHat, packedLight, color);

        poseStack.popPose();
    }

    private static void renderHeadModifiers(PoseStack poseStack, MultiBufferSource bufferSource, AbstractClientPlayer player, SkinModifierState skinState, PlayerModel<AbstractClientPlayer> model, boolean renderHat, int packedLight, int aberrationColor) {
        if (skinState == null || skinState.getModifiers().isEmpty()) return;

        PlayerSkin.Model skinModel = player.getSkin().model();

        for (SkinModifier modifier : skinState.getModifiers()) {
            if (modifier == null) continue;

            boolean rendersHead = modifier.rendersOverlayPart(SkinModifier.OverlayPart.HEAD);
            boolean rendersHat = renderHat && modifier.rendersOverlayPart(SkinModifier.OverlayPart.HAT);

            if (!rendersHead && !rendersHat) continue;

            ResourceLocation texture = modifier.getTexture(skinModel);
            int color = overlayAberrationColor(aberrationColor, modifier.getColor());
            VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(texture));

            if (rendersHead) {
                model.head.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, color);
            }

            if (rendersHat) {
                model.hat.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, color);
            }

            if (modifier.hasGlint()) {
                VertexConsumer glintConsumer = bufferSource.getBuffer(SkinRenderTypes.translucentGlintOverlay(texture));

                if (rendersHead) {
                    model.head.render(poseStack, glintConsumer, packedLight, OverlayTexture.NO_OVERLAY, aberrationColor);
                }

                if (rendersHat) {
                    model.hat.render(poseStack, glintConsumer, packedLight, OverlayTexture.NO_OVERLAY, aberrationColor);
                }
            }
        }
    }

    private static void renderHeadHighlights(PoseStack poseStack, MultiBufferSource bufferSource, AbstractClientPlayer player, SkinModifierState skinState, PlayerModel<AbstractClientPlayer> model, boolean renderHat, int packedLight, int aberrationColor) {
        if (skinState == null || skinState.getHighlights().isEmpty()) return;

        PlayerSkin.Model skinModel = player.getSkin().model();

        for (SkinHighlight highlight : skinState.getHighlights()) {
            if (highlight == null) continue;

            ResourceLocation texture = highlight.getTexture(skinModel);

            RenderType renderType;
            int light;
            int highlightColor;

            if (highlight.isEmissive()) {
                light = 0x00F000F0;

                if (highlight.tintOnEmissive()) {
                    renderType = SkinRenderTypes.emissiveTinted(texture);
                    highlightColor = highlightAberrationColor(aberrationColor, highlight.getColor());
                } else {
                    renderType = RenderType.entityTranslucent(texture);
                    highlightColor = withAlpha(0xFFFFFFFF, FastColor.ARGB32.alpha(aberrationColor));
                }
            } else {
                light = packedLight;
                renderType = RenderType.entityTranslucent(texture);
                highlightColor = highlightAberrationColor(aberrationColor, highlight.getColor());
            }

            VertexConsumer consumer = bufferSource.getBuffer(renderType);

            model.head.render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, highlightColor);

            if (renderHat) {
                model.hat.render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, highlightColor);
            }
        }
    }

    private static int multiplyHighlightColors(int aberrationColor, int highlightColor) {
        int alpha = FastColor.ARGB32.alpha(highlightColor);

        int aberrationRed = FastColor.ARGB32.red(aberrationColor);
        int aberrationGreen = FastColor.ARGB32.green(aberrationColor);
        int aberrationBlue = FastColor.ARGB32.blue(aberrationColor);

        int highlightRed = FastColor.ARGB32.red(highlightColor);
        int highlightGreen = FastColor.ARGB32.green(highlightColor);
        int highlightBlue = FastColor.ARGB32.blue(highlightColor);

        int red = Mth.clamp((int) (highlightRed * 0.72F + aberrationRed * 0.28F), 0, 255);
        int green = Mth.clamp((int) (highlightGreen * 0.72F + aberrationGreen * 0.28F), 0, 255);
        int blue = Mth.clamp((int) (highlightBlue * 0.72F + aberrationBlue * 0.28F), 0, 255);

        return FastColor.ARGB32.color(alpha, red, green, blue);
    }

    private static int highlightAberrationColor(int aberrationColor, int highlightColor) {
        int alpha = FastColor.ARGB32.alpha(highlightColor);

        int aberrationRed = FastColor.ARGB32.red(aberrationColor);
        int aberrationGreen = FastColor.ARGB32.green(aberrationColor);
        int aberrationBlue = FastColor.ARGB32.blue(aberrationColor);

        int highlightRed = FastColor.ARGB32.red(highlightColor);
        int highlightGreen = FastColor.ARGB32.green(highlightColor);
        int highlightBlue = FastColor.ARGB32.blue(highlightColor);

        int red = Mth.clamp((int) (highlightRed * 0.78F + aberrationRed * 0.22F), 0, 255);
        int green = Mth.clamp((int) (highlightGreen * 0.78F + aberrationGreen * 0.22F), 0, 255);
        int blue = Mth.clamp((int) (highlightBlue * 0.78F + aberrationBlue * 0.22F), 0, 255);

        return FastColor.ARGB32.color(alpha, red, green, blue);
    }

    private static int overlayAberrationColor(int aberrationColor, int overlayColor) {
        int aberrationAlpha = FastColor.ARGB32.alpha(aberrationColor);
        int overlayAlpha = FastColor.ARGB32.alpha(overlayColor);

        int alpha = Math.max(aberrationAlpha, overlayAlpha);

        int red = FastColor.ARGB32.red(aberrationColor) * FastColor.ARGB32.red(overlayColor) / 255;
        int green = FastColor.ARGB32.green(aberrationColor) * FastColor.ARGB32.green(overlayColor) / 255;
        int blue = FastColor.ARGB32.blue(aberrationColor) * FastColor.ARGB32.blue(overlayColor) / 255;

        return FastColor.ARGB32.color(alpha, red, green, blue);
    }

    private static int withAlpha(int color, int alpha) {
        return FastColor.ARGB32.color(alpha, FastColor.ARGB32.red(color), FastColor.ARGB32.green(color), FastColor.ARGB32.blue(color));
    }

    private static int multiplyColors(int aberrationColor, int modifierColor) {
        int alpha = FastColor.ARGB32.alpha(modifierColor);

        int red = FastColor.ARGB32.red(aberrationColor) * FastColor.ARGB32.red(modifierColor) / 255;
        int green = FastColor.ARGB32.green(aberrationColor) * FastColor.ARGB32.green(modifierColor) / 255;
        int blue = FastColor.ARGB32.blue(aberrationColor) * FastColor.ARGB32.blue(modifierColor) / 255;

        return FastColor.ARGB32.color(alpha, red, green, blue);
    }

    private static boolean shouldRender(AbstractClientPlayer player) {
        if (player == null || !player.isAlive() || player.isSpectator() || player.isInvisible()) return false;
        if (!player.hasEffect(ModEffects.CYBERWARE_REJECTION)) return false;

        return player != MC.player || MC.options.getCameraType() != CameraType.FIRST_PERSON;
    }

    private static float getIntensity(int level, boolean fugue) {
        if (fugue) return 1.0F;

        return switch (level) {
            case 1 -> 0.48F;
            case 2 -> 0.76F;
            default -> 1.0F;
        };
    }

    private static float getSpikePhaseLength(int level, boolean fugue) {
        if (fugue) return 2.0F;

        return switch (level) {
            case 1 -> 10.0F;
            case 2 -> 5.0F;
            default -> 2.5F;
        };
    }

    private static float getRapidPhaseSpeed(int level, boolean fugue) {
        if (fugue) return 2.8F;

        return switch (level) {
            case 1 -> 0.55F;
            case 2 -> 1.10F;
            default -> 2.10F;
        };
    }

    private static float getSpikeThreshold(int level) {
        return switch (level) {
            case 1 -> 0.94F;
            case 2 -> 0.78F;
            default -> 0.48F;
        };
    }

    private static float getRapidSpikeThreshold(int level) {
        return switch (level) {
            case 1 -> 0.985F;
            case 2 -> 0.92F;
            default -> 0.77F;
        };
    }

    private static float hash(int seed, int phase) {
        int value = seed;
        value = value * 31 + phase;
        value ^= value >>> 16;
        value *= 0x7FEB352D;
        value ^= value >>> 15;
        value *= 0x846CA68B;
        value ^= value >>> 16;

        return (value & 0x7FFFFFFF) / (float) Integer.MAX_VALUE;
    }

    private static int color(float alpha, float red, float green, float blue) {
        int alphaByte = Mth.clamp((int) (alpha * 255.0F), 0, 255);
        int redByte = Mth.clamp((int) (red * 255.0F), 0, 255);
        int greenByte = Mth.clamp((int) (green * 255.0F), 0, 255);
        int blueByte = Mth.clamp((int) (blue * 255.0F), 0, 255);

        return alphaByte << 24 | redByte << 16 | greenByte << 8 | blueByte;
    }
}