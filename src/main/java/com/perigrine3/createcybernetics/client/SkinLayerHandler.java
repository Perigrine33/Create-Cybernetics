package com.perigrine3.createcybernetics.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;

public final class SkinLayerHandler extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final ResourceLocation DEFAULT_MISSING_SKIN =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/playermuscles.png");

    public SkinLayerHandler(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player,
                      float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                      float netHeadYaw, float headPitch) {
    
    SkinModifierState state = SkinModifierManager.getPlayerSkinState(player);
    if (state == null || !state.hasModifiers()) return;

    PlayerModel<AbstractClientPlayer> model = this.getParentModel();

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
}
}