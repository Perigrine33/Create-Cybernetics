package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.client.render.CyberpsychosisShadowRenderer;
import com.perigrine3.createcybernetics.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class CyberpsychosisPlayerShadowController {

    private static final float SHADOW_ALPHA_LEVEL_2 = 0.86F;
    private static final float SHADOW_ALPHA_LEVEL_3 = 0.96F;
    private static final float SHADOW_ALPHA_FUGUE = 1.0F;

    private static final int LEVEL_2_MIN_ACTIVE_TICKS = 20 * 2;
    private static final int LEVEL_2_MAX_ACTIVE_TICKS = 20 * 5;
    private static final int LEVEL_2_MIN_CALM_TICKS = 20 * 12;
    private static final int LEVEL_2_MAX_CALM_TICKS = 20 * 30;

    private static final int LEVEL_3_MIN_ACTIVE_TICKS = 20 * 8;
    private static final int LEVEL_3_MAX_ACTIVE_TICKS = 20 * 18;
    private static final int LEVEL_3_MIN_CALM_TICKS = 5;
    private static final int LEVEL_3_MAX_CALM_TICKS = 20 * 2;

    private static int activeTicksRemaining = 0;
    private static int calmTicksRemaining = 0;
    private static int previousRejectionLevel = 0;
    private static boolean shadowEpisodeActive = false;

    private CyberpsychosisPlayerShadowController() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        Minecraft mc = Minecraft.getInstance();

        if (mc.isPaused() || mc.level == null) return;

        MobEffectInstance rejection = player.getEffect(ModEffects.CYBERWARE_REJECTION);
        int rejectionLevel = rejection == null ? 0 : Mth.clamp(rejection.getAmplifier() + 1, 1, 3);

        if (rejectionLevel < 2) {
            clear();
            previousRejectionLevel = rejectionLevel;
            return;
        }

        if (previousRejectionLevel != rejectionLevel) {
            resetForLevel(player, rejectionLevel);
            previousRejectionLevel = rejectionLevel;
        }

        if (player.hasEffect(ModEffects.CYBERPSYCHOSIS_FUGUE)) {
            shadowEpisodeActive = true;
            activeTicksRemaining = 0;
            calmTicksRemaining = 0;
            return;
        }

        tickEpisode(player, rejectionLevel);
    }

    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer localPlayer = mc.player;

        if (localPlayer == null) return;
        if (!(event.getEntity() instanceof AbstractClientPlayer renderedPlayer)) return;
        if (renderedPlayer == localPlayer) return;
        if (!shouldRenderPlayersAsShadows(localPlayer)) return;

        event.setCanceled(true);

        float partialTick = event.getPartialTick();
        float bodyYaw = Mth.rotLerp(partialTick, renderedPlayer.yBodyRotO, renderedPlayer.yBodyRot);
        float headYaw = Mth.rotLerp(partialTick, renderedPlayer.yHeadRotO, renderedPlayer.yHeadRot);
        float headPitch = Mth.lerp(partialTick, renderedPlayer.xRotO, renderedPlayer.getXRot());
        float alpha = getShadowAlpha(localPlayer);

        PlayerModel<AbstractClientPlayer> vanillaModel = event.getRenderer().getModel();

        CyberpsychosisShadowRenderer.renderPlayerShadow(event.getPoseStack(), event.getMultiBufferSource(), renderedPlayer, vanillaModel, bodyYaw, headYaw, headPitch, partialTick, event.getPackedLight(), alpha);
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        clear();
        previousRejectionLevel = 0;
    }

    private static void tickEpisode(LocalPlayer player, int rejectionLevel) {
        if (shadowEpisodeActive) {
            if (activeTicksRemaining > 0) {
                activeTicksRemaining--;
            }

            if (activeTicksRemaining <= 0) {
                shadowEpisodeActive = false;
                scheduleCalmPeriod(player.getRandom(), rejectionLevel);
            }

            return;
        }

        if (calmTicksRemaining > 0) {
            calmTicksRemaining--;
        }

        if (calmTicksRemaining <= 0) {
            shadowEpisodeActive = true;
            scheduleActivePeriod(player.getRandom(), rejectionLevel);
        }
    }

    private static void resetForLevel(LocalPlayer player, int rejectionLevel) {
        activeTicksRemaining = 0;
        calmTicksRemaining = 0;
        shadowEpisodeActive = false;

        if (rejectionLevel == 2) {
            scheduleCalmPeriod(player.getRandom(), rejectionLevel);
            return;
        }

        shadowEpisodeActive = true;
        scheduleActivePeriod(player.getRandom(), rejectionLevel);
    }

    private static void scheduleActivePeriod(RandomSource random, int rejectionLevel) {
        activeTicksRemaining = switch (rejectionLevel) {
            case 2 -> Mth.nextInt(random, LEVEL_2_MIN_ACTIVE_TICKS, LEVEL_2_MAX_ACTIVE_TICKS);
            default -> Mth.nextInt(random, LEVEL_3_MIN_ACTIVE_TICKS, LEVEL_3_MAX_ACTIVE_TICKS);
        };
    }

    private static void scheduleCalmPeriod(RandomSource random, int rejectionLevel) {
        calmTicksRemaining = switch (rejectionLevel) {
            case 2 -> Mth.nextInt(random, LEVEL_2_MIN_CALM_TICKS, LEVEL_2_MAX_CALM_TICKS);
            default -> Mth.nextInt(random, LEVEL_3_MIN_CALM_TICKS, LEVEL_3_MAX_CALM_TICKS);
        };
    }

    private static boolean shouldRenderPlayersAsShadows(LocalPlayer localPlayer) {
        return shouldAppearAsShadowToLocalPlayer();
    }

    public static boolean shouldAppearAsShadowToLocalPlayer() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null) return false;
        if (player.hasEffect(ModEffects.CYBERPSYCHOSIS_FUGUE)) return true;

        MobEffectInstance rejection = player.getEffect(ModEffects.CYBERWARE_REJECTION);
        if (rejection == null) return false;

        int rejectionLevel = Mth.clamp(rejection.getAmplifier() + 1, 1, 3);

        return rejectionLevel >= 2 && shadowEpisodeActive;
    }

    private static float getShadowAlpha(LocalPlayer localPlayer) {
        if (localPlayer.hasEffect(ModEffects.CYBERPSYCHOSIS_FUGUE)) return SHADOW_ALPHA_FUGUE;

        MobEffectInstance rejection = localPlayer.getEffect(ModEffects.CYBERWARE_REJECTION);
        if (rejection == null) return 0.0F;

        int rejectionLevel = Mth.clamp(rejection.getAmplifier() + 1, 1, 3);

        return rejectionLevel >= 3 ? SHADOW_ALPHA_LEVEL_3 : SHADOW_ALPHA_LEVEL_2;
    }

    private static void clear() {
        activeTicksRemaining = 0;
        calmTicksRemaining = 0;
        shadowEpisodeActive = false;
    }
}