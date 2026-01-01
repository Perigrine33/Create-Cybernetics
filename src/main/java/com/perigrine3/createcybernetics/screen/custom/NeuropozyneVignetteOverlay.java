package com.perigrine3.createcybernetics.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.effect.ModEffects;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

import java.util.UUID;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class NeuropozyneVignetteOverlay {

    private static final ResourceLocation LAYER_ID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "neuropozyne_vignette");

    private static final int VIGNETTE_RGB = 0xC4D925;

    private static final int PULSE_TICKS = 240;
    private static final float MAX_ALPHA = 0.5f;
    private static final float THICKNESS_RATIO = 0.15f;
    private static final int STEPS = 32;

    private static boolean hadNeuropozyne = false;
    private static int pulseStartTick = -1;

    private static UUID lastPlayerUUID = null;

    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(LAYER_ID, NeuropozyneVignetteOverlay::render);
    }

    private static void render(GuiGraphics gui, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        if (mc.options.hideGui) return;

        boolean has = player.hasEffect(ModEffects.NEUROPOZYNE);

        UUID id = player.getUUID();
        if (lastPlayerUUID == null || !lastPlayerUUID.equals(id)) {
            lastPlayerUUID = id;
            hadNeuropozyne = has;
            pulseStartTick = -1;
            return;
        }

        if (has && !hadNeuropozyne) {
            pulseStartTick = player.tickCount;
        }
        hadNeuropozyne = has;

        if (pulseStartTick < 0) return;

        float pt = delta.getGameTimeDeltaPartialTick(false);
        float age = (player.tickCount + pt) - pulseStartTick;
        if (age >= PULSE_TICKS) {
            pulseStartTick = -1;
            return;
        }

        float t = Mth.clamp(age / (float) PULSE_TICKS, 0f, 1f);
        float eased = 1f - smoothstep(t); // 1 -> 0
        float alpha = Mth.clamp(eased * MAX_ALPHA, 0f, MAX_ALPHA);
        if (alpha <= 0.001f) return;

        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();
        int thick = (int) (Math.min(w, h) * THICKNESS_RATIO);
        if (thick <= 0) return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        drawVignetteStrips(gui, w, h, thick, VIGNETTE_RGB, alpha);

        RenderSystem.disableBlend();
    }

    private static void drawVignetteStrips(GuiGraphics gui, int w, int h, int thick, int rgb, float alpha) {
        for (int i = 0; i < STEPS; i++) {
            float u0 = i / (float) STEPS;
            float u1 = (i + 1) / (float) STEPS;

            int x0 = (int) (u0 * thick);
            int x1 = (int) (u1 * thick);

            float edge = 1f - u0;
            float a = alpha * (edge * edge);
            int argb = argb(a, rgb);

            gui.fill(0 + x0, 0, 0 + x1, h, argb);
            gui.fill(w - x1, 0, w - x0, h, argb);
        }

        for (int i = 0; i < STEPS; i++) {
            float u0 = i / (float) STEPS;
            float u1 = (i + 1) / (float) STEPS;

            int y0 = (int) (u0 * thick);
            int y1 = (int) (u1 * thick);

            float edge = 1f - u0;
            float a = alpha * (edge * edge);
            int argb = argb(a, rgb);

            gui.fill(0, 0 + y0, w, 0 + y1, argb);
            gui.fill(0, h - y1, w, h - y0, argb);
        }
    }

    private static int argb(float alpha01, int rgb) {
        int a = Mth.clamp((int) (alpha01 * 255f), 0, 255);
        return (a << 24) | (rgb & 0xFFFFFF);
    }

    private static float smoothstep(float x) {
        x = Mth.clamp(x, 0f, 1f);
        return x * x * (3f - 2f * x);
    }

    private NeuropozyneVignetteOverlay() {}
}
