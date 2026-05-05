package com.perigrine3.createcybernetics.entity.client.renderers;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.custom.TatHogEntity;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TatHogRenderer extends MobRenderer<TatHogEntity, PiglinModel<TatHogEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            CreateCybernetics.MODID,
            "textures/entity/tathog.png"
    );

    public TatHogRenderer(EntityRendererProvider.Context context) {
        super(context, new PiglinModel<>(context.bakeLayer(ModelLayers.PIGLIN)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(TatHogEntity entity) {
        return TEXTURE;
    }
}