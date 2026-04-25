package com.perigrine3.createcybernetics.screen.custom.surgery.ripper;

import com.mojang.blaze3d.systems.RenderSystem;
import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.Optional;

public class RipperTradeScreen extends AbstractContainerScreen<RipperTradeMenu> {

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/ripper_trade.png");

    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;

    private static final int TRADE_INPUT_X = 53;
    private static final int TRADE_INPUT_Y = 13;

    private static final int REFURBISH_CYBERWARE_X = 35;
    private static final int REFURBISH_CYBERWARE_Y = 54;

    private static final int REFURBISH_EMERALD_X = 53;
    private static final int REFURBISH_EMERALD_Y = 54;

    public RipperTradeScreen(RipperTradeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        guiGraphics.blit(GUI_TEXTURE, leftPos, topPos,
                0, 0,
                imageWidth, imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font,
                Component.translatable("key.createcybernetics.rippergui.trade_title"),
                8, 6, 0x404040, false);

        guiGraphics.drawString(this.font,
                Component.translatable("key.createcybernetics.rippergui.refurbish_title"),
                8, 45, 0x404040, false);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (isHoveringEmptyInput(mouseX, mouseY, TRADE_INPUT_X, TRADE_INPUT_Y, 0)) {
            guiGraphics.renderTooltip(this.font,
                    List.of(Component.translatable("key.createcybernetics.rippergui.trade_input_desc")),
                    Optional.empty(), mouseX, mouseY);
            return;
        }

        if (isHoveringEmptyInput(mouseX, mouseY, REFURBISH_CYBERWARE_X, REFURBISH_CYBERWARE_Y, 2)) {
            guiGraphics.renderTooltip(this.font,
                    List.of(Component.translatable("key.createcybernetics.rippergui.refurbish_input1_desc")),
                    Optional.empty(), mouseX, mouseY);
            return;
        }

        if (isHoveringEmptyInput(mouseX, mouseY, REFURBISH_EMERALD_X, REFURBISH_EMERALD_Y, 3)) {
            guiGraphics.renderTooltip(this.font,
                    List.of(Component.translatable("key.createcybernetics.rippergui.refurbish_input2_desc")),
                    Optional.empty(), mouseX, mouseY);
            return;
        }

        super.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private boolean isHoveringEmptyInput(int mouseX, int mouseY, int slotX, int slotY, int menuSlotIndex) {
        if (menu.slots.get(menuSlotIndex).hasItem()) {
            return false;
        }

        int x = leftPos + slotX;
        int y = topPos + slotY;

        return mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}