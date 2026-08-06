package com.perigrine3.createcybernetics.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public final class CyberpsychosisShadowRenderer {

    private static final ResourceLocation SHADOW_TEXTURE = ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/cyberpsychosis_shadow.png");

    private static PlayerModel<Player> hallucinationModel;

    private CyberpsychosisShadowRenderer() {
    }

    public static void renderHallucination(PoseStack poseStack, MultiBufferSource buffers, Player animationSource, float partialTick, float alpha) {
        PlayerModel<Player> shadowModel = getHallucinationModel();

        prepareHallucinationModel(shadowModel, animationSource, partialTick);
        shadowModel.setupAnim(animationSource, 0.0F, 0.0F, animationSource.tickCount + partialTick, 0.0F, 0.0F);

        poseStack.pushPose();

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.501F, 0.0F);

        renderModel(poseStack, buffers, shadowModel, LightTexture.pack(2, 2), alpha);

        poseStack.popPose();
    }

    public static void renderPlayerShadow(PoseStack poseStack, MultiBufferSource buffers, AbstractClientPlayer target, PlayerModel<AbstractClientPlayer> shadowModel, float bodyYaw, float headYaw, float headPitch, float partialTick, int packedLight, float alpha) {
        preparePlayerModel(shadowModel, target, partialTick);

        float limbSwing = target.walkAnimation.position(partialTick);
        float limbSwingAmount = target.walkAnimation.speed(partialTick);
        float ageInTicks = target.tickCount + partialTick;

        if (target.isPassenger() && target.getVehicle() instanceof net.minecraft.world.entity.LivingEntity vehicle) {
            bodyYaw = Mth.rotLerp(partialTick, vehicle.yBodyRotO, vehicle.yBodyRot);
            float relativeHeadYaw = Mth.wrapDegrees(headYaw - bodyYaw);
            relativeHeadYaw = Mth.clamp(relativeHeadYaw, -85.0F, 85.0F);
            bodyYaw = headYaw - relativeHeadYaw;

            if (relativeHeadYaw * relativeHeadYaw > 2500.0F) {
                bodyYaw += relativeHeadYaw * 0.2F;
            }
        }

        shadowModel.setupAnim(target, limbSwing, limbSwingAmount, ageInTicks, Mth.wrapDegrees(headYaw - bodyYaw), headPitch);

        poseStack.pushPose();

        applyPlayerRotations(poseStack, target, bodyYaw, partialTick);

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.501F, 0.0F);

        renderModel(poseStack, buffers, shadowModel, packedLight, alpha);

        poseStack.popPose();
    }

    private static void prepareHallucinationModel(PlayerModel<Player> shadowModel, Player player, float partialTick) {
        shadowModel.young = false;
        shadowModel.riding = false;
        shadowModel.attackTime = 0.0F;
        shadowModel.swimAmount = 0.0F;
        shadowModel.crouching = false;
        shadowModel.leftArmPose = HumanoidModel.ArmPose.EMPTY;
        shadowModel.rightArmPose = HumanoidModel.ArmPose.EMPTY;

        setPartVisibility(shadowModel);
    }

    private static void preparePlayerModel(PlayerModel<AbstractClientPlayer> shadowModel, AbstractClientPlayer player, float partialTick) {
        shadowModel.young = player.isBaby();
        shadowModel.riding = player.isPassenger();
        shadowModel.attackTime = player.getAttackAnim(partialTick);
        shadowModel.swimAmount = player.getSwimAmount(partialTick);
        shadowModel.crouching = player.isCrouching();
        shadowModel.leftArmPose = HumanoidModel.ArmPose.EMPTY;
        shadowModel.rightArmPose = HumanoidModel.ArmPose.EMPTY;

        setPartVisibility(shadowModel);
    }

    private static void setPartVisibility(PlayerModel<?> shadowModel) {
        shadowModel.head.visible = true;
        shadowModel.hat.visible = false;
        shadowModel.body.visible = true;
        shadowModel.rightArm.visible = true;
        shadowModel.leftArm.visible = true;
        shadowModel.rightLeg.visible = true;
        shadowModel.leftLeg.visible = true;

        shadowModel.jacket.visible = false;
        shadowModel.rightSleeve.visible = false;
        shadowModel.leftSleeve.visible = false;
        shadowModel.rightPants.visible = false;
        shadowModel.leftPants.visible = false;
    }

    private static void applyPlayerRotations(PoseStack poseStack, AbstractClientPlayer player, float bodyYaw, float partialTick) {
        if (player.isSleeping()) {
            poseStack.mulPose(Axis.YP.rotationDegrees(getBedRotation(player)));
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
            return;
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));

        if (player.isFallFlying()) {
            float fallFlyingTicks = player.getFallFlyingTicks() + partialTick;
            float rotationProgress = Mth.clamp(fallFlyingTicks * fallFlyingTicks / 100.0F, 0.0F, 1.0F);

            if (!player.isAutoSpinAttack()) {
                poseStack.mulPose(Axis.XP.rotationDegrees(rotationProgress * (-90.0F - player.getXRot())));
            }

            return;
        }

        if (player.isVisuallySwimming()) {
            float swimmingAmount = player.getSwimAmount(partialTick);
            float swimmingPitch = player.isInWater() ? -90.0F - player.getXRot() : -90.0F;

            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(swimmingAmount, 0.0F, swimmingPitch)));
            poseStack.translate(0.0F, -1.0F, 0.3F);
        }
    }

    private static float getBedRotation(AbstractClientPlayer player) {
        if (player.getBedOrientation() == null) {
            return 0.0F;
        }

        return switch (player.getBedOrientation()) {
            case SOUTH -> 90.0F;
            case WEST -> 0.0F;
            case NORTH -> 270.0F;
            case EAST -> 180.0F;
            default -> 0.0F;
        };
    }

    private static void renderModel(PoseStack poseStack, MultiBufferSource buffers, PlayerModel<?> shadowModel, int packedLight, float alpha) {
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityTranslucent(SHADOW_TEXTURE));
        int color = getRenderColor(alpha);

        shadowModel.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, color);
    }

    private static PlayerModel<Player> getHallucinationModel() {
        if (hallucinationModel != null) {
            return hallucinationModel;
        }

        LayerDefinition layer = LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, false), 64, 64);
        ModelPart root = layer.bakeRoot();

        hallucinationModel = new PlayerModel<>(root, false);
        return hallucinationModel;
    }

    private static int getRenderColor(float alpha) {
        int alphaByte = Mth.clamp((int) (alpha * 255.0F), 0, 255);
        return alphaByte << 24 | 0x050505;
    }
}