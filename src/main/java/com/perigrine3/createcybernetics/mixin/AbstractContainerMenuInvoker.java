package com.perigrine3.createcybernetics.mixin;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerMenu.class)
public interface AbstractContainerMenuInvoker {

    @Invoker("addSlot")
    Slot cc$invokeAddSlot(Slot slot);

    @Invoker("moveItemStackTo")
    boolean cc$invokeMoveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection);
}
