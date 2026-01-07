package com.perigrine3.createcybernetics.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public final class CalfPropellerAttachmentModel extends Model {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("createcybernetics", "calf_propeller"), "main");

    private final ModelPart bone;

    public CalfPropellerAttachmentModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        //PROPELLER
        root.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-1.5F, -6.0F, -1.5F, 3.0F, 6.0F, 3.0F,
                        new CubeDeformation(-0.4F))
                .texOffs(-3, 9).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 0.0F, 3.0F,
                        new CubeDeformation(-0.4F))
                .texOffs(3, 9).addBox(-1.5F, -0.075F, -1.5F, 3.0F, 0.0F, 3.0F,
                        new CubeDeformation(-0.5F)),

                PartPose.offset(0.0F, 25.0F, 0.0F));

        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, int color) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
