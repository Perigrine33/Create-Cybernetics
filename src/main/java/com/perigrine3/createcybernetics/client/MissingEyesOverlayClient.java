package com.perigrine3.createcybernetics.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class MissingEyesOverlayClient {

    private MissingEyesOverlayClient() {}

    private static final int ALPHA = 0xFF;

    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.screen != null) return;
        if (!mc.player.hasData(ModAttachments.CYBERWARE)) return;

        PlayerCyberwareData data = mc.player.getData(ModAttachments.CYBERWARE);

        boolean missingEyes = !data.hasAnyTagged(ModTags.Items.EYE_ITEMS, CyberwareSlot.EYES);
        if (!missingEyes) return;

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
