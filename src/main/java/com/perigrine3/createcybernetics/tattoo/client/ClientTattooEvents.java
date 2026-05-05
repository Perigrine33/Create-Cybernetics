package com.perigrine3.createcybernetics.tattoo.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientTattooEvents {
    private ClientTattooEvents() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ClientTattooUploadDirectories.ensureCreated();
    }

    @SubscribeEvent
    public static void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientTattooRegistry.clear();
        ClientPendingTattooRegistry.clear();
        ClientTattooTextureCache.clear();
        ClientTattooAccess.clear();
    }
}