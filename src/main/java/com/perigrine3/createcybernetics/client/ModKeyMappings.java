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

import java.util.function.Supplier;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModKeyMappings {
    private ModKeyMappings() {}

    public static final String CATEGORY = "key.categories.createcybernetics";

    public static final Lazy<KeyMapping> CYBERWARE_WHEEL = Lazy.of(() ->
            new KeyMapping("key.createcybernetics.cyberware_wheel", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, CATEGORY));

    public static final Lazy<KeyMapping> SPINAL_INJECTOR = Lazy.of(() ->
            new KeyMapping("key.createcybernetics.spinal_injector", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, CATEGORY));

    public static final Lazy<KeyMapping> ARM_CANNON = Lazy.of(() ->
            new KeyMapping("key.createcybernetics.arm_cannon", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, CATEGORY));
    public static final Lazy<KeyMapping> ARM_CANNON_WHEEL = Lazy.of(() ->
            new KeyMapping("key.createcybernetics.arm_cannon_wheel", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY));

    public static final Lazy<KeyMapping> HEAT_ENGINE = Lazy.of(() ->
            new KeyMapping("key.createcybernetics.heat_engine", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, CATEGORY));

    public static final Lazy<KeyMapping> INFOLOG = Lazy.of(() ->
            new KeyMapping("key.createcybernetics.infolog_gui", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, CATEGORY));


    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(CYBERWARE_WHEEL.get());
        event.register(SPINAL_INJECTOR.get());
        event.register(ARM_CANNON.get());
        event.register(ARM_CANNON_WHEEL.get());
        event.register(HEAT_ENGINE.get());
        event.register(INFOLOG.get());
    }
}
