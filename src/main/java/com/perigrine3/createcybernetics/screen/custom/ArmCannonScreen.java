package com.perigrine3.createcybernetics.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ArmCannonScreen extends AbstractContainerScreen<ArmCannonMenu> {

    private static final ResourceLocation TEX =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/arm_cannon_gui.png");

    public ArmCannonScreen(ArmCannonMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 256;
        this.imageHeight = 256;

        this.titleLabelX = 0x7fffffff;
        this.inventoryLabelX = 0x7fffffff;
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEX);
        gfx.blit(TEX, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        renderBackground(gfx, mouseX, mouseY, partialTick);
        super.render(gfx, mouseX, mouseY, partialTick);
        renderTooltip(gfx, mouseX, mouseY);
    }
}
