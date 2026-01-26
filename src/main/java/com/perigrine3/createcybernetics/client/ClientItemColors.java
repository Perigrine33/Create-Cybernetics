package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientItemColors {
    private ClientItemColors() {}

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex != 1) return 0xFFFFFFFF;

            DyedItemColor dyed = stack.get(DataComponents.DYED_COLOR);
            if (dyed == null) {
                return 0x00FFFFFF;
            }

            return 0xFF000000 | (dyed.rgb() & 0x00FFFFFF);
        },

            //DYEABLE ITEMS
                ModItems.BASECYBERWARE_LEFTARM.get(),
                ModItems.BASECYBERWARE_RIGHTARM.get(),
                ModItems.BASECYBERWARE_LEFTLEG.get(),
                ModItems.BASECYBERWARE_RIGHTLEG.get(),
                ModItems.BASECYBERWARE_CYBEREYES.get(),
                ModItems.SKINUPGRADES_METALPLATING.get(),
                ModItems.LEGUPGRADES_OCELOTPAWS.get()
        );
    }
}
