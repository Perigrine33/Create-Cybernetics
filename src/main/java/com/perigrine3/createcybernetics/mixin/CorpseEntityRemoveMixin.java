package com.perigrine3.createcybernetics.mixin;

import com.perigrine3.createcybernetics.compat.corpse.CorpseCompat;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "de.maxhenkel.corpse.entities.CorpseEntity")
public abstract class CorpseEntityRemoveMixin {

    @Inject(method = "remove", at = @At("HEAD"))
    private void createcybernetics$dropCyberwareOnRemove(
            @Coerce Enum<?> reason,
            CallbackInfo ci
    ) {
        Entity self = (Entity) (Object) this;

        for (ItemStack stack : CorpseCompat.getCorpseCyberwareItems(self)) {
            if (stack == null || stack.isEmpty()) continue;
            Containers.dropItemStack(self.level(), self.getX(), self.getY(), self.getZ(), stack.copy());
        }

        CorpseCompat.writeCorpseCyberwareItems(
                self,
                net.minecraft.core.NonNullList.withSize(CorpseCompat.CYBERWARE_SLOT_COUNT, ItemStack.EMPTY)
        );
    }
}