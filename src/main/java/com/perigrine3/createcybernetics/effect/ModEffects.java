package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.print.attribute.Attribute;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, CreateCybernetics.MODID);

        public static final Holder<MobEffect> CYBERWARE_REJECTION = MOB_EFFECTS.register("cyberware_rejection",
                () -> new CyberwareRejectionEffect(MobEffectCategory.NEUTRAL, 0X36ebab)
                        .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware_rejection"), -0.25f,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));


    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
