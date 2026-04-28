package com.perigrine3.createcybernetics.entity.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.perigrine3.createcybernetics.client.model.CyberentityAttachmentLayer;
import com.perigrine3.createcybernetics.entity.client.highlights.PunklinHighlightLayer;
import com.perigrine3.createcybernetics.entity.client.models.PunklinModel;
import com.perigrine3.createcybernetics.entity.custom.PunklinEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class PunklinRenderer extends MobRenderer<PunklinEntity, PunklinModel<PunklinEntity>> {

    public PunklinRenderer(EntityRendererProvider.Context context) {
        super(context, new PunklinModel<>(context.bakeLayer(PunklinModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        this.addLayer(new PunklinHighlightLayer(this));
        this.addLayer(new CyberentityAttachmentLayer<>(this, context));
    }

    @Override
    public ResourceLocation getTextureLocation(PunklinEntity entity) {
        return entity.getGangTextureLocation();
    }

    @Override
    protected void scale(PunklinEntity entity, PoseStack poseStack, float partialTickTime) {
        if (entity.isBaby()) {
            poseStack.scale(0.7F, 0.7F, 0.7F);
        }

        super.scale(entity, poseStack, partialTickTime);
    }
}