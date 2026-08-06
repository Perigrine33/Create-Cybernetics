package com.perigrine3.createcybernetics.client.render.rejection;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CyberwareRejectionClientRegistration {

    private CyberwareRejectionClientRegistration() {
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(CyberwareRejectionReloadListener.INSTANCE);
    }
}