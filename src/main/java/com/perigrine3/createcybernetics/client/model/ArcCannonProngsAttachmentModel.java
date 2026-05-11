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

public final class ArcCannonProngsAttachmentModel extends Model {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "arc_cannon_prongs"), "main");

    private final ModelPart bbMain;

    public ArcCannonProngsAttachmentModel(ModelPart root) {
        super((Function<ResourceLocation, RenderType>) RenderType::entityCutoutNoCull);
        this.bbMain = root.getChild("bb_main");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(1, 3)
                        .addBox(-6.0F, -13.0F, -2.5F, 0.0F, 3.0F, 5.0F,
                                new CubeDeformation(0.0F)),

                PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        bbMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}