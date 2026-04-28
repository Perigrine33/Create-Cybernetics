package com.perigrine3.createcybernetics.entity.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.perigrine3.createcybernetics.client.model.CyberentityAttachmentLayer;
import com.perigrine3.createcybernetics.entity.client.highlights.PigstromHighlightLayer;
import com.perigrine3.createcybernetics.entity.client.models.PigstromModel;
import com.perigrine3.createcybernetics.entity.custom.PigstromEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class PigstromRenderer extends MobRenderer<PigstromEntity, PigstromModel<PigstromEntity>> {

    public PigstromRenderer(EntityRendererProvider.Context context) {
        super(context, new PigstromModel<>(context.bakeLayer(PigstromModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        this.addLayer(new PigstromHighlightLayer(this));
        this.addLayer(new CyberentityAttachmentLayer<>(this, context));
    }

    @Override
    public ResourceLocation getTextureLocation(PigstromEntity entity) {
        return entity.getGangTextureLocation();
    }

    @Override
    protected void scale(PigstromEntity entity, PoseStack poseStack, float partialTickTime) {
        if (entity.isBaby()) {
            poseStack.scale(0.7F, 0.7F, 0.7F);
        }

        super.scale(entity, poseStack, partialTickTime);
    }
}