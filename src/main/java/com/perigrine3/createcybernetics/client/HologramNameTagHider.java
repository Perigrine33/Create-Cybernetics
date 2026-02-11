package com.perigrine3.createcybernetics.event.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.common.util.TriState;

@EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class HologramNameTagHider {

    private HologramNameTagHider() {}

    @SubscribeEvent
    public static void onRenderNameTag(RenderNameTagEvent event) {
        if (event == null) return;
        if (event.getEntity() == null) return;

        // Only hide nameplates for hologram RemotePlayers we tagged with HOLO_SNAPSHOT_FLAG
        if (event.getEntity().getPersistentData().getBoolean(PlayerCyberwareData.HOLO_SNAPSHOT_FLAG)) {
            event.setCanRender(TriState.FALSE);
        }
    }
}
