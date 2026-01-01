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

public final class DrillFistAttachmentModel extends Model {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("createcybernetics", "drill_fist"), "main");

    private final ModelPart bbMain;

    public DrillFistAttachmentModel(ModelPart root) {
        super((Function<ResourceLocation, RenderType>) RenderType::entityCutoutNoCull);
        this.bbMain = root.getChild("bb_main");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("bb_main",
                CubeListBuilder.create()
                        .texOffs(0, 7).addBox(-8.0F, -14.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-8.5F, -16.0F, -2.5F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 13).addBox(-7.5F, -12.0F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                        .texOffs(12, 13).addBox(-7.0F, -10.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(16, 7).addBox(-6.5F, -8.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, int color) {
        bbMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
