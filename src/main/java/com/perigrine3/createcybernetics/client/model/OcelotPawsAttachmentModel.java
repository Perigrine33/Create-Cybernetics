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

public final class OcelotPawsAttachmentModel extends Model {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("createcybernetics", "ocelot_paws"), "main");

    private final ModelPart bone;

    public OcelotPawsAttachmentModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.bone = root.getChild("pad");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition bb_main = root.addOrReplaceChild("pad",
                CubeListBuilder.create()

                        //BEAN 3
                        .texOffs(0, 3)
                        .addBox(-1.8F, -0.5F, -1.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))

                        //BEAN 2
                        .texOffs(4, 3)
                        .addBox(-0.5F, -0.5F, -2.25F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))

                        //BEAN 1
                        .texOffs(0, 5)
                        .addBox(0.8F, -0.5F, -1.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))

                        //PAD
                        .texOffs(0, 0)
                        .addBox(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation( 0.0F)),

                PartPose.offsetAndRotation(0.0F, -0.425F, 0.5F, 0.0F, 0.0F, 0.0F));

        bb_main.getChild("bb_main");

        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, int color) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
