package com.perigrine3.createcybernetics.entity.client.highlights;

import com.perigrine3.createcybernetics.entity.client.models.HogBoyModel;
import com.perigrine3.createcybernetics.entity.custom.HogBoyEntity;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;

public class HogBoyHighlightLayer extends AbstractCyberPiglinGangHighlightLayer<HogBoyEntity, HogBoyModel<HogBoyEntity>> {

    public HogBoyHighlightLayer(RenderLayerParent<HogBoyEntity, HogBoyModel<HogBoyEntity>> renderer) {
        super(renderer);
    }

    @Override
    protected ResourceLocation getHighlightTexture(HogBoyEntity entity) {
        return texture("hogboy", entity.getTextureVariant());    }
}