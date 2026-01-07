package com.perigrine3.createcybernetics.mixin.client;

import com.perigrine3.createcybernetics.common.attributes.ModAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractContainerScreen.class)
public abstract class CraftingResultCountMixin {

    @ModifyVariable(method = "renderSlotContents(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/inventory/Slot;Ljava/lang/String;)V", at =
    @At("HEAD"), argsOnly = true, index = 4)
    private String cc$overrideCraftingResultCountString(String countString, net.minecraft.client.gui.GuiGraphics guiGraphics, ItemStack stack, Slot slot) {
        if (countString != null && !countString.isEmpty()) return countString;

        if (!(slot instanceof ResultSlot)) return countString;
        if (stack.isEmpty()) return countString;

        Player player = Minecraft.getInstance().player;
        if (player == null) return countString;

        double mult = player.getAttributeValue(ModAttributes.CRAFTING_OUTPUT);
        if (!Double.isFinite(mult) || mult <= 1.0D) return countString;

        int base = stack.getCount();
        if (base <= 0) return countString;

        int total = (int) Math.floor(base * mult);
        if (total <= base) return countString;

        int max = stack.getMaxStackSize();
        if (total > max) return max + "+";

        return Integer.toString(total);
    }
}
