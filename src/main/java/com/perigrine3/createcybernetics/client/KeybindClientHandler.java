package com.perigrine3.createcybernetics.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.network.payload.ArmCannonWheelPayloads;
import com.perigrine3.createcybernetics.network.payload.OpenArmCannonPayload;
import com.perigrine3.createcybernetics.network.payload.OpenSpinalInjectorPayload;
import com.perigrine3.createcybernetics.screen.custom.ArmCannonWheelScreen;
import com.perigrine3.createcybernetics.screen.custom.CyberwareToggleWheelScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class KeybindClientHandler {
    private KeybindClientHandler() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        while (ModKeyMappings.CYBERWARE_WHEEL.get().consumeClick()) {
            if (CyberwareToggleWheelScreen.isWheelOpen()) {
                CyberwareToggleWheelScreen.closeWheel();
                if (mc.screen != null) mc.setScreen(null);
            } else {
                mc.setScreen(new CyberwareToggleWheelScreen());
            }
        }

        while (ModKeyMappings.ARM_CANNON_WHEEL.get().consumeClick()) {
            if (ArmCannonWheelScreen.isOpen()) {
                ArmCannonWheelScreen.close();
                if (mc.screen != null) mc.setScreen(null);
            } else {
                ArmCannonWheelScreen.open(4);

                if (mc.player != null && mc.player.hasData(ModAttachments.CYBERWARE)) {
                    PlayerCyberwareData data = mc.player.getData(ModAttachments.CYBERWARE);
                    if (data != null) {
                        ArmCannonWheelScreen.setPreselectedIndex(data.getArmCannonSelected());
                    }
                }

                mc.setScreen(new ArmCannonWheelScreen());
            }
        }

        while (ModKeyMappings.SPINAL_INJECTOR.get().consumeClick()) {
            if (mc.screen != null) continue;
            OpenSpinalInjectorPayload payload = new OpenSpinalInjectorPayload();
            PacketDistributor.sendToServer(payload);
        }
        while (ModKeyMappings.ARM_CANNON.get().consumeClick()) {
            if (mc.screen != null) continue;
            OpenArmCannonPayload payload = new OpenArmCannonPayload();
            PacketDistributor.sendToServer(payload);
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
