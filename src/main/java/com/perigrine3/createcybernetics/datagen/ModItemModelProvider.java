package com.perigrine3.createcybernetics.datagen;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CreateCybernetics.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.TITANIUMINGOT.get());
        basicItem(ModItems.TITANIUMSHEET.get());
        basicItem(ModItems.TITANIUMNUGGET.get());
        basicItem(ModItems.RAWTITANIUM.get());
        basicItem(ModItems.CRUSHEDTITANIUM.get());

        basicItem(ModItems.EYEUPGRADEBASE.get());
        basicItem(ModItems.TITANIUM_HAND.get());
        basicItem(ModItems.FRONTAL_LOBE.get());
        basicItem(ModItems.PARIETAL_LOBE.get());
        basicItem(ModItems.TEMPORAL_LOBE.get());
        basicItem(ModItems.OCCIPITAL_LOBE.get());
        basicItem(ModItems.CEREBELLUM.get());
        basicItem(ModItems.NEUROPOZYNE_AUTOINJECTOR.get());
        basicItem(ModItems.EMPTY_AUTOINJECTOR.get());
        basicItem(ModItems.XP_CAPSULE.get());
        basicItem(ModItems.FACEPLATE.get());
        basicItem(ModItems.NETHERITE_QPU.get());
        basicItem(ModItems.DATURA_FLOWER.get());
        basicItem(ModItems.DATURA_SEED_POD.get());

        basicItem(ModItems.COPPER_UPGRADE_TEMPLATE.get());
        basicItem(ModItems.IRON_UPGRADE_TEMPLATE.get());
        basicItem(ModItems.GOLD_UPGRADE_TEMPLATE.get());

        //basicItem(ModItems.QUICKHACK_BURNING.get());
        //basicItem(ModItems.QUICKHACK_REBOOT.get());
        //basicItem(ModItems.QUICKHACK_ITEM.get());

        basicItem(ModItems.MUSIC_DISC_CYBERPSYCHO.get());
        basicItem(ModItems.MUSIC_DISC_NEON_OVERLORDS.get());
        basicItem(ModItems.MUSIC_DISC_NEUROHACK.get());
        basicItem(ModItems.MUSIC_DISC_THE_GRID.get());



        basicItem(ModItems.COOKED_BRAIN.get());
        basicItem(ModItems.COOKED_HEART.get());
        basicItem(ModItems.COOKED_LIVER.get());
        basicItem(ModItems.BONE_MARROW.get());

        if (ModItems.ANDOUILLE_SAUSAGE != null && ModItems.ROASTED_ANDOUILLE != null && ModItems.GROUND_OFFAL != null && ModItems.BRAIN_STEW != null ) {
            basicItem(ModItems.ANDOUILLE_SAUSAGE.get());
            basicItem(ModItems.ROASTED_ANDOUILLE.get());
            basicItem(ModItems.GROUND_OFFAL.get());
            basicItem(ModItems.BRAIN_STEW.get());
        }




        withExistingParent(ModItems.SMASHER_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.CYBERZOMBIE_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.CYBERSKELETON_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));


//COMPONENT
        {
        basicItem(ModItems.COMPONENT_ACTUATOR.get());
        basicItem(ModItems.COMPONENT_FIBEROPTICS.get());
        basicItem(ModItems.COMPONENT_WIRING.get());
        basicItem(ModItems.COMPONENT_DIODES.get());
        basicItem(ModItems.COMPONENT_PLATING.get());
        basicItem(ModItems.COMPONENT_GRAPHICSCARD.get());
        basicItem(ModItems.COMPONENT_SSD.get());
        basicItem(ModItems.COMPONENT_STORAGE.get());
        basicItem(ModItems.COMPONENT_SYNTHNERVES.get());
        basicItem(ModItems.COMPONENT_MESH.get());

        if (ModItems.COMPONENT_LED != null && ModItems.COMPONENT_TITANIUMROD != null) {
            basicItem(ModItems.COMPONENT_LED.get());
            basicItem(ModItems.COMPONENT_TITANIUMROD.get());
        }
    }

