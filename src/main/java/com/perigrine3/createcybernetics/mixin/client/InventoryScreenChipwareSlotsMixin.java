package com.perigrine3.createcybernetics.mixin.client;

import com.perigrine3.createcybernetics.api.IChipwareSlotsMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenChipwareSlotsMixin {

    private static final int CC_SLOT_X = 77;
    private static final int CC_SLOT_Y0 = 8;
    private static final int CC_SLOT_SPACING = 18;

    @Inject(method = "renderBg(Lnet/minecraft/client/gui/GuiGraphics;FII)V", at = @At("TAIL"))
    private void cc$renderChipwareSlotBackgrounds(GuiGraphics gg, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
        var accessor = (AbstractContainerScreenAccessor) (Object) this;
        var menu = accessor.cc$getMenu();

        if (!(menu instanceof IChipwareSlotsMenu chip) || !chip.cc$chipwareSlotsActive()) return;

        int leftPos = accessor.cc$getLeftPos();
        int topPos = accessor.cc$getTopPos();

        int sx = leftPos + CC_SLOT_X;
        int sy0 = topPos + CC_SLOT_Y0;
        int sy1 = sy0 + CC_SLOT_SPACING;

        cc$drawSlotBackground(gg, sx, sy0);
        cc$drawSlotBackground(gg, sx, sy1);
    }

    private static void cc$drawSlotBackground(GuiGraphics gg, int x, int y) {
        int left = x - 1;
        int top = y - 1;
        int right = x + 17;
        int bottom = y + 17;

        gg.fill(left, top, right, bottom, 0xE005070A);

        gg.fill(left, top, right, top + 1, 0xFF4AB3FF);
        gg.fill(left, bottom - 1, right, bottom, 0xFF4AB3FF);
        gg.fill(left, top, left + 1, bottom, 0xFF4AB3FF);
        gg.fill(right - 1, top, right, bottom, 0xFF4AB3FF);
    }
}
