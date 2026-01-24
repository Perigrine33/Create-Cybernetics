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
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderArmEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT)
public final class SkinHighlightRender {

    private SkinHighlightRender() {}

    public static void apply(SkinModifierState state, boolean enabled,
                             ResourceLocation wide, ResourceLocation slim,
                             int color, boolean emissive) {
        apply(state, enabled, wide, slim, color, emissive, false);
    }

    public static void apply(SkinModifierState state, boolean enabled,
                             ResourceLocation wide, ResourceLocation slim,
                             int color, boolean emissive, boolean tintOnEmissive) {
        if (state == null) return;

        if (!enabled) {
            state.clearHighlights();
            return;
        }

        state.addHighlight(new SkinHighlight(wide, slim, color, emissive, tintOnEmissive));
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static final class Layers {
        private Layers() {}

        @SubscribeEvent
        public static void addLayers(EntityRenderersEvent.AddLayers event) {
            PlayerRenderer wide = event.getSkin(PlayerSkin.Model.WIDE);
            if (wide != null) wide.addLayer(new SkinHighlightLayer(wide));

            PlayerRenderer slim = event.getSkin(PlayerSkin.Model.SLIM);
            if (slim != null) slim.addLayer(new SkinHighlightLayer(slim));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onRenderArm(RenderArmEvent event) {
        AbstractClientPlayer player = event.getPlayer();

        SkinModifierState state = SkinModifierManager.getPlayerSkinState(player);
        if (state == null || !state.hasHighlights()) return;

        Minecraft mc = Minecraft.getInstance();
        EntityRenderer<? super AbstractClientPlayer> renderer = mc.getEntityRenderDispatcher().getRenderer(player);
        if (!(renderer instanceof PlayerRenderer playerRenderer)) return;

        HumanoidArm arm = event.getArm();

        PlayerModel<AbstractClientPlayer> model = playerRenderer.getModel();
        PlayerSkin.Model modelType = player.getSkin().model();

        ModelPart armPart = (arm == HumanoidArm.RIGHT) ? model.rightArm : model.leftArm;
        ModelPart sleevePart = (arm == HumanoidArm.RIGHT) ? model.rightSleeve : model.leftSleeve;

        boolean hideSleeve = (arm == HumanoidArm.RIGHT)
                ? state.getHideMask().contains(SkinModifier.HideVanilla.RIGHT_SLEEVE)
                : state.getHideMask().contains(SkinModifier.HideVanilla.LEFT_SLEEVE);

        var prevRightPose = model.rightArmPose;
        var prevLeftPose = model.leftArmPose;
        boolean prevCrouching = model.crouching;
        float prevSwimAmount = model.swimAmount;
        float prevAttackTime = model.attackTime;

        boolean prevRightArmVis = model.rightArm.visible;
        boolean prevLeftArmVis = model.leftArm.visible;
        boolean prevRightSleeveVis = model.rightSleeve.visible;
        boolean prevLeftSleeveVis = model.leftSleeve.visible;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource buffer = event.getMultiBufferSource();

        poseStack.pushPose();
        try {
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

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            for (SkinHighlight highlight : state.getHighlights()) {
                if (highlight == null) continue;

                ResourceLocation tex = highlight.getTexture(modelType);

                final boolean emissive = highlight.isEmissive();
                final boolean tintOnEmissive = highlight.tintOnEmissive();

                RenderType rt;
                int light;
                int color;

                if (emissive) {
                    light = 0x00F000F0;

                    if (tintOnEmissive) {
                        rt = SkinRenderTypes.emissiveTinted(tex);
                        color = highlight.getColor();
                    } else {
                        rt = RenderType.entityTranslucent(tex);
                        color = 0xFFFFFFFF;
                    }
                } else {
                    light = event.getPackedLight();
                    rt = RenderType.entityTranslucent(tex);
                    color = highlight.getColor();
                }

                var vc = buffer.getBuffer(rt);
                armPart.render(poseStack, vc, light, OverlayTexture.NO_OVERLAY, color);
                if (!hideSleeve) {
                    sleevePart.render(poseStack, vc, light, OverlayTexture.NO_OVERLAY, color);
                }
            }
        } finally {
            RenderSystem.disableBlend();

            model.rightArmPose = prevRightPose;
            model.leftArmPose = prevLeftPose;
            model.crouching = prevCrouching;
            model.swimAmount = prevSwimAmount;
            model.attackTime = prevAttackTime;

            model.rightArm.visible = prevRightArmVis;
            model.leftArm.visible = prevLeftArmVis;
            model.rightSleeve.visible = prevRightSleeveVis;
            model.leftSleeve.visible = prevLeftSleeveVis;

            poseStack.popPose();
        }
    }
}
