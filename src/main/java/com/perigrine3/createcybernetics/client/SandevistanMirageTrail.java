package com.perigrine3.createcybernetics.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.effect.ModEffects;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT)
public final class SandevistanMirageTrail {

    private SandevistanMirageTrail() {}

    private static final int RENDER_SNAPSHOTS = 241;
    private static final int TRAIL_LIFETIME_TICKS = 12 * 20;
    private static final float MIRAGE_MODEL_SCALE = 0.9375F;

    private static final double RENDER_DELAY_TICKS = 0.6725;
    private static final double SAMPLES_PER_SECOND = 60.0;
    private static final double SAMPLE_PERIOD_TICKS = 20.0 / SAMPLES_PER_SECOND;

    private static final float MIN_CAMERA_DIST = 0.55F;
    private static final float MIN_CAMERA_DIST_SQR = MIN_CAMERA_DIST * MIN_CAMERA_DIST;

    private static final int MAX_SNAPSHOTS = Math.max(
            (int) Math.ceil(TRAIL_LIFETIME_TICKS / SAMPLE_PERIOD_TICKS) + 2,
            RENDER_SNAPSHOTS + (int) Math.ceil(RENDER_DELAY_TICKS / SAMPLE_PERIOD_TICKS) + 2
    );

    private static final Map<UUID, Deque<Snapshot>> TRAILS = new HashMap<>();

    private static double lastFrameTimeTicks = Double.NaN;
    private static double accumulatorTicks = 0.0;

    private static final class Snapshot {
        final Vec3 pos;
        final float bodyYaw;
        final float headYaw;
        final float pitch;
        final boolean crouching;

        final float limbSwing;
        final float limbSwingAmount;
        final float ageInTicks;

        int ageTicks;

        Snapshot(Vec3 pos, float bodyYaw, float headYaw, float pitch, boolean crouching,
                 float limbSwing, float limbSwingAmount, float ageInTicks) {
            this.pos = pos;
            this.bodyYaw = bodyYaw;
            this.headYaw = headYaw;
            this.pitch = pitch;
            this.crouching = crouching;
            this.limbSwing = limbSwing;
            this.limbSwingAmount = limbSwingAmount;
            this.ageInTicks = ageInTicks;
            this.ageTicks = 0;
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        for (Iterator<Map.Entry<UUID, Deque<Snapshot>>> it = TRAILS.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<UUID, Deque<Snapshot>> e = it.next();
            Deque<Snapshot> q = e.getValue();
            if (q == null || q.isEmpty()) {
                it.remove();
                continue;
            }
            for (Snapshot s : q) s.ageTicks++;
            while (!q.isEmpty() && q.peekFirst().ageTicks > TRAIL_LIFETIME_TICKS) q.pollFirst();
            if (q.isEmpty()) it.remove();
        }
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        PoseStack poseStack = event.getPoseStack();
        if (poseStack == null) return;

        Vec3 camPos = event.getCamera().getPosition();
        float partial = event.getPartialTick().getGameTimeDeltaPartialTick(true);

        recordSubTickSnapshots(mc, partial);

        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();

        for (var p : mc.level.players()) {
            if (!(p instanceof AbstractClientPlayer player)) continue;

            UUID id = player.getUUID();
            Deque<Snapshot> q = TRAILS.get(id);
            if (q == null || q.isEmpty()) continue;

            EntityRenderer<? super AbstractClientPlayer> er = mc.getEntityRenderDispatcher().getRenderer(player);
            if (!(er instanceof PlayerRenderer playerRenderer)) continue;

            renderTrailForPlayer(poseStack, buffer, playerRenderer, player, q, camPos, partial);
        }

        buffer.endBatch();
    }

