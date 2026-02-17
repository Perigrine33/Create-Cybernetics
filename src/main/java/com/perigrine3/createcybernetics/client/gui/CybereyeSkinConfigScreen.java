package com.perigrine3.createcybernetics.client.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.client.skin.CybereyeOverlayHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLPaths;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public final class CybereyeSkinConfigScreen extends Screen {

    private static final int SKIN_TEX_W = 64;
    private static final int SKIN_TEX_H = 64;

    // Base face (front): 8x8 at (8,8)
    private static final int FACE_U = 8;
    private static final int FACE_V = 8;
    private static final int FACE_PX = 8;

    private static final int GRID_CELLS = 8;

    private static final int PAD = 12;
    private static final int BTN_W = 60;
    private static final int BTN_H = 20;
    private static final int BTN_GAP = 6;

    private static final int GRID_LINE   = 0x66FFFFFF;
    private static final int GRID_BORDER = 0xAAFFFFFF;

    // Active edit highlight (red)
    private static final int ACTIVE_FILL = 0x66FF0000;
    private static final int ACTIVE_EDGE = 0xCCFF0000;

    // Saved/locked highlight (green)
    private static final int SAVED_FILL  = 0x6600FF00;
    private static final int SAVED_EDGE  = 0xCC00FF00;

    private static final int DIM_OVERLAY = 0x88000000;

    private final Screen parent;

    private int gridX, gridY, cell, gridW, gridH;

    private enum Eye { LEFT, RIGHT }

    private enum IrisSize {
        ONE_BY_ONE(1, 1),
        ONE_BY_TWO(1, 2),
        TWO_BY_TWO(2, 2);

        final int w;
        final int h;

        IrisSize(int w, int h) {
            this.w = w;
            this.h = h;
        }
    }

    private static final class EyeConfig {
        IrisSize size = IrisSize.ONE_BY_ONE;
        int x = 3;
        int y = 5;
        boolean locked = false;
    }

    private Eye activeEye = Eye.LEFT;
    private boolean editing = true;

    private final EyeConfig left = new EyeConfig();
    private final EyeConfig right = new EyeConfig();

    private boolean dragging = false;
    private int dragOffX = 0;
    private int dragOffY = 0;

    private Button btnL, btnR, btnSave;
    private Button btn11, btn12, btn22;
    private Button btnBack;

    private @Nullable UUID playerId = null;

    public CybereyeSkinConfigScreen(Screen parent) {
        super(Component.translatable("screen.createcybernetics.cybereye_skin"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        LocalPlayer p = Minecraft.getInstance().player;
        this.playerId = p != null ? p.getUUID() : null;

        if (this.playerId != null) {
            StoredConfig loaded = ConfigStore.load(this.playerId);
            if (loaded != null) applyLoaded(loaded);
        }
        pushConfigToPlayerNbtAndInvalidate();

        cc$rebuildWidgets();
        updateButtonStates();
    }

    @Override
    public void resize(Minecraft mc, int width, int height) {
        super.resize(mc, width, height);
        cc$rebuildWidgets();
        updateButtonStates();
    }

    // NOTE: must not be private because Screen has protected rebuildWidgets in some versions.
    protected void cc$rebuildWidgets() {
        clearWidgets();
        computeGridLayout();

        int leftColX = gridX - (BTN_W + PAD);
        int rightColX = gridX + gridW + PAD;

        int topY = gridY;

        btnL = addRenderableWidget(Button.builder(Component.literal("L"), b -> selectEye(Eye.LEFT))
                .pos(leftColX, topY)
                .size(BTN_W, BTN_H)
                .build());

        btnR = addRenderableWidget(Button.builder(Component.literal("R"), b -> selectEye(Eye.RIGHT))
                .pos(leftColX, topY + (BTN_H + BTN_GAP))
                .size(BTN_W, BTN_H)
                .build());

        btnSave = addRenderableWidget(Button.builder(Component.translatable("gui.createcybernetics.save"), b -> saveActiveEye())
                .pos(leftColX, topY + (BTN_H + BTN_GAP) * 2 + 4)
                .size(BTN_W, BTN_H)
                .build());

        // Back: returns to game menu (pause screen)
        btnBack = addRenderableWidget(Button.builder(Component.translatable("gui.back"), b -> Minecraft.getInstance().setScreen(parent))
                .pos(leftColX, topY + (BTN_H + BTN_GAP) * 3 + 10)
                .size(BTN_W, BTN_H)
                .build());

        btn11 = addRenderableWidget(Button.builder(Component.literal("1x1"), b -> setActiveSize(IrisSize.ONE_BY_ONE))
                .pos(rightColX, topY)
                .size(BTN_W, BTN_H)
                .build());

        btn12 = addRenderableWidget(Button.builder(Component.literal("1x2"), b -> setActiveSize(IrisSize.ONE_BY_TWO))
                .pos(rightColX, topY + (BTN_H + BTN_GAP))
                .size(BTN_W, BTN_H)
                .build());

        btn22 = addRenderableWidget(Button.builder(Component.literal("2x2"), b -> setActiveSize(IrisSize.TWO_BY_TWO))
                .pos(rightColX, topY + (BTN_H + BTN_GAP) * 2)
                .size(BTN_W, BTN_H)
                .build());
    }

    private void computeGridLayout() {
        int sideReserve = (BTN_W + PAD) * 2 + PAD * 2;
        int availW = Math.max(80, this.width - sideReserve);
        int availH = Math.max(80, this.height - 70);

        int maxCellByW = availW / GRID_CELLS;
        int maxCellByH = availH / GRID_CELLS;
        int chosen = Math.min(maxCellByW, maxCellByH);

        chosen = Math.max(10, Math.min(64, chosen));

        this.cell = chosen;
        this.gridW = cell * GRID_CELLS;
        this.gridH = cell * GRID_CELLS;

        this.gridX = (this.width - gridW) / 2;
        this.gridY = 44 + (availH - gridH) / 2;
    }

    private void selectEye(Eye eye) {
        this.activeEye = eye;
        this.editing = true;
        active().locked = false;
        this.dragging = false;
        updateButtonStates();
    }

    private void saveActiveEye() {
        active().locked = true;
        this.editing = false;
        this.dragging = false;

        if (this.playerId != null) {
            ConfigStore.save(this.playerId, toStored());
        }
        pushConfigToPlayerNbtAndInvalidate();
        updateButtonStates();
    }

    private void setActiveSize(IrisSize size) {
        if (!canEdit()) return;
        EyeConfig cfg = active();
        cfg.size = size;
        clampSelection(cfg);
        updateButtonStates();
    }

    private EyeConfig active() {
        return activeEye == Eye.LEFT ? left : right;
    }

    private EyeConfig other() {
        return activeEye == Eye.LEFT ? right : left;
    }

    private boolean canEdit() {
        return editing && !active().locked;
    }

    private void updateButtonStates() {
        boolean edit = canEdit();
        if (btnSave != null) btnSave.active = edit;
        if (btn11 != null) btn11.active = edit;
        if (btn12 != null) btn12.active = edit;
        if (btn22 != null) btn22.active = edit;
        // Back always active.
        if (btnBack != null) btnBack.active = true;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    // ---------------- Input / Drag ----------------

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
        if (button != 0) return false;
        if (!canEdit()) return false;
        if (!isInGrid(mouseX, mouseY)) return false;

        int cx = (int) ((mouseX - gridX) / cell);
        int cy = (int) ((mouseY - gridY) / cell);

        EyeConfig cfg = active();

        if (isInsideSelectionCell(cfg, cx, cy)) {
            dragging = true;
            dragOffX = cx - cfg.x;
            dragOffY = cy - cfg.y;
            return true;
        }

        cfg.x = cx;
        cfg.y = cy;
        clampSelection(cfg);

        dragging = true;
        dragOffX = 0;
        dragOffY = 0;
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (super.mouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;
        if (button != 0) return false;
        if (!dragging) return false;
        if (!canEdit()) return false;

        int cx = (int) Math.floor((mouseX - gridX) / (double) cell);
        int cy = (int) Math.floor((mouseY - gridY) / (double) cell);

        EyeConfig cfg = active();
        cfg.x = cx - dragOffX;
        cfg.y = cy - dragOffY;

        clampSelection(cfg);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (super.mouseReleased(mouseX, mouseY, button)) return true;
        if (button == 0) dragging = false;
        return false;
    }

    private boolean isInsideSelectionCell(EyeConfig cfg, int cx, int cy) {
        int w = cfg.size.w;
        int h = cfg.size.h;
        return cx >= cfg.x && cx < cfg.x + w && cy >= cfg.y && cy < cfg.y + h;
    }

    private boolean isInGrid(double mouseX, double mouseY) {
        return mouseX >= gridX && mouseX < gridX + gridW && mouseY >= gridY && mouseY < gridY + gridH;
    }

    private void clampSelection(EyeConfig cfg) {
        int maxX = GRID_CELLS - cfg.size.w;
        int maxY = GRID_CELLS - cfg.size.h;

        if (cfg.x < 0) cfg.x = 0;
        if (cfg.y < 0) cfg.y = 0;
        if (cfg.x > maxX) cfg.x = maxX;
        if (cfg.y > maxY) cfg.y = maxY;
    }

    // ---------------- Render ----------------

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        computeGridLayout();

        gg.fill(0, 0, this.width, this.height, DIM_OVERLAY);

        // Draw widgets first (crisp), then our custom content (also crisp, avoids pause blur)
        super.render(gg, mouseX, mouseY, partialTick);

        gg.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFF);

        EyeConfig cfg = active();
        String eyeName = (activeEye == Eye.LEFT) ? "L" : "R";
        Component status = Component.literal("Eye: " + eyeName + " | Iris: "
                + cfg.size.w + "x" + cfg.size.h + " | "
                + (cfg.locked ? "Saved" : (canEdit() ? "Editing" : "Locked")));
        gg.drawCenteredString(this.font, status, this.width / 2, 26, 0xAAAAAA);

        renderPlayerFaceScaledNearest(gg);
        renderGridLines(gg);

        // Always show both highlights:
        // - saved/locked eyes in green
        // - active eye overlay in red (even if locked, red shows current eye selection)
        renderSavedHighlights(gg);
        renderActiveHighlight(gg);
    }

    private void renderPlayerFaceScaledNearest(GuiGraphics gg) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            gg.fill(gridX, gridY, gridX + gridW, gridY + gridH, 0x66000000);
            return;
        }

        AbstractClientPlayer player = mc.player;
        ResourceLocation skin = player.getSkin().texture();

        try {
            AbstractTexture tex = mc.getTextureManager().getTexture(skin);
            if (tex != null) tex.setFilter(false, false);
        } catch (Throwable ignored) {}

        RenderSystem.setShaderTexture(0, skin);

        gg.pose().pushPose();
        gg.pose().translate(gridX, gridY, 0);
        gg.pose().scale(cell, cell, 1.0f);

        gg.blit(
                skin,
                0, 0,
                FACE_U, FACE_V,
                FACE_PX, FACE_PX,
                SKIN_TEX_W, SKIN_TEX_H
        );

        gg.pose().popPose();
    }

    private void renderGridLines(GuiGraphics gg) {
        gg.hLine(gridX, gridX + gridW, gridY, GRID_BORDER);
        gg.hLine(gridX, gridX + gridW, gridY + gridH, GRID_BORDER);
        gg.vLine(gridX, gridY, gridY + gridH, GRID_BORDER);
        gg.vLine(gridX + gridW, gridY, gridY + gridH, GRID_BORDER);

        for (int i = 1; i < GRID_CELLS; i++) {
            int x = gridX + i * cell;
            int y = gridY + i * cell;

            gg.vLine(x, gridY, gridY + gridH, GRID_LINE);
            gg.hLine(gridX, gridX + gridW, y, GRID_LINE);
        }
    }

    private void renderSavedHighlights(GuiGraphics gg) {
        // Show both saved locks in green
        if (left.locked) renderSelection(gg, left, SAVED_FILL, SAVED_EDGE);
        if (right.locked) renderSelection(gg, right, SAVED_FILL, SAVED_EDGE);
    }

    private void renderActiveHighlight(GuiGraphics gg) {
        // Active eye always visible in red (even if locked; you can see which is selected)
        renderSelection(gg, active(), ACTIVE_FILL, ACTIVE_EDGE);
    }

    private void renderSelection(GuiGraphics gg, EyeConfig cfg, int fill, int edge) {
        int sx = gridX + cfg.x * cell;
        int sy = gridY + cfg.y * cell;
        int sw = cfg.size.w * cell;
        int sh = cfg.size.h * cell;

        gg.fill(sx, sy, sx + sw, sy + sh, fill);

        gg.hLine(sx, sx + sw, sy, edge);
        gg.hLine(sx, sx + sw, sy + sh, edge);
        gg.vLine(sx, sy, sy + sh, edge);
        gg.vLine(sx + sw, sy, sy + sh, edge);
    }

    // ---------------- Persistence ----------------

    private static final class StoredEye {
        String size = "1x1";
        int x = 3;
        int y = 5;
    }

    private static final class StoredConfig {
        StoredEye left = new StoredEye();
        StoredEye right = new StoredEye();
    }

    private StoredConfig toStored() {
        StoredConfig sc = new StoredConfig();
        sc.left.size = encodeSize(left.size);
        sc.left.x = left.x;
        sc.left.y = left.y;

        sc.right.size = encodeSize(right.size);
        sc.right.x = right.x;
        sc.right.y = right.y;

        return sc;
    }

    private void applyLoaded(StoredConfig sc) {
        left.size = decodeSize(sc.left.size);
        left.x = sc.left.x;
        left.y = sc.left.y;
        left.locked = true;
        clampSelection(left);

        right.size = decodeSize(sc.right.size);
        right.x = sc.right.x;
        right.y = sc.right.y;
        right.locked = true;
        clampSelection(right);

        editing = false;
        activeEye = Eye.LEFT;
        dragging = false;
    }

    private static String encodeSize(IrisSize s) {
        return switch (s) {
            case ONE_BY_ONE -> "1x1";
            case ONE_BY_TWO -> "1x2";
            case TWO_BY_TWO -> "2x2";
        };
    }

    private static IrisSize decodeSize(String s) {
        if (s == null) return IrisSize.ONE_BY_ONE;
        return switch (s) {
            case "1x2" -> IrisSize.ONE_BY_TWO;
            case "2x2" -> IrisSize.TWO_BY_TWO;
            default -> IrisSize.ONE_BY_ONE;
        };
    }

    private static final class ConfigStore {
        private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

        private static Path fileFor(UUID playerId) {
            Path dir = FMLPaths.CONFIGDIR.get().resolve(CreateCybernetics.MODID);
            return dir.resolve("cybereye_iris_" + playerId + ".json");
        }

        static @Nullable StoredConfig load(UUID playerId) {
            Path file = fileFor(playerId);
            if (!Files.exists(file)) return null;

            try (Reader r = Files.newBufferedReader(file)) {
                JsonObject obj = GSON.fromJson(r, JsonObject.class);
                if (obj == null) return null;

                StoredConfig sc = new StoredConfig();

                if (obj.has("left")) {
                    JsonObject l = obj.getAsJsonObject("left");
                    sc.left.size = l.has("size") ? l.get("size").getAsString() : "1x1";
                    sc.left.x = l.has("x") ? l.get("x").getAsInt() : 3;
                    sc.left.y = l.has("y") ? l.get("y").getAsInt() : 5;
                }

                if (obj.has("right")) {
                    JsonObject rr = obj.getAsJsonObject("right");
                    sc.right.size = rr.has("size") ? rr.get("size").getAsString() : "1x1";
                    sc.right.x = rr.has("x") ? rr.get("x").getAsInt() : 3;
                    sc.right.y = rr.has("y") ? rr.get("y").getAsInt() : 5;
                }

                return sc;
            } catch (Throwable ignored) {
                return null;
            }
        }

        static void save(UUID playerId, StoredConfig sc) {
            if (sc == null) return;

            Path file = fileFor(playerId);
            try {
                Files.createDirectories(file.getParent());

                JsonObject root = new JsonObject();

                JsonObject l = new JsonObject();
                l.addProperty("size", sc.left.size);
                l.addProperty("x", sc.left.x);
                l.addProperty("y", sc.left.y);

                JsonObject r = new JsonObject();
                r.addProperty("size", sc.right.size);
                r.addProperty("x", sc.right.x);
                r.addProperty("y", sc.right.y);

                root.add("left", l);
                root.add("right", r);

                try (Writer w = Files.newBufferedWriter(file)) {
                    GSON.toJson(root, w);
                }
            } catch (IOException ignored) {
            }
        }
    }

    private void pushConfigToPlayerNbtAndInvalidate() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer p = mc.player;
        if (p == null) return;

        // Root tag
        var root = p.getPersistentData().getCompound(CybereyeOverlayHandler.NBT_ROOT);

        // LEFT
        var l = new net.minecraft.nbt.CompoundTag();
        l.putInt(CybereyeOverlayHandler.NBT_X, FACE_U + left.x);   // grid 0..7 -> skin 8..15
        l.putInt(CybereyeOverlayHandler.NBT_Y, FACE_V + left.y);
        l.putInt(CybereyeOverlayHandler.NBT_VARIANT, encodeVariant(left.size));
        root.put(CybereyeOverlayHandler.NBT_LEFT, l);

        // RIGHT
        var r = new net.minecraft.nbt.CompoundTag();
        r.putInt(CybereyeOverlayHandler.NBT_X, FACE_U + right.x);
        r.putInt(CybereyeOverlayHandler.NBT_Y, FACE_V + right.y);
        r.putInt(CybereyeOverlayHandler.NBT_VARIANT, encodeVariant(right.size));
        root.put(CybereyeOverlayHandler.NBT_RIGHT, r);

        p.getPersistentData().put(CybereyeOverlayHandler.NBT_ROOT, root);

        // Force next render to rebuild the dynamic overlay
        CybereyeOverlayHandler.invalidate(p);
    }

    private static int encodeVariant(IrisSize size) {
        return switch (size) {
            case ONE_BY_ONE -> 0; // 1x1
            case ONE_BY_TWO -> 1; // 1x2
            case TWO_BY_TWO -> 2; // 2x2
        };
    }

    public static void applyClientConfigToPlayerNbtAndInvalidate(LocalPlayer p) {
        if (p == null) return;

        var layout = com.perigrine3.createcybernetics.client.CybereyeIrisConfigClient.get(p.getUUID());

        // Root tag
        var root = p.getPersistentData().getCompound(CybereyeOverlayHandler.NBT_ROOT);

        // LEFT
        var l = new net.minecraft.nbt.CompoundTag();
        l.putInt(CybereyeOverlayHandler.NBT_X, FACE_U + layout.left.x);
        l.putInt(CybereyeOverlayHandler.NBT_Y, FACE_V + layout.left.y);
        l.putInt(CybereyeOverlayHandler.NBT_VARIANT, encodeVariant(convert(layout.left.size)));
        root.put(CybereyeOverlayHandler.NBT_LEFT, l);

        // RIGHT
        var r = new net.minecraft.nbt.CompoundTag();
        r.putInt(CybereyeOverlayHandler.NBT_X, FACE_U + layout.right.x);
        r.putInt(CybereyeOverlayHandler.NBT_Y, FACE_V + layout.right.y);
        r.putInt(CybereyeOverlayHandler.NBT_VARIANT, encodeVariant(convert(layout.right.size)));
        root.put(CybereyeOverlayHandler.NBT_RIGHT, r);

        p.getPersistentData().put(CybereyeOverlayHandler.NBT_ROOT, root);

        CybereyeOverlayHandler.invalidate(p);
    }

    // maps client-config enum to screen enum for encodeVariant
    private static IrisSize convert(com.perigrine3.createcybernetics.client.CybereyeIrisConfigClient.IrisSize s) {
        return switch (s) {
            case ONE_BY_ONE -> IrisSize.ONE_BY_ONE;
            case ONE_BY_TWO -> IrisSize.ONE_BY_TWO;
            case TWO_BY_TWO -> IrisSize.TWO_BY_TWO;
        };
    }

}
