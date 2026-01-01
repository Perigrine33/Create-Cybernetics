package com.perigrine3.createcybernetics.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class TrajectoryPreviewClient {

    private static final int MAX_STEPS = 120;

    private static final float BOW_GRAVITY = 0.05F;
    private static final float BOW_DRAG = 0.99F;

    private static boolean externalEnable = true;

    private TrajectoryPreviewClient() {}

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (player == null || level == null) return;

        if (!shouldRender(player)) return;

        float partial = event.getPartialTick().getGameTimeDeltaPartialTick(true);

        List<Vec3> pts = new ArrayList<>(MAX_STEPS + 2);

        HitTarget firstHit = new HitTarget();

        boolean built =
                tryBuildBow(mc, player, level, partial, pts, firstHit)
                        || tryBuildGenericThrowable(mc, player, level, partial, pts, firstHit)
                        || tryBuildCrossbow(mc, player, level, partial, pts, firstHit)
                        || tryBuildTrident(mc, player, level, partial, pts, firstHit);

        if (!built || pts.size() < 2) return;

        renderLine(event, mc, level, pts);

        renderFirstHitOverlay(event, mc, level, firstHit, partial);
    }

    private static boolean tryBuildBow(
            Minecraft mc, LocalPlayer player, ClientLevel level, float partial,
            List<Vec3> pts, HitTarget firstHit
    ) {
        if (!player.isUsingItem()) return false;

        ItemStack using = player.getUseItem();
        if (using.isEmpty() || !(using.getItem() instanceof BowItem)) return false;

        int usedTicks = using.getUseDuration(player) - player.getUseItemRemainingTicks();
        float power = BowItem.getPowerForTime(usedTicks);
        if (power <= 0.05F) return false;

        Vec3 eye = player.getEyePosition(partial);
        Vec3 look = player.getViewVector(partial).normalize();

        double speed = power * 3.0D;

        Vec3 up = new Vec3(0.0D, 1.0D, 0.0D);
        Vec3 right = look.cross(up).normalize(); // camera-right (correct handedness)

        int armSign = (player.getMainArm() == HumanoidArm.RIGHT) ? 1 : -1;

        Vec3 pos = eye
                .add(look.scale(0.45D))            // keep original forward
                .add(right.scale(0.16D * armSign)) // shift to bow side
                .add(0.0D, -0.08D, 0.0D);      // slight downward to match hand height

        Vec3 vel = look.scale(speed);


        pts.clear();
        pts.add(pos);

        firstHit.clear();

        for (int i = 0; i < MAX_STEPS; i++) {
            Vec3 next = pos.add(vel);

            HitResult hit = clipFirst(level, player, pos, next);

            if (hit.getType() != HitResult.Type.MISS) {
                pts.add(hit.getLocation());
                firstHit.setFrom(hit);
                break;
            }

            pts.add(next);

            vel = vel.scale(BOW_DRAG).add(0.0D, -BOW_GRAVITY, 0.0D);
            pos = next;
        }

        return pts.size() >= 2;
    }

    private static HitResult clipFirst(ClientLevel level, LocalPlayer player, Vec3 from, Vec3 to) {
        BlockHitResult blockHit = level.clip(new ClipContext(
                from, to,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));

        Vec3 blockLoc = blockHit.getLocation();
        double blockDist2 = from.distanceToSqr(blockLoc);

        EntityHitResult entityHit = clipEntities(level, player, from, to);
        if (entityHit == null) return blockHit;

        Vec3 entLoc = entityHit.getLocation();
        double entDist2 = from.distanceToSqr(entLoc);

        if (blockHit.getType() == HitResult.Type.MISS) return entityHit;
        return (entDist2 <= blockDist2) ? entityHit : blockHit;
    }

    private static EntityHitResult clipEntities(ClientLevel level, LocalPlayer player, Vec3 from, Vec3 to) {
        Vec3 delta = to.subtract(from);
        AABB sweep = new AABB(from, to).inflate(0.75D);

        Entity best = null;
        Vec3 bestHit = null;
        double bestDist2 = Double.MAX_VALUE;

        List<Entity> ents = level.getEntities(player, sweep, e -> e instanceof LivingEntity && e.isPickable() && e.isAlive());
        for (Entity e : ents) {
            AABB bb = e.getBoundingBox().inflate(0.3D);
            var opt = bb.clip(from, to);
            if (opt.isEmpty()) continue;

            Vec3 hit = opt.get();
            double d2 = from.distanceToSqr(hit);
            if (d2 < bestDist2) {
                bestDist2 = d2;
                best = e;
                bestHit = hit;
            }
        }

        if (best == null) return null;
        return new EntityHitResult(best, bestHit);
    }

    private static boolean tryBuildGenericThrowable(Minecraft mc, LocalPlayer player, ClientLevel level, float partial, List<Vec3> pts, HitTarget firstHit) {
        return false;
    }

    private static boolean tryBuildCrossbow(Minecraft mc, LocalPlayer player, ClientLevel level, float partial, List<Vec3> pts, HitTarget firstHit) {
        return false;
    }

    private static boolean tryBuildTrident(Minecraft mc, LocalPlayer player, ClientLevel level, float partial, List<Vec3> pts, HitTarget firstHit) {
        return false;
    }

    private static void renderLine(RenderLevelStageEvent event, Minecraft mc, ClientLevel level, List<Vec3> pts) {
        RenderSystem.lineWidth(3.0F);
        Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        VertexConsumer vc = buffer.getBuffer(RenderType.lines());

        var mat = poseStack.last().pose();

        int r = 255;
        int g = 160;
        int b = 40;
        int a = 255;

        for (int i = 0; i < pts.size() - 1; i++) {
            Vec3 p0 = pts.get(i).subtract(cam);
            Vec3 p1 = pts.get(i + 1).subtract(cam);

            float nx = 0.0F, ny = 1.0F, nz = 0.0F;

            vc.addVertex(mat, (float) p0.x, (float) p0.y, (float) p0.z).setColor(r, g, b, a).setNormal(nx, ny, nz);
            vc.addVertex(mat, (float) p1.x, (float) p1.y, (float) p1.z).setColor(r, g, b, a).setNormal(nx, ny, nz);
        }

        buffer.endBatch(RenderType.lines());
    }

    private static void renderFirstHitOverlay(RenderLevelStageEvent event, Minecraft mc, ClientLevel level, HitTarget hit, float partial) {
        if (hit.type == HitTargetType.NONE) return;

        OutlineBufferSource outlines = mc.renderBuffers().outlineBufferSource();
        outlines.setColor(255, 160, 40, 255);

        PoseStack poseStack = event.getPoseStack();

        if (hit.type == HitTargetType.ENTITY) {
            Entity e = level.getEntity(hit.entityId);
            if (!(e instanceof LivingEntity living)) return;
            if (!living.isAlive()) return;

            Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();
            EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();

            double x = living.getX() - cam.x;
            double y = living.getY() - cam.y;
            double z = living.getZ() - cam.z;

            dispatcher.render(
                    living,
                    x, y, z,
                    living.getYRot(),
                    partial,
                    poseStack,
                    outlines,
                    0x00F000F0
            );

            outlines.endOutlineBatch();
            return;
        }

        if (hit.type == HitTargetType.BLOCK) {
            BlockPos pos = hit.blockPos;
            if (pos == null) return;

            Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();

            AABB bb = level.getBlockState(pos).getShape(level, pos).bounds().move(pos);
            if (bb.getSize() < 1.0E-6D) {
                bb = new AABB(pos);
            }

            renderBoxOutline(poseStack, mc.renderBuffers().bufferSource(), cam, bb);
        }
    }

    private static void renderBoxOutline(PoseStack poseStack, MultiBufferSource.BufferSource buffer, Vec3 cam, AABB worldBox) {
        VertexConsumer vc = buffer.getBuffer(RenderType.lines());
        var mat = poseStack.last().pose();

        AABB base = worldBox.move(-cam.x, -cam.y, -cam.z);

        int r = 255, g = 160, bl = 40, a = 255;
        float nx = 0.0F, ny = 1.0F, nz = 0.0F;

        double e = 0.0035D;

        double[] s = new double[]{ -e, e };

        drawBoxLines(vc, mat, base, r, g, bl, a, nx, ny, nz);

        for (double ox : s) for (double oy : s) for (double oz : s) {
            drawBoxLines(vc, mat, base.move(ox, oy, oz), r, g, bl, a, nx, ny, nz);
        }

        buffer.endBatch(RenderType.lines());
    }

    private static void drawBoxLines(VertexConsumer vc, org.joml.Matrix4f mat, AABB b,
                                     int r, int g, int bl, int a,
                                     float nx, float ny, float nz) {
        float x0 = (float) b.minX, y0 = (float) b.minY, z0 = (float) b.minZ;
        float x1 = (float) b.maxX, y1 = (float) b.maxY, z1 = (float) b.maxZ;

        line(vc, mat, x0, y0, z0, x1, y0, z0, r, g, bl, a, nx, ny, nz);
        line(vc, mat, x1, y0, z0, x1, y0, z1, r, g, bl, a, nx, ny, nz);
        line(vc, mat, x1, y0, z1, x0, y0, z1, r, g, bl, a, nx, ny, nz);
        line(vc, mat, x0, y0, z1, x0, y0, z0, r, g, bl, a, nx, ny, nz);

        line(vc, mat, x0, y1, z0, x1, y1, z0, r, g, bl, a, nx, ny, nz);
        line(vc, mat, x1, y1, z0, x1, y1, z1, r, g, bl, a, nx, ny, nz);
        line(vc, mat, x1, y1, z1, x0, y1, z1, r, g, bl, a, nx, ny, nz);
        line(vc, mat, x0, y1, z1, x0, y1, z0, r, g, bl, a, nx, ny, nz);

        line(vc, mat, x0, y0, z0, x0, y1, z0, r, g, bl, a, nx, ny, nz);
        line(vc, mat, x1, y0, z0, x1, y1, z0, r, g, bl, a, nx, ny, nz);
        line(vc, mat, x1, y0, z1, x1, y1, z1, r, g, bl, a, nx, ny, nz);
        line(vc, mat, x0, y0, z1, x0, y1, z1, r, g, bl, a, nx, ny, nz);
    }

    private static void line(VertexConsumer vc, org.joml.Matrix4f mat,
                             float x0, float y0, float z0, float x1, float y1, float z1,
                             int r, int g, int b, int a, float nx, float ny, float nz) {
        vc.addVertex(mat, x0, y0, z0).setColor(r, g, b, a).setNormal(nx, ny, nz);
        vc.addVertex(mat, x1, y1, z1).setColor(r, g, b, a).setNormal(nx, ny, nz);
    }

    private static boolean shouldRender(LocalPlayer player) {
        if (!player.isUsingItem()) return false;
        if (!externalEnable) return false;

        if (!player.hasData(ModAttachments.CYBERWARE)) return false;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        Item module = ModItems.EYEUPGRADES_TRAJECTORYCALCULATOR.get();
        Item hudjack = ModItems.EYEUPGRADES_HUDJACK.get();

        if (!hasInstalled(data, module)) return false;
        return hasInstalled(data, hudjack);
    }

    private static boolean hasInstalled(PlayerCyberwareData data, Item item) {
        for (var entry : data.getAll().entrySet()) {
            CyberwareSlot slot = entry.getKey();
            InstalledCyberware[] arr = entry.getValue();
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {
                InstalledCyberware cw = arr[i];
                if (cw == null) continue;

                ItemStack st = cw.getItem();
                if (st == null || st.isEmpty()) continue;

                if (st.getItem() == item) return true;
            }
        }
        return false;
    }

    public static void setExternallyEnabled(boolean enabled) {
        externalEnable = enabled;
    }

    private enum HitTargetType { NONE, BLOCK, ENTITY }

    private static final class HitTarget {
        private HitTargetType type = HitTargetType.NONE;
        private BlockPos blockPos = null;
        private int entityId = -1;

        void clear() {
            type = HitTargetType.NONE;
            blockPos = null;
            entityId = -1;
        }

        void setFrom(HitResult hit) {
            clear();

            if (hit instanceof EntityHitResult ehr) {
                type = HitTargetType.ENTITY;
                entityId = ehr.getEntity().getId();
                return;
            }

            if (hit instanceof BlockHitResult bhr) {
                type = HitTargetType.BLOCK;
                blockPos = bhr.getBlockPos();
            }
        }
    }
}
