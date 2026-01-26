package com.perigrine3.createcybernetics.advancement;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.advancement.triggers.*;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCriteria {
    private ModCriteria() {}

    public static final DeferredRegister<CriterionTrigger<?>> TRIGGER_TYPES =
            DeferredRegister.create(Registries.TRIGGER_TYPE, CreateCybernetics.MODID);

    public static final DeferredHolder<CriterionTrigger<?>, FirstScavengedCyberwareTrigger> FIRST_SCAVENGED_CYBERWARE =
            TRIGGER_TYPES.register("first_scavenged_cyberware", FirstScavengedCyberwareTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, FirstCyberwareTrigger> FIRST_CYBERWARE =
            TRIGGER_TYPES.register("first_cyberware", FirstCyberwareTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, FirstRipperdocVisitTrigger> FIRST_RIPPERDOC_VISIT =
            TRIGGER_TYPES.register("first_ripperdoc_visit", FirstRipperdocVisitTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, CyberpsychosisTrigger> CYBERPSYCHOSIS =
            TRIGGER_TYPES.register("cyberpsychosis", CyberpsychosisTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ChromeJunkieTrigger> CHROME_JUNKIE =
            TRIGGER_TYPES.register("chrome_junkie", ChromeJunkieTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, WeakFleshTrigger> WEAK_FLESH =
            TRIGGER_TYPES.register("weak_flesh", WeakFleshTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, DeusExMachinaTrigger> DEUS_EX_MACHINA =
            TRIGGER_TYPES.register("deus_ex_machina", DeusExMachinaTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, SpiderManTrigger> SPIDER_MAN =
            TRIGGER_TYPES.register("spider_man", SpiderManTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, CorticalStackTrigger> CORTICAL_STACK =
            TRIGGER_TYPES.register("cortical_stack", CorticalStackTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, CogitoErgoSumTrigger> COGITO_ERGO_SUM =
            TRIGGER_TYPES.register("cogito_ergo_sum", CogitoErgoSumTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, BonesAndAllTrigger> BONES_AND_ALL =
            TRIGGER_TYPES.register("bones_and_all", BonesAndAllTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, TooMuchTooFastTrigger> TOO_MUCH_TOO_FAST =
            TRIGGER_TYPES.register("too_much_too_fast", TooMuchTooFastTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, OverTheEdgeTrigger> OVER_THE_EDGE =
            TRIGGER_TYPES.register("over_the_edge", OverTheEdgeTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ThoughtlessTrigger> THOUGHTLESS =
            TRIGGER_TYPES.register("thoughtless", ThoughtlessTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ChestPainsTrigger> CHEST_PAINS =
            TRIGGER_TYPES.register("chest_pains", ChestPainsTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, LiverRemovedTrigger> LIVER_REMOVED =
            TRIGGER_TYPES.register("liver_removed", LiverRemovedTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, SkinRemovedTrigger> SKIN_REMOVED =
            TRIGGER_TYPES.register("skin_removed", SkinRemovedTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, LungsRemovedTrigger> LUNGS_REMOVED =
            TRIGGER_TYPES.register("lungs_removed", LungsRemovedTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, BonelessTrigger> BONELESS =
            TRIGGER_TYPES.register("boneless", BonelessTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, MissingMuscleTrigger> MISSING_MUSCLE =
            TRIGGER_TYPES.register("missing_muscle", MissingMuscleTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, FleshWoundTrigger> FLESH_WOUND =
            TRIGGER_TYPES.register("missing_limbs", FleshWoundTrigger::new);


    public static final DeferredHolder<CriterionTrigger<?>, DavidMartinezSpecialTrigger> DAVID_SPECIAL =
            TRIGGER_TYPES.register("david_special", DavidMartinezSpecialTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, KungFuTrigger> KUNG_FU =
            TRIGGER_TYPES.register("kung_fu", KungFuTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, UpgradedTrigger> UPGRADED =
            TRIGGER_TYPES.register("upgraded", UpgradedTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, SniktTrigger> SNIKT =
            TRIGGER_TYPES.register("snikt", SniktTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ThoughtsNotFoundTrigger> THOUGHTS_NOT_FOUND =
            TRIGGER_TYPES.register("thoughts_not_found", ThoughtsNotFoundTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, PrettyInPinkTrigger> PRETTY_IN_PINK =
            TRIGGER_TYPES.register("pretty_in_pink", PrettyInPinkTrigger::new);

    public static void register(IEventBus modEventBus) {
        TRIGGER_TYPES.register(modEventBus);
    }
}
