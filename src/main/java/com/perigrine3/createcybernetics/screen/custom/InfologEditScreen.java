package com.perigrine3.createcybernetics.screen.custom;

import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.network.payload.InfologSaveChipwarePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.neoforge.network.PacketDistributor;

public final class InfologEditScreen extends Screen {

    private static final int GUI_W = 256;
    private static final int GUI_H = 256;

    private static final int PAD_L = 14;
    private static final int PAD_T = 14;
    private static final int PAD_R = 14;
    private static final int PAD_B = 42;

    // Neon green (ARGB)
    private static final int NEON_GREEN = 0xFF00FF66;

    private final int chipwareSlot;
    private final String initialText;

    private int leftPos;
    private int topPos;

    private int editX, editY, editW, editH;

    private int accentColor = 0xFFFFFFFF; // defaults to white (ARGB)
    private MultiLineEditBox editor;

    public InfologEditScreen(int chipwareSlot, String initialText) {
        super(Component.translatable("gui.infolog.title"));
        this.chipwareSlot = chipwareSlot;
        this.initialText = initialText == null ? "" : initialText;
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - GUI_W) / 2;
        this.topPos = (this.height - GUI_H) / 2;

        this.editX = leftPos + PAD_L;
        this.editY = topPos + PAD_T;
        this.editW = GUI_W - PAD_L - PAD_R;
        this.editH = GUI_H - PAD_T - PAD_B;

        this.accentColor = resolveChipDyeOrWhite();

        this.editor = new MultiLineEditBox(
                this.font,
                editX, editY,
                editW, editH,
                Component.empty(),
                Component.empty()
        );

        this.editor.setValue(this.initialText);

        this.addRenderableWidget(this.editor);
        this.setInitialFocus(this.editor);

        // Center the buttons under the edit box.
        int btnY = editY + editH + 10;
        int btnW = 90;
        int gap = 10;
        int totalW = btnW + gap + btnW;
        int startX = editX + (editW - totalW) / 2;

        this.addRenderableWidget(new AccentButton(
                startX, btnY, btnW, 20,
                Component.translatable("gui.cancel"),
                b -> onClose(),
                accentColor
        ));

        this.addRenderableWidget(new AccentButton(
                startX + btnW + gap, btnY, btnW, 20,
                Component.translatable("gui.done"),
                b -> {
                    sendSave();
                    onClose();
                },
                accentColor
        ));
    }

    private int resolveChipDyeOrWhite() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return 0xFFFFFFFF;
        if (!mc.player.hasData(ModAttachments.CYBERWARE)) return 0xFFFFFFFF;

        PlayerCyberwareData data = mc.player.getData(ModAttachments.CYBERWARE);
        if (data == null) return 0xFFFFFFFF;

        if (chipwareSlot < 0 || chipwareSlot >= PlayerCyberwareData.CHIPWARE_SLOT_COUNT) return 0xFFFFFFFF;

        ItemStack st = data.getChipwareStack(chipwareSlot);
        if (st.isEmpty()) return 0xFFFFFFFF;

        DyedItemColor dyed = st.get(DataComponents.DYED_COLOR);
        if (dyed == null) return 0xFFFFFFFF;

        // dyed.rgb() is 0xRRGGBB; convert to opaque ARGB
        return 0xFF000000 | (dyed.rgb() & 0x00FFFFFF);
    }

    private void sendSave() {
        String text = this.editor.getValue();
        if (text == null) text = "";
        if (text.length() > 32000) text = text.substring(0, 32000);

        PacketDistributor.sendToServer(new InfologSaveChipwarePayload(this.chipwareSlot, text, false));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        super.render(gg, mouseX, mouseY, partialTick);

        // Draw a 1px outline around the editor using the chip dye (or white).
        drawBorder(gg, editX - 1, editY - 1, editW + 2, editH + 2, accentColor);
    }

    private static void drawBorder(GuiGraphics gg, int x, int y, int w, int h, int argb) {
        // top
        gg.fill(x, y, x + w, y + 1, argb);
        // bottom
        gg.fill(x, y + h - 1, x + w, y + h, argb);
        // left
        gg.fill(x, y, x + 1, y + h, argb);
        // right
        gg.fill(x + w - 1, y, x + w, y + h, argb);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(null);
    }

    /**
     * Simple black button with 1px accent border and accent-colored label.
     */
    private static final class AccentButton extends Button {
        private final int accent;

        private AccentButton(int x, int y, int w, int h, Component msg, OnPress onPress, int accent) {
            super(x, y, w, h, msg, onPress, DEFAULT_NARRATION);
            this.accent = accent;
        }

        @Override
        protected void renderWidget(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
            int x = getX();
            int y = getY();
            int w = getWidth();
            int h = getHeight();

            // Black fill
            gg.fill(x, y, x + w, y + h, 0xFF000000);

            // Accent border
            drawBorder(gg, x, y, w, h, accent);

            // Slight hover tint (still dark)
            if (this.isHoveredOrFocused()) {
                gg.fill(x + 1, y + 1, x + w - 1, y + h - 1, 0x22000000);
            }

            // Centered label in accent color
            int textX = x + w / 2;
            int textY = y + (h - 8) / 2;
            gg.drawCenteredString(Minecraft.getInstance().font, getMessage(), textX, textY, accent);
        }
    }
}
