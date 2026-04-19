package com.perigrine3.createcybernetics.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

public final class CyberentityAttachmentLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    private final CyberentityAttachmentManager manager;

    public CyberentityAttachmentLayer(RenderLayerParent<T, M> parent, EntityRendererProvider.Context context) {
        super(parent);
        this.manager = new CyberentityAttachmentManager(context);
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            T entity,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        if (entity.isInvisible()) return;

        manager.renderAttachments(
                entity,
                this.getParentModel(),
                poseStack,
                buffer,
                packedLight,
                0
        );
    }
}