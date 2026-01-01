package com.perigrine3.createcybernetics.client.skin;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class SkinLayerRegistration {

    private SkinLayerRegistration() {}

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        PlayerRenderer wide = event.getSkin(PlayerSkin.Model.WIDE);
        if (wide != null) {
            wide.addLayer(new SkinLayerHandler(wide));
            wide.addLayer(new SkinHighlightLayer(wide));
        }

        PlayerRenderer slim = event.getSkin(PlayerSkin.Model.SLIM);
        if (slim != null) {
            slim.addLayer(new SkinLayerHandler(slim));
            slim.addLayer(new SkinHighlightLayer(slim));
        }
    }
}
