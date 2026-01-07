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

public final class SpursAttachmentModel extends Model {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("createcybernetics", "spurs"), "main");

    private final ModelPart bone;

    public SpursAttachmentModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.bone = root.getChild("spur");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition spur = root.addOrReplaceChild("spur", CubeListBuilder.create()

                .texOffs(0, 0).addBox(0.0F, -4.0F, -1.0F,
                        0.0F, 4.0F, 5.0F, new CubeDeformation(-0.75F)),
                PartPose.offset(-0.8F, 10.0F, 2F));

        spur.getChild("spur");

        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, int color) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
