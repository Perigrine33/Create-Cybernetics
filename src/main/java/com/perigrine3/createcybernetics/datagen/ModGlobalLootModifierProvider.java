package com.perigrine3.createcybernetics.datagen;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.loot.AddItemModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {

    public ModGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateCybernetics.MODID);
    }

    @Override
    protected void start() {
        addTrialChambers();
        addArchaeology();
        addChests();
        addEntities();
    }

    private void addTrialChambers() {
        this.add("trial_chamber_vault_qpu",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("vault/ominous_trial_chamber")).build(),
                        LootItemRandomChanceCondition.randomChance(0.33f).build()}, ModItems.NETHERITE_QPU.get()));
        this.add("trial_chamber_vault_armcannon",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("vault/ominous_trial_chamber")).build(),
                        LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.ARMUPGRADES_ARMCANNON.get()));
        this.add("trial_chamber_vault_claws",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("vault/ominous_trial_chamber")).build(),
                        LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.ARMUPGRADES_CLAWS.get()));

        this.add("trial_chamber_vault_rarm",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("vault/trial_chamber")).build(),
                        LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.BASECYBERWARE_RIGHTARM.get()));
        this.add("trial_chamber_vault_larm",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("vault/trial_chamber")).build(),
                        LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.BASECYBERWARE_LEFTARM.get()));
        this.add("trial_chamber_vault_redshard",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("vault/trial_chamber")).build(),
                        LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.DATA_SHARD_RED.get()));
        this.add("trial_chamber_vault_flywheel",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("vault/trial_chamber")).build(),
                        LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.ARMUPGRADES_FLYWHEEL.get()));
        this.add("trial_chamber_vault_wrist",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("vault/trial_chamber")).build(),
                        LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.ARMUPGRADES_PNEUMATICWRIST.get()));
        this.add("trial_chamber_vault_jumpboost",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("vault/trial_chamber")).build(),
                        LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.LEGUPGRADES_JUMPBOOST.get()));
        this.add("trial_chamber_vault_bonelacing",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("vault/trial_chamber")).build(),
                        LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.BONEUPGRADES_BONELACING.get()));
        this.add("trial_chamber_vault_boneflex",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("vault/trial_chamber")).build(),
                        LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.BONEUPGRADES_BONEFLEX.get()));
        this.add("trial_chamber_vault_matrix",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("vault/trial_chamber")).build(),
                        LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.BRAINUPGRADES_MATRIX.get()));
        this.add("trial_chamber_vault_subdermalarmor",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("vault/trial_chamber")).build(),
                        LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SKINUPGRADES_SUBDERMALARMOR.get()));

        this.add("trial_chamber_vault_neuro",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("vault/trial_chamber")).build(),
                        LootItemRandomChanceCondition.randomChance(0.3f).build()}, ModItems.NEUROPOZYNE_AUTOINJECTOR.get()));
    }

    private void addArchaeology() {
        this.add("trail_ruins_common_armor",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("archaeology/trail_ruins_common")).build(),
                        LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SKINUPGRADES_SUBDERMALARMOR.get()));
        this.add("trail_ruins_common_larm",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("archaeology/trail_ruins_common")).build(),
                        LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_LEFTARM.get()));
        this.add("trail_ruins_common_rleg",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("archaeology/trail_ruins_common")).build(),
                        LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_RIGHTLEG.get()));
        this.add("trail_ruins_common_eye",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("archaeology/trail_ruins_common")).build(),
                        LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_CYBEREYES.get()));

        this.add("trail_ruins_common_datura",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("archaeology/trail_ruins_common")).build(),
                        LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.DATURA_SEED_POD.get()));

        this.add("trail_ruins_common_mesh",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("archaeology/trail_ruins_common")).build(),
                        LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.COMPONENT_MESH.get()));
        this.add("trail_ruins_common_fiber",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("archaeology/trail_ruins_common")).build(),
                        LootItemRandomChanceCondition.randomChance(0.15f).build()}, ModItems.COMPONENT_FIBEROPTICS.get()));
        this.add("trail_ruins_common_diodes",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("archaeology/trail_ruins_common")).build(),
                        LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.COMPONENT_DIODES.get()));
        this.add("trail_ruins_common_wiring",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("archaeology/trail_ruins_common")).build(),
                        LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.COMPONENT_WIRING.get()));
        this.add("trail_ruins_common_actuator",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("archaeology/trail_ruins_common")).build(),
                        LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.COMPONENT_ACTUATOR.get()));
        this.add("trail_ruins_common_gpu",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("archaeology/trail_ruins_common")).build(),
                        LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.COMPONENT_GRAPHICSCARD.get()));
        this.add("trail_ruins_common_ssd",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("archaeology/trail_ruins_common")).build(),
                        LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.COMPONENT_SSD.get()));
        this.add("trail_ruins_common_plating",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("archaeology/trail_ruins_common")).build(),
                        LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.COMPONENT_PLATING.get()));
        this.add("trail_ruins_common_storage",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("archaeology/trail_ruins_common")).build(),
                        LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.COMPONENT_STORAGE.get()));
        this.add("trail_ruins_common_nerves",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("archaeology/trail_ruins_common")).build(),
                        LootItemRandomChanceCondition.randomChance(0.05f).build()}, ModItems.COMPONENT_SYNTHNERVES.get()));
    }

    private void addChests() {
                this.add("abandoned_mineshaft_knuckles",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_REINFORCEDKNUCKLES.get()));
                this.add("abandoned_mineshaft_drillfist",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_DRILLFIST.get()));
                this.add("abandoned_mineshaft_cybereyes",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_CYBEREYES.get()));
                this.add("abandoned_mineshaft_nightvision",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_NIGHTVISION.get()));
                this.add("abandoned_mineshaft_metaldetector",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_METALDETECTOR.get()));
                this.add("abandoned_mineshaft_pneumaticwrist",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_PNEUMATICWRIST.get()));
                this.add("abandoned_mineshaft_boneflex",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_BONEFLEX.get()));
                this.add("abandoned_mineshaft_anklebracers",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_ANKLEBRACERS.get()));
                this.add("abandoned_mineshaft_piezo",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_PIEZO.get()));
                this.add("abandoned_mineshaft_crafthands",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_CRAFTHANDS.get()));
                this.add("abandoned_mineshaft_defibrillator",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_DEFIBRILLATOR.get()));
                this.add("abandoned_mineshaft_liverfilter",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_LIVERFILTER.get()));
                this.add("abandoned_mineshaft_idem",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_IDEM.get()));
                this.add("abandoned_mineshaft_rightarm",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_RIGHTARM.get()));
                this.add("abandoned_mineshaft_rightleg",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_RIGHTLEG.get()));
                this.add("abandoned_mineshaft_leftarm",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_LEFTARM.get()));
                this.add("abandoned_mineshaft_leftleg",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_LEFTLEG.get()));

                this.add("abandoned_mineshaft_neuropozyne_autoinjector",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.25f).build()}, ModItems.NEUROPOZYNE_AUTOINJECTOR.get()));

                this.add("abandoned_mineshaft_data_shard_red",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.25f).build()}, ModItems.DATA_SHARD_RED.get()));
                this.add("abandoned_mineshaft_data_shard_orange",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.25f).build()}, ModItems.DATA_SHARD_ORANGE.get()));
                this.add("abandoned_mineshaft_data_shard_yellow",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.25f).build()}, ModItems.DATA_SHARD_YELLOW.get()));
                this.add("abandoned_mineshaft_data_shard_green",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.25f).build()}, ModItems.DATA_SHARD_GREEN.get()));
                this.add("abandoned_mineshaft_data_shard_cyan",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.25f).build()}, ModItems.DATA_SHARD_CYAN.get()));
                this.add("abandoned_mineshaft_data_shard_blue",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.25f).build()}, ModItems.DATA_SHARD_BLUE.get()));
                this.add("abandoned_mineshaft_data_shard_purple",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.25f).build()}, ModItems.DATA_SHARD_PURPLE.get()));
                this.add("abandoned_mineshaft_data_shard_pink",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.25f).build()}, ModItems.DATA_SHARD_PINK.get()));
                this.add("abandoned_mineshaft_data_shard_brown",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.25f).build()}, ModItems.DATA_SHARD_BROWN.get()));
                this.add("abandoned_mineshaft_data_shard_gray",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.25f).build()}, ModItems.DATA_SHARD_GRAY.get()));
                this.add("abandoned_mineshaft_data_shard_black",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.25f).build()}, ModItems.DATA_SHARD_BLACK.get()));


