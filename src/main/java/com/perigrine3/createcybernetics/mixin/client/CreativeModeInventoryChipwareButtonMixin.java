package com.perigrine3.createcybernetics.mixin.client;

import com.perigrine3.createcybernetics.network.payload.OpenChipwareMiniPayload;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryChipwareButtonMixin
        extends AbstractContainerScreen<CreativeModeInventoryScreen.ItemPickerMenu> {

    @Unique private Button cc$chipwareBtn;

    protected CreativeModeInventoryChipwareButtonMixin(CreativeModeInventoryScreen.ItemPickerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void cc$addChipwareButton(CallbackInfo ci) {
        int x = this.leftPos + 127;
        int y = this.topPos + 7;

        this.cc$chipwareBtn = Button.builder(
                        Component.literal("Chipware"),
                        b -> PacketDistributor.sendToServer(new OpenChipwareMiniPayload()))
                .bounds(x, y, 48, 15).build();

        this.addRenderableWidget(this.cc$chipwareBtn);

        cc$updateChipwareButtonVisibility();
    }

    @Inject(method = "containerTick", at = @At("TAIL"))
    private void cc$tickButtonVisibility(CallbackInfo ci) {
        cc$updateChipwareButtonVisibility();
    }

    @Unique
    private void cc$updateChipwareButtonVisibility() {
        if (this.cc$chipwareBtn == null) return;

        boolean open = ((CreativeModeInventoryScreen)(Object)this).isInventoryOpen();
        this.cc$chipwareBtn.visible = open;
        this.cc$chipwareBtn.active = open;
    }
}
