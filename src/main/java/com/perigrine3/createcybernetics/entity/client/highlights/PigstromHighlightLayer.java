package com.perigrine3.createcybernetics.entity.client.highlights;

import com.perigrine3.createcybernetics.entity.client.models.PigstromModel;
import com.perigrine3.createcybernetics.entity.custom.PigstromEntity;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;

public class PigstromHighlightLayer extends AbstractCyberPiglinGangHighlightLayer<PigstromEntity, PigstromModel<PigstromEntity>> {

    public PigstromHighlightLayer(RenderLayerParent<PigstromEntity, PigstromModel<PigstromEntity>> renderer) {
        super(renderer);
    }

    @Override
    protected ResourceLocation getHighlightTexture(PigstromEntity entity) {
        return texture("pigstrom", entity.getTextureVariant());    }
}