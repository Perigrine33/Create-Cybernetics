package com.perigrine3.createcybernetics.screen.custom.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.client.HudConfigClient;
import com.perigrine3.createcybernetics.client.gui.HudLayoutScreen;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.compat.creatingspace.CreatingSpaceSuitPredicate;
import com.perigrine3.createcybernetics.compat.northstar.CopernicusSuitPredicate;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.item.cyberware.eyes.CybereyeItem;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CyberwareHudLayer {

    private CyberwareHudLayer() {}

    public static final ResourceLocation HUD_LAYER_LEFT =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware_hud_left");

    public static final ResourceLocation HUD_LAYER_RIGHT =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware_hud_right");

    public static final ResourceLocation HUD_WIDGETS_LAYER =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware_hud_widgets");

    public static final ResourceLocation CROSSHAIR_LAYER =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware_hud_crosshair");

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

    private static final ResourceLocation CENTER_OVERLAY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/hud/hud_overlay.png");

    private static final ResourceLocation CENTER_SPINNER =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/hud/hud_overlay_circle.png");

    private static final int OVERLAY_W = 2048;
    private static final int OVERLAY_H = 1055;
    private static final int OVERLAY_HALF_W = OVERLAY_W / 2;

    private static final int SPINNER_W = 2048;
    private static final int SPINNER_H = 1055;

    private static final float OVERLAY_MAX_SCREEN_FRACTION = 0.95f;
    private static final float OVERLAY_ALPHA = 0.5f;

    private static final float SPINNER_MAX_SCREEN_FRACTION = 1.25f;
    private static final float SPINNER_ALPHA = 0.1f;
    private static final float SPINNER_OFFSET_X_PX = -0.5f;
    private static final float SPINNER_OFFSET_Y_PX = -0.5f;
    private static final float SPINNER_DEG_PER_SECOND = 10.0f;

    private static final int TEX_W = 13;
    private static final int TEX_H = 25;
    private static final int INNER_X = 1;
    private static final int INNER_Y = 2;
    private static final int INNER_W = 10;
    private static final int INNER_H = 21;

    private static final int HUD_TINT_WHITE_ARGB = 0xFFFFFFFF;
    private static final int HUD_TINT_LOW_RED_ARGB = 0xFFFF5555;

    private static final float BATTERY_SCALE_PX = 4f;
    private static final float VALUE_SCALE_REL = 0.75f;

    private static final int VALUE_PADDING_PX = 2;
    private static final int VALUE_COLOR = 0xFFFFFF;
    private static final int VALUE_COLOR_LOW = 0xFF5555;
    private static final boolean VALUE_SHADOW = true;
    private static final float LOW_THRESHOLD = 0.25f;

    private static final int ENERGY_STATS_LINE_GAP_PX = 1;
    private static final int ENERGY_STATS_EXTRA_PADDING_PX = 2;

    private static final int COORDS_LINE_GAP_PX = 1;
    private static final boolean COORDS_SHADOW = true;

    private static final int TOGGLE_ROW_GAP_PX = 2;
    private static final int TOGGLE_ICON_TEXT_GAP_PX = 4;
    private static final boolean TOGGLE_SHADOW = true;
    private static final int TOGGLE_MAX_ROWS = 16;

    private static final Component ENABLED_TXT = Component.literal("ENABLED");
    private static final Component DISABLED_TXT = Component.literal("DISABLED");
    private static final int TOGGLE_ENABLED_COLOR = 0x55FF55;

    private static final int SHARDS_ICON_GAP_PX = 0;
    private static final float SHARDS_SCALE_REL = 1.75f;

    private static final boolean TARGET_SHADOW = true;

    public static final int COPERNICUS_OXYGEN_MAX_DISPLAY = 3000;
    private static final int OXYGEN_TEXT_COLOR = 0xFFFFFF;
    private static final int OXYGEN_TEXT_COLOR_LOW = 0xFF5555;
    private static final boolean OXYGEN_TEXT_SHADOW = true;
    private static final float OXYGEN_LOW_THRESHOLD = 0.25f;

    private static final ResourceLocation HEAT_FLAME_TEX =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/flame_indicator.png");

    private static final int HEAT_FLAME_TEX_W = 16;
    private static final int HEAT_FLAME_TEX_H = 16;
    private static final int HEAT_FLAME_DRAW_W = 16;
    private static final int HEAT_FLAME_DRAW_H = 16;
    private static final int HEAT_FLAME_TEXT_GAP_PX = 4;
    private static final int HEAT_TIME_OFFSET_X_PX = 0;
    private static final int HEAT_TIME_OFFSET_Y_PX = 165;
    private static final float HEAT_TIME_SCALE_REL = 1f;

    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, HUD_LAYER_LEFT, CyberwareHudLayer::renderHudLayerLeft);
        event.registerAbove(VanillaGuiLayers.HOTBAR, HUD_LAYER_RIGHT, CyberwareHudLayer::renderHudLayerRight);
        event.registerAbove(VanillaGuiLayers.HOTBAR, HUD_WIDGETS_LAYER, CyberwareHudLayer::renderHud);
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, CROSSHAIR_LAYER, CyberwareHudLayer::renderCrosshairOverlay);
    }

    public enum HudLayerSide {
        LEFT,
        RIGHT
    }

    public record HudRect(int x, int y, int w, int h) {
        public boolean containsPixel(int px, int py) {
            return px >= x && py >= y && px < x + w && py < y + h;
        }

        public boolean containsGui(double guiX, double guiY, Minecraft mc) {
            double guiScale = mc.getWindow().getGuiScale();
            int px = Math.round((float) (guiX * guiScale));
            int py = Math.round((float) (guiY * guiScale));
            return containsPixel(px, py);
        }
    }

    private record BatteryRectData(HudRect rawRect, HudRect clampedRect, int rawIconX, int rawIconY, int iconW, int iconH) {}

    public record HudWidgetRects(
            HudRect hudLeft,
            HudRect hudRight,
            HudRect battery,
            HudRect coords,
            HudRect toggleList,
            HudRect shards,
            HudRect target
    ) {
        public HudRect rect(HudConfigClient.HudComponent component) {
            return switch (component) {
                case HUD_LEFT -> hudLeft;
                case HUD_RIGHT -> hudRight;
                case BATTERY -> battery;
                case COORDS -> coords;
                case TOGGLE_LIST -> toggleList;
                case SHARDS -> shards;
                case TARGET -> target;
            };
        }
    }

    public static HudWidgetRects computeRectsForConfig(Minecraft mc, HudConfigClient.HudConfig cfg) {
        int screenPxW = mc.getWindow().getScreenWidth();
        int screenPxH = mc.getWindow().getScreenHeight();

        float overlayScale = overlayBaseScale(screenPxW, screenPxH) * cfg.hudLayer.scale;
        int overlayHalfW = Math.round(OVERLAY_HALF_W * overlayScale);
        int overlayH = Math.round(OVERLAY_H * overlayScale);

        int hudLayerY = clampInt(cfg.hudLayer.pixelY(screenPxH), 0, Math.max(0, screenPxH - overlayH));

        HudRect hudLeft = new HudRect(
                clampInt(cfg.hudLayer.leftPixelX(screenPxW), 0, Math.max(0, screenPxW - overlayHalfW)),
                hudLayerY,
                overlayHalfW,
                overlayH
        );

        HudRect hudRight = new HudRect(
                clampInt(cfg.hudLayer.rightPixelX(screenPxW), 0, Math.max(0, screenPxW - overlayHalfW)),
                hudLayerY,
                overlayHalfW,
                overlayH
        );

        BatteryRectData batteryData = computeBatteryRectData(mc, cfg, screenPxW, screenPxH);

        HudConfigClient.ComponentLayout coordsLayout = cfg.coords;
        HudConfigClient.ComponentLayout toggleLayout = cfg.toggleList;
        HudConfigClient.ComponentLayout shardsLayout = cfg.shards;
        HudConfigClient.ComponentLayout targetLayout = cfg.target;

        float coordsScale = BATTERY_SCALE_PX * VALUE_SCALE_REL * coordsLayout.scale;
        int coordsW = Math.round(190 * coordsScale);
        int coordsH = Math.round((mc.font.lineHeight * coordsScale) * 2) + COORDS_LINE_GAP_PX;

        HudRect coords = clampRectToScreen(
                new HudRect(coordsLayout.pixelX(screenPxW), coordsLayout.pixelY(screenPxH), coordsW, coordsH),
                screenPxW,
                screenPxH
        );

        float toggleTextScale = BATTERY_SCALE_PX * VALUE_SCALE_REL * toggleLayout.scale;
        float toggleIconScale = toggleTextScale;

        int iconPx = Math.round(16f * toggleIconScale);
        int lineH = Math.round(mc.font.lineHeight * toggleTextScale);
        int rowH = Math.max(iconPx, lineH);
        int rows = Math.max(1, Math.min(TOGGLE_MAX_ROWS, 10));

        int enabledW = Math.round(mc.font.width(ENABLED_TXT.getString()) * toggleTextScale);
        int disabledW = Math.round(mc.font.width(DISABLED_TXT.getString()) * toggleTextScale);
        int statusW = Math.max(enabledW, disabledW);

        int toggleW = iconPx + TOGGLE_ICON_TEXT_GAP_PX + statusW;
        int toggleH = rows * rowH + Math.max(0, rows - 1) * TOGGLE_ROW_GAP_PX;

        HudRect toggleList = clampRectToScreen(
                new HudRect(toggleLayout.pixelX(screenPxW), toggleLayout.pixelY(screenPxH), toggleW, toggleH),
                screenPxW,
                screenPxH
        );

        float shardScale = BATTERY_SCALE_PX * VALUE_SCALE_REL * SHARDS_SCALE_REL * shardsLayout.scale;
        int shardIconPx = Math.round(16f * shardScale);
        int shardsW = (2 * shardIconPx) + SHARDS_ICON_GAP_PX;
        int shardsH = shardIconPx;

        HudRect shards = clampRectToScreen(
                new HudRect(shardsLayout.pixelX(screenPxW), shardsLayout.pixelY(screenPxH), shardsW, shardsH),
                screenPxW,
                screenPxH
        );

        float targetScale = BATTERY_SCALE_PX * VALUE_SCALE_REL * targetLayout.scale;
        int targetW = Math.round(140 * targetScale);
        int targetH = Math.round(mc.font.lineHeight * targetScale);

        HudRect target = clampRectToScreen(
                new HudRect(targetLayout.pixelX(screenPxW), targetLayout.pixelY(screenPxH), targetW, targetH),
                screenPxW,
                screenPxH
        );

        return new HudWidgetRects(
                hudLeft,
                hudRight,
                batteryData.clampedRect(),
                coords,
                toggleList,
                shards,
                target
        );
    }

    public static void renderHudLayerPreview(GuiGraphics gg, float partialTick, HudConfigClient.HudConfig cfg) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        if (mc.options.hideGui) return;
        if (!cfg.hudLayer.enabled) return;

        int hudTintArgb = resolveHudTintArgb(player);

        renderHudLayerSidePixels(gg, cfg, HudLayerSide.LEFT, hudTintArgb);
        renderHudLayerSidePixels(gg, cfg, HudLayerSide.RIGHT, hudTintArgb);
    }

    public static void renderHudPreview(GuiGraphics gg, float partialTick, HudConfigClient.HudConfig cfg) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        if (mc.options.hideGui) return;

        double guiScale = mc.getWindow().getGuiScale();
        int screenPxW = mc.getWindow().getScreenWidth();
        int screenPxH = mc.getWindow().getScreenHeight();

        float hudTextScale = BATTERY_SCALE_PX * VALUE_SCALE_REL;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);

        int hudTintArgb = resolveHudTintArgb(player);

        int current = data != null ? data.getEnergyStored() : 0;
        int capacity = data != null ? data.getTotalEnergyCapacity(player) : 0;

        TickSnapshot snap = ClientEnergyState.getSnapshot();
        EnergyRates localRates = data != null ? computeClientEnergyRates(player, data) : EnergyRates.ZERO;

        int genPerTick = localRates.generatedPerTick;
        int usePerTick = localRates.requiredPerTick;

        if (snap != null) {
            boolean snapMeaningful = snap.generatedPerTick() != 0 || snap.consumedPerTick() != 0;
            if (snapMeaningful) {
                genPerTick = snap.generatedPerTick();
                usePerTick = snap.consumedPerTick();
            }
        }

        int netPerTick = genPerTick - usePerTick;

        int capForPct = Math.max(1, capacity);
        float pct = Mth.clamp(current / (float) capForPct, 0f, 1f);
        boolean low = pct <= LOW_THRESHOLD;

        int batteryTintArgb = low ? HUD_TINT_LOW_RED_ARGB : hudTintArgb;

        gg.pose().pushPose();
        gg.pose().scale((float) (1.0 / guiScale), (float) (1.0 / guiScale), 1.0f);

        if (cfg.battery.enabled) {
            renderBatteryWithModePixels(
                    gg,
                    mc,
                    screenPxW,
                    screenPxH,
                    current,
                    capacity,
                    capForPct,
                    genPerTick,
                    usePerTick,
                    netPerTick,
                    low,
                    hudTintArgb,
                    batteryTintArgb,
                    cfg
            );
        }

        renderHeatEngineRemainingBurnAboveHotbarPixels(
                gg,
                mc,
                player,
                screenPxW,
                screenPxH,
                partialTick,
                hudTintArgb,
                hudTextScale * HEAT_TIME_SCALE_REL
        );

        renderCopernicusOxygenIndicatorTintedPixels(gg, mc, player, screenPxW, screenPxH, hudTintArgb, hudTextScale);

        if (cfg.coords.enabled) {
            renderCoordsAndBiomePixels(gg, mc, player, screenPxW, screenPxH, hudTintArgb, cfg);
        }

        if (cfg.toggleList.enabled) {
            renderToggleListPixels(gg, mc, player, screenPxW, screenPxH, hudTintArgb, cfg);
        }

        if (cfg.shards.enabled) {
            renderChipwareShardsPixels(gg, player, screenPxW, screenPxH, cfg);
        }

        if (cfg.target.enabled && cfg.targetMode != HudConfigClient.TargetMode.OFF) {
            renderTargetNamePixels(gg, mc, player, screenPxW, screenPxH, hudTintArgb, cfg);
        }

        gg.pose().popPose();
    }

    public static void renderCrosshairPreview(GuiGraphics gg, float partialTick, HudConfigClient.HudConfig cfg) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        if (mc.options.hideGui) return;

        double guiScale = mc.getWindow().getGuiScale();
        int screenPxW = mc.getWindow().getScreenWidth();
        int screenPxH = mc.getWindow().getScreenHeight();

        int hudTintArgb = resolveHudTintArgb(player);

        gg.pose().pushPose();
        gg.pose().scale((float) (1.0 / guiScale), (float) (1.0 / guiScale), 1.0f);

        renderSpinningCenteredImageAutoFitTintedPixels(
                gg,
                CENTER_SPINNER,
                SPINNER_W,
                SPINNER_H,
                screenPxW,
                screenPxH,
                SPINNER_MAX_SCREEN_FRACTION,
                SPINNER_ALPHA,
                player.tickCount,
                partialTick,
                SPINNER_DEG_PER_SECOND,
                SPINNER_OFFSET_X_PX,
                SPINNER_OFFSET_Y_PX,
                hudTintArgb
        );

        gg.pose().popPose();
    }

    private static void renderHudLayerLeft(GuiGraphics gg, DeltaTracker delta) {
        renderHudLayerSide(gg, delta, HudLayerSide.LEFT);
    }

    private static void renderHudLayerRight(GuiGraphics gg, DeltaTracker delta) {
        renderHudLayerSide(gg, delta, HudLayerSide.RIGHT);
    }

    private static void renderHudLayerSide(GuiGraphics gg, DeltaTracker delta, HudLayerSide side) {
        if (hudLayoutEditorOpen()) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        if (mc.options.hideGui) return;
        if (!mc.options.getCameraType().isFirstPerson()) return;
        if (!CyberwareInstallQueries.hasHudAccess(player)) return;

        HudConfigClient.HudConfig cfg = HudConfigClient.get(player.getUUID());
        if (!cfg.hudLayer.enabled) return;

        int hudTintArgb = resolveHudTintArgb(player);
        renderHudLayerSidePixels(gg, cfg, side, hudTintArgb);
    }

    private static void renderHud(GuiGraphics gg, DeltaTracker delta) {
        if (hudLayoutEditorOpen()) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        if (mc.options.hideGui) return;
        if (!mc.options.getCameraType().isFirstPerson()) return;
        if (!CyberwareInstallQueries.hasHudAccess(player)) return;

        HudConfigClient.HudConfig cfg = HudConfigClient.get(player.getUUID());

        float partialTick;
        try {
            partialTick = delta.getGameTimeDeltaPartialTick(true);
        } catch (Throwable ignored) {
            partialTick = 0.0f;
        }

        renderHudPreview(gg, partialTick, cfg);
    }

    private static void renderCrosshairOverlay(GuiGraphics gg, DeltaTracker delta) {
        if (hudLayoutEditorOpen()) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        if (mc.options.hideGui) return;
        if (!mc.options.getCameraType().isFirstPerson()) return;
        if (!CyberwareInstallQueries.hasHudAccess(player)) return;

        HudConfigClient.HudConfig cfg = HudConfigClient.get(player.getUUID());

        float partialTick;
        try {
            partialTick = delta.getGameTimeDeltaPartialTick(true);
        } catch (Throwable ignored) {
            partialTick = 0.0f;
        }

        renderCrosshairPreview(gg, partialTick, cfg);
    }

    private static boolean hudLayoutEditorOpen() {
        return Minecraft.getInstance().screen instanceof HudLayoutScreen;
    }

    private static void renderHudLayerSidePixels(GuiGraphics gg, HudConfigClient.HudConfig cfg, HudLayerSide side, int tintArgb) {
        Minecraft mc = Minecraft.getInstance();
        double guiScale = mc.getWindow().getGuiScale();
        int screenPxW = mc.getWindow().getScreenWidth();
        int screenPxH = mc.getWindow().getScreenHeight();

        HudWidgetRects rects = computeRectsForConfig(mc, cfg);
        HudRect rect = side == HudLayerSide.LEFT ? rects.hudLeft() : rects.hudRight();

        float baseScale = overlayBaseScale(screenPxW, screenPxH);
        float scale = baseScale * cfg.hudLayer.scale;

        int srcU = side == HudLayerSide.LEFT ? 0 : OVERLAY_HALF_W;

        gg.pose().pushPose();
        gg.pose().scale((float) (1.0 / guiScale), (float) (1.0 / guiScale), 1.0f);
        gg.pose().translate(rect.x(), rect.y(), 0);
        gg.pose().scale(scale, scale, 1.0f);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int argb = argbWithAlphaFromFloat(tintArgb, OVERLAY_ALPHA);
        setShaderColorFromArgb(argb);

        gg.blit(
                CENTER_OVERLAY,
                0,
                0,
                srcU,
                0,
                OVERLAY_HALF_W,
                OVERLAY_H,
                OVERLAY_W,
                OVERLAY_H
        );

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();

        gg.pose().popPose();
    }

    private static float overlayBaseScale(int screenPxW, int screenPxH) {
        float sx = (screenPxW * OVERLAY_MAX_SCREEN_FRACTION) / (float) OVERLAY_W;
        float sy = (screenPxH * OVERLAY_MAX_SCREEN_FRACTION) / (float) OVERLAY_H;
        return Math.min(Math.min(sx, sy), 1.0f);
    }

    private static BatteryRectData computeBatteryRectData(Minecraft mc, HudConfigClient.HudConfig cfg, int screenPxW, int screenPxH) {
        HudConfigClient.ComponentLayout batteryLayout = cfg.battery;

        float batteryScale = BATTERY_SCALE_PX * batteryLayout.scale;
        float batteryTextScale = batteryScale * VALUE_SCALE_REL;

        int iconW = Math.round(TEX_W * batteryScale);
        int iconH = Math.round(TEX_H * batteryScale);

        int iconX = batteryLayout.pixelX(screenPxW);
        int iconY = batteryLayout.pixelY(screenPxH);

        int rectX = iconX;
        int rectY = iconY;
        int rectW = iconW;
        int rectH = iconH;

        if (cfg.batteryMode == HudConfigClient.BatteryMode.TEXT_ONLY) {
            int textW = Math.round(mc.font.width("9999/9999") * batteryTextScale);
            int textH = Math.round(mc.font.lineHeight * batteryTextScale);

            rectX = iconX + (iconW / 2) - (textW / 2);
            rectY = iconY - VALUE_PADDING_PX - textH;
            rectW = textW;
            rectH = textH;
        } else {
            boolean hasText = cfg.batteryMode == HudConfigClient.BatteryMode.ICON_PLUS_CAPACITY
                    || cfg.batteryMode == HudConfigClient.BatteryMode.ICON_PLUS_CAPACITY_PLUS_STATS;

            boolean hasStats = cfg.batteryMode == HudConfigClient.BatteryMode.ICON_PLUS_CAPACITY_PLUS_STATS;

            if (hasText) {
                int textW = Math.round(mc.font.width("9999/9999") * batteryTextScale);
                int textH = Math.round(mc.font.lineHeight * batteryTextScale);

                int textX = iconX + (iconW / 2) - (textW / 2);
                int textY = iconY - VALUE_PADDING_PX - textH;

                int minX = Math.min(rectX, textX);
                int minY = Math.min(rectY, textY);
                int maxX = Math.max(rectX + rectW, textX + textW);
                int maxY = Math.max(rectY + rectH, textY + textH);

                rectX = minX;
                rectY = minY;
                rectW = maxX - minX;
                rectH = maxY - minY;

                if (hasStats) {
                    int genW = Math.round(mc.font.width("GEN: +999") * batteryTextScale);
                    int useW = Math.round(mc.font.width("USE: -999") * batteryTextScale);
                    int statsW = Math.max(genW, useW);

                    int lineH = Math.round(mc.font.lineHeight * batteryTextScale);
                    int statsH = (lineH * 2) + ENERGY_STATS_LINE_GAP_PX;

                    int statsX = iconX + (iconW / 2) - (statsW / 2);
                    int statsY = textY - ENERGY_STATS_EXTRA_PADDING_PX - statsH;

                    minX = Math.min(rectX, statsX);
                    minY = Math.min(rectY, statsY);
                    maxX = Math.max(rectX + rectW, statsX + statsW);
                    maxY = Math.max(rectY + rectH, statsY + statsH);

                    rectX = minX;
                    rectY = minY;
                    rectW = maxX - minX;
                    rectH = maxY - minY;
                }
            }
        }

        HudRect raw = new HudRect(rectX, rectY, rectW, rectH);
        HudRect clamped = clampRectToScreen(raw, screenPxW, screenPxH);

        return new BatteryRectData(raw, clamped, iconX, iconY, iconW, iconH);
    }

    private static HudRect clampRectToScreen(HudRect rect, int screenPxW, int screenPxH) {
        int x = clampInt(rect.x(), 0, Math.max(0, screenPxW - rect.w()));
        int y = clampInt(rect.y(), 0, Math.max(0, screenPxH - rect.h()));
        return new HudRect(x, y, rect.w(), rect.h());
    }

    private static int clampInt(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static final class EnergyRates {
        static final EnergyRates ZERO = new EnergyRates(0, 0);

        final int generatedPerTick;
        final int requiredPerTick;

        EnergyRates(int generatedPerTick, int requiredPerTick) {
            this.generatedPerTick = generatedPerTick;
            this.requiredPerTick = requiredPerTick;
        }
    }

    private static EnergyRates computeClientEnergyRates(LocalPlayer player, PlayerCyberwareData data) {
        int generated = 0;
        int required = 0;

        for (var entry : data.getAll().entrySet()) {
            CyberwareSlot slot = entry.getKey();
            InstalledCyberware[] arr = entry.getValue();
            if (arr == null) continue;

            for (InstalledCyberware cw : arr) {
                if (cw == null) continue;

                ItemStack stack = cw.getItem();
                if (stack == null || stack.isEmpty()) continue;
                if (!(stack.getItem() instanceof ICyberwareItem item)) continue;

                int gen = item.getEnergyGeneratedPerTick(player, stack, slot);
                if (gen > 0) generated += gen;

                int use = item.getEnergyUsedPerTick(player, stack, slot);
                if (use > 0) required += use;

                if (item.shouldConsumeActivationEnergyThisTick(player, stack, slot)) {
                    int act = item.getEnergyActivationCost(player, stack, slot);
                    if (act > 0) required += act;
                }
            }
        }

        return new EnergyRates(generated, required);
    }

    private static void renderBatteryWithModePixels(
            GuiGraphics gg,
            Minecraft mc,
            int screenPxW,
            int screenPxH,
            int current,
            int capacity,
            int capForPct,
            int genPerTick,
            int usePerTick,
            int netPerTick,
            boolean low,
            int hudTintArgb,
            int batteryTintArgb,
            HudConfigClient.HudConfig cfg
    ) {
        HudConfigClient.ComponentLayout layout = cfg.battery;

        float batteryScale = BATTERY_SCALE_PX * layout.scale;
        float textScale = batteryScale * VALUE_SCALE_REL;

        BatteryRectData data = computeBatteryRectData(mc, cfg, screenPxW, screenPxH);

        int dx = data.clampedRect().x() - data.rawRect().x();
        int dy = data.clampedRect().y() - data.rawRect().y();

        int iconX = data.rawIconX() + dx;
        int iconY = data.rawIconY() + dy;

        int scaledW = data.iconW();

        boolean drawIcon = cfg.batteryMode != HudConfigClient.BatteryMode.TEXT_ONLY;
        boolean drawCapacityText = cfg.batteryMode == HudConfigClient.BatteryMode.TEXT_ONLY
                || cfg.batteryMode == HudConfigClient.BatteryMode.ICON_PLUS_CAPACITY
                || cfg.batteryMode == HudConfigClient.BatteryMode.ICON_PLUS_CAPACITY_PLUS_STATS;

        boolean drawStats = cfg.batteryMode == HudConfigClient.BatteryMode.ICON_PLUS_CAPACITY_PLUS_STATS;

        int valueTopY = iconY;

        if (drawCapacityText) {
            valueTopY = renderEnergyValueAboveBatteryPixels(
                    gg,
                    mc,
                    current,
                    capacity,
                    iconX,
                    iconY,
                    scaledW,
                    low,
                    textScale
            );
        }

        if (drawStats && drawCapacityText) {
            renderEnergyStatsPixels(
                    gg,
                    mc,
                    genPerTick,
                    usePerTick,
                    netPerTick,
                    iconX,
                    valueTopY,
                    scaledW,
                    low,
                    hudTintArgb,
                    textScale
            );
        }

        if (drawIcon) {
            renderBatteryScaledPixels(gg, iconX, iconY, current, capForPct, 0, low, batteryTintArgb, batteryScale);
        }
    }

    private static int renderEnergyValueAboveBatteryPixels(
            GuiGraphics gg,
            Minecraft mc,
            int current,
            int capacity,
            int batteryX,
            int batteryY,
            int scaledBatteryW,
            boolean low,
            float valueScale
    ) {
        String text = current + "/" + capacity;

        int scaledTextH = Math.round(mc.font.lineHeight * valueScale);
        int textY = batteryY - VALUE_PADDING_PX - scaledTextH;

        int scaledTextW = Math.round(mc.font.width(text) * valueScale);
        int textX = batteryX + (scaledBatteryW / 2) - (scaledTextW / 2);

        int color = low ? VALUE_COLOR_LOW : VALUE_COLOR;

        gg.pose().pushPose();
        gg.pose().translate(textX, textY, 0);
        gg.pose().scale(valueScale, valueScale, 1.0f);
        gg.drawString(mc.font, text, 0, 0, color, VALUE_SHADOW);
        gg.pose().popPose();

        return textY;
    }

    private static void renderEnergyStatsPixels(
            GuiGraphics gg,
            Minecraft mc,
            int genPerTick,
            int usePerTick,
            int netPerTick,
            int batteryX,
            int valueTopY,
            int scaledBatteryW,
            boolean low,
            int hudTintArgb,
            float statsScale
    ) {
        String genText = "GEN: +" + Math.max(0, genPerTick);
        String useText = "USE: -" + Math.max(0, usePerTick);

        int color;
        if (low) {
            color = VALUE_COLOR_LOW;
        } else {
            int rgbTint = hudTintArgb & 0x00FFFFFF;
            color = rgbTint != 0 ? rgbTint : VALUE_COLOR;
        }

        int genW = Math.round(mc.font.width(genText) * statsScale);
        int useW = Math.round(mc.font.width(useText) * statsScale);

        int lineH = Math.round(mc.font.lineHeight * statsScale);
        int gap = ENERGY_STATS_LINE_GAP_PX;

        int blockH = (lineH * 2) + gap;
        int baseY = valueTopY - ENERGY_STATS_EXTRA_PADDING_PX - blockH;

        int genX = batteryX + (scaledBatteryW / 2) - (genW / 2);
        int useX = batteryX + (scaledBatteryW / 2) - (useW / 2);

        gg.pose().pushPose();
        gg.pose().translate(genX, baseY, 0);
        gg.pose().scale(statsScale, statsScale, 1.0f);
        gg.drawString(mc.font, genText, 0, 0, color, VALUE_SHADOW);
        gg.pose().popPose();

        gg.pose().pushPose();
        gg.pose().translate(useX, baseY + lineH + gap, 0);
        gg.pose().scale(statsScale, statsScale, 1.0f);
        gg.drawString(mc.font, useText, 0, 0, color, VALUE_SHADOW);
        gg.pose().popPose();
    }

    private static void renderBatteryScaledPixels(
            GuiGraphics gg,
            int x,
            int y,
            int currentPower,
            int maxPower,
            int netPowerPerTick,
            boolean low,
            int tintArgb,
            float batteryScalePx
    ) {
        gg.pose().pushPose();
        gg.pose().translate(x, y, 0);
        gg.pose().scale(batteryScalePx, batteryScalePx, 1.0f);
        renderBatteryTinted(gg, 0, 0, currentPower, maxPower, netPowerPerTick, low, tintArgb);
        gg.pose().popPose();
    }

    private static void renderBatteryTinted(
            GuiGraphics gg,
            int x,
            int y,
            int currentPower,
            int maxPower,
            int netPowerPerTick,
            boolean low,
            int tintArgb
    ) {
        float pct = maxPower <= 0 ? 0f : currentPower / (float) maxPower;
        pct = Mth.clamp(pct, 0f, 1f);

        int fillPx = Math.round(pct * INNER_H);
        fillPx = Mth.clamp(fillPx, 0, INNER_H);

        int usedPx = INNER_H - fillPx;

        ResourceLocation frame = low ? FRAME_EMPTY : FRAME;
        ResourceLocation bars1 = low ? BARS1_EMPTY : BARS1;
        ResourceLocation bars2 = low ? BARS2_EMPTY : BARS2;

        if (fillPx > 0) {
            int dstX = x + INNER_X;
            int dstY = y + INNER_Y + usedPx;

            int srcU = INNER_X;
            int srcV = INNER_Y + usedPx;

            blitTinted(gg, bars2, dstX, dstY, srcU, srcV, INNER_W, fillPx, TEX_W, TEX_H, tintArgb);
            blitTinted(gg, bars1, dstX, dstY, srcU, srcV, INNER_W, fillPx, TEX_W, TEX_H, tintArgb);
        }

        blitTinted(gg, frame, x, y, 0, 0, TEX_W, TEX_H, TEX_W, TEX_H, tintArgb);
    }

    private static void renderCoordsAndBiomePixels(
            GuiGraphics gg,
            Minecraft mc,
            LocalPlayer player,
            int screenPxW,
            int screenPxH,
            int hudTintArgb,
            HudConfigClient.HudConfig cfg
    ) {
        HudConfigClient.ComponentLayout layout = cfg.coords;
        float hudTextScale = BATTERY_SCALE_PX * VALUE_SCALE_REL * layout.scale;

        HudRect rect = computeRectsForConfig(mc, cfg).coords();

        BlockPos pos = player.blockPosition();

        String coords = "X: " + pos.getX() + "  Y: " + pos.getY() + "  Z: " + pos.getZ();
        Component biomeComp = biomeDisplayName(player, pos);
        String biomeLine = "Biome: " + biomeComp.getString();

        int rgbTint = hudTintArgb & 0x00FFFFFF;
        int color = rgbTint != 0 ? rgbTint : 0xFFFFFF;

        int lineH = Math.round(mc.font.lineHeight * hudTextScale);
        int gap = COORDS_LINE_GAP_PX;

        int x = rect.x();
        int y = rect.y();

        gg.pose().pushPose();
        gg.pose().translate(x, y, 0);
        gg.pose().scale(hudTextScale, hudTextScale, 1.0f);
        gg.drawString(mc.font, coords, 0, 0, color, COORDS_SHADOW);
        gg.pose().popPose();

        gg.pose().pushPose();
        gg.pose().translate(x, y + lineH + gap, 0);
        gg.pose().scale(hudTextScale, hudTextScale, 1.0f);
        gg.drawString(mc.font, biomeLine, 0, 0, color, COORDS_SHADOW);
        gg.pose().popPose();
    }

    private static Component biomeDisplayName(LocalPlayer player, BlockPos pos) {
        try {
            Holder<Biome> biomeHolder = player.level().getBiome(pos);
            ResourceKey<Biome> key = biomeHolder.unwrapKey().orElse(null);
            if (key == null) return Component.literal("Unknown");

            ResourceLocation id = key.location();
            return Component.translatable("biome." + id.getNamespace() + "." + id.getPath());
        } catch (Throwable ignored) {
            return Component.literal("Unknown");
        }
    }

    private static void renderToggleListPixels(
            GuiGraphics gg,
            Minecraft mc,
            LocalPlayer player,
            int screenPxW,
            int screenPxH,
            int hudTintArgb,
            HudConfigClient.HudConfig cfg
    ) {
        HudConfigClient.ComponentLayout layout = cfg.toggleList;

        float hudTextScale = BATTERY_SCALE_PX * VALUE_SCALE_REL * layout.scale;
        float hudIconScale = hudTextScale;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        List<ToggleEntry> entries = collectToggleEntries(data);
        if (entries.isEmpty()) return;

        int rows = Math.min(TOGGLE_MAX_ROWS, entries.size());

        int iconPx = Math.round(16f * hudIconScale);
        int lineH = Math.round(mc.font.lineHeight * hudTextScale);
        int rowH = Math.max(iconPx, lineH);

        HudRect rect = computeRectsForConfig(mc, cfg).toggleList();

        int x0 = rect.x();
        int y0 = rect.y();

        int rgbTint = hudTintArgb & 0x00FFFFFF;
        int disabledColor = rgbTint != 0 ? rgbTint : 0xFFFFFF;

        for (int i = 0; i < rows; i++) {
            ToggleEntry e = entries.get(i);

            int rowY = y0 + i * (rowH + TOGGLE_ROW_GAP_PX);

            gg.pose().pushPose();
            gg.pose().translate(x0, rowY + (rowH - iconPx) / 2, 0);
            gg.pose().scale(hudIconScale, hudIconScale, 1.0f);
            gg.renderItem(e.stack, 0, 0);
            gg.pose().popPose();

            boolean enabled = e.enabled;
            String line = enabled ? ENABLED_TXT.getString() : DISABLED_TXT.getString();

            int textX = x0 + iconPx + TOGGLE_ICON_TEXT_GAP_PX;
            int textY = rowY + (rowH - lineH) / 2;

            int color = enabled ? TOGGLE_ENABLED_COLOR : disabledColor;

            gg.pose().pushPose();
            gg.pose().translate(textX, textY, 0);
            gg.pose().scale(hudTextScale, hudTextScale, 1.0f);
            gg.drawString(mc.font, line, 0, 0, color, TOGGLE_SHADOW);
            gg.pose().popPose();
        }
    }

    private static List<ToggleEntry> collectToggleEntries(PlayerCyberwareData data) {
        List<ToggleEntry> out = new ArrayList<>();

        for (var entry : data.getAll().entrySet()) {
            CyberwareSlot slot = entry.getKey();
            InstalledCyberware[] arr = entry.getValue();
            if (arr == null) continue;

            for (int idx = 0; idx < arr.length; idx++) {
                InstalledCyberware cw = arr[idx];
                if (cw == null) continue;

                ItemStack stack = cw.getItem();
                if (stack == null || stack.isEmpty()) continue;
                if (!stack.is(ModTags.Items.TOGGLEABLE_CYBERWARE)) continue;

                boolean enabled = data.isEnabled(slot, idx);
                out.add(new ToggleEntry(stack.copy(), enabled));
            }
        }

        return out;
    }

    private record ToggleEntry(ItemStack stack, boolean enabled) {}

    private static void renderChipwareShardsPixels(
            GuiGraphics gg,
            LocalPlayer player,
            int screenPxW,
            int screenPxH,
            HudConfigClient.HudConfig cfg
    ) {
        Minecraft mc = Minecraft.getInstance();

        HudConfigClient.ComponentLayout layout = cfg.shards;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        ItemStack s0 = data.getChipwareStack(0);
        ItemStack s1 = data.getChipwareStack(1);
        if (s0.isEmpty() && s1.isEmpty()) return;

        float shardScale = BATTERY_SCALE_PX * VALUE_SCALE_REL * SHARDS_SCALE_REL * layout.scale;
        int iconPx = Math.round(16f * shardScale);

        HudRect rect = computeRectsForConfig(mc, cfg).shards();

        int x = rect.x();
        int y = rect.y();

        if (!s0.isEmpty()) {
            gg.pose().pushPose();
            gg.pose().translate(x, y, 0);
            gg.pose().scale(shardScale, shardScale, 1.0f);
            gg.renderItem(s0, 0, 0);
            gg.pose().popPose();
            x += iconPx + SHARDS_ICON_GAP_PX;
        }

        if (!s1.isEmpty()) {
            gg.pose().pushPose();
            gg.pose().translate(x, y, 0);
            gg.pose().scale(shardScale, shardScale, 1.0f);
            gg.renderItem(s1, 0, 0);
            gg.pose().popPose();
        }
    }

    private static void renderTargetNamePixels(
            GuiGraphics gg,
            Minecraft mc,
            LocalPlayer player,
            int screenPxW,
            int screenPxH,
            int hudTintArgb,
            HudConfigClient.HudConfig cfg
    ) {
        HitResult hr = mc.hitResult;
        if (hr == null || hr.getType() == HitResult.Type.MISS) return;

        Component name = null;

        if (hr.getType() == HitResult.Type.ENTITY && hr instanceof EntityHitResult ehr) {
            Entity e = ehr.getEntity();
            if (e instanceof ItemEntity itemEntity) {
                name = itemEntity.getItem().getHoverName();
            } else {
                name = e.getDisplayName();
            }
        } else if (hr.getType() == HitResult.Type.BLOCK && hr instanceof BlockHitResult bhr) {
            BlockPos pos = bhr.getBlockPos();
            try {
                name = player.level().getBlockState(pos).getBlock().getName();
            } catch (Throwable ignored) {
                name = null;
            }
        }

        if (name == null) return;

        String text = name.getString();
        if (text.isBlank()) return;

        HudConfigClient.ComponentLayout layout = cfg.target;
        float hudTextScale = BATTERY_SCALE_PX * VALUE_SCALE_REL * layout.scale;

        int rgbTint = hudTintArgb & 0x00FFFFFF;
        int color = rgbTint != 0 ? rgbTint : 0xFFFFFF;

        HudRect rect = computeRectsForConfig(mc, cfg).target();

        int x = rect.x();
        int y = rect.y();

        gg.pose().pushPose();
        gg.pose().translate(x, y, 0);
        gg.pose().scale(hudTextScale, hudTextScale, 1.0f);
        gg.drawString(mc.font, text, 0, 0, color, TARGET_SHADOW);
        gg.pose().popPose();
    }

    private static void renderCopernicusOxygenIndicatorTintedPixels(
            GuiGraphics gg,
            Minecraft mc,
            LocalPlayer player,
            int screenPxW,
            int screenPxH,
            int hudTintArgb,
            float hudTextScale
    ) {
        if (!hasExternalOxygenHudAccess(player)) return;

        int oxygen = ClientCopernicusOxygenState.get();
        int max = COPERNICUS_OXYGEN_MAX_DISPLAY;

        String text = "OXYGEN: " + oxygen + "/" + max;

        float pct = max <= 0 ? 0f : oxygen / (float) max;
        boolean low = pct <= OXYGEN_LOW_THRESHOLD;

        int rgbTint = hudTintArgb & 0x00FFFFFF;
        int color = low ? OXYGEN_TEXT_COLOR_LOW : rgbTint != 0 ? rgbTint : OXYGEN_TEXT_COLOR;

        int oxygenBarCenterX = (screenPxW / 2) + 200;
        int oxygenBarTopY = screenPxH - 200;

        int scaledTextW = Math.round(mc.font.width(text) * hudTextScale);
        int scaledTextH = Math.round(mc.font.lineHeight * hudTextScale);

        int textX = oxygenBarCenterX - (scaledTextW / 2);
        int textY = oxygenBarTopY - scaledTextH - 1;

        gg.pose().pushPose();
        gg.pose().translate(textX, textY, 0);
        gg.pose().scale(hudTextScale, hudTextScale, 1.0f);
        gg.drawString(mc.font, text, 0, 0, color, OXYGEN_TEXT_SHADOW);
        gg.pose().popPose();
    }

    private static boolean hasExternalOxygenHudAccess(LocalPlayer player) {
        return CopernicusSuitPredicate.hasCopernicusSetInstalled(player)
                || CreatingSpaceSuitPredicate.hasCreatingSpaceSuitEquivalentInstalled(player);
    }

    private static int resolveHudTintArgb(LocalPlayer player) {
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return HUD_TINT_WHITE_ARGB;

        if (data.hasSpecificItem(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES)
                && data.isDyed(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES)) {

            int rgb = data.dyeColor(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES);
            return (rgb & 0x00FFFFFF) | 0xFF000000;
        }

        return HUD_TINT_WHITE_ARGB;
    }

    private static int argbWithAlphaFromFloat(int argb, float alpha) {
        int a = Mth.clamp(Math.round(alpha * 255f), 0, 255);
        return (argb & 0x00FFFFFF) | (a << 24);
    }

    private static void setShaderColorFromArgb(int argb) {
        float a = ((argb >>> 24) & 0xFF) / 255f;
        float r = ((argb >>> 16) & 0xFF) / 255f;
        float g = ((argb >>> 8) & 0xFF) / 255f;
        float b = (argb & 0xFF) / 255f;
        RenderSystem.setShaderColor(r, g, b, a);
    }

    private static void renderSpinningCenteredImageAutoFitTintedPixels(
            GuiGraphics gg,
            ResourceLocation tex,
            int texW,
            int texH,
            int screenPxW,
            int screenPxH,
            float maxScreenFraction,
            float alpha,
            int tickCount,
            float partialTick,
            float degPerSecond,
            float offsetXPx,
            float offsetYPx,
            int tintArgb
    ) {
        float sx = (screenPxW * maxScreenFraction) / (float) texW;
        float sy = (screenPxH * maxScreenFraction) / (float) texH;
        float scale = Math.min(sx, sy);
        scale = Math.min(scale, 1.0f);

        float timeSeconds = (tickCount + partialTick) / 20.0f;
        float angleDeg = (timeSeconds * degPerSecond) % 360.0f;

        gg.pose().pushPose();
        gg.pose().translate((screenPxW / 2.0f) + offsetXPx, (screenPxH / 2.0f) + offsetYPx, 0);
        gg.pose().mulPose(Axis.ZP.rotationDegrees(angleDeg));
        gg.pose().scale(scale, scale, 1.0f);
        gg.pose().translate(-texW / 2.0f, -texH / 2.0f, 0);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int argb = argbWithAlphaFromFloat(tintArgb, alpha);
        setShaderColorFromArgb(argb);

        gg.blit(tex, 0, 0, 0, 0, texW, texH, texW, texH);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();

        gg.pose().popPose();
    }

    private static void blitTinted(
            GuiGraphics gg,
            ResourceLocation tex,
            int x,
            int y,
            int u,
            int v,
            int w,
            int h,
            int texW,
            int texH,
            int argb
    ) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        setShaderColorFromArgb(argb);
        gg.blit(tex, x, y, u, v, w, h, texW, texH);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }

    private static int computeHeatEngineRemainingBurnTicks(LocalPlayer player, PlayerCyberwareData data) {
        int burnRemaining = Math.max(0, data.getHeatEngineBurnTime());

        ItemStack fuel = data.getHeatEngineStack(PlayerCyberwareData.HEAT_ENGINE_FUEL);
        if (fuel.isEmpty()) return burnRemaining;

        Integer perItem = AbstractFurnaceBlockEntity.getFuel().get(fuel.getItem());
        int perItemTicks = perItem != null ? Math.max(0, perItem) : 0;

        if (perItemTicks <= 0) return burnRemaining;

        long extra = (long) fuel.getCount() * (long) perItemTicks;
        long total = (long) burnRemaining + extra;

        return total > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) total;
    }

    private static String formatTicksAsTime(int ticks) {
        int totalSec = Math.max(0, ticks / 20);
        int s = totalSec % 60;
        int m = (totalSec / 60) % 60;
        int h = totalSec / 3600;

        if (h > 0) {
            return h + ":" + String.format("%02d:%02d", m, s);
        }

        return m + ":" + String.format("%02d", s);
    }

    private static void renderHeatEngineRemainingBurnAboveHotbarPixels(
            GuiGraphics gg,
            Minecraft mc,
            LocalPlayer player,
            int screenPxW,
            int screenPxH,
            float partialTick,
            int hudTintArgb,
            float hudTextScale
    ) {
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;
        if (!data.hasSpecificItem(ModItems.ORGANSUPGRADES_HEATENGINE.get(), CyberwareSlot.ORGANS)) return;

        int remainingTicks = computeHeatEngineRemainingBurnTicks(player, data);
        if (remainingTicks <= 0) return;

        String timeText = formatTicksAsTime(remainingTicks);

        int hotbarW = 182;
        int hotbarH = 22;

        int hotbarX = (screenPxW / 2) - (hotbarW / 2);
        int hotbarY = screenPxH - hotbarH;

        int baseAnchorX = hotbarX + (hotbarW / 2);
        int baseAnchorY = hotbarY;

        int lineH = Math.round(mc.font.lineHeight * hudTextScale);

        int textW = Math.round(mc.font.width(timeText) * hudTextScale);
        int textH = lineH;

        int flameW = Math.round(HEAT_FLAME_DRAW_W * hudTextScale);
        int flameH = Math.round(HEAT_FLAME_DRAW_H * hudTextScale);

        int blockW = flameW + HEAT_FLAME_TEXT_GAP_PX + textW;
        int blockH = Math.max(flameH, textH);

        int x0 = baseAnchorX - (blockW / 2) + HEAT_TIME_OFFSET_X_PX;
        int y0 = baseAnchorY - HEAT_TIME_OFFSET_Y_PX - blockH;

        int rgbTint = hudTintArgb & 0x00FFFFFF;
        int color = rgbTint != 0 ? rgbTint : 0xFFFFFF;

        float t = mc.level != null ? mc.level.getGameTime() + partialTick : partialTick;

        float pulse = 1.0f + 0.06f * Mth.sin(t * 0.35f);
        float bob = 0.8f * Mth.sin(t * 0.25f);
        float alpha = 0.85f + 0.15f * Mth.sin(t * 0.40f);

        int flameX = x0;
        int flameY = y0 + (blockH - flameH) / 2;

        gg.pose().pushPose();
        gg.pose().translate(flameX, flameY, 0);
        gg.pose().scale(hudTextScale, hudTextScale, 1.0f);

        float baseX = 0;
        float baseY = -2;

        float cx = baseX + HEAT_FLAME_DRAW_W / 2.0f;
        float cy = baseY + HEAT_FLAME_DRAW_H / 2.0f;

        gg.pose().translate(cx, cy + bob, 0.0f);
        gg.pose().scale(pulse, pulse, 1.0f);
        gg.pose().translate(-cx, -cy, 0.0f);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

        gg.blit(
                HEAT_FLAME_TEX,
                (int) baseX,
                (int) baseY,
                HEAT_FLAME_DRAW_W,
                HEAT_FLAME_DRAW_H,
                0,
                0,
                HEAT_FLAME_TEX_W,
                HEAT_FLAME_TEX_H,
                HEAT_FLAME_TEX_W,
                HEAT_FLAME_TEX_H
        );

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        gg.pose().popPose();

        int textX = x0 + flameW + HEAT_FLAME_TEXT_GAP_PX;
        int textY = y0 + (blockH - textH) / 2;

        gg.pose().pushPose();
        gg.pose().translate(textX, textY, 0);
        gg.pose().scale(hudTextScale, hudTextScale, 1.0f);
        gg.drawString(mc.font, timeText, 0, 0, color, VALUE_SHADOW);
        gg.pose().popPose();
    }

    public static final class CyberwareInstallQueries {
        private CyberwareInstallQueries() {}

        public static boolean hasHudAccess(LocalPlayer player) {
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);

            return hasHudUpgrade(data) && cybereyesAreFunctionalForHud(data);
        }

        private static boolean hasHudUpgrade(PlayerCyberwareData data) {
            return data.hasSpecificItem(ModItems.EYEUPGRADES_HUDLENS.get(), CyberwareSlot.EYES)
                    || data.hasSpecificItem(ModItems.EYEUPGRADES_HUDJACK.get(), CyberwareSlot.EYES);
        }

        private static boolean cybereyesAreFunctionalForHud(PlayerCyberwareData data) {
            InstalledCyberware[] arr = data.getAll().get(CyberwareSlot.EYES);
            if (arr == null) return false;

            boolean foundEnabledCybereyes = false;
            boolean foundPoweredEnabledCybereyes = false;

            for (int idx = 0; idx < arr.length; idx++) {
                InstalledCyberware installed = arr[idx];
                if (installed == null) continue;

                ItemStack stack = installed.getItem();
                if (stack == null || stack.isEmpty()) continue;
                if (!(stack.getItem() instanceof CybereyeItem)) continue;
                if (!data.isEnabled(CyberwareSlot.EYES, idx)) continue;

                foundEnabledCybereyes = true;

                if (installed.isPowered()) {
                    foundPoweredEnabledCybereyes = true;
                }
            }

            return !foundEnabledCybereyes || foundPoweredEnabledCybereyes;
        }
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

    public static final class ClientCopernicusOxygenState {
        private static volatile int LAST = 0;

        private ClientCopernicusOxygenState() {}

        public static void set(int oxygen) {
            LAST = Math.max(0, oxygen);
        }

        public static int get() {
            return LAST;
        }
    }
}