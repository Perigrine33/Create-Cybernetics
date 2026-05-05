package com.perigrine3.createcybernetics.client.event;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.screen.custom.TattooOverlayBrowserScreen;
import com.perigrine3.createcybernetics.tattoo.client.ClientTattooAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(
        modid = CreateCybernetics.MODID,
        value = Dist.CLIENT
)
public final class TattooPauseMenuButtonEvents {
    private TattooPauseMenuButtonEvents() {
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof PauseScreen pauseScreen)) {
            return;
        }

        if (!ClientTattooAccess.canOpenBrowser()) {
            return;
        }

        int x = pauseScreen.width - 82;
        int y = 8;

        Button button = Button.builder(
                        Component.literal("Tattoos"),
                        b -> Minecraft.getInstance().setScreen(new TattooOverlayBrowserScreen(pauseScreen))
                )
                .bounds(x, y, 74, 20)
                .build();

        event.addListener(button);
    }
}