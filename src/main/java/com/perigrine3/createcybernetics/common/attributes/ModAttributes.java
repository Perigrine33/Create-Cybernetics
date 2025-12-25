package com.perigrine3.createcybernetics.common.attributes;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModAttributes {
    private ModAttributes() {}

    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, CreateCybernetics.MODID);






    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}
