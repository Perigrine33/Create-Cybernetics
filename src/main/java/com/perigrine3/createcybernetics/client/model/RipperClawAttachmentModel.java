package com.perigrine3.createcybernetics.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public final class RipperClawAttachmentModel extends Model {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "ripper_claw"), "main");

    private static final CubeDeformation BLADE_DEFORM = new CubeDeformation(0.005F);

    private final ModelPart ripperClaw;

    public RipperClawAttachmentModel(ModelPart root) {
        super((Function<ResourceLocation, RenderType>) RenderType::entityCutoutNoCull);
        this.ripperClaw = root.getChild("ripperClaw");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition ripperClaw = partdefinition.addOrReplaceChild("ripperClaw",
                CubeListBuilder.create()
                        .texOffs(3, 2)
                        .addBox(-3.0F, -4.0F, 1.0F, 3.0F, 9.0F, 0.0F, BLADE_DEFORM),
                PartPose.offset(-7.0F, 10.0F, -1.5F));

        ripperClaw.addOrReplaceChild("cube_r1",
                CubeListBuilder.create()
                        .texOffs(4, 6)
                        .addBox(-2.0F, 0.0F, 0.0F, 2.0F, 5.0F, 0.0F, BLADE_DEFORM),
                PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.1309F, 0.0F, -0.1745F));

        ripperClaw.addOrReplaceChild("cube_r2",
                CubeListBuilder.create()
                        .texOffs(4, 5)
                        .addBox(-2.0F, -1.0F, 0.0F, 2.0F, 6.0F, 0.0F, BLADE_DEFORM),
                PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.0436F, 0.0F, -0.1745F));

        ripperClaw.addOrReplaceChild("cube_r3",
                CubeListBuilder.create()
                        .texOffs(4, 5)
                        .addBox(-2.0F, -1.0F, 0.0F, 2.0F, 6.0F, 0.0F, BLADE_DEFORM),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0873F, 0.0F, -0.1745F));

        return LayerDefinition.create(meshdefinition, 12, 12);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, int color) {
        ripperClaw.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}