package com.perigrine3.createcybernetics.screen.custom.surgery.ripper;

import com.mojang.blaze3d.systems.RenderSystem;
import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class SurgeryPaymentScreen extends AbstractContainerScreen<SurgeryPaymentMenu> {

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/ripper_payment.png");

    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;

    private static final int INSTALL_TITLE_X = 33;
    private static final int INSTALL_TITLE_Y = 6;
    private static final int INSTALL_LIST_X = 13;
    private static final int INSTALL_LIST_START_Y = 16;

    private static final int REMOVE_TITLE_X = 110;
    private static final int REMOVE_TITLE_Y = 6;
    private static final int REMOVE_LIST_X = 90;
    private static final int REMOVE_LIST_START_Y = 16;

    private static final int TOTAL_PRICE_X = 107;
    private static final int TOTAL_PRICE_Y = 97;

    private static final int PAYMENT_LABEL_X = 25;
    private static final int PAYMENT_LABEL_Y = 97;

    private static final int TRUST_TITLE_X = 45;
    private static final int TRUST_TITLE_Y = 74;

    private static final int TRUST_BAR_X = 24;
    private static final int TRUST_BAR_Y = 83;
    private static final int TRUST_BAR_W = 128;
    private static final int TRUST_BAR_H = 8;

    private static final int CONFIRM_BUTTON_X = 44;
    private static final int CONFIRM_BUTTON_Y = 111;
    private static final int CONFIRM_BUTTON_W = 40;
    private static final int CONFIRM_BUTTON_H = 16;

    private static final int CANCEL_BUTTON_X = 92;
    private static final int CANCEL_BUTTON_Y = 111;
    private static final int CANCEL_BUTTON_W = 40;
    private static final int CANCEL_BUTTON_H = 16;

    private Button confirmButton;
    private Button cancelButton;

    public SurgeryPaymentScreen(SurgeryPaymentMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = SurgeryPaymentMenu.IMAGE_WIDTH;
        this.imageHeight = SurgeryPaymentMenu.IMAGE_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        confirmButton = Button.builder(
                        Component.literal("Confirm"),
                        button -> {
                            if (minecraft != null && minecraft.gameMode != null) {
                                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
                            }
                        }
                )
                .bounds(leftPos + CONFIRM_BUTTON_X, topPos + CONFIRM_BUTTON_Y, CONFIRM_BUTTON_W, CONFIRM_BUTTON_H)
                .build();

        cancelButton = Button.builder(
                        Component.literal("Cancel"),
                        button -> {
                            if (minecraft != null && minecraft.gameMode != null) {
                                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 1);
                            }
                        }
                )
                .bounds(leftPos + CANCEL_BUTTON_X, topPos + CANCEL_BUTTON_Y, CANCEL_BUTTON_W, CANCEL_BUTTON_H)
                .build();

        addRenderableWidget(confirmButton);
        addRenderableWidget(cancelButton);
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        if (confirmButton != null) {
            confirmButton.active = menu.hasSufficientPayment() && menu.getTotalPrice() > 0;
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        guiGraphics.blit(
                GUI_TEXTURE,
                leftPos,
                topPos,
                0,
                0,
                imageWidth,
                imageHeight,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );

        int trustBarX = leftPos + TRUST_BAR_X;
        int trustBarY = topPos + TRUST_BAR_Y;

        guiGraphics.fill(trustBarX, trustBarY, trustBarX + TRUST_BAR_W, trustBarY + TRUST_BAR_H, 0xFF202020);

        float pct = Mth.clamp(menu.getTrustworthiness() / 100.0F, 0.0F, 1.0F);
        int fill = (int) (TRUST_BAR_W * pct);
        int color = pct > 0.66F ? 0xFF2AFF00 : pct > 0.25F ? 0xFFFFAA00 : 0xFFFF0000;

        guiGraphics.fill(trustBarX, trustBarY, trustBarX + fill, trustBarY + TRUST_BAR_H, color);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        List<String> installs = menu.getInstallEntries();
        List<String> removals = menu.getRemovalEntries();

        guiGraphics.drawString(font, "Install", INSTALL_TITLE_X, INSTALL_TITLE_Y, 0xFF2AFF00, false);

        int y = INSTALL_LIST_START_Y;
        for (String s : installs) {
            guiGraphics.drawString(font, "- " + s, INSTALL_LIST_X, y, 0xFF2AFF00, false);
            y += 10;
        }

        guiGraphics.drawString(font, "Remove", REMOVE_TITLE_X, REMOVE_TITLE_Y, 0xFFFF4040, false);

        y = REMOVE_LIST_START_Y;
        for (String s : removals) {
            guiGraphics.drawString(font, "- " + s, REMOVE_LIST_X, y, 0xFFFF4040, false);
            y += 10;
        }

        guiGraphics.drawString(font, menu.getTotalPrice() + " Emeralds", TOTAL_PRICE_X, TOTAL_PRICE_Y, 0xFFFFFFFF, false);
        guiGraphics.drawString(font, "Payment", PAYMENT_LABEL_X, PAYMENT_LABEL_Y, 0xFFD0D0D0, false);
        guiGraphics.drawString(font, "Trustworthiness", TRUST_TITLE_X, TRUST_TITLE_Y, 0xFFFFFFFF, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}