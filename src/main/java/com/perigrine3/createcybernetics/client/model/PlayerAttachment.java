package com.perigrine3.createcybernetics.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.model.Model;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;

public interface PlayerAttachment {

    AttachmentAnchor anchor();

    /** Texture to use for this attachment (wide/slim variants if you want them). */
    ResourceLocation texture(PlayerSkin.Model modelType);

    /** The baked model instance to render. */
    Model model(PlayerSkin.Model modelType);

    /** 0xAARRGGBB tint. */
    default int color() { return 0xFFFFFFFF; }

    /**
     * Called AFTER the pose stack has been moved into the anchor ModelPart's space via translateAndRotate.
     * Use this to position the attachment where you want (e.g., from shoulder to hand).
     */
    default void setupPose(PoseStack poseStack,
                           AbstractClientPlayer player,
                           PlayerModel<AbstractClientPlayer> parentModel,
                           PlayerSkin.Model modelType,
                           float partialTick) {}

    /** If true, do not render when player is invisible to the viewer. */
    default boolean respectsInvisibility() { return true; }

    /** Optional: only render in third-person. */
    default boolean thirdPersonOnly() { return false; }
}
