package com.perigrine3.createcybernetics.screen.custom.cyberdeck;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class CyberdeckScreen extends AbstractContainerScreen<CyberdeckMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/cyberdeck.png");

    private static final int PANEL_COLOR = 0xFF111111;
    private static final int SLOT_FILL_COLOR = 0xFF000000;
    private static final int SLOT_OUTLINE_COLOR = 0xFF00FFFF;

    public CyberdeckScreen(CyberdeckMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
        this.titleLabelY = 6;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int left = this.leftPos;
        int top = this.topPos;

        guiGraphics.fill(left, top, left + this.imageWidth, top + this.imageHeight, PANEL_COLOR);

        for (int i = 0; i < CyberdeckMenu.QUICKHACK_SLOT_COUNT; i++) {
            Slot slot = this.menu.slots.get(i);
            drawQuickhackSlot(guiGraphics, slot.x + left, slot.y + top);
        }

        guiGraphics.blit(TEXTURE, left, top, 0, 0, this.imageWidth, this.imageHeight);
    }

    private void drawQuickhackSlot(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.fill(x, y, x + 16, y + 16, SLOT_FILL_COLOR);
        guiGraphics.fill(x - 1, y - 1, x + 17, y, SLOT_OUTLINE_COLOR);
        guiGraphics.fill(x - 1, y + 16, x + 17, y + 17, SLOT_OUTLINE_COLOR);
        guiGraphics.fill(x - 1, y, x, y + 16, SLOT_OUTLINE_COLOR);
        guiGraphics.fill(x + 16, y, x + 17, y + 16, SLOT_OUTLINE_COLOR);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x00FFFF, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}