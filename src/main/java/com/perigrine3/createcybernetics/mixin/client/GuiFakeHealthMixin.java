package com.perigrine3.createcybernetics.mixin.client;

import com.perigrine3.createcybernetics.client.CyberpsychosisFakeDamageController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Gui.class)
public abstract class GuiFakeHealthMixin {

    @ModifyArg(method = {"renderHealthLevel", "renderPlayerHealth"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHearts(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;IIIIFIIIZ)V"), index = 7, require = 0)
    private int createcybernetics$replaceCurrentHealth(int originalHealth) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null || !CyberpsychosisFakeDamageController.hasFakeDamage()) return originalHealth;

        return CyberpsychosisFakeDamageController.getDisplayedHealthCeil(player.getHealth());
    }

    @ModifyArg(method = {"renderHealthLevel", "renderPlayerHealth"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHearts(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;IIIIFIIIZ)V"), index = 8, require = 0)
    private int createcybernetics$replaceDisplayedHealth(int originalHealth) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null || !CyberpsychosisFakeDamageController.hasFakeDamage()) return originalHealth;

        return CyberpsychosisFakeDamageController.getDisplayedHealthCeil(player.getHealth());
    }
}