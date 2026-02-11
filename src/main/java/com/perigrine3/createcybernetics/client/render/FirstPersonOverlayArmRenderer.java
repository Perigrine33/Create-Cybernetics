package com.perigrine3.createcybernetics.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;

public final class FirstPersonOverlayArmRenderer {

    private FirstPersonOverlayArmRenderer() {}

    public static void renderOverlayArmAndSleeve(
            AbstractClientPlayer player,
            HumanoidArm arm,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            ResourceLocation overlayTexture
    ) {
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        if (!(dispatcher.getRenderer(player) instanceof PlayerRenderer playerRenderer)) return;

        @SuppressWarnings("unchecked")
        PlayerModel<AbstractClientPlayer> model = (PlayerModel<AbstractClientPlayer>) playerRenderer.getModel();

        // Save visibility state
        boolean oldHead = model.head.visible;
        boolean oldHat = model.hat.visible;
        boolean oldBody = model.body.visible;
        boolean oldJacket = model.jacket.visible;

        boolean oldLeftArm = model.leftArm.visible;
        boolean oldLeftSleeve = model.leftSleeve.visible;
        boolean oldRightArm = model.rightArm.visible;
        boolean oldRightSleeve = model.rightSleeve.visible;

        boolean oldLeftLeg = model.leftLeg.visible;
        boolean oldLeftPants = model.leftPants.visible;
        boolean oldRightLeg = model.rightLeg.visible;
        boolean oldRightPants = model.rightPants.visible;

        // Hide everything, then selectively enable only the desired parts
        model.setAllVisible(false);

        // Stabilize pose flags for first-person overlay pass
        model.young = false;
        model.crouching = player.isCrouching();
        model.riding = false;
        model.attackTime = 0.0f;
        model.swimAmount = 0.0f;

        // Ensure arm rotations are up-to-date (RenderArmEvent runs after vanilla arm transforms,
        // but setupAnim helps avoid stale state if another render path touched the model)
        model.setupAnim(player, 0.0f, 0.0f, 0.0f, player.getYRot(), player.getXRot());

        if (arm == HumanoidArm.RIGHT) {
            model.rightArm.visible = true;
            model.rightSleeve.visible = true;
        } else {
            model.leftArm.visible = true;
            model.leftSleeve.visible = true;
        }

        VertexConsumer vc = buffers.getBuffer(RenderType.entityTranslucent(overlayTexture));
        model.renderToBuffer(poseStack, vc, packedLight, 0);

        // Restore visibility state
        model.head.visible = oldHead;
        model.hat.visible = oldHat;
        model.body.visible = oldBody;
        model.jacket.visible = oldJacket;

        model.leftArm.visible = oldLeftArm;
        model.leftSleeve.visible = oldLeftSleeve;
        model.rightArm.visible = oldRightArm;
        model.rightSleeve.visible = oldRightSleeve;

        model.leftLeg.visible = oldLeftLeg;
        model.leftPants.visible = oldLeftPants;
        model.rightLeg.visible = oldRightLeg;
        model.rightPants.visible = oldRightPants;
    }
}
