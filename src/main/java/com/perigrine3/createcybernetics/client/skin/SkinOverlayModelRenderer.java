package com.perigrine3.createcybernetics.client.skin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;

public final class SkinOverlayModelRenderer {

    private SkinOverlayModelRenderer() {}

    public static void renderModifier(
            PlayerModel<AbstractClientPlayer> model,
            SkinModifier modifier,
            PoseStack poseStack,
            VertexConsumer vc,
            int light,
            int overlay,
            int color
    ) {
        if (modifier == null) return;

        if (modifier.rendersOverlayPart(SkinModifier.OverlayPart.HEAD)) {
            model.head.render(poseStack, vc, light, overlay, color);
        }
        if (modifier.rendersOverlayPart(SkinModifier.OverlayPart.HAT)) {
            model.hat.render(poseStack, vc, light, overlay, color);
        }
        if (modifier.rendersOverlayPart(SkinModifier.OverlayPart.BODY)) {
            model.body.render(poseStack, vc, light, overlay, color);
        }
        if (modifier.rendersOverlayPart(SkinModifier.OverlayPart.JACKET)) {
            model.jacket.render(poseStack, vc, light, overlay, color);
        }
        if (modifier.rendersOverlayPart(SkinModifier.OverlayPart.LEFT_ARM)) {
            model.leftArm.render(poseStack, vc, light, overlay, color);
        }
        if (modifier.rendersOverlayPart(SkinModifier.OverlayPart.LEFT_SLEEVE)) {
            model.leftSleeve.render(poseStack, vc, light, overlay, color);
        }
        if (modifier.rendersOverlayPart(SkinModifier.OverlayPart.RIGHT_ARM)) {
            model.rightArm.render(poseStack, vc, light, overlay, color);
        }
        if (modifier.rendersOverlayPart(SkinModifier.OverlayPart.RIGHT_SLEEVE)) {
            model.rightSleeve.render(poseStack, vc, light, overlay, color);
        }
        if (modifier.rendersOverlayPart(SkinModifier.OverlayPart.LEFT_LEG)) {
            model.leftLeg.render(poseStack, vc, light, overlay, color);
        }
        if (modifier.rendersOverlayPart(SkinModifier.OverlayPart.LEFT_PANTS)) {
            model.leftPants.render(poseStack, vc, light, overlay, color);
        }
        if (modifier.rendersOverlayPart(SkinModifier.OverlayPart.RIGHT_LEG)) {
            model.rightLeg.render(poseStack, vc, light, overlay, color);
        }
        if (modifier.rendersOverlayPart(SkinModifier.OverlayPart.RIGHT_PANTS)) {
            model.rightPants.render(poseStack, vc, light, overlay, color);
        }
    }

    public static void renderFirstPersonArmModifier(
            PlayerModel<AbstractClientPlayer> model,
            SkinModifier modifier,
            boolean rightArm,
            PoseStack poseStack,
            VertexConsumer vc,
            int light,
            int overlay,
            int color
    ) {
        if (modifier == null) return;

        if (rightArm) {
            if (modifier.rendersOverlayPart(SkinModifier.OverlayPart.RIGHT_ARM)) {
                model.rightArm.render(poseStack, vc, light, overlay, color);
            }
            if (modifier.rendersOverlayPart(SkinModifier.OverlayPart.RIGHT_SLEEVE)) {
                model.rightSleeve.render(poseStack, vc, light, overlay, color);
            }
        } else {
            if (modifier.rendersOverlayPart(SkinModifier.OverlayPart.LEFT_ARM)) {
                model.leftArm.render(poseStack, vc, light, overlay, color);
            }
            if (modifier.rendersOverlayPart(SkinModifier.OverlayPart.LEFT_SLEEVE)) {
                model.leftSleeve.render(poseStack, vc, light, overlay, color);
            }
        }
    }
}