package com.perigrine3.createcybernetics.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.perigrine3.createcybernetics.entity.custom.GuardianBeamEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class GuardianBeamRenderer extends EntityRenderer<GuardianBeamEntity> {
    private static final ResourceLocation TEX = ResourceLocation.withDefaultNamespace("textures/entity/guardian_beam.png");

    public GuardianBeamRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public boolean shouldRender(GuardianBeamEntity entity, net.minecraft.client.renderer.culling.Frustum frustum, double x, double y, double z) {
        return true;
    }

    @Override
    public void render(GuardianBeamEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Vec3 start = entity.getStart();
        Vec3 end = entity.getEnd();
        Vec3 delta = end.subtract(start);

        float len = (float) delta.length();
        if (len < 0.001F) return;

        float dx = (float) delta.x;
        float dy = (float) delta.y;
        float dz = (float) delta.z;

        float yawRad = (float) Mth.atan2(dx, dz);
        float pitchRad = (float) Mth.atan2(dy, Mth.sqrt(dx * dx + dz * dz));

        float power = Mth.clamp(entity.getPower(), 0.0F, 1.0F);
        boolean charging = entity.isCharging();

        float baseW = 0.06F;
        float extraW = 0.16F * power;
        float w = baseW + extraW;

        if (!charging) {
            w *= 1.35F;
        } else {
            w *= 1.10F;
        }

        float glowW = w * 1.75F;

        int baseR = 58;
        int baseG = 176;
        int baseB = 158;

        float pulse = 0.6F + 0.4F * (0.5F + 0.5F * Mth.sin((entity.tickCount + partialTick) * 0.35F));
        int coreA = charging ? (int) (Mth.lerp(power, 70.0F, 170.0F) * pulse) : 230;
        int glowA = charging ? (int) (Mth.lerp(power, 25.0F, 95.0F) * pulse) : 120;

        float t = (entity.tickCount + partialTick) * 0.02F;
        float v0 = -t;
        float v1 = (len * 0.5F) - t;

        int overlay = OverlayTexture.NO_OVERLAY;
        int light = LightTexture.FULL_BRIGHT;

        poseStack.pushPose();

        poseStack.mulPose(new Quaternionf().rotateY(yawRad));
        poseStack.mulPose(new Quaternionf().rotateX(-pitchRad));

        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucent(TEX));
        PoseStack.Pose pose = poseStack.last();

        emitQuad(vc, pose,
                -glowW, 0F, 0F,
                glowW, 0F, 0F,
                glowW, 0F, len,
                -glowW, 0F, len,
                0F, v0, 1F, v1,
                baseR, baseG, baseB, glowA,
                overlay, light,
                0F, 1F, 0F);

        emitQuad(vc, pose,
                0F, -glowW, 0F,
                0F,  glowW, 0F,
                0F,  glowW, len,
                0F, -glowW, len,
                0F, v0, 1F, v1,
                baseR, baseG, baseB, glowA,
                overlay, light,
                1F, 0F, 0F);

        int coreR = Mth.clamp(baseR + (int) (25.0F * power), 0, 255);
        int coreG = Mth.clamp(baseG + (int) (55.0F * power), 0, 255);
        int coreB = Mth.clamp(baseB + (int) (25.0F * power), 0, 255);

        emitQuad(vc, pose,
                -w, 0F, 0F,
                w, 0F, 0F,
                w, 0F, len,
                -w, 0F, len,
                0F, v0, 1F, v1,
                coreR, coreG, coreB, coreA,
                overlay, light,
                0F, 1F, 0F);

        emitQuad(vc, pose,
                0F, -w, 0F,
                0F,  w, 0F,
                0F,  w, len,
                0F, -w, len,
                0F, v0, 1F, v1,
                coreR, coreG, coreB, coreA,
                overlay, light,
                1F, 0F, 0F);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private static void emitQuad(VertexConsumer vc, PoseStack.Pose pose,
                                 float x0, float y0, float z0,
                                 float x1, float y1, float z1,
                                 float x2, float y2, float z2,
                                 float x3, float y3, float z3,
                                 float u0, float v0, float u1, float v1,
                                 int r, int g, int b, int a,
                                 int overlay, int light,
                                 float nx, float ny, float nz) {

        vc.addVertex(pose, x0, y0, z0).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
        vc.addVertex(pose, x1, y1, z1).setColor(r, g, b, a).setUv(u1, v0).setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
        vc.addVertex(pose, x2, y2, z2).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
        vc.addVertex(pose, x3, y3, z3).setColor(r, g, b, a).setUv(u0, v1).setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
    }

    @Override
    public ResourceLocation getTextureLocation(GuardianBeamEntity entity) {
        return TEX;
    }
}
