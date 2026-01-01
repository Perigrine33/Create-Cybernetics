package com.perigrine3.createcybernetics.potion;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.effect.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(BuiltInRegistries.POTION, CreateCybernetics.MODID);

    public static final Holder<Potion> NEUROPOZYNE = POTIONS.register("neuropozyne_potion",
            () -> new Potion(new MobEffectInstance(ModEffects.NEUROPOZYNE, 3600, 0)));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}