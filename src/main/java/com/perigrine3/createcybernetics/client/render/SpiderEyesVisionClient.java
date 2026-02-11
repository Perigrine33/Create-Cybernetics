package com.perigrine3.createcybernetics.client.render;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.effect.ModEffects;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

import java.lang.reflect.Field;

@EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class SpiderEyesVisionClient {
    private SpiderEyesVisionClient() {}

    private static final ResourceLocation SPIDER_SHADER =
            ResourceLocation.withDefaultNamespace("shaders/post/spider.json");

    private static final double MAX_FOV = 110.0D;

    private static boolean appliedByUs = false;

    private static Field postChainField = null;
    private static boolean postChainFieldSearched = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.level == null) {
            if (appliedByUs) safeDisable(mc.gameRenderer);
            return;
        }

        boolean hasImplant = hasSpiderEyesInstalled(player);
        boolean inFirstPerson = mc.options.getCameraType() == CameraType.FIRST_PERSON;

        if (appliedByUs && !isAnyPostChainActive(mc.gameRenderer)) {
            appliedByUs = false;
        }

        boolean shouldApplyNow = hasImplant && inFirstPerson;

        if (shouldApplyNow) {
            if (!appliedByUs) safeEnable(mc.gameRenderer);
        } else {
            if (appliedByUs) safeDisable(mc.gameRenderer);
        }
    }

    @SubscribeEvent
    public static void onComputeFov(ViewportEvent.ComputeFov event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        if (mc.options.getCameraType() == CameraType.FIRST_PERSON && hasSpiderEyesInstalled(player)) {
            event.setFOV(MAX_FOV);
        }
    }

    private static boolean hasSpiderEyesInstalled(Player player) {
        return player.hasEffect(ModEffects.SPIDER_EYES_EFFECT);
    }

    private static void safeEnable(GameRenderer renderer) {
        try {
            renderer.loadEffect(SPIDER_SHADER);
            appliedByUs = true;
        } catch (Throwable t) {
            appliedByUs = false;
        }
    }

    private static void safeDisable(GameRenderer renderer) {
        try {
            renderer.shutdownEffect();
        } finally {
            appliedByUs = false;
        }
    }

    private static boolean isAnyPostChainActive(GameRenderer renderer) {
        PostChain chain = getPostChain(renderer);
        return chain != null;
    }

    private static PostChain getPostChain(GameRenderer renderer) {
        try {
            Field f = findPostChainField();
            if (f == null) return null;
            Object v = f.get(renderer);
            return (v instanceof PostChain pc) ? pc : null;
        } catch (Throwable t) {
            return null;
        }
    }

    private static Field findPostChainField() {
        if (postChainFieldSearched) return postChainField;
        postChainFieldSearched = true;

        for (Field f : GameRenderer.class.getDeclaredFields()) {
            if (f.getType() == PostChain.class) {
                f.setAccessible(true);
                postChainField = f;
                break;
            }
        }
        return postChainField;
    }
}
