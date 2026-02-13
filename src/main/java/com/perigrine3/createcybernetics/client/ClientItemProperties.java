package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientItemProperties {
    private ClientItemProperties() {}

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(ModItems.DATA_SHARD_INFOLOG.get(), ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "dyed"),
                    (stack, level, entity, seed) -> stack.has(DataComponents.DYED_COLOR) ? 1.0F : 0.0F);

            ItemProperties.register(ModItems.BASECYBERWARE_LEFTARM.get(), ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "dyed"),
                    (stack, level, entity, seed) -> stack.has(DataComponents.DYED_COLOR) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.BASECYBERWARE_RIGHTARM.get(), ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "dyed"),
                    (stack, level, entity, seed) -> stack.has(DataComponents.DYED_COLOR) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.BASECYBERWARE_LEFTLEG.get(), ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "dyed"),
                    (stack, level, entity, seed) -> stack.has(DataComponents.DYED_COLOR) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.BASECYBERWARE_RIGHTLEG.get(), ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "dyed"),
                    (stack, level, entity, seed) -> stack.has(DataComponents.DYED_COLOR) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.BASECYBERWARE_CYBEREYES.get(), ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "dyed"),
                    (stack, level, entity, seed) -> stack.has(DataComponents.DYED_COLOR) ? 1.0F : 0.0F);
        });
    }
}
