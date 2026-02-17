package com.perigrine3.createcybernetics.client.gui;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class CCIconButton extends AbstractButton {

    public enum Icon {
        CYBEREYE_SKIN(ResourceLocation.fromNamespaceAndPath(
                CreateCybernetics.MODID, "textures/gui/cybereye_skin.png"
        )),
        HUD_LAYOUT(ResourceLocation.fromNamespaceAndPath(
                CreateCybernetics.MODID, "textures/gui/hud_layout.png"
        ));

        final ResourceLocation texture;

        Icon(ResourceLocation texture) {
            this.texture = texture;
        }
    }

    private final Icon icon;
    private final Runnable onPress;
    private final String markerId;

    private static final int DRAW_ICON_W = 16;
    private static final int DRAW_ICON_H = 16;

    private static final int ICON_TEX_W = 16;
    private static final int ICON_TEX_H = 16;

    public CCIconButton(int x, int y, int w, int h, Icon icon, Component tooltip, String markerId, Runnable onPress) {
        super(x, y, w, h, Component.empty());
        this.icon = icon;
        this.onPress = onPress;
        this.markerId = markerId;

        setTooltip(Tooltip.create(tooltip));
    }

    public String cc$getMarkerId() {
        return markerId;
    }

    @Override
    public void onPress() {
        if (onPress != null) onPress.run();
    }

    @Override
    protected void renderWidget(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(gg, mouseX, mouseY, partialTick);

        int ix = getX() + (width - DRAW_ICON_W) / 2;
        int iy = getY() + (height - DRAW_ICON_H) / 2;

        if (!this.active) {
            gg.setColor(1f, 1f, 1f, 0.35f);
        }

        // Draw the full icon texture scaled to DRAW_ICON_W/H
        gg.blit(icon.texture, ix, iy, 0, 0,
                DRAW_ICON_W, DRAW_ICON_H,
                ICON_TEX_W, ICON_TEX_H
        );

        gg.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narration) {
        this.defaultButtonNarrationText(narration);
    }
}
