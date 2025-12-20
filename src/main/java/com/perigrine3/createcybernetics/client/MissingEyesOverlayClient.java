package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.util.ModTags;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class MissingEyesOverlayClient {

    private MissingEyesOverlayClient() {}

    // 0x00..0xFF alpha. Higher = darker.
    private static final int ALPHA = 0xFF;

    @SubscribeEvent
    public static void onRenderGuiPre(RenderGuiEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (mc.screen != null) return;

        PlayerCyberwareData data = mc.player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        boolean hasEyes = data.hasAnyTagged(ModTags.Items.EYE_ITEMS, CyberwareSlot.EYES);
        if (hasEyes) return;

        GuiGraphics gg = event.getGuiGraphics();
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int argb = (ALPHA << 24) | 0x000000;
        gg.fill(0, 0, w, h, argb);

        RenderSystem.disableBlend();
    }
}
