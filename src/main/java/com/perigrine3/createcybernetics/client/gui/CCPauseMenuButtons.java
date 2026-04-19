package com.perigrine3.createcybernetics.client.gui;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.Locale;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class CCPauseMenuButtons {

    private static final int ICON_W = 20;
    private static final int GAP_PX = 4;

    private static final String KEY_SKIN_CUSTOMIZATION = "options.skinCustomisation";
    private static final String KEY_VIDEO_SETTINGS = "options.video";

    private static final String CC_MARKER_ID = "createcybernetics_pause_buttons";

    private CCPauseMenuButtons() {}

    @SubscribeEvent
    public static void onScreenInitPost(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof OptionsScreen options)) return;
        if (alreadyHasOurButtons(event)) return;

        AbstractWidget skinBtn = findButtonByTranslatableKey(event, KEY_SKIN_CUSTOMIZATION);
        if (skinBtn == null) skinBtn = findButtonByTextFallback(event, "skin");

        AbstractWidget videoBtn = findButtonByTranslatableKey(event, KEY_VIDEO_SETTINGS);
        if (videoBtn == null) videoBtn = findButtonByTextFallback(event, "video");

        if (skinBtn != null) {
            int x = skinBtn.getX() - (ICON_W + GAP_PX);
            int y = skinBtn.getY();

            CCIconButton eyeBtn = new CCIconButton(
                    x, y, ICON_W, skinBtn.getHeight(),
                    CCIconButton.Icon.CYBEREYE_SKIN,
                    Component.translatable("gui.createcybernetics.pause.cybereye_skin"),
                    CC_MARKER_ID,
                    () -> openCybereyeSkin(options)
            );

            event.addListener(eyeBtn);
        }

        if (videoBtn != null) {
            int x = videoBtn.getX() - (ICON_W + GAP_PX);
            int y = videoBtn.getY();

            CCIconButton hudBtn = new CCIconButton(
                    x, y, ICON_W, videoBtn.getHeight(),
                    CCIconButton.Icon.HUD_LAYOUT,
                    Component.translatable("gui.createcybernetics.pause.hud_layout"),
                    CC_MARKER_ID,
                    () -> openHudLayout(options)
            );

            event.addListener(hudBtn);
        }
    }

    private static boolean alreadyHasOurButtons(ScreenEvent.Init.Post event) {
        for (var child : event.getListenersList()) {
            if (child instanceof CCIconButton b) {
                if (CC_MARKER_ID.equals(b.cc$getMarkerId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void openHudLayout(OptionsScreen parent) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;
        mc.setScreen(new HudLayoutScreen(parent));
    }

    private static void openCybereyeSkin(OptionsScreen parent) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;
        mc.setScreen(new CybereyeSkinConfigScreen(parent));
    }

    private static AbstractWidget findButtonByTranslatableKey(ScreenEvent.Init.Post event, String key) {
        for (var child : event.getListenersList()) {
            if (!(child instanceof AbstractWidget w)) continue;

            String k = getTranslatableKey(w.getMessage());
            if (k != null && k.equals(key)) return w;
        }
        return null;
    }

    private static AbstractWidget findButtonByTextFallback(ScreenEvent.Init.Post event, String needleLower) {
        for (var child : event.getListenersList()) {
            if (!(child instanceof AbstractWidget w)) continue;

            String s = w.getMessage() != null ? w.getMessage().getString() : "";
            if (s.toLowerCase(Locale.ROOT).contains(needleLower)) return w;
        }
        return null;
    }

    private static String getTranslatableKey(Component c) {
        if (c == null) return null;
        if (c.getContents() instanceof TranslatableContents tc) {
            return tc.getKey();
        }
        return null;
    }
}