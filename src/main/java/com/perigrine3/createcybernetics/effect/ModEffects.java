package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.item.cyberware.SandevistanItem;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, CreateCybernetics.MODID);

        public static final Holder<MobEffect> CYBERWARE_REJECTION = MOB_EFFECTS.register("cyberware_rejection",
                () -> new CyberwareRejectionEffect(MobEffectCategory.NEUTRAL, 0xA11F05)
                        .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware_rejection"), -0.17f,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        public static final Holder<MobEffect> NEUROPOZYNE = MOB_EFFECTS.register("neuropozyne",
                () -> new NeuropozyneEffect(MobEffectCategory.BENEFICIAL, 0xC4D925)
                        .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "neuropozyne_speed"), 0.001D,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        public static final Holder<MobEffect> EMP = MOB_EFFECTS.register("emp_effect",
                () -> new EmpEffect()
                        .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "emp_speed"), 0.0D,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

        public static final Holder<MobEffect> SYNTHETIC_SETULES_EFFECT = MOB_EFFECTS.register("synthetic_setules_effect",
                () -> new SyntheticSetulesEffect(MobEffectCategory.NEUTRAL, 0x000000)
                        .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "synthetic_setules_effect"), 0,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        public static final Holder<MobEffect> AEROSTASIS_GYROBLADDER_EFFECT = MOB_EFFECTS.register("aerostasis_gyrobladder_effect",
                () -> new AerostasisGyrobladderEffect(MobEffectCategory.NEUTRAL, 0x000000)
                        .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "aerostasis_gyrobladder_effect"), 0,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        public static final Holder<MobEffect> PNEUMATIC_CALVES_EFFECT = MOB_EFFECTS.register("pneumatic_calves_effect",
                () -> new PneumaticCalvesEffect(MobEffectCategory.NEUTRAL, 0x000000)
                        .addAttributeModifier(Attributes.JUMP_STRENGTH,
                                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "pneumatic_calves_effect"), 0.1,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        public static final Holder<MobEffect> SPURS_EFFECT = MOB_EFFECTS.register("spurs_effect",
                () -> new SpursEffect()
                        .addAttributeModifier(Attributes.JUMP_STRENGTH,
                                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "spurs_effect"), 0,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        public static final Holder<MobEffect> NEURAL_CONTEXTUALIZER_EFFECT = MOB_EFFECTS.register("neural_contextualizer_effect",
                () -> new NeuralContextualizerEffect(MobEffectCategory.NEUTRAL, 0x000000)
                        .addAttributeModifier(Attributes.JUMP_STRENGTH,
                                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "neural_contextualizer_effect"), 0,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        public static final Holder<MobEffect> SUBDERMAL_SPIKES_EFFECT = MOB_EFFECTS.register("subdermal_spikes_effect",
                () -> new SubdermalSpikesEffect()
                        .addAttributeModifier(Attributes.JUMP_STRENGTH,
                                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "subdermal_spikes_effect"), 0,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        public static final Holder<MobEffect> GUARDIAN_EYE_EFFECT = MOB_EFFECTS.register("guardian_eye_effect",
                () -> new GuardianEyeEffect(MobEffectCategory.NEUTRAL, 0x000000)
                        .addAttributeModifier(Attributes.JUMP_STRENGTH,
                                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "guardian_eye_effect"), 0,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        public static final Holder<MobEffect> PROJECTILE_DODGE_EFFECT = MOB_EFFECTS.register("projectile_dodge_effect",
                () -> new ProjectileDodgeEffect()
                        .addAttributeModifier(Attributes.JUMP_STRENGTH,
                                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "projectile_dodge_effect"), 0,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        public static final Holder<MobEffect> SCULK_LUNGS_EFFECT = MOB_EFFECTS.register("sculk_lungs_effect",
                () -> new SculkLungsEffect(MobEffectCategory.NEUTRAL, 0x000000)
                        .addAttributeModifier(Attributes.JUMP_STRENGTH,
                                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "sculk_lungs_effect"), 0,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        public static final Holder<MobEffect> INKED_EFFECT = MOB_EFFECTS.register("inked_effect",
                () -> new InkedEffect()
                        .addAttributeModifier(Attributes.JUMP_STRENGTH,
                                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "inked_effect"), 0,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        public static final Holder<MobEffect> BREATHLESS_EFFECT = MOB_EFFECTS.register("breathless_effect",
                () -> new BreathlessEffect()
                        .addAttributeModifier(Attributes.JUMP_STRENGTH,
                                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "breathless_effect"), 0,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        public static final Holder<MobEffect> SANDEVISTAN_EFFECT = MOB_EFFECTS.register("sandevistan_effect",
                () -> new SandevistanEffect());


    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
