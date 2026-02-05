package com.perigrine3.createcybernetics.datagen;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.advancement.triggers.*;
import com.perigrine3.createcybernetics.block.ModBlocks;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class ModAdvancementProvider extends AdvancementProvider {

    public ModAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, existingFileHelper, List.of(new DashAdvancements()));
    }

    private static final class DashAdvancements implements AdvancementProvider.AdvancementGenerator {

        private static final ResourceLocation MACHINE_BG =
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/block/blankmachine.png");

        private static final ResourceLocation TITANIUM_BG =
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/block/titanium_block.png");

        private static final ResourceLocation RAW_TITANIUM_BG =
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/block/raw_titanium_block.png");

        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
            AdvancementHolder root = Advancement.Builder.advancement().display(ModItems.BASECYBERWARE_CYBEREYES,
                            Component.translatable("advancement.cybernetics"),
                            Component.translatable("advancement.cybernetics.desc"),
                            MACHINE_BG, AdvancementType.TASK, true, true, false)
                    .addCriterion("tick", net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance.tick())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/root"), existingFileHelper);

            AdvancementHolder firstScavenged = Advancement.Builder.advancement().parent(root).display(ModItems.SCAVENGED_LEFTARM,
                            Component.translatable("advancement.first_scavenged_cyberware"),
                            Component.translatable("advancement.first_scavenged_cyberware.desc"),
                            MACHINE_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("first_scavenged_cyberware", FirstScavengedCyberwareTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/first_scavenged_cyberware"), existingFileHelper);

            AdvancementHolder firstCyberware = Advancement.Builder.advancement().parent(firstScavenged).display(ModItems.BASECYBERWARE_LEFTARM,
                            Component.translatable("advancement.first_cyberware"),
                            Component.translatable("advancement.first_cyberware.desc"),
                            MACHINE_BG, AdvancementType.TASK, true, true, false)
                    .addCriterion("first_cyberware", FirstCyberwareTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/first_cyberware"), existingFileHelper);

            AdvancementHolder firstRipperdocVisit = Advancement.Builder.advancement().parent(firstCyberware).display(ModBlocks.ROBOSURGEON.get(),
                            Component.translatable("advancement.first_ripperdoc_visit"),
                            Component.translatable("advancement.first_ripperdoc_visit.desc"),
                            MACHINE_BG, AdvancementType.TASK, true, true, false)
                    .addCriterion("first_ripperdoc_visit", FirstRipperdocVisitTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/first_ripperdoc_visit"), existingFileHelper);

            AdvancementHolder goingPsycho = Advancement.Builder.advancement().parent(firstRipperdocVisit).display(ModItems.ARMUPGRADES_CRAFTHANDS.get(),
                            Component.translatable("advancement.cyberpsychosis"),
                            Component.translatable("advancement.cyberpsychosis.desc"),
                            MACHINE_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("cyberpsychosis", CyberpsychosisTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/cyberpsychosis"), existingFileHelper);

            AdvancementHolder chromeJunkie = Advancement.Builder.advancement().parent(goingPsycho).display(ModItems.NEUROPOZYNE_AUTOINJECTOR.get(),
                            Component.translatable("advancement.chrome_junkie"),
                            Component.translatable("advancement.chrome_junkie.desc"),
                            MACHINE_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("chrome_junkie", ChromeJunkieTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/chrome_junkie"), existingFileHelper);

            AdvancementHolder psychoDeath = Advancement.Builder.advancement().parent(goingPsycho).display(ModBlocks.SURGERY_CHAMBER_BOTTOM.get().asItem(),
                            Component.translatable("advancement.over_the_edge"),
                            Component.translatable("advancement.over_the_edge.desc"),
                            MACHINE_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("over_the_edge", OverTheEdgeTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/over_the_edge"), existingFileHelper);

            AdvancementHolder fullBorg = Advancement.Builder.advancement().parent(firstRipperdocVisit).display(ModItems.SKINUPGRADES_METALPLATING.get(),
                            Component.translatable("advancement.weak_flesh"),
                            Component.translatable("advancement.weak_flesh.desc"),
                            MACHINE_BG, AdvancementType.TASK, true, true, false)
                    .addCriterion("weak_flesh", WeakFleshTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/weak_flesh"), existingFileHelper);

            AdvancementHolder survivedDeath = Advancement.Builder.advancement().parent(firstRipperdocVisit).display(ModItems.HEARTUPGRADES_DEFIBRILLATOR.get(),
                            Component.translatable("advancement.deus_ex_machina"),
                            Component.translatable("advancement.deus_ex_machina.desc"),
                            MACHINE_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("deus_ex_machina", DeusExMachinaTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/deus_ex_machina"), existingFileHelper);

            AdvancementHolder spiderMan = Advancement.Builder.advancement().parent(firstRipperdocVisit).display(Blocks.COBWEB.asItem(),
                            Component.translatable("advancement.spider_man"),
                            Component.translatable("advancement.spider_man.desc"),
                            MACHINE_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("spider_man", DeusExMachinaTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/spider_man"), existingFileHelper);

            AdvancementHolder corticalStack = Advancement.Builder.advancement().parent(firstRipperdocVisit).display(Blocks.COBWEB.asItem(),
                            Component.translatable("advancement.cortical_stack"),
                            Component.translatable("advancement.cortical_stack.desc"),
                            MACHINE_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("cortical_stack", CorticalStackTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/cortical_stack"), existingFileHelper);





            AdvancementHolder brainRemoved = Advancement.Builder.advancement().display(ModItems.BODYPART_BRAIN.get(),
                            Component.translatable("advancement.thoughtless"),
                            Component.translatable("advancement.thoughtless.desc"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("thoughtless", ThoughtlessTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/thoughtless"), existingFileHelper);

            AdvancementHolder heartRemoved = Advancement.Builder.advancement().parent(brainRemoved).display(ModItems.BODYPART_HEART.get(),
                            Component.translatable("advancement.chest_pains"),
                            Component.translatable("advancement.chest_pains.desc"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("chest_pains", ChestPainsTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/chest_pains"), existingFileHelper);

            AdvancementHolder liverRemoved = Advancement.Builder.advancement().parent(heartRemoved).display(ModItems.BODYPART_LIVER.get(),
                            Component.translatable("advancement.liver_removed"),
                            Component.translatable("advancement.liver_removed.desc"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("liver_removed", LiverRemovedTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/liver_removed"), existingFileHelper);

            AdvancementHolder skinRemoved = Advancement.Builder.advancement().parent(liverRemoved).display(ModItems.BODYPART_SKIN.get(),
                            Component.translatable("advancement.skin_removed"),
                            Component.translatable("advancement.skin_removed.desc"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("skin_removed", SkinRemovedTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/skin_removed"), existingFileHelper);

            AdvancementHolder lungsRemoved = Advancement.Builder.advancement().parent(skinRemoved).display(ModItems.BODYPART_LUNGS.get(),
                            Component.translatable("advancement.lungs_removed"),
                            Component.translatable("advancement.lungs_removed.desc"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("lungs_removed", LungsRemovedTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/lungs_removed"), existingFileHelper);

            AdvancementHolder skeletonRemoved = Advancement.Builder.advancement().parent(lungsRemoved).display(ModItems.BODYPART_SKELETON.get(),
                            Component.translatable("advancement.boneless"),
                            Component.translatable("advancement.boneless.desc"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("boneless", BonelessTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/boneless"), existingFileHelper);

            AdvancementHolder muscleRemoved = Advancement.Builder.advancement().parent(skeletonRemoved).display(ModItems.BODYPART_MUSCLE.get(),
                            Component.translatable("advancement.missing_muscle"),
                            Component.translatable("advancement.missing_muscle.desc"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("missing_muscle", MissingMuscleTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/missing_muscle"), existingFileHelper);

            AdvancementHolder limbsRemoved = Advancement.Builder.advancement().parent(muscleRemoved).display(ModItems.BODYPART_LEFTARM.get(),
                            Component.translatable("advancement.missing_limbs"),
                            Component.translatable("advancement.missing_limbs.desc"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("missing_limbs", FleshWoundTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/missing_limbs"), existingFileHelper);




            AdvancementHolder davidMartinez = Advancement.Builder.advancement().parent(firstRipperdocVisit).display(ModItems.BONEUPGRADES_SANDEVISTAN.get(),
                            Component.translatable("advancement.david_special"),
                            Component.translatable("advancement.david_special.desc"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("david_special", DavidMartinezSpecialTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/david_special"), existingFileHelper);

            AdvancementHolder kungFu = Advancement.Builder.advancement().parent(firstRipperdocVisit).display(ModItems.DATA_SHARD_RED.get(),
                            Component.translatable("advancement.kung_fu"),
                            Component.translatable("advancement.kung_fu.desc"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("kung_fu", KungFuTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/kung_fu"), existingFileHelper);

            AdvancementHolder armCannon = Advancement.Builder.advancement().parent(firstRipperdocVisit).display(ModItems.ARMUPGRADES_ARMCANNON.get(),
                            Component.translatable("advancement.upgraded"),
                            Component.translatable("advancement.upgraded.desc"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                        .addCriterion("upgraded", UpgradedTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/upgraded"), existingFileHelper);

            AdvancementHolder wolverineClaws = Advancement.Builder.advancement().parent(firstRipperdocVisit).display(ModItems.ARMUPGRADES_CLAWS.get(),
                            Component.translatable("advancement.snikt"),
                            Component.translatable("advancement.snikt.desc"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                        .addCriterion("snikt", SniktTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/snikt"), existingFileHelper);

            AdvancementHolder digitalBrain = Advancement.Builder.advancement().parent(firstRipperdocVisit).display(ModItems.BRAINUPGRADES_CYBERBRAIN.get(),
                            Component.translatable("advancement.cogito_ergo_sum"),
                            Component.translatable("advancement.cogito_ergo_sum.desc"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                        .addCriterion("cogito_ergo_sum", CogitoErgoSumTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/cogito_ergo_sum"), existingFileHelper);

            AdvancementHolder brainOff = Advancement.Builder.advancement().parent(digitalBrain).display(ModItems.BRAINUPGRADES_CYBERBRAIN.get(),
                            Component.translatable("advancement.thoughts_not_found"),
                            Component.translatable("advancement.thoughts_not_found.desc"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                        .addCriterion("thoughts_not_found", ThoughtsNotFoundTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/thoughts_not_found"), existingFileHelper);

            AdvancementHolder prettyPink = Advancement.Builder.advancement().parent(fullBorg).display(ModItems.BRAINUPGRADES_CYBERBRAIN.get(),
                            Component.translatable("advancement.pretty_in_pink"),
                            Component.translatable("advancement.pretty_in_pink.desc"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                        .addCriterion("pretty_in_pink", PrettyInPinkTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/pretty_in_pink"), existingFileHelper);

            AdvancementHolder letsDance = Advancement.Builder.advancement().parent(root).display(ModItems.BODYPART_BRAIN.get(),
                            Component.translatable("advancement.lets_dance"),
                            Component.translatable("advancement.lets_dance.desc"),
                            TITANIUM_BG, AdvancementType.TASK, true, true, false)
                        .addCriterion("lets_dance", LetsDanceTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/lets_dance"), existingFileHelper);




            AdvancementHolder boneMarrow = Advancement.Builder.advancement().display(ModItems.BONE_MARROW.get(),
                            Component.translatable("advancement.bones_and_all"),
                            Component.translatable("advancement.bones_and_all.desc"),
                            RAW_TITANIUM_BG, AdvancementType.TASK, true, false, false)
                        .addCriterion("bones_and_all", BonesAndAllTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/bones_and_all"), existingFileHelper);


        }
    }
}
