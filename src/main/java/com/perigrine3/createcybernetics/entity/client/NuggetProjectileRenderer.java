package com.perigrine3.createcybernetics.entity.client;

import com.perigrine3.createcybernetics.entity.projectile.NuggetProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class NuggetProjectileRenderer extends ThrownItemRenderer<NuggetProjectile> {

    public NuggetProjectileRenderer(EntityRendererProvider.Context ctx) {
        // scale = 0.5f makes it half-size
        super(ctx, 0.5f, false);
    }
}
