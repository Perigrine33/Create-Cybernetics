package com.perigrine3.createcybernetics.common.effects;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.EntityCyberwareData;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.ModMobAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class CyberwareTickDispatcher {

    private CyberwareTickDispatcher() {}

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (!player.hasData(ModAttachments.CYBERWARE)) return;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        tickInstalledCyberware(player, data.getAll());
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (entity instanceof Player) return;

        if (!entity.hasData(ModMobAttachments.CYBERENTITY_CYBERWARE)) return;
        EntityCyberwareData data = entity.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
        if (data == null) return;

        tickInstalledCyberware(entity, data.getAll());
    }

    private static void tickInstalledCyberware(LivingEntity entity, java.util.Map<CyberwareSlot, InstalledCyberware[]> all) {
        for (var entry : all.entrySet()) {
            CyberwareSlot slot = entry.getKey();
            InstalledCyberware[] arr = entry.getValue();
            if (arr == null) continue;

            for (int index = 0; index < arr.length; index++) {
                InstalledCyberware installed = arr[index];
                if (installed == null) continue;

                ItemStack stack = installed.getItem();
                if (stack == null || stack.isEmpty()) continue;

                if (stack.getItem() instanceof ICyberwareItem cyberItem) {
                    cyberItem.onTick(entity, stack, slot, index);
                }
            }
        }
    }
}