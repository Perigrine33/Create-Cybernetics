package com.perigrine3.createcybernetics.network.handler;

import com.perigrine3.createcybernetics.screen.container.ChipwareContainer;
import com.perigrine3.createcybernetics.screen.custom.ChipwareMiniMenu;
import com.perigrine3.createcybernetics.network.payload.OpenChipwareMiniPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class OpenChipwareMiniHandler {
    private OpenChipwareMiniHandler() {}

    public static void handle(OpenChipwareMiniPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sp)) return;
            if (!ChipwareMiniMenu.canOpen(sp)) return;

            MenuProvider provider = new SimpleMenuProvider((id, inv, player) -> new ChipwareMiniMenu(id, inv, new ChipwareContainer(player)),
                    Component.translatable("gui.chipwarecreative.title"));

            sp.openMenu(provider, buf -> {});
        });
    }
}
