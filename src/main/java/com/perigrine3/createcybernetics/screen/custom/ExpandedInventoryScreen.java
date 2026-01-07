package com.perigrine3.createcybernetics.screen.custom;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ExpandedInventoryScreen extends AbstractContainerScreen<ExpandedInventoryMenu> {

    private static final ResourceLocation TEX =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/inventory_crafting.png");

    public ExpandedInventoryScreen(ExpandedInventoryMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Intentionally empty: removes the big vanilla labels.
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEX, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        // Custom slot backgrounds for shard slots (only when present)
        if (this.menu != null && this.menu.hasDataShardSlots()) {
            int sx = leftPos + ExpandedInventoryMenu.DATA_SHARD_X;
            int sy0 = topPos + ExpandedInventoryMenu.DATA_SHARD_Y;
            int sy1 = sy0 + ExpandedInventoryMenu.DATA_SHARD_SPACING;

            drawSlotBackground(graphics, sx, sy0);
            drawSlotBackground(graphics, sx, sy1);
        }

        if (this.minecraft == null || this.minecraft.player == null) return;

        int x1 = leftPos + 26;
        int y1 = topPos + 8;
        int x2 = leftPos + 75;
        int y2 = topPos + 78;
        int scale = 30;

        InventoryScreen.renderEntityInInventoryFollowsMouse(
                graphics,
                x1, y1, x2, y2,
                scale,
                0.0f,
                (float) mouseX, (float) mouseY,
                this.minecraft.player
        );
    }

    private static void drawSlotBackground(GuiGraphics gg, int x, int y) {
        int left = x - 1;
        int top = y - 1;
        int right = x + 17;
        int bottom = y + 17;

        gg.fill(left, top, right, bottom, 0xE005070A);

        gg.fill(left, top, right, top + 1, 0xFF4AB3FF);
        gg.fill(left, bottom - 1, right, bottom, 0xFF4AB3FF);
        gg.fill(left, top, left + 1, bottom, 0xFF4AB3FF);
        gg.fill(right - 1, top, right, bottom, 0xFF4AB3FF);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
