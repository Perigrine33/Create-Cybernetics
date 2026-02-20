package com.perigrine3.createcybernetics.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.component.ChargedProjectiles;
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

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class TrajectoryPreviewClient {

    private static final int MAX_STEPS = 120;

    private static final float ARROW_GRAVITY = 0.05F;
    private static final float ARROW_DRAG = 0.99F;

    private static final float TRIDENT_GRAVITY = 0.05F;
    private static final float TRIDENT_DRAG = 0.99F;

    private static final float THROW_GRAVITY = 0.03F;
    private static final float THROW_DRAG = 0.99F;

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
                        || tryBuildCrossbow(mc, player, level, partial, pts, firstHit)
                        || tryBuildTrident(mc, player, level, partial, pts, firstHit)
                        || tryBuildGenericThrowable(mc, player, level, partial, pts, firstHit);

        if (!built || pts.size() < 2) return;

        renderLine(event, mc, pts);
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

        Vec3 simPos = eye.add(0.0D, -0.10D, 0.0D);
        Vec3 simVel = look.scale(speed);

        boolean ok = buildBallistic(level, player, simPos, simVel, ARROW_GRAVITY, ARROW_DRAG, pts, firstHit);
        if (!ok) return false;

        applyRenderOffset(player, look, pts, 0.45D, 0.16D, 0.02D);
        return true;
    }

    private static boolean tryBuildCrossbow(
            Minecraft mc, LocalPlayer player, ClientLevel level, float partial,
            List<Vec3> pts, HitTarget firstHit
    ) {
        ItemStack held = getHeldOrUsing(player);
        if (held.isEmpty()) return false;

        if (!(held.getItem() instanceof CrossbowItem)) return false;
        if (!CrossbowItem.isCharged(held)) return false;

        if (isCrossbowLoadedWithRocket(held)) return false;

        Vec3 eye = player.getEyePosition(partial);
        Vec3 look = player.getViewVector(partial).normalize();

        double speed = 3.15D;

        Vec3 simPos = eye.add(look.scale(0.35D)).add(0.0D, -0.10D, 0.0D);
        Vec3 simVel = look.scale(speed);

        boolean ok = buildBallistic(level, player, simPos, simVel, ARROW_GRAVITY, ARROW_DRAG, pts, firstHit);
        if (!ok) return false;

        applyRenderOffset(player, look, pts, 0.38D, 0.14D, 0.02D);
        return true;
    }

    private static boolean isCrossbowLoadedWithRocket(ItemStack crossbow) {
        ChargedProjectiles charged = crossbow.get(DataComponents.CHARGED_PROJECTILES);
        if (charged == null) return false;

        List<ItemStack> items = charged.getItems();
        if (items == null || items.isEmpty()) return false;

        for (ItemStack st : items) {
            if (st != null && !st.isEmpty() && st.is(Items.FIREWORK_ROCKET)) return true;
        }
        return false;
    }

    private static boolean tryBuildTrident(
            Minecraft mc, LocalPlayer player, ClientLevel level, float partial,
            List<Vec3> pts, HitTarget firstHit
    ) {
        if (!player.isUsingItem()) return false;

        ItemStack using = player.getUseItem();
        if (using.isEmpty() || !(using.getItem() instanceof TridentItem)) return false;

        int usedTicks = using.getUseDuration(player) - player.getUseItemRemainingTicks();
        if (usedTicks < 10) return false;

        Vec3 eye = player.getEyePosition(partial);
        Vec3 look = player.getViewVector(partial).normalize();

        double speed = 2.5D;

        Vec3 simPos = eye.add(0.0D, -0.10D, 0.0D);
        Vec3 simVel = look.scale(speed);

        boolean ok = buildBallistic(level, player, simPos, simVel, TRIDENT_GRAVITY, TRIDENT_DRAG, pts, firstHit);
        if (!ok) return false;

        applyRenderOffset(player, look, pts, 0.30D, 0.12D, 0.02D);
        return true;
    }

    private static boolean tryBuildGenericThrowable(
            Minecraft mc, LocalPlayer player, ClientLevel level, float partial,
            List<Vec3> pts, HitTarget firstHit
    ) {
        ItemStack stack = getHeldOrUsing(player);
        if (stack.isEmpty()) return false;

        Item item = stack.getItem();
        if (!(item instanceof EnderpearlItem
                || item instanceof SnowballItem
                || item instanceof EggItem
                || item instanceof ThrowablePotionItem
                || item instanceof ExperienceBottleItem)) {
            return false;
        }

        Vec3 eye = player.getEyePosition(partial);
        Vec3 look = player.getViewVector(partial).normalize();

        double speed = 1.5D;
        float gravity = THROW_GRAVITY;
        float drag = THROW_DRAG;

        if (item instanceof ExperienceBottleItem) {
            speed = 0.7D;
            gravity = 0.07F;
        } else if (item instanceof ThrowablePotionItem) {
            speed = 0.5D;
            gravity = 0.05F;
        }

        Vec3 simPos = eye.add(look.scale(0.25D)).add(0.0D, -0.10D, 0.0D);
        Vec3 simVel = look.scale(speed);

        boolean ok = buildBallistic(level, player, simPos, simVel, gravity, drag, pts, firstHit);
        if (!ok) return false;

        applyRenderOffset(player, look, pts, 0.25D, 0.10D, 0.02D);
        return true;
    }

    private static ItemStack getHeldOrUsing(LocalPlayer player) {
        if (player.isUsingItem()) return player.getUseItem();

        ItemStack main = player.getMainHandItem();
        if (!main.isEmpty()) return main;

        return player.getOffhandItem();
    }

    private static boolean buildBallistic(
            ClientLevel level, LocalPlayer player,
            Vec3 startPos, Vec3 startVel,
            float gravity, float drag,
            List<Vec3> pts, HitTarget firstHit
    ) {
        pts.clear();
        firstHit.clear();

        Vec3 pos = startPos;
        Vec3 vel = startVel;

        pts.add(pos);

        for (int i = 0; i < MAX_STEPS; i++) {
            Vec3 next = pos.add(vel);

            HitResult hit = clipFirst(level, player, pos, next);
            if (hit.getType() != HitResult.Type.MISS) {
                pts.add(hit.getLocation());
                firstHit.setFrom(hit);
                break;
            }

            pts.add(next);

            vel = vel.scale(drag).add(0.0D, -gravity, 0.0D);
            pos = next;
        }

        return pts.size() >= 2;
    }

    private static void applyRenderOffset(LocalPlayer player, Vec3 lookUnit, List<Vec3> pts,
                                          double forward, double side, double yDelta) {
        if (pts.isEmpty()) return;

        Vec3 up = new Vec3(0.0D, 1.0D, 0.0D);
        Vec3 right = lookUnit.cross(up);
        if (right.lengthSqr() < 1.0E-6D) right = new Vec3(1.0D, 0.0D, 0.0D);
        else right = right.normalize();

        int armSign = (player.getMainArm() == HumanoidArm.RIGHT) ? 1 : -1;

        Vec3 delta0 = lookUnit.scale(forward)
                .add(right.scale(side * armSign))
                .add(0.0D, yDelta, 0.0D);

        int n = pts.size();
        if (n == 1) {
            pts.set(0, pts.get(0).add(delta0));
            return;
        }

        for (int i = 0; i < n; i++) {
            double t = (double) i / (double) (n - 1);
            double w = 1.0D - t;
            w = w * w;
            if (w <= 0.0D) continue;
            pts.set(i, pts.get(i).add(delta0.scale(w)));
        }
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
        AABB sweep = new AABB(from, to).inflate(0.75D);

        Entity best = null;
        Vec3 bestHit = null;
        double bestDist2 = Double.MAX_VALUE;

        List<Entity> ents = level.getEntities(player, sweep,
                e -> e instanceof LivingEntity living && living.isPickable() && living.isAlive());

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

    private static void renderLine(RenderLevelStageEvent event, Minecraft mc, List<Vec3> pts) {
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
            if (bb.getSize() < 1.0E-6D) bb = new AABB(pos);

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
        if (!player.isUsingItem() && player.getMainHandItem().isEmpty() && player.getOffhandItem().isEmpty()) return false;
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
            InstalledCyberware[] arr = entry.getValue();
            if (arr == null) continue;

            for (InstalledCyberware cw : arr) {
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
