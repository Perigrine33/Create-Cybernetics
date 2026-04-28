package com.perigrine3.createcybernetics.entity.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.perigrine3.createcybernetics.client.model.CyberentityAttachmentLayer;
import com.perigrine3.createcybernetics.entity.client.highlights.HogBoyHighlightLayer;
import com.perigrine3.createcybernetics.entity.client.models.HogBoyModel;
import com.perigrine3.createcybernetics.entity.custom.HogBoyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class HogBoyRenderer extends MobRenderer<HogBoyEntity, HogBoyModel<HogBoyEntity>> {

    public HogBoyRenderer(EntityRendererProvider.Context context) {
        super(context, new HogBoyModel<>(context.bakeLayer(HogBoyModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        this.addLayer(new HogBoyHighlightLayer(this));
        this.addLayer(new CyberentityAttachmentLayer<>(this, context));
    }

    @Override
    public ResourceLocation getTextureLocation(HogBoyEntity entity) {
        return entity.getGangTextureLocation();
    }

    @Override
    protected void scale(HogBoyEntity entity, PoseStack poseStack, float partialTickTime) {
        if (entity.isBaby()) {
            poseStack.scale(0.7F, 0.7F, 0.7F);
        }

        super.scale(entity, poseStack, partialTickTime);
    }
}