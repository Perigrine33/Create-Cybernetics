package com.perigrine3.createcybernetics.compat.corpse;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CorpseCyberwareScreen extends AbstractContainerScreen<CorpseCyberwareMenu> {

    public CorpseCyberwareScreen(CorpseCyberwareMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 194;
        this.imageHeight = 256;
        this.inventoryLabelY = this.imageHeight - 94;

        CreateCybernetics.LOGGER.info(
                "[corpse compat] CorpseCyberwareScreen ctor hit; menuClass={}, title={}, imageWidth={}, imageHeight={}",
                menu == null ? "null" : menu.getClass().getName(),
                title == null ? "null" : title.getString(),
                this.imageWidth,
                this.imageHeight
        );
    }

    @Override
    protected void init() {
        super.init();

        CreateCybernetics.LOGGER.info(
                "[corpse compat] CorpseCyberwareScreen init hit; leftPos={}, topPos={}, width={}, height={}, menuSlots={}",
                this.leftPos,
                this.topPos,
                this.width,
                this.height,
                this.menu == null ? -1 : this.menu.slots.size()
        );
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        CreateCybernetics.LOGGER.info(
                "[corpse compat] CorpseCyberwareScreen renderBg hit; leftPos={}, topPos={}, partialTick={}, mouseX={}, mouseY={}",
                this.leftPos,
                this.topPos,
                partialTick,
                mouseX,
                mouseY
        );

        int x = leftPos;
        int y = topPos;

        guiGraphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFF1A1A1A);
        guiGraphics.fill(x + 1, y + 1, x + imageWidth - 1, y + imageHeight - 1, 0xFF2A2A2A);

        int startX = x + 7;
        int startY = y + 18;

        for (int row = 0; row < CorpseCyberwareMenu.ROWS; row++) {
            for (int col = 0; col < CorpseCyberwareMenu.COLUMNS; col++) {
                int sx = startX + col * 18;
                int sy = startY + row * 18;
                guiGraphics.fill(sx, sy, sx + 16, sy + 16, 0xFF111111);
                guiGraphics.fill(sx - 1, sy - 1, sx + 17, sy, 0xFF555555);
                guiGraphics.fill(sx - 1, sy + 16, sx + 17, sy + 17, 0xFF000000);
                guiGraphics.fill(sx - 1, sy, sx, sy + 16, 0xFF555555);
                guiGraphics.fill(sx + 16, sy, sx + 17, sy + 16, 0xFF000000);
            }
        }

        int playerInvY = y + 176;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int sx = x + 15 + col * 18;
                int sy = playerInvY + row * 18;
                guiGraphics.fill(sx, sy, sx + 16, sy + 16, 0xFF111111);
            }
        }

        int hotbarY = y + 234;
        for (int col = 0; col < 9; col++) {
            int sx = x + 15 + col * 18;
            guiGraphics.fill(sx, hotbarY, sx + 16, hotbarY + 16, 0xFF111111);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        CreateCybernetics.LOGGER.info(
                "[corpse compat] CorpseCyberwareScreen renderLabels hit; title={}, playerInventoryTitle={}, mouseX={}, mouseY={}",
                this.title == null ? "null" : this.title.getString(),
                this.playerInventoryTitle == null ? "null" : this.playerInventoryTitle.getString(),
                mouseX,
                mouseY
        );

        guiGraphics.drawString(this.font, this.title, 8, 6, 0xFFFFFF, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 15, this.inventoryLabelY, 0xFFFFFF, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        CreateCybernetics.LOGGER.info(
                "[corpse compat] CorpseCyberwareScreen render hit; mouseX={}, mouseY={}, partialTick={}",
                mouseX,
                mouseY,
                partialTick
        );

        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}