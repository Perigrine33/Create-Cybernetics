package com.perigrine3.createcybernetics.screen.custom.spinal_injector;

import com.perigrine3.createcybernetics.CreateCybernetics;
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
        this.renderTooltip(gfx, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics gfx, int mouseX, int mouseY) {
    }
}