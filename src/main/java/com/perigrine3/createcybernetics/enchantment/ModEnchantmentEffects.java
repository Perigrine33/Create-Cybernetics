package com.perigrine3.createcybernetics.enchantment;

import com.mojang.serialization.MapCodec;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.enchantment.custom.HarvesterEnchantmentEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEnchantmentEffects {

    public static final DeferredRegister<MapCodec<? extends EnchantmentEntityEffect>> ENTITY_ENCHANTMENT_EFFECTS =
            DeferredRegister.create(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, CreateCybernetics.MODID);

        public static final Supplier<MapCodec<? extends EnchantmentEntityEffect>> HARVESTER =
            ENTITY_ENCHANTMENT_EFFECTS.register("harvester", () -> HarvesterEnchantmentEffect.CODEC);


    public static void register(IEventBus eventBus) {
        ENTITY_ENCHANTMENT_EFFECTS.register(eventBus);
    }
}
