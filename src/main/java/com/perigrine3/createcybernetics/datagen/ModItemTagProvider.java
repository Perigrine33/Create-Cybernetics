package com.perigrine3.createcybernetics.datagen;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, CreateCybernetics.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
//CYBERWARE
        tag(ModTags.Items.CYBERWARE_ITEM)
                .add(ModItems.BASECYBERWARE_RIGHTLEG.get())
                .add(ModItems.BASECYBERWARE_LEFTLEG.get())
                .add(ModItems.BASECYBERWARE_RIGHTARM.get())
                .add(ModItems.BASECYBERWARE_LEFTARM.get())
                .add(ModItems.BASECYBERWARE_CYBEREYES.get())
                .add(ModItems.BASECYBERWARE_LINEARFRAME.get())
                .add(ModItems.EYEUPGRADES_HUDLENS.get())
                .add(ModItems.EYEUPGRADES_NAVIGATIONCHIP.get())
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
                .add(ModItems.BONEUPGRADES_BONEBATTERY.get())
                .add(ModItems.BONEUPGRADES_BONEFLEX.get())
                .add(ModItems.BONEUPGRADES_BONELACING.get())
                .add(ModItems.BONEUPGRADES_ELYTRA.get())
                .add(ModItems.BONEUPGRADES_PIEZO.get())
                .add(ModItems.BONEUPGRADES_SPINALINJECTOR.get())
                .add(ModItems.BRAINUPGRADES_EYEOFDEFENDER.get())
                .add(ModItems.BRAINUPGRADES_CONCIOUSNESSTRANSMITTER.get())
                .add(ModItems.BRAINUPGRADES_CORTICALSTACK.get())
                .add(ModItems.BRAINUPGRADES_ENDERJAMMER.get())
                .add(ModItems.BRAINUPGRADES_MATRIX.get())
                .add(ModItems.BRAINUPGRADES_NEURALCONTEXTUALIZER.get())
                .add(ModItems.BRAINUPGRADES_CYBERDECK.get())
                .add(ModItems.BRAINUPGRADES_IDEM.get())
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
                .add(ModItems.SKINUPGRADES_ARTERIALTURBINE.get())
                .add(ModItems.SKINUPGRADES_CHROMATOPHORES.get())
                .add(ModItems.SKINUPGRADES_SYNTHSKIN.get())
                .add(ModItems.SKINUPGRADES_IMMUNO.get())
                .add(ModItems.SKINUPGRADES_FACEPLATE.get())
                .add(ModItems.SKINUPGRADES_NETHERITEPLATING.get())
                .add(ModItems.SKINUPGRADES_SOLARSKIN.get())
                .add(ModItems.SKINUPGRADES_SUBDERMALARMOR.get())
                .add(ModItems.SKINUPGRADES_SUBDERMALSPIKES.get())
                .add(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get())
                .add(ModItems.MUSCLEUPGRADES_WIREDREFLEXES.get());
//WETWARE
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
                .add(ModItems.WETWARE_FIREBREATHINGGLAND.get())
                .add(ModItems.WETWARE_GILLS.get())
                .add(ModItems.WETWARE_GUARDIANEYE.get())
                .add(ModItems.WETWARE_BLUBBER.get())
                .add(ModItems.WETWARE_SCULKLUNGS.get())
                .add(ModItems.WETWARE_TACTICALINKSAC.get())
                .add(ModItems.WETWARE_AEROSTASISGYROBLADDER.get())
                .add(ModItems.WETWARE_POLARBEARFUR.get())
                .add(ModItems.WETWARE_RAVAGERTENDONS.get());
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
                .add(ModItems.BONEUPGRADES_ELYTRA.get())
                .add(ModItems.BONEUPGRADES_PIEZO.get())
                .add(ModItems.BONEUPGRADES_SPINALINJECTOR.get());
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
                .add(ModItems.SKINUPGRADES_SUBDERMALSPIKES.get());
//ORGAN UPGRADES
        tag(ModTags.Items.ORGAN_UPGRADES)
                .add(ModItems.ORGANSUPGRADES_ADRENALINE.get())
                .add(ModItems.ORGANSUPGRADES_BATTERY.get())
                .add(ModItems.ORGANSUPGRADES_DIAMONDWAFERSTACK.get())
                .add(ModItems.ORGANSUPGRADES_DUALISTICCONVERTER.get())
                .add(ModItems.ORGANSUPGRADES_LIVERFILTER.get())
                .add(ModItems.ORGANSUPGRADES_MAGICCATALYST.get())
                .add(ModItems.ORGANSUPGRADES_METABOLIC.get())
                .add(ModItems.ORGANSUPGRADES_DENSEBATTERY.get());
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
                .add(ModItems.EYEUPGRADES_NAVIGATIONCHIP.get())
                .add(ModItems.EYEUPGRADES_HUDJACK.get())
                .add(ModItems.EYEUPGRADES_NIGHTVISION.get())
                .add(ModItems.EYEUPGRADES_TARGETING.get())
                .add(ModItems.EYEUPGRADES_UNDERWATERVISION.get())
                .add(ModItems.EYEUPGRADES_ZOOM.get());
//BRAIN UPGRADES
        tag(ModTags.Items.BRAIN_UPGRADES)
                .add(ModItems.BRAINUPGRADES_EYEOFDEFENDER.get())
                .add(ModItems.BRAINUPGRADES_CONCIOUSNESSTRANSMITTER.get())
                .add(ModItems.BRAINUPGRADES_CORTICALSTACK.get())
                .add(ModItems.BRAINUPGRADES_ENDERJAMMER.get())
                .add(ModItems.BRAINUPGRADES_MATRIX.get())
                .add(ModItems.BRAINUPGRADES_NEURALCONTEXTUALIZER.get())
                .add(ModItems.BRAINUPGRADES_CYBERDECK.get())
                .add(ModItems.BRAINUPGRADES_IDEM.get());
//MUSCLE UPGRADES
        tag(ModTags.Items.MUSCLE_UPGRADES)
                .add(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get())
                .add(ModItems.MUSCLEUPGRADES_WIREDREFLEXES.get());
    }
}
