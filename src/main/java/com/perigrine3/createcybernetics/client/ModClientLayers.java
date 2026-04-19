package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.client.render.RobosurgeonSkeletonOverlayLayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModClientLayers {
    private ModClientLayers() {}

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        EntityRenderer<? extends AbstractSkeleton> renderer = event.getRenderer(EntityType.SKELETON);
        if (renderer instanceof SkeletonRenderer skeletonRenderer) {
            skeletonRenderer.addLayer(new RobosurgeonSkeletonOverlayLayer<>(skeletonRenderer));
        }
    }
}