package com.perigrine3.createcybernetics.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public final class HeatEngineScreen extends AbstractContainerScreen<HeatEngineMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/heatengine_gui.png");

    private static final ResourceLocation FLAME_TEX =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/flame_indicator.png");

    private static final int FLAME_TEX_W = 16;
    private static final int FLAME_TEX_H = 16;

    private static final int FLAME_DRAW_W = 12;
    private static final int FLAME_DRAW_H = 12;

    private static final int SLOT_SIZE = 18;

    private static final float FLAME_BASE_SCALE = 2.0f;

    private static final int PROGRESS_W = 24;
    private static final int PROGRESS_H = 2;

    private static final int FLAME_OFFSET_X = -2;
    private static final int FLAME_OFFSET_Y = 0;

    private static final int FLAME_ANCHOR_GAP_X = 3;

    public HeatEngineScreen(HeatEngineMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 184;
        this.imageHeight = 163;
    }

    @Override
    protected void renderBg(GuiGraphics gg, float partialTick, int mouseX, int mouseY) {
        gg.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if (menu.getBurnTimeClient() > 0) {
            drawFlameBesideFuelSlot(gg, partialTick);
        }

        drawProgressBar(gg);
    }

    private void drawProgressBar(GuiGraphics gg) {
        int cook = menu.getCookTimeClient();
        int total = menu.getCookTimeTotalClient();
        if (cook <= 0 || total <= 0) return;

        int filled = Mth.clamp((int) Math.floor(PROGRESS_W * (cook / (double) total)), 0, PROGRESS_W);

        int[] pos = computeProgressBarPos();
        int x0 = pos[0];
        int y0 = pos[1];

        gg.fill(x0, y0, x0 + filled, y0 + PROGRESS_H, 0xFFFFFFFF);
    }

    private void drawFlameBesideFuelSlot(GuiGraphics gg, float partialTick) {
        if (menu.slots.size() <= 2) return;

        Slot fuel = menu.slots.get(2);

        float baseX = leftPos + fuel.x + SLOT_SIZE + FLAME_ANCHOR_GAP_X + FLAME_OFFSET_X;
        float baseY = topPos + fuel.y + (SLOT_SIZE - FLAME_DRAW_H) / 2.0f + FLAME_OFFSET_Y;

        Minecraft mc = Minecraft.getInstance();
        float t = (mc.level != null ? (mc.level.getGameTime() + partialTick) : partialTick);

        float pulse = 1.0f + 0.06f * Mth.sin(t * 0.35f);
        float bob = 0.8f * Mth.sin(t * 0.25f);
        float alpha = 0.85f + 0.15f * Mth.sin(t * 0.40f);

        float scale = FLAME_BASE_SCALE * pulse;

        float cx = baseX + FLAME_DRAW_W / 2.0f;
        float cy = baseY + FLAME_DRAW_H / 2.0f;

        gg.pose().pushPose();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

        gg.pose().translate(cx, cy + bob, 0.0f);
        gg.pose().scale(scale, scale, 1.0f);
        gg.pose().translate(-cx, -cy, 0.0f);

        gg.blit(
                FLAME_TEX,
                (int) baseX, (int) baseY,
                FLAME_DRAW_W, FLAME_DRAW_H,
                0, 0,
                FLAME_TEX_W, FLAME_TEX_H,
                FLAME_TEX_W, FLAME_TEX_H
        );

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        gg.pose().popPose();
    }

    private int[] computeProgressBarPos() {
        int fallbackX = leftPos + (imageWidth - PROGRESS_W) / 2;
        int fallbackY = topPos + 44;

        if (menu.slots.size() <= 1) {
            return new int[]{fallbackX, fallbackY};
        }

        Slot input = menu.slots.get(0);
        Slot output = menu.slots.get(1);

        int inputCenterX = input.x + 8;
        int outputCenterX = output.x + 8;
        int centerX = (inputCenterX + outputCenterX) / 2;

        int x0 = leftPos + centerX - (PROGRESS_W / 2);

        int topSlotY = Math.min(input.y, output.y);
        int y0 = topPos + topSlotY - 7;

        y0 = Math.max(topPos + 5, y0);

        return new int[]{x0, y0};
    }

    @Override
    protected void renderLabels(GuiGraphics gg, int mouseX, int mouseY) {
    }
}
