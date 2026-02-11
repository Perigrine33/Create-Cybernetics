package com.perigrine3.createcybernetics.common.capabilities;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID)
public final class CyberwareAttachmentSyncEvents {

    private CyberwareAttachmentSyncEvents() {}

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        ModAttachments.syncCyberware(sp);
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        Entity target = event.getTarget();
        if (!(target instanceof ServerPlayer tracked)) return;
        ModAttachments.syncCyberware(tracked);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        if (sp.level().isClientSide) return;

        PlayerCyberwareData data = sp.getData(ModAttachments.CYBERWARE);
        if (data.isDirty()) {
            sp.syncData(ModAttachments.CYBERWARE);
            data.clean();
        }
    }
}
