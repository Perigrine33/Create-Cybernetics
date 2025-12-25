package com.perigrine3.createcybernetics.common.effects;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.item.cyberware.OcelotPawsItem;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.VanillaGameEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class OcelotPawsVibrationHandler {
    private OcelotPawsVibrationHandler() {}

    @SubscribeEvent
    public static void onVanillaGameEvent(VanillaGameEvent event) {
        if (!(event.getCause() instanceof Player player)) return;

        if (OcelotPawsItem.shouldSuppressVibration(player, event.getVanillaEvent(), event.getContext())) {
            event.setCanceled(true);
        }
    }
}
