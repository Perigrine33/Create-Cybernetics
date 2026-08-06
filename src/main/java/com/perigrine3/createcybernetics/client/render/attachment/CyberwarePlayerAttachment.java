package com.perigrine3.createcybernetics.client.render.attachment;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;

public interface CyberwarePlayerAttachment {

    Model model(AbstractClientPlayer player);

    ResourceLocation texture(AbstractClientPlayer player);

    void setupPose(PoseStack poseStack, AbstractClientPlayer player, PlayerModel<AbstractClientPlayer> parentModel, float partialTick);

    default int color(AbstractClientPlayer player) {
        return 0xFFFFFFFF;
    }

    default int packedLight(AbstractClientPlayer player, int packedLight) {
        return packedLight;
    }
}