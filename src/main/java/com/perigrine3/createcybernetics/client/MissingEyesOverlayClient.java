package com.perigrine3.createcybernetics.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.cyberware.CybereyeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class MissingEyesOverlayClient {

    private MissingEyesOverlayClient() {}

    private static final int ALPHA = 0xFF;

    @SubscribeEvent
    public static void onRenderGuiPre(RenderGuiEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.screen != null) return;

        PlayerCyberwareData data = mc.player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        // Only show this overlay if the player actually has cybereyes installed+enabled
        if (!hasCybereyesInstalledAndEnabled(data)) return;

        // Effects are synced to client; this is reliable.
        boolean blinded = mc.player.hasEffect(MobEffects.BLINDNESS);
        boolean darkened = mc.player.hasEffect(MobEffects.DARKNESS);
        if (!blinded && !darkened) return;

        GuiGraphics gg = event.getGuiGraphics();
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int argb = (ALPHA << 24) | 0x000000;
        gg.fill(0, 0, w, h, argb);

        RenderSystem.disableBlend();
    }

    private static boolean hasCybereyesInstalledAndEnabled(PlayerCyberwareData data) {
        InstalledCyberware[] arr = data.getAll().get(CyberwareSlot.EYES);
        if (arr == null) return false;

        for (int idx = 0; idx < arr.length; idx++) {
            InstalledCyberware cw = arr[idx];
            if (cw == null) continue;

            ItemStack st = cw.getItem();
            if (st == null || st.isEmpty()) continue;

            if (!(st.getItem() instanceof CybereyeItem)) continue;
            if (!data.isEnabled(CyberwareSlot.EYES, idx)) continue;

            return true;
        }
        return false;
    }
}
