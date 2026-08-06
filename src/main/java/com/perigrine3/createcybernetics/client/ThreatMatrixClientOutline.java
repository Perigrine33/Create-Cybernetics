package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.ModTags;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
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

    private static final int RANGE = 32;

    private static final IntSet TARGET_IDS = new IntOpenHashSet();

    private static boolean active = false;
    private static boolean falseActivation = false;

    private ThreatMatrixClientOutline() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;

        active = false;
        falseActivation = false;
        TARGET_IDS.clear();

        if (player == null || level == null) return;
        if (!player.hasData(ModAttachments.CYBERWARE)) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        InstalledLoc location = findInstalledMatrix(data);
        if (location == null) return;

        boolean normallyEnabled = location.stack.is(ModTags.Items.TOGGLEABLE_CYBERWARE) && data.isEnabled(location.slot, location.index);
        falseActivation = shouldFalselyActivate(player);

        if (!normallyEnabled && !falseActivation) return;

        active = true;

        AABB box = player.getBoundingBox().inflate(RANGE);

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, box)) {
            if (!entity.isAlive()) continue;
            if (entity.isInvisible()) continue;

            boolean realEnemy = entity instanceof Enemy;
            boolean fakeHostile = CyberpsychosisMobHallucinationController.isHallucinated(entity);
            boolean shadowPlayer = entity instanceof AbstractClientPlayer && entity != player && CyberpsychosisPlayerShadowController.shouldAppearAsShadowToLocalPlayer();

            if (!realEnemy && !fakeHostile && !shadowPlayer) continue;

            TARGET_IDS.add(entity.getId());
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
        Vec3 cameraPosition = mc.gameRenderer.getMainCamera().getPosition();

        OutlineBufferSource outlines = mc.renderBuffers().outlineBufferSource();
        long gameTime = level.getGameTime();
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);

        double periodTicks = falseActivation ? 18.0D : 40.0D;
        double phase = ((gameTime + partialTick) / periodTicks) * (Math.PI * 2.0D);
        double pulse = (Math.sin(phase) + 1.0D) * 0.5D;

        int minimumRed = falseActivation ? 190 : 160;
        int maximumRed = falseActivation ? 255 : 220;
        int red = (int) Math.round(minimumRed + (maximumRed - minimumRed) * pulse);

        outlines.setColor(red, 0, 0, 255);

        var poseStack = event.getPoseStack();

        for (int id : TARGET_IDS) {
            var entity = level.getEntity(id);

            if (!(entity instanceof LivingEntity living)) continue;
            if (!living.isAlive()) continue;

            double x = living.getX() - cameraPosition.x;
            double y = living.getY() - cameraPosition.y;
            double z = living.getZ() - cameraPosition.z;

            dispatcher.render(living, x, y, z, living.getYRot(), partialTick, poseStack, outlines, 0x00F000F0);
        }

        outlines.endOutlineBatch();

        if (!falseActivation) renderNames(event, red);
    }

    private static boolean shouldFalselyActivate(LocalPlayer player) {
        if (player.hasEffect(ModEffects.CYBERPSYCHOSIS_FUGUE)) return true;
        if (CyberpsychosisShadowController.hasActiveShadow()) return true;
        if (CyberpsychosisPlayerShadowController.shouldAppearAsShadowToLocalPlayer()) return true;

        return CyberpsychosisMobHallucinationController.hasActiveHallucinations();
    }

    private static void renderNames(RenderLevelStageEvent event, int red) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;

        if (player == null || level == null) return;

        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        Font font = mc.font;
        Vec3 cameraPosition = mc.gameRenderer.getMainCamera().getPosition();

        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        var poseStack = event.getPoseStack();

        for (int id : TARGET_IDS) {
            var entity = level.getEntity(id);

            if (!(entity instanceof LivingEntity living)) continue;
            if (!living.isAlive()) continue;

            Component name = living.getDisplayName();

            double x = living.getX() - cameraPosition.x;
            double y = living.getY() + living.getBbHeight() + 0.35D - cameraPosition.y;
            double z = living.getZ() - cameraPosition.z;

            poseStack.pushPose();
            poseStack.translate(x, y, z);
            poseStack.mulPose(dispatcher.cameraOrientation());
            poseStack.scale(0.025F, -0.025F, 0.025F);

            int packedColor = 0xFF000000 | (red & 0xFF) << 16;
            float backgroundOpacity = mc.options.getBackgroundOpacity(0.25F);
            int background = (int) (backgroundOpacity * 255.0F) << 24;
            float width = font.width(name);

            poseStack.translate(-width / 2.0F, 0.0F, 0.0F);

            font.drawInBatch(name, 0.0F, 0.0F, packedColor, false, poseStack.last().pose(), buffers, Font.DisplayMode.SEE_THROUGH, background, 0x00F000F0);

            poseStack.popPose();
        }

        buffers.endBatch();
    }

    private static InstalledLoc findInstalledMatrix(PlayerCyberwareData data) {
        InstalledCyberware[] installed = data.getAll().get(CyberwareSlot.BRAIN);
        if (installed == null) return null;

        Item matrixItem = ModItems.BRAINUPGRADES_MATRIX.get();

        for (int index = 0; index < installed.length; index++) {
            InstalledCyberware cyberware = installed[index];

            if (cyberware == null) continue;

            ItemStack stack = cyberware.getItem();

            if (stack == null || stack.isEmpty()) continue;
            if (stack.getItem() == matrixItem) return new InstalledLoc(CyberwareSlot.BRAIN, index, stack);
        }

        return null;
    }

    private record InstalledLoc(CyberwareSlot slot, int index, ItemStack stack) {
    }
}