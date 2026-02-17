package com.perigrine3.createcybernetics.client.gui;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.PauseScreen;
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

    private static final String KEY_OPTIONS = "menu.options";
    private static final String KEY_MODS_FML = "fml.menu.mods";
    private static final String KEY_MODS_NEOFORGE = "neoforge.menu.mods";
    private static final String CC_MARKER_ID = "createcybernetics_pause_buttons";

    private CCPauseMenuButtons() {}

    @SubscribeEvent
    public static void onScreenInitPost(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof PauseScreen pause)) return;
        if (alreadyHasOurButtons(event)) return;

        AbstractWidget optionsBtn = findButtonByTranslatableKey(event, KEY_OPTIONS);
        AbstractWidget modsBtn = findButtonByTranslatableKey(event, KEY_MODS_FML);
        if (modsBtn == null) modsBtn = findButtonByTranslatableKey(event, KEY_MODS_NEOFORGE);
        if (modsBtn == null) modsBtn = findButtonByTextFallback(event, "mods");

        if (optionsBtn != null) {
            int x = optionsBtn.getX() - (ICON_W + GAP_PX);
            int y = optionsBtn.getY();

            CCIconButton hudBtn = new CCIconButton(
                    x, y, ICON_W, optionsBtn.getHeight(),
                    CCIconButton.Icon.HUD_LAYOUT,
                    Component.translatable("gui.createcybernetics.pause.hud_layout"),
                    CC_MARKER_ID,
                    () -> openHudLayout(pause)
            );

            event.addListener(hudBtn);
        }

        if (modsBtn != null) {
            int x = modsBtn.getX() - (ICON_W + GAP_PX);
            int y = modsBtn.getY();

            CCIconButton eyeBtn = new CCIconButton(
                    x, y, ICON_W, modsBtn.getHeight(),
                    CCIconButton.Icon.CYBEREYE_SKIN,
                    Component.translatable("gui.createcybernetics.pause.cybereye_skin"),
                    CC_MARKER_ID,
                    () -> openCybereyeSkin(pause)
            );

            event.addListener(eyeBtn);
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

    private static void openHudLayout(PauseScreen parent) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;
        mc.setScreen(new HudLayoutScreen(parent));
    }

    private static void openCybereyeSkin(PauseScreen parent) {
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
