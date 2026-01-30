package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class CCItemModelProperties {
    private CCItemModelProperties() {}

    private static final ResourceLocation TRIMMED = ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "trimmed");

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            registerTrimmed(ModItems.BASECYBERWARE_LEFTLEG.get());
            registerTrimmed(ModItems.BASECYBERWARE_RIGHTLEG.get());
            registerTrimmed(ModItems.BASECYBERWARE_LEFTARM.get());
            registerTrimmed(ModItems.BASECYBERWARE_RIGHTARM.get());
        });
    }

    private static void registerTrimmed(Item item) {
        ItemProperties.register(item, TRIMMED, (stack, level, entity, seed) ->
                stack.get(DataComponents.TRIM) != null ? 1.0F : 0.0F
        );
    }
}
