package com.perigrine3.createcybernetics.client.render.rejection;

import com.mojang.logging.LogUtils;
import com.perigrine3.createcybernetics.ConfigValues;
import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.slf4j.Logger;

import java.io.IOException;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class CyberwareRejectionPostProcessor {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final ResourceLocation SCREEN_CHAIN_PATH =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "shaders/post/cyberware_rejection_screen.json");

    private static PostChain screenChain;
    private static boolean screenLoadAttempted = false;

    private static int renderWidth = -1;
    private static int renderHeight = -1;

    private CyberwareRejectionPostProcessor() {
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        renderPostEffect();
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        close();
    }

    private static void renderPostEffect() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null || mc.level == null) {
            return;
        }

        if (ConfigValues.EPILEPSY_MODE) {
            return;
        }

        float partialTick = mc.getTimer().getGameTimeDeltaPartialTick(false);
        float time = player.tickCount + partialTick;

        float intensity = CyberwareRejectionPostState.getScreenIntensity(player);
        float fugue = CyberwareRejectionPostState.getFugueIntensity(player);
        float pulse = CyberwareRejectionPostState.getForcedPulse(partialTick);
        float burst = CyberwareRejectionPostState.getBurstStrength(partialTick);

        if (intensity <= 0.0F && fugue <= 0.0F && pulse <= 0.0F && burst <= 0.0F) {
            return;
        }

        ensureSize(mc);
        renderScreenEffect(mc, time, partialTick, intensity, fugue, pulse, burst);
    }

    private static void renderScreenEffect(Minecraft mc, float time, float partialTick, float intensity, float fugue, float pulse, float burst) {
        PostChain chain = getScreenChain(mc);

        if (chain == null) {
            return;
        }

        chain.setUniform("Time", time);
        chain.setUniform("Intensity", Mth.clamp(intensity, 0.0F, 1.0F));
        chain.setUniform("Fugue", Mth.clamp(fugue, 0.0F, 1.0F));
        chain.setUniform("Pulse", Mth.clamp(pulse, 0.0F, 1.0F));
        chain.setUniform("Burst", Mth.clamp(burst, 0.0F, 1.0F));

        chain.process(partialTick);

        mc.getMainRenderTarget().bindWrite(false);
    }

    private static PostChain getScreenChain(Minecraft mc) {
        if (screenChain != null) {
            return screenChain;
        }

        if (screenLoadAttempted) {
            return null;
        }

        screenLoadAttempted = true;

        try {
            screenChain = new PostChain(mc.getTextureManager(), mc.getResourceManager(), mc.getMainRenderTarget(), SCREEN_CHAIN_PATH);
            resizeChain(screenChain, mc);
            return screenChain;
        } catch (IOException | RuntimeException exception) {
            LOGGER.error("Failed to load Cybernetics rejection screen post-processing chain", exception);
            return null;
        }
    }

    private static void ensureSize(Minecraft mc) {
        int width = mc.getWindow().getWidth();
        int height = mc.getWindow().getHeight();

        if (width == renderWidth && height == renderHeight) {
            return;
        }

        renderWidth = width;
        renderHeight = height;

        if (screenChain != null) {
            screenChain.resize(width, height);
        }
    }

    private static void resizeChain(PostChain chain, Minecraft mc) {
        int width = mc.getWindow().getWidth();
        int height = mc.getWindow().getHeight();

        chain.resize(width, height);

        renderWidth = width;
        renderHeight = height;
    }

    public static void reload() {
        closeChain();

        screenLoadAttempted = false;
        renderWidth = -1;
        renderHeight = -1;
    }

    public static void close() {
        closeChain();

        CyberwareRejectionPostState.clear();

        screenLoadAttempted = false;
        renderWidth = -1;
        renderHeight = -1;
    }

    private static void closeChain() {
        if (screenChain == null) {
            return;
        }

        screenChain.close();
        screenChain = null;
    }
}