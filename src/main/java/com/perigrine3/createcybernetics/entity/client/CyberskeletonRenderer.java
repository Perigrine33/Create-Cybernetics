package com.perigrine3.createcybernetics.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.custom.CyberskeletonEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class CyberskeletonRenderer extends MobRenderer<CyberskeletonEntity, CyberskeletonModel<CyberskeletonEntity>> {


    public CyberskeletonRenderer(EntityRendererProvider.Context context) {
        super(context, new CyberskeletonModel<>(context.bakeLayer(CyberskeletonModel.LAYER_LOCATION)), 0.5f);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        this.addLayer(new CyberskeletonHighlightLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(CyberskeletonEntity cyberskeletonEntity) {
        return ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/cyberskeleton.png");
    }

    @Override
    public void render(CyberskeletonEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