//ANCIENT CITY

                this.add("ancient_city_ocelotpaws",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/ancient_city")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_OCELOTPAWS.get()));
                this.add("ancient_city_cybereyes",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/ancient_city")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_CYBEREYES.get()));
                this.add("ancient_city_nightvision",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/ancient_city")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_NIGHTVISION.get()));
                this.add("ancient_city_targeting",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/ancient_city")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_TARGETING.get()));
                this.add("ancient_city_adrenaline_1",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/ancient_city")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_ADRENALINE.get()));
                this.add("ancient_city_adrenaline_2",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/ancient_city")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_ADRENALINE.get()));
                this.add("ancient_city_dualisticconverter",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/ancient_city")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_DUALISTICCONVERTER.get()));
                this.add("ancient_city_arterialturbine",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/ancient_city")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_ARTERIALTURBINE.get()));
                this.add("ancient_city_neuropozyne_autoinjector",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/ancient_city")).build(),
                                LootItemRandomChanceCondition.randomChance(0.45f).build() }, ModItems.NEUROPOZYNE_AUTOINJECTOR.get()));


//END CITY TREASURE

                this.add("end_city_treasure_enderjammer",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.33f).build() }, ModItems.SCAVENGED_ENDERJAMMER.get()));
                this.add("end_city_treasure_eyeofdefender",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.33f).build() }, ModItems.SCAVENGED_EYEOFDEFENDER.get()));
                this.add("end_city_treasure_matrix",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_MATRIX.get()));
                this.add("end_city_treasure_dualisticconverter",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_DUALISTICCONVERTER.get()));
                this.add("end_city_treasure_stemcell",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_STEMCELL.get()));
                this.add("end_city_treasure_subdermalarmor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_SUBDERMALARMOR.get()));
                this.add("end_city_treasure_linearframe",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_LINEARFRAME.get()));
                this.add("end_city_treasure_cybereyes",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_CYBEREYES.get()));
                this.add("end_city_treasure_hudjack",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_HUDJACK.get()));
                this.add("end_city_treasure_hudlens",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_HUDLENS.get()));
                this.add("end_city_treasure_jumpboost",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_JUMPBOOST.get()));
                this.add("end_city_treasure_armcannon",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_ARMCANNON.get()));
                this.add("end_city_treasure_elytra",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_ELYTRA.get()));
                this.add("end_city_treasure_bonelacing",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_BONELACING.get()));
                this.add("end_city_treasure_zoom",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_ZOOM.get()));
                this.add("end_city_treasure_rightleg",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_RIGHTLEG.get()));
                this.add("end_city_treasure_leftleg",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_LEFTLEG.get()));
                this.add("end_city_treasure_rightarm",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_RIGHTARM.get()));
                this.add("end_city_treasure_leftarm",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_LEFTLEG.get()));

                this.add("end_city_treasure_neuropozyne_autoinjector",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.45f).build() }, ModItems.NEUROPOZYNE_AUTOINJECTOR.get()));


