package com.perigrine3.createcybernetics.compat.pointblank;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.perigrine3.createcybernetics.client.skin.SkinHighlight;
import com.perigrine3.createcybernetics.client.skin.SkinModifier;
import com.perigrine3.createcybernetics.client.skin.SkinModifierManager;
import com.perigrine3.createcybernetics.client.skin.SkinModifierState;
import com.perigrine3.createcybernetics.client.skin.SkinRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class PointBlankFirstPersonOverlayCompat {

    private static final ThreadLocal<List<QueuedVertex>> QUEUED_VERTICES =
            ThreadLocal.withInitial(ArrayList::new);

    private static Method cubeQuadsMethod;
    private static Method quadVerticesMethod;
    private static Method quadNormalMethod;
    private static Method vertexPositionMethod;
    private static Method vertexTexUMethod;
    private static Method vertexTexVMethod;
    private static boolean reflectionFailed;

    private PointBlankFirstPersonOverlayCompat() {}

    public static void beginFrame() {
        QUEUED_VERTICES.get().clear();
    }

    public static void captureCubeOverlays(
            PoseStack poseStack,
            Object cube,
            int packedLight,
            int packedOverlay
    ) {
        if (poseStack == null || cube == null) return;

        Minecraft mc = Minecraft.getInstance();
        if (!(mc.player instanceof AbstractClientPlayer player)) return;

        SkinModifierState state = SkinModifierManager.getPlayerSkinState(player);
        if (state == null) return;
        if (!state.hasModifiers() && !state.hasHighlights()) return;

        PlayerSkin.Model modelType = player.getSkin().model();

        if (state.hasModifiers()) {
            for (SkinModifier modifier : state.getModifiers()) {
                if (modifier == null) continue;

                ResourceLocation texture = modifier.getTexture(modelType);

                captureCube(
                        poseStack,
                        cube,
                        RenderType.entityTranslucent(texture),
                        packedLight,
                        packedOverlay,
                        modifier.getColor()
                );

                if (modifier.hasGlint()) {
                    captureCube(
                            poseStack,
                            cube,
                            RenderType.entityGlint(),
                            packedLight,
                            packedOverlay,
                            0xFFFFFFFF
                    );
                }
            }
        }

        if (state.hasHighlights()) {
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

                captureCube(
                        poseStack,
                        cube,
                        renderType,
                        light,
                        packedOverlay,
                        color
                );
            }
        }
    }

    public static void flush(MultiBufferSource bufferSource) {
        if (bufferSource == null) {
            QUEUED_VERTICES.get().clear();
            return;
        }

        List<QueuedVertex> vertices = QUEUED_VERTICES.get();
        if (vertices.isEmpty()) return;

        try {
            for (QueuedVertex vertex : vertices) {
                VertexConsumer vc = bufferSource.getBuffer(vertex.renderType);

                vc.addVertex(
                        vertex.x,
                        vertex.y,
                        vertex.z,
                        vertex.color,
                        vertex.u,
                        vertex.v,
                        vertex.packedOverlay,
                        vertex.packedLight,
                        vertex.normalX,
                        vertex.normalY,
                        vertex.normalZ
                );
            }
        } finally {
            vertices.clear();
        }
    }

    private static void captureCube(
            PoseStack poseStack,
            Object cube,
            RenderType renderType,
            int packedLight,
            int packedOverlay,
            int color
    ) {
        if (reflectionFailed || renderType == null) return;

        try {
            Object[] quads = getQuads(cube);
            if (quads == null) return;

            Matrix4f pose = new Matrix4f(poseStack.last().pose());
            Matrix3f normalMatrix = new Matrix3f(poseStack.last().normal());
            List<QueuedVertex> out = QUEUED_VERTICES.get();

            for (Object quad : quads) {
                if (quad == null) continue;

                Object[] vertices = getVertices(quad);
                if (vertices == null || vertices.length == 0) continue;

                Vector3f normal = getNormal(quad);
                if (normal == null) {
                    normal = new Vector3f(0.0F, 1.0F, 0.0F);
                } else {
                    normal = normalMatrix.transform(new Vector3f(normal));
                }

                for (Object vertex : vertices) {
                    if (vertex == null) continue;

                    Vector3f pos = getPosition(vertex);
                    if (pos == null) continue;

                    float u = getTexU(vertex);
                    float v = getTexV(vertex);

                    Vector4f transformed = pose.transform(new Vector4f(pos.x(), pos.y(), pos.z(), 1.0F));

                    out.add(new QueuedVertex(
                            renderType,
                            transformed.x(),
                            transformed.y(),
                            transformed.z(),
                            color,
                            u,
                            v,
                            packedOverlay,
                            packedLight,
                            normal.x(),
                            normal.y(),
                            normal.z()
                    ));
                }
            }
        } catch (Throwable ignored) {
            reflectionFailed = true;
        }
    }

    private static Object[] getQuads(Object cube) throws ReflectiveOperationException {
        if (cubeQuadsMethod == null) {
            cubeQuadsMethod = cube.getClass().getMethod("quads");
            cubeQuadsMethod.setAccessible(true);
        }

        Object result = cubeQuadsMethod.invoke(cube);
        return result instanceof Object[] arr ? arr : null;
    }

    private static Object[] getVertices(Object quad) throws ReflectiveOperationException {
        if (quadVerticesMethod == null) {
            quadVerticesMethod = quad.getClass().getMethod("vertices");
            quadVerticesMethod.setAccessible(true);
        }

        Object result = quadVerticesMethod.invoke(quad);
        return result instanceof Object[] arr ? arr : null;
    }

    private static Vector3f getNormal(Object quad) throws ReflectiveOperationException {
        if (quadNormalMethod == null) {
            quadNormalMethod = quad.getClass().getMethod("normal");
            quadNormalMethod.setAccessible(true);
        }

        Object result = quadNormalMethod.invoke(quad);
        return result instanceof Vector3f normal ? normal : null;
    }

    private static Vector3f getPosition(Object vertex) throws ReflectiveOperationException {
        if (vertexPositionMethod == null) {
            vertexPositionMethod = vertex.getClass().getMethod("position");
            vertexPositionMethod.setAccessible(true);
        }

        Object result = vertexPositionMethod.invoke(vertex);
        return result instanceof Vector3f pos ? pos : null;
    }

    private static float getTexU(Object vertex) throws ReflectiveOperationException {
        if (vertexTexUMethod == null) {
            vertexTexUMethod = vertex.getClass().getMethod("texU");
            vertexTexUMethod.setAccessible(true);
        }

        Object result = vertexTexUMethod.invoke(vertex);
        return result instanceof Number number ? number.floatValue() : 0.0F;
    }

    private static float getTexV(Object vertex) throws ReflectiveOperationException {
        if (vertexTexVMethod == null) {
            vertexTexVMethod = vertex.getClass().getMethod("texV");
            vertexTexVMethod.setAccessible(true);
        }

        Object result = vertexTexVMethod.invoke(vertex);
        return result instanceof Number number ? number.floatValue() : 0.0F;
    }

    private record QueuedVertex(
            RenderType renderType,
            float x,
            float y,
            float z,
            int color,
            float u,
            float v,
            int packedOverlay,
            int packedLight,
            float normalX,
            float normalY,
            float normalZ
    ) {}
}