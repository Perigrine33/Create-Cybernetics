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

public final class WardenAntlersAttachmentModel extends Model {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("createcybernetics", "warden_antlers"), "main");

    private final ModelPart bone;

    public WardenAntlersAttachmentModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.bone = root.getChild("warden_antlers");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();

        PartDefinition warden_antlers = part.addOrReplaceChild("warden_antlers", CubeListBuilder.create()

                .texOffs(0, 0).addBox(-14.0F, -10.0F, 0.0F,
                        28.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -3.0F, 0.0F));

        warden_antlers.getChild("warden_antlers");

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, int color) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
