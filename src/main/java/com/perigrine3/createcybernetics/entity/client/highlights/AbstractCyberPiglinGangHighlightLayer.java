package com.perigrine3.createcybernetics.entity.client.highlights;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.custom.AbstractCyberPiglinGangEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractCyberPiglinGangHighlightLayer<T extends AbstractCyberPiglinGangEntity, M extends EntityModel<T>>
        extends RenderLayer<T, M> {

    private static final int FULL_BRIGHT = 0xF000F0;
    private static final int WHITE_ARGB = 0xFFFFFFFF;

    protected AbstractCyberPiglinGangHighlightLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            T entity,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        if (entity.isInvisible()) return;

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(getHighlightTexture(entity)));

        this.getParentModel().renderToBuffer(
                poseStack,
                vertexConsumer,
                FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                WHITE_ARGB
        );
    }

    protected ResourceLocation texture(String prefix, int variant) {
        return ResourceLocation.fromNamespaceAndPath(
                CreateCybernetics.MODID,
                "textures/entity/punklin/" + prefix + "_highlight_" + Math.floorMod(variant, 4) + ".png"
        );
    }

    protected abstract ResourceLocation getHighlightTexture(T entity);
}