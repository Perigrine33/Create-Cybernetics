package com.perigrine3.createcybernetics.client.gui;

import com.perigrine3.createcybernetics.client.HudConfigClient;
import com.perigrine3.createcybernetics.screen.custom.hud.CyberwareHudLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class HudLayoutScreen extends Screen {

    private static final int DIM_OVERLAY = 0x88000000;

    // DEV: button sizing
    private static final int BTN_W = 130;
    private static final int BTN_H = 20;

    // DEV: spacing
    private static final int STACK_GAP = 6;

    // Title positions
    private static final int TITLE_Y = 12;
    private static final int SUBTITLE_Y = 28;

    // Whole button block starts here (always above HUD preview)
    private static final int BUTTON_BLOCK_TOP_Y = 44;

    // Extra padding between Back/Save and option buttons
    private static final int TOP_GAP_AFTER_SAVE = 10;

    private final Screen parent;

    private @Nullable UUID playerId;
    private HudConfigClient.HudConfig working;

    private Button btnBack;
    private Button btnSave;

    private Button btnCoords;
    private Button btnToggleables;
    private Button btnShards;
    private Button btnTarget;
    private Button btnBattery;

    private boolean savedPulse = false;
    private int savedPulseTicks = 0;

    public HudLayoutScreen(Screen parent) {
        super(Component.translatable("screen.createcybernetics.hud_layout"));
        this.parent = parent;

        this.working = new HudConfigClient.HudConfig(
                true,
                true,
                true,
                HudConfigClient.TargetMode.ABOVE_HOTBAR,
                HudConfigClient.BatteryMode.ICON_PLUS_CAPACITY_PLUS_STATS
        );
    }

    @Override
    protected void init() {
        LocalPlayer p = Minecraft.getInstance().player;
        this.playerId = (p != null) ? p.getUUID() : null;

        if (this.playerId != null) {
            this.working = HudConfigClient.get(this.playerId).copy();
        }

        rebuildWidgets();
    }

    @Override
    public void resize(Minecraft mc, int width, int height) {
        super.resize(mc, width, height);
        rebuildWidgets();
    }

    @Override
    protected void rebuildWidgets() {
        clearWidgets();

        int cx = this.width / 2;
        int x = cx - (BTN_W / 2);

        int y = BUTTON_BLOCK_TOP_Y;

        btnBack = addRenderableWidget(Button.builder(Component.translatable("gui.back"), b -> onClose())
                .pos(x, y)
                .size(BTN_W, BTN_H)
                .build());
        y += BTN_H + STACK_GAP;

        btnSave = addRenderableWidget(Button.builder(Component.translatable("gui.createcybernetics.save"), b -> save())
                .pos(x, y)
                .size(BTN_W, BTN_H)
                .build());
        y += BTN_H + TOP_GAP_AFTER_SAVE;

        btnCoords = addRenderableWidget(Button.builder(coordsLabel(), b -> cycleCoords())
                .pos(x, y)
                .size(BTN_W, BTN_H)
                .build());
        y += BTN_H + STACK_GAP;

        btnToggleables = addRenderableWidget(Button.builder(toggleablesLabel(), b -> cycleToggleables())
                .pos(x, y)
                .size(BTN_W, BTN_H)
                .build());
        y += BTN_H + STACK_GAP;

        btnShards = addRenderableWidget(Button.builder(shardsLabel(), b -> cycleShards())
                .pos(x, y)
                .size(BTN_W, BTN_H)
                .build());
        y += BTN_H + STACK_GAP;

        btnTarget = addRenderableWidget(Button.builder(targetLabel(), b -> cycleTarget())
                .pos(x, y)
                .size(BTN_W, BTN_H)
                .build());
        y += BTN_H + STACK_GAP;

        btnBattery = addRenderableWidget(Button.builder(batteryLabel(), b -> cycleBattery())
                .pos(x, y)
                .size(BTN_W, BTN_H)
                .build());
    }

    private void save() {
        if (playerId == null) return;
        HudConfigClient.save(playerId, working.copy());
        savedPulse = true;
        savedPulseTicks = 20;
    }

    private void cycleCoords() {
        working.coordsEnabled = !working.coordsEnabled;
        refreshButtonLabels();
    }

    private void cycleToggleables() {
        working.toggleListEnabled = !working.toggleListEnabled;
        refreshButtonLabels();
    }

    private void cycleShards() {
        working.shardsEnabled = !working.shardsEnabled;
        refreshButtonLabels();
    }

    private void cycleTarget() {
        working.targetMode = switch (working.targetMode) {
            case ABOVE_HOTBAR -> HudConfigClient.TargetMode.UNDER_CROSSHAIR;
            case UNDER_CROSSHAIR -> HudConfigClient.TargetMode.OFF;
            case OFF -> HudConfigClient.TargetMode.ABOVE_HOTBAR;
        };
        refreshButtonLabels();
    }

    private void cycleBattery() {
        working.batteryMode = switch (working.batteryMode) {
            case TEXT_ONLY -> HudConfigClient.BatteryMode.ICON_ONLY;
            case ICON_ONLY -> HudConfigClient.BatteryMode.ICON_PLUS_CAPACITY;
            case ICON_PLUS_CAPACITY -> HudConfigClient.BatteryMode.ICON_PLUS_CAPACITY_PLUS_STATS;
            case ICON_PLUS_CAPACITY_PLUS_STATS -> HudConfigClient.BatteryMode.TEXT_ONLY;
        };
        refreshButtonLabels();
    }

    private void refreshButtonLabels() {
        if (btnCoords != null) btnCoords.setMessage(coordsLabel());
        if (btnToggleables != null) btnToggleables.setMessage(toggleablesLabel());
        if (btnShards != null) btnShards.setMessage(shardsLabel());
        if (btnTarget != null) btnTarget.setMessage(targetLabel());
        if (btnBattery != null) btnBattery.setMessage(batteryLabel());
    }

    private Component coordsLabel() {
        return Component.literal("COORDINATES: " + (working.coordsEnabled ? "ON" : "OFF"));
    }

    private Component toggleablesLabel() {
        return Component.literal("TOGGLEABLES: " + (working.toggleListEnabled ? "ON" : "OFF"));
    }

    private Component shardsLabel() {
        return Component.literal("CHIPWARE: " + (working.shardsEnabled ? "ON" : "OFF"));
    }

    private Component targetLabel() {
        return Component.literal("TARGET: " + switch (working.targetMode) {
            case ABOVE_HOTBAR -> "ABOVE HOTBAR";
            case UNDER_CROSSHAIR -> "UNDER CROSSHAIR";
            case OFF -> "OFF";
        });
    }

    private Component batteryLabel() {
        return Component.literal("BATTERY: " + switch (working.batteryMode) {
            case TEXT_ONLY -> "CAPACITY ONLY";
            case ICON_ONLY -> "ICON ONLY";
            case ICON_PLUS_CAPACITY -> "ICON+CAPACITY";
            case ICON_PLUS_CAPACITY_PLUS_STATS -> "FULL INFO";
        });
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (savedPulse) {
            savedPulseTicks--;
            if (savedPulseTicks <= 0) savedPulse = false;
        }
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        gg.fill(0, 0, this.width, this.height, DIM_OVERLAY);

        super.render(gg, mouseX, mouseY, partialTick);

        gg.drawCenteredString(this.font, this.title, this.width / 2, TITLE_Y, 0xFFFFFF);

        if (savedPulse) {
            gg.drawCenteredString(this.font, Component.literal("Saved"), this.width / 2, SUBTITLE_Y, 0x55FF55);
        } else {
            gg.drawCenteredString(this.font, Component.literal("Preview + cycle buttons"), this.width / 2, SUBTITLE_Y, 0xAAAAAA);
        }

        // HUD preview draws AFTER widgets, so it can never cover buttons.
        // (Buttons will still be clickable since they were created as widgets.)
        CyberwareHudLayer.renderHudPreview(gg, partialTick, working);
        CyberwareHudLayer.renderCrosshairPreview(gg, partialTick, working);
    }
}