//BODY PART
        {
        basicItem(ModItems.BODYPART_RIGHTLEG.get());
        basicItem(ModItems.BODYPART_LEFTLEG.get());
        basicItem(ModItems.BODYPART_RIGHTARM.get());
        basicItem(ModItems.BODYPART_LEFTARM.get());
        basicItem(ModItems.BODYPART_SKELETON.get());
        basicItem(ModItems.BODYPART_BRAIN.get());
        basicItem(ModItems.BODYPART_EYEBALLS.get());
        basicItem(ModItems.BODYPART_HEART.get());
        basicItem(ModItems.BODYPART_LUNGS.get());
        basicItem(ModItems.BODYPART_LIVER.get());
        basicItem(ModItems.BODYPART_INTESTINES.get());
        basicItem(ModItems.BODYPART_MUSCLE.get());
        basicItem(ModItems.BODYPART_SKIN.get());
    }

//ANIMAL BODY PARTS
        {
        basicItem(ModItems.BODYPART_GUARDIANRETINA.get());
        basicItem(ModItems.BODYPART_WARDENESOPHAGUS.get());
        basicItem(ModItems.BODYPART_GYROSCOPICBLADDER.get());
        basicItem(ModItems.BODYPART_SPINNERETTE.get());
        basicItem(ModItems.BODYPART_FIREGLAND.get());
        basicItem(ModItems.BODYPART_GILLS.get());
    }

//BASE
        {
        basicItem(ModItems.BASECYBERWARE_LINEARFRAME.get());

        basicItem(ModItems.BASECYBERWARE_LEFTARM_COPPERPLATED.get());
        basicItem(ModItems.BASECYBERWARE_RIGHTARM_COPPERPLATED.get());
        basicItem(ModItems.BASECYBERWARE_LEFTLEG_COPPERPLATED.get());
        basicItem(ModItems.BASECYBERWARE_RIGHTLEG_COPPERPLATED.get());

        basicItem(ModItems.BASECYBERWARE_LEFTARM_IRONPLATED.get());
        basicItem(ModItems.BASECYBERWARE_RIGHTARM_IRONPLATED.get());
        basicItem(ModItems.BASECYBERWARE_LEFTLEG_IRONPLATED.get());
        basicItem(ModItems.BASECYBERWARE_RIGHTLEG_IRONPLATED.get());

        basicItem(ModItems.BASECYBERWARE_LEFTARM_GOLDPLATED.get());
        basicItem(ModItems.BASECYBERWARE_RIGHTARM_GOLDPLATED.get());
        basicItem(ModItems.BASECYBERWARE_LEFTLEG_GOLDPLATED.get());
        basicItem(ModItems.BASECYBERWARE_RIGHTLEG_GOLDPLATED.get());
    }

//EYES
        {
        basicItem(ModItems.EYEUPGRADES_HUDLENS.get());

            if (ModItems.EYEUPGRADES_NAVIGATIONCHIP != null) {
                basicItem(ModItems.EYEUPGRADES_NAVIGATIONCHIP.get());
            }

        basicItem(ModItems.EYEUPGRADES_HUDJACK.get());
        basicItem(ModItems.EYEUPGRADES_NIGHTVISION.get());
        basicItem(ModItems.EYEUPGRADES_TARGETING.get());
        basicItem(ModItems.EYEUPGRADES_UNDERWATERVISION.get());
        basicItem(ModItems.EYEUPGRADES_ZOOM.get());
        basicItem(ModItems.EYEUPGRADES_TRAJECTORYCALCULATOR.get());
    }

//ARMS
        {
        basicItem(ModItems.ARMUPGRADES_ARMCANNON.get());
        basicItem(ModItems.ARMUPGRADES_FLYWHEEL.get());
        basicItem(ModItems.ARMUPGRADES_CLAWS.get());
        basicItem(ModItems.ARMUPGRADES_CRAFTHANDS.get());
        basicItem(ModItems.ARMUPGRADES_DRILLFIST.get());
        basicItem(ModItems.ARMUPGRADES_FIRESTARTER.get());
        basicItem(ModItems.ARMUPGRADES_PNEUMATICWRIST.get());
        basicItem(ModItems.ARMUPGRADES_REINFORCEDKNUCKLES.get());
    }

//LEGS
        {
        basicItem(ModItems.LEGUPGRADES_METALDETECTOR.get());
        basicItem(ModItems.LEGUPGRADES_ANKLEBRACERS.get());
        basicItem(ModItems.LEGUPGRADES_JUMPBOOST.get());
        basicItem(ModItems.LEGUPGRADES_PROPELLERS.get());
        basicItem(ModItems.LEGUPGRADES_SPURS.get());
    }

