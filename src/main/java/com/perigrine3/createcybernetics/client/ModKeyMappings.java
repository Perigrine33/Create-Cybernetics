package com.perigrine3.createcybernetics.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModKeyMappings {
    private ModKeyMappings() {}

    public static final String CATEGORY = "key.categories.createcybernetics";

    public static final Lazy<KeyMapping> CYBERWARE_WHEEL = Lazy.of(() ->
            new KeyMapping("key.createcybernetics.cyberware_wheel", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, CATEGORY));

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(CYBERWARE_WHEEL.get());
    }
}