//NETHER BRIDGE

            this.add("nether_bridge_netherite_qpu",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                            LootItemRandomChanceCondition.randomChance(0.33f).build()}, ModItems.NETHERITE_QPU.get()));
            this.add("nether_bridge_netheriteplating",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_NETHERITEPLATING.get()));
            this.add("nether_bridge_stemcell",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_STEMCELL.get()));
            this.add("nether_bridge_dualisticconverter",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_DUALISTICCONVERTER.get()));
            this.add("nether_bridge_densebattery",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_DENSEBATTERY.get()));
            this.add("nether_bridge_chromatophores",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_CHROMATOPHORES.get()));
            this.add("nether_bridge_wiredreflexes",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_WIREDREFLEXES.get()));
            this.add("nether_bridge_metabolic",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_METABOLIC.get()));
            this.add("nether_bridge_synthmuscle",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_SYNTHMUSCLE.get()));
            this.add("nether_bridge_synthskin",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_SYNTHSKIN.get()));
            this.add("nether_bridge_immuno",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_IMMUNO.get()));
            this.add("nether_bridge_diamondwaferstack",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_DIAMONDWAFERSTACK.get()));
            this.add("nether_bridge_flywheel",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_FLYWHEEL.get()));
            this.add("nether_bridge_piezo",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_PIEZO.get()));
            this.add("nether_bridge_spinalinjector",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_SPINALINJECTOR.get()));
            this.add("nether_bridge_bonebattery",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_BONEBATTERY.get()));
            this.add("nether_bridge_cybereyes",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_CYBEREYES.get()));
            this.add("nether_bridge_cyberdeck",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_CYBERDECK.get()));
            this.add("nether_bridge_firestarter",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_FIRESTARTER.get()));
            this.add("nether_bridge_targeting",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_TARGETING.get()));
            this.add("nether_bridge_zoom",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_ZOOM.get()));
            this.add("nether_bridge_neuropozyne_autoinjector",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                            LootItemRandomChanceCondition.randomChance(0.65f).build()}, ModItems.NEUROPOZYNE_AUTOINJECTOR.get()));

