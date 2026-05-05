package com.perigrine3.createcybernetics.client.skin;

import com.mojang.blaze3d.systems.RenderSystem;
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

public final class SkinHighlightLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public SkinHighlightLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
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
        if (state == null || !state.hasHighlights()) return;

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
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        try {
            PlayerSkin.Model modelType = player.getSkin().model();

            for (SkinHighlight highlight : state.getHighlights()) {
                if (highlight == null) continue;

                ResourceLocation tex = highlight.getTexture(modelType);

                RenderType rt;
                int light;
                int color;

                if (highlight.isEmissive()) {
                    light = 0x00F000F0;

                    if (highlight.tintOnEmissive()) {
                        rt = SkinRenderTypes.emissiveTinted(tex);
                        color = highlight.getColor();
                    } else {
                        rt = RenderType.entityTranslucent(tex);
                        color = 0xFFFFFFFF;
                    }
                } else {
                    light = packedLight;
                    rt = RenderType.entityTranslucent(tex);
                    color = highlight.getColor();
                }

                var vc = buffer.getBuffer(rt);
                model.renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY, color);
            }
        } finally {
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
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