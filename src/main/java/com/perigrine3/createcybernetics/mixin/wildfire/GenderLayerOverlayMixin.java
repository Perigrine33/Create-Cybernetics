package com.perigrine3.createcybernetics.mixin.wildfire;

import com.mojang.blaze3d.vertex.PoseStack;
import com.perigrine3.createcybernetics.compat.wildfire.WildfireGenderOverlayCompat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.wildfire.render.GenderLayer", remap = false)
public abstract class GenderLayerOverlayMixin {

    @Inject(
            method = "renderBreast(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;IIFZZ)V",
            at = @At("TAIL"),
            require = 0
    )
    private void createcybernetics$renderWildfireBreastOverlays(
            LivingEntity entity,
            ItemStack armorStack,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            RenderType breastRenderType,
            int light,
            int overlay,
            float alpha,
            boolean left,
            boolean hasJacketLayer,
            CallbackInfo ci
    ) {
        WildfireGenderOverlayCompat.renderBreastOverlays(
                this,
                entity,
                poseStack,
                bufferSource,
                light,
                overlay,
                left
        );
    }
}