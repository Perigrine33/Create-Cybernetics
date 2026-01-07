package com.perigrine3.createcybernetics.mixin;

import com.perigrine3.createcybernetics.common.attributes.ModAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Projectile.class)
public abstract class ProjectileInaccuracyMixin {

    @ModifyArgs(method = "shootFromRotation(Lnet/minecraft/world/entity/Entity;FFFFF)V", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;shoot(DDDFF)V"))
    private void cc$scaleInaccuracyInShootFromRotation(Args args, Entity shooter, float xRot, float yRot, float roll, float velocity, float inaccuracy) {
        if (!((Object) this instanceof AbstractArrow)) return;

        if (!(shooter instanceof LivingEntity living)) return;

        double mult = living.getAttributeValue(ModAttributes.ARROW_INACCURACY);
        if (!Double.isFinite(mult) || mult < 0.0D) mult = 1.0D;

        float baseInacc = (float) args.get(4);
        float newInacc = (float) (baseInacc * mult);

        if (newInacc < 0.0F) newInacc = 0.0F;

        args.set(4, newInacc);
    }
}
