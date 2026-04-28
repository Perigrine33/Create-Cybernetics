package com.perigrine3.createcybernetics.entity.client.highlights;

import com.perigrine3.createcybernetics.entity.client.models.PunklinModel;
import com.perigrine3.createcybernetics.entity.custom.PunklinEntity;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;

public class PunklinHighlightLayer extends AbstractCyberPiglinGangHighlightLayer<PunklinEntity, PunklinModel<PunklinEntity>> {

    public PunklinHighlightLayer(RenderLayerParent<PunklinEntity, PunklinModel<PunklinEntity>> renderer) {
        super(renderer);
    }

    @Override
    protected ResourceLocation getHighlightTexture(PunklinEntity entity) {
        return texture("punklin", entity.getTextureVariant());    }
}