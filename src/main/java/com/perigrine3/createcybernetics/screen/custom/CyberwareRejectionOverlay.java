package com.perigrine3.createcybernetics.screen.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.systems.RenderSystem;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.effect.ModEffects;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

import javax.annotation.Nullable;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CyberwareRejectionOverlay {

    private static final ResourceLocation LAYER_ID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "glitch_overlay");

    private static final ResourceLocation GLITCH_TEX =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID,
                    "textures/gui/glitch_effect.png");

    // -------------------- VISUAL TUNING --------------------

    private static final float MIN_ALPHA = 0.02f;
    private static final float MAX_ALPHA = 0.8f;
    private static final float FADE_SPEED = 0.35f;

    private static final float FLICKER_CHANCE_PER_SEC = 0.20f;
    private static final int FLICKER_HOLD_TICKS_MIN = 2;
    private static final int FLICKER_HOLD_TICKS_MAX = 6;

    private static final int ACTIVATE_FADE_TICKS = 30; // 1.5s

    // -------------------- FREQUENCY RAMP (BURSTS) --------------------
    // Under effect longer => render more frequently (shorter OFF, slightly longer ON)

    private static final int RAMP_SECONDS = 45; // reaches max frequency after ~45s
    private static final int RAMP_TICKS = RAMP_SECONDS * 20;

    private static final int BURST_ON_MIN_START = 6;
    private static final int BURST_ON_MAX_START = 12;
    private static final int BURST_ON_MIN_END   = 10;
    private static final int BURST_ON_MAX_END   = 20;

    private static final int BURST_OFF_MIN_START = 40;
    private static final int BURST_OFF_MAX_START = 90;
    private static final int BURST_OFF_MIN_END   = 6;
    private static final int BURST_OFF_MAX_END   = 16;

    // -------------------- ANIMATION META CACHE --------------------

    private static boolean metaLoaded = false;
    private static int frameTime = 2;
    private static List<Integer> frames = null;
    private static int texW = 0, texH = 0;
    private static int frameW = 0, frameH = 0;
    private static int frameCount = 1;

    // -------------------- RUNTIME STATE --------------------

    private static int flickerHoldTicks = 0;

    private static boolean wasActive = false;
    private static int effectStartTick = 0;

    private static int burstOnTicksLeft = 0;
    private static int burstOffTicksLeft = 0;
    private static int burstStartedAtTick = 0;
    private static int burstLengthLast = 1;

    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(LAYER_ID, CyberwareRejectionOverlay::render);
    }

    private static void render(GuiGraphics gui, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        if (mc.options.hideGui) return;

        boolean active = hasEffect(player, ModEffects.CYBERWARE_REJECTION);
        if (!active) {
            wasActive = false;
            burstOnTicksLeft = 0;
            burstOffTicksLeft = 0;
            flickerHoldTicks = 0;
            return;
        }

        int nowTick = player.tickCount;
        if (!wasActive) {
            wasActive = true;
            effectStartTick = nowTick;

            burstOnTicksLeft = 0;
            burstOffTicksLeft = 0;
            burstStartedAtTick = nowTick;
            burstLengthLast = 1;
        }

        ensureMetaLoaded(mc);
        if (frameCount <= 0 || frameH <= 0 || frameW <= 0) return;

        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();

        float pt = delta.getGameTimeDeltaPartialTick(false);
        float tTicks = player.tickCount + pt;

        // -------------------- determine burst gating --------------------
        int underTicks = nowTick - effectStartTick;
        float ramp = Mth.clamp(underTicks / (float) RAMP_TICKS, 0f, 1f);

        if (burstOnTicksLeft > 0) {
            burstOnTicksLeft--;
        } else {
            if (burstOffTicksLeft > 0) {
                burstOffTicksLeft--;
            } else {
                int onMin  = (int) Mth.lerp(ramp, BURST_ON_MIN_START,  BURST_ON_MIN_END);
                int onMax  = (int) Mth.lerp(ramp, BURST_ON_MAX_START,  BURST_ON_MAX_END);
                int offMin = (int) Mth.lerp(ramp, BURST_OFF_MIN_START, BURST_OFF_MIN_END);
                int offMax = (int) Mth.lerp(ramp, BURST_OFF_MAX_START, BURST_OFF_MAX_END);

                int onLen  = onMin  + player.getRandom().nextInt(Math.max(1, onMax - onMin + 1));
                int offLen = offMin + player.getRandom().nextInt(Math.max(1, offMax - offMin + 1));

                burstOnTicksLeft = onLen;
                burstOffTicksLeft = offLen;
                burstStartedAtTick = nowTick;
                burstLengthLast = onLen;
            }
        }

        if (burstOnTicksLeft <= 0) {
            return;
        }

        // -------------------- frame selection --------------------
        int animIndex = (int) (tTicks / Math.max(1, frameTime));
        int frameIndex;
        if (frames != null && !frames.isEmpty()) {
            frameIndex = frames.get(Math.floorMod(animIndex, frames.size()));
        } else {
            frameIndex = Math.floorMod(animIndex, frameCount);
        }

        int u = 0;
        int v = frameIndex * frameH;

        // -------------------- alpha computation --------------------

        float s = 0.5f + 0.5f * Mth.sin((float) (tTicks * 0.05f) * (FADE_SPEED * 2.0f));
        float eased = smoothstep(s);
        float alpha = Mth.lerp(eased, MIN_ALPHA, MAX_ALPHA);

        if (flickerHoldTicks > 0) {
            flickerHoldTicks--;
            alpha = MAX_ALPHA;
        } else {
            float perTick = FLICKER_CHANCE_PER_SEC / 20f;
            if (player.getRandom().nextFloat() < perTick) {
                flickerHoldTicks = FLICKER_HOLD_TICKS_MIN
                        + player.getRandom().nextInt(FLICKER_HOLD_TICKS_MAX - FLICKER_HOLD_TICKS_MIN + 1);
            }
        }

        float activateFade = smoothstep(Mth.clamp(underTicks / (float) ACTIVATE_FADE_TICKS, 0f, 1f));

        int burstAge = nowTick - burstStartedAtTick;
        float burstPhase = burstLengthLast <= 0 ? 1f : (burstAge / (float) burstLengthLast);
        burstPhase = Mth.clamp(burstPhase, 0f, 1f);

        float burstEnv = 1f - Math.abs(2f * burstPhase - 1f);
        burstEnv = smoothstep(burstEnv);

        alpha = Mth.clamp(alpha * activateFade * burstEnv, 0f, 1f);

        float maxAlphaOverTime = Mth.lerp(ramp, 0.75f, MAX_ALPHA);
        alpha = Mth.clamp(alpha, MIN_ALPHA, maxAlphaOverTime);

        // -------------------- jitter + rendering --------------------

        int jitter = 1 + player.getRandom().nextInt(3);
        int jx = player.getRandom().nextInt(jitter * 2 + 1) - jitter;
        int jy = player.getRandom().nextInt(jitter * 2 + 1) - jitter;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        gui.setColor(1f, 1f, 1f, alpha);
        blitScaled(gui, GLITCH_TEX, jx, jy, w, h, u, v, frameW, frameH, texW, texH);

        float chromaA = alpha * 0.35f;
        int split = 1 + player.getRandom().nextInt(3);

        gui.setColor(1f, 0.25f, 0.25f, chromaA);
        blitScaled(gui, GLITCH_TEX, jx + split, jy, w, h, u, v, frameW, frameH, texW, texH);

        gui.setColor(0.25f, 1f, 0.25f, chromaA);
        blitScaled(gui, GLITCH_TEX, jx - split, jy, w, h, u, v, frameW, frameH, texW, texH);

        gui.setColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }

    private static void blitScaled(GuiGraphics gui, ResourceLocation tex,
                                   int x, int y, int dstW, int dstH,
                                   int u, int v, int srcW, int srcH,
                                   int texW, int texH) {
        gui.blit(tex, x, y, dstW, dstH, u, v, srcW, srcH, texW, texH);
    }



    private static boolean hasEffect(Player player, Holder<MobEffect> effect) {
        for (MobEffectInstance inst : player.getActiveEffects()) {
            if (inst != null && inst.is(effect)) return true;
        }
        return false;
    }

    private static float smoothstep(float x) {
        x = Mth.clamp(x, 0f, 1f);
        return x * x * (3f - 2f * x);
    }

    private static void ensureMetaLoaded(Minecraft mc) {
        if (metaLoaded) return;
        metaLoaded = true;

        TextureManager tm = mc.getTextureManager();
        AbstractTexture tex = tm.getTexture(GLITCH_TEX);
        tm.bindForSetup(GLITCH_TEX);

        try {
            Resource pngRes = mc.getResourceManager().getResource(GLITCH_TEX).orElse(null);
            if (pngRes != null) {
                try (var in = pngRes.open()) {
                    var img = com.mojang.blaze3d.platform.NativeImage.read(in);
                    texW = img.getWidth();
                    texH = img.getHeight();
                    img.close();
                }
            }

            frameW = texW;
            frameH = texW;

            ResourceLocation metaLoc = ResourceLocation.fromNamespaceAndPath(
                    GLITCH_TEX.getNamespace(),
                    GLITCH_TEX.getPath() + ".mcmeta"
            );
            @Nullable Resource metaRes = mc.getResourceManager().getResource(metaLoc).orElse(null);

            if (metaRes != null) {
                try (var rdr = new InputStreamReader(metaRes.open(), StandardCharsets.UTF_8)) {
                    JsonObject root = JsonParser.parseReader(rdr).getAsJsonObject();
                    JsonObject anim = root.has("animation") ? root.getAsJsonObject("animation") : null;
                    if (anim != null) {
                        if (anim.has("frametime")) frameTime = Math.max(1, anim.get("frametime").getAsInt());
                        if (anim.has("height")) frameH = Math.max(1, anim.get("height").getAsInt());
                        if (anim.has("width")) frameW = Math.max(1, anim.get("width").getAsInt());

                        if (anim.has("frames")) {
                            JsonArray arr = anim.getAsJsonArray("frames");
                            List<Integer> tmp = new ArrayList<>();
                            for (int i = 0; i < arr.size(); i++) {
                                if (arr.get(i).isJsonPrimitive()) {
                                    tmp.add(arr.get(i).getAsInt());
                                } else if (arr.get(i).isJsonObject()
                                        && arr.get(i).getAsJsonObject().has("index")) {
                                    tmp.add(arr.get(i).getAsJsonObject().get("index").getAsInt());
                                }
                            }
                            frames = tmp.isEmpty() ? null : tmp;
                        }
                    }
                }
            }

            if (frameH <= 0) frameH = texW;
            frameCount = (frameH > 0) ? Math.max(1, texH / frameH) : 1;

        } catch (Exception e) {
            frameTime = 2;
            texW = Math.max(1, texW);
            texH = Math.max(1, texH);
            frameW = Math.max(1, frameW);
            frameH = Math.max(1, frameH);
            frameCount = 1;
            frames = null;
        }
    }

    private CyberwareRejectionOverlay() {}
}
