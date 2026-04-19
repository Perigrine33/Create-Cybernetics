package com.perigrine3.createcybernetics.network.handler;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.network.payload.OpenCyberdeckPayload;
import com.perigrine3.createcybernetics.screen.container.CyberdeckInventoryHandler;
import com.perigrine3.createcybernetics.screen.custom.CyberdeckMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class OpenCyberdeckPayloadHandler {
    private OpenCyberdeckPayloadHandler() {
    }

    public static void handle(OpenCyberdeckPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            if (!player.hasData(ModAttachments.CYBERWARE)) {
                return;
            }

            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) {
                return;
            }

            if (!data.hasSpecificItem(ModItems.BRAINUPGRADES_CYBERDECK.get(), CyberwareSlot.BRAIN)) {
                return;
            }

            player.openMenu(new SimpleMenuProvider(
                    (containerId, playerInventory, ignoredPlayer) ->
                            new CyberdeckMenu(containerId, playerInventory, new CyberdeckInventoryHandler(data)),
                    Component.literal("Cyberdeck")
            ));
        });
    }
}