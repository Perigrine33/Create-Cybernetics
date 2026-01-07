package com.perigrine3.createcybernetics.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public final class CyberElytraRenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private final ElytraModel<AbstractClientPlayer> cyberElytraModel;

    private final ElytraLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> vanillaHelper;

    public CyberElytraRenderLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent,
                                  EntityModelSet modelSet) {
        super(parent);
        this.cyberElytraModel = new ElytraModel<>(modelSet.bakeLayer(CyberElytraClient.CYBER_ELYTRA_LAYER));
        this.vanillaHelper = new ElytraLayer<>(parent, modelSet);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {

        if (!shouldRenderCyberElytra(player)) return;

        if (player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) return;

        poseStack.pushPose();

        poseStack.translate(0.0F, 0.0F, 0.125F);

        EntityModel<AbstractClientPlayer> parentModel = this.getParentModel();
        parentModel.copyPropertiesTo(this.cyberElytraModel);
        this.cyberElytraModel.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        ResourceLocation tex = vanillaHelper.getElytraTexture(Items.ELYTRA.getDefaultInstance(), player);

        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(tex));
        this.cyberElytraModel.renderToBuffer(
                poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY, 0x999999);

        poseStack.popPose();
    }

    private static boolean shouldRenderCyberElytra(AbstractClientPlayer player) {
        if (player == null) return false;
        if (!player.hasData(ModAttachments.CYBERWARE)) return false;

        if (ModItems.BONEUPGRADES_ELYTRA != null) {
            Item item = ModItems.BONEUPGRADES_ELYTRA.get();
            if (!(item instanceof ICyberwareItem cw)) return false;

            return cw.isEnabledByWheel(player);
        }

        return false;
    }
}
