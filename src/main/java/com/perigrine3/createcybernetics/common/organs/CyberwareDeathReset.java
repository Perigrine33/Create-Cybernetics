package com.perigrine3.createcybernetics.common.organs;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class CyberwareDeathReset {

    private CyberwareDeathReset() {}

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        PlayerCyberwareData newData = player.getData(ModAttachments.CYBERWARE);
        if (newData == null) return;

        newData.resetToDefaultOrgans();
        newData.setDirty();
        player.syncData(ModAttachments.CYBERWARE);
    }

    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        if (!data.hasAnyInSlots(CyberwareSlot.BRAIN)) {
            data.resetToDefaultOrgans();
            data.setDirty();
        }

        player.syncData(ModAttachments.CYBERWARE);

    }

}
