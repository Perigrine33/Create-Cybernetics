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

        //CHEST
        {
//ABANDONED MINESHAFT
            {
                this.add("abandoned_mineshaft",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_REINFORCEDKNUCKLES.get()));
                this.add("abandoned_mineshaft",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_DRILLFIST.get()));
                this.add("abandoned_mineshaft",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_CYBEREYES.get()));
                this.add("abandoned_mineshaft",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_NIGHTVISION.get()));
                this.add("abandoned_mineshaft",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_METALDETECTOR.get()));
                this.add("abandoned_mineshaft",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_PNEUMATICWRIST.get()));
                this.add("abandoned_mineshaft",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_BONEFLEX.get()));
                this.add("abandoned_mineshaft",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_ANKLEBRACERS.get()));
                this.add("abandoned_mineshaft",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_PIEZO.get()));
                this.add("abandoned_mineshaft",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_CRAFTHANDS.get()));
                this.add("abandoned_mineshaft",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_DEFIBRILLATOR.get()));
                this.add("abandoned_mineshaft",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_LIVERFILTER.get()));
                this.add("abandoned_mineshaft",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_IDEM.get()));
                this.add("abandoned_mineshaft",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_RIGHTARM.get()));
                this.add("abandoned_mineshaft",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_RIGHTLEG.get()));
                this.add("abandoned_mineshaft",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_LEFTARM.get()));
                this.add("abandoned_mineshaft",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_LEFTLEG.get()));
                this.add("abandoned_mineshaft",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.25f).build()}, ModItems.NEUROPOZYNE.get()));
            }

//ANCIENT CITY
            {
                this.add("ancient_city",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/ancient_city")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_OCELOTPAWS.get()));
                this.add("ancient_city",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/ancient_city")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_CYBEREYES.get()));
                this.add("ancient_city",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/ancient_city")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_NIGHTVISION.get()));
                this.add("ancient_city",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/ancient_city")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_TARGETING.get()));
                this.add("ancient_city",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/ancient_city")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_ADRENALINE.get()));
                this.add("ancient_city",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/ancient_city")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_ADRENALINE.get()));
                this.add("ancient_city",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/ancient_city")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_DUALISTICCONVERTER.get()));
                this.add("ancient_city",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/ancient_city")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_ARTERIALTURBINE.get()));
                this.add("ancient_city",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/ancient_city")).build(),
                                LootItemRandomChanceCondition.randomChance(0.15f).build() }, ModItems.NEUROPOZYNE.get()));

            }

//END CITY TREASURE
            {
                this.add("end_city_treasure",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_ENDERJAMMER.get()));
                this.add("end_city_treasure",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_EYEOFDEFENDER.get()));
                this.add("end_city_treasure",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_MATRIX.get()));
                this.add("end_city_treasure",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_DUALISTICCONVERTER.get()));
                this.add("end_city_treasure",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_STEMCELL.get()));
                this.add("end_city_treasure",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_SUBDERMALARMOR.get()));
                this.add("end_city_treasure",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_LINEARFRAME.get()));
                this.add("end_city_treasure",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_CYBEREYES.get()));
                this.add("end_city_treasure",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_HUDJACK.get()));
                this.add("end_city_treasure",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_HUDLENS.get()));
                this.add("end_city_treasure",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_JUMPBOOST.get()));
                this.add("end_city_treasure",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_ARMCANNON.get()));
                this.add("end_city_treasure",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_ELYTRA.get()));
                this.add("end_city_treasure",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_BONELACING.get()));
                if (ModItems.SCAVENGED_NAVIGATIONCHIP != null) {
                    this.add("end_city_treasure",
                            new AddItemModifier(new LootItemCondition[]{
                                    new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                    LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_NAVIGATIONCHIP.get()));
                }
                this.add("end_city_treasure",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_ZOOM.get()));
                this.add("end_city_treasure",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_RIGHTLEG.get()));
                this.add("end_city_treasure",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_LEFTLEG.get()));
                this.add("end_city_treasure",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_RIGHTARM.get()));
                this.add("end_city_treasure",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_LEFTLEG.get()));
                this.add("end_city_treasure",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.45f).build() }, ModItems.NEUROPOZYNE.get()));

            }

