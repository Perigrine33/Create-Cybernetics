package com.perigrine3.createcybernetics.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.custom.CyberzombieEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import static net.minecraft.client.model.AnimationUtils.animateZombieArms;

public class CyberzombieModel<T extends CyberzombieEntity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberzombie"), "main");

    private final ModelPart waist;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public CyberzombieModel(ModelPart root) {
        this.waist = root.getChild("waist");
        this.body = waist.getChild("body");
        this.head = body.getChild("head");
        this.rightArm = body.getChild("rightArm");
        this.leftArm = body.getChild("leftArm");
        this.rightLeg = body.getChild("rightLeg");
        this.leftLeg = body.getChild("leftLeg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition waist = root.addOrReplaceChild("waist", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));
        PartDefinition body = waist.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16)
                .addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4), PartPose.offset(0.0F, -12.0F, 0.0F));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8), PartPose.offset(0.0F, 0.0F, 0.0F));
        body.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(40, 16)
                .addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4), PartPose.offset(-5.0F, 2.0F, 0.0F));
        body.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(40, 16).mirror()
                .addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4), PartPose.offset(5.0F, 2.0F, 0.0F));
        body.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(0, 16)
                .addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4), PartPose.offset(-1.9F, 12.0F, 0.0F));
        body.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(0, 16).mirror()
                .addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4), PartPose.offset(1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CyberzombieEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);
        this.rightArm.xRot = 0.0F;
        this.rightArm.yRot = 0.0F;
        this.rightArm.zRot = 0.0F;
        this.leftArm.xRot = 0.0F;
        this.leftArm.yRot = 0.0F;
        this.leftArm.zRot = 0.0F;
        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        animateZombieArms(this.leftArm, this.rightArm, entity.isAggressive(), this.attackTime, ageInTicks);
    }

    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer buffer, int light, int overlay, int color) {
        waist.render(stack, buffer, light, overlay, color);
    }

    public ModelPart root() {
        return waist;
    }
}
