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
        if (state == null || !state.hasHighlights()) return;

        PlayerSkin.Model modelType = player.getSkin().model();

        RenderSystem.enableBlend();
        try {
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
                    light = packedLight;
                    rt = RenderType.entityTranslucent(tex);
                    color = highlight.getColor();
                }

                var vc = buffer.getBuffer(rt);
                this.getParentModel().renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY, color);
            }

        } finally {
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
        }
    }
}
