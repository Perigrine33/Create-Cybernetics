package com.perigrine3.createcybernetics.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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

public final class ClawAttachmentModel extends Model {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("createcybernetics", "claws"), "main");

    private static final float THICK = 0.5F;
    private static final float HALF = THICK / 2.0F;

    private final ModelPart bone;

    public ClawAttachmentModel(ModelPart root) {
        super((Function<ResourceLocation, RenderType>) RenderType::entityCutoutNoCull);
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        //CENTER BLADE
        PartDefinition bone = root.addOrReplaceChild("bone",
                CubeListBuilder.create().texOffs(-2, -2)
                        .addBox(-HALF, 0, -4.3422F, 0, 8, 2, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0, 0, 0, 0F, 0F, 0.175F));
        //LEFT BLADE
        bone.addOrReplaceChild("right_claw",
                CubeListBuilder.create().texOffs(-2, -1)
                        .addBox(-HALF, 0, -4.3422F, 0, 7, 2, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1, 0, 0, 0F, 0F, 0.1F));
        //RIGHT BLADE
        bone.addOrReplaceChild("left_claw",
                CubeListBuilder.create().texOffs(-2, -1)
                        .addBox(-HALF, 0, -4.3422F, 0, 7, 2, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1, 0, 0, 0F, 0F, -0.1F));

        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, int color) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
