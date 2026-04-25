package com.perigrine3.createcybernetics.entity.client.renderers;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.client.RipperModel;
import com.perigrine3.createcybernetics.entity.custom.RipperEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RipperRenderer extends MobRenderer<RipperEntity, RipperModel<RipperEntity>> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/ripper.png");

    public RipperRenderer(EntityRendererProvider.Context context) {
        super(context, new RipperModel<>(context.bakeLayer(RipperModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(RipperEntity entity) {
        return TEXTURE;
    }
}