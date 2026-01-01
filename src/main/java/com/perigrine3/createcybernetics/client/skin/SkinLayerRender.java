package com.perigrine3.createcybernetics.client.skin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderArmEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SkinLayerRender {
    private SkinLayerRender() {}

    private static final Map<UUID, boolean[]> PREV_WEAR_VIS = new HashMap<>();
    private static final Map<UUID, SkinModifierState> SKIN_STATES = new HashMap<>();

    private static final Map<UUID, Boolean> REPLACE_FP_RIGHT = new HashMap<>();
    private static final Map<UUID, Boolean> REPLACE_FP_LEFT  = new HashMap<>();

    private static final Map<UUID, Boolean> FP_CANCEL_RIGHT = new HashMap<>();
    private static final Map<UUID, Boolean> FP_CANCEL_LEFT  = new HashMap<>();
    private static final Map<UUID, Boolean> FP_HIDE_SLEEVE_RIGHT = new HashMap<>();
    private static final Map<UUID, Boolean> FP_HIDE_SLEEVE_LEFT  = new HashMap<>();


    @EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static final class Layers {
        @SubscribeEvent
        public static void addLayers(EntityRenderersEvent.AddLayers event) {
            PlayerRenderer wide = event.getSkin(PlayerSkin.Model.WIDE);
            if (wide != null) wide.addLayer(new SkinLayerHandler(wide));

            PlayerRenderer slim = event.getSkin(PlayerSkin.Model.SLIM);
            if (slim != null) slim.addLayer(new SkinLayerHandler(slim));
        }
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT)
    public static final class Wear {

        private static final Map<UUID, boolean[]> PREV_WEAR_VIS = new HashMap<>();

        @SubscribeEvent
        public static void onRenderLivingPre(net.neoforged.neoforge.client.event.RenderLivingEvent.Pre<?, ?> event) {
            if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;
            if (!(event.getRenderer() instanceof PlayerRenderer)) return;

            SkinModifierState state = SkinModifierManager.getPlayerSkinState(player);
            if (state == null || !state.hasModifiers()) return;

            var hide = state.getHideMask();
            if (hide.isEmpty()) return;

            var parentModel = ((PlayerRenderer) event.getRenderer()).getModel();
            if (!(parentModel instanceof PlayerModel<?> pmAny)) return;

            @SuppressWarnings("unchecked")
            PlayerModel<AbstractClientPlayer> model = (PlayerModel<AbstractClientPlayer>) pmAny;

            PREV_WEAR_VIS.put(player.getUUID(), new boolean[]{
                    model.hat.visible,
                    model.jacket.visible,
                    model.leftSleeve.visible,
                    model.rightSleeve.visible,
                    model.leftPants.visible,
                    model.rightPants.visible
            });

            if (hide.contains(SkinModifier.HideVanilla.HAT)) model.hat.visible = false;
            if (hide.contains(SkinModifier.HideVanilla.JACKET)) model.jacket.visible = false;
            if (hide.contains(SkinModifier.HideVanilla.LEFT_SLEEVE)) model.leftSleeve.visible = false;
            if (hide.contains(SkinModifier.HideVanilla.RIGHT_SLEEVE)) model.rightSleeve.visible = false;
            if (hide.contains(SkinModifier.HideVanilla.LEFT_PANTS)) model.leftPants.visible = false;
            if (hide.contains(SkinModifier.HideVanilla.RIGHT_PANTS)) model.rightPants.visible = false;
        }

        @SubscribeEvent
        public static void onRenderLivingPost(net.neoforged.neoforge.client.event.RenderLivingEvent.Post<?, ?> event) {
            if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;
            if (!(event.getRenderer() instanceof PlayerRenderer)) return;

            boolean[] prev = PREV_WEAR_VIS.remove(player.getUUID());
            if (prev == null) return;

            var parentModel = ((PlayerRenderer) event.getRenderer()).getModel();
            if (!(parentModel instanceof PlayerModel<?> pmAny)) return;

            @SuppressWarnings("unchecked")
            PlayerModel<AbstractClientPlayer> model = (PlayerModel<AbstractClientPlayer>) pmAny;

            model.hat.visible = prev[0];
            model.jacket.visible = prev[1];
            model.leftSleeve.visible = prev[2];
            model.rightSleeve.visible = prev[3];
            model.leftPants.visible = prev[4];
            model.rightPants.visible = prev[5];
        }
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT)
    public final class FirstPersonSkinOverlayRenderer {

        private FirstPersonSkinOverlayRenderer() {}

        private static final Map<UUID, Boolean> FP_CANCEL_RIGHT = new HashMap<>();
        private static final Map<UUID, Boolean> FP_CANCEL_LEFT  = new HashMap<>();
        private static final Map<UUID, Boolean> FP_HIDE_SLEEVE_RIGHT = new HashMap<>();
        private static final Map<UUID, Boolean> FP_HIDE_SLEEVE_LEFT  = new HashMap<>();

        @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
        public static void onRenderArmCancel(RenderArmEvent event) {
            AbstractClientPlayer player = event.getPlayer();

            SkinModifierState state = SkinModifierManager.getPlayerSkinState(player);
            if (state == null || !state.hasModifiers()) return;

            HumanoidArm arm = event.getArm();
            UUID id = player.getUUID();

            boolean replace = false;
            for (SkinModifier m : state.getModifiers()) {
                if (m.replacesVanillaArm(arm)) {
                    replace = true;
                    break;
                }
            }

            var hide = state.getHideMask();
            boolean hideSleeve = (arm == HumanoidArm.RIGHT)
                    ? hide.contains(SkinModifier.HideVanilla.RIGHT_SLEEVE)
                    : hide.contains(SkinModifier.HideVanilla.LEFT_SLEEVE);

            boolean cancel = replace || hideSleeve;

            if (arm == HumanoidArm.RIGHT) {
                REPLACE_FP_RIGHT.put(id, replace);
                FP_HIDE_SLEEVE_RIGHT.put(id, hideSleeve);
                FP_CANCEL_RIGHT.put(id, cancel);
            } else {
                REPLACE_FP_LEFT.put(id, replace);
                FP_HIDE_SLEEVE_LEFT.put(id, hideSleeve);
                FP_CANCEL_LEFT.put(id, cancel);
            }

            if (cancel) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
        public static void onRenderArm(RenderArmEvent event) {
            AbstractClientPlayer player = event.getPlayer();

            SkinModifierState state = SkinModifierManager.getPlayerSkinState(player);
            if (state == null || !state.hasModifiers()) return;

            Minecraft mc = Minecraft.getInstance();
            EntityRenderer<? super AbstractClientPlayer> renderer = mc.getEntityRenderDispatcher().getRenderer(player);
            if (!(renderer instanceof PlayerRenderer playerRenderer)) return;

            PoseStack poseStack = event.getPoseStack();
            MultiBufferSource buffer = event.getMultiBufferSource();
            int light = event.getPackedLight();
            HumanoidArm arm = event.getArm();

            UUID id = player.getUUID();

            boolean replaceVanillaArm = (arm == HumanoidArm.RIGHT)
                    ? Boolean.TRUE.equals(REPLACE_FP_RIGHT.get(id))
                    : Boolean.TRUE.equals(REPLACE_FP_LEFT.get(id));

            boolean cancel = (arm == HumanoidArm.RIGHT)
                    ? Boolean.TRUE.equals(FP_CANCEL_RIGHT.get(id))
                    : Boolean.TRUE.equals(FP_CANCEL_LEFT.get(id));

            boolean hideSleeve = (arm == HumanoidArm.RIGHT)
                    ? Boolean.TRUE.equals(FP_HIDE_SLEEVE_RIGHT.get(id))
                    : Boolean.TRUE.equals(FP_HIDE_SLEEVE_LEFT.get(id));

            SkinModifier baseArmModifier = null;
            if (replaceVanillaArm) {
                for (SkinModifier m : state.getModifiers()) {
                    if (m.replacesVanillaArm(arm)) {
                        baseArmModifier = m;
                        break;
                    }
                }
            }

            PlayerModel<AbstractClientPlayer> model = playerRenderer.getModel();
            PlayerSkin.Model modelType = player.getSkin().model();

            ModelPart armPart = (arm == HumanoidArm.RIGHT) ? model.rightArm : model.leftArm;
            ModelPart sleevePart = (arm == HumanoidArm.RIGHT) ? model.rightSleeve : model.leftSleeve;

            var prevRightPose = model.rightArmPose;
            var prevLeftPose = model.leftArmPose;
            boolean prevCrouching = model.crouching;
            float prevSwimAmount = model.swimAmount;
            float prevAttackTime = model.attackTime;

            boolean prevRightArmVis = model.rightArm.visible;
            boolean prevLeftArmVis = model.leftArm.visible;
            boolean prevRightSleeveVis = model.rightSleeve.visible;
            boolean prevLeftSleeveVis = model.leftSleeve.visible;

            poseStack.pushPose();
            try {
                poseStack.scale(1.005F, 1.005F, 1.005F);

                model.attackTime = 0.0F;
                model.crouching = false;
                model.swimAmount = 0.0F;

                model.rightArmPose = net.minecraft.client.model.HumanoidModel.ArmPose.EMPTY;
                model.leftArmPose  = net.minecraft.client.model.HumanoidModel.ArmPose.EMPTY;

                model.rightArm.visible = (arm == HumanoidArm.RIGHT);
                model.rightSleeve.visible = (arm == HumanoidArm.RIGHT);
                model.leftArm.visible = (arm == HumanoidArm.LEFT);
                model.leftSleeve.visible = (arm == HumanoidArm.LEFT);

                model.setupAnim(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

                if (cancel) {
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    RenderSystem.colorMask(true, true, true, true);

                    if (replaceVanillaArm && baseArmModifier != null) {
                        ResourceLocation baseTex = baseArmModifier.getTexture(modelType);
                        var baseVc = buffer.getBuffer(RenderType.entityTranslucent(baseTex));
                        int whiteOpaque = 0xFFFFFFFF;

                        armPart.render(poseStack, baseVc, light, OverlayTexture.NO_OVERLAY, whiteOpaque);
                        sleevePart.render(poseStack, baseVc, light, OverlayTexture.NO_OVERLAY, whiteOpaque);
                    } else {
                        ResourceLocation baseSkinTex = player.getSkin().texture();
                        var baseVc = buffer.getBuffer(RenderType.entitySolid(baseSkinTex));
                        int whiteOpaque = 0xFFFFFFFF;

                        armPart.render(poseStack, baseVc, light, OverlayTexture.NO_OVERLAY, whiteOpaque);
                        if (!hideSleeve) {
                            sleevePart.render(poseStack, baseVc, light, OverlayTexture.NO_OVERLAY, whiteOpaque);
                        }
                    }
                }

                if (!replaceVanillaArm) {
                    boolean needsPlayerUnderlay = state.getModifiers().stream()
                            .anyMatch(m -> m.needsPlayerSkinUnderlay() && FastColor.ARGB32.alpha(m.getColor()) < 255);

                    if (needsPlayerUnderlay) {
                        RenderSystem.enableBlend();
                        RenderSystem.defaultBlendFunc();
                        RenderSystem.colorMask(true, true, true, true);

                        ResourceLocation baseSkinTex = player.getSkin().texture();
                        var underlayVc = buffer.getBuffer(RenderType.entityTranslucent(baseSkinTex));
                        int whiteOpaque = 0xFFFFFFFF;

                        armPart.render(poseStack, underlayVc, light, OverlayTexture.NO_OVERLAY, whiteOpaque);
                        if (!hideSleeve) {
                            sleevePart.render(poseStack, underlayVc, light, OverlayTexture.NO_OVERLAY, whiteOpaque);
                        }
                    }
                }

                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.colorMask(true, true, true, false);

                for (SkinModifier modifier : state.getModifiers()) {
                    ResourceLocation overlayTex = modifier.getTexture(modelType);
                    var vc = buffer.getBuffer(RenderType.entityTranslucent(overlayTex));

                    int color = modifier.getColor();
                    armPart.render(poseStack, vc, light, OverlayTexture.NO_OVERLAY, color);
                    sleevePart.render(poseStack, vc, light, OverlayTexture.NO_OVERLAY, color);
                }

                RenderSystem.colorMask(true, true, true, true);
                RenderSystem.disableBlend();

            } finally {
                model.rightArmPose = prevRightPose;
                model.leftArmPose = prevLeftPose;
                model.crouching = prevCrouching;
                model.swimAmount = prevSwimAmount;
                model.attackTime = prevAttackTime;

                model.rightArm.visible = prevRightArmVis;
                model.leftArm.visible = prevLeftArmVis;
                model.rightSleeve.visible = prevRightSleeveVis;
                model.leftSleeve.visible = prevLeftSleeveVis;

                if (arm == HumanoidArm.RIGHT) {
                    REPLACE_FP_RIGHT.remove(id);
                    FP_CANCEL_RIGHT.remove(id);
                    FP_HIDE_SLEEVE_RIGHT.remove(id);
                } else {
                    REPLACE_FP_LEFT.remove(id);
                    FP_CANCEL_LEFT.remove(id);
                    FP_HIDE_SLEEVE_LEFT.remove(id);
                }

                poseStack.popPose();
            }
        }
    }
}
