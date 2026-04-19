package com.perigrine3.createcybernetics.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.ISandevistanTrailState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT)
public final class CyberentitySandevistanMirageTrail {

    private CyberentitySandevistanMirageTrail() {}

    private static final int RENDER_SNAPSHOTS = 160;
    private static final int TRAIL_LIFETIME_TICKS = 4 * 20;
    private static final int MAX_BODY_ALPHA = 130;
    private static final float MIRAGE_MODEL_SCALE = 0.9375F;
    private static final int RENDER_DELAY_SNAPSHOTS = 2;
    private static final double TRACK_RANGE = 128.0D;

    private static final int MAX_SNAPSHOTS = Math.max(
            TRAIL_LIFETIME_TICKS + 8,
            RENDER_SNAPSHOTS + RENDER_DELAY_SNAPSHOTS + 8
    );

    private static final Map<Integer, Deque<Snapshot>> TRAILS = new LinkedHashMap<>();

    private static final class Snapshot {
        final Vec3 pos;
        final float bodyYawDeg;
        final ResourceLocation texture;
        final Map<String, PartState> parts;
        int ageTicks;

        Snapshot(Vec3 pos, float bodyYawDeg, ResourceLocation texture, Map<String, PartState> parts) {
            this.pos = pos;
            this.bodyYawDeg = bodyYawDeg;
            this.texture = texture;
            this.parts = parts;
            this.ageTicks = 0;
        }
    }

    private static final class PartState {
        final float x;
        final float y;
        final float z;
        final float xRot;
        final float yRot;
        final float zRot;
        final float xScale;
        final float yScale;
        final float zScale;
        final boolean visible;
        final boolean skipDraw;

        PartState(ModelPart part) {
            this.x = part.x;
            this.y = part.y;
            this.z = part.z;
            this.xRot = part.xRot;
            this.yRot = part.yRot;
            this.zRot = part.zRot;
            this.xScale = part.xScale;
            this.yScale = part.yScale;
            this.zScale = part.zScale;
            this.visible = part.visible;
            this.skipDraw = part.skipDraw;
        }

        void apply(ModelPart part) {
            part.x = this.x;
            part.y = this.y;
            part.z = this.z;
            part.xRot = this.xRot;
            part.yRot = this.yRot;
            part.zRot = this.zRot;
            part.xScale = this.xScale;
            part.yScale = this.yScale;
            part.zScale = this.zScale;
            part.visible = this.visible;
            part.skipDraw = this.skipDraw;
        }
    }

    private static final class ModelBackup {
        final Map<String, ModelPart> parts;
        final Map<String, PartState> states = new LinkedHashMap<>();

        ModelBackup(Map<String, ModelPart> parts) {
            this.parts = parts;
            for (Map.Entry<String, ModelPart> entry : parts.entrySet()) {
                states.put(entry.getKey(), new PartState(entry.getValue()));
            }
        }

