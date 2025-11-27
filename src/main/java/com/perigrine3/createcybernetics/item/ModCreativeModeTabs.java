package com.perigrine3.createcybernetics.item;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateCybernetics.MODID);

    //Adds custom creative mode tab
    public static final Supplier<CreativeModeTab> CREATE_CYBERNETICS_TAB = CREATIVE_MODE_TAB.register("create_cybernetics_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.TITANIUMINGOT.get()))
                    .title(Component.translatable("creativetab.createcybernetics.create_cybernetics_tab"))
                    .displayItems((itemDisplayParameters, output) -> {

                        //Adds items to the creative mode tab
                        output.accept(ModItems.RAWTITANIUM);
                        output.accept(ModItems.CRUSHEDTITANIUM);
                        output.accept(ModItems.TITANIUMINGOT);
                        output.accept(ModItems.TITANIUMNUGGET);
                        output.accept(ModItems.TITANIUMSHEET);

                        //COMPONENTS
                        output.accept(ModItems.COMPONENT_ACTUATOR);
                        output.accept(ModItems.COMPONENT_FIBEROPTICS);
                        output.accept(ModItems.COMPONENT_FULLERENE);
                        output.accept(ModItems.COMPONENT_DIODES);
                        output.accept(ModItems.COMPONENT_PLATING);
                        output.accept(ModItems.COMPONENT_GRAPHICSCARD);
                        output.accept(ModItems.COMPONENT_SSD);
                        output.accept(ModItems.COMPONENT_STORAGE);
                        output.accept(ModItems.COMPONENT_SYNTHNERVES);
                        output.accept(ModItems.COMPONENT_MESH);

                    }).build());

    public static final Supplier<CreativeModeTab> CREATE_CYBERNETICS_UPGRADES_TAB = CREATIVE_MODE_TAB.register("create_cybernetics_upgrades_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.BASECYBERWARE_CYBEREYES.get()))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "create_cybernetics_tab"))
                    .title(Component.translatable("creativetab.createcybernetics.create_cybernetics_upgrades_tab"))
                    .displayItems((itemDisplayParameters, output) -> {

                    //LIMBS
                        output.accept(ModItems.BODYPART_LEFTARM);
                        output.accept(ModItems.BODYPART_RIGHTARM);
                        output.accept(ModItems.BODYPART_LEFTLEG);
                        output.accept(ModItems.BODYPART_RIGHTLEG);
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
                        output.accept(ModItems.BASECYBERWARE_LINEARFRAME);
                        output.accept(ModItems.BASECYBERWARE_CYBEREYES);
                        output.accept(ModItems.BASECYBERWARE_CYBERHEART);
                    //EYE UPGRADES
                        output.accept(ModItems.EYEUPGRADES_HUDLENS);
                        output.accept(ModItems.EYEUPGRADES_NAVIGATIONCHIP);
                        output.accept(ModItems.EYEUPGRADES_HUDJACK);
                        output.accept(ModItems.EYEUPGRADES_NIGHTVISION);
                        output.accept(ModItems.EYEUPGRADES_UNDERWATERVISION);
                        output.accept(ModItems.EYEUPGRADES_TARGETING);
                        output.accept(ModItems.EYEUPGRADES_ZOOM);
                    //ARM UPGRADES
                        output.accept(ModItems.ARMUPGRADES_ARMCANNON);
                        output.accept(ModItems.ARMUPGRADES_CLAWS);
                        output.accept(ModItems.ARMUPGRADES_CRAFTHANDS);
                        output.accept(ModItems.ARMUPGRADES_DRILLFIST);
                        output.accept(ModItems.ARMUPGRADES_FLYWHEEL);
                        output.accept(ModItems.ARMUPGRADES_FIRESTARTER);
                        output.accept(ModItems.ARMUPGRADES_ENGINEERSPHALANGES);
                        output.accept(ModItems.ARMUPGRADES_PNEUMATICWRIST);
                        output.accept(ModItems.ARMUPGRADES_REINFORCEDKNUCKLES);
                    //LEG UPGRADES
                        output.accept(ModItems.LEGUPGRADES_METALDETECTOR);
                        output.accept(ModItems.LEGUPGRADES_ANKLEBRACERS);
                        output.accept(ModItems.LEGUPGRADES_JUMPBOOST);
                        output.accept(ModItems.LEGUPGRADES_PROPELLERS);
                        output.accept(ModItems.LEGUPGRADES_SPURS);
                    //BONE UPGRADES
                        output.accept(ModItems.BONEUPGRADES_BONEBATTERY);
                        output.accept(ModItems.BONEUPGRADES_BONEFLEX);
                        output.accept(ModItems.BONEUPGRADES_BONELACING);
                        output.accept(ModItems.BONEUPGRADES_ELYTRA);
                        output.accept(ModItems.BONEUPGRADES_PIEZO);
                        output.accept(ModItems.BONEUPGRADES_SPINALINJECTOR);
                    //BRAIN UPGRADES
                        output.accept(ModItems.BRAINUPGRADES_EYEOFDEFENDER);
                        output.accept(ModItems.BRAINUPGRADES_CONCIOUSNESSTRANSMITTER);
                        output.accept(ModItems.BRAINUPGRADES_CORTICALSTACK);
                        output.accept(ModItems.BRAINUPGRADES_ENDERJAMMER);
                        output.accept(ModItems.BRAINUPGRADES_MATRIX);
                        output.accept(ModItems.BRAINUPGRADES_NEURALCONTEXTUALIZER);
                        output.accept(ModItems.BRAINUPGRADES_CYBERDECK);
                    //HEART UPGRADES
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
                    //MUSCLE UPGRADES
                        output.accept(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE);
                        output.accept(ModItems.MUSCLEUPGRADES_WIREDREFLEXES);
                    //WETWARE UPGRADES
                        output.accept(ModItems.WETWARE_FIREBREATHINGGLAND);
                        output.accept(ModItems.WETWARE_GILLS);
                        output.accept(ModItems.WETWARE_GUARDIANEYE);



                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
