package com.perigrine3.createcybernetics.event;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.event.custom.CyberwareSurgeryEvent;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.potion.ModPotions;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ModEvents {

    @SubscribeEvent
    public static void onBrewingRecipeRegister(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addMix(Potions.AWKWARD, ModItems.DATURA_FLOWER.get(), ModPotions.NEUROPOZYNE);
    }

    @SubscribeEvent
    public static void onCyberwareInstalled(CyberwareSurgeryEvent event) {

    }
}
