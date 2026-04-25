package com.perigrine3.createcybernetics.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.custom.RipperEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RipperModel<T extends RipperEntity> extends EntityModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "ripper"), "main");

    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart nose;
    private final ModelPart goggles;
    private final ModelPart leg0;
    private final ModelPart leg1;
    private final ModelPart rightArm;
    private final ModelPart ripperClaw;
    private final ModelPart rightItem;
    private final ModelPart leftArm;
    private final ModelPart leftItem;

    public RipperModel(ModelPart root) {
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.nose = this.head.getChild("nose");
        this.goggles = this.head.getChild("goggles");
        this.leg0 = this.body.getChild("leg0");
        this.leg1 = this.body.getChild("leg1");
        this.rightArm = this.body.getChild("rightArm");
        this.ripperClaw = this.rightArm.getChild("ripperClaw");
        this.rightItem = this.rightArm.getChild("rightItem");
        this.leftArm = this.body.getChild("leftArm");
        this.leftItem = this.leftArm.getChild("leftItem");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.5F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create()
                        .texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition goggles = head.addOrReplaceChild("goggles", CubeListBuilder.create()
                        .texOffs(32, 0).addBox(-1.0F, -1.5F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(32, 0).addBox(3.0F, -1.5F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(39, 1).addBox(1.0F, -1.0F, -0.5F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-2.0F, -6.0F, -4.0F));

        PartDefinition leg0 = body.addOrReplaceChild("leg0", CubeListBuilder.create()
                        .texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-2.0F, 12.0F, 0.0F));

        PartDefinition leg1 = body.addOrReplaceChild("leg1", CubeListBuilder.create()
                        .texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(2.0F, 12.0F, 0.0F));

        PartDefinition rightArm = body.addOrReplaceChild("rightArm", CubeListBuilder.create()
                        .texOffs(32, 46).mirror().addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                        .texOffs(48, 46).mirror().addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(-5.0F, 2.0F, 0.0F));

        PartDefinition ripperClaw = rightArm.addOrReplaceChild("ripperClaw", CubeListBuilder.create()
                        .texOffs(55, 28).addBox(-3.0F, -4.0F, 1.0F, 3.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-2.0F, 8.0F, -1.5F));

        ripperClaw.addOrReplaceChild("cube_r1", CubeListBuilder.create()
                        .texOffs(56, 32).addBox(-2.0F, 0.0F, 0.0F, 2.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.1309F, 0.0F, -0.1745F));

        ripperClaw.addOrReplaceChild("cube_r2", CubeListBuilder.create()
                        .texOffs(56, 31).addBox(-2.0F, -1.0F, 0.0F, 2.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.0436F, 0.0F, -0.1745F));

        ripperClaw.addOrReplaceChild("cube_r3", CubeListBuilder.create()
                        .texOffs(56, 31).addBox(-2.0F, -1.0F, 0.0F, 2.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0873F, 0.0F, -0.1745F));

        PartDefinition rightItem = rightArm.addOrReplaceChild("rightItem", CubeListBuilder.create(),
                PartPose.offset(-0.5F, 6.0F, 0.5F));

        PartDefinition leftArm = body.addOrReplaceChild("leftArm", CubeListBuilder.create()
                        .texOffs(32, 46).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                        .texOffs(48, 46).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        PartDefinition leftItem = leftArm.addOrReplaceChild("leftItem", CubeListBuilder.create(),
                PartPose.offset(1.0F, 7.0F, 1.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        body.yRot = 0.0F;
        body.xRot = 0.0F;

        head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        head.xRot = headPitch * ((float)Math.PI / 180F);

        leg0.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        leg1.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;

        rightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.2F * limbSwingAmount;
        leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 1.2F * limbSwingAmount;

        rightArm.yRot = 0.0F;
        leftArm.yRot = 0.0F;
        rightArm.zRot = 0.0F;
        leftArm.zRot = 0.0F;

        if (entity.isAggressive()) {
            rightArm.xRot = -1.1F;
            leftArm.xRot = -1.1F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}