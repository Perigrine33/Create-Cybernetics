package com.perigrine3.createcybernetics.mixin.client;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
    @Accessor("leftPos") int cc$getLeftPos();
    @Accessor("topPos") int cc$getTopPos();
    @Accessor("menu") AbstractContainerMenu cc$getMenu();
}
