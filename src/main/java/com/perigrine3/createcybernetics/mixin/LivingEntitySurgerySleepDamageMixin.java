package com.perigrine3.createcybernetics.mixin;

import com.perigrine3.createcybernetics.util.SurgerySleepHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntitySurgerySleepDamageMixin {

    @Redirect(
            method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;stopSleeping()V"
            )
    )
    private void createcybernetics$doNotWakeSurgeryPatientFromDamage(LivingEntity entity, DamageSource source, float amount) {
        if (SurgerySleepHelper.isSleepingOnSurgeryTable(entity)) {
            return;
        }

        entity.stopSleeping();
    }
}