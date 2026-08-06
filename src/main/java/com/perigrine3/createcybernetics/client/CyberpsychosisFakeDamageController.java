package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.screen.custom.visual_overlays.CyberwareRejectionOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class CyberpsychosisFakeDamageController {

    private static final int FAKE_HURT_DURATION = 10;

    private static float accumulatedFakeDamage = 0.0F;
    private static int fakeHurtTicks = 0;

    private CyberpsychosisFakeDamageController() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof LocalPlayer)) return;

        if (fakeHurtTicks > 0) fakeHurtTicks--;
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (fakeHurtTicks <= 0) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null) return;

        float strength = Mth.clamp(fakeHurtTicks / (float) FAKE_HURT_DURATION, 0.0F, 1.0F);
        int alpha = Mth.clamp((int) (strength * 72.0F), 0, 72);
        int color = alpha << 24 | 0x00A00000;

        GuiGraphics guiGraphics = event.getGuiGraphics();

        guiGraphics.fill(0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), color);
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        clearFakeDamage();
    }

    public static void applyFakeDamage(float damage) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null || damage <= 0.0F) return;

        float maximumFakeDamage = Math.max(0.0F, player.getHealth() - 1.0F);

        accumulatedFakeDamage = Mth.clamp(accumulatedFakeDamage + damage, 0.0F, maximumFakeDamage);
        fakeHurtTicks = FAKE_HURT_DURATION;

        CyberwareRejectionOverlay.triggerForcedPulse(8);
    }

    public static float getDisplayedHealth(float realHealth) {
        if (accumulatedFakeDamage <= 0.0F) return realHealth;

        return Math.max(1.0F, realHealth - accumulatedFakeDamage);
    }

    public static int getDisplayedHealthCeil(float realHealth) {
        return Mth.ceil(getDisplayedHealth(realHealth));
    }

    public static boolean hasFakeDamage() {
        return accumulatedFakeDamage > 0.0F;
    }

    public static void clearFakeDamage() {
        accumulatedFakeDamage = 0.0F;
        fakeHurtTicks = 0;
    }
}