//SIMPLE DUNGEON
            this.add("simple_dungeon_cybereyes",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/simple_dungeon")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_CYBEREYES.get()));
            this.add("simple_dungeon_rightarm",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/simple_dungeon")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_RIGHTARM.get()));
            this.add("simple_dungeon_rightleg",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/simple_dungeon")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_RIGHTLEG.get()));
            this.add("simple_dungeon_leftleg",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/simple_dungeon")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_LEFTLEG.get()));
            this.add("simple_dungeon_leftarm",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/simple_dungeon")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_LEFTARM.get()));
            this.add("simple_dungeon_hudlens",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/simple_dungeon")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_HUDLENS.get()));
            this.add("simple_dungeon_bonebattery",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/simple_dungeon")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_BONEBATTERY.get()));
            this.add("simple_dungeon_battery",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/simple_dungeon")).build(),
                            LootItemRandomChanceCondition.randomChance(0.4f).build()}, ModItems.SCAVENGED_BATTERY.get()));
            this.add("simple_dungeon_neuropozyne_autoinjector",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/simple_dungeon")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.NEUROPOZYNE_AUTOINJECTOR.get()));


//WOODLAND MANSION

            this.add("woodland_mansion_basecybereyes",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.BASECYBERWARE_CYBEREYES.get()));
            this.add("woodland_mansion_scavenged_cybereyes",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                            LootItemRandomChanceCondition.randomChance(0.3f).build()}, ModItems.SCAVENGED_CYBEREYES.get()));
            this.add("woodland_mansion_base_rightarm",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.BASECYBERWARE_RIGHTARM.get()));
            this.add("woodland_mansion_scavenged_rightarm",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                            LootItemRandomChanceCondition.randomChance(0.3f).build()}, ModItems.SCAVENGED_RIGHTARM.get()));
            this.add("woodland_mansion_base_rightleg",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.BASECYBERWARE_RIGHTLEG.get()));
            this.add("woodland_mansion_scavenged_rightleg",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                            LootItemRandomChanceCondition.randomChance(0.3f).build()}, ModItems.SCAVENGED_RIGHTLEG.get()));
            this.add("woodland_mansion_base_leftarm",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.BASECYBERWARE_LEFTARM.get()));
            this.add("woodland_mansion_scavenged_leftarm",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                            LootItemRandomChanceCondition.randomChance(0.3f).build()}, ModItems.SCAVENGED_LEFTARM.get()));
            this.add("woodland_mansion_base_leftleg",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.BASECYBERWARE_LEFTLEG.get()));
            this.add("woodland_mansion_scavenged_leftleg",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                            LootItemRandomChanceCondition.randomChance(0.3f).build()}, ModItems.SCAVENGED_LEFTLEG.get()));
            this.add("woodland_mansion_organsupgrades_densebattery",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                            LootItemRandomChanceCondition.randomChance(0.15f).build()}, ModItems.ORGANSUPGRADES_DENSEBATTERY.get()));
            this.add("woodland_mansion_scavenged_densebattery",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_DENSEBATTERY.get()));
            this.add("woodland_mansion_organsupgrades_battery",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                            LootItemRandomChanceCondition.randomChance(0.3f).build()}, ModItems.ORGANSUPGRADES_BATTERY.get()));
            this.add("woodland_mansion_scavenged_battery",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                            LootItemRandomChanceCondition.randomChance(0.4f).build()}, ModItems.SCAVENGED_BATTERY.get()));
            this.add("woodland_mansion_scavenged_spinalinjector",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                            LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_SPINALINJECTOR.get()));
            this.add("woodland_mansion_boneupgrades_spinalinjector",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                            LootItemRandomChanceCondition.randomChance(0.05f).build()}, ModItems.BONEUPGRADES_SPINALINJECTOR.get()));
            this.add("woodland_mansion_wetware_ravagertendons",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                            LootItemRandomChanceCondition.randomChance(0.3f).build()}, ModItems.WETWARE_RAVAGERTENDONS.get()));
            this.add("woodland_mansion_heartupgrades_cyberheart",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                            LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.HEARTUPGRADES_CYBERHEART.get()));
            this.add("woodland_mansion_neuropozyne_autoinjector",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                            LootItemRandomChanceCondition.randomChance(0.75f).build()}, ModItems.NEUROPOZYNE_AUTOINJECTOR.get()));

