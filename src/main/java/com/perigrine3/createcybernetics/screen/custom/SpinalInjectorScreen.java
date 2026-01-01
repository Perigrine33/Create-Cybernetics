package com.perigrine3.createcybernetics.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.item.cyberware.SpinalInjectorItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SpinalInjectorScreen extends AbstractContainerScreen<SpinalInjectorMenu> {

    private static final ResourceLocation TEX =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/spinal_injector_gui.png");

    private static final int TEX_W = 256;
    private static final int TEX_H = 256;

    private static final int GUI_W = 185;
    private static final int GUI_H = 233;

    private static final int GUI_U = 35;
    private static final int GUI_V = 10;

    public SpinalInjectorScreen(SpinalInjectorMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = GUI_W;
        this.imageHeight = GUI_H;
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float partial, int mouseX, int mouseY) {
        gfx.blit(TEX, this.leftPos, this.topPos, GUI_U, GUI_V, this.imageWidth, this.imageHeight, TEX_W, TEX_H);
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partial) {
        this.renderBackground(gfx, mouseX, mouseY, partial);
        super.render(gfx, mouseX, mouseY, partial);

        renderInjectorCounts(gfx);

        this.renderTooltip(gfx, mouseX, mouseY);
    }

    private void renderInjectorCounts(GuiGraphics gfx) {
        gfx.pose().pushPose();
        // Very high Z so it is definitely above everything rendered earlier.
        gfx.pose().translate(0.0F, 0.0F, 10_000.0F);

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        for (int i = 0; i < SpinalInjectorItem.SLOT_COUNT; i++) {
            int count = this.menu.getInjectorDisplayCount(i);
            if (count <= 1) continue;

            int x = this.leftPos + this.menu.getInjectorSlotX(i);
            int y = this.topPos + this.menu.getInjectorSlotY(i);

            String text = Integer.toString(count);
            int w = this.font.width(text);

            // bottom-right within 16x16 slot
            int tx = x + 16 - w - 1;
            int ty = y + 16 - 8 - 1;

            gfx.drawString(this.font, text, tx, ty, 0xEDEDED, false);
        }

        RenderSystem.enableDepthTest();
        gfx.pose().popPose();
    }

    @Override
    protected void renderLabels(GuiGraphics gfx, int mouseX, int mouseY) {
        // intentionally no labels
    }
}
