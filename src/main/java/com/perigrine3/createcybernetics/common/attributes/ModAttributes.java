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

    public static final DeferredHolder<Attribute, Attribute> XP_GAIN_MULTIPLIER =
            ATTRIBUTES.register("xp_gain_multiplier", () -> new RangedAttribute("attribute." + CreateCybernetics.MODID + ".xp_gain_multiplier",
                    1.0D, 0.0D, 16.0D).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> ORE_DROP_MULTIPLIER =
            ATTRIBUTES.register("ore_drop_multiplier", () -> new RangedAttribute("attribute." + CreateCybernetics.MODID + ".ore_drop_multiplier",
                    1.0D, 0.0D, 16.0D).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> HAGGLING =
            ATTRIBUTES.register("haggling", () -> new RangedAttribute("attribute." + CreateCybernetics.MODID + ".haggling",
                    1.0D, 0.0D, 2.0D).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> ENDER_PEARL_DAMAGE =
            ATTRIBUTES.register("ender_pearl_damage", () -> new RangedAttribute("attribute." + CreateCybernetics.MODID + ".ender_pearl_damage",
                    1.0D, 0.0D, 16.0D).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> ARROW_INACCURACY =
            ATTRIBUTES.register("arrow_inaccuracy", () -> new RangedAttribute("attribute." + CreateCybernetics.MODID + ".arrow_inaccuracy",
                    1.0D, 0.0D, 16.0D).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> BREEDING_MULTIPLIER =
            ATTRIBUTES.register("breeding_multiplier", () -> new RangedAttribute("attribute." + CreateCybernetics.MODID + ".breeding_multiplier",
                    1.0D, 0.0D, 16.0D).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> CROP_MULTIPLIER =
            ATTRIBUTES.register("crop_multiplier", () -> new RangedAttribute("attribute." + CreateCybernetics.MODID + ".crop_multiplier",
                    1.0D, 0.0D, 16.0D).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> ELYTRA_SPEED =
            ATTRIBUTES.register("elytra_speed", () -> new RangedAttribute("attribute." + CreateCybernetics.MODID + ".elytra_speed",
                    1.0D, 0.0D, 16.0D).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> ELYTRA_HANDLING =
            ATTRIBUTES.register("elytra_handling", () -> new RangedAttribute("attribute." + CreateCybernetics.MODID + ".elytra_handling",
                    1.0D, 0.0D, 16.0D).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> INSOMNIA =
            ATTRIBUTES.register("insomnia", () -> new RangedAttribute("attribute." + CreateCybernetics.MODID + ".insomnia",
                    3.0D, 0.0D, 16.0D).setSyncable(true));



    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}
