package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.ModTags;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class ThreatMatrixClientOutline {

    private static final IntSet TARGET_IDS = new IntOpenHashSet();
    private static boolean active = false;

    private ThreatMatrixClientOutline() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (player == null || level == null) return;

        active = false;
        TARGET_IDS.clear();

        if (!player.hasData(ModAttachments.CYBERWARE)) return;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        InstalledLoc loc = findInstalledMatrix(data);
        if (loc == null) return;

        if (!loc.stack.is(ModTags.Items.TOGGLEABLE_CYBERWARE)) return;
        if (!data.isEnabled(loc.slot, loc.index)) return;

        active = true;

        int range = 32;
        AABB box = player.getBoundingBox().inflate(range);

        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box)) {
            if (!e.isAlive()) continue;
            if (!(e instanceof Enemy)) continue;
            if (e.isInvisible()) continue;
            TARGET_IDS.add(e.getId());
        }
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
        if (!active || TARGET_IDS.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (player == null || level == null) return;

        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();

        OutlineBufferSource outlines = mc.renderBuffers().outlineBufferSource();
        long gt = level.getGameTime();
        float p = event.getPartialTick().getGameTimeDeltaPartialTick(true);

        double periodTicks = 40.0;
        double phase = ((gt + p) / periodTicks) * (Math.PI * 2.0);
        double pulse01 = (Math.sin(phase) + 1.0) * 0.5;

        int rMin = 160;
        int rMax = 220;
        int r = (int) Math.round(rMin + (rMax - rMin) * pulse01);

        outlines.setColor(r, 0, 0, 255);

        var poseStack = event.getPoseStack();
        float partial = event.getPartialTick().getGameTimeDeltaPartialTick(true);

        for (int id : TARGET_IDS) {
            var ent = level.getEntity(id);
            if (!(ent instanceof LivingEntity living)) continue;
            if (!living.isAlive()) continue;

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
        }

        outlines.endOutlineBatch();

        renderNames(event, r);
    }

    private static void renderNames(RenderLevelStageEvent event, int red) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (player == null || level == null) return;

        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        Font font = mc.font;
        Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();

        MultiBufferSource.BufferSource buf = mc.renderBuffers().bufferSource();
        var poseStack = event.getPoseStack();

        for (int id : TARGET_IDS) {
            var ent = level.getEntity(id);
            if (!(ent instanceof LivingEntity living)) continue;
            if (!living.isAlive()) continue;

            Component name = living.getDisplayName();

            double x = living.getX() - cam.x;
            double y = (living.getY() + living.getBbHeight() + 0.35D) - cam.y;
            double z = living.getZ() - cam.z;

            poseStack.pushPose();
            poseStack.translate(x, y, z);
            poseStack.mulPose(dispatcher.cameraOrientation());
            poseStack.scale(0.025F, -0.025F, 0.025F);

            int packedColor = (0xFF << 24) | ((red & 0xFF) << 16);

            float bgOpacity = mc.options.getBackgroundOpacity(0.25F);
            int bg = ((int) (bgOpacity * 255.0F) << 24);

            float w = (float) font.width(name);
            poseStack.translate(-w / 2.0F, 0.0F, 0.0F);

            font.drawInBatch(
                    name,
                    0.0F,
                    0.0F,
                    packedColor,
                    false,
                    poseStack.last().pose(),
                    buf,
                    Font.DisplayMode.SEE_THROUGH,
                    bg,
                    0x00F000F0
            );

            poseStack.popPose();
        }

        buf.endBatch();
    }

    private static InstalledLoc findInstalledMatrix(PlayerCyberwareData data) {
        InstalledCyberware[] arr = data.getAll().get(CyberwareSlot.BRAIN);
        if (arr == null) return null;

        Item matrixItem = ModItems.BRAINUPGRADES_MATRIX.get();

        for (int i = 0; i < arr.length; i++) {
            InstalledCyberware cw = arr[i];
            if (cw == null) continue;

            ItemStack st = cw.getItem();
            if (st == null || st.isEmpty()) continue;

            if (st.getItem() == matrixItem) {
                return new InstalledLoc(CyberwareSlot.BRAIN, i, st);
            }
        }
        return null;
    }

    private record InstalledLoc(CyberwareSlot slot, int index, ItemStack stack) {}
}
