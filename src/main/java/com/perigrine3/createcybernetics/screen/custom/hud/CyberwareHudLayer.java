package com.perigrine3.createcybernetics.screen.custom.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CyberwareHudLayer {

    // Battery/normal HUD layer (unchanged intent)
    public static final ResourceLocation LAYER_ID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware_hud");

    // Spinner/crosshair overlay layer (NEW)
    public static final ResourceLocation CROSSHAIR_LAYER_ID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware_hud_crosshair");

    // ---- Battery textures ----
    private static final ResourceLocation FRAME =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/hud/hud_batteryframe.png");
    private static final ResourceLocation FRAME_EMPTY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/hud/hud_batteryframe_empty.png");

    private static final ResourceLocation BARS1 =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/hud/hud_batterybars1.png");
    private static final ResourceLocation BARS1_EMPTY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/hud/hud_batterybars1_empty.png");

    private static final ResourceLocation BARS2 =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/hud/hud_batterybars2.png");
    private static final ResourceLocation BARS2_EMPTY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/hud/hud_batterybars2_empty.png");

    // ---- Center Fram texture ----
    private static final ResourceLocation CENTER_OVERLAY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/hud/hud_overlay.png");

    // ---- Spinning crosshair overlay texture ----
    private static final ResourceLocation CENTER_SPINNER =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/hud/hud_overlay_circle.png");

    private static final int OVERLAY_W = 2048;
    private static final int OVERLAY_H = 1055;
    private static final int SPINNER_W = 2048;
    private static final int SPINNER_H = 1055;
    // Overlay tuning
    private static final float OVERLAY_MAX_SCREEN_FRACTION = 0.95f;
    private static final float OVERLAY_ALPHA = 0.5f;
    private static final boolean OVERLAY_DRAW_BEHIND_BATTERY = true;
    // Spinner tuning
    private static final float SPINNER_MAX_SCREEN_FRACTION = 1.25f;
    private static final float SPINNER_ALPHA = 0.1f;
    private static float SPINNER_OFFSET_X_PX = -0.5f;
    private static float SPINNER_OFFSET_Y_PX = -0.5f;

    private static float SPINNER_DEG_PER_SECOND = 10.0f;

    private static final int TEX_W = 13;
    private static final int TEX_H = 25;
    private static final int INNER_X = 1;
    private static final int INNER_Y = 2;
    private static final int INNER_W = 10;
    private static final int INNER_H = 21;
    private static final int MARGIN_X = 122;
    private static final int MARGIN_Y = 2;
    private static final float BATTERY_SCALE = 1.5f;
    private static final float VALUE_SCALE = 0.8f;
    private static final int VALUE_PADDING_PX = 2;
    private static final int VALUE_COLOR = 0xFFFFFF;
    private static final int VALUE_COLOR_LOW = 0xFF5555;
    private static final boolean VALUE_SHADOW = true;
    private static final float LOW_THRESHOLD = 0.25f;

    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, LAYER_ID, CyberwareHudLayer::renderHud);
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, CROSSHAIR_LAYER_ID, CyberwareHudLayer::renderCrosshairOverlay);
    }

    private static void renderHud(GuiGraphics gg, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        if (mc.options.hideGui) return;
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) return;
        if (!CyberwareInstallQueries.hasHudAccess(player)) return;

        int screenW = gg.guiWidth();
        int screenH = gg.guiHeight();

        if (OVERLAY_DRAW_BEHIND_BATTERY) {
            renderCenteredImageAutoFit(gg, CENTER_OVERLAY, OVERLAY_W, OVERLAY_H, screenW, screenH,
                    OVERLAY_MAX_SCREEN_FRACTION, OVERLAY_ALPHA);
        }

        TickSnapshot s = ClientEnergyState.getSnapshot();

        int current;
        int capacity;
        int net;

        if (s != null) {
            current = s.storedAfter();
            capacity = s.capacity();
            net = s.netDeltaPerTick();
        } else {
            var data = player.getData(ModAttachments.CYBERWARE);
            current = (data != null) ? data.getEnergyStored() : 0;
            capacity = (data != null) ? data.getTotalEnergyCapacity(player) : 0;
            net = 0;
        }

        int capForPct = Math.max(1, capacity);
        float pct = Mth.clamp(current / (float) capForPct, 0f, 1f);
        boolean low = pct <= LOW_THRESHOLD;

        int scaledW = Math.round(TEX_W * BATTERY_SCALE);
        int scaledH = Math.round(TEX_H * BATTERY_SCALE);

        int x = MARGIN_X;
        int y = screenH - scaledH - MARGIN_Y;

        renderEnergyValueAboveBattery(gg, mc, current, capacity, x, y, scaledW, low);
        renderBatteryScaled(gg, x, y, current, capForPct, net);

        if (!OVERLAY_DRAW_BEHIND_BATTERY) {
            renderCenteredImageAutoFit(gg, CENTER_OVERLAY, OVERLAY_W, OVERLAY_H, screenW, screenH,
                    OVERLAY_MAX_SCREEN_FRACTION, OVERLAY_ALPHA);
        }
    }

    private static void renderCrosshairOverlay(GuiGraphics gg, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        if (mc.options.hideGui) return;
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) return;
        if (!CyberwareInstallQueries.hasHudAccess(player)) return;

        int screenW = gg.guiWidth();
        int screenH = gg.guiHeight();

        float partialTick = 0.0f;
        try {
            partialTick = delta.getGameTimeDeltaPartialTick(true);
        } catch (Throwable ignored) {
            partialTick = 0.0f;
        }

        renderSpinningCenteredImageAutoFit(
                gg,
                CENTER_SPINNER,
                SPINNER_W, SPINNER_H,
                screenW, screenH,
                SPINNER_MAX_SCREEN_FRACTION,
                SPINNER_ALPHA,
                player.tickCount,
                partialTick,
                SPINNER_DEG_PER_SECOND,
                SPINNER_OFFSET_X_PX,
                SPINNER_OFFSET_Y_PX
        );
    }

    private static void renderCenteredImageAutoFit(GuiGraphics gg, ResourceLocation tex, int texW, int texH,
                                                int screenW, int screenH, float maxScreenFraction, float alpha) {
        float sx = (screenW * maxScreenFraction) / (float) texW;
        float sy = (screenH * maxScreenFraction) / (float) texH;
        float scale = Math.min(sx, sy);
        scale = Math.min(scale, 1.0f);

        int drawW = Math.round(texW * scale);
        int drawH = Math.round(texH * scale);

        int x = (screenW - drawW) / 2;
        int y = (screenH - drawH) / 2;

        gg.pose().pushPose();
        gg.pose().translate(x, y, 0);
        gg.pose().scale(scale, scale, 1.0f);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);

        gg.blit(tex, 0, 0, 0, 0, texW, texH, texW, texH);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();

        gg.pose().popPose();
    }

    private static void renderSpinningCenteredImageAutoFit(GuiGraphics gg, ResourceLocation tex, int texW, int texH, int screenW, int screenH,
                    float maxScreenFraction, float alpha, int tickCount, float partialTick, float degPerSecond, float offsetXPx, float offsetYPx) {
        float sx = (screenW * maxScreenFraction) / (float) texW;
        float sy = (screenH * maxScreenFraction) / (float) texH;
        float scale = Math.min(sx, sy);
        scale = Math.min(scale, 1.0f);

        float timeSeconds = (tickCount + partialTick) / 20.0f;
        float angleDeg = (timeSeconds * degPerSecond) % 360.0f;

        gg.pose().pushPose();
        gg.pose().translate((screenW / 2.0f) + offsetXPx, (screenH / 2.0f) + offsetYPx, 0);
        gg.pose().mulPose(Axis.ZP.rotationDegrees(angleDeg));
        gg.pose().scale(scale, scale, 1.0f);
        gg.pose().translate(-texW / 2.0f, -texH / 2.0f, 0);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);

        gg.blit(tex, 0, 0, 0, 0, texW, texH, texW, texH);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();

        gg.pose().popPose();
    }


    private static void renderEnergyValueAboveBattery(
            GuiGraphics gg, Minecraft mc,
            int current, int capacity,
            int batteryX, int batteryY, int scaledBatteryW,
            boolean low
    ) {
        String text = current + "/" + capacity;

        int scaledTextH = Math.round(mc.font.lineHeight * VALUE_SCALE);
        int textY = batteryY - VALUE_PADDING_PX - scaledTextH;

        int scaledTextW = Math.round(mc.font.width(text) * VALUE_SCALE);
        int textX = batteryX + (scaledBatteryW / 2) - (scaledTextW / 2);

        int color = low ? VALUE_COLOR_LOW : VALUE_COLOR;

        gg.pose().pushPose();
        gg.pose().translate(textX, textY, 0);
        gg.pose().scale(VALUE_SCALE, VALUE_SCALE, 1.0f);
        gg.drawString(mc.font, text, 0, 0, color, VALUE_SHADOW);
        gg.pose().popPose();
    }

    private static void renderBatteryScaled(GuiGraphics gg, int x, int y, int currentPower, int maxPower, int netPowerPerTick) {
        gg.pose().pushPose();
        gg.pose().translate(x, y, 0);
        gg.pose().scale(BATTERY_SCALE, BATTERY_SCALE, 1.0f);
        renderBattery(gg, 0, 0, currentPower, maxPower, netPowerPerTick);
        gg.pose().popPose();
    }

    public static final class CyberwareInstallQueries {
        private CyberwareInstallQueries() {}

        public static boolean hasHudAccess(LocalPlayer player) {
            var data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return false;

            return data.hasSpecificItem(ModItems.EYEUPGRADES_HUDLENS.get(), CyberwareSlot.EYES)
                    || data.hasSpecificItem(ModItems.EYEUPGRADES_HUDJACK.get(), CyberwareSlot.EYES);
        }
    }

    private static void renderBattery(GuiGraphics gg, int x, int y, int currentPower, int maxPower, int netPowerPerTick) {
        float pct = (maxPower <= 0) ? 0f : (currentPower / (float) maxPower);
        pct = Mth.clamp(pct, 0f, 1f);

        int fillPx = Math.round(pct * INNER_H);
        fillPx = Mth.clamp(fillPx, 0, INNER_H);

        int usedPx = INNER_H - fillPx;

        boolean low = pct <= LOW_THRESHOLD;

        ResourceLocation frame = low ? FRAME_EMPTY : FRAME;
        ResourceLocation bars1 = low ? BARS1_EMPTY : BARS1;
        ResourceLocation bars2 = low ? BARS2_EMPTY : BARS2;

        if (fillPx > 0) {
            int dstX = x + INNER_X;
            int dstY = y + INNER_Y + usedPx;

            int srcU = INNER_X;
            int srcV = INNER_Y + usedPx;

            blit(gg, bars2, dstX, dstY, srcU, srcV, INNER_W, fillPx);
            blit(gg, bars1, dstX, dstY, srcU, srcV, INNER_W, fillPx);
        }

        blit(gg, frame, x, y, 0, 0, TEX_W, TEX_H);
    }

    private static void blit(GuiGraphics gg, ResourceLocation tex, int x, int y, int u, int v, int w, int h) {
        gg.blit(tex, x, y, u, v, w, h, TEX_W, TEX_H);
    }

    public record TickSnapshot(
            int generatedPerTick,
            int consumedPerTick,
            int storedBefore,
            int storedAfter,
            int capacity,
            int netDeltaPerTick
    ) {}

    public static final class ClientEnergyState {
        private static volatile TickSnapshot LAST = null;

        private ClientEnergyState() {}

        public static void update(TickSnapshot snapshot) {
            LAST = snapshot;
        }

        public static TickSnapshot getSnapshot() {
            return LAST;
        }
    }
}
