package com.perigrine3.createcybernetics.datagen;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, CreateCybernetics.MODID, existingFileHelper);
    }

    private static ResourceLocation resourceLocation(String id) {
        return ResourceLocation.parse(id);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
//TOGGLEABLE CYBERWARE
        tag(ModTags.Items.TOGGLEABLE_CYBERWARE)
                .add(ModItems.EYEUPGRADES_ZOOM.get())
                .add(ModItems.EYEUPGRADES_NIGHTVISION.get())
                .add(ModItems.ARMUPGRADES_CLAWS.get())
                .add(ModItems.ARMUPGRADES_FIRESTARTER.get())
                .add(ModItems.SKINUPGRADES_CHROMATOPHORES.get())
                .add(ModItems.BRAINUPGRADES_IDEM.get())
                .add(ModItems.BRAINUPGRADES_MATRIX.get())
                .add(ModItems.BONEUPGRADES_SPINALINJECTOR.get())
                .add(ModItems.BONEUPGRADES_SANDEVISTAN.get())
                .add(ModItems.ARMUPGRADES_ARMCANNON.get())
                .addOptional(resourceLocation("createcybernetics:boneupgrades_elytra"));

//ARM CANNON AMMO
        tag(ModTags.Items.ARM_CANNON_AMMO)
                .addTag(Tags.Items.NUGGETS)
                .add(Items.ARROW)
                .add(Items.SPECTRAL_ARROW)
                .add(Items.TIPPED_ARROW)
                .add(Items.TNT)
                .add(Items.SNOWBALL)
                .add(Items.ENDER_PEARL)
                .add(Items.EGG)
                .add(Items.FIRE_CHARGE)
                .add(Items.FIREWORK_ROCKET)
                .add(Items.WIND_CHARGE);

//DATA SHARDS
        tag(ModTags.Items.DATA_SHARDS)
                .add(ModItems.DATA_SHARD_RED.get())
                .add(ModItems.DATA_SHARD_ORANGE.get())
                .add(ModItems.DATA_SHARD_YELLOW.get())
                .add(ModItems.DATA_SHARD_GREEN.get())
                .add(ModItems.DATA_SHARD_CYAN.get())
                .add(ModItems.DATA_SHARD_BLUE.get())
                .add(ModItems.DATA_SHARD_PURPLE.get())
                .add(ModItems.DATA_SHARD_PINK.get())
                .add(ModItems.DATA_SHARD_BROWN.get())
                .add(ModItems.DATA_SHARD_GRAY.get())
                .add(ModItems.DATA_SHARD_BLACK.get())
                .add(ModItems.DATA_SHARD_BIOCHIP.get());


//CYBERWARE
        tag(ModTags.Items.CYBERWARE_ITEM)
                .add(ModItems.BASECYBERWARE_RIGHTLEG.get())
                .add(ModItems.BASECYBERWARE_LEFTLEG.get())
                .add(ModItems.BASECYBERWARE_RIGHTARM.get())
                .add(ModItems.BASECYBERWARE_LEFTARM.get())
                .add(ModItems.BASECYBERWARE_CYBEREYES.get())
                .add(ModItems.BASECYBERWARE_LINEARFRAME.get())
                .add(ModItems.EYEUPGRADES_HUDLENS.get())
                .add(ModItems.EYEUPGRADES_HUDJACK.get())
                .add(ModItems.EYEUPGRADES_NIGHTVISION.get())
                .add(ModItems.EYEUPGRADES_TARGETING.get())
                .add(ModItems.EYEUPGRADES_UNDERWATERVISION.get())
                .add(ModItems.EYEUPGRADES_ZOOM.get())
                .add(ModItems.ARMUPGRADES_ARMCANNON.get())
                .add(ModItems.ARMUPGRADES_FLYWHEEL.get())
                .add(ModItems.ARMUPGRADES_CLAWS.get())
                .add(ModItems.ARMUPGRADES_CRAFTHANDS.get())
                .add(ModItems.ARMUPGRADES_DRILLFIST.get())
                .add(ModItems.ARMUPGRADES_FIRESTARTER.get())
                .add(ModItems.ARMUPGRADES_PNEUMATICWRIST.get())
                .add(ModItems.ARMUPGRADES_REINFORCEDKNUCKLES.get())
                .add(ModItems.LEGUPGRADES_METALDETECTOR.get())
                .add(ModItems.LEGUPGRADES_ANKLEBRACERS.get())
                .add(ModItems.LEGUPGRADES_JUMPBOOST.get())
                .add(ModItems.LEGUPGRADES_PROPELLERS.get())
                .add(ModItems.LEGUPGRADES_SPURS.get())
                .add(ModItems.LEGUPGRADES_OCELOTPAWS.get())
                .add(ModItems.BONEUPGRADES_BONEBATTERY.get())
                .add(ModItems.BONEUPGRADES_BONEFLEX.get())
                .add(ModItems.BONEUPGRADES_BONELACING.get())
                .add(ModItems.BONEUPGRADES_PIEZO.get())
                .add(ModItems.BONEUPGRADES_SPINALINJECTOR.get())
                .add(ModItems.BONEUPGRADES_SANDEVISTAN.get())
                .add(ModItems.BONEUPGRADES_CYBERSKULL.get())
                .add(ModItems.BRAINUPGRADES_EYEOFDEFENDER.get())
                .add(ModItems.BRAINUPGRADES_ENDERJAMMER.get())
                .add(ModItems.BRAINUPGRADES_MATRIX.get())
                .add(ModItems.BRAINUPGRADES_NEURALCONTEXTUALIZER.get())
                .add(ModItems.BRAINUPGRADES_CYBERDECK.get())
                .add(ModItems.BRAINUPGRADES_IDEM.get())
                .add(ModItems.BRAINUPGRADES_CHIPWARESLOTS.get())
                .add(ModItems.HEARTUPGRADES_CYBERHEART.get())
                .add(ModItems.HEARTUPGRADES_COUPLER.get())
                .add(ModItems.HEARTUPGRADES_CREEPERHEART.get())
                .add(ModItems.HEARTUPGRADES_DEFIBRILLATOR.get())
                .add(ModItems.HEARTUPGRADES_STEMCELL.get())
                .add(ModItems.HEARTUPGRADES_PLATELETS.get())
                .add(ModItems.LUNGSUPGRADES_HYPEROXYGENATION.get())
                .add(ModItems.LUNGSUPGRADES_OXYGEN.get())
                .add(ModItems.ORGANSUPGRADES_ADRENALINE.get())
                .add(ModItems.ORGANSUPGRADES_BATTERY.get())
                .add(ModItems.ORGANSUPGRADES_DIAMONDWAFERSTACK.get())
                .add(ModItems.ORGANSUPGRADES_DUALISTICCONVERTER.get())
                .add(ModItems.ORGANSUPGRADES_LIVERFILTER.get())
                .add(ModItems.ORGANSUPGRADES_MAGICCATALYST.get())
                .add(ModItems.ORGANSUPGRADES_METABOLIC.get())
                .add(ModItems.ORGANSUPGRADES_DENSEBATTERY.get())
                .add(ModItems.ORGANSUPGRADES_HEATENGINE.get())
                .add(ModItems.SKINUPGRADES_ARTERIALTURBINE.get())
                .add(ModItems.SKINUPGRADES_CHROMATOPHORES.get())
                .add(ModItems.SKINUPGRADES_SYNTHSKIN.get())
                .add(ModItems.SKINUPGRADES_IMMUNO.get())
                .add(ModItems.SKINUPGRADES_FACEPLATE.get())
                .add(ModItems.SKINUPGRADES_NETHERITEPLATING.get())
                .add(ModItems.SKINUPGRADES_SOLARSKIN.get())
                .add(ModItems.SKINUPGRADES_SUBDERMALARMOR.get())
                .add(ModItems.SKINUPGRADES_SUBDERMALSPIKES.get())
                .add(ModItems.SKINUPGRADES_SYNTHETICSETULES.get())
                .add(ModItems.SKINUPGRADES_METALPLATING.get())
                .add(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get())
                .add(ModItems.MUSCLEUPGRADES_WIREDREFLEXES.get())
                .addOptional(resourceLocation("createcybernetics:boneupgrades_elytra"))
                .addOptional(resourceLocation("createcybernetics:skinupgrades_sweat"))
                .addOptional(resourceLocation("createcybernetics:eyeupgrades_navigationchip"))
                .addOptional(resourceLocation("createcybernetics:brainupgrades_consciousnesstransmitter"))
                .addOptional(resourceLocation("createcybernetics:brainupgrades_corticalstack"));

//BODYPART DROPS
        tag(ModTags.Items.BODYPART_DROPS)
                .add(ModItems.BODYPART_SKELETON.get())
                .add(ModItems.BODYPART_BRAIN.get())
                .add(ModItems.BODYPART_EYEBALLS.get())
                .add(ModItems.BODYPART_HEART.get())
                .add(ModItems.BODYPART_LUNGS.get())
                .add(ModItems.BODYPART_LIVER.get())
                .add(ModItems.BODYPART_INTESTINES.get())
                .add(ModItems.BODYPART_MUSCLE.get());

//GRASSFED BODYPART DROPS
        tag(ModTags.Items.GRASSFED_BODYPART_DROPS)
                .add(ModItems.BODYPART_SKELETON.get())
                .add(ModItems.BODYPART_BRAIN.get())
                .add(ModItems.BODYPART_EYEBALLS.get())
                .add(ModItems.BODYPART_HEART.get())
                .add(ModItems.BODYPART_LUNGS.get())
                .add(ModItems.BODYPART_LIVER.get())
                .add(ModItems.WETWARE_GRASSFEDSTOMACH.get())
                .add(ModItems.BODYPART_MUSCLE.get());

//FISH BODYPART DROPS
        tag(ModTags.Items.FISH_BODYPART_DROPS)
                .add(ModItems.BODYPART_SKELETON.get())
                .add(ModItems.BODYPART_EYEBALLS.get())
                .add(ModItems.BODYPART_GILLS.get())
                .add(ModItems.BODYPART_MUSCLE.get());

//HUMANOID BODYPART DROPS
        tag(ModTags.Items.HUMANOID_BODYPART_DROPS)
                .add(ModItems.BODYPART_RIGHTLEG.get())
                .add(ModItems.BODYPART_LEFTLEG.get())
                .add(ModItems.BODYPART_RIGHTARM.get())
                .add(ModItems.BODYPART_LEFTARM.get())
                .add(ModItems.BODYPART_SKELETON.get())
                .add(ModItems.BODYPART_BRAIN.get())
                .add(ModItems.BODYPART_EYEBALLS.get())
                .add(ModItems.BODYPART_HEART.get())
                .add(ModItems.BODYPART_LUNGS.get())
                .add(ModItems.BODYPART_LIVER.get())
                .add(ModItems.BODYPART_INTESTINES.get())
                .add(ModItems.BODYPART_MUSCLE.get())
                .add(ModItems.BODYPART_SKIN.get());


//WETWARE/BODY PARTS
        tag(ModTags.Items.WETWARE_ITEM)
                .add(ModItems.BODYPART_RIGHTLEG.get())
                .add(ModItems.BODYPART_LEFTLEG.get())
                .add(ModItems.BODYPART_RIGHTARM.get())
                .add(ModItems.BODYPART_LEFTARM.get())
                .add(ModItems.BODYPART_SKELETON.get())
                .add(ModItems.BODYPART_BRAIN.get())
                .add(ModItems.BODYPART_EYEBALLS.get())
                .add(ModItems.BODYPART_HEART.get())
                .add(ModItems.BODYPART_LUNGS.get())
                .add(ModItems.BODYPART_LIVER.get())
                .add(ModItems.BODYPART_INTESTINES.get())
                .add(ModItems.BODYPART_MUSCLE.get())
                .add(ModItems.BODYPART_SKIN.get())
                .add(ModItems.BODYPART_GUARDIANRETINA.get())
                .add(ModItems.BODYPART_WARDENESOPHAGUS.get())
                .add(ModItems.BODYPART_GYROSCOPICBLADDER.get())
                .add(ModItems.BODYPART_SPINNERETTE.get())
                .add(ModItems.BODYPART_FIREGLAND.get())
                .add(ModItems.BODYPART_GILLS.get())

                .add(ModItems.WETWARE_FIREBREATHINGLUNGS.get())
                .add(ModItems.WETWARE_WATERBREATHINGLUNGS.get())
                .add(ModItems.WETWARE_GUARDIANEYE.get())
                .add(ModItems.WETWARE_SCULKLUNGS.get())
                .add(ModItems.WETWARE_TACTICALINKSAC.get())
                .add(ModItems.WETWARE_AEROSTASISGYROBLADDER.get())
                .add(ModItems.WETWARE_POLARBEARFUR.get())
                .add(ModItems.WETWARE_RAVAGERTENDONS.get())
                .add(ModItems.WETWARE_GRASSFEDSTOMACH.get())
                .add(ModItems.WETWARE_WEBSHOOTING_LEFTARM.get())
                .add(ModItems.WETWARE_WEBSHOOTING_RIGHTARM.get())
                .add(ModItems.WETWARE_WEBSHOOTINGINTESTINES.get())
                .add(ModItems.WETWARE_SPIDEREYES.get())
                .addOptional(resourceLocation("createcybernetics:wetware_blubber"));
//BODY PARTS
        tag(ModTags.Items.BODY_PARTS)
                .add(ModItems.BODYPART_RIGHTLEG.get())
                .add(ModItems.BODYPART_LEFTLEG.get())
                .add(ModItems.BODYPART_RIGHTARM.get())
                .add(ModItems.BODYPART_LEFTARM.get())
                .add(ModItems.BODYPART_SKELETON.get())
                .add(ModItems.BODYPART_BRAIN.get())
                .add(ModItems.BODYPART_EYEBALLS.get())
                .add(ModItems.BODYPART_HEART.get())
                .add(ModItems.BODYPART_LUNGS.get())
                .add(ModItems.BODYPART_LIVER.get())
                .add(ModItems.BODYPART_INTESTINES.get())
                .add(ModItems.BODYPART_MUSCLE.get())
                .add(ModItems.BODYPART_SKIN.get());
//BASE CYBERWARE
        tag(ModTags.Items.BASE_CYBERWARE)
                .add(ModItems.BASECYBERWARE_RIGHTLEG.get())
                .add(ModItems.BASECYBERWARE_LEFTLEG.get())
                .add(ModItems.BASECYBERWARE_RIGHTARM.get())
                .add(ModItems.BASECYBERWARE_LEFTARM.get())
                .add(ModItems.BASECYBERWARE_CYBEREYES.get())
                .add(ModItems.BASECYBERWARE_LINEARFRAME.get());
//ARM UPGRADES
        tag(ModTags.Items.ARM_UPGRADES)
                .add(ModItems.ARMUPGRADES_ARMCANNON.get())
                .add(ModItems.ARMUPGRADES_FLYWHEEL.get())
                .add(ModItems.ARMUPGRADES_CLAWS.get())
                .add(ModItems.ARMUPGRADES_CRAFTHANDS.get())
                .add(ModItems.ARMUPGRADES_DRILLFIST.get())
                .add(ModItems.ARMUPGRADES_FIRESTARTER.get())
                .add(ModItems.ARMUPGRADES_PNEUMATICWRIST.get())
                .add(ModItems.ARMUPGRADES_REINFORCEDKNUCKLES.get());
//LEG UPGRADES
        tag(ModTags.Items.LEG_UPGRADES)
                .add(ModItems.LEGUPGRADES_METALDETECTOR.get())
                .add(ModItems.LEGUPGRADES_ANKLEBRACERS.get())
                .add(ModItems.LEGUPGRADES_JUMPBOOST.get())
                .add(ModItems.LEGUPGRADES_PROPELLERS.get())
                .add(ModItems.LEGUPGRADES_SPURS.get())
                .add(ModItems.LEGUPGRADES_OCELOTPAWS.get());
//BONE UPGRADES
        tag(ModTags.Items.BONE_UPGRADES)
                .add(ModItems.BONEUPGRADES_BONEBATTERY.get())
                .add(ModItems.BONEUPGRADES_BONEFLEX.get())
                .add(ModItems.BONEUPGRADES_BONELACING.get())
                .add(ModItems.BONEUPGRADES_PIEZO.get())
                .add(ModItems.BONEUPGRADES_SPINALINJECTOR.get())
                .add(ModItems.BONEUPGRADES_SANDEVISTAN.get())
                .add(ModItems.BONEUPGRADES_CYBERSKULL.get())
                .addOptional(resourceLocation("createcybernetics:boneupgrades_elytra"));
//SKIN UPGRADES
        tag(ModTags.Items.SKIN_UPGRADES)
                .add(ModItems.SKINUPGRADES_ARTERIALTURBINE.get())
                .add(ModItems.SKINUPGRADES_CHROMATOPHORES.get())
                .add(ModItems.SKINUPGRADES_SYNTHSKIN.get())
                .add(ModItems.SKINUPGRADES_IMMUNO.get())
                .add(ModItems.SKINUPGRADES_FACEPLATE.get())
                .add(ModItems.SKINUPGRADES_NETHERITEPLATING.get())
                .add(ModItems.SKINUPGRADES_SOLARSKIN.get())
                .add(ModItems.SKINUPGRADES_SUBDERMALARMOR.get())
                .add(ModItems.SKINUPGRADES_SUBDERMALSPIKES.get())
                .add(ModItems.SKINUPGRADES_SYNTHETICSETULES.get())
                .add(ModItems.SKINUPGRADES_METALPLATING.get())
                .addOptional(resourceLocation("createcybernetics:skinupgrades_sweat"));
//ORGAN UPGRADES
        tag(ModTags.Items.ORGAN_UPGRADES)
                .add(ModItems.ORGANSUPGRADES_ADRENALINE.get())
                .add(ModItems.ORGANSUPGRADES_BATTERY.get())
                .add(ModItems.ORGANSUPGRADES_DIAMONDWAFERSTACK.get())
                .add(ModItems.ORGANSUPGRADES_DUALISTICCONVERTER.get())
                .add(ModItems.ORGANSUPGRADES_LIVERFILTER.get())
                .add(ModItems.ORGANSUPGRADES_MAGICCATALYST.get())
                .add(ModItems.ORGANSUPGRADES_METABOLIC.get())
                .add(ModItems.ORGANSUPGRADES_DENSEBATTERY.get())
                .add(ModItems.ORGANSUPGRADES_HEATENGINE.get());
//HEART UPGRADES
        tag(ModTags.Items.HEART_UPGRADES)
                .add(ModItems.HEARTUPGRADES_CYBERHEART.get())
                .add(ModItems.HEARTUPGRADES_COUPLER.get())
                .add(ModItems.HEARTUPGRADES_CREEPERHEART.get())
                .add(ModItems.HEARTUPGRADES_DEFIBRILLATOR.get())
                .add(ModItems.HEARTUPGRADES_STEMCELL.get())
                .add(ModItems.HEARTUPGRADES_PLATELETS.get());
//LUNG UPGRADES
        tag(ModTags.Items.LUNG_UPGRADES)
                .add(ModItems.LUNGSUPGRADES_HYPEROXYGENATION.get())
                .add(ModItems.LUNGSUPGRADES_OXYGEN.get());
//EYE UPGRADES
        tag(ModTags.Items.EYE_UPGRADES)
                .add(ModItems.EYEUPGRADES_HUDLENS.get())
                .add(ModItems.EYEUPGRADES_HUDJACK.get())
                .add(ModItems.EYEUPGRADES_NIGHTVISION.get())
                .add(ModItems.EYEUPGRADES_TARGETING.get())
                .add(ModItems.EYEUPGRADES_UNDERWATERVISION.get())
                .add(ModItems.EYEUPGRADES_ZOOM.get())
                .add(ModItems.EYEUPGRADES_TRAJECTORYCALCULATOR.get())
                .addOptional(resourceLocation("createcybernetics:eyeupgrades_navigationchip"));
//BRAIN UPGRADES
        tag(ModTags.Items.BRAIN_UPGRADES)
                .add(ModItems.BRAINUPGRADES_CYBERBRAIN.get())
                .add(ModItems.BRAINUPGRADES_EYEOFDEFENDER.get())
                .add(ModItems.BRAINUPGRADES_ENDERJAMMER.get())
                .add(ModItems.BRAINUPGRADES_MATRIX.get())
                .add(ModItems.BRAINUPGRADES_NEURALCONTEXTUALIZER.get())
                .add(ModItems.BRAINUPGRADES_CYBERDECK.get())
                .add(ModItems.BRAINUPGRADES_IDEM.get())
                .add(ModItems.BRAINUPGRADES_CHIPWARESLOTS.get())
                .addOptional(resourceLocation("createcybernetics:brainupgrades_consciousnesstransmitter"))
                .addOptional(resourceLocation("createcybernetics:brainupgrades_corticalstack"));

//MUSCLE UPGRADES
        tag(ModTags.Items.MUSCLE_UPGRADES)
                .add(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get())
                .add(ModItems.MUSCLEUPGRADES_WIREDREFLEXES.get());

//SCAVENGED CYBERWARE
        tag(ModTags.Items.SCAVENGED_CYBERWARE)
                .add(ModItems.SCAVENGED_RIGHTLEG.get())
                .add(ModItems.SCAVENGED_LEFTLEG.get())
                .add(ModItems.SCAVENGED_RIGHTARM.get())
                .add(ModItems.SCAVENGED_LEFTARM.get())
                .add(ModItems.SCAVENGED_CYBEREYES.get())
                .add(ModItems.SCAVENGED_LINEARFRAME.get())

                .add(ModItems.SCAVENGED_HUDLENS.get())
                .add(ModItems.SCAVENGED_HUDJACK.get())
                .add(ModItems.SCAVENGED_NIGHTVISION.get())
                .add(ModItems.SCAVENGED_TARGETING.get())
                .add(ModItems.SCAVENGED_UNDERWATERVISION.get())
                .add(ModItems.SCAVENGED_ZOOM.get())
                .add(ModItems.SCAVENGED_TRAJECTORYCALCULATOR.get())

                .add(ModItems.SCAVENGED_ARMCANNON.get())
                .add(ModItems.SCAVENGED_FLYWHEEL.get())
                .add(ModItems.SCAVENGED_CLAWS.get())
                .add(ModItems.SCAVENGED_CRAFTHANDS.get())
                .add(ModItems.SCAVENGED_DRILLFIST.get())
                .add(ModItems.SCAVENGED_FIRESTARTER.get())
                .add(ModItems.SCAVENGED_PNEUMATICWRIST.get())
                .add(ModItems.SCAVENGED_REINFORCEDKNUCKLES.get())

                .add(ModItems.SCAVENGED_METALDETECTOR.get())
                .add(ModItems.SCAVENGED_ANKLEBRACERS.get())
                .add(ModItems.SCAVENGED_JUMPBOOST.get())
                .add(ModItems.SCAVENGED_PROPELLERS.get())
                .add(ModItems.SCAVENGED_SPURS.get())
                .add(ModItems.SCAVENGED_OCELOTPAWS.get())

                .add(ModItems.SCAVENGED_BONEBATTERY.get())
                .add(ModItems.SCAVENGED_BONEFLEX.get())
                .add(ModItems.SCAVENGED_BONELACING.get())
                .add(ModItems.SCAVENGED_PIEZO.get())
                .add(ModItems.SCAVENGED_SPINALINJECTOR.get())
                .add(ModItems.SCAVENGED_SANDEVISTAN.get())

                .add(ModItems.SCAVENGED_EYEOFDEFENDER.get())
                .add(ModItems.SCAVENGED_ENDERJAMMER.get())
                .add(ModItems.SCAVENGED_MATRIX.get())
                .add(ModItems.SCAVENGED_NEURALCONTEXTUALIZER.get())
                .add(ModItems.SCAVENGED_CYBERDECK.get())
                .add(ModItems.SCAVENGED_CHIPWARESLOTS.get())
                .add(ModItems.SCAVENGED_IDEM.get())

                .add(ModItems.SCAVENGED_CYBERHEART.get())
                .add(ModItems.SCAVENGED_COUPLER.get())
                .add(ModItems.SCAVENGED_CREEPERHEART.get())
                .add(ModItems.SCAVENGED_DEFIBRILLATOR.get())
                .add(ModItems.SCAVENGED_STEMCELL.get())
                .add(ModItems.SCAVENGED_PLATELETS.get())

                .add(ModItems.SCAVENGED_HYPEROXYGENATION.get())
                .add(ModItems.SCAVENGED_OXYGEN.get())

                .add(ModItems.SCAVENGED_ADRENALINE.get())
                .add(ModItems.SCAVENGED_BATTERY.get())
                .add(ModItems.SCAVENGED_DIAMONDWAFERSTACK.get())
                .add(ModItems.SCAVENGED_DUALISTICCONVERTER.get())
                .add(ModItems.SCAVENGED_LIVERFILTER.get())
                .add(ModItems.SCAVENGED_METABOLIC.get())
                .add(ModItems.SCAVENGED_DENSEBATTERY.get())
                .add(ModItems.SCAVENGED_HEATENGINE.get())

                .add(ModItems.SCAVENGED_ARTERIALTURBINE.get())
                .add(ModItems.SCAVENGED_CHROMATOPHORES.get())
                .add(ModItems.SCAVENGED_SYNTHSKIN.get())
                .add(ModItems.SCAVENGED_IMMUNO.get())
                .add(ModItems.SCAVENGED_FACEPLATE.get())
                .add(ModItems.SCAVENGED_NETHERITEPLATING.get())
                .add(ModItems.SCAVENGED_SOLARSKIN.get())
                .add(ModItems.SCAVENGED_SUBDERMALARMOR.get())
                .add(ModItems.SCAVENGED_SUBDERMALSPIKES.get())
                .add(ModItems.SCAVENGED_SYNTHETICSETULES.get())
                .add(ModItems.SCAVENGED_METALPLATING.get())
                .add(ModItems.SCAVENGED_SYNTHMUSCLE.get())
                .add(ModItems.SCAVENGED_WIREDREFLEXES.get())
                .addOptional(resourceLocation("createcybernetics:scavenged_elytra"))
                .addOptional(resourceLocation("createcybernetics:scavenged_navigationchip"))
                .addOptional(resourceLocation("createcybernetics:scavenged_consciousnesstransmitter"))
                .addOptional(resourceLocation("createcybernetics:scavenged_corticalstack"))
                .addOptional(resourceLocation("createcybernetics:scavenged_sweat"));


        tag(ModTags.Items.BRAIN_ITEMS)
                .add(ModItems.BRAINUPGRADES_CYBERBRAIN.get())
                .add(ModItems.BODYPART_BRAIN.get());

        tag(ModTags.Items.EYE_ITEMS)
                .add(ModItems.BASECYBERWARE_CYBEREYES.get())
                .add(ModItems.WETWARE_SPIDEREYES.get())
                .add(ModItems.BODYPART_EYEBALLS.get());

        tag(ModTags.Items.SKIN_ITEMS)
                .add(ModItems.SKINUPGRADES_METALPLATING.get())
                .add(ModItems.SKINUPGRADES_SUBDERMALSPIKES.get())
                .add(ModItems.SKINUPGRADES_SUBDERMALARMOR.get())
                .add(ModItems.SKINUPGRADES_SOLARSKIN.get())
                .add(ModItems.SKINUPGRADES_NETHERITEPLATING.get())
                .add(ModItems.SKINUPGRADES_SYNTHSKIN.get())
                .add(ModItems.SKINUPGRADES_CHROMATOPHORES.get())
                .add(ModItems.WETWARE_POLARBEARFUR.get())
                .add(ModItems.BODYPART_SKIN.get())
                .addOptional(resourceLocation("createcybernetics:skinupgrades_sweat"));

        tag(ModTags.Items.MUSCLE_ITEMS)
                .add(ModItems.WETWARE_RAVAGERTENDONS.get())
                .add(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get())
                .add(ModItems.BODYPART_MUSCLE.get());

        tag(ModTags.Items.BONE_ITEMS)
                .add(ModItems.BASECYBERWARE_LINEARFRAME.get())
                .add(ModItems.BODYPART_SKELETON.get());

        tag(ModTags.Items.HEART_ITEMS)
                .add(ModItems.HEARTUPGRADES_CYBERHEART.get())
                .add(ModItems.BODYPART_HEART.get());

        tag(ModTags.Items.LUNGS_ITEMS)
                .add(ModItems.WETWARE_SCULKLUNGS.get())
                .add(ModItems.WETWARE_AEROSTASISGYROBLADDER.get())
                .add(ModItems.WETWARE_FIREBREATHINGLUNGS.get())
                .add(ModItems.BODYPART_LUNGS.get());

        tag(ModTags.Items.LIVER_ITEMS)
                .add(ModItems.ORGANSUPGRADES_LIVERFILTER.get())
                .add(ModItems.BODYPART_LIVER.get());

        tag(ModTags.Items.INTESTINES_ITEMS)
                .add(ModItems.WETWARE_TACTICALINKSAC.get())
                .add(ModItems.WETWARE_GRASSFEDSTOMACH.get())
                .add(ModItems.WETWARE_WEBSHOOTINGINTESTINES.get())
                .add(ModItems.BODYPART_INTESTINES.get());

        tag(ModTags.Items.LEFTARM_ITEMS)
                .add(ModItems.BASECYBERWARE_LEFTARM.get())
                .add(ModItems.BASECYBERWARE_LEFTARM_COPPERPLATED.get())
                .add(ModItems.BASECYBERWARE_LEFTARM_IRONPLATED.get())
                .add(ModItems.BASECYBERWARE_LEFTARM_GOLDPLATED.get())
                .add(ModItems.WETWARE_WEBSHOOTING_LEFTARM.get())
                .add(ModItems.BODYPART_LEFTARM.get());

        tag(ModTags.Items.RIGHTARM_ITEMS)
                .add(ModItems.BASECYBERWARE_RIGHTARM.get())
                .add(ModItems.BASECYBERWARE_RIGHTARM_COPPERPLATED.get())
                .add(ModItems.BASECYBERWARE_RIGHTARM_IRONPLATED.get())
                .add(ModItems.BASECYBERWARE_RIGHTARM_GOLDPLATED.get())
                .add(ModItems.WETWARE_WEBSHOOTING_RIGHTARM.get())
                .add(ModItems.BODYPART_RIGHTARM.get());

        tag(ModTags.Items.LEFTLEG_ITEMS)
                .add(ModItems.BASECYBERWARE_LEFTLEG.get())
                .add(ModItems.BASECYBERWARE_LEFTLEG_COPPERPLATED.get())
                .add(ModItems.BASECYBERWARE_LEFTLEG_IRONPLATED.get())
                .add(ModItems.BASECYBERWARE_LEFTLEG_GOLDPLATED.get())
                .add(ModItems.BODYPART_LEFTLEG.get());

        tag(ModTags.Items.RIGHTLEG_ITEMS)
                .add(ModItems.BASECYBERWARE_RIGHTLEG.get())
                .add(ModItems.BASECYBERWARE_RIGHTLEG_COPPERPLATED.get())
                .add(ModItems.BASECYBERWARE_RIGHTLEG_IRONPLATED.get())
                .add(ModItems.BASECYBERWARE_RIGHTLEG_GOLDPLATED.get())
                .add(ModItems.BODYPART_RIGHTLEG.get());



        tag(ModTags.Items.BRAIN_REPLACEMENTS)
                .add(ModItems.BRAINUPGRADES_CYBERBRAIN.get());

        tag(ModTags.Items.EYE_REPLACEMENTS)
                .add(ModItems.WETWARE_SPIDEREYES.get())
                .add(ModItems.BASECYBERWARE_CYBEREYES.get());

        tag(ModTags.Items.SKIN_REPLACEMENTS)
                .add(ModItems.SKINUPGRADES_CHROMATOPHORES.get());

        tag(ModTags.Items.MUSCLE_REPLACEMENTS)
                .add(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get());

        tag(ModTags.Items.BONE_REPLACEMENTS)
                .add(ModItems.BASECYBERWARE_LINEARFRAME.get());

        tag(ModTags.Items.HEART_REPLACEMENTS)
                .add(ModItems.HEARTUPGRADES_CYBERHEART.get());

        tag(ModTags.Items.LIVER_REPLACEMENTS)
                .add(ModItems.ORGANSUPGRADES_LIVERFILTER.get());


        tag(ModTags.Items.LEFTARM_REPLACEMENTS)
                .add(ModItems.BASECYBERWARE_LEFTARM_COPPERPLATED.get())
                .add(ModItems.BASECYBERWARE_LEFTARM_IRONPLATED.get())
                .add(ModItems.BASECYBERWARE_LEFTARM_GOLDPLATED.get())
                .add(ModItems.WETWARE_WEBSHOOTING_LEFTARM.get())
                .add(ModItems.BASECYBERWARE_LEFTARM.get());

        tag(ModTags.Items.RIGHTARM_REPLACEMENTS)
                .add(ModItems.BASECYBERWARE_RIGHTARM_COPPERPLATED.get())
                .add(ModItems.BASECYBERWARE_RIGHTARM_IRONPLATED.get())
                .add(ModItems.BASECYBERWARE_RIGHTARM_GOLDPLATED.get())
                .add(ModItems.WETWARE_WEBSHOOTING_RIGHTARM.get())
                .add(ModItems.BASECYBERWARE_RIGHTARM.get());

        tag(ModTags.Items.LEFTLEG_REPLACEMENTS)
                .add(ModItems.BASECYBERWARE_LEFTLEG_COPPERPLATED.get())
                .add(ModItems.BASECYBERWARE_LEFTLEG_IRONPLATED.get())
                .add(ModItems.BASECYBERWARE_LEFTLEG_GOLDPLATED.get())
                .add(ModItems.BASECYBERWARE_LEFTLEG.get());

        tag(ModTags.Items.RIGHTLEG_REPLACEMENTS)
                .add(ModItems.BASECYBERWARE_RIGHTLEG_COPPERPLATED.get())
                .add(ModItems.BASECYBERWARE_RIGHTLEG_IRONPLATED.get())
                .add(ModItems.BASECYBERWARE_RIGHTLEG_GOLDPLATED.get())
                .add(ModItems.BASECYBERWARE_RIGHTLEG.get());


        tag(ModTags.Items.DEFAULTS_FAIL_AS_MISSING_WHEN_UNPOWERED)
                .add(ModItems.BASECYBERWARE_CYBEREYES.get())
                .add(ModItems.HEARTUPGRADES_CYBERHEART.get());





// OFFAL
        tag(ModTags.Items.OFFAL)
                .add(ModItems.BODYPART_BRAIN.get())
                .add(ModItems.BODYPART_LUNGS.get())
                .add(ModItems.BODYPART_LIVER.get())
                .add(ModItems.BODYPART_INTESTINES.get())
                .add(ModItems.BODYPART_EYEBALLS.get())
                .add(ModItems.BODYPART_SKIN.get())
                .add(ModItems.BODYPART_MUSCLE.get());




// VANILLA
        tag(Tags.Items.POTIONS)
                .add(ModItems.NEUROPOZYNE_AUTOINJECTOR.get());

        tag(Tags.Items.NUGGETS)
                .add(ModItems.TITANIUMNUGGET.get());

        tag(Tags.Items.INGOTS)
                .add(ModItems.TITANIUMINGOT.get());

        tag(Tags.Items.MUSIC_DISCS)
                .add(ModItems.MUSIC_DISC_CYBERPSYCHO.get())
                .add(ModItems.MUSIC_DISC_THE_GRID.get())
                .add(ModItems.MUSIC_DISC_NEON_OVERLORDS.get())
                .add(ModItems.MUSIC_DISC_CYBERPSYCHO.get());

        tag(ItemTags.TRIMMABLE_ARMOR)
                .add(ModItems.BASECYBERWARE_RIGHTLEG.get())
                .add(ModItems.BASECYBERWARE_LEFTLEG.get())
                .add(ModItems.BASECYBERWARE_RIGHTARM.get())
                .add(ModItems.BASECYBERWARE_LEFTARM.get())
                .add(ModItems.SKINUPGRADES_METALPLATING.get())
                .add(ModItems.BASECYBERWARE_RIGHTLEG_COPPERPLATED.get())
                .add(ModItems.BASECYBERWARE_LEFTLEG_COPPERPLATED.get())
                .add(ModItems.BASECYBERWARE_RIGHTARM_COPPERPLATED.get())
                .add(ModItems.BASECYBERWARE_LEFTARM_COPPERPLATED.get())
                .add(ModItems.BASECYBERWARE_RIGHTLEG_IRONPLATED.get())
                .add(ModItems.BASECYBERWARE_LEFTLEG_IRONPLATED.get())
                .add(ModItems.BASECYBERWARE_RIGHTARM_IRONPLATED.get())
                .add(ModItems.BASECYBERWARE_LEFTARM_IRONPLATED.get())
                .add(ModItems.BASECYBERWARE_RIGHTLEG_GOLDPLATED.get())
                .add(ModItems.BASECYBERWARE_LEFTLEG_GOLDPLATED.get())
                .add(ModItems.BASECYBERWARE_RIGHTARM_GOLDPLATED.get())
                .add(ModItems.BASECYBERWARE_LEFTARM_GOLDPLATED.get());

        tag(ModTags.Items.C_FOODS_RAW_MEATS)
                .add(ModItems.BODYPART_BRAIN.get())
                .add(ModItems.BODYPART_HEART.get())
                .add(ModItems.BODYPART_LUNGS.get())
                .add(ModItems.BODYPART_INTESTINES.get())
                .add(ModItems.BODYPART_EYEBALLS.get())
                .add(ModItems.BODYPART_MUSCLE.get())
                .add(ModItems.BODYPART_SKIN.get())
                .add(ModItems.BODYPART_LIVER.get());

        tag(vectorwing.farmersdelight.common.tag.ModTags.KNIVES)
                .add(ModItems.BONE_SAW.get());

        tag(ModTags.Items.C_TITANIUM)
                .add(ModItems.TITANIUMINGOT.get());
    }
}
