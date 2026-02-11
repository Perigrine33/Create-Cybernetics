package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class TargetingModuleClientOutline {

    private static final IntSet TARGET_ID = new IntOpenHashSet(1);
    private static long expiresAtGameTime = 0L;

    private TargetingModuleClientOutline() {}

    public static void clearTarget() {
        TARGET_ID.clear();
        expiresAtGameTime = 0L;
    }

    public static void setTarget(int entityId, int durationTicks) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        LocalPlayer player = mc.player;

        if (level == null) return;

        if (player != null && entityId == player.getId()) {
            clearTarget();
            return;
        }

        if (entityId < 0) {
            clearTarget();
            return;
        }

        TARGET_ID.clear();
        TARGET_ID.add(entityId);

        long now = level.getGameTime();
        expiresAtGameTime = now + Math.max(1, durationTicks);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (player == null || level == null) return;

        if (TARGET_ID.isEmpty()) return;
        if (level.getGameTime() >= expiresAtGameTime) {
            clearTarget();
            return;
        }

        int id = TARGET_ID.iterator().nextInt();

        if (id == player.getId()) {
            clearTarget();
            return;
        }

        var ent = level.getEntity(id);
        if (!(ent instanceof LivingEntity living)) {
            clearTarget();
            return;
        }

        if (living == player) {
            clearTarget();
            return;
        }

        if (!living.isAlive()) {
            clearTarget();
            return;
        }

        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();

        OutlineBufferSource outlines = mc.renderBuffers().outlineBufferSource();
        outlines.setColor(255, 140, 0, 255);

        var poseStack = event.getPoseStack();
        float partial = event.getPartialTick().getGameTimeDeltaPartialTick(true);

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
    }
}
