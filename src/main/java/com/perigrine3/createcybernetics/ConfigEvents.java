package com.perigrine3.createcybernetics;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ConfigEvents {
    private ConfigEvents() {}

    @SubscribeEvent
    public static void onConfigLoading(ModConfigEvent.Loading e) {
        bake(e.getConfig());
    }

    @SubscribeEvent
    public static void onConfigReloading(ModConfigEvent.Reloading e) {
        bake(e.getConfig());
    }

    private static void bake(ModConfig config) {
        if (config.getSpec() != Config.SPEC) return;

        ConfigValues.BASE_HUMANITY = Config.HUMANITY.get();
    }
}
