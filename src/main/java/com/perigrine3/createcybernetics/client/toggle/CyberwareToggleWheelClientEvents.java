package com.perigrine3.createcybernetics.client.toggle;

import com.mojang.blaze3d.platform.InputConstants;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.client.ModKeyMappings;
import com.perigrine3.createcybernetics.screen.custom.CyberwareToggleWheelScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class CyberwareToggleWheelClientEvents {
    private CyberwareToggleWheelClientEvents() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (ModKeyMappings.CYBERWARE_WHEEL.get().consumeClick()) {
            if (mc.screen instanceof CyberwareToggleWheelScreen) {
                mc.setScreen(null);
            } else {
                mc.setScreen(new CyberwareToggleWheelScreen());
            }
        }

        if (mc.screen instanceof CyberwareToggleWheelScreen) {
            passthroughMovement(mc);
        }
    }

    private static void passthroughMovement(Minecraft mc) {
        passthrough(mc.options.keyUp);
        passthrough(mc.options.keyDown);
        passthrough(mc.options.keyLeft);
        passthrough(mc.options.keyRight);
        passthrough(mc.options.keyJump);
        passthrough(mc.options.keySprint);
        passthrough(mc.options.keyShift);
    }

    private static void passthrough(KeyMapping key) {
        long window = Minecraft.getInstance().getWindow().getWindow();

        int code = key.getKey().getValue();
        boolean logicalDown = key.isDown();
        boolean physicalDown = InputConstants.isKeyDown(window, code);

        if (physicalDown != logicalDown) {
            key.setDown(physicalDown);
        }
    }
}
