package com.perigrine3.createcybernetics.network;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModNetworking {
    private ModNetworking() {}

    private static final String PROTOCOL_VERSION = "1";

    @SubscribeEvent
    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(CreateCybernetics.MODID)
                .versioned(PROTOCOL_VERSION);

        registerAll(registrar);
    }

    private static void registerAll(PayloadRegistrar registrar) {
        ModPayloads.register(registrar);

        // Future additions go here as single lines:
    }
}
