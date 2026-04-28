package com.perigrine3.createcybernetics.entity.client.models;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.custom.HogBoyEntity;
import net.minecraft.client.model.HumanoidModel;
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

public class HogBoyModel<T extends HogBoyEntity> extends HumanoidModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "hogboy"),
            "main"
    );

    private final ModelPart leftear;
    private final ModelPart rightear;
    private final ModelPart glasses;
    private final ModelPart rightItem;

    public HogBoyModel(ModelPart root) {
        super(root);
        this.leftear = this.head.getChild("leftear");
        this.rightear = this.head.getChild("rightear");
        this.glasses = this.head.getChild("glasses");
        this.rightItem = this.rightArm.getChild("rightItem");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                        .texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, new CubeDeformation(-0.02F))
                        .texOffs(31, 1).addBox(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 1.0F, CubeDeformation.NONE)
                        .texOffs(2, 4).addBox(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                        .texOffs(2, 0).addBox(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                        .texOffs(0, 37).addBox(0.0F, -13.0F, -6.0F, 0.0F, 12.0F, 14.0F, CubeDeformation.NONE)
                        .texOffs(39, 59).addBox(-5.0F, -6.0F, -4.5F, 10.0F, 3.0F, 2.0F, CubeDeformation.NONE)
                        .texOffs(42, 2).addBox(-1.5F, -1.0F, -4.0F, 3.0F, 3.0F, 0.0F, CubeDeformation.NONE),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        head.addOrReplaceChild("leftear", CubeListBuilder.create()
                        .texOffs(39, 6).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, 0.0F, -0.5236F));

        head.addOrReplaceChild("rightear", CubeListBuilder.create()
                        .texOffs(50, 6).addBox(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(-5.0F, -6.0F, 0.0F, 0.0F, 0.0F, 0.5236F));

        head.addOrReplaceChild("glasses", CubeListBuilder.create(),
                PartPose.offset(1.0F, -5.0F, -4.5F));

        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition rightarm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
                        .texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offset(-5.0F, 2.0F, 0.0F));

        rightarm.addOrReplaceChild("rightItem", CubeListBuilder.create(),
                PartPose.offset(-1.0F, 7.0F, 1.0F));

        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
                        .texOffs(40, 32).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create()
                        .texOffs(0, 16).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offset(-1.9F, 12.0F, 0.0F));

        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        float earWiggle = Mth.cos(ageInTicks * 0.08F) * 0.04F;
        this.leftear.zRot = -0.5236F - earWiggle;
        this.rightear.zRot = 0.5236F + earWiggle;

        this.glasses.visible = true;
        this.rightItem.visible = true;
    }
}