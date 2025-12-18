package com.perigrine3.createcybernetics.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.custom.SmasherEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class SmasherHighlightLayer extends RenderLayer<SmasherEntity, SmasherModel<SmasherEntity>> {

    private static final ResourceLocation HIGHLIGHT_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/smasherhighlight.png");

    public SmasherHighlightLayer(RenderLayerParent<SmasherEntity, SmasherModel<SmasherEntity>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, SmasherEntity entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible()) return;

        VertexConsumer vc = buffer.getBuffer(RenderType.eyes(HIGHLIGHT_TEXTURE));

        int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);

        this.getParentModel().renderToBuffer(poseStack, vc, LightTexture.FULL_BRIGHT, overlay, 0xFFFFFFFF);
    }
}
