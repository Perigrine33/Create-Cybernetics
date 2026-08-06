package com.perigrine3.createcybernetics.client.render.rejection;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CyberwareRejectionHeadLayerRegistration {

    private CyberwareRejectionHeadLayerRegistration() {
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        addPlayerLayer(event, PlayerSkin.Model.WIDE);
        addPlayerLayer(event, PlayerSkin.Model.SLIM);
    }

    private static void addPlayerLayer(EntityRenderersEvent.AddLayers event, PlayerSkin.Model skinModel) {
        EntityRenderer<?> renderer = event.getSkin(skinModel);

        if (!(renderer instanceof PlayerRenderer playerRenderer)) {
            CreateCybernetics.LOGGER.error(
                    "Failed to register cyberware rejection head layer for {} player renderer",
                    skinModel
            );
            return;
        }

        playerRenderer.addLayer(new CyberwareRejectionHeadLayer(playerRenderer));

        CreateCybernetics.LOGGER.info(
                "Registered cyberware rejection head layer for {} player renderer",
                skinModel
        );
    }
}