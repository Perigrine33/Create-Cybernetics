package com.perigrine3.createcybernetics.screen.custom.crafting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class SoftCompatInventoryIconButton extends AbstractButton {

    public enum Kind {
        CURIOS_COSMETIC
    }

    private static final ResourceLocation COSMETIC_OFF =
            ResourceLocation.fromNamespaceAndPath("curios", "cosmetic_off");

    private static final ResourceLocation COSMETIC_OFF_HIGHLIGHTED =
            ResourceLocation.fromNamespaceAndPath("curios", "cosmetic_off_highlighted");

    private final Kind kind;
    private final Runnable action;

    public SoftCompatInventoryIconButton(int x, int y, int width, int height, Kind kind, Component tooltip, Runnable action) {
        super(x, y, width, height, tooltip);
        this.kind = kind;
        this.action = action;
    }

    @Override
    public void onPress() {
        if (action != null) {
            action.run();
        }
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blitSprite(texture(), getX(), getY(), this.width, this.height);

        if (!this.active) {
            graphics.fill(getX(), getY(), getX() + this.width, getY() + this.height, 0x99000000);
        }
    }

    private ResourceLocation texture() {
        return switch (kind) {
            case CURIOS_COSMETIC -> this.isHoveredOrFocused() ? COSMETIC_OFF_HIGHLIGHTED : COSMETIC_OFF;
        };
    }

    @Override
    protected void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }

    public void renderTooltipIfHovered(GuiGraphics graphics, int mouseX, int mouseY) {
        if (this.isHoveredOrFocused()) {
            Minecraft mc = Minecraft.getInstance();
            graphics.renderTooltip(mc.font, this.getMessage(), mouseX, mouseY);
        }
    }
}