//STRONGHOLD CORRIDOR
            this.add("stronghold_corridor_rightleg",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_RIGHTLEG.get()));
            this.add("stronghold_corridor_leftleg",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_LEFTLEG.get()));
            this.add("stronghold_corridor_rightarm",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_RIGHTARM.get()));
            this.add("stronghold_corridor_leftarm",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_LEFTARM.get()));
            this.add("stronghold_corridor_cybereyes",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_CYBEREYES.get()));
            this.add("stronghold_corridor_linearframe",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_LINEARFRAME.get()));
            this.add("stronghold_corridor_hudlens",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_HUDLENS.get()));
            this.add("stronghold_corridor_hudjack",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_HUDJACK.get()));
            this.add("stronghold_corridor_nightvision",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_NIGHTVISION.get()));
            this.add("stronghold_corridor_underwatervision",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_UNDERWATERVISION.get()));
            this.add("stronghold_corridor_targeting",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_TARGETING.get()));
            this.add("stronghold_corridor_zoom",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_ZOOM.get()));
            this.add("stronghold_corridor_armcannon",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_ARMCANNON.get()));
            this.add("stronghold_corridor_claws",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_CLAWS.get()));
            this.add("stronghold_corridor_crafthands",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_CRAFTHANDS.get()));
            this.add("stronghold_corridor_drillfist",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_DRILLFIST.get()));
            this.add("stronghold_corridor_flywheel",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_FLYWHEEL.get()));
            this.add("stronghold_corridor_firestarter",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_FIRESTARTER.get()));
            this.add("stronghold_corridor_pneumaticwrist",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_PNEUMATICWRIST.get()));
            this.add("stronghold_corridor_reinforcedknuckles",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_REINFORCEDKNUCKLES.get()));
            this.add("stronghold_corridor_metaldetector",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_METALDETECTOR.get()));
            this.add("stronghold_corridor_anklebracers",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_ANKLEBRACERS.get()));
            this.add("stronghold_corridor_jumpboost",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_JUMPBOOST.get()));
            this.add("stronghold_corridor_propellers",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_PROPELLERS.get()));
            this.add("stronghold_corridor_spurs",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_SPURS.get()));
            this.add("stronghold_corridor_bonebattery",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_BONEBATTERY.get()));
            this.add("stronghold_corridor_boneflex",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_BONEFLEX.get()));
            this.add("stronghold_corridor_bonelacing",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_BONELACING.get()));
            this.add("stronghold_corridor_elytra",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_ELYTRA.get()));
            this.add("stronghold_corridor_piezo",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_PIEZO.get()));
            this.add("stronghold_corridor_spinalinjector",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_SPINALINJECTOR.get()));
            this.add("stronghold_corridor_eyeofdefender",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_EYEOFDEFENDER.get()));
            this.add("stronghold_corridor_enderjammer",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_ENDERJAMMER.get()));
            this.add("stronghold_corridor_matrix",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_MATRIX.get()));
            this.add("stronghold_corridor_neuralcontextualizer",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_NEURALCONTEXTUALIZER.get()));
            this.add("stronghold_corridor_cyberdeck",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_CYBERDECK.get()));
            this.add("stronghold_corridor_idem",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_IDEM.get()));
            this.add("stronghold_corridor_cyberheart",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_CYBERHEART.get()));
            this.add("stronghold_corridor_coupler",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_COUPLER.get()));
            this.add("stronghold_corridor_creeperheart",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_CREEPERHEART.get()));
            this.add("stronghold_corridor_defibrillator",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_DEFIBRILLATOR.get()));
            this.add("stronghold_corridor_stemcell",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_STEMCELL.get()));
            this.add("stronghold_corridor_platelets",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_PLATELETS.get()));
            this.add("stronghold_corridor_hyperoxygenation",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_HYPEROXYGENATION.get()));
            this.add("stronghold_corridor_oxygen",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_OXYGEN.get()));
            this.add("stronghold_corridor_adrenaline",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_ADRENALINE.get()));
            this.add("stronghold_corridor_battery",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_BATTERY.get()));
            this.add("stronghold_corridor_diamondwaferstack",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_DIAMONDWAFERSTACK.get()));
            this.add("stronghold_corridor_dualisticconverter_2",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_DUALISTICCONVERTER.get()));
            this.add("stronghold_corridor_liverfilter",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_LIVERFILTER.get()));
            this.add("stronghold_corridor_metabolic_2",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_METABOLIC.get()));
            this.add("stronghold_corridor_densebattery_2",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_DENSEBATTERY.get()));
            this.add("stronghold_corridor_arterialturbine",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_ARTERIALTURBINE.get()));
            this.add("stronghold_corridor_chromatophores",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_CHROMATOPHORES.get()));
            this.add("stronghold_corridor_synthskin_2",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_SYNTHSKIN.get()));
            this.add("stronghold_corridor_immuno_2",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_IMMUNO.get()));
            this.add("stronghold_corridor_faceplate",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_FACEPLATE.get()));
            this.add("stronghold_corridor_netheriteplating",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_NETHERITEPLATING.get()));
            this.add("stronghold_corridor_solarskin",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_SOLARSKIN.get()));
            this.add("stronghold_corridor_subdermalarmor_2",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_SUBDERMALARMOR.get()));
            this.add("stronghold_corridor_subdermalspikes",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_SUBDERMALSPIKES.get()));
            this.add("stronghold_corridor_synthmuscle_2",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_SYNTHMUSCLE.get()));
            this.add("stronghold_corridor_wiredreflexes_2",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.2f).build()}, ModItems.SCAVENGED_WIREDREFLEXES.get()));

            this.add("stronghold_corridor_neuropozyne_autoinjector",
                    new AddItemModifier(new LootItemCondition[]{
                            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.55f).build()}, ModItems.NEUROPOZYNE_AUTOINJECTOR.get()));
    }

    private void addEntities() {
//COW LOOT ADDS
            {
                this.add("bodypart_brain_to_cow",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/cow")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_cow",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/cow")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_cow",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/cow")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_cow",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/cow")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_cow",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/cow")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_cow",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/cow")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_cow",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/cow")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_cow",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/cow")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//SHEEP LOOT ADDS
            {
                this.add("bodypart_brain_to_sheep",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/sheep")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_sheep",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/sheep")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_sheep",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/sheep")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_sheep",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/sheep")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_sheep",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/sheep")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_sheep",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/sheep")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_sheep",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/sheep")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_sheep",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/sheep")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//PIG LOOT ADDS
            {
                this.add("bodypart_brain_to_pig",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/pig")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_pig",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/pig")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_pig",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/pig")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_pig",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/pig")).build(),
                                LootItemRandomChanceCondition.randomChance(0.4f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_pig",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/pig")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_pig",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/pig")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_pig",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/pig")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_pig",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/pig")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//MOOSHROOM LOOT ADDS
            {
                this.add("bodypart_brain_to_mooshroom",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/mooshroom")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_mooshroom",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/mooshroom")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_mooshroom",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/mooshroom")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_mooshroom",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/mooshroom")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_mooshroom",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/mooshroom")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_mooshroom",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/mooshroom")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_mooshroom",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/mooshroom")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_mooshroom",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/mooshroom")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//LLAMA LOOT ADDS
            {
                this.add("bodypart_brain_to_llama",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/llama")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_llama",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/llama")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_llama",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/llama")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_llama",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/llama")).build(),
                                LootItemRandomChanceCondition.randomChance(0.20f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_llama",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/llama")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_llama",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/llama")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_llama",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/llama")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_llama",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/llama")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//TRADER LLAMA LOOT ADDS
            {
                this.add("bodypart_brain_to_trader_llama",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/trader_llama")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_trader_llama",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/trader_llama")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_trader_llama",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/trader_llama")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_trader_llama",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/trader_llama")).build(),
                                LootItemRandomChanceCondition.randomChance(0.20f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_trader_llama",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/trader_llama")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_trader_llama",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/trader_llama")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_trader_llama",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/trader_llama")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_trader_llama",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/trader_llama")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//WOLF LOOT ADDS
            {
                this.add("bodypart_brain_to_wolf",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/wolf")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_wolf",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/wolf")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_wolf",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/wolf")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_wolf",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/wolf")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_wolf",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/wolf")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_wolf",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/wolf")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_wolf",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/wolf")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_wolf",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/wolf")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//VILLAGER LOOT ADDS
            {
                this.add("bodypart_brain_to_villager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/villager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_villager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/villager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_villager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/villager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_villager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/villager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.3f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_villager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/villager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_villager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/villager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_villager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/villager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_villager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/villager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//CHICKEN LOOT ADDS
            {
                this.add("bodypart_heart_to_chicken",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/chicken")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_chicken",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/chicken")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_chicken",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/chicken")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_chicken",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/chicken")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
            }

//CAT LOOT ADDS
            {
                this.add("bodypart_brain_to_cat",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/cat")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_cat",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/cat")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_cat",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/cat")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_cat",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/cat")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_cat",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/cat")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_cat",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/cat")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_cat",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/cat")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_cat",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/cat")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//DOLPHIN LOOT ADDS
            {
                this.add("bodypart_brain_to_dolphin",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/dolphin")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_dolphin",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/dolphin")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_dolphin",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/dolphin")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_dolphin",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/dolphin")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_dolphin",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/dolphin")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_dolphin",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/dolphin")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_dolphin",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/dolphin")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_dolphin",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/dolphin")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//DONKEY LOOT ADDS
            {
                this.add("bodypart_brain_to_donkey",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/donkey")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_donkey",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/donkey")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_donkey",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/donkey")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_donkey",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/donkey")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_donkey",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/donkey")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_donkey",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/donkey")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_donkey",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/donkey")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_donkey",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/donkey")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//HORSE LOOT ADDS
            {
                this.add("bodypart_brain_to_horse",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/horse")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_horse",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/horse")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_horse",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/horse")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_horse",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/horse")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_horse",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/horse")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_horse",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/horse")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_horse",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/horse")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_horse",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/horse")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//FOX LOOT ADDS
            {
                this.add("bodypart_brain_to_fox",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/fox")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_fox",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/fox")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_fox",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/fox")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_fox",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/fox")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_fox",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/fox")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_fox",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/fox")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_fox",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/fox")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_fox",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/fox")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//GOAT LOOT ADDS
            {
                this.add("bodypart_brain_to_goat",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/goat")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_goat",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/goat")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_goat",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/goat")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_goat",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/goat")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_goat",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/goat")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_goat",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/goat")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_goat",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/goat")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_goat",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/goat")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//MULE LOOT ADDS
            {
                this.add("bodypart_brain_to_mule",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/mule")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_mule",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/mule")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_mule",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/mule")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_mule",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/mule")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_mule",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/mule")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_mule",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/mule")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_mule",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/mule")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_mule",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/mule")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//OCELOT LOOT ADDS
            {
                this.add("bodypart_brain_to_ocelot",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/ocelot")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_ocelot",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/ocelot")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_ocelot",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/ocelot")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_ocelot",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/ocelot")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_ocelot",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/ocelot")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_ocelot",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/ocelot")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_ocelot",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/ocelot")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_ocelot",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/ocelot")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//PANDA LOOT ADDS
            {
                this.add("bodypart_brain_to_panda",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/panda")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_panda",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/panda")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_panda",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/panda")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_panda",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/panda")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_panda",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/panda")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_panda",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/panda")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_panda",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/panda")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_panda",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/panda")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//POLAR BEAR LOOT ADDS
            {
                this.add("bodypart_brain_to_polar_bear",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/polar_bear")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_polar_bear",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/polar_bear")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_polar_bear",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/polar_bear")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_polar_bear",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/polar_bear")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_polar_bear",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/polar_bear")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_polar_bear",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/polar_bear")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_polar_bear",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/polar_bear")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_polar_bear",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/polar_bear")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
                this.add("wetware_polarbearfur_to_polar_bear",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/polar_bear")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.WETWARE_POLARBEARFUR.get()));
                if (ModItems.WETWARE_BLUBBER != null) {
                    this.add("wetware_blubber_to_polar_bear",
                            new AddItemModifier(new LootItemCondition[]{
                                    new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/polar_bear")).build(),
                                    LootItemRandomChanceCondition.randomChance(0.05f).build()
                            }, ModItems.WETWARE_BLUBBER.get()));
                }
            }

//RABBIT LOOT ADDS
            {
                this.add("bodypart_brain_to_rabbit",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/rabbit")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_rabbit",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/rabbit")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_rabbit",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/rabbit")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_rabbit",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/rabbit")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_rabbit",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/rabbit")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_rabbit",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/rabbit")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_rabbit",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/rabbit")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_rabbit",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/rabbit")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//SNIFFER LOOT ADDS
            {
                this.add("bodypart_brain_to_sniffer",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/sniffer")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_sniffer",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/sniffer")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_sniffer",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/sniffer")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_sniffer",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/sniffer")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_sniffer",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/sniffer")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_sniffer",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/sniffer")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_sniffer",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/sniffer")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_sniffer",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/sniffer")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//STRIDER LOOT ADDS
            {
                this.add("bodypart_brain_to_strider",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/strider")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_strider",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/strider")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_strider",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/strider")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_strider",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/strider")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_strider",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/strider")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_strider",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/strider")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_strider",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/strider")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_strider",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/strider")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//GUARDIAN LOOT ADDS
            {
                this.add("bodypart_brain_to_guardian",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/guardian")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_guardian",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/guardian")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_guardian",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/guardian")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_guardian",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/guardian")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_guardian",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/guardian")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_guardian",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/guardian")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_guardian",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/guardian")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_guardian",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/guardian")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
                this.add("bodypart_guardianretina_to_guardian",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/guardian")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_GUARDIANRETINA.get()));
            }

//ELDER GUARDIAN LOOT ADDS
            {
                this.add("bodypart_brain_to_elder_guardian",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/elder_guardian")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_elder_guardian",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/elder_guardian")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_elder_guardian",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/elder_guardian")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_elder_guardian",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/elder_guardian")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_elder_guardian",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/elder_guardian")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_elder_guardian",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/elder_guardian")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_elder_guardian",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/elder_guardian")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_elder_guardian",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/elder_guardian")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
                this.add("bodypart_guardianretina_to_elder_guardian",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/elder_guardian")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_GUARDIANRETINA.get()));
            }

//WITCH LOOT ADDS
            {
                this.add("bodypart_brain_to_witch",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/witch")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_witch",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/witch")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_witch",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/witch")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_witch",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/witch")).build(),
                                LootItemRandomChanceCondition.randomChance(0.3f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_witch",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/witch")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_witch",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/witch")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_witch",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/witch")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_witch",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/witch")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//ILLUSIONER LOOT ADDS
            {
                this.add("bodypart_brain_to_illusioner",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/illusioner")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_illusioner",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/illusioner")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_illusioner",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/illusioner")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_illusioner",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/illusioner")).build(),
                                LootItemRandomChanceCondition.randomChance(0.3f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_illusioner",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/illusioner")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_illusioner",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/illusioner")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_illusioner",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/illusioner")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_illusioner",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/illusioner")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//PIGLIN LOOT ADDS
            {
                this.add("bodypart_brain_to_piglin",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/piglin")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_piglin",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/piglin")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_piglin",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/piglin")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_piglin",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/piglin")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_piglin",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/piglin")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_piglin",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/piglin")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_piglin",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/piglin")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_piglin",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/piglin")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//PIGLIN BRUTE LOOT ADDS
            {
                this.add("bodypart_brain_to_piglin_brute",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/piglin_brute")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_piglin_brute",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/piglin_brute")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_piglin_brute",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/piglin_brute")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_piglin_brute",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/piglin_brute")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_piglin_brute",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/piglin_brute")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_piglin_brute",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/piglin_brute")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_piglin_brute",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/piglin_brute")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_piglin_brute",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/piglin_brute")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//ENDERMAN LOOT ADDS
            {
                this.add("bodypart_brain_to_enderman",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/enderman")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_heart_to_enderman",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/enderman")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_enderman",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/enderman")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_enderman",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/enderman")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_enderman",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/enderman")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_enderman",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/enderman")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_enderman",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/enderman")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//CAMEL LOOT ADDS
            {
                this.add("bodypart_brain_to_camel",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/camel")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_camel",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/camel")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_camel",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/camel")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_camel",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/camel")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_camel",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/camel")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_camel",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/camel")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_camel",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/camel")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_camel",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/camel")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//WANDERING TRADER LOOT ADDS
            {
                this.add("bodypart_brain_to_wandering_trader",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/wandering_trader")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_wandering_trader",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/wandering_trader")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_wandering_trader",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/wandering_trader")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_wandering_trader",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/wandering_trader")).build(),
                                LootItemRandomChanceCondition.randomChance(0.3f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_wandering_trader",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/wandering_trader")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_wandering_trader",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/wandering_trader")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_wandering_trader",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/wandering_trader")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_wandering_trader",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/wandering_trader")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//PILLAGER LOOT ADDS
            {
                this.add("bodypart_brain_to_pillager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/pillager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_pillager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/pillager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_pillager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/pillager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_pillager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/pillager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.3f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_pillager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/pillager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_pillager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/pillager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_pillager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/pillager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_pillager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/pillager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//VINDICATOR LOOT ADDS
            {
                this.add("bodypart_brain_to_vindicator",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/vindicator")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_vindicator",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/vindicator")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_vindicator",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/vindicator")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_vindicator",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/vindicator")).build(),
                                LootItemRandomChanceCondition.randomChance(0.3f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_vindicator",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/vindicator")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_vindicator",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/vindicator")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_vindicator",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/vindicator")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_vindicator",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/vindicator")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//EVOKER LOOT ADDS
            {
                this.add("bodypart_brain_to_evoker",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/evoker")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_evoker",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/evoker")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_evoker",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/evoker")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_evoker",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/evoker")).build(),
                                LootItemRandomChanceCondition.randomChance(0.3f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_evoker",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/evoker")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_evoker",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/evoker")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_evoker",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/evoker")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_evoker",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/evoker")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//RAVAGER LOOT ADDS
            {
                this.add("bodypart_brain_to_ravager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/ravager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_ravager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/ravager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_ravager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/ravager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_ravager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/ravager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_ravager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/ravager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_ravager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/ravager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_ravager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/ravager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_ravager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/ravager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
                this.add("wetware_ravagertendons_to_ravager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/ravager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.WETWARE_RAVAGERTENDONS.get()));
            }

//HOGLIN LOOT ADDS
            {
                this.add("bodypart_brain_to_hoglin",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/hoglin")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_hoglin",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/hoglin")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_hoglin",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/hoglin")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_hoglin",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/hoglin")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_hoglin",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/hoglin")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_hoglin",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/hoglin")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_hoglin",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/hoglin")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_hoglin",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/hoglin")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//SALMON LOOT ADDS
            {
                this.add("wetware_gills_to_salmon",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/salmon")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_GILLS.get()));
            }

//COD LOOT ADDS
            {
                this.add("wetware_gills_to_cod",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/cod")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.BODYPART_GILLS.get()));
            }


//SKELETON LOOT ADDS
            {
                this.add("bodypart_skeleton_to_skeleton",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/skeleton")).build(),
                                LootItemRandomChanceCondition.randomChance(0.25f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//BOGGED LOOT ADDS
            {
                this.add("bodypart_skeleton_to_bogged",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/bogged")).build(),
                                LootItemRandomChanceCondition.randomChance(0.25f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//STRAY LOOT ADDS
            {
                this.add("bodypart_skeleton_to_bogged",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/stray")).build(),
                                LootItemRandomChanceCondition.randomChance(0.25f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }


//ZOMBIE LOOT ADDS
            {
                this.add("bodypart_brain_to_zombie",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/zombie")).build(),
                                LootItemRandomChanceCondition.randomChance(0.0001f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_zombie",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/zombie")).build(),
                                LootItemRandomChanceCondition.randomChance(0.005f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_zombie",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/zombie")).build(),
                                LootItemRandomChanceCondition.randomChance(0.005f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_zombie",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/zombie")).build(),
                                LootItemRandomChanceCondition.randomChance(0.02f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_zombie",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/zombie")).build(),
                                LootItemRandomChanceCondition.randomChance(0.005f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_zombie",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/zombie")).build(),
                                LootItemRandomChanceCondition.randomChance(0.005f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_zombie",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/zombie")).build(),
                                LootItemRandomChanceCondition.randomChance(0.005f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_zombie",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/zombie")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//HUSK LOOT ADDS
            {
                this.add("bodypart_brain_to_husk",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/husk")).build(),
                                LootItemRandomChanceCondition.randomChance(0.0001f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_husk",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/husk")).build(),
                                LootItemRandomChanceCondition.randomChance(0.005f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_husk",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/husk")).build(),
                                LootItemRandomChanceCondition.randomChance(0.005f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_husk",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/husk")).build(),
                                LootItemRandomChanceCondition.randomChance(0.02f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_husk",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/husk")).build(),
                                LootItemRandomChanceCondition.randomChance(0.005f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_husk",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/husk")).build(),
                                LootItemRandomChanceCondition.randomChance(0.005f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_husk",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/husk")).build(),
                                LootItemRandomChanceCondition.randomChance(0.005f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_husk",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/husk")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//ZOMBIE VILLAGER LOOT ADDS
            {
                this.add("bodypart_brain_to_zombie_villager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/zombie_villager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.0001f).build()
                        }, ModItems.BODYPART_BRAIN.get()));
                this.add("bodypart_eyeballs_to_zombie_villager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/zombie_villager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.005f).build()
                        }, ModItems.BODYPART_EYEBALLS.get()));
                this.add("bodypart_heart_to_zombie_villager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/zombie_villager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.005f).build()
                        }, ModItems.BODYPART_HEART.get()));
                this.add("bodypart_intestines_to_zombie_villager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/zombie_villager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.02f).build()
                        }, ModItems.BODYPART_INTESTINES.get()));
                this.add("bodypart_liver_to_zombie_villager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/zombie_villager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.005f).build()
                        }, ModItems.BODYPART_LIVER.get()));
                this.add("bodypart_lungs_to_zombie_villager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/zombie_villager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.005f).build()
                        }, ModItems.BODYPART_LUNGS.get()));
                this.add("bodypart_muscle_to_zombie_villager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/zombie_villager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.005f).build()
                        }, ModItems.BODYPART_MUSCLE.get()));
                this.add("bodypart_skeleton_to_zombie_villager",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/zombie_villager")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SKELETON.get()));
            }

//SPIDER LOOT ADDS
            {
                this.add("bodypart_spinnerette_to_spider",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/spider")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_SPINNERETTE.get()));
            }



//ENDER DRAGON LOOT ADDS
            {
                this.add("firebreathinggland_to_ender_dragon",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/ender_dragon")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_FIREGLAND.get()));
                this.add("dragonscale_to_ender_dragon",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/ender_dragon")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()
                        }, ModItems.BODYPART_DRAGONSCALE.get()));
            }

//WARDEN LOOT ADDS
            {
                this.add("bodypart_wardenesophagus_to_warden",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/warden")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_WARDENESOPHAGUS.get()));
            }

//GHAST LOOT ADDS
            {
                this.add("bodypart_gyroscopicbladder_to_ghast",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/ghast")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_GYROSCOPICBLADDER.get()));
            }

//AXOLOTL LOOT ADDS
            {
                this.add("bodypart_axolotlmarrow_to_axolotl",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/axolotl")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.BODYPART_AXOLOTLMARROW.get()));
            }


        }
}