//NETHER BRIDGE
            {
                this.add("nether_bridge",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_NETHERITEPLATING.get()));
                if (ModItems.SCAVENGED_CONSCIOUSNESSTRANSMITTER != null && ModItems.SCAVENGED_CORTICALSTACK != null) {
                    this.add("nether_bridge",
                            new AddItemModifier(new LootItemCondition[]{
                                    new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                    LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_CONSCIOUSNESSTRANSMITTER.get()));
                    this.add("nether_bridge",
                            new AddItemModifier(new LootItemCondition[]{
                                    new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                    LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_CORTICALSTACK.get()));
                }
                this.add("nether_bridge",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_STEMCELL.get()));
                this.add("nether_bridge",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_DUALISTICCONVERTER.get()));
                this.add("nether_bridge",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_DENSEBATTERY.get()));
                this.add("nether_bridge",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_CHROMATOPHORES.get()));
                this.add("nether_bridge",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_WIREDREFLEXES.get()));
                this.add("nether_bridge",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_METABOLIC.get()));
                this.add("nether_bridge",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_SYNTHMUSCLE.get()));
                this.add("nether_bridge",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_SYNTHSKIN.get()));
                this.add("nether_bridge",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_IMMUNO.get()));
                this.add("nether_bridge",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_DIAMONDWAFERSTACK.get()));
                this.add("nether_bridge",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_FLYWHEEL.get()));
                this.add("nether_bridge",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_PIEZO.get()));
                this.add("nether_bridge",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_SPINALINJECTOR.get()));
                this.add("nether_bridge",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_BONEBATTERY.get()));
                this.add("nether_bridge",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_CYBEREYES.get()));
                this.add("nether_bridge",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_CYBERDECK.get()));
                this.add("nether_bridge",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_FIRESTARTER.get()));
                if (ModItems.SCAVENGED_NAVIGATIONCHIP != null) {
                    this.add("nether_bridge",
                            new AddItemModifier(new LootItemCondition[]{
                                    new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                    LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_NAVIGATIONCHIP.get()));
                }
                this.add("nether_bridge",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_TARGETING.get()));
                this.add("nether_bridge",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_ZOOM.get()));
                this.add("nether_bridge",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                LootItemRandomChanceCondition.randomChance(0.65f).build() }, ModItems.NEUROPOZYNE.get()));

            }

//SIMPLE DUNGEON
            {
                this.add("simple_dungeon",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/simple_dungeon")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_CYBEREYES.get()));
                this.add("simple_dungeon",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/simple_dungeon")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_RIGHTARM.get()));
                this.add("simple_dungeon",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/simple_dungeon")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_RIGHTLEG.get()));
                this.add("simple_dungeon",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/simple_dungeon")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_LEFTLEG.get()));
                this.add("simple_dungeon",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/simple_dungeon")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_LEFTARM.get()));
                this.add("simple_dungeon",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/simple_dungeon")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_HUDLENS.get()));
                this.add("simple_dungeon",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/simple_dungeon")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_BONEBATTERY.get()));
                this.add("simple_dungeon",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/simple_dungeon")).build(),
                                LootItemRandomChanceCondition.randomChance(0.4f).build() }, ModItems.SCAVENGED_BATTERY.get()));
                this.add("simple_dungeon",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/simple_dungeon")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.NEUROPOZYNE.get()));

            }

//WOODLAND MANSION
            {
                this.add("woodland_mansion",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.BASECYBERWARE_CYBEREYES.get()));
                this.add("woodland_mansion",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                                LootItemRandomChanceCondition.randomChance(0.3f).build() }, ModItems.SCAVENGED_CYBEREYES.get()));
                this.add("woodland_mansion",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.BASECYBERWARE_RIGHTARM.get()));
                this.add("woodland_mansion",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                                LootItemRandomChanceCondition.randomChance(0.3f).build() }, ModItems.SCAVENGED_RIGHTARM.get()));
                this.add("woodland_mansion",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.BASECYBERWARE_RIGHTLEG.get()));
                this.add("woodland_mansion",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                                LootItemRandomChanceCondition.randomChance(0.3f).build() }, ModItems.SCAVENGED_RIGHTLEG.get()));
                this.add("woodland_mansion",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.BASECYBERWARE_LEFTARM.get()));
                this.add("woodland_mansion",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                                LootItemRandomChanceCondition.randomChance(0.3f).build() }, ModItems.SCAVENGED_LEFTARM.get()));
                this.add("woodland_mansion",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.BASECYBERWARE_LEFTLEG.get()));
                this.add("woodland_mansion",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                                LootItemRandomChanceCondition.randomChance(0.3f).build() }, ModItems.SCAVENGED_LEFTLEG.get()));
                this.add("woodland_mansion",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                                LootItemRandomChanceCondition.randomChance(0.15f).build() }, ModItems.ORGANSUPGRADES_DENSEBATTERY.get()));
                this.add("woodland_mansion",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                                LootItemRandomChanceCondition.randomChance(0.2f).build() }, ModItems.SCAVENGED_DENSEBATTERY.get()));
                this.add("woodland_mansion",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                                LootItemRandomChanceCondition.randomChance(0.3f).build() }, ModItems.ORGANSUPGRADES_BATTERY.get()));
                this.add("woodland_mansion",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                                LootItemRandomChanceCondition.randomChance(0.4f).build() }, ModItems.SCAVENGED_BATTERY.get()));
                this.add("woodland_mansion",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_SPINALINJECTOR.get()));
                this.add("woodland_mansion",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build() }, ModItems.BONEUPGRADES_SPINALINJECTOR.get()));
                this.add("woodland_mansion",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                                LootItemRandomChanceCondition.randomChance(0.3f).build() }, ModItems.WETWARE_RAVAGERTENDONS.get()));
                this.add("woodland_mansion",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.HEARTUPGRADES_CYBERHEART.get()));
                this.add("woodland_mansion",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                                LootItemRandomChanceCondition.randomChance(0.75f).build() }, ModItems.NEUROPOZYNE.get()));

            }

