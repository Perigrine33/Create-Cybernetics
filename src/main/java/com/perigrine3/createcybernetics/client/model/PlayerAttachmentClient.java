package com.perigrine3.createcybernetics.client.model;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class PlayerAttachmentClient {

    private PlayerAttachmentClient() {}

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ClawAttachmentModel.LAYER, ClawAttachmentModel::createLayer);
        event.registerLayerDefinition(DrillFistAttachmentModel.LAYER, DrillFistAttachmentModel::createLayer);
        event.registerLayerDefinition(OcelotPawsAttachmentModel.LAYER, OcelotPawsAttachmentModel::createLayer);
        event.registerLayerDefinition(CalfPropellerAttachmentModel.LAYER, CalfPropellerAttachmentModel::createLayer);
        event.registerLayerDefinition(SpursAttachmentModel.LAYER, SpursAttachmentModel::createLayer);
        event.registerLayerDefinition(GuardianEyeAttachmentModel.LAYER, GuardianEyeAttachmentModel::createLayer);
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        PlayerRenderer wide = event.getSkin(PlayerSkin.Model.WIDE);
        if (wide != null) wide.addLayer(new PlayerAttachmentLayer(wide));

        PlayerRenderer slim = event.getSkin(PlayerSkin.Model.SLIM);
        if (slim != null) slim.addLayer(new PlayerAttachmentLayer(slim));
    }
}

