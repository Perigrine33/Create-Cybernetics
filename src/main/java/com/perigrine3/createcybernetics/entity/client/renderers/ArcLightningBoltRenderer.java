package com.perigrine3.createcybernetics.entity.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.perigrine3.createcybernetics.entity.custom.ArcLightningBoltEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ArcLightningBoltRenderer extends EntityRenderer<ArcLightningBoltEntity> {
    private static final ResourceLocation NO_TEXTURE = ResourceLocation.withDefaultNamespace("textures/misc/white.png");

    public ArcLightningBoltRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(
            ArcLightningBoltEntity entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        Vec3 start = entity.getStart();
        Vec3 end = entity.getEnd();
        Vec3 delta = end.subtract(start);

        double length = delta.length();
        if (length <= 0.001D) {
            return;
        }

        Vec3 direction = delta.normalize();

        poseStack.pushPose();

        Vector3f from = new Vector3f(0.0F, 1.0F, 0.0F);
        Vector3f to = new Vector3f((float) direction.x, (float) direction.y, (float) direction.z);
        Quaternionf rotation = new Quaternionf().rotationTo(from, to);

        poseStack.mulPose(rotation);

        VertexConsumer vertices = buffer.getBuffer(RenderType.lightning());

        renderLightning(vertices, poseStack, entity.getRenderSeed(), (float) length);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private static void renderLightning(VertexConsumer vertices, PoseStack poseStack, long seed, float length) {
        RandomSource random = RandomSource.create(seed);

        int points = Mth.clamp((int) (length * 1.6F), 8, 48);

        float[] xs = new float[points + 1];
        float[] zs = new float[points + 1];

        xs[0] = 0.0F;
        zs[0] = 0.0F;
        xs[points] = 0.0F;
        zs[points] = 0.0F;

        float driftX = 0.0F;
        float driftZ = 0.0F;

        for (int i = 1; i < points; i++) {
            float t = i / (float) points;
            float taper = Mth.sin(t * Mth.PI);

            driftX += (random.nextFloat() - 0.5F) * 0.28F;
            driftZ += (random.nextFloat() - 0.5F) * 0.28F;

            xs[i] = driftX * taper;
            zs[i] = driftZ * taper;
        }

        for (int pass = 0; pass < 4; pass++) {
            float alpha = pass == 0 ? 0.75F : 0.35F;
            float width = pass == 0 ? 0.075F : 0.16F + pass * 0.035F;

            float r = 0.45F;
            float g = 0.65F;
            float b = 1.0F;

            for (int i = 0; i < points; i++) {
                float y0 = length * (i / (float) points);
                float y1 = length * ((i + 1) / (float) points);

                float x0 = xs[i];
                float z0 = zs[i];
                float x1 = xs[i + 1];
                float z1 = zs[i + 1];

                addCrossSegment(vertices, poseStack, x0, y0, z0, x1, y1, z1, width, r, g, b, alpha);
            }
        }

        for (int branch = 0; branch < 5; branch++) {
            int startIndex = 1 + random.nextInt(Math.max(1, points - 2));
            int branchLength = 2 + random.nextInt(4);
            float branchSideX = (random.nextFloat() - 0.5F) * 0.9F;
            float branchSideZ = (random.nextFloat() - 0.5F) * 0.9F;

            float x0 = xs[startIndex];
            float z0 = zs[startIndex];
            float y0 = length * (startIndex / (float) points);

            for (int i = 0; i < branchLength; i++) {
                float t0 = i / (float) branchLength;
                float t1 = (i + 1) / (float) branchLength;

                float x1 = x0 + branchSideX * t1;
                float z1 = z0 + branchSideZ * t1;
                float y1 = y0 + length * 0.035F * (i + 1);

                addCrossSegment(vertices, poseStack, x0, y0, z0, x1, y1, z1, 0.045F, 0.45F, 0.65F, 1.0F, 0.35F);

                x0 = x1;
                y0 = y1;
                z0 = z1;
            }
        }
    }

    private static void addCrossSegment(
            VertexConsumer vertices,
            PoseStack poseStack,
            float x0,
            float y0,
            float z0,
            float x1,
            float y1,
            float z1,
            float width,
            float r,
            float g,
            float b,
            float a
    ) {
        addQuadX(vertices, poseStack, x0, y0, z0, x1, y1, z1, width, r, g, b, a);
        addQuadZ(vertices, poseStack, x0, y0, z0, x1, y1, z1, width, r, g, b, a);
        addQuadDiagA(vertices, poseStack, x0, y0, z0, x1, y1, z1, width * 0.75F, r, g, b, a);
        addQuadDiagB(vertices, poseStack, x0, y0, z0, x1, y1, z1, width * 0.75F, r, g, b, a);
    }

    private static void addQuadX(
            VertexConsumer vertices,
            PoseStack poseStack,
            float x0,
            float y0,
            float z0,
            float x1,
            float y1,
            float z1,
            float width,
            float r,
            float g,
            float b,
            float a
    ) {
        Matrix4f matrix = poseStack.last().pose();

        vertices.addVertex(matrix, x0 - width, y0, z0).setColor(r, g, b, a);
        vertices.addVertex(matrix, x0 + width, y0, z0).setColor(r, g, b, a);
        vertices.addVertex(matrix, x1 + width, y1, z1).setColor(r, g, b, a);
        vertices.addVertex(matrix, x1 - width, y1, z1).setColor(r, g, b, a);
    }

    private static void addQuadZ(
            VertexConsumer vertices,
            PoseStack poseStack,
            float x0,
            float y0,
            float z0,
            float x1,
            float y1,
            float z1,
            float width,
            float r,
            float g,
            float b,
            float a
    ) {
        Matrix4f matrix = poseStack.last().pose();

        vertices.addVertex(matrix, x0, y0, z0 - width).setColor(r, g, b, a);
        vertices.addVertex(matrix, x0, y0, z0 + width).setColor(r, g, b, a);
        vertices.addVertex(matrix, x1, y1, z1 + width).setColor(r, g, b, a);
        vertices.addVertex(matrix, x1, y1, z1 - width).setColor(r, g, b, a);
    }

    private static void addQuadDiagA(
            VertexConsumer vertices,
            PoseStack poseStack,
            float x0,
            float y0,
            float z0,
            float x1,
            float y1,
            float z1,
            float width,
            float r,
            float g,
            float b,
            float a
    ) {
        Matrix4f matrix = poseStack.last().pose();

        vertices.addVertex(matrix, x0 - width, y0, z0 - width).setColor(r, g, b, a);
        vertices.addVertex(matrix, x0 + width, y0, z0 + width).setColor(r, g, b, a);
        vertices.addVertex(matrix, x1 + width, y1, z1 + width).setColor(r, g, b, a);
        vertices.addVertex(matrix, x1 - width, y1, z1 - width).setColor(r, g, b, a);
    }

    private static void addQuadDiagB(
            VertexConsumer vertices,
            PoseStack poseStack,
            float x0,
            float y0,
            float z0,
            float x1,
            float y1,
            float z1,
            float width,
            float r,
            float g,
            float b,
            float a
    ) {
        Matrix4f matrix = poseStack.last().pose();

        vertices.addVertex(matrix, x0 - width, y0, z0 + width).setColor(r, g, b, a);
        vertices.addVertex(matrix, x0 + width, y0, z0 - width).setColor(r, g, b, a);
        vertices.addVertex(matrix, x1 + width, y1, z1 - width).setColor(r, g, b, a);
        vertices.addVertex(matrix, x1 - width, y1, z1 + width).setColor(r, g, b, a);
    }

    @Override
    public ResourceLocation getTextureLocation(ArcLightningBoltEntity entity) {
        return NO_TEXTURE;
    }
}