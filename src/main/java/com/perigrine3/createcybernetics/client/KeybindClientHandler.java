package com.perigrine3.createcybernetics.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.screen.custom.CyberwareToggleWheelScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class KeybindClientHandler {
    private KeybindClientHandler() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Toggle wheel on key press. Do NOT rely on mc.screen, because the wheel screen self-closes in init().
        while (ModKeyMappings.CYBERWARE_WHEEL.get().consumeClick()) {
            if (CyberwareToggleWheelScreen.isWheelOpen()) {
                CyberwareToggleWheelScreen.closeWheel();
                // Ensure no GUI is left open (usually already null).
                if (mc.screen != null) mc.setScreen(null);
            } else {
                mc.setScreen(new CyberwareToggleWheelScreen());
            }
        }

        // While the wheel is open, keep movement keys functioning (wheel is HUD-based, not a persistent screen).
        if (CyberwareToggleWheelScreen.isWheelOpen()) {
            passthroughMovementKeys(mc);
        }
    }

    private static void passthroughMovementKeys(Minecraft mc) {
        passthrough(mc, mc.options.keyUp);
        passthrough(mc, mc.options.keyDown);
        passthrough(mc, mc.options.keyLeft);
        passthrough(mc, mc.options.keyRight);
        passthrough(mc, mc.options.keyJump);
        passthrough(mc, mc.options.keySprint);
        passthrough(mc, mc.options.keyShift); // crouch
    }

    private static void passthrough(Minecraft mc, KeyMapping key) {
        long window = mc.getWindow().getWindow();
        var k = key.getKey();

        boolean down;
        if (k.getType() == InputConstants.Type.MOUSE) {
            down = GLFW.glfwGetMouseButton(window, k.getValue()) == GLFW.GLFW_PRESS;
        } else {
            down = InputConstants.isKeyDown(window, k.getValue());
        }

        if (key.isDown() != down) {
            key.setDown(down);
        }
    }
}
