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
            return !target.isInvisibleTo(viewer);
        }

        return !target.isInvisible();
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (!shouldRenderOverlaysFor(player)) return;

        SkinModifierState state = SkinModifierManager.getPlayerSkinState(player);
        if (state == null || !state.hasModifiers()) return;

        PlayerModel<AbstractClientPlayer> model = this.getParentModel();

        boolean prevHead = model.head.visible;
        boolean prevHat = model.hat.visible;
        boolean prevBody = model.body.visible;
        boolean prevJacket = model.jacket.visible;
        boolean prevLeftArm = model.leftArm.visible;
        boolean prevLeftSleeve = model.leftSleeve.visible;
        boolean prevRightArm = model.rightArm.visible;
        boolean prevRightSleeve = model.rightSleeve.visible;
        boolean prevLeftLeg = model.leftLeg.visible;
        boolean prevLeftPants = model.leftPants.visible;
        boolean prevRightLeg = model.rightLeg.visible;
        boolean prevRightPants = model.rightPants.visible;

        model.head.visible = true;
        model.hat.visible = true;
        model.body.visible = true;
        model.jacket.visible = true;
        model.leftArm.visible = true;
        model.leftSleeve.visible = true;
        model.rightArm.visible = true;
        model.rightSleeve.visible = true;
        model.leftLeg.visible = true;
        model.leftPants.visible = true;
        model.rightLeg.visible = true;
        model.rightPants.visible = true;

        SkinVanillaWearVisibility.pushSuppress();
        try {
            PlayerSkin.Model modelType = player.getSkin().model();

            for (SkinModifier modifier : state.getModifiers()) {
                if (modifier == null) continue;

                poseStack.pushPose();
                try {
                    ResourceLocation texture = modifier.getTexture(modelType);
                    int color = modifier.getColor();

                    var baseVc = buffer.getBuffer(RenderType.entityTranslucent(texture));
                    SkinOverlayModelRenderer.renderModifier(
                            model,
                            modifier,
                            poseStack,
                            baseVc,
                            packedLight,
                            OverlayTexture.NO_OVERLAY,
                            color
                    );

                    if (modifier.hasGlint()) {
                        var glintVc = buffer.getBuffer(SkinRenderTypes.translucentGlintOverlay(texture));
                        SkinOverlayModelRenderer.renderModifier(
                                model,
                                modifier,
                                poseStack,
                                glintVc,
                                packedLight,
                                OverlayTexture.NO_OVERLAY,
                                0xFFFFFFFF
                        );
                    }
                } finally {
                    poseStack.popPose();
                }
            }
        } finally {
            SkinVanillaWearVisibility.popSuppress();

            model.head.visible = prevHead;
            model.hat.visible = prevHat;
            model.body.visible = prevBody;
            model.jacket.visible = prevJacket;
            model.leftArm.visible = prevLeftArm;
            model.leftSleeve.visible = prevLeftSleeve;
            model.rightArm.visible = prevRightArm;
            model.rightSleeve.visible = prevRightSleeve;
            model.leftLeg.visible = prevLeftLeg;
            model.leftPants.visible = prevLeftPants;
            model.rightLeg.visible = prevRightLeg;
            model.rightPants.visible = prevRightPants;
        }
    }
}