        void restore() {
            for (Map.Entry<String, PartState> entry : states.entrySet()) {
                ModelPart part = parts.get(entry.getKey());
                if (part != null) {
                    entry.getValue().apply(part);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            TRAILS.clear();
            return;
        }

        ageAndPruneTrails(mc);

        AABB box = mc.player.getBoundingBox().inflate(TRACK_RANGE);

        for (LivingEntity entity : mc.level.getEntitiesOfClass(LivingEntity.class, box)) {
            if (!shouldTrail(entity)) continue;

            Snapshot snapshot = captureSnapshot(entity);
            if (snapshot == null) continue;

            Deque<Snapshot> queue = TRAILS.computeIfAbsent(entity.getId(), id -> new ArrayDeque<>());
            queue.addLast(snapshot);

            while (queue.size() > MAX_SNAPSHOTS) {
                queue.pollFirst();
            }
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
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();

        for (Map.Entry<Integer, Deque<Snapshot>> entry : TRAILS.entrySet()) {
            Entity entity = mc.level.getEntity(entry.getKey());
            if (!(entity instanceof LivingEntity living)) continue;

            Deque<Snapshot> queue = entry.getValue();
            if (queue == null || queue.isEmpty()) continue;

            renderTrailForEntity(poseStack, buffer, living, queue, camPos, partial);
        }

        buffer.endBatch();
    }

    private static void ageAndPruneTrails(Minecraft mc) {
        for (Iterator<Map.Entry<Integer, Deque<Snapshot>>> it = TRAILS.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, Deque<Snapshot>> entry = it.next();
            Entity entity = mc.level.getEntity(entry.getKey());
            Deque<Snapshot> queue = entry.getValue();

            if (!(entity instanceof LivingEntity) || queue == null || queue.isEmpty()) {
                it.remove();
                continue;
            }

            for (Snapshot snapshot : queue) {
                snapshot.ageTicks++;
            }

            while (!queue.isEmpty() && queue.peekFirst().ageTicks > TRAIL_LIFETIME_TICKS) {
                queue.pollFirst();
            }

            if (queue.isEmpty()) {
                it.remove();
            }
        }
    }

    private static boolean shouldTrail(LivingEntity entity) {
        if (!entity.isAlive()) return false;
        if (entity instanceof Player) return false;
        return entity instanceof ISandevistanTrailState trailState
                && trailState.createcybernetics$isSandevistanTrailActive();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Snapshot captureSnapshot(LivingEntity entity) {
        Minecraft mc = Minecraft.getInstance();
        EntityRenderer<? super LivingEntity> rawRenderer =
                (EntityRenderer<? super LivingEntity>) mc.getEntityRenderDispatcher().getRenderer(entity);

        if (!(rawRenderer instanceof LivingEntityRenderer livingRenderer)) return null;

        EntityModel model = livingRenderer.getModel();
        if (model == null) return null;

        ResourceLocation texture = livingRenderer.getTextureLocation(entity);
        if (texture == null) return null;

        Map<String, ModelPart> parts = discoverModelParts(model);
        if (parts.isEmpty()) return null;

        ModelBackup backup = new ModelBackup(parts);

        try {
            prepareModelForSnapshot(entity, model);

            float limbSwing = safeWalkPosition(entity, 1.0F);
            float limbSwingAmount = safeWalkSpeed(entity, 1.0F);
            float bodyYaw = entity.yBodyRot;
            float headYaw = entity.getYHeadRot();
            float pitch = entity.getXRot();
            float netHeadYaw = wrapDegrees(headYaw - bodyYaw);
            float ageInTicks = entity.tickCount;

            model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, pitch);

            Map<String, PartState> captured = new LinkedHashMap<>();
            for (Map.Entry<String, ModelPart> entry : parts.entrySet()) {
                captured.put(entry.getKey(), new PartState(entry.getValue()));
            }

            return new Snapshot(entity.position(), bodyYaw, texture, captured);
        } finally {
            backup.restore();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void renderTrailForEntity(
            PoseStack poseStack,
            MultiBufferSource buffer,
            LivingEntity entity,
            Deque<Snapshot> queue,
            Vec3 camPos,
            float partial
    ) {
        Minecraft mc = Minecraft.getInstance();
        EntityRenderer<? super LivingEntity> rawRenderer =
                (EntityRenderer<? super LivingEntity>) mc.getEntityRenderDispatcher().getRenderer(entity);

        if (!(rawRenderer instanceof LivingEntityRenderer livingRenderer)) return;

        EntityModel model = livingRenderer.getModel();
        if (model == null) return;

        Map<String, ModelPart> liveParts = discoverModelParts(model);
        if (liveParts.isEmpty()) return;

        ModelBackup backup = new ModelBackup(liveParts);

        try {
            Snapshot[] arr = queue.toArray(new Snapshot[0]);

            int endExclusive = Math.max(0, arr.length - RENDER_DELAY_SNAPSHOTS);
            if (endExclusive <= 0) return;

            int start = Math.max(0, endExclusive - RENDER_SNAPSHOTS);
            int renderCount = endExclusive - start;
            if (renderCount <= 0) return;

            for (int i = start; i < endExclusive; i++) {
                Snapshot snapshot = arr[i];

                float t = snapshot.ageTicks / (float) TRAIL_LIFETIME_TICKS;
                if (t < 0.0F || t > 1.0F) continue;

                float alpha = 1.0F - t;
                alpha *= alpha;

                int aBody = (int) (alpha * (float) MAX_BODY_ALPHA);
                if (aBody <= 0) continue;

                float indexNorm = (i - start) / (float) Math.max(1, renderCount - 1);
                float baseHue = ((entity.tickCount + partial) * 0.01F) % 1.0F;
                float hue = (baseHue + indexNorm) % 1.0F;

                int rgb = Mth.hsvToRgb(hue, 0.90F, 1.0F);
                int hueR = (rgb >> 16) & 0xFF;
                int hueG = (rgb >> 8) & 0xFF;
                int hueB = rgb & 0xFF;

                applyCapturedPartState(liveParts, snapshot.parts);

                double dx = snapshot.pos.x - camPos.x;
                double dy = snapshot.pos.y - camPos.y;
                double dz = snapshot.pos.z - camPos.z;

                poseStack.pushPose();
                try {
                    poseStack.translate(dx, dy, dz);
                    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - snapshot.bodyYawDeg));
                    poseStack.scale(-1.0F, -1.0F, 1.0F);
                    poseStack.scale(MIRAGE_MODEL_SCALE, MIRAGE_MODEL_SCALE, MIRAGE_MODEL_SCALE);
                    poseStack.translate(0.0F, -1.501F / MIRAGE_MODEL_SCALE, 0.0F);

                    int packedLight = LevelRenderer.getLightColor(
                            entity.level(),
                            BlockPos.containing(snapshot.pos.x, snapshot.pos.y, snapshot.pos.z)
                    );

                    int bodyColor = FastColor.ARGB32.color(aBody, hueR, hueG, hueB);
                    var vcBody = buffer.getBuffer(RenderType.entityTranslucent(snapshot.texture));
                    model.renderToBuffer(poseStack, vcBody, packedLight, OverlayTexture.NO_OVERLAY, bodyColor);
                } finally {
                    poseStack.popPose();
                }
            }
        } finally {
            backup.restore();
        }
    }

    private static void applyCapturedPartState(Map<String, ModelPart> liveParts, Map<String, PartState> captured) {
        for (Map.Entry<String, PartState> entry : captured.entrySet()) {
            ModelPart part = liveParts.get(entry.getKey());
            if (part != null) {
                entry.getValue().apply(part);
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void prepareModelForSnapshot(LivingEntity entity, EntityModel model) {
        if (model instanceof HumanoidModel humanoid) {
            humanoid.attackTime = 0.0F;
            humanoid.crouching = entity.isCrouching();
            humanoid.young = entity.isBaby();
            humanoid.swimAmount = 0.0F;
            humanoid.rightArmPose = HumanoidModel.ArmPose.EMPTY;
            humanoid.leftArmPose = HumanoidModel.ArmPose.EMPTY;
            return;
        }

        trySetBoolean(model, "crouching", entity.isCrouching());
        trySetBoolean(model, "young", entity.isBaby());
        trySetFloat(model, "attackTime", 0.0F);
        trySetFloat(model, "swimAmount", 0.0F);
    }

    private static Map<String, ModelPart> discoverModelParts(EntityModel<?> model) {
        Map<String, ModelPart> out = new LinkedHashMap<>();
        IdentityHashMap<ModelPart, Boolean> visited = new IdentityHashMap<>();

        if (model instanceof HierarchicalModel<?> hierarchical) {
            try {
                ModelPart root = hierarchical.root();
                if (root != null) {
                    walkModelPart("root", root, out, visited);
                }
            } catch (Throwable ignored) {
            }
        }

        collectModelPartFields("model", model, model.getClass(), out, visited);
        return out;
    }

    private static void collectModelPartFields(
            String prefix,
            Object obj,
            Class<?> cls,
            Map<String, ModelPart> out,
            IdentityHashMap<ModelPart, Boolean> visited
    ) {
        if (obj == null || cls == null || cls == Object.class) return;

        collectModelPartFields(prefix, obj, cls.getSuperclass(), out, visited);

        for (Field field : cls.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(obj);
                if (value instanceof ModelPart part) {
                    walkModelPart(prefix + "." + field.getName(), part, out, visited);
                }
            } catch (Throwable ignored) {
            }
        }
    }

    private static void walkModelPart(
            String path,
            ModelPart part,
            Map<String, ModelPart> out,
            IdentityHashMap<ModelPart, Boolean> visited
    ) {
        if (part == null || visited.containsKey(part)) return;

        visited.put(part, Boolean.TRUE);
        out.put(path, part);

        for (Map.Entry<String, ModelPart> child : getChildren(part).entrySet()) {
            walkModelPart(path + "/" + child.getKey(), child.getValue(), out, visited);
        }
    }

    private static Map<String, ModelPart> getChildren(ModelPart part) {
        try {
            Field field = ModelPart.class.getDeclaredField("children");
            field.setAccessible(true);
            Object value = field.get(part);
            if (value instanceof Map<?, ?> raw) {
                Map<String, ModelPart> out = new LinkedHashMap<>();
                for (Map.Entry<?, ?> entry : raw.entrySet()) {
                    if (entry.getKey() instanceof String key && entry.getValue() instanceof ModelPart child) {
                        out.put(key, child);
                    }
                }
                return out;
            }
        } catch (Throwable ignored) {
        }
        return Map.of();
    }

    private static void trySetBoolean(Object obj, String fieldName, boolean value) {
        try {
            Field field = findField(obj.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                field.setBoolean(obj, value);
            }
        } catch (Throwable ignored) {
        }
    }

    private static void trySetFloat(Object obj, String fieldName, float value) {
        try {
            Field field = findField(obj.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                field.setFloat(obj, value);
            }
        } catch (Throwable ignored) {
        }
    }

    private static Field findField(Class<?> cls, String name) {
        Class<?> current = cls;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    private static float safeWalkPosition(LivingEntity entity, float partial) {
        try {
            Object wa = entity.walkAnimation;
            Method m = wa.getClass().getMethod("position", float.class);
            Object r = m.invoke(wa, partial);
            return (r instanceof Float f) ? f : 0.0F;
        } catch (Throwable ignored1) {
            try {
                Object wa = entity.walkAnimation;
                Method m = wa.getClass().getMethod("position");
                Object r = m.invoke(wa);
                return (r instanceof Float f) ? f : 0.0F;
            } catch (Throwable ignored2) {
                return 0.0F;
            }
        }
    }

    private static float safeWalkSpeed(LivingEntity entity, float partial) {
        try {
            Object wa = entity.walkAnimation;
            Method m = wa.getClass().getMethod("speed", float.class);
            Object r = m.invoke(wa, partial);
            return (r instanceof Float f) ? f : 0.0F;
        } catch (Throwable ignored1) {
            try {
                Object wa = entity.walkAnimation;
                Method m = wa.getClass().getMethod("speed");
                Object r = m.invoke(wa);
                return (r instanceof Float f) ? f : 0.0F;
            } catch (Throwable ignored2) {
                return 0.0F;
            }
        }
    }

    private static float wrapDegrees(float deg) {
        deg %= 360.0F;
        if (deg >= 180.0F) deg -= 360.0F;
        if (deg < -180.0F) deg += 360.0F;
        return deg;
    }
}