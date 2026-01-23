package com.perigrine3.createcybernetics.item;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateCybernetics.MODID);

    public static final Supplier<CreativeModeTab> CREATE_CYBERNETICS_TAB = CREATIVE_MODE_TAB.register("create_cybernetics_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.TITANIUMINGOT.get()))
                    .title(Component.translatable("creativetab.createcybernetics.create_cybernetics_tab"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(ModBlocks.ROBOSURGEON);
                        output.accept(ModBlocks.SURGERY_CHAMBER_BOTTOM);
                        output.accept(ModBlocks.ENGINEERING_TABLE);
                        output.accept(ModBlocks.CHARGING_BLOCK);
                        output.accept(ModItems.NEUROPOZYNE_AUTOINJECTOR);
                        output.accept(ModItems.EMPTY_AUTOINJECTOR);

                        output.accept(ModItems.RAWTITANIUM);
                        output.accept(ModItems.CRUSHEDTITANIUM);
                        output.accept(ModItems.TITANIUMINGOT);
                        output.accept(ModItems.TITANIUMNUGGET);
                        output.accept(ModItems.TITANIUMSHEET);
                        output.accept(ModItems.EYEUPGRADEBASE);
                        output.accept(ModItems.TITANIUM_HAND);
                        output.accept(ModItems.FRONTAL_LOBE);
                        output.accept(ModItems.PARIETAL_LOBE);
                        output.accept(ModItems.TEMPORAL_LOBE);
                        output.accept(ModItems.OCCIPITAL_LOBE);
                        output.accept(ModItems.CEREBELLUM);
                        output.accept(ModItems.FACEPLATE);
                        output.accept(ModItems.EMP_GRENADE);

                        output.accept(ModItems.MUSIC_DISC_CYBERPSYCHO);
                        output.accept(ModItems.MUSIC_DISC_NEON_OVERLORDS);
                        output.accept(ModItems.MUSIC_DISC_NEUROHACK);
                        output.accept(ModItems.MUSIC_DISC_THE_GRID);

                        //DATA SHARDS
                        output.accept(ModItems.DATA_SHARD_RED);
                        output.accept(ModItems.DATA_SHARD_ORANGE);
                        output.accept(ModItems.DATA_SHARD_YELLOW);
                        output.accept(ModItems.DATA_SHARD_GREEN);
                        output.accept(ModItems.DATA_SHARD_CYAN);
                        output.accept(ModItems.DATA_SHARD_BLUE);
                        output.accept(ModItems.DATA_SHARD_PURPLE);
                        output.accept(ModItems.DATA_SHARD_PINK);
                        output.accept(ModItems.DATA_SHARD_BROWN);
                        output.accept(ModItems.DATA_SHARD_GRAY);
                        output.accept(ModItems.DATA_SHARD_BLACK);
                        output.accept(ModItems.DATA_SHARD_BIOCHIP);

                        //COMPONENTS
                        output.accept(ModItems.COMPONENT_ACTUATOR);
                        output.accept(ModItems.COMPONENT_FIBEROPTICS);
                        output.accept(ModItems.COMPONENT_WIRING);
                        output.accept(ModItems.COMPONENT_DIODES);
                        output.accept(ModItems.COMPONENT_PLATING);
                        output.accept(ModItems.COMPONENT_GRAPHICSCARD);
                        output.accept(ModItems.COMPONENT_SSD);
                        output.accept(ModItems.COMPONENT_STORAGE);
                        output.accept(ModItems.COMPONENT_SYNTHNERVES);
                        output.accept(ModItems.COMPONENT_MESH);

                        if (ModItems.COMPONENT_LED != null && ModItems.COMPONENT_TITANIUMROD != null) {
                            output.accept(ModItems.COMPONENT_LED);
                            output.accept(ModItems.COMPONENT_TITANIUMROD);
                        }

                        output.accept(ModItems.NETHERITE_QPU);

                        //WETWARE PARTS
                        output.accept(ModItems.BODYPART_GUARDIANRETINA);
                        output.accept(ModItems.BODYPART_WARDENESOPHAGUS);
                        output.accept(ModItems.BODYPART_GYROSCOPICBLADDER);

                        //SCAVENGED PARTS
                        output.accept(ModItems.SCAVENGED_RIGHTLEG);
                        output.accept(ModItems.SCAVENGED_LEFTLEG);
                        output.accept(ModItems.SCAVENGED_RIGHTARM);
                        output.accept(ModItems.SCAVENGED_LEFTARM);
                        output.accept(ModItems.SCAVENGED_CYBEREYES);
                        output.accept(ModItems.SCAVENGED_LINEARFRAME);
                        output.accept(ModItems.SCAVENGED_HUDLENS);

                        if (ModItems.SCAVENGED_NAVIGATIONCHIP != null) {
                            output.accept(ModItems.SCAVENGED_NAVIGATIONCHIP);
                        }

                        output.accept(ModItems.SCAVENGED_HUDJACK);
                        output.accept(ModItems.SCAVENGED_NIGHTVISION);
                        output.accept(ModItems.SCAVENGED_TARGETING);
                        output.accept(ModItems.SCAVENGED_UNDERWATERVISION);
                        output.accept(ModItems.SCAVENGED_ZOOM);
                        output.accept(ModItems.SCAVENGED_TRAJECTORYCALCULATOR);
                        output.accept(ModItems.SCAVENGED_ARMCANNON);
                        output.accept(ModItems.SCAVENGED_FLYWHEEL);
                        output.accept(ModItems.SCAVENGED_CLAWS);
                        output.accept(ModItems.SCAVENGED_CRAFTHANDS);
                        output.accept(ModItems.SCAVENGED_DRILLFIST);
                        output.accept(ModItems.SCAVENGED_FIRESTARTER);
                        output.accept(ModItems.SCAVENGED_PNEUMATICWRIST);
                        output.accept(ModItems.SCAVENGED_REINFORCEDKNUCKLES);
                        output.accept(ModItems.SCAVENGED_METALDETECTOR);
                        output.accept(ModItems.SCAVENGED_ANKLEBRACERS);
                        output.accept(ModItems.SCAVENGED_JUMPBOOST);
                        output.accept(ModItems.SCAVENGED_PROPELLERS);
                        output.accept(ModItems.SCAVENGED_SPURS);
                        output.accept(ModItems.SCAVENGED_OCELOTPAWS);
                        output.accept(ModItems.SCAVENGED_BONEBATTERY);
                        output.accept(ModItems.SCAVENGED_BONEFLEX);
                        output.accept(ModItems.SCAVENGED_BONELACING);
                        output.accept(ModItems.SCAVENGED_PIEZO);
                        output.accept(ModItems.SCAVENGED_SPINALINJECTOR);
                        output.accept(ModItems.SCAVENGED_SANDEVISTAN);
                        output.accept(ModItems.SCAVENGED_EYEOFDEFENDER);

                        if (ModItems.SCAVENGED_CONSCIOUSNESSTRANSMITTER != null && ModItems.SCAVENGED_CORTICALSTACK != null) {
                            output.accept(ModItems.SCAVENGED_CONSCIOUSNESSTRANSMITTER);
                            output.accept(ModItems.SCAVENGED_CORTICALSTACK);
                        }

                        if (ModItems.SCAVENGED_ELYTRA != null) {
                            output.accept(ModItems.SCAVENGED_ELYTRA);
                        }


                        output.accept(ModItems.SCAVENGED_ENDERJAMMER);
                        output.accept(ModItems.SCAVENGED_MATRIX);
                        output.accept(ModItems.SCAVENGED_NEURALCONTEXTUALIZER);
                        output.accept(ModItems.SCAVENGED_CYBERDECK);
                        output.accept(ModItems.SCAVENGED_IDEM);
                        output.accept(ModItems.SCAVENGED_CHIPWARESLOTS);
                        output.accept(ModItems.SCAVENGED_CYBERHEART);
                        output.accept(ModItems.SCAVENGED_COUPLER);
                        output.accept(ModItems.SCAVENGED_CREEPERHEART);
                        output.accept(ModItems.SCAVENGED_DEFIBRILLATOR);
                        output.accept(ModItems.SCAVENGED_STEMCELL);
                        output.accept(ModItems.SCAVENGED_PLATELETS);
                        output.accept(ModItems.SCAVENGED_HYPEROXYGENATION);
                        output.accept(ModItems.SCAVENGED_OXYGEN);
                        output.accept(ModItems.SCAVENGED_ADRENALINE);
                        output.accept(ModItems.SCAVENGED_BATTERY);
                        output.accept(ModItems.SCAVENGED_DIAMONDWAFERSTACK);
                        output.accept(ModItems.SCAVENGED_DUALISTICCONVERTER);
                        output.accept(ModItems.SCAVENGED_LIVERFILTER);
                        output.accept(ModItems.SCAVENGED_METABOLIC);
                        output.accept(ModItems.SCAVENGED_DENSEBATTERY);
                        output.accept(ModItems.SCAVENGED_ARTERIALTURBINE);
                        output.accept(ModItems.SCAVENGED_CHROMATOPHORES);
                        output.accept(ModItems.SCAVENGED_SYNTHSKIN);
                        output.accept(ModItems.SCAVENGED_IMMUNO);
                        output.accept(ModItems.SCAVENGED_FACEPLATE);
                        output.accept(ModItems.SCAVENGED_NETHERITEPLATING);
                        output.accept(ModItems.SCAVENGED_SOLARSKIN);
                        output.accept(ModItems.SCAVENGED_SUBDERMALARMOR);
                        output.accept(ModItems.SCAVENGED_SUBDERMALSPIKES);
                        output.accept(ModItems.SCAVENGED_SYNTHETICSETULES);
                        output.accept(ModItems.SCAVENGED_METALPLATING);

                        if (ModItems.SCAVENGED_SWEAT != null) {
                            output.accept(ModItems.SCAVENGED_SWEAT);
                        }

                        output.accept(ModItems.SCAVENGED_SYNTHMUSCLE);
                        output.accept(ModItems.SCAVENGED_WIREDREFLEXES);


                    }).build());

    public static final Supplier<CreativeModeTab> CREATE_CYBERNETICS_UPGRADES_TAB = CREATIVE_MODE_TAB.register("create_cybernetics_upgrades_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.BASECYBERWARE_CYBEREYES.get()))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "create_cybernetics_tab"))
                    .title(Component.translatable("creativetab.createcybernetics.create_cybernetics_upgrades_tab"))
                    .displayItems((itemDisplayParameters, output) -> {

                    //LIMBS
                        output.accept(ModItems.BODYPART_RIGHTARM);
                        output.accept(ModItems.BODYPART_LEFTARM);
                        output.accept(ModItems.BODYPART_RIGHTLEG);
                        output.accept(ModItems.BODYPART_LEFTLEG);
                        //INTERNAL WETWARE
                        output.accept(ModItems.BODYPART_SKELETON);
                        output.accept(ModItems.BODYPART_BRAIN);
                        output.accept(ModItems.BODYPART_EYEBALLS);
                        output.accept(ModItems.BODYPART_HEART);
                        output.accept(ModItems.BODYPART_LUNGS);
                        output.accept(ModItems.BODYPART_LIVER);
                        output.accept(ModItems.BODYPART_INTESTINES);
                        output.accept(ModItems.BODYPART_MUSCLE);
                        output.accept(ModItems.BODYPART_SKIN);
                    //BASE CYBERWARE
                        output.accept(ModItems.BASECYBERWARE_RIGHTARM);
                        output.accept(ModItems.BASECYBERWARE_LEFTARM);
                        output.accept(ModItems.BASECYBERWARE_RIGHTLEG);
                        output.accept(ModItems.BASECYBERWARE_LEFTLEG);
                        output.accept(ModItems.BASECYBERWARE_CYBEREYES);
                        output.accept(ModItems.BASECYBERWARE_LINEARFRAME);
                    //EYE UPGRADES
                        output.accept(ModItems.EYEUPGRADES_HUDLENS);

                        if (ModItems.EYEUPGRADES_NAVIGATIONCHIP != null) {
                            output.accept(ModItems.EYEUPGRADES_NAVIGATIONCHIP);
                        }

                        output.accept(ModItems.EYEUPGRADES_HUDJACK);
                        output.accept(ModItems.EYEUPGRADES_NIGHTVISION);
                        output.accept(ModItems.EYEUPGRADES_UNDERWATERVISION);
                        output.accept(ModItems.EYEUPGRADES_TARGETING);
                        output.accept(ModItems.EYEUPGRADES_ZOOM);
                        output.accept(ModItems.EYEUPGRADES_TRAJECTORYCALCULATOR);
                    //ARM UPGRADES
                        output.accept(ModItems.ARMUPGRADES_ARMCANNON);
                        output.accept(ModItems.ARMUPGRADES_CLAWS);
                        output.accept(ModItems.ARMUPGRADES_CRAFTHANDS);
                        output.accept(ModItems.ARMUPGRADES_DRILLFIST);
                        output.accept(ModItems.ARMUPGRADES_FLYWHEEL);
                        output.accept(ModItems.ARMUPGRADES_FIRESTARTER);
                        output.accept(ModItems.ARMUPGRADES_PNEUMATICWRIST);
                        output.accept(ModItems.ARMUPGRADES_REINFORCEDKNUCKLES);
                    //LEG UPGRADES
                        output.accept(ModItems.LEGUPGRADES_METALDETECTOR);
                        output.accept(ModItems.LEGUPGRADES_ANKLEBRACERS);
                        output.accept(ModItems.LEGUPGRADES_JUMPBOOST);
                        output.accept(ModItems.LEGUPGRADES_PROPELLERS);
                        output.accept(ModItems.LEGUPGRADES_SPURS);
                        output.accept(ModItems.LEGUPGRADES_OCELOTPAWS);
                    //BONE UPGRADES
                        output.accept(ModItems.BONEUPGRADES_BONEBATTERY);
                        output.accept(ModItems.BONEUPGRADES_BONEFLEX);
                        output.accept(ModItems.BONEUPGRADES_BONELACING);
                        output.accept(ModItems.BONEUPGRADES_PIEZO);
                        output.accept(ModItems.BONEUPGRADES_SPINALINJECTOR);
                        output.accept(ModItems.BONEUPGRADES_SANDEVISTAN);
                        output.accept(ModItems.BONEUPGRADES_CYBERSKULL);
                    //BRAIN UPGRADES
                        output.accept(ModItems.BRAINUPGRADES_CYBERBRAIN);
                        output.accept(ModItems.BRAINUPGRADES_EYEOFDEFENDER);

                        if (ModItems.BRAINUPGRADES_CONSCIOUSNESSTRANSMITTER != null && ModItems.BRAINUPGRADES_CORTICALSTACK != null) {
                            output.accept(ModItems.BRAINUPGRADES_CONSCIOUSNESSTRANSMITTER);
                            output.accept(ModItems.BRAINUPGRADES_CORTICALSTACK);
                        }

                        if (ModItems.BONEUPGRADES_ELYTRA != null) {
                            output.accept(ModItems.BONEUPGRADES_ELYTRA);
                        }

                        output.accept(ModItems.BRAINUPGRADES_ENDERJAMMER);
                        output.accept(ModItems.BRAINUPGRADES_MATRIX);
                        output.accept(ModItems.BRAINUPGRADES_NEURALCONTEXTUALIZER);
                        output.accept(ModItems.BRAINUPGRADES_CYBERDECK);
                        output.accept(ModItems.BRAINUPGRADES_IDEM);
                        output.accept(ModItems.BRAINUPGRADES_CHIPWARESLOTS);
                    //HEART UPGRADES
                        output.accept(ModItems.HEARTUPGRADES_CYBERHEART);
                        output.accept(ModItems.HEARTUPGRADES_COUPLER);
                        output.accept(ModItems.HEARTUPGRADES_CREEPERHEART);
                        output.accept(ModItems.HEARTUPGRADES_DEFIBRILLATOR);
                        output.accept(ModItems.HEARTUPGRADES_STEMCELL);
                        output.accept(ModItems.HEARTUPGRADES_PLATELETS);
                    //LUNG UPGRADES
                        output.accept(ModItems.LUNGSUPGRADES_HYPEROXYGENATION);
                        output.accept(ModItems.LUNGSUPGRADES_OXYGEN);
                    //ORGAN UPGRADES
                        output.accept(ModItems.ORGANSUPGRADES_ADRENALINE);
                        output.accept(ModItems.ORGANSUPGRADES_BATTERY);
                        output.accept(ModItems.ORGANSUPGRADES_DIAMONDWAFERSTACK);
                        output.accept(ModItems.ORGANSUPGRADES_DUALISTICCONVERTER);
                        output.accept(ModItems.ORGANSUPGRADES_LIVERFILTER);
                        output.accept(ModItems.ORGANSUPGRADES_MAGICCATALYST);
                        output.accept(ModItems.ORGANSUPGRADES_METABOLIC);
                        output.accept(ModItems.ORGANSUPGRADES_DENSEBATTERY);
                    //SKIN UPGRADES
                        output.accept(ModItems.SKINUPGRADES_ARTERIALTURBINE);
                        output.accept(ModItems.SKINUPGRADES_CHROMATOPHORES);
                        output.accept(ModItems.SKINUPGRADES_SYNTHSKIN);
                        output.accept(ModItems.SKINUPGRADES_IMMUNO);
                        output.accept(ModItems.SKINUPGRADES_FACEPLATE);
                        output.accept(ModItems.SKINUPGRADES_NETHERITEPLATING);
                        output.accept(ModItems.SKINUPGRADES_SOLARSKIN);
                        output.accept(ModItems.SKINUPGRADES_SUBDERMALARMOR);
                        output.accept(ModItems.SKINUPGRADES_SUBDERMALSPIKES);
                        output.accept(ModItems.SKINUPGRADES_SYNTHETICSETULES);
                        output.accept(ModItems.SKINUPGRADES_METALPLATING);

                        if (ModItems.SKINUPGRADES_SWEAT != null) {
                            output.accept(ModItems.SKINUPGRADES_SWEAT);
                        }

                    //MUSCLE UPGRADES
                        output.accept(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE);
                        output.accept(ModItems.MUSCLEUPGRADES_WIREDREFLEXES);
                    //WETWARE UPGRADES
                        if (ModItems.WETWARE_BLUBBER != null) {
                            output.accept(ModItems.WETWARE_BLUBBER);
                        }

                        output.accept(ModItems.WETWARE_FIREBREATHINGGLAND);
                        output.accept(ModItems.WETWARE_GILLS);
                        output.accept(ModItems.WETWARE_GUARDIANEYE);
                        output.accept(ModItems.WETWARE_POLARBEARFUR);
                        output.accept(ModItems.WETWARE_RAVAGERTENDONS);
                        output.accept(ModItems.WETWARE_SCULKLUNGS);
                        output.accept(ModItems.WETWARE_TACTICALINKSAC);
                        output.accept(ModItems.WETWARE_AEROSTASISGYROBLADDER);
                        output.accept(ModItems.WETWARE_GRASSFEDSTOMACH);
                        output.accept(ModItems.WETWARE_SPINNERETTE);



                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