    private static void recordSubTickSnapshots(Minecraft mc, float currentPartial) {
        if (mc.level == null) return;

        double nowTimeTicks = (double) mc.level.getGameTime() + (double) currentPartial;

        if (Double.isNaN(lastFrameTimeTicks)) {
            lastFrameTimeTicks = nowTimeTicks;
            accumulatorTicks = 0.0;
            return;
        }

        double deltaTicks = nowTimeTicks - lastFrameTimeTicks;
        if (deltaTicks < 0.0) deltaTicks = 0.0;
        if (deltaTicks > 5.0) deltaTicks = 5.0;

        lastFrameTimeTicks = nowTimeTicks;
        accumulatorTicks += deltaTicks;

        while (accumulatorTicks >= SAMPLE_PERIOD_TICKS) {
            accumulatorTicks -= SAMPLE_PERIOD_TICKS;

            double sampleTimeTicks = nowTimeTicks - accumulatorTicks;

            float samplePartial = (float) (sampleTimeTicks - Math.floor(sampleTimeTicks));
            samplePartial = Mth.clamp(samplePartial, 0.0F, 1.0F);

            for (var p : mc.level.players()) {
                if (!(p instanceof AbstractClientPlayer player)) continue;

                boolean active = player.hasEffect(ModEffects.SANDEVISTAN_EFFECT) && player.isSprinting();
                UUID id = player.getUUID();

                if (!active) {
                    TRAILS.remove(id);
                    continue;
                }

                double x = Mth.lerp(samplePartial, player.xo, player.getX());
                double y = Mth.lerp(samplePartial, player.yo, player.getY());
                double z = Mth.lerp(samplePartial, player.zo, player.getZ());

                float bodyYaw = rotLerp(samplePartial, player.yRotO, player.getYRot());
                float headYaw = rotLerp(samplePartial, player.yHeadRotO, player.getYHeadRot());
                float pitch = rotLerp(samplePartial, player.xRotO, player.getXRot());

                float limbSwing = safeWalkPosition(player, samplePartial);
                float limbSwingAmount = safeWalkSpeed(player, samplePartial);

                Snapshot snap = new Snapshot(
                        new Vec3(x, y, z),
                        bodyYaw,
                        headYaw,
                        pitch,
                        player.isCrouching(),
                        limbSwing,
                        limbSwingAmount,
                        (float) sampleTimeTicks
                );

                Deque<Snapshot> q = TRAILS.computeIfAbsent(id, k -> new ArrayDeque<>());
                q.addLast(snap);
                while (q.size() > MAX_SNAPSHOTS) q.pollFirst();
            }
        }
    }

