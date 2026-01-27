package com.perigrine3.createcybernetics.compat.stellaris;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber
public final class StellarisCompatEvents {
    private StellarisCompatEvents() {}


    private static boolean isCopernicusSetActive(PlayerCyberwareData data) {
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM) && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM) &&
                data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG) && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG) &&
                data.hasSpecificItem(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN) && data.hasSpecificItem(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get(), CyberwareSlot.MUSCLE) &&
                data.hasSpecificItem(ModItems.HEARTUPGRADES_CYBERHEART.get(), CyberwareSlot.HEART) && data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE) &&
                data.hasMultipleSpecificItem(ModItems.LUNGSUPGRADES_OXYGEN.get(), CyberwareSlot.LUNGS, 3)) {
            return true;

        } else {

            return false;
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!StellarisCompat.isLoaded()) return;

        Level level = player.level();
        if (!StellarisCompat.isStellarisLevel(level)) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;
        if (!isCopernicusSetActive(data)) return;

        if (player.getAirSupply() < player.getMaxAirSupply()) {
            player.setAirSupply(player.getMaxAirSupply());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onUseItemStart(LivingEntityUseItemEvent.Start event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!StellarisCompat.isLoaded()) return;
        if (!StellarisCompat.isStellarisLevel(player.level())) return;

        ItemStack stack = event.getItem();
        if (stack.isEmpty()) return;
        if (stack.getFoodProperties(player) == null) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;
        if (!isCopernicusSetActive(data)) return;

        if (event.isCanceled()) event.setCanceled(false);
    }
}
