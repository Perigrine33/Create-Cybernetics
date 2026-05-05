package com.perigrine3.createcybernetics.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.perigrine3.createcybernetics.client.skin.SkinVanillaWearVisibility;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererSkinHideMixin {

    @Inject(
            method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD")
    )
    private void createcybernetics$pushCurrentRenderedPlayer(AbstractClientPlayer player, float entityYaw, float partialTicks,
                                                             PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                                                             CallbackInfo ci) {
        SkinVanillaWearVisibility.pushPlayer(player);
    }

    @Inject(
            method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("RETURN")
    )
    private void createcybernetics$popCurrentRenderedPlayer(AbstractClientPlayer player, float entityYaw, float partialTicks,
                                                            PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                                                            CallbackInfo ci) {
        SkinVanillaWearVisibility.popPlayer(player);
    }
}