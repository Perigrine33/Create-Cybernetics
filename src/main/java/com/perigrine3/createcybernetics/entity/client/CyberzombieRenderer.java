package com.perigrine3.createcybernetics.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.custom.CyberzombieEntity;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer; // Change to MobRenderer
import net.minecraft.resources.ResourceLocation;

// Extend MobRenderer and specify your entity type and model type
public class CyberzombieRenderer extends MobRenderer<CyberzombieEntity, CyberzombieModel<CyberzombieEntity>> {


    public CyberzombieRenderer(EntityRendererProvider.Context context) {
        super(context, new CyberzombieModel<>(context.bakeLayer(CyberzombieModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(CyberzombieEntity cyberzombieEntity) {
        return ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/cyberzombie.png");
    }

    @Override
    public void render(CyberzombieEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
