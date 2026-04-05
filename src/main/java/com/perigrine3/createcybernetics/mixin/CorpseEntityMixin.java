package com.perigrine3.createcybernetics.mixin;

import com.perigrine3.createcybernetics.compat.corpse.CorpseCompat;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "de.maxhenkel.corpse.entities.CorpseEntity")
public abstract class CorpseEntityMixin {

    @Inject(method = "isEmpty", at = @At("HEAD"), cancellable = true)
    private void createcybernetics$includeCyberwareInEmptyCheck(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        if (CorpseCompat.hasCyberware(self)) {
            cir.setReturnValue(false);
        }
    }
}