//STRONGHOLD CORRIDOR
            {
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_RIGHTLEG.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_LEFTLEG.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_RIGHTARM.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_LEFTARM.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_CYBEREYES.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_LINEARFRAME.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_HUDLENS.get()));
                if (ModItems.SCAVENGED_NAVIGATIONCHIP != null) {
                    this.add("stronghold_corridor",
                            new AddItemModifier(new LootItemCondition[]{
                                    new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                    LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_NAVIGATIONCHIP.get()));
                }
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_HUDJACK.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_NIGHTVISION.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_UNDERWATERVISION.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_TARGETING.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_ZOOM.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_ARMCANNON.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_CLAWS.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_CRAFTHANDS.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_DRILLFIST.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_FLYWHEEL.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_FIRESTARTER.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_PNEUMATICWRIST.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_REINFORCEDKNUCKLES.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_METALDETECTOR.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_ANKLEBRACERS.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_JUMPBOOST.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_PROPELLERS.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_SPURS.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_BONEBATTERY.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_BONEFLEX.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_BONELACING.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_ELYTRA.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_PIEZO.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_SPINALINJECTOR.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_EYEOFDEFENDER.get()));
                if (ModItems.SCAVENGED_CONSCIOUSNESSTRANSMITTER != null && ModItems.SCAVENGED_CORTICALSTACK != null) {
                    this.add("stronghold_corridor",
                            new AddItemModifier(new LootItemCondition[]{
                                    new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                    LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_CONSCIOUSNESSTRANSMITTER.get()));
                    this.add("stronghold_corridor",
                            new AddItemModifier(new LootItemCondition[]{
                                    new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                    LootItemRandomChanceCondition.randomChance(0.1f).build()}, ModItems.SCAVENGED_CORTICALSTACK.get()));
                }
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_ENDERJAMMER.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_MATRIX.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_NEURALCONTEXTUALIZER.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_CYBERDECK.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_IDEM.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_CYBERHEART.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_COUPLER.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_CREEPERHEART.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_DEFIBRILLATOR.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_STEMCELL.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_PLATELETS.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_HYPEROXYGENATION.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_OXYGEN.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_ADRENALINE.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_BATTERY.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_DIAMONDWAFERSTACK.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_DUALISTICCONVERTER.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_LIVERFILTER.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_METABOLIC.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_DENSEBATTERY.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_ARTERIALTURBINE.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_CHROMATOPHORES.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_SYNTHSKIN.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_IMMUNO.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_FACEPLATE.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_NETHERITEPLATING.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_SOLARSKIN.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_SUBDERMALARMOR.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_SUBDERMALSPIKES.get()));
              this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_SYNTHMUSCLE.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.1f).build() }, ModItems.SCAVENGED_WIREDREFLEXES.get()));
                this.add("stronghold_corridor",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                                LootItemRandomChanceCondition.randomChance(0.55f).build() }, ModItems.NEUROPOZYNE.get()));

            }


        }

        //ENTITY
        {
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
                        }, ModItems.WETWARE_GILLS.get()));
            }

//COD LOOT ADDS
            {
                this.add("wetware_gills_to_cod",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/cod")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build()
                        }, ModItems.WETWARE_GILLS.get()));
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


//ENDER DRAGON LOOT ADDS
            {
                this.add("wetware_firebreathinggland_to_ender_dragon",
                        new AddItemModifier(new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/ender_dragon")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build()
                        }, ModItems.WETWARE_FIREBREATHINGGLAND.get()));
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


        }
    }
}
