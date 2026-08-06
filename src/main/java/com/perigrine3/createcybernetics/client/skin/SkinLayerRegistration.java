package com.perigrine3.createcybernetics.client.skin;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.client.render.attachment.CyberwarePlayerAttachmentLayer;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
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
            wide.addLayer(new CyberwarePlayerAttachmentLayer(wide));
        }

        PlayerRenderer slim = event.getSkin(PlayerSkin.Model.SLIM);
        if (slim != null) {
            slim.addLayer(new SkinLayerHandler(slim));
            slim.addLayer(new SkinHighlightLayer(slim));
            slim.addLayer(new CyberwarePlayerAttachmentLayer(slim));
        }

        EntityRenderer<? extends ArmorStand> armorStandRenderer =
                event.getRenderer(EntityType.ARMOR_STAND);

        if (armorStandRenderer instanceof ArmorStandRenderer renderer) {
            renderer.addLayer(new ExosuitArmorStandLayer(renderer, event.getEntityModels()));
        }
    }
}