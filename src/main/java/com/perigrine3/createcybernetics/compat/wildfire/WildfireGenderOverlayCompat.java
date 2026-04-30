package com.perigrine3.createcybernetics.compat.wildfire;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.perigrine3.createcybernetics.client.skin.SkinHighlight;
import com.perigrine3.createcybernetics.client.skin.SkinModifier;
import com.perigrine3.createcybernetics.client.skin.SkinModifierManager;
import com.perigrine3.createcybernetics.client.skin.SkinModifierState;
import com.perigrine3.createcybernetics.client.skin.SkinRenderTypes;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class WildfireGenderOverlayCompat {

    private static Field leftBreastField;
    private static Field rightBreastField;

    private static Field quadsField;
    private static Field quadVertexPositionsField;
    private static Field quadNormalField;

    private static Method vertexXMethod;
    private static Method vertexYMethod;
    private static Method vertexZMethod;
    private static Method vertexUMethod;
    private static Method vertexVMethod;

    private static Method normalXMethod;
    private static Method normalYMethod;
    private static Method normalZMethod;

    private static boolean reflectionFailed;

    private WildfireGenderOverlayCompat() {}

    public static void renderBreastOverlays(
            Object genderLayer,
            LivingEntity entity,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            int overlay,
            boolean left
    ) {
        if (reflectionFailed) return;
        if (genderLayer == null || entity == null || poseStack == null || buffer == null) return;
        if (!(entity instanceof AbstractClientPlayer player)) return;

        SkinModifierState state = SkinModifierManager.getPlayerSkinState(player);
        if (state == null) return;
        if (!state.hasModifiers() && !state.hasHighlights()) return;

        Object breastBox = getBreastBox(genderLayer, left);
        if (breastBox == null) return;

        PlayerSkin.Model modelType = player.getSkin().model();

        renderModifiers(state, modelType, breastBox, poseStack, buffer, light, overlay);
        renderHighlights(state, modelType, breastBox, poseStack, buffer, light, overlay);
    }

    private static void renderModifiers(
            SkinModifierState state,
            PlayerSkin.Model modelType,
            Object box,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            int overlay
    ) {
        if (!state.hasModifiers()) return;

        for (SkinModifier modifier : state.getModifiers()) {
            if (modifier == null) continue;

            ResourceLocation texture = modifier.getTexture(modelType);
            VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucent(texture));
            renderBox(box, poseStack, vc, light, overlay, modifier.getColor());

            if (modifier.hasGlint()) {
                VertexConsumer glint = buffer.getBuffer(RenderType.entityGlint());
                renderBox(box, poseStack, glint, light, overlay, 0xFFFFFFFF);
            }
        }
    }

    private static void renderHighlights(
            SkinModifierState state,
            PlayerSkin.Model modelType,
            Object box,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            int overlay
    ) {
        if (!state.hasHighlights()) return;

        for (SkinHighlight highlight : state.getHighlights()) {
            if (highlight == null) continue;

            ResourceLocation texture = highlight.getTexture(modelType);

            RenderType renderType;
            int packedLight;
            int color;

            if (highlight.isEmissive()) {
                packedLight = 0x00F000F0;

                if (highlight.tintOnEmissive()) {
                    renderType = SkinRenderTypes.emissiveTinted(texture);
                    color = highlight.getColor();
                } else {
                    renderType = RenderType.entityTranslucent(texture);
                    color = 0xFFFFFFFF;
                }
            } else {
                packedLight = light;
                renderType = RenderType.entityTranslucent(texture);
                color = highlight.getColor();
            }

            VertexConsumer vc = buffer.getBuffer(renderType);
            renderBox(box, poseStack, vc, packedLight, overlay, color);
        }
    }

    private static void renderBox(
            Object modelBox,
            PoseStack poseStack,
            VertexConsumer buffer,
            int light,
            int overlay,
            int color
    ) {
        if (reflectionFailed || modelBox == null || buffer == null) return;

        try {
            Object[] quads = getQuads(modelBox);
            if (quads == null) return;

            Matrix4f pose = poseStack.last().pose();
            Matrix3f normalMatrix = poseStack.last().normal();

            for (Object quad : quads) {
                if (quad == null) continue;

                Object[] vertices = getQuadVertices(quad);
                Object normal = getQuadNormal(quad);
                if (vertices == null || normal == null) continue;

                Vector3f normalVec = new Vector3f(
                        getNormalX(normal),
                        getNormalY(normal),
                        getNormalZ(normal)
                );
                normalVec.mul(normalMatrix);

                for (Object vertex : vertices) {
                    if (vertex == null) continue;

                    float x = getVertexX(vertex) / 16.0F;
                    float y = getVertexY(vertex) / 16.0F;
                    float z = getVertexZ(vertex) / 16.0F;
                    float u = getVertexU(vertex);
                    float v = getVertexV(vertex);

                    buffer.addVertex(pose, x, y, z)
                            .setColor(color)
                            .setUv(u, v)
                            .setOverlay(overlay)
                            .setLight(light)
                            .setNormal(normalVec.x(), normalVec.y(), normalVec.z());
                }
            }
        } catch (Throwable ignored) {
            reflectionFailed = true;
        }
    }

    private static Object getBreastBox(Object genderLayer, boolean left) {
        try {
            if (leftBreastField == null) {
                leftBreastField = genderLayer.getClass().getDeclaredField("lBreast");
                leftBreastField.setAccessible(true);
            }

            if (rightBreastField == null) {
                rightBreastField = genderLayer.getClass().getDeclaredField("rBreast");
                rightBreastField.setAccessible(true);
            }

            return (left ? leftBreastField : rightBreastField).get(genderLayer);
        } catch (Throwable ignored) {
            reflectionFailed = true;
            return null;
        }
    }

    private static Object[] getQuads(Object modelBox) throws ReflectiveOperationException {
        if (quadsField == null) {
            quadsField = modelBox.getClass().getField("quads");
            quadsField.setAccessible(true);
        }

        Object result = quadsField.get(modelBox);
        return result instanceof Object[] arr ? arr : null;
    }

    private static Object[] getQuadVertices(Object quad) throws ReflectiveOperationException {
        if (quadVertexPositionsField == null) {
            quadVertexPositionsField = quad.getClass().getField("vertexPositions");
            quadVertexPositionsField.setAccessible(true);
        }

        Object result = quadVertexPositionsField.get(quad);
        return result instanceof Object[] arr ? arr : null;
    }

    private static Object getQuadNormal(Object quad) throws ReflectiveOperationException {
        if (quadNormalField == null) {
            quadNormalField = quad.getClass().getField("normal");
            quadNormalField.setAccessible(true);
        }

        return quadNormalField.get(quad);
    }

    private static void ensureVertexMethods(Object vertex) throws ReflectiveOperationException {
        Class<?> cls = vertex.getClass();

        if (vertexXMethod == null) {
            vertexXMethod = cls.getMethod("x");
            vertexXMethod.setAccessible(true);
        }

        if (vertexYMethod == null) {
            vertexYMethod = cls.getMethod("y");
            vertexYMethod.setAccessible(true);
        }

        if (vertexZMethod == null) {
            vertexZMethod = cls.getMethod("z");
            vertexZMethod.setAccessible(true);
        }

        if (vertexUMethod == null) {
            vertexUMethod = cls.getMethod("texturePositionX");
            vertexUMethod.setAccessible(true);
        }

        if (vertexVMethod == null) {
            vertexVMethod = cls.getMethod("texturePositionY");
            vertexVMethod.setAccessible(true);
        }
    }

    private static void ensureNormalMethods(Object normal) throws ReflectiveOperationException {
        Class<?> cls = normal.getClass();

        if (normalXMethod == null) {
            normalXMethod = cls.getMethod("getX");
            normalXMethod.setAccessible(true);
        }

        if (normalYMethod == null) {
            normalYMethod = cls.getMethod("getY");
            normalYMethod.setAccessible(true);
        }

        if (normalZMethod == null) {
            normalZMethod = cls.getMethod("getZ");
            normalZMethod.setAccessible(true);
        }
    }

    private static float getVertexX(Object vertex) throws ReflectiveOperationException {
        ensureVertexMethods(vertex);
        Object result = vertexXMethod.invoke(vertex);
        return result instanceof Number number ? number.floatValue() : 0.0F;
    }

    private static float getVertexY(Object vertex) throws ReflectiveOperationException {
        ensureVertexMethods(vertex);
        Object result = vertexYMethod.invoke(vertex);
        return result instanceof Number number ? number.floatValue() : 0.0F;
    }

    private static float getVertexZ(Object vertex) throws ReflectiveOperationException {
        ensureVertexMethods(vertex);
        Object result = vertexZMethod.invoke(vertex);
        return result instanceof Number number ? number.floatValue() : 0.0F;
    }

    private static float getVertexU(Object vertex) throws ReflectiveOperationException {
        ensureVertexMethods(vertex);
        Object result = vertexUMethod.invoke(vertex);
        return result instanceof Number number ? number.floatValue() : 0.0F;
    }

    private static float getVertexV(Object vertex) throws ReflectiveOperationException {
        ensureVertexMethods(vertex);
        Object result = vertexVMethod.invoke(vertex);
        return result instanceof Number number ? number.floatValue() : 0.0F;
    }

    private static int getNormalX(Object normal) throws ReflectiveOperationException {
        ensureNormalMethods(normal);
        Object result = normalXMethod.invoke(normal);
        return result instanceof Number number ? number.intValue() : 0;
    }

    private static int getNormalY(Object normal) throws ReflectiveOperationException {
        ensureNormalMethods(normal);
        Object result = normalYMethod.invoke(normal);
        return result instanceof Number number ? number.intValue() : 0;
    }

    private static int getNormalZ(Object normal) throws ReflectiveOperationException {
        ensureNormalMethods(normal);
        Object result = normalZMethod.invoke(normal);
        return result instanceof Number number ? number.intValue() : 0;
    }
}