//BONE
        {
        basicItem(ModItems.BONEUPGRADES_BONEBATTERY.get());
        basicItem(ModItems.BONEUPGRADES_BONEFLEX.get());
        basicItem(ModItems.BONEUPGRADES_BONELACING.get());

            if (ModItems.BONEUPGRADES_ELYTRA != null) {
                basicItem(ModItems.BONEUPGRADES_ELYTRA.get());
            }

        basicItem(ModItems.BONEUPGRADES_PIEZO.get());
        basicItem(ModItems.BONEUPGRADES_SPINALINJECTOR.get());
        basicItem(ModItems.BONEUPGRADES_SANDEVISTAN.get());
        basicItem(ModItems.BONEUPGRADES_CYBERSKULL.get());
    }

//BRAIN
        {
        basicItem(ModItems.BRAINUPGRADES_CYBERBRAIN.get());
        basicItem(ModItems.BRAINUPGRADES_EYEOFDEFENDER.get());

            if (ModItems.BRAINUPGRADES_CONSCIOUSNESSTRANSMITTER != null && ModItems.BRAINUPGRADES_CORTICALSTACK != null) {
                basicItem(ModItems.BRAINUPGRADES_CONSCIOUSNESSTRANSMITTER.get());
                basicItem(ModItems.BRAINUPGRADES_CORTICALSTACK.get());
            }

        basicItem(ModItems.BRAINUPGRADES_ENDERJAMMER.get());
        basicItem(ModItems.BRAINUPGRADES_MATRIX.get());
        basicItem(ModItems.BRAINUPGRADES_NEURALCONTEXTUALIZER.get());
        basicItem(ModItems.BRAINUPGRADES_CYBERDECK.get());
        basicItem(ModItems.BRAINUPGRADES_IDEM.get());
        basicItem(ModItems.BRAINUPGRADES_CHIPWARESLOTS.get());

            if (ModItems.BRAINUPGRADES_SPELLJAMMER != null) {
                basicItem(ModItems.BRAINUPGRADES_SPELLJAMMER.get());
            }
    }

//HEART
        {
        basicItem(ModItems.HEARTUPGRADES_CYBERHEART.get());
        basicItem(ModItems.HEARTUPGRADES_COUPLER.get());
        basicItem(ModItems.HEARTUPGRADES_CREEPERHEART.get());
        basicItem(ModItems.HEARTUPGRADES_DEFIBRILLATOR.get());
        basicItem(ModItems.HEARTUPGRADES_STEMCELL.get());
        basicItem(ModItems.HEARTUPGRADES_PLATELETS.get());
    }

//LUNGS
        {
        basicItem(ModItems.LUNGSUPGRADES_HYPEROXYGENATION.get());
        basicItem(ModItems.LUNGSUPGRADES_OXYGEN.get());
    }

//ORGANS
        {
        basicItem(ModItems.ORGANSUPGRADES_ADRENALINE.get());
        basicItem(ModItems.ORGANSUPGRADES_BATTERY.get());
        basicItem(ModItems.ORGANSUPGRADES_DIAMONDWAFERSTACK.get());
        basicItem(ModItems.ORGANSUPGRADES_DUALISTICCONVERTER.get());
        basicItem(ModItems.ORGANSUPGRADES_LIVERFILTER.get());
        basicItem(ModItems.ORGANSUPGRADES_MAGICCATALYST.get());
        basicItem(ModItems.ORGANSUPGRADES_METABOLIC.get());
        basicItem(ModItems.ORGANSUPGRADES_DENSEBATTERY.get());
        basicItem(ModItems.ORGANSUPGRADES_HEATENGINE.get());
            if (ModItems.ORGANSUPGRADES_MANABATTERY != null) {
                basicItem(ModItems.ORGANSUPGRADES_MANABATTERY.get());
            }
    }

//SKIN
        {
        basicItem(ModItems.SKINUPGRADES_ARTERIALTURBINE.get());
        basicItem(ModItems.SKINUPGRADES_CHROMATOPHORES.get());
        basicItem(ModItems.SKINUPGRADES_SYNTHSKIN.get());
        basicItem(ModItems.SKINUPGRADES_IMMUNO.get());
        basicItem(ModItems.SKINUPGRADES_FACEPLATE.get());
        basicItem(ModItems.SKINUPGRADES_NETHERITEPLATING.get());
        basicItem(ModItems.SKINUPGRADES_SOLARSKIN.get());
        basicItem(ModItems.SKINUPGRADES_SUBDERMALARMOR.get());
        basicItem(ModItems.SKINUPGRADES_SUBDERMALSPIKES.get());
        basicItem(ModItems.SKINUPGRADES_SYNTHETICSETULES.get());

            if (ModItems.SKINUPGRADES_SWEAT != null) {
                basicItem(ModItems.SKINUPGRADES_SWEAT.get());
            }
            if (ModItems.SKINUPGRADES_MANASKIN != null) {
                basicItem(ModItems.SKINUPGRADES_MANASKIN.get());
            }
    }

