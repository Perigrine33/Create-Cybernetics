package com.perigrine3.createcybernetics.screen.custom.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.client.HudConfigClient;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.compat.northstar.CopernicusSuitPredicate;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
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

    public static final ResourceLocation LAYER_ID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware_hud");

    public static final ResourceLocation CROSSHAIR_LAYER_ID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware_hud_crosshair");

    // -------------------- Textures --------------------

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

    // -------------------- Overlay sizing (resolution-locked) --------------------

    private static final int OVERLAY_W = 2048;
    private static final int OVERLAY_H = 1055;
    private static final int SPINNER_W = 2048;
    private static final int SPINNER_H = 1055;

    private static final float OVERLAY_MAX_SCREEN_FRACTION = 0.95f;
    private static final float OVERLAY_ALPHA = 0.5f;
    private static final boolean OVERLAY_DRAW_BEHIND_BATTERY = true;

    private static final float SPINNER_MAX_SCREEN_FRACTION = 1.25f;
    private static final float SPINNER_ALPHA = 0.1f;
    private static float SPINNER_OFFSET_X_PX = -0.5f;
    private static float SPINNER_OFFSET_Y_PX = -0.5f;

    private static float SPINNER_DEG_PER_SECOND = 10.0f;

    // -------------------- Battery texture metrics --------------------

    private static final int TEX_W = 13;
    private static final int TEX_H = 25;
    private static final int INNER_X = 1;
    private static final int INNER_Y = 2;
    private static final int INNER_W = 10;
    private static final int INNER_H = 21;

    // -------------------- Shared positioning helpers --------------------

    public enum Anchor {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, TOP_CENTER, BOTTOM_CENTER, CENTER
    }

    private static int anchoredX(int screenW, int widgetW, Anchor anchor, int offsetX) {
        return switch (anchor) {
            case TOP_LEFT, BOTTOM_LEFT -> offsetX;
            case TOP_RIGHT, BOTTOM_RIGHT -> screenW - widgetW - offsetX;
            case TOP_CENTER, BOTTOM_CENTER, CENTER -> (screenW / 2) - (widgetW / 2) + offsetX;
        };
    }

    private static int anchoredY(int screenH, int widgetH, Anchor anchor, int offsetY) {
        return switch (anchor) {
            case TOP_LEFT, TOP_RIGHT, TOP_CENTER -> offsetY;
            case BOTTOM_LEFT, BOTTOM_RIGHT, BOTTOM_CENTER -> screenH - widgetH - offsetY;
            case CENTER -> (screenH / 2) - (widgetH / 2) + offsetY;
        };
    }

    // -------------------- HUD tint behavior --------------------

    private static final int HUD_TINT_WHITE_ARGB = 0xFFFFFFFF;
    private static final int HUD_TINT_LOW_RED_ARGB = 0xFFFF5555;

    // -------------------- Battery knobs (resolution-locked) --------------------

    private static Anchor BATTERY_ANCHOR = Anchor.BOTTOM_RIGHT;
    private static int BATTERY_OFFSET_X_PX = 300;
    private static int BATTERY_OFFSET_Y_PX = 100;
    private static float BATTERY_SCALE_PX = 4f;

    /** Battery value text baseline. ALL other HUD text + icons match this size. */
    private static final float VALUE_SCALE_REL = 0.75f;

    private static final int VALUE_PADDING_PX = 2;
    private static final int VALUE_COLOR = 0xFFFFFF;
    private static final int VALUE_COLOR_LOW = 0xFF5555;
    private static final boolean VALUE_SHADOW = true;
    private static final float LOW_THRESHOLD = 0.25f;

    // Energy stats (above battery)
    private static final int ENERGY_STATS_LINE_GAP_PX = 1;
    private static final int ENERGY_STATS_EXTRA_PADDING_PX = 2;

    // -------------------- Coords/biome (top-right) --------------------

    private static Anchor COORDS_ANCHOR = Anchor.TOP_RIGHT;
    private static int COORDS_OFFSET_X_PX = 160;
    private static int COORDS_OFFSET_Y_PX = 120;
    private static final int COORDS_LINE_GAP_PX = 1;
    private static final boolean COORDS_SHADOW = true;

    // -------------------- Toggle list (left side) --------------------

    private static Anchor TOGGLE_ANCHOR = Anchor.TOP_LEFT;
    private static int TOGGLE_OFFSET_X_PX = 140;
    private static int TOGGLE_OFFSET_Y_PX = 130;
    private static final int TOGGLE_ROW_GAP_PX = 2;
    private static final int TOGGLE_ICON_TEXT_GAP_PX = 4;
    private static final boolean TOGGLE_SHADOW = true;
    private static int TOGGLE_MAX_ROWS = 16;

    private static final Component ENABLED_TXT = Component.literal("ENABLED");
    private static final Component DISABLED_TXT = Component.literal("DISABLED");

    private static final int TOGGLE_ENABLED_COLOR = 0x55FF55; // green

    // -------------------- Shards (bottom-left) --------------------

    private static Anchor SHARDS_ANCHOR = Anchor.BOTTOM_LEFT;
    private static int SHARDS_OFFSET_X_PX = 270;
    private static int SHARDS_OFFSET_Y_PX = 90;
    private static final int SHARDS_ICON_GAP_PX = 0;
    private static float SHARDS_SCALE_REL = 1.75f;

    // -------------------- Target name --------------------
    // You requested 3 options:
    //  - current position ("above hotbar") => your existing Y=250 offset
    //  - under crosshair => Y=50
    //  - off
    private static Anchor TARGET_ANCHOR = Anchor.CENTER;
    private static int TARGET_OFFSET_X_PX = 0;
    private static final int TARGET_OFFSET_Y_ABOVE_HOTBAR_PX = 250; // your current
    private static final int TARGET_OFFSET_Y_UNDER_CROSSHAIR_PX = 50; // requested
    private static final boolean TARGET_SHADOW = true;

    // -------------------- Oxygen --------------------

    public static final int COPERNICUS_OXYGEN_MAX_DISPLAY = 3000;
    private static final int OXYGEN_TEXT_COLOR = 0xFFFFFF;
    private static final int OXYGEN_TEXT_COLOR_LOW = 0xFF5555;
    private static final boolean OXYGEN_TEXT_SHADOW = true;
    private static final float OXYGEN_LOW_THRESHOLD = 0.25f;

    // -------------------- Registration --------------------

    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, LAYER_ID, CyberwareHudLayer::renderHud);
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, CROSSHAIR_LAYER_ID, CyberwareHudLayer::renderCrosshairOverlay);
    }

    // ======================================================================
    // Public helpers for the config screen
    // ======================================================================

    /** Rect bundle used by the HUD config screen to position cycle buttons. All values are in SCREEN PIXELS. */
    public record HudWidgetRects(
            int batteryX, int batteryY, int batteryW, int batteryH,
            int coordsX, int coordsY, int coordsW, int coordsH,
            int toggleX, int toggleY, int toggleW, int toggleH,
            int shardsX, int shardsY, int shardsW, int shardsH,
            int targetX, int targetY, int targetW, int targetH
    ) {}

    /**
     * Computes approximate widget rectangles for UI placement.
     * Uses the same layout math as the renderer.
     */
    public static HudWidgetRects computeRectsForConfig(Minecraft mc, HudConfigClient.HudConfig cfg) {
        int screenPxW = mc.getWindow().getScreenWidth();
        int screenPxH = mc.getWindow().getScreenHeight();

        // HUD scale baseline
        float hudTextScale = BATTERY_SCALE_PX * VALUE_SCALE_REL;
        float hudIconScale = hudTextScale;

        // Battery block bounds
        int scaledBatteryW = Math.round(TEX_W * BATTERY_SCALE_PX);
        int scaledBatteryH = Math.round(TEX_H * BATTERY_SCALE_PX);
        int batteryX = anchoredX(screenPxW, scaledBatteryW, BATTERY_ANCHOR, BATTERY_OFFSET_X_PX);
        int batteryY = anchoredY(screenPxH, scaledBatteryH, BATTERY_ANCHOR, BATTERY_OFFSET_Y_PX);

        // If battery is TEXT_ONLY, we treat the "battery block" as the text bounds near the battery anchor.
        // This keeps the cycle button placement stable and near where the user expects.
        int batteryBlockX = batteryX;
        int batteryBlockY = batteryY;
        int batteryBlockW = scaledBatteryW;
        int batteryBlockH = scaledBatteryH;

        // Coords block bounds (worst-case width assumption based on typical strings)
        // We compute actual widths when we have a player; here we use conservative approximations.
        int approxCoordsW = Math.round(180 * hudTextScale);
        int approxCoordsH = Math.round((mc.font.lineHeight * hudTextScale) * 2) + COORDS_LINE_GAP_PX;
        int coordsX = anchoredX(screenPxW, approxCoordsW, COORDS_ANCHOR, COORDS_OFFSET_X_PX);
        int coordsY = anchoredY(screenPxH, approxCoordsH, COORDS_ANCHOR, COORDS_OFFSET_Y_PX);

        // Toggle list bounds (max rows and known icon/text sizing)
        int iconPx = Math.round(16f * hudIconScale);
        int lineH = Math.round(mc.font.lineHeight * hudTextScale);
        int rowH = Math.max(iconPx, lineH);
        int rows = Math.max(1, Math.min(TOGGLE_MAX_ROWS, 10));
        int enabledW = Math.round(mc.font.width(ENABLED_TXT.getString()) * hudTextScale);
        int disabledW = Math.round(mc.font.width(DISABLED_TXT.getString()) * hudTextScale);
        int statusW = Math.max(enabledW, disabledW);
        int toggleW = iconPx + TOGGLE_ICON_TEXT_GAP_PX + statusW;
        int toggleH = rows * rowH + Math.max(0, rows - 1) * TOGGLE_ROW_GAP_PX;
        int toggleX = anchoredX(screenPxW, toggleW, TOGGLE_ANCHOR, TOGGLE_OFFSET_X_PX);
        int toggleY = anchoredY(screenPxH, toggleH, TOGGLE_ANCHOR, TOGGLE_OFFSET_Y_PX);

        // Shards bounds (2 icons worst-case)
        float shardScale = hudIconScale * SHARDS_SCALE_REL;
        int shardIconPx = Math.round(16f * shardScale);
        int shardsW = (2 * shardIconPx) + SHARDS_ICON_GAP_PX;
        int shardsH = shardIconPx;
        int shardsX = anchoredX(screenPxW, shardsW, SHARDS_ANCHOR, SHARDS_OFFSET_X_PX);
        int shardsY = anchoredY(screenPxH, shardsH, SHARDS_ANCHOR, SHARDS_OFFSET_Y_PX);

        // Target bounds (approx, one line)
        int targetOffsetY = switch (cfg.targetMode) {
            case ABOVE_HOTBAR -> TARGET_OFFSET_Y_ABOVE_HOTBAR_PX;
            case UNDER_CROSSHAIR -> TARGET_OFFSET_Y_UNDER_CROSSHAIR_PX;
            case OFF -> TARGET_OFFSET_Y_ABOVE_HOTBAR_PX;
        };

        int approxTargetW = Math.round(140 * hudTextScale);
        int approxTargetH = Math.round(mc.font.lineHeight * hudTextScale);
        int targetX = anchoredX(screenPxW, approxTargetW, TARGET_ANCHOR, TARGET_OFFSET_X_PX);
        int targetY = anchoredY(screenPxH, approxTargetH, TARGET_ANCHOR, targetOffsetY);

        // Battery block override for TEXT_ONLY: move rect upward a bit so button isn't sitting on nothing.
        if (cfg.batteryMode == HudConfigClient.BatteryMode.TEXT_ONLY) {
            int textW = Math.round(mc.font.width("9999/9999") * hudTextScale);
            int textH = Math.round(mc.font.lineHeight * hudTextScale);
            batteryBlockW = textW;
            batteryBlockH = textH;
            batteryBlockX = batteryX + (scaledBatteryW / 2) - (textW / 2);
            batteryBlockY = batteryY - VALUE_PADDING_PX - textH;
        }

        return new HudWidgetRects(
                batteryBlockX, batteryBlockY, batteryBlockW, batteryBlockH,
                coordsX, coordsY, approxCoordsW, approxCoordsH,
                toggleX, toggleY, toggleW, toggleH,
                shardsX, shardsY, shardsW, shardsH,
                targetX, targetY, approxTargetW, approxTargetH
        );
    }

    /** HUD preview for config screen. Uses the provided config, does NOT consult the saved config. */
    public static void renderHudPreview(GuiGraphics gg, float partialTick, HudConfigClient.HudConfig cfg) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        if (mc.options.hideGui) return;

        double guiScale = mc.getWindow().getGuiScale();
        int screenPxW = mc.getWindow().getScreenWidth();
        int screenPxH = mc.getWindow().getScreenHeight();

        float hudTextScale = BATTERY_SCALE_PX * VALUE_SCALE_REL;
        float hudIconScale = hudTextScale;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);

        int hudTintArgb = resolveHudTintArgb(player);

        int current = (data != null) ? data.getEnergyStored() : 0;
        int capacity = (data != null) ? data.getTotalEnergyCapacity(player) : 0;

        TickSnapshot snap = ClientEnergyState.getSnapshot();
        EnergyRates localRates = (data != null) ? computeClientEnergyRates(player, data) : EnergyRates.ZERO;

        int genPerTick = localRates.generatedPerTick;
        int usePerTick = localRates.requiredPerTick;

        if (snap != null) {
            boolean snapMeaningful = (snap.generatedPerTick() != 0) || (snap.consumedPerTick() != 0);
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

        if (OVERLAY_DRAW_BEHIND_BATTERY) {
            renderCenteredImageAutoFitTintedPixels(
                    gg, CENTER_OVERLAY, OVERLAY_W, OVERLAY_H,
                    screenPxW, screenPxH,
                    OVERLAY_MAX_SCREEN_FRACTION, OVERLAY_ALPHA,
                    hudTintArgb
            );
        }

        renderBatteryWithModePixels(
                gg, mc, screenPxW, screenPxH,
                current, capacity, capForPct,
                genPerTick, usePerTick, netPerTick,
                low, hudTintArgb, batteryTintArgb,
                hudTextScale,
                cfg.batteryMode
        );

        renderCopernicusOxygenIndicatorTintedPixels(gg, mc, player, screenPxW, screenPxH, hudTintArgb, hudTextScale);

        if (cfg.coordsEnabled) {
            renderCoordsAndBiomePixels(gg, mc, player, screenPxW, screenPxH, hudTintArgb, hudTextScale);
        }

        if (cfg.toggleListEnabled) {
            renderToggleListPixels(gg, mc, player, screenPxW, screenPxH, hudTintArgb, hudTextScale, hudIconScale);
        }

        if (cfg.shardsEnabled) {
            renderChipwareShardsPixels(gg, player, screenPxW, screenPxH, hudIconScale);
        }

        if (cfg.targetMode != HudConfigClient.TargetMode.OFF) {
            int targetOffsetY = (cfg.targetMode == HudConfigClient.TargetMode.UNDER_CROSSHAIR)
                    ? TARGET_OFFSET_Y_UNDER_CROSSHAIR_PX
                    : TARGET_OFFSET_Y_ABOVE_HOTBAR_PX;
            renderTargetNamePixels(gg, mc, player, screenPxW, screenPxH, hudTintArgb, hudTextScale, targetOffsetY);
        }

        if (!OVERLAY_DRAW_BEHIND_BATTERY) {
            renderCenteredImageAutoFitTintedPixels(
                    gg, CENTER_OVERLAY, OVERLAY_W, OVERLAY_H,
                    screenPxW, screenPxH,
                    OVERLAY_MAX_SCREEN_FRACTION, OVERLAY_ALPHA,
                    hudTintArgb
            );
        }

        gg.pose().popPose();
    }

    /** Crosshair preview for config screen. */
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
                SPINNER_W, SPINNER_H,
                screenPxW, screenPxH,
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

    // ======================================================================
    // Vanilla HUD layer entrypoints (now config-driven)
    // ======================================================================

    private static void renderHud(GuiGraphics gg, DeltaTracker delta) {
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

    // ======================================================================
    // Energy rates (client-side)
    // ======================================================================

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

            for (int idx = 0; idx < arr.length; idx++) {
                InstalledCyberware cw = arr[idx];
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

    // ======================================================================
    // Battery (mode-driven)
    // ======================================================================

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
            float hudTextScale,
            HudConfigClient.BatteryMode mode
    ) {
        int scaledW = Math.round(TEX_W * BATTERY_SCALE_PX);
        int scaledH = Math.round(TEX_H * BATTERY_SCALE_PX);

        int x = anchoredX(screenPxW, scaledW, BATTERY_ANCHOR, BATTERY_OFFSET_X_PX);
        int y = anchoredY(screenPxH, scaledH, BATTERY_ANCHOR, BATTERY_OFFSET_Y_PX);

        boolean drawIcon = (mode != HudConfigClient.BatteryMode.TEXT_ONLY);
        boolean drawCapacityText = (mode == HudConfigClient.BatteryMode.TEXT_ONLY)
                || (mode == HudConfigClient.BatteryMode.ICON_PLUS_CAPACITY)
                || (mode == HudConfigClient.BatteryMode.ICON_PLUS_CAPACITY_PLUS_STATS);

        boolean drawStats = (mode == HudConfigClient.BatteryMode.ICON_PLUS_CAPACITY_PLUS_STATS);

        int valueTopY = y;

        if (drawCapacityText) {
            valueTopY = renderEnergyValueAboveBatteryPixels(
                    gg, mc, current, capacity,
                    x, y, scaledW,
                    low, hudTextScale
            );
        }

        if (drawStats && drawCapacityText) {
            renderEnergyStatsPixels(
                    gg, mc,
                    genPerTick, usePerTick, netPerTick,
                    x, valueTopY, scaledW,
                    low, hudTintArgb,
                    hudTextScale
            );
        }

        if (drawIcon) {
            renderBatteryScaledPixels(gg, x, y, current, capForPct, 0, low, batteryTintArgb, BATTERY_SCALE_PX);
        }
    }

    private static int renderEnergyValueAboveBatteryPixels(
            GuiGraphics gg, Minecraft mc,
            int current, int capacity,
            int batteryX, int batteryY, int scaledBatteryW,
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
            color = (rgbTint != 0) ? rgbTint : VALUE_COLOR;
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

    private static void renderBatteryScaledPixels(GuiGraphics gg, int x, int y,
                                                  int currentPower, int maxPower, int netPowerPerTick,
                                                  boolean low,
                                                  int tintArgb,
                                                  float batteryScalePx) {
        gg.pose().pushPose();
        gg.pose().translate(x, y, 0);
        gg.pose().scale(batteryScalePx, batteryScalePx, 1.0f);
        renderBatteryTinted(gg, 0, 0, currentPower, maxPower, netPowerPerTick, low, tintArgb);
        gg.pose().popPose();
    }

    private static void renderBatteryTinted(GuiGraphics gg, int x, int y,
                                            int currentPower, int maxPower, int netPowerPerTick,
                                            boolean low,
                                            int tintArgb) {
        float pct = (maxPower <= 0) ? 0f : (currentPower / (float) maxPower);
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

    // ======================================================================
    // Coords + biome
    // ======================================================================

    private static void renderCoordsAndBiomePixels(GuiGraphics gg, Minecraft mc, LocalPlayer player,
                                                   int screenPxW, int screenPxH, int hudTintArgb,
                                                   float hudTextScale) {
        BlockPos pos = player.blockPosition();

        String coords = "X: " + pos.getX() + "  Y: " + pos.getY() + "  Z: " + pos.getZ();
        Component biomeComp = biomeDisplayName(player, pos);
        String biomeLine = "Biome: " + biomeComp.getString();

        int rgbTint = hudTintArgb & 0x00FFFFFF;
        int color = (rgbTint != 0) ? rgbTint : 0xFFFFFF;

        int lineH = Math.round(mc.font.lineHeight * hudTextScale);
        int gap = COORDS_LINE_GAP_PX;

        int w1 = Math.round(mc.font.width(coords) * hudTextScale);
        int w2 = Math.round(mc.font.width(biomeLine) * hudTextScale);
        int blockW = Math.max(w1, w2);
        int blockH = (lineH * 2) + gap;

        int x = anchoredX(screenPxW, blockW, COORDS_ANCHOR, COORDS_OFFSET_X_PX);
        int y = anchoredY(screenPxH, blockH, COORDS_ANCHOR, COORDS_OFFSET_Y_PX);

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
        } catch (Throwable t) {
            return Component.literal("Unknown");
        }
    }

    // ======================================================================
    // Toggle list
    // ======================================================================

    private static void renderToggleListPixels(
            GuiGraphics gg, Minecraft mc, LocalPlayer player,
            int screenPxW, int screenPxH, int hudTintArgb,
            float hudTextScale, float hudIconScale
    ) {
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        List<ToggleEntry> entries = collectToggleEntries(data);
        if (entries.isEmpty()) return;

        int rows = Math.min(TOGGLE_MAX_ROWS, entries.size());

        int iconPx = Math.round(16f * hudIconScale);

        int lineH = Math.round(mc.font.lineHeight * hudTextScale);
        int rowH = Math.max(iconPx, lineH);

        int enabledW  = Math.round(mc.font.width(ENABLED_TXT.getString()) * hudTextScale);
        int disabledW = Math.round(mc.font.width(DISABLED_TXT.getString()) * hudTextScale);
        int statusW = Math.max(enabledW, disabledW);

        int maxRowW = iconPx + TOGGLE_ICON_TEXT_GAP_PX + statusW;
        int blockH = rows * rowH + Math.max(0, rows - 1) * TOGGLE_ROW_GAP_PX;

        int x0 = anchoredX(screenPxW, maxRowW, TOGGLE_ANCHOR, TOGGLE_OFFSET_X_PX);
        int y0 = anchoredY(screenPxH, blockH, TOGGLE_ANCHOR, TOGGLE_OFFSET_Y_PX);

        int rgbTint = hudTintArgb & 0x00FFFFFF;
        int disabledColor = (rgbTint != 0) ? rgbTint : 0xFFFFFF;

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

    // ======================================================================
    // Chipware shards icons
    // ======================================================================

    private static void renderChipwareShardsPixels(
            GuiGraphics gg,
            LocalPlayer player,
            int screenPxW,
            int screenPxH,
            float hudIconScale
    ) {
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        ItemStack s0 = data.getChipwareStack(0);
        ItemStack s1 = data.getChipwareStack(1);
        if (s0.isEmpty() && s1.isEmpty()) return;

        float shardScale = hudIconScale * SHARDS_SCALE_REL;
        int iconPx = Math.round(16f * shardScale);

        int count = 0;
        if (!s0.isEmpty()) count++;
        if (!s1.isEmpty()) count++;

        int blockW = (count * iconPx) + Math.max(0, count - 1) * SHARDS_ICON_GAP_PX;
        int blockH = iconPx;

        int x0 = anchoredX(screenPxW, blockW, SHARDS_ANCHOR, SHARDS_OFFSET_X_PX);
        int y0 = anchoredY(screenPxH, blockH, SHARDS_ANCHOR, SHARDS_OFFSET_Y_PX);

        int x = x0;

        if (!s0.isEmpty()) {
            gg.pose().pushPose();
            gg.pose().translate(x, y0, 0);
            gg.pose().scale(shardScale, shardScale, 1.0f);
            gg.renderItem(s0, 0, 0);
            gg.pose().popPose();
            x += iconPx + SHARDS_ICON_GAP_PX;
        }

        if (!s1.isEmpty()) {
            gg.pose().pushPose();
            gg.pose().translate(x, y0, 0);
            gg.pose().scale(shardScale, shardScale, 1.0f);
            gg.renderItem(s1, 0, 0);
            gg.pose().popPose();
        }
    }

    // ======================================================================
    // Target name
    // ======================================================================

    private static void renderTargetNamePixels(GuiGraphics gg, Minecraft mc, LocalPlayer player,
                                               int screenPxW, int screenPxH, int hudTintArgb,
                                               float hudTextScale,
                                               int targetOffsetYPx) {
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

        int rgbTint = hudTintArgb & 0x00FFFFFF;
        int color = (rgbTint != 0) ? rgbTint : 0xFFFFFF;

        int w = Math.round(mc.font.width(text) * hudTextScale);
        int h = Math.round(mc.font.lineHeight * hudTextScale);

        int x = anchoredX(screenPxW, w, TARGET_ANCHOR, TARGET_OFFSET_X_PX);
        int y = anchoredY(screenPxH, h, TARGET_ANCHOR, targetOffsetYPx);

        gg.pose().pushPose();
        gg.pose().translate(x, y, 0);
        gg.pose().scale(hudTextScale, hudTextScale, 1.0f);
        gg.drawString(mc.font, text, 0, 0, color, TARGET_SHADOW);
        gg.pose().popPose();
    }

    // ======================================================================
    // Oxygen (unchanged behavior)
    // ======================================================================

    private static void renderCopernicusOxygenIndicatorTintedPixels(GuiGraphics gg, Minecraft mc, LocalPlayer player,
                                                                    int screenPxW, int screenPxH,
                                                                    int hudTintArgb,
                                                                    float hudTextScale) {
        if (!CopernicusSuitPredicate.hasCopernicusSetInstalled(player)) return;

        int oxygen = ClientCopernicusOxygenState.get();
        int max = COPERNICUS_OXYGEN_MAX_DISPLAY;

        String text = "OXYGEN: " + oxygen + "/" + max;

        float pct = (max <= 0) ? 0f : (oxygen / (float) max);
        boolean low = pct <= OXYGEN_LOW_THRESHOLD;

        int rgbTint = hudTintArgb & 0x00FFFFFF;
        int color = low ? OXYGEN_TEXT_COLOR_LOW : (rgbTint != 0 ? rgbTint : OXYGEN_TEXT_COLOR);

        int airRightX = (screenPxW / 2) + 91;
        int airY = screenPxH - 52;

        int scaledTextW = Math.round(mc.font.width(text) * hudTextScale);
        int scaledTextH = Math.round(mc.font.lineHeight * hudTextScale);

        int textX = airRightX - scaledTextW;
        int textY = airY - scaledTextH - 1;

        gg.pose().pushPose();
        gg.pose().translate(textX, textY, 0);
        gg.pose().scale(hudTextScale, hudTextScale, 1.0f);
        gg.drawString(mc.font, text, 0, 0, color, OXYGEN_TEXT_SHADOW);
        gg.pose().popPose();
    }

    // ======================================================================
    // Tint + overlay helpers
    // ======================================================================

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

    private static void renderCenteredImageAutoFitTintedPixels(GuiGraphics gg, ResourceLocation tex, int texW, int texH,
                                                               int screenPxW, int screenPxH,
                                                               float maxScreenFraction, float alpha,
                                                               int tintArgb) {
        float sx = (screenPxW * maxScreenFraction) / (float) texW;
        float sy = (screenPxH * maxScreenFraction) / (float) texH;
        float scale = Math.min(sx, sy);
        scale = Math.min(scale, 1.0f);

        int drawW = Math.round(texW * scale);
        int drawH = Math.round(texH * scale);

        int x = (screenPxW - drawW) / 2;
        int y = (screenPxH - drawH) / 2;

        gg.pose().pushPose();
        gg.pose().translate(x, y, 0);
        gg.pose().scale(scale, scale, 1.0f);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int argb = argbWithAlphaFromFloat(tintArgb, alpha);
        setShaderColorFromArgb(argb);

        gg.blit(tex, 0, 0, 0, 0, texW, texH, texW, texH);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();

        gg.pose().popPose();
    }

    private static void renderSpinningCenteredImageAutoFitTintedPixels(GuiGraphics gg, ResourceLocation tex, int texW, int texH,
                                                                       int screenPxW, int screenPxH,
                                                                       float maxScreenFraction, float alpha,
                                                                       int tickCount, float partialTick,
                                                                       float degPerSecond,
                                                                       float offsetXPx, float offsetYPx,
                                                                       int tintArgb) {
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

    private static void blitTinted(GuiGraphics gg, ResourceLocation tex,
                                   int x, int y, int u, int v, int w, int h,
                                   int texW, int texH,
                                   int argb) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        setShaderColorFromArgb(argb);
        gg.blit(tex, x, y, u, v, w, h, texW, texH);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }

    // ======================================================================
    // Access gate (unchanged)
    // ======================================================================

    public static final class CyberwareInstallQueries {
        private CyberwareInstallQueries() {}

        public static boolean hasHudAccess(LocalPlayer player) {
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return false;

            return data.hasSpecificItem(ModItems.EYEUPGRADES_HUDLENS.get(), CyberwareSlot.EYES)
                    || data.hasSpecificItem(ModItems.EYEUPGRADES_HUDJACK.get(), CyberwareSlot.EYES);
        }
    }

    // ======================================================================
    // Client-fed state (unchanged)
    // ======================================================================

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
