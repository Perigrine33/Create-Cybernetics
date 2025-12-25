package com.perigrine3.createcybernetics.common.effects;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class CyberwareTickDispatcher {

    private CyberwareTickDispatcher() {}

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);

        Set<Item> ticked = new HashSet<>();

        for (Map.Entry<?, InstalledCyberware[]> entry : data.getAll().entrySet()) {
            InstalledCyberware[] installedArray = entry.getValue();
            if (installedArray == null) continue;

            for (InstalledCyberware installed : installedArray) {
                if (installed == null) continue;

                ItemStack stack = installed.getItem();
                if (stack.isEmpty()) continue;

                Item item = stack.getItem();
                if (!(item instanceof ICyberwareItem cyberItem)) continue;

                if (ticked.add(item)) {
                    cyberItem.onTick(player);
                }
            }
        }
    }
}