//MUSCLE
        {
        basicItem(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get());
        basicItem(ModItems.MUSCLEUPGRADES_WIREDREFLEXES.get());
    }

//WETWARE
        {
            if (ModItems.WETWARE_BLUBBER != null) {
                basicItem(ModItems.WETWARE_BLUBBER.get());
            }

            basicItem(ModItems.WETWARE_FIREBREATHINGLUNGS.get());
            basicItem(ModItems.WETWARE_WATERBREATHINGLUNGS.get());
            basicItem(ModItems.WETWARE_GUARDIANEYE.get());
            basicItem(ModItems.WETWARE_POLARBEARFUR.get());
            basicItem(ModItems.WETWARE_RAVAGERTENDONS.get());
            basicItem(ModItems.WETWARE_SCULKLUNGS.get());
            basicItem(ModItems.WETWARE_TACTICALINKSAC.get());
            basicItem(ModItems.WETWARE_AEROSTASISGYROBLADDER.get());
            basicItem(ModItems.WETWARE_GRASSFEDSTOMACH.get());
            basicItem(ModItems.WETWARE_WEBSHOOTINGINTESTINES.get());
            basicItem(ModItems.WETWARE_WEBSHOOTING_RIGHTARM.get());
            basicItem(ModItems.WETWARE_WEBSHOOTING_LEFTARM.get());
            basicItem(ModItems.WETWARE_SPIDEREYES.get());
        }



