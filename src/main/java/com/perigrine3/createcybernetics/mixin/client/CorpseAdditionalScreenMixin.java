package com.perigrine3.createcybernetics.mixin.client;

import com.perigrine3.createcybernetics.compat.corpse.CorpseCompat;
import com.perigrine3.createcybernetics.compat.corpse.OpenCorpseCyberwarePayload;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Pseudo
@Mixin(targets = "de.maxhenkel.corpse.gui.CorpseAdditionalScreen")
public abstract class CorpseAdditionalScreenMixin {

    @Unique
    private static final String CORPSE_ADDITIONAL_SCREEN_CLASS = "de.maxhenkel.corpse.gui.CorpseAdditionalScreen";

    @Unique
    private static Field createcybernetics$corpseField;
    @Unique
    private static boolean createcybernetics$corpseFieldResolved = false;

    @Unique
    private static final Component CREATECYBERNETICS$CYBERWARE_TEXT =
            Component.translatable("gui.createcybernetics.corpse_cyberware_button");

    @Dynamic
    @Inject(method = "init", at = @At("TAIL"))
    private void createcybernetics$addCyberwareButton(CallbackInfo ci) {
        if (!CorpseCompat.isLoaded()) return;

        Object corpse = createcybernetics$getCorpse();
        if (!(corpse instanceof net.minecraft.world.entity.Entity corpseEntity)) return;

        Object screen = this;

        if (!(screen instanceof ScreenInvoker invoker)) return;
        if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) return;

        int left = containerScreen.getGuiLeft();
        int top = containerScreen.getGuiTop();

        invoker.cc$invokeAddRenderableWidget(
                Button.builder(
                                CREATECYBERNETICS$CYBERWARE_TEXT,
                                b -> PacketDistributor.sendToServer(
                                        new OpenCorpseCyberwarePayload(corpseEntity.getUUID())
                                )
                        )
                        .bounds(left + 63, top + 142, 50, 20)
                        .build()
        );
    }

    @Unique
    private Object createcybernetics$getCorpse() {
        Field field = createcybernetics$getCorpseField();
        if (field == null) return null;

        try {
            return field.get(this);
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Unique
    private static Field createcybernetics$getCorpseField() {
        if (createcybernetics$corpseFieldResolved) {
            return createcybernetics$corpseField;
        }

        createcybernetics$corpseFieldResolved = true;

        try {
            Class<?> screenClass = Class.forName(CORPSE_ADDITIONAL_SCREEN_CLASS);
            createcybernetics$corpseField = screenClass.getDeclaredField("corpse");
            createcybernetics$corpseField.setAccessible(true);
        } catch (Throwable ignored) {
            createcybernetics$corpseField = null;
        }

        return createcybernetics$corpseField;
    }
}