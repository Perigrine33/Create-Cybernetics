package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class HudConfigClientEvents {

    private HudConfigClientEvents() {}

    @SubscribeEvent
    public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        LocalPlayer p = Minecraft.getInstance().player;
        if (p != null) {
            HudConfigClient.reload(p.getUUID());
        }
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        LocalPlayer p = Minecraft.getInstance().player;
        if (p != null) {
            HudConfigClient.invalidate(p.getUUID());
        }
    }
}
