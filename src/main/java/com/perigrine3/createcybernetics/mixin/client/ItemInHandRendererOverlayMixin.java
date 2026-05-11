package com.perigrine3.createcybernetics.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.perigrine3.createcybernetics.compat.playeranimator.PlayerAnimatorFirstPersonOverlayCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererOverlayMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(
            method = "renderPlayerArm",
            at = @At("TAIL")
    )
    private void createcybernetics$renderFirstPersonArmOverlays(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            float equippedProgress,
            float swingProgress,
            HumanoidArm arm,
            CallbackInfo ci
    ) {
        if (!(minecraft.player instanceof AbstractClientPlayer player)) return;
        if (minecraft.getCameraEntity() != player) return;
        if (player.isInvisible()) return;

        PlayerRenderer renderer = (PlayerRenderer) minecraft.getEntityRenderDispatcher().getRenderer(player);
        PlayerModel<AbstractClientPlayer> model = renderer.getModel();

        PlayerAnimatorFirstPersonOverlayCompat.renderVanillaFirstPersonArmOverlays(
                player,
                arm,
                model,
                poseStack,
                buffer,
                packedLight
        );
    }
}