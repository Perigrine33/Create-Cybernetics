package com.perigrine3.createcybernetics.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.custom.SmasherEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SmasherModel<T extends SmasherEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "smasher"), "main");
    private final ModelPart body;
    private final ModelPart Torso;
    private final ModelPart body2;
    private final ModelPart Wires;
    private final ModelPart head2;
    private final ModelPart eye;
    private final ModelPart arm0;
    private final ModelPart arm1;
    private final ModelPart leg0;
    private final ModelPart leg1;

    public SmasherModel(ModelPart root) {
        this.body = root.getChild("body");
        this.Torso = this.body.getChild("Torso");
        this.body2 = this.Torso.getChild("body2");
        this.Wires = this.Torso.getChild("Wires");
        this.head2 = this.body.getChild("head2");
        this.eye = this.head2.getChild("eye");
        this.arm0 = this.body.getChild("arm0");
        this.arm1 = this.body.getChild("arm1");
        this.leg0 = this.body.getChild("leg0");
        this.leg1 = this.body.getChild("leg1");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -7.0F, 0.0F));

        PartDefinition Torso = body.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(58, 0).addBox(-4.5F, 4.5F, -1.5F, 9.0F, 7.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -1.5F, -1.5F));

        PartDefinition body2 = Torso.addOrReplaceChild("body2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.1198F, -1.3043F));

        PartDefinition body_r1 = body2.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(82, 5).addBox(-1.0F, -6.5F, -1.5F, 2.0F, 13.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.2304F, 0.1827F, 0.2182F, 0.0F, 0.0F));

        PartDefinition body_r2 = body2.addOrReplaceChild("body_r2", CubeListBuilder.create().texOffs(48, 5).addBox(5.0F, 0.5F, -6.0F, 4.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, -3.1198F, 0.3043F, 0.2182F, 0.0F, 0.0F));

        PartDefinition body_r3 = body2.addOrReplaceChild("body_r3", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -5.5F, -5.5F, 18.0F, 11.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.1198F, 1.3043F, 0.2182F, 0.0F, 0.0F));

        PartDefinition Wires = Torso.addOrReplaceChild("Wires", CubeListBuilder.create(), PartPose.offset(-0.0687F, -0.1132F, -0.403F));

        PartDefinition body_r4 = Wires.addOrReplaceChild("body_r4", CubeListBuilder.create().texOffs(73, 29).addBox(0.0F, -7.0F, -6.5F, 0.0F, 14.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.1006F, 5.2044F, 0.2814F, -0.7418F, -3.0543F, -0.6109F));

        PartDefinition body_r5 = Wires.addOrReplaceChild("body_r5", CubeListBuilder.create().texOffs(73, 29).addBox(0.0F, -7.0F, -6.5F, 0.0F, 14.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8994F, 5.2044F, 0.2814F, -0.9163F, 3.0543F, 0.6109F));

        PartDefinition body_r6 = Wires.addOrReplaceChild("body_r6", CubeListBuilder.create().texOffs(73, 29).addBox(6.0F, -7.5F, -1.5F, 0.0F, 14.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.9313F, -3.9973F, -2.7186F, 0.2182F, 0.0F, 0.3054F));

        PartDefinition body_r7 = Wires.addOrReplaceChild("body_r7", CubeListBuilder.create().texOffs(73, 18).addBox(6.0F, -7.5F, -1.5F, 0.0F, 14.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.9313F, -6.9973F, 0.2814F, 0.0F, 3.1416F, -1.0472F));

        PartDefinition body_r8 = Wires.addOrReplaceChild("body_r8", CubeListBuilder.create().texOffs(73, 18).addBox(6.0F, -7.5F, -1.5F, 0.0F, 14.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0687F, 3.0027F, 0.2814F, 0.0F, 3.1416F, 1.0908F));

        PartDefinition body_r9 = Wires.addOrReplaceChild("body_r9", CubeListBuilder.create().texOffs(73, 16).addBox(6.0F, -7.5F, -1.5F, 0.0F, 14.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.9313F, 0.0027F, -1.7186F, 0.2182F, 0.1309F, 0.0436F));

        PartDefinition body_r10 = Wires.addOrReplaceChild("body_r10", CubeListBuilder.create().texOffs(73, 16).addBox(6.0F, -7.5F, -1.5F, 0.0F, 14.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.9313F, 1.0027F, -2.7186F, 0.2182F, -0.0873F, -0.0873F));

        PartDefinition head2 = body.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(0, 58).addBox(-4.0F, -9.0F, -5.0F, 8.0F, 10.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(58, 13).addBox(-4.0F, -9.0F, -6.0F, 8.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(58, 19).addBox(-4.0F, -4.0F, -6.0F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 58).addBox(-4.0F, -2.0F, -6.0F, 8.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 62).addBox(-1.0F, -2.0F, -8.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, -6.5F));

        PartDefinition head_r1 = head2.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(58, 16).addBox(-4.0F, -2.0F, -1.0F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, -6.0F, -0.48F, 0.0F, 0.0F));

        PartDefinition eye = head2.addOrReplaceChild("eye", CubeListBuilder.create().texOffs(39, 63).addBox(-0.5F, -7.0F, -5.51F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, -0.5F));

        PartDefinition arm0 = body.addOrReplaceChild("arm0", CubeListBuilder.create().texOffs(0, 22).addBox(-15.0F, -2.5F, -5.0F, 6.0F, 30.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(110, 18).addBox(-14.0F, 9.5F, 0.0F, 4.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

        PartDefinition cube_r1 = arm0.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(116, 33).addBox(-3.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.0F, -3.0F, -2.0F, 0.0F, 0.3054F, 0.0F));

        PartDefinition arm3_r1 = arm0.addOrReplaceChild("arm3_r1", CubeListBuilder.create().texOffs(105, 0).addBox(-7.0F, -2.0F, -2.0F, 7.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.5F, 12.5F, -2.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition arm1 = body.addOrReplaceChild("arm1", CubeListBuilder.create().texOffs(24, 22).addBox(9.0F, -2.5F, -5.0F, 6.0F, 30.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(110, 18).addBox(10.0F, 9.5F, 0.0F, 4.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

        PartDefinition cube_r2 = arm1.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(116, 41).addBox(-3.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.0F, -3.0F, -3.0F, 0.0F, 0.2182F, 0.0F));

        PartDefinition arm2_r1 = arm1.addOrReplaceChild("arm2_r1", CubeListBuilder.create().texOffs(105, 0).addBox(-7.0F, -2.0F, -2.0F, 7.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(15.5F, 12.5F, -2.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition leg0 = body.addOrReplaceChild("leg0", CubeListBuilder.create().texOffs(48, 22).addBox(-3.5F, 0.0F, -3.0F, 6.0F, 21.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(110, 18).addBox(-2.5F, 7.0F, -4.0F, 4.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 10.0F, 0.0F));

        PartDefinition leg2_r1 = leg0.addOrReplaceChild("leg2_r1", CubeListBuilder.create().texOffs(105, 0).addBox(-7.5F, -2.0F, -2.0F, 7.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, 10.5F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition leg1 = body.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(48, 49).addBox(-3.5F, 0.0F, -3.0F, 6.0F, 21.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(110, 18).addBox(-2.5F, 7.0F, -4.0F, 4.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 10.0F, 0.0F));

        PartDefinition leg3_r1 = leg1.addOrReplaceChild("leg3_r1", CubeListBuilder.create().texOffs(105, 0).addBox(-7.5F, -2.0F, -2.0F, 7.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, 10.5F, 0.0F, -0.7854F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(SmasherEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);

        this.animateWalk(SmasherAnimations.WALK_ANIM, limbSwing, limbSwingAmount, 2f, 2.5f);
        this.animate(entity.idleAnimationState, SmasherAnimations.IDLE_ANIM, ageInTicks, 1f);
    }

    private void applyHeadRotation(float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -25, 45);

        this.head2.yRot = headYaw * ((float)Math.PI / 180f);
        this.head2.xRot = headPitch * ((float)Math.PI / 180f);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    public ModelPart root() {
        return body;
    }

}
