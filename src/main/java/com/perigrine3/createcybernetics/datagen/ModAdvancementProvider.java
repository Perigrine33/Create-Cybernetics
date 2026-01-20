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

        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
            Advancement.Builder.advancement().display(ModItems.SCAVENGED_LEFTARM,
                            Component.literal("I Don't Think I Want To Put That In My Body..."),
                            Component.literal("Obtained Scavenged Cyberware"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("first_scavenged_cyberware", FirstScavengedCyberwareTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/first_scavenged_cyberware"), existingFileHelper);

            Advancement.Builder.advancement().display(ModItems.BASECYBERWARE_CYBEREYES,
                            Component.literal("Time To Chrome"),
                            Component.literal("Obtained Cyberware"),
                            null, AdvancementType.TASK, true, true, false)
                    .addCriterion("first_cyberware", FirstCyberwareTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/first_cyberware"), existingFileHelper);

            Advancement.Builder.advancement().display(ModBlocks.ROBOSURGEON.get(),
                            Component.literal("I Want This Fused Here"),
                            Component.literal("Installed Cyberware"),
                            null, AdvancementType.TASK, true, true, false)
                    .addCriterion("first_ripperdoc_visit", FirstRipperdocVisitTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/first_ripperdoc_visit"), existingFileHelper);

            Advancement.Builder.advancement().display(ModItems.ARMUPGRADES_CRAFTHANDS.get(),
                            Component.literal("Best Lift Ya Pedal Off The Metal, Choom!"),
                            Component.literal("Incurred Cyberware Rejection"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("cyberpsychosis", CyberpsychosisTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/cyberpsychosis"), existingFileHelper);

            Advancement.Builder.advancement().display(ModItems.NEUROPOZYNE_AUTOINJECTOR.get(),
                            Component.literal("Chrome Junkie"),
                            Component.literal("Used Neuropozyne 100 Times"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("chrome_junkie", ChromeJunkieTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/chrome_junkie"), existingFileHelper);

            Advancement.Builder.advancement().display(ModBlocks.SURGERY_CHAMBER_BOTTOM.get().asItem(),
                            Component.literal("You Fell Over The Edge, Punk"),
                            Component.literal("Died From Cyberpsychosis"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("over_the_edge", OverTheEdgeTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/over_the_edge"), existingFileHelper);

            Advancement.Builder.advancement().display(ModItems.SKINUPGRADES_METALPLATING.get(),
                            Component.literal("From The Moment I Understood The Weakness Of My Flesh..."),
                            Component.literal("Went Full Borg"),
                            null, AdvancementType.TASK, true, true, false)
                    .addCriterion("weak_flesh", WeakFleshTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/weak_flesh"), existingFileHelper);

            Advancement.Builder.advancement().display(ModItems.HEARTUPGRADES_DEFIBRILLATOR.get(),
                            Component.literal("Deus Ex Machina"),
                            Component.literal("Used Cyberware To Escape Death"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("deus_ex_machina", DeusExMachinaTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/deus_ex_machina"), existingFileHelper);

            Advancement.Builder.advancement().display(Blocks.COBWEB.asItem(),
                            Component.literal("Spider-Man: 2099"),
                            Component.literal("Became A Cyborg Spider-Man"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("spider_man", DeusExMachinaTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/spider_man"), existingFileHelper);

            Advancement.Builder.advancement().display(Blocks.COBWEB.asItem(),
                            Component.literal("We Aren't Meant To Live Forever. It Corrupts Even The Best Of Us"),
                            Component.literal("Persisted Through Death In A Cortical Stack"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("cortical_stack", CorticalStackTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/cortical_stack"), existingFileHelper);

            Advancement.Builder.advancement().display(ModItems.BODYPART_BRAIN.get(),
                            Component.literal("That Was Pretty Thoughtless..."),
                            Component.literal("Removed The Brain"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("thoughtless", ThoughtlessTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/thoughtless"), existingFileHelper);

            Advancement.Builder.advancement().display(ModItems.BODYPART_HEART.get(),
                            Component.literal("Chest Pains"),
                            Component.literal("Removed The Heart"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("chest_pains", ChestPainsTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/chest_pains"), existingFileHelper);

            Advancement.Builder.advancement().display(ModItems.BODYPART_LIVER.get(),
                            Component.literal("Liver? Hardly Know Her..."),
                            Component.literal("Removed The Liver"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("liver_removed", LiverRemovedTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/liver_removed"), existingFileHelper);

            Advancement.Builder.advancement().display(ModItems.BODYPART_SKIN.get(),
                            Component.literal("My Skin Feels Weird On My Body. Does Your Skin Feel Weird On Your Body? It Does? You Should Pull It Off. Peel Off Your Skin..."),
                            Component.literal("Removed The Skin"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("skin_removed", SkinRemovedTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/skin_removed"), existingFileHelper);

            Advancement.Builder.advancement().display(ModItems.BODYPART_LUNGS.get(),
                            Component.literal("Respiratoryn’t"),
                            Component.literal("Removed The Lungs"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("lungs_removed", LungsRemovedTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/lungs_removed"), existingFileHelper);

            Advancement.Builder.advancement().display(ModItems.BODYPART_SKELETON.get(),
                            Component.literal("My Skeleton Hatched..."),
                            Component.literal("Removed The Skeleton"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("boneless", BonelessTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/boneless"), existingFileHelper);

            Advancement.Builder.advancement().display(ModItems.BODYPART_MUSCLE.get(),
                            Component.literal("I Have No Muscle And I Must Scream"),
                            Component.literal("Removed The Muscle"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("missing_muscle", MissingMuscleTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/missing_muscle"), existingFileHelper);

            Advancement.Builder.advancement().display(ModItems.BONEUPGRADES_SANDEVISTAN.get(),
                            Component.literal("The David Martinez Special"),
                            Component.literal("Did not adhere to the limitations of the flesh"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("david_special", DavidMartinezSpecialTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/david_special"), existingFileHelper);

            Advancement.Builder.advancement().display(ModItems.DATA_SHARD_RED.get(),
                            Component.literal("I Know Kung Fu"),
                            Component.literal("Downloaded Martial Arts Skills From A Data Shard"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("kung_fu", KungFuTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/kung_fu"), existingFileHelper);

            Advancement.Builder.advancement().display(ModItems.ARMUPGRADES_ARMCANNON.get(),
                            Component.literal("You Upgraded. Now You're Stronger. Faster. Better Than Everyone Else"),
                            Component.literal("Installed An Arm Cannon"),
                            null, AdvancementType.TASK, true, false, false)
                        .addCriterion("upgraded", UpgradedTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/upgraded"), existingFileHelper);

            Advancement.Builder.advancement().display(ModItems.ARMUPGRADES_CLAWS.get(),
                            Component.literal("When They Come Out... Does It Hurt? Every Time."),
                            Component.literal("Installed Retractable Claws"),
                            null, AdvancementType.TASK, true, false, false)
                        .addCriterion("snikt", SniktTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/snikt"), existingFileHelper);

            Advancement.Builder.advancement().display(ModItems.BRAINUPGRADES_CYBERBRAIN.get(),
                            Component.literal("Cogito Ergo Sum"),
                            Component.literal("Installed A Cerebral Processing Unit And Abandoned Your Humanity"),
                            null, AdvancementType.TASK, true, false, false)
                        .addCriterion("cogito_ergo_sum", CogitoErgoSumTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/cogito_ergo_sum"), existingFileHelper);

            Advancement.Builder.advancement().display(ModItems.BRAINUPGRADES_CYBERBRAIN.get(),
                            Component.literal("404: Thoughts Not Found"),
                            Component.literal("Experienced systemwide failure due to being unable to power your brain"),
                            null, AdvancementType.TASK, true, false, false)
                        .addCriterion("thoughts_not_found", ThoughtsNotFoundTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/thoughts_not_found"), existingFileHelper);

            Advancement.Builder.advancement().display(ModItems.BONE_MARROW.get(),
                            Component.literal("Bones And All"),
                            Component.literal("Consumed Bones"),
                            null, AdvancementType.TASK, true, false, false)
                        .addCriterion("bones_and_all", BonesAndAllTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/bones_and_all"), existingFileHelper);


        }
    }
}
