package com.perigrine3.createcybernetics.mixin.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntityRenderer.class)
public interface LivingEntityRendererAccessor<T extends net.minecraft.world.entity.LivingEntity, M extends EntityModel<T>> {

    @Accessor("model")
    M createcybernetics$getModel();
}