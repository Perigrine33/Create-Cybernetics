package com.perigrine3.createcybernetics.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
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

public final class PlayerAttachmentLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public PlayerAttachmentLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {

        PlayerAttachmentState state = PlayerAttachmentManager.getState(player);
        if (state == null || state.isEmpty()) return;

        PlayerModel<AbstractClientPlayer> pm = getParentModel();
        PlayerSkin.Model modelType = player.getSkin().model();

        Minecraft mc = Minecraft.getInstance();
        Entity cam = mc.getCameraEntity();
        boolean isFirstPerson = mc.options.getCameraType().isFirstPerson();
        boolean isLocalViewTarget = (cam == player);

        boolean viewerCanSee = shouldRenderToViewer(player);

        for (PlayerAttachment att : state.all()) {
            if (att.respectsInvisibility() && !viewerCanSee) continue;

            // IMPORTANT: "thirdPersonOnly" should only suppress rendering on the LOCAL player in first person.
            // It should NOT hide attachments on other players just because the camera is first-person.
            if (att.thirdPersonOnly() && isFirstPerson && isLocalViewTarget) {
                continue;
            }

            ModelPart anchorPart = resolveAnchor(pm, att.anchor());
            if (anchorPart == null) continue;

            poseStack.pushPose();
            try {
                // Move into the anchor part's local space
                anchorPart.translateAndRotate(poseStack);

                // Attachment-specific offsets/rotations
                att.setupPose(poseStack, player, pm, modelType, partialTick);

                ResourceLocation tex = att.texture(modelType);
                Model m = att.model(modelType);

                RenderType rt = m.renderType(tex);
                var vc = buffer.getBuffer(rt);

                m.renderToBuffer(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY, att.color());
            } finally {
                poseStack.popPose();
            }
        }
    }

    private static ModelPart resolveAnchor(PlayerModel<?> pm, AttachmentAnchor a) {
        return switch (a) {
            case HEAD -> pm.head;
            case BODY -> pm.body;
            case RIGHT_ARM -> pm.rightArm;
            case LEFT_ARM -> pm.leftArm;
            case RIGHT_LEG -> pm.rightLeg;
            case LEFT_LEG -> pm.leftLeg;
        };
    }

    private static boolean shouldRenderToViewer(AbstractClientPlayer target) {
        Minecraft mc = Minecraft.getInstance();
        Entity cam = mc.getCameraEntity();
        if (cam instanceof Player viewer) {
            return !target.isInvisibleTo(viewer);
        }
        return !target.isInvisible();
    }
}
