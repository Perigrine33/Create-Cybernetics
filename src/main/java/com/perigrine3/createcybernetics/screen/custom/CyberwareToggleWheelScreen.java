package com.perigrine3.createcybernetics.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.network.payload.CyberwareTogglePayloads;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class CyberwareToggleWheelScreen extends Screen {

    private static final ResourceLocation LAYER_ID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware_toggle_wheel");

    private static boolean OPEN = false;

    // Track currently selected segment so LMB can toggle it.
    private static int SELECTED_INDEX = 0;

    // Sticky selection: when cursor recenters, keep last chosen segment instead of snapping.
    private static int STICKY_INDEX = 0;

    private record Entry(ItemStack icon, CyberwareSlot slot, int index) {}
    private static final List<Entry> ENTRIES = new ArrayList<>();

    // ---- Virtual "radial cursor" driven by head movement deltas ----
    // Cursor space is like screen space: +X right, +Y down.
    private static double CURSOR_X = 0.0;
    private static double CURSOR_Y = 0.0;

    private static boolean HAS_LAST_ROT = false;
    private static float LAST_YAW = 0.0f;
    private static float LAST_PITCH = 0.0f;

    // Tuning knobs:
    // Smaller => more subtle movement per degree.
    private static final double YAW_TO_CURSOR = 0.020;   // degrees -> cursor units
    private static final double PITCH_TO_CURSOR = 0.020; // degrees -> cursor units

    // Damping each tick (closer to 1.0 = slower recenter; smaller = faster recenter)
    private static final double DAMPING = 0.85;

    // Clamp so spinning doesn’t blow it out; also helps responsiveness after large turns.
    private static final double CURSOR_MAX = 1.25;

    // If the cursor is near center, keep last selection.
    private static final double SELECT_DEADZONE = 0.08;

    public CyberwareToggleWheelScreen() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        OPEN = true;

        // Reset selection + cursor each time you open.
        STICKY_INDEX = 0;
        SELECTED_INDEX = 0;
        CURSOR_X = 0.0;
        CURSOR_Y = 0.0;

        // Ask server for current enabled states so UI text is accurate.
        PacketDistributor.sendToServer(new CyberwareTogglePayloads.RequestToggleStatesPayload());

        Minecraft mc = Minecraft.getInstance();
        Player p = mc.player;
        if (p != null) {
            LAST_YAW = p.getYRot();
            LAST_PITCH = p.getXRot();
            HAS_LAST_ROT = true;
        } else {
            HAS_LAST_ROT = false;
        }

        // Immediately return focus to gameplay (so player can keep moving/looking).
        if (this.minecraft != null && this.minecraft.screen == this) {
            this.minecraft.setScreen(null);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static boolean isWheelOpen() {
        return OPEN;
    }

    public static void closeWheel() {
        OPEN = false;
        HAS_LAST_ROT = false;
    }

    /**
     * Called by LMB while wheel is open.
     * Sends a toggle payload for the currently selected entry (slot/index).
     */
    public static void toggleSelected() {
        if (!OPEN) return;
        if (ENTRIES.isEmpty()) return;

        int idx = Mth.clamp(SELECTED_INDEX, 0, ENTRIES.size() - 1);
        Entry e = ENTRIES.get(idx);

        PacketDistributor.sendToServer(new CyberwareTogglePayloads.ToggleCyberwarePayload(e.slot().name(), e.index()));
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        // no-op
    }

    public boolean shouldBlurBackground() {
        return false;
    }

    @Override
    public void onClose() {
        OPEN = false;
        HAS_LAST_ROT = false;
        super.onClose();
    }

    /* -----------------------------
     * HUD layer registration/render
     * ----------------------------- */

    @EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static final class ClientModBus {
        @SubscribeEvent
        public static void registerGuiLayers(RegisterGuiLayersEvent event) {
            event.registerAboveAll(LAYER_ID, CyberwareToggleWheelScreen::renderHudLayer);
        }
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
    public static final class ClientGameBus {

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            if (!OPEN) return;

            Minecraft mc = Minecraft.getInstance();

            // Requirement: opening any other GUI closes it.
            if (mc.screen != null) {
                closeWheel();
                return;
            }

            // While the wheel is open, do not allow holding LMB to keep mining.
            KeyMapping attack = mc.options.keyAttack;
            if (attack != null && attack.isDown()) {
                attack.setDown(false);
            }

            Player p = mc.player;
            if (p == null) return;

            float yaw = p.getYRot();
            float pitch = p.getXRot();

            if (!HAS_LAST_ROT) {
                LAST_YAW = yaw;
                LAST_PITCH = pitch;
                HAS_LAST_ROT = true;
                return;
            }

            // dyaw in [-180,180] handles full spins smoothly.
            float dyaw = Mth.wrapDegrees(yaw - LAST_YAW);
            float dpitch = pitch - LAST_PITCH; // pitch is bounded [-90,90], wrap not needed.

            LAST_YAW = yaw;
            LAST_PITCH = pitch;

            // Update cursor: turning right pushes +X, looking down pushes +Y.
            CURSOR_X += dyaw * YAW_TO_CURSOR;
            CURSOR_Y += dpitch * PITCH_TO_CURSOR;

            // Clamp so big turns don’t lock selection far away.
            CURSOR_X = Mth.clamp(CURSOR_X, -CURSOR_MAX, CURSOR_MAX);
            CURSOR_Y = Mth.clamp(CURSOR_Y, -CURSOR_MAX, CURSOR_MAX);

            // Damping pulls cursor back toward center over time.
            CURSOR_X *= DAMPING;
            CURSOR_Y *= DAMPING;
        }

        @SubscribeEvent
        public static void onMouseButton(InputEvent.MouseButton.Pre event) {
            if (!OPEN) return;
            if (event.getAction() != GLFW.GLFW_PRESS) return;

            Minecraft mc = Minecraft.getInstance();

            // LMB: TOGGLE selected (and cancel so it does not break blocks)
            if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                event.setCanceled(true);

                // Force-release attack so it doesn't start mining this tick.
                KeyMapping attack = mc.options.keyAttack;
                if (attack != null) attack.setDown(false);

                toggleSelected();
                return;
            }

            // RMB: CLOSE (and cancel so it does not place/use items)
            if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                event.setCanceled(true);
                closeWheel();
            }
        }
    }

    private static void renderHudLayer(GuiGraphics graphics, DeltaTracker delta) {
        if (!OPEN) return;

        Minecraft mc = Minecraft.getInstance();

        // Requirement: opening any other GUI closes it.
        if (mc.screen != null) {
            closeWheel();
            return;
        }

        PlayerCyberwareData data = rebuildEntries(mc);

        var window = mc.getWindow();

        // GUI-space dimensions (scaled by vanilla GUI scale)
        int w = window.getGuiScaledWidth();
        int h = window.getGuiScaledHeight();
        int cx = w / 2;
        int cy = h / 2;

        // --- GUI SCALE COMPATIBILITY ---
        // Compute radii in raw screen pixels so the wheel's physical size is consistent across GUI scale,
        // then convert back into GUI units by dividing by the current GUI scale factor.
        int sw = window.getScreenWidth();    // raw pixels
        int sh = window.getScreenHeight();   // raw pixels
        double guiScale = window.getGuiScale(); // actual factor (works for "Auto" too)

        float outerR_px = Math.min(sw, sh) * 0.37f;   // same tuned proportion, but in pixels
        float outerR = (float) (outerR_px / guiScale);

        float innerR = outerR * 0.40f;
        float midR = (innerR + outerR) * 0.5f;

        int n = Math.max(1, ENTRIES.size());

        int selected = selectedIndexFromCursor(n);
        SELECTED_INDEX = selected;

        final int baseArgb = 0x88000000;
        final int hoverArgb = 0xAA2E7BFF;

        // Draw segmented donut; selected segment lights up blue.
        for (int i = 0; i < n; i++) {
            int argb = (i == selected) ? hoverArgb : baseArgb;
            drawDonutSegment(graphics, cx, cy, innerR, outerR, n, i, 24, argb);
        }

        // Draw icons + labels.
        final int nameColor = 0xFFFFFFFF;
        final int enabledColor = 0xFF55FF55;
        final int disabledColor = 0xFFFF5555;

        RenderSystem.enableDepthTest();

        for (int i = 0; i < ENTRIES.size(); i++) {
            Entry e = ENTRIES.get(i);

            double ang = angleForIndex(n, i) + ((Math.PI * 2.0) / n) * 0.5;

            int centerX = (int) Math.round(cx + Math.cos(ang) * midR);
            int centerY = (int) Math.round(cy + Math.sin(ang) * midR);

            // Icon (16x16), centered
            int ix = centerX - 8;
            int iy = centerY - 8;
            graphics.renderItem(e.icon(), ix, iy);

            // Name (above icon) - scaled down (~55%)
            String rawName = e.icon().getHoverName().getString();
            String name = (rawName.length() > 22) ? (rawName.substring(0, 21) + "…") : rawName;

            var poseStack = graphics.pose();
            poseStack.pushPose();

            final float nameScale = 0.55f;
            poseStack.scale(nameScale, nameScale, 1.0f);

            int nameW = mc.font.width(name);
            int scaledCenterX = (int) (centerX / nameScale);
            int scaledNameX = scaledCenterX - (nameW / 2);
            int scaledNameY = (int) ((iy - (mc.font.lineHeight + 2)) / nameScale);

            graphics.drawString(mc.font, name, scaledNameX, scaledNameY, nameColor, true);

            poseStack.popPose();

            // Enabled/Disabled (below icon), based on PlayerCyberwareData enabled flag
            boolean enabled = data != null && data.isEnabled(e.slot(), e.index());
            String stateText = enabled ? "ENABLED" : "DISABLED";

            int stateW = mc.font.width(stateText);
            int stateX = centerX - (stateW / 2);
            int stateY = iy + 16 + 2;

            graphics.drawString(
                    mc.font,
                    stateText,
                    stateX,
                    stateY,
                    enabled ? enabledColor : disabledColor,
                    true
            );
        }
    }

    /**
     * Converts the virtual cursor direction into a segment index.
     * 0 is at the top, increasing clockwise, matching ring math.
     * When cursor recenters, keep last meaningful index (sticky/momentum).
     */
    private static int selectedIndexFromCursor(int n) {
        if (n <= 0) return 0;

        double mag = Math.sqrt(CURSOR_X * CURSOR_X + CURSOR_Y * CURSOR_Y);

        if (mag < SELECT_DEADZONE) {
            return Mth.clamp(STICKY_INDEX, 0, n - 1);
        }

        double ang = Math.atan2(CURSOR_Y, CURSOR_X); // -pi..pi, 0 at +X, +pi/2 at +Y (down)
        ang = (ang + Math.PI / 2.0 + (Math.PI * 2.0)) % (Math.PI * 2.0); // 0 at top, clockwise

        int idx = (int) Math.floor((ang / (Math.PI * 2.0)) * n);
        if (idx < 0) idx += n;
        if (idx >= n) idx -= n;

        STICKY_INDEX = idx;
        return idx;
    }

    /* -----------------------------
     * Build entries (per installed instance)
     * ----------------------------- */

    private static PlayerCyberwareData rebuildEntries(Minecraft mc) {
        ENTRIES.clear();

        if (mc.player == null) return null;
        if (!mc.player.hasData(ModAttachments.CYBERWARE)) return null;

        PlayerCyberwareData data = mc.player.getData(ModAttachments.CYBERWARE);
        if (data == null) return null;

        for (var entry : data.getAll().entrySet()) {
            CyberwareSlot slot = entry.getKey();
            var arr = entry.getValue();
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {
                var cw = arr[i];
                if (cw == null) continue;

                ItemStack stack = cw.getItem();
                if (stack == null || stack.isEmpty()) continue;
                if (!stack.is(ModTags.Items.TOGGLEABLE_CYBERWARE)) continue;

                ENTRIES.add(new Entry(stack.copy(), slot, i));
            }
        }

        if (SELECTED_INDEX >= ENTRIES.size()) {
            SELECTED_INDEX = Math.max(0, ENTRIES.size() - 1);
        }
        if (STICKY_INDEX >= ENTRIES.size()) {
            STICKY_INDEX = Math.max(0, ENTRIES.size() - 1);
        }

        return data;
    }

    /* -----------------------------
     * Segment angles
     * ----------------------------- */

    private static double angleForIndex(int n, int i) {
        double step = (Math.PI * 2.0) / n;
        return -Math.PI / 2.0 + step * i;
    }

    /* -----------------------------
     * Rendering: true donut segments
     * ----------------------------- */

    private static void drawDonutSegment(
            GuiGraphics graphics,
            int cx, int cy,
            float innerR, float outerR,
            int n, int idx,
            int arcSteps,
            int argb
    ) {
        float a = ((argb >>> 24) & 0xFF) / 255.0f;
        float r = ((argb >>> 16) & 0xFF) / 255.0f;
        float g = ((argb >>> 8) & 0xFF) / 255.0f;
        float b = (argb & 0xFF) / 255.0f;

        double step = (Math.PI * 2.0) / (double) n;
        double a0 = -Math.PI / 2.0 + step * (double) idx;
        double a1 = a0 + step;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Matrix4f pose = graphics.pose().last().pose();
        BufferBuilder bb = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        for (int i = 0; i <= arcSteps; i++) {
            double t = i / (double) arcSteps;
            double ang = a0 + (a1 - a0) * t;

            float cos = (float) Math.cos(ang);
            float sin = (float) Math.sin(ang);

            float xo = cx + cos * outerR;
            float yo = cy + sin * outerR;

            float xi = cx + cos * innerR;
            float yi = cy + sin * innerR;

            bb.addVertex(pose, xo, yo, 0.0f).setColor(r, g, b, a);
            bb.addVertex(pose, xi, yi, 0.0f).setColor(r, g, b, a);
        }

        BufferUploader.drawWithShader(bb.buildOrThrow());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
}
