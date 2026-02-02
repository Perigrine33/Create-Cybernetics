package com.perigrine3.createcybernetics.network.handler;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.cyberware.HeatEngineItem;
import com.perigrine3.createcybernetics.network.payload.OpenHeatEnginePayload;
import com.perigrine3.createcybernetics.screen.custom.HeatEngineMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;

public final class OpenHeatEnginePayloadHandler {
    private OpenHeatEnginePayloadHandler() {}

    public static void handle(OpenHeatEnginePayload payload, ServerPlayer player) {
        if (player == null) return;

        if (!player.hasData(ModAttachments.CYBERWARE)) return;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        // Only open if Heat Engine is installed in ORGANS
        if (!hasHeatEngineInstalled(data)) return;

        player.openMenu(new SimpleMenuProvider(
                (containerId, inv, p) -> new HeatEngineMenu(containerId, inv),
                Component.translatable("gui.heatengine.title")
        ));
    }

    private static boolean hasHeatEngineInstalled(PlayerCyberwareData data) {
        InstalledCyberware[] arr = data.getAll().get(CyberwareSlot.ORGANS);
        if (arr == null) return false;

        for (InstalledCyberware inst : arr) {
            if (inst == null) continue;

            ItemStack st = inst.getItem();
            if (st == null || st.isEmpty()) continue;

            if (st.getItem() instanceof HeatEngineItem) {
                return true;
            }
        }

        return false;
    }
}
