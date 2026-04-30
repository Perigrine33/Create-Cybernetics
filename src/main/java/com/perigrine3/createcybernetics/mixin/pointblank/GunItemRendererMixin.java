package com.perigrine3.createcybernetics.mixin.pointblank;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.perigrine3.createcybernetics.compat.pointblank.PointBlankFirstPersonOverlayCompat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;

@Mixin(targets = "com.vicmatskiv.pointblank.client.render.GunItemRenderer", remap = false)
public abstract class GunItemRendererMixin {

    private final Deque<String> createcybernetics$boneStack = new ArrayDeque<>();

    private static Method createcybernetics$getBoneNameMethod;
    private static boolean createcybernetics$reflectionFailed;

    @Inject(method = "renderByItem", at = @At("HEAD"), require = 0)
    private void createcybernetics$beginPointBlankOverlayCapture(
            ItemStack stack,
            ItemDisplayContext itemDisplayContext,
            PoseStack poseStack,
            MultiBufferSource bufferSourceOrig,
            int packedLight,
            int packedOverlay,
            CallbackInfo ci
    ) {
        if (createcybernetics$isFirstPerson(itemDisplayContext)) {
            PointBlankFirstPersonOverlayCompat.beginFrame();
        }
    }

    @Inject(method = "renderByItem", at = @At("TAIL"), require = 0)
    private void createcybernetics$flushPointBlankOverlayCapture(
            ItemStack stack,
            ItemDisplayContext itemDisplayContext,
            PoseStack poseStack,
            MultiBufferSource bufferSourceOrig,
            int packedLight,
            int packedOverlay,
            CallbackInfo ci
    ) {
        if (createcybernetics$isFirstPerson(itemDisplayContext)) {
            PointBlankFirstPersonOverlayCompat.flush(bufferSourceOrig);
        }
    }

    @Inject(method = "renderCubesOfBone", at = @At("HEAD"), require = 0)
    private void createcybernetics$pushCurrentBone(
            PoseStack poseStack,
            @Coerce Object bone,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            int color,
            CallbackInfo ci
    ) {
        createcybernetics$boneStack.push(createcybernetics$getBoneName(bone));
    }

    @Inject(method = "renderCubesOfBone", at = @At("TAIL"), require = 0)
    private void createcybernetics$popCurrentBone(
            PoseStack poseStack,
            @Coerce Object bone,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            int color,
            CallbackInfo ci
    ) {
        if (!createcybernetics$boneStack.isEmpty()) {
            createcybernetics$boneStack.pop();
        }
    }

    @Inject(method = "renderCube", at = @At("TAIL"), require = 0)
    private void createcybernetics$captureCubeOverlay(
            PoseStack poseStack,
            @Coerce Object cube,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            int color,
            CallbackInfo ci
    ) {
        if (createcybernetics$boneStack.isEmpty()) return;

        String boneName = createcybernetics$boneStack.peek();
        if (!"rightarm".equals(boneName) && !"leftarm".equals(boneName)) return;

        PointBlankFirstPersonOverlayCompat.captureCubeOverlays(
                poseStack,
                cube,
                packedLight,
                packedOverlay
        );
    }

    private static boolean createcybernetics$isFirstPerson(ItemDisplayContext context) {
        return context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                || context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
    }

    private static String createcybernetics$getBoneName(Object bone) {
        if (createcybernetics$reflectionFailed || bone == null) return "";

        try {
            if (createcybernetics$getBoneNameMethod == null) {
                createcybernetics$getBoneNameMethod = bone.getClass().getMethod("getName");
                createcybernetics$getBoneNameMethod.setAccessible(true);
            }

            Object result = createcybernetics$getBoneNameMethod.invoke(bone);
            return result instanceof String s ? s : "";
        } catch (Throwable ignored) {
            createcybernetics$reflectionFailed = true;
            return "";
        }
    }
}