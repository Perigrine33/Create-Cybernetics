package com.perigrine3.createcybernetics;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue HUMANITY = BUILDER
            .comment("Base Humanity Value")
            .defineInRange("humanity", 100, 50, 1000);

    public static final ModConfigSpec.BooleanValue KEEP_CYBERWARE = BUILDER
            .comment("Keep Cyberware on Death. If true, cyberware will not drop and will persist through death.")
            .define("keepCyberware", false);





    public static final ModConfigSpec SPEC = BUILDER.build();




    public static void bake() {
        ConfigValues.BASE_HUMANITY = HUMANITY.get();
        ConfigValues.KEEP_CYBERWARE = KEEP_CYBERWARE.get();
    }

    @SubscribeEvent
    public static void onConfigLoading(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == SPEC) bake();
    }

    @SubscribeEvent
    public static void onConfigReloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) bake();
    }
}
