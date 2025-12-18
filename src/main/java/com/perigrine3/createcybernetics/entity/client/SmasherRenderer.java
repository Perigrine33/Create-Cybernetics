package com.perigrine3.createcybernetics.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.custom.SmasherEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SmasherRenderer extends MobRenderer<SmasherEntity, SmasherModel<SmasherEntity>> {
    public SmasherRenderer(EntityRendererProvider.Context context) {
        super(context, new SmasherModel<>(context.bakeLayer(SmasherModel.LAYER_LOCATION)), 0.5f);
        this.addLayer(new SmasherHighlightLayer(this));

    }

    @Override
    public ResourceLocation getTextureLocation(SmasherEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/smasher.png");
    }

    @Override
    public void render(SmasherEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
