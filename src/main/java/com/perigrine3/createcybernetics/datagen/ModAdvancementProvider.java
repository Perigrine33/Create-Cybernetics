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
                            Component.literal("Cybernetics"),
                            Component.literal("Become a cyberpunk"),
                            MACHINE_BG, AdvancementType.TASK, true, true, false)
                    .addCriterion("tick", net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance.tick())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/root"), existingFileHelper);

            AdvancementHolder firstScavenged = Advancement.Builder.advancement().parent(root).display(ModItems.SCAVENGED_LEFTARM,
                            Component.literal("I Don't Think I Want To Put That In My Body..."),
                            Component.literal("Obtained Scavenged Cyberware"),
                            MACHINE_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("first_scavenged_cyberware", FirstScavengedCyberwareTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/first_scavenged_cyberware"), existingFileHelper);

            AdvancementHolder firstCyberware = Advancement.Builder.advancement().parent(firstScavenged).display(ModItems.BASECYBERWARE_LEFTARM,
                            Component.literal("Time To Chrome"),
                            Component.literal("Obtained Cyberware"),
                            MACHINE_BG, AdvancementType.TASK, true, true, false)
                    .addCriterion("first_cyberware", FirstCyberwareTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/first_cyberware"), existingFileHelper);

            AdvancementHolder firstRipperdocVisit = Advancement.Builder.advancement().parent(firstCyberware).display(ModBlocks.ROBOSURGEON.get(),
                            Component.literal("I Want This Fused Here"),
                            Component.literal("Installed Cyberware"),
                            MACHINE_BG, AdvancementType.TASK, true, true, false)
                    .addCriterion("first_ripperdoc_visit", FirstRipperdocVisitTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/first_ripperdoc_visit"), existingFileHelper);

            AdvancementHolder goingPsycho = Advancement.Builder.advancement().parent(firstRipperdocVisit).display(ModItems.ARMUPGRADES_CRAFTHANDS.get(),
                            Component.literal("Best Lift Ya Pedal Off The Metal, Choom!"),
                            Component.literal("Incurred Cyberware Rejection"),
                            MACHINE_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("cyberpsychosis", CyberpsychosisTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/cyberpsychosis"), existingFileHelper);

            AdvancementHolder chromeJunkie = Advancement.Builder.advancement().parent(goingPsycho).display(ModItems.NEUROPOZYNE_AUTOINJECTOR.get(),
                            Component.literal("Chrome Junkie"),
                            Component.literal("Used Neuropozyne 100 Times"),
                            MACHINE_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("chrome_junkie", ChromeJunkieTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/chrome_junkie"), existingFileHelper);

            AdvancementHolder psychoDeath = Advancement.Builder.advancement().parent(goingPsycho).display(ModBlocks.SURGERY_CHAMBER_BOTTOM.get().asItem(),
                            Component.literal("You Fell Over The Edge, Punk"),
                            Component.literal("Died From Cyberpsychosis"),
                            MACHINE_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("over_the_edge", OverTheEdgeTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/over_the_edge"), existingFileHelper);

            AdvancementHolder fullBorg = Advancement.Builder.advancement().parent(firstRipperdocVisit).display(ModItems.SKINUPGRADES_METALPLATING.get(),
                            Component.literal("From The Moment I Understood The Weakness Of My Flesh..."),
                            Component.literal("Went Full Borg"),
                            MACHINE_BG, AdvancementType.TASK, true, true, false)
                    .addCriterion("weak_flesh", WeakFleshTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/weak_flesh"), existingFileHelper);

            AdvancementHolder survivedDeath = Advancement.Builder.advancement().parent(firstRipperdocVisit).display(ModItems.HEARTUPGRADES_DEFIBRILLATOR.get(),
                            Component.literal("Deus Ex Machina"),
                            Component.literal("Used Cyberware To Escape Death"),
                            MACHINE_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("deus_ex_machina", DeusExMachinaTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/deus_ex_machina"), existingFileHelper);

            AdvancementHolder spiderMan = Advancement.Builder.advancement().parent(firstRipperdocVisit).display(Blocks.COBWEB.asItem(),
                            Component.literal("Spider-Man: 2099"),
                            Component.literal("Became A Cyborg Spider-Man"),
                            MACHINE_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("spider_man", DeusExMachinaTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/spider_man"), existingFileHelper);

            AdvancementHolder corticalStack = Advancement.Builder.advancement().parent(firstRipperdocVisit).display(Blocks.COBWEB.asItem(),
                            Component.literal("We Aren't Meant To Live Forever. It Corrupts Even The Best Of Us"),
                            Component.literal("Persisted Through Death In A Cortical Stack"),
                            MACHINE_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("cortical_stack", CorticalStackTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/cortical_stack"), existingFileHelper);





            AdvancementHolder brainRemoved = Advancement.Builder.advancement().display(ModItems.BODYPART_BRAIN.get(),
                            Component.literal("That Was Pretty Thoughtless..."),
                            Component.literal("Removed The Brain"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("thoughtless", ThoughtlessTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/thoughtless"), existingFileHelper);

            AdvancementHolder heartRemoved = Advancement.Builder.advancement().parent(brainRemoved).display(ModItems.BODYPART_HEART.get(),
                            Component.literal("Chest Pains"),
                            Component.literal("Removed The Heart"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("chest_pains", ChestPainsTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/chest_pains"), existingFileHelper);

            AdvancementHolder liverRemoved = Advancement.Builder.advancement().parent(heartRemoved).display(ModItems.BODYPART_LIVER.get(),
                            Component.literal("Liver? Hardly Know Her..."),
                            Component.literal("Removed The Liver"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("liver_removed", LiverRemovedTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/liver_removed"), existingFileHelper);

            AdvancementHolder skinRemoved = Advancement.Builder.advancement().parent(liverRemoved).display(ModItems.BODYPART_SKIN.get(),
                            Component.literal("My Skin Feels Weird On My Body. Does Your Skin Feel Weird On Your Body? It Does? You Should Pull It Off. Peel Off Your Skin..."),
                            Component.literal("Removed The Skin"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("skin_removed", SkinRemovedTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/skin_removed"), existingFileHelper);

            AdvancementHolder lungsRemoved = Advancement.Builder.advancement().parent(skinRemoved).display(ModItems.BODYPART_LUNGS.get(),
                            Component.literal("Respiratoryn’t"),
                            Component.literal("Removed The Lungs"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("lungs_removed", LungsRemovedTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/lungs_removed"), existingFileHelper);

            AdvancementHolder skeletonRemoved = Advancement.Builder.advancement().parent(lungsRemoved).display(ModItems.BODYPART_SKELETON.get(),
                            Component.literal("My Skeleton Hatched..."),
                            Component.literal("Removed The Skeleton"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("boneless", BonelessTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/boneless"), existingFileHelper);

            AdvancementHolder muscleRemoved = Advancement.Builder.advancement().parent(skeletonRemoved).display(ModItems.BODYPART_MUSCLE.get(),
                            Component.literal("I Have No Muscle And I Must Scream"),
                            Component.literal("Removed The Muscle"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("missing_muscle", MissingMuscleTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/missing_muscle"), existingFileHelper);

            AdvancementHolder limbsRemoved = Advancement.Builder.advancement().parent(muscleRemoved).display(ModItems.BODYPART_LEFTARM.get(),
                            Component.literal("'Tis But A Flesh Wound"),
                            Component.literal("Removed All Limbs"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("missing_limbs", FleshWoundTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/missing_limbs"), existingFileHelper);




            AdvancementHolder davidMartinez = Advancement.Builder.advancement().parent(firstRipperdocVisit).display(ModItems.BONEUPGRADES_SANDEVISTAN.get(),
                            Component.literal("The David Martinez Special"),
                            Component.literal("Did not adhere to the limitations of the flesh"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("david_special", DavidMartinezSpecialTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/david_special"), existingFileHelper);

            AdvancementHolder kungFu = Advancement.Builder.advancement().parent(firstRipperdocVisit).display(ModItems.DATA_SHARD_RED.get(),
                            Component.literal("I Know Kung Fu"),
                            Component.literal("Downloaded Martial Arts Skills From A Data Shard"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                    .addCriterion("kung_fu", KungFuTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/kung_fu"), existingFileHelper);

            AdvancementHolder armCannon = Advancement.Builder.advancement().parent(firstRipperdocVisit).display(ModItems.ARMUPGRADES_ARMCANNON.get(),
                            Component.literal("You Upgraded. Now You're Stronger. Faster. Better Than Everyone Else"),
                            Component.literal("Installed An Arm Cannon"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                        .addCriterion("upgraded", UpgradedTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/upgraded"), existingFileHelper);

            AdvancementHolder wolverineClaws = Advancement.Builder.advancement().parent(firstRipperdocVisit).display(ModItems.ARMUPGRADES_CLAWS.get(),
                            Component.literal("When They Come Out... Does It Hurt? Every Time."),
                            Component.literal("Installed Retractable Claws"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                        .addCriterion("snikt", SniktTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/snikt"), existingFileHelper);

            AdvancementHolder digitalBrain = Advancement.Builder.advancement().parent(firstRipperdocVisit).display(ModItems.BRAINUPGRADES_CYBERBRAIN.get(),
                            Component.literal("Cogito Ergo Sum"),
                            Component.literal("Installed A Cerebral Processing Unit And Abandoned Your Humanity"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                        .addCriterion("cogito_ergo_sum", CogitoErgoSumTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/cogito_ergo_sum"), existingFileHelper);

            AdvancementHolder brainOff = Advancement.Builder.advancement().parent(digitalBrain).display(ModItems.BRAINUPGRADES_CYBERBRAIN.get(),
                            Component.literal("404: Thoughts Not Found"),
                            Component.literal("Experienced systemwide failure due to being unable to power your brain"),
                            TITANIUM_BG, AdvancementType.TASK, true, false, false)
                        .addCriterion("thoughts_not_found", ThoughtsNotFoundTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/thoughts_not_found"), existingFileHelper);




            AdvancementHolder boneMarrow = Advancement.Builder.advancement().display(ModItems.BONE_MARROW.get(),
                            Component.literal("Bones And All"),
                            Component.literal("Consumed Bones"),
                            RAW_TITANIUM_BG, AdvancementType.TASK, true, false, false)
                        .addCriterion("bones_and_all", BonesAndAllTrigger.Instance.any())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware/bones_and_all"), existingFileHelper);


        }
    }
}
