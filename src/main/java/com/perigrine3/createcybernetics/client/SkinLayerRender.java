package com.perigrine3.createcybernetics.client;

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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderArmEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SkinLayerRender {
    private SkinLayerRender() {
    }

    private static final Map<UUID, boolean[]> PREV_WEAR_VIS = new HashMap<>();
    private static final Map<UUID, SkinModifierState> SKIN_STATES = new HashMap<>();

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
        @SubscribeEvent
        public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
            if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;

            SkinModifierState state = SkinModifierManager.getPlayerSkinState(player);
            if (state == null || !state.shouldHideVanillaLayers()) return;

            PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();
            PREV_WEAR_VIS.put(player.getUUID(), new boolean[]{
                    model.jacket.visible,
                    model.leftSleeve.visible,
                    model.rightSleeve.visible,
                    model.leftPants.visible,
                    model.rightPants.visible,
                    model.hat.visible
            });

            model.jacket.visible = false;
            model.leftSleeve.visible = false;
            model.rightSleeve.visible = false;
            model.leftPants.visible = false;
            model.rightPants.visible = false;
            model.hat.visible = false;
        }

        @SubscribeEvent
        public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
            if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;

            boolean[] prev = PREV_WEAR_VIS.remove(player.getUUID());
            if (prev == null) return;

            PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();
            model.jacket.visible = prev[0];
            model.leftSleeve.visible = prev[1];
            model.rightSleeve.visible = prev[2];
            model.leftPants.visible = prev[3];
            model.rightPants.visible = prev[4];
            model.hat.visible = prev[5];
        }
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT)
    public final class FirstPersonSkinOverlayRenderer {

        private FirstPersonSkinOverlayRenderer() {
        }

        @SubscribeEvent
        public static void onRenderArm(RenderArmEvent event) {
            AbstractClientPlayer player = event.getPlayer();

            SkinModifierState state = SkinModifierManager.getPlayerSkinState(player);
            if (state == null || !state.hasModifiers()) return;

            Minecraft mc = Minecraft.getInstance();

            EntityRenderer<? super AbstractClientPlayer> renderer = mc.getEntityRenderDispatcher().getRenderer(player);
            if (!(renderer instanceof PlayerRenderer playerRenderer)) return;

            PlayerSkin.Model modelType = player.getSkin().model();

            PoseStack poseStack = event.getPoseStack();
            MultiBufferSource buffers = event.getMultiBufferSource();
            int light = event.getPackedLight();
            HumanoidArm arm = event.getArm();

            PlayerModel<AbstractClientPlayer> model = playerRenderer.getModel();
            ModelPart armPart = (arm == HumanoidArm.RIGHT) ? model.rightArm : model.leftArm;
            ModelPart sleevePart = (arm == HumanoidArm.RIGHT) ? model.rightSleeve : model.leftSleeve;

            poseStack.pushPose();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            poseStack.translate(0.0F, 0.0F, 0.0F);

            float scale = 1.005F;
            poseStack.scale(scale, scale, scale);

            for (SkinModifier modifier : state.getModifiers()) {
                // Use the correct texture based on model type
                ResourceLocation overlayTex = modifier.getTexture(modelType);
                var vc = buffers.getBuffer(RenderType.entityTranslucent(overlayTex));

                int color = modifier.getColor();
                float r = FastColor.ARGB32.red(color) / 255.0F;
                float g = FastColor.ARGB32.green(color) / 255.0F;
                float b = FastColor.ARGB32.blue(color) / 255.0F;
                float a = FastColor.ARGB32.alpha(color) / 255.0F;

                armPart.render(poseStack, vc, light, OverlayTexture.NO_OVERLAY);
                sleevePart.render(poseStack, vc, light, OverlayTexture.NO_OVERLAY);
            }

            RenderSystem.disableBlend();
            poseStack.popPose();
        }
    }
}