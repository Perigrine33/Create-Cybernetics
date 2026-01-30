package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class CCItemColors {
    private CCItemColors() {}

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(CCItemColors::tintCyberwareTrimMask,
                ModItems.BASECYBERWARE_LEFTLEG.get(),
                ModItems.BASECYBERWARE_RIGHTLEG.get(),
                ModItems.BASECYBERWARE_LEFTARM.get(),
                ModItems.BASECYBERWARE_RIGHTARM.get()
        );
    }

    private static int tintCyberwareTrimMask(ItemStack stack, int tintIndex) {
        if (tintIndex != 1) return -1;

        ArmorTrim trim = stack.get(DataComponents.TRIM);
        if (trim == null) return -1;

        int argb = TrimColorPresets.colorFor(trim.material());
        return argb & 0x00FFFFFF;
    }
}
