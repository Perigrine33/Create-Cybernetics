package com.perigrine3.createcybernetics.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public record CyberentityAttachment(
        AttachmentAnchor anchor,
        Model model,
        ResourceLocation texture,
        int color,
        boolean fullBright,
        PoseTuner tuner
) {
    @FunctionalInterface
    public interface PoseTuner {
        void apply(PoseStack poseStack, LivingEntity entity);
    }
}