package com.perigrine3.createcybernetics.mixin.client;

import com.perigrine3.createcybernetics.screen.slot.DataShardSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen$SlotWrapper")
public abstract class CreativeSlotWrapperHideChipwareMixin {

    @Shadow @Final Slot target;

    @Inject(method = "isActive", at = @At("HEAD"), cancellable = true)
    private void cc$hideChipwareSlotsInCreative(CallbackInfoReturnable<Boolean> cir) {
        if (!(Minecraft.getInstance().screen instanceof CreativeModeInventoryScreen cms) || !cms.isInventoryOpen()) {
            return;
        }

        if (this.target instanceof DataShardSlot) {
            cir.setReturnValue(false);
        }
    }
}
