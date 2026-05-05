package com.perigrine3.createcybernetics.tattoo;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID)
public final class TattooAutoReloadEvents {
    private static int tickCounter;

    private TattooAutoReloadEvents() {
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        TattooAutoReloadService.initialize();
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        tickCounter++;

        if (tickCounter < 40) {
            return;
        }

        tickCounter = 0;
        TattooAutoReloadService.poll(event.getServer());
    }
}