    private static void renderTrailForPlayer(
            PoseStack poseStack,
            MultiBufferSource buffer,
            PlayerRenderer playerRenderer,
            AbstractClientPlayer player,
            Deque<Snapshot> q,
            Vec3 camPos,
            float partial
    ) {
        PlayerModel<AbstractClientPlayer> model = playerRenderer.getModel();
        Minecraft mc = Minecraft.getInstance();
        boolean isLocalFirstPerson =
                (mc.player != null && mc.player.getUUID().equals(player.getUUID()))
                        && mc.options.getCameraType() == CameraType.FIRST_PERSON;

        var prevRightPose = model.rightArmPose;
        var prevLeftPose = model.leftArmPose;
        boolean prevCrouching = model.crouching;
        float prevSwimAmount = model.swimAmount;
        float prevAttackTime = model.attackTime;

        boolean prevHat = model.hat.visible;
        boolean prevJacket = model.jacket.visible;
        boolean prevLeftSleeve = model.leftSleeve.visible;
        boolean prevRightSleeve = model.rightSleeve.visible;
        boolean prevLeftPants = model.leftPants.visible;
        boolean prevRightPants = model.rightPants.visible;
        boolean prevYoung = model.young;

        try {
            model.young = false;
            model.hat.visible = true;
            model.jacket.visible = true;
            model.leftSleeve.visible = true;
            model.rightSleeve.visible = true;
            model.leftPants.visible = true;
            model.rightPants.visible = true;

            Snapshot[] arr = q.toArray(new Snapshot[0]);

            int delaySamples = (int) Math.ceil(RENDER_DELAY_TICKS / SAMPLE_PERIOD_TICKS);

            int endExclusive = Math.max(0, arr.length - delaySamples);
            if (endExclusive <= 0) return;

            int start = Math.max(0, endExclusive - RENDER_SNAPSHOTS);
            int renderCount = endExclusive - start;
            if (renderCount <= 0) return;

            for (int i = start; i < endExclusive; i++) {
                Snapshot s = arr[i];

                float t = s.ageTicks / (float) TRAIL_LIFETIME_TICKS;
                if (t < 0.0F || t > 1.0F) continue;

                float alpha = (1.0F - t);
                alpha *= alpha;

                int aBody = (int) (alpha * 170.0F);
                if (aBody <= 0) continue;

                float indexNorm = (i - start) / (float) Math.max(1, (renderCount - 1));
                float baseHue = ((player.tickCount + partial) * 0.01F) % 1.0F;
                float hue = (baseHue + indexNorm) % 1.0F;

                int rgb = Mth.hsvToRgb(hue, 0.90F, 1.0F);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                Vec3 renderPos = s.pos;
                if (isLocalFirstPerson) {
                    Vec3 toCam = renderPos.subtract(camPos);
                    if (toCam.lengthSqr() < MIN_CAMERA_DIST_SQR) continue;
                }

                model.attackTime = 0.0F;
                model.crouching = s.crouching;
                model.swimAmount = 0.0F;
                model.rightArmPose = HumanoidModel.ArmPose.EMPTY;
                model.leftArmPose = HumanoidModel.ArmPose.EMPTY;

                float netHeadYaw = wrapDegrees(s.headYaw - s.bodyYaw);
                model.setupAnim(player, s.limbSwing, s.limbSwingAmount, s.ageInTicks, netHeadYaw, s.pitch);

                double dx = renderPos.x - camPos.x;
                double dy = renderPos.y - camPos.y;
                double dz = renderPos.z - camPos.z;

                poseStack.pushPose();
                try {
                    poseStack.translate(dx, dy, dz);
                    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - s.bodyYaw));
                    poseStack.scale(-1.0F, -1.0F, 1.0F);
                    poseStack.scale(MIRAGE_MODEL_SCALE, MIRAGE_MODEL_SCALE, MIRAGE_MODEL_SCALE);
                    poseStack.translate(0.0F, -1.501F / MIRAGE_MODEL_SCALE, 0.0F);


                    int packedLight = LevelRenderer.getLightColor(
                            player.level(),
                            BlockPos.containing(renderPos.x, renderPos.y, renderPos.z)
                    );

                    int bodyColor = FastColor.ARGB32.color(aBody, r, g, b);
                    ResourceLocation skin = player.getSkin().texture();

                    var vcBody = buffer.getBuffer(RenderType.entityTranslucent(skin));
                    model.renderToBuffer(poseStack, vcBody, packedLight, OverlayTexture.NO_OVERLAY, bodyColor);
                } finally {
                    poseStack.popPose();
                }
            }
        } finally {
            model.rightArmPose = prevRightPose;
            model.leftArmPose = prevLeftPose;
            model.crouching = prevCrouching;
            model.swimAmount = prevSwimAmount;
            model.attackTime = prevAttackTime;

            model.hat.visible = prevHat;
            model.jacket.visible = prevJacket;
            model.leftSleeve.visible = prevLeftSleeve;
            model.rightSleeve.visible = prevRightSleeve;
            model.leftPants.visible = prevLeftPants;
            model.rightPants.visible = prevRightPants;
            model.young = prevYoung;
        }
    }

    private static float safeWalkPosition(AbstractClientPlayer player, float partial) {
        try {
            Object wa = player.walkAnimation;
            Method m = wa.getClass().getMethod("position", float.class);
            Object r = m.invoke(wa, partial);
            return (r instanceof Float f) ? f : 0.0F;
        } catch (Throwable ignored1) {
            try {
                Object wa = player.walkAnimation;
                Method m = wa.getClass().getMethod("position");
                Object r = m.invoke(wa);
                return (r instanceof Float f) ? f : 0.0F;
            } catch (Throwable ignored2) {
                return 0.0F;
            }
        }
    }

    private static float safeWalkSpeed(AbstractClientPlayer player, float partial) {
        try {
            Object wa = player.walkAnimation;
            Method m = wa.getClass().getMethod("speed", float.class);
            Object r = m.invoke(wa, partial);
            return (r instanceof Float f) ? f : 0.0F;
        } catch (Throwable ignored1) {
            try {
                Object wa = player.walkAnimation;
                Method m = wa.getClass().getMethod("speed");
                Object r = m.invoke(wa);
                return (r instanceof Float f) ? f : 0.0F;
            } catch (Throwable ignored2) {
                return 0.0F;
            }
        }
    }

    private static float wrapDegrees(float deg) {
        deg = deg % 360.0F;
        if (deg >= 180.0F) deg -= 360.0F;
        if (deg < -180.0F) deg += 360.0F;
        return deg;
    }

    private static float rotLerp(float t, float a, float b) {
        float delta = wrapDegrees(b - a);
        return a + delta * t;
    }
}
