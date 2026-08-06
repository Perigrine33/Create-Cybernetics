package com.perigrine3.createcybernetics.mixin.client;

import com.perigrine3.createcybernetics.client.CyberpsychosisInventoryHallucination;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

    @Inject(method = "renderSlot", at = @At("HEAD"))
    private void createcybernetics$pushHallucinatedSlotOffset(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        guiGraphics.pose().pushPose();

        if (CyberpsychosisInventoryHallucination.hasRenderOffset(slot)) {
            guiGraphics.pose().translate(
                    CyberpsychosisInventoryHallucination.getRenderOffsetX(slot),
                    CyberpsychosisInventoryHallucination.getRenderOffsetY(slot),
                    0.0F
            );
        }
    }

    @Redirect(
            method = "renderSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/Slot;getItem()Lnet/minecraft/world/item/ItemStack;"
            )
    )
    private ItemStack createcybernetics$replaceHallucinatedRenderedStack(Slot slot) {
        ItemStack realStack = slot.getItem();
        return CyberpsychosisInventoryHallucination.getRenderedStack(slot, realStack);
    }

    @Inject(method = "renderSlot", at = @At("RETURN"))
    private void createcybernetics$popHallucinatedSlotOffset(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        guiGraphics.pose().popPose();
    }
}