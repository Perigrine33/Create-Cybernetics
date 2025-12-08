package com.perigrine3.createcybernetics.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.custom.SkinstackEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

// Extend MobRenderer and specify your entity type and model type
public class SkinstackRenderer extends MobRenderer<SkinstackEntity, SkinstackModel<SkinstackEntity>> {


    public SkinstackRenderer(EntityRendererProvider.Context context) {
        super(context, new SkinstackModel<>(context.bakeLayer(SkinstackModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(SkinstackEntity skinstackEntity) {
        return ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/skinstack.png");
    }

    @Override
    public void render(SkinstackEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
