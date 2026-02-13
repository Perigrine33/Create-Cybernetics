package com.perigrine3.createcybernetics.mixin.client;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.vertex.PoseStack;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Shadow private ItemStack offHandItem;
    @Shadow private float offHandHeight;
    @Shadow private float oOffHandHeight;

    @Shadow
    private void renderArmWithItem(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack,
                                   float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight) {}

    @Shadow
    private void renderPlayerArm(PoseStack poseStack, MultiBufferSource buffer, int packedLight, float equippedProgress, float swingProgress, HumanoidArm side) {}

    @Inject(method = "renderHandsWithItems", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch()V",
            shift = At.Shift.BEFORE)
    )
    private void createcybernetics$renderExtraOffhandArm(float partialTicks, PoseStack poseStack, MultiBufferSource.BufferSource buffer, LocalPlayer playerEntity, int combinedLight, CallbackInfo ci) {
        if (playerEntity == null) return;
        if (!playerEntity.hasData(ModAttachments.CYBERWARE)) return;
        PlayerCyberwareData data = playerEntity.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        HumanoidArm main = playerEntity.getMainArm();
        CyberwareSlot offSlot = (main == HumanoidArm.RIGHT) ? CyberwareSlot.LARM : CyberwareSlot.RARM;

        if (!data.hasSpecificItem(ModItems.ARMUPGRADES_DRILLFIST.get(), offSlot)) return;
        if (!playerEntity.getOffhandItem().isEmpty()) return;

        float equipped = 1.0F - Mth.lerp(partialTicks, this.oOffHandHeight, this.offHandHeight);

        poseStack.pushPose();
        float attack = playerEntity.getAttackAnim(partialTicks);
        InteractionHand swinging = MoreObjects.firstNonNull(playerEntity.swingingArm, InteractionHand.MAIN_HAND);
        float swing = (swinging == InteractionHand.OFF_HAND) ? attack : 0.0F;

        this.renderPlayerArm(poseStack, buffer, combinedLight, equipped, swing, main.getOpposite());
        poseStack.popPose();
    }

    private static boolean vanillaWouldRenderOffhand(LocalPlayer player) {
        ItemStack main = player.getMainHandItem();
        ItemStack off  = player.getOffhandItem();

        boolean holdingBowLike = main.is(Items.BOW) || off.is(Items.BOW);
        boolean holdingCrossbow = main.is(Items.CROSSBOW) || off.is(Items.CROSSBOW);

        if (!holdingBowLike && !holdingCrossbow) {
            return true;
        }

        if (player.isUsingItem()) {
            return usingItemWhileHoldingBowLikeRendersOffhand(player);
        }

        return !isChargedCrossbow(main);
    }

    private static boolean usingItemWhileHoldingBowLikeRendersOffhand(LocalPlayer player) {
        ItemStack using = player.getUseItem();
        InteractionHand usedHand = player.getUsedItemHand();

        if (using.is(Items.BOW) || using.is(Items.CROSSBOW)) {
            return usedHand == InteractionHand.OFF_HAND;
        }

        if (usedHand == InteractionHand.MAIN_HAND && isChargedCrossbow(player.getOffhandItem())) {
            return false;
        }

        return true;
    }

    private static boolean isChargedCrossbow(ItemStack stack) {
        return stack.is(Items.CROSSBOW) && CrossbowItem.isCharged(stack);
    }
}
