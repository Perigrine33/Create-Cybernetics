package com.perigrine3.createcybernetics.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.custom.CyberskeletonEntity;
import net.minecraft.client.model.ArmedModel;              // <-- ADD
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Items;

public class CyberskeletonModel<T extends CyberskeletonEntity> extends HierarchicalModel<T> implements ArmedModel {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberskeleton"), "main");

    private final ModelPart waist;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public CyberskeletonModel(ModelPart root) {
        this.waist = root.getChild("waist");
        this.body = this.waist.getChild("body");
        this.head = this.body.getChild("head");
        this.rightArm = this.body.getChild("rightArm");
        this.leftArm = this.body.getChild("leftArm");
        this.rightLeg = this.body.getChild("rightLeg");
        this.leftLeg = this.body.getChild("leftLeg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition waist = partdefinition.addOrReplaceChild("waist", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));
        PartDefinition body = waist.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16)
                .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        body.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(40, 16)
                .addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
        body.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(40, 16).mirror()
                .addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, 2.0F, 0.0F));
        body.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(0, 16)
                .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 12.0F, 0.0F));
        body.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(0, 16).mirror()
                .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, 12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    //Thx w1shadel (https://github.com/w1shadel/Cyberware-1.20.1-Port/blob/1.20.1-latest/src/main/java/com/Maxwell/cyber_ware_port/Common/Entity/Monster/CyberSkeleton/CyberSkeletonModel.java)
    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);

        this.rightArm.xRot = 0.0F;
        this.rightArm.yRot = 0.0F;
        this.rightArm.zRot = 0.0F;
        this.leftArm.xRot = 0.0F;
        this.leftArm.yRot = 0.0F;
        this.leftArm.zRot = 0.0F;

        this.rightArm.xRot += Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
        this.leftArm.xRot  += Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;

        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leftLeg.xRot  = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;

        boolean isAggressive = entity.isAggressive();
        boolean holdingBow = entity.getMainHandItem().is(Items.BOW);

        if (isAggressive) {
            if (holdingBow) {
                this.rightArm.yRot = this.head.yRot - 0.1F;
                this.leftArm.yRot  = this.head.yRot + 0.1F;
                this.rightArm.xRot = (-(float) Math.PI / 2F) + this.head.xRot;
                this.leftArm.xRot  = (-(float) Math.PI / 2F) + this.head.xRot;
                this.leftArm.xRot -= 0.05F;
                this.leftArm.yRot += 0.3F;
            } else {
                float attackTime = entity.getAttackAnim(ageInTicks);
                float sin1 = Mth.sin(attackTime * (float) Math.PI);
                float sin2 = Mth.sin((1.0F - (1.0F - attackTime) * (1.0F - attackTime)) * (float) Math.PI);

                this.rightArm.zRot = 0.0F;
                this.leftArm.zRot  = 0.0F;
                this.rightArm.yRot = -(0.1F - sin1 * 0.6F);
                this.leftArm.yRot  = 0.1F - sin1 * 0.6F;

                this.rightArm.xRot = (-(float) Math.PI / 2F);
                this.leftArm.xRot  = (-(float) Math.PI / 2F);

                this.rightArm.xRot -= sin1 * 1.2F - sin2 * 0.4F;
                this.leftArm.xRot  -= sin1 * 1.2F - sin2 * 0.4F;
            }
        } else {
            this.rightArm.zRot += Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            this.leftArm.zRot  -= Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            this.rightArm.xRot += Mth.sin(ageInTicks * 0.067F) * 0.05F;
            this.leftArm.xRot  -= Mth.sin(ageInTicks * 0.067F) * 0.05F;
        }
    }

    @Override
    public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
        this.waist.translateAndRotate(poseStack);
        this.body.translateAndRotate(poseStack);

        if (arm == HumanoidArm.RIGHT) {
            this.rightArm.translateAndRotate(poseStack);
        } else {
            this.leftArm.translateAndRotate(poseStack);
        }
    }

    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer buffer, int light, int overlay, int color) {
        waist.render(stack, buffer, light, overlay, color);
    }

    @Override
    public ModelPart root() {
        return waist;
    }
}