//SCAVENGED
        {
            basicItem(ModItems.SCAVENGED_RIGHTLEG.get());
            basicItem(ModItems.SCAVENGED_LEFTLEG.get());
            basicItem(ModItems.SCAVENGED_RIGHTARM.get());
            basicItem(ModItems.SCAVENGED_LEFTARM.get());
            basicItem(ModItems.SCAVENGED_CYBEREYES.get());
            basicItem(ModItems.SCAVENGED_LINEARFRAME.get());
            basicItem(ModItems.SCAVENGED_HUDLENS.get());

            if (ModItems.SCAVENGED_NAVIGATIONCHIP != null) {
                basicItem(ModItems.SCAVENGED_NAVIGATIONCHIP.get());
            }

            basicItem(ModItems.SCAVENGED_HUDJACK.get());
            basicItem(ModItems.SCAVENGED_NIGHTVISION.get());
            basicItem(ModItems.SCAVENGED_TARGETING.get());
            basicItem(ModItems.SCAVENGED_UNDERWATERVISION.get());
            basicItem(ModItems.SCAVENGED_ZOOM.get());
            basicItem(ModItems.SCAVENGED_TRAJECTORYCALCULATOR.get());
            basicItem(ModItems.SCAVENGED_ARMCANNON.get());
            basicItem(ModItems.SCAVENGED_FLYWHEEL.get());
            basicItem(ModItems.SCAVENGED_CLAWS.get());
            basicItem(ModItems.SCAVENGED_CRAFTHANDS.get());
            basicItem(ModItems.SCAVENGED_DRILLFIST.get());
            basicItem(ModItems.SCAVENGED_FIRESTARTER.get());
            basicItem(ModItems.SCAVENGED_PNEUMATICWRIST.get());
            basicItem(ModItems.SCAVENGED_REINFORCEDKNUCKLES.get());
            basicItem(ModItems.SCAVENGED_METALDETECTOR.get());
            basicItem(ModItems.SCAVENGED_ANKLEBRACERS.get());
            basicItem(ModItems.SCAVENGED_JUMPBOOST.get());
            basicItem(ModItems.SCAVENGED_PROPELLERS.get());
            basicItem(ModItems.SCAVENGED_SPURS.get());
            basicItem(ModItems.SCAVENGED_OCELOTPAWS.get());
            basicItem(ModItems.SCAVENGED_BONEBATTERY.get());
            basicItem(ModItems.SCAVENGED_BONEFLEX.get());
            basicItem(ModItems.SCAVENGED_BONELACING.get());

            if (ModItems.SCAVENGED_ELYTRA != null) {
                basicItem(ModItems.SCAVENGED_ELYTRA.get());
            }

            basicItem(ModItems.SCAVENGED_PIEZO.get());
            basicItem(ModItems.SCAVENGED_SPINALINJECTOR.get());
            basicItem(ModItems.SCAVENGED_SANDEVISTAN.get());
            basicItem(ModItems.SCAVENGED_EYEOFDEFENDER.get());

            if (ModItems.SCAVENGED_CONSCIOUSNESSTRANSMITTER != null && ModItems.SCAVENGED_CORTICALSTACK != null) {
                basicItem(ModItems.SCAVENGED_CONSCIOUSNESSTRANSMITTER.get());
                basicItem(ModItems.SCAVENGED_CORTICALSTACK.get());
            }

            basicItem(ModItems.SCAVENGED_ENDERJAMMER.get());
            basicItem(ModItems.SCAVENGED_MATRIX.get());
            basicItem(ModItems.SCAVENGED_NEURALCONTEXTUALIZER.get());
            basicItem(ModItems.SCAVENGED_CYBERDECK.get());
            basicItem(ModItems.SCAVENGED_IDEM.get());
            basicItem(ModItems.SCAVENGED_CHIPWARESLOTS.get());
            basicItem(ModItems.SCAVENGED_CYBERHEART.get());
            basicItem(ModItems.SCAVENGED_COUPLER.get());
            basicItem(ModItems.SCAVENGED_CREEPERHEART.get());
            basicItem(ModItems.SCAVENGED_DEFIBRILLATOR.get());
            basicItem(ModItems.SCAVENGED_STEMCELL.get());
            basicItem(ModItems.SCAVENGED_PLATELETS.get());
            basicItem(ModItems.SCAVENGED_HYPEROXYGENATION.get());
            basicItem(ModItems.SCAVENGED_OXYGEN.get());
            basicItem(ModItems.SCAVENGED_ADRENALINE.get());
            basicItem(ModItems.SCAVENGED_BATTERY.get());
            basicItem(ModItems.SCAVENGED_DIAMONDWAFERSTACK.get());
            basicItem(ModItems.SCAVENGED_DUALISTICCONVERTER.get());
            basicItem(ModItems.SCAVENGED_LIVERFILTER.get());
            basicItem(ModItems.SCAVENGED_METABOLIC.get());
            basicItem(ModItems.SCAVENGED_DENSEBATTERY.get());
            basicItem(ModItems.SCAVENGED_HEATENGINE.get());
            basicItem(ModItems.SCAVENGED_ARTERIALTURBINE.get());
            basicItem(ModItems.SCAVENGED_CHROMATOPHORES.get());
            basicItem(ModItems.SCAVENGED_SYNTHSKIN.get());
            basicItem(ModItems.SCAVENGED_IMMUNO.get());
            basicItem(ModItems.SCAVENGED_FACEPLATE.get());
            basicItem(ModItems.SCAVENGED_NETHERITEPLATING.get());
            basicItem(ModItems.SCAVENGED_SOLARSKIN.get());
            basicItem(ModItems.SCAVENGED_SUBDERMALARMOR.get());
            basicItem(ModItems.SCAVENGED_SUBDERMALSPIKES.get());
            basicItem(ModItems.SCAVENGED_SYNTHETICSETULES.get());
            basicItem(ModItems.SCAVENGED_METALPLATING.get());

            if (ModItems.SCAVENGED_SWEAT != null) {
                basicItem(ModItems.SCAVENGED_SWEAT.get());
            }

            basicItem(ModItems.SCAVENGED_SYNTHMUSCLE.get());
            basicItem(ModItems.SCAVENGED_WIREDREFLEXES.get());
        }

// DATA SHARDS
        {
            basicItem(ModItems.DATA_SHARD_RED.get());
            basicItem(ModItems.DATA_SHARD_ORANGE.get());
            basicItem(ModItems.DATA_SHARD_YELLOW.get());
            basicItem(ModItems.DATA_SHARD_GREEN.get());
            basicItem(ModItems.DATA_SHARD_CYAN.get());
            basicItem(ModItems.DATA_SHARD_BLUE.get());
            basicItem(ModItems.DATA_SHARD_PURPLE.get());
            basicItem(ModItems.DATA_SHARD_PINK.get());
            basicItem(ModItems.DATA_SHARD_BROWN.get());
            basicItem(ModItems.DATA_SHARD_GRAY.get());
            basicItem(ModItems.DATA_SHARD_BLACK.get());
            basicItem(ModItems.DATA_SHARD_BIOCHIP.get());
        }
    }
}
