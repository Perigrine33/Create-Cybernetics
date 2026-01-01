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

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
