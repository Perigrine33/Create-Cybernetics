package com.perigrine3.createcybernetics.screen.custom;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class CyberwareToggleWheelClientGameBus {

    private CyberwareToggleWheelClientGameBus() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (!CyberwareToggleWheelScreen.isWheelOpen()) return;

        Minecraft mc = Minecraft.getInstance();

        // If any other GUI opens, close the wheel.
        if (mc.screen != null) {
            CyberwareToggleWheelScreen.closeWheel();
            return;
        }

        // Prevent "hold LMB" from continuing to break blocks while the wheel is open.
        KeyMapping attack = mc.options.keyAttack;
        if (attack != null && attack.isDown()) {
            attack.setDown(false);
        }
    }

    @SubscribeEvent
    public static void onMouseButton(InputEvent.MouseButton.Pre event) {
        if (!CyberwareToggleWheelScreen.isWheelOpen()) return;

        // Only react on press, ignore release/repeat.
        if (event.getAction() != GLFW.GLFW_PRESS) return;

        Minecraft mc = Minecraft.getInstance();

        // RMB: CLOSE (and do not break blocks)
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            event.setCanceled(true);

            // Force-release attack so the click doesn't start mining.
            KeyMapping attack = mc.options.keyAttack;
            if (attack != null) attack.setDown(false);

            CyberwareToggleWheelScreen.closeWheel();
            return;
        }

        // LMB: TOGGLE selected (and do not place/use items)
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            event.setCanceled(true);
            CyberwareToggleWheelScreen.toggleSelected();
        }
    }
}
