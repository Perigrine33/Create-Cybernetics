package com.perigrine3.createcybernetics.client.skin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class SkinLayerHandler extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public SkinLayerHandler(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    private static boolean shouldRenderOverlaysFor(AbstractClientPlayer target) {
        Minecraft mc = Minecraft.getInstance();
        Entity cam = mc.getCameraEntity();

        if (cam instanceof Player viewer) {
            if (target.isInvisibleTo(viewer)) return false;
        } else {
            if (target.isInvisible()) return false;
        }

        return true;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {

        if (!shouldRenderOverlaysFor(player)) return;

        SkinModifierState state = SkinModifierManager.getPlayerSkinState(player);
        if (state == null || !state.hasModifiers()) return;

        PlayerModel<AbstractClientPlayer> model = this.getParentModel();

        boolean prevHat = model.hat.visible;
        boolean prevJacket = model.jacket.visible;
        boolean prevLeftSleeve = model.leftSleeve.visible;
        boolean prevRightSleeve = model.rightSleeve.visible;
        boolean prevLeftPants = model.leftPants.visible;
        boolean prevRightPants = model.rightPants.visible;

        model.hat.visible = true;
        model.jacket.visible = true;
        model.leftSleeve.visible = true;
        model.rightSleeve.visible = true;
        model.leftPants.visible = true;
        model.rightPants.visible = true;

        try {
            for (SkinModifier modifier : state.getModifiers()) {
                poseStack.pushPose();

                float scale = 1.0F;
                poseStack.scale(scale, scale, scale);

                PlayerSkin.Model modelType = player.getSkin().model();
                ResourceLocation texture = modifier.getTexture(modelType);

                int color = modifier.getColor();

                var vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(texture));
                model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, color);

                poseStack.popPose();
            }
        } finally {
            model.hat.visible = prevHat;
            model.jacket.visible = prevJacket;
            model.leftSleeve.visible = prevLeftSleeve;
            model.rightSleeve.visible = prevRightSleeve;
            model.leftPants.visible = prevLeftPants;
            model.rightPants.visible = prevRightPants;
        }
    }
}
