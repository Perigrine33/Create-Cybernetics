package com.perigrine3.createcybernetics.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;

public class RobosurgeonSkeletonOverlayLayer<T extends AbstractSkeleton>
        extends RenderLayer<T, SkeletonModel<T>> {

    public RobosurgeonSkeletonOverlayLayer(RenderLayerParent<T, SkeletonModel<T>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack,
                       MultiBufferSource buffer,
                       int packedLight,
                       T entity,
                       float limbSwing,
                       float limbSwingAmount,
                       float partialTick,
                       float ageInTicks,
                       float netHeadYaw,
                       float headPitch) {

        RobosurgeonPreviewOverlayContext.State ctx = RobosurgeonPreviewOverlayContext.get();
        if (ctx == null) return;
        if (ctx.target() != entity) return;
        if (ctx.overlays().isEmpty()) return;

        SkeletonModel<T> model = this.getParentModel();

        for (RobosurgeonPreviewOverlayContext.Entry entry : ctx.overlays()) {
            renderOverlay(model, poseStack, buffer, packedLight, entry.texture(), entry.alpha());
        }
    }

    private void renderOverlay(SkeletonModel<T> model,
                               PoseStack poseStack,
                               MultiBufferSource buffer,
                               int packedLight,
                               ResourceLocation texture,
                               float alpha) {
        if (texture == null || alpha <= 0f) return;

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(texture));
        model.renderToBuffer(
                poseStack,
                consumer,
                packedLight,
                OverlayTexture.NO_OVERLAY
        );
    }
}