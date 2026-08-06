package com.perigrine3.createcybernetics.client.render.attachment;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public final class CyberwarePlayerAttachmentLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public CyberwarePlayerAttachmentLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (player == null || player.isInvisible()) return;

        List<CyberwarePlayerAttachment> attachments = CyberwarePlayerAttachmentRegistry.getAttachments(player);
        if (attachments.isEmpty()) return;

        PlayerModel<AbstractClientPlayer> parentModel = this.getParentModel();

        for (CyberwarePlayerAttachment attachment : attachments) {
            if (attachment == null) continue;

            Model model = attachment.model(player);
            ResourceLocation texture = attachment.texture(player);

            if (model == null || texture == null) continue;

            poseStack.pushPose();

            try {
                attachment.setupPose(poseStack, player, parentModel, partialTick);

                var vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(texture));
                model.renderToBuffer(poseStack, vertexConsumer, attachment.packedLight(player, packedLight), OverlayTexture.NO_OVERLAY, attachment.color(player));
            } finally {
                poseStack.popPose();
            }
        }
    }
}