package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import com.mojang.blaze3d.vertex.PoseStack;

public final class MissingSkinLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final ResourceLocation SKIN_OVERLAY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/playermuscles.png");

    public MissingSkinLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player, float limbSwing,
                       float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!player.hasData(ModAttachments.CYBERWARE)) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);

        boolean hasSkin = data.hasAnyTagged(ModTags.Items.SKIN_ITEMS, CyberwareSlot.SKIN);
        if (hasSkin) return;

        PlayerModel<AbstractClientPlayer> model = this.getParentModel();

        var vc = buffer.getBuffer(RenderType.entityTranslucent(SKIN_OVERLAY));
        int packedColor = FastColor.ARGB32.color(255, 255, 255, 255);

        model.renderToBuffer(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY, packedColor);
    }
}
