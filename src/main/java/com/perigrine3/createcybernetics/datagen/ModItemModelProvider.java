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
        basicItem(ModItems.NEUROPOZYNE.get());
        basicItem(ModItems.EXPCAPSULE.get());
        basicItem(ModItems.CYBERPUNK_DISC.get());
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
        basicItem(ModItems.BASECYBERWARE_RIGHTLEG.get());
        basicItem(ModItems.BASECYBERWARE_LEFTLEG.get());
        basicItem(ModItems.BASECYBERWARE_RIGHTARM.get());
        basicItem(ModItems.BASECYBERWARE_LEFTARM.get());
        basicItem(ModItems.BASECYBERWARE_CYBEREYES.get());
        basicItem(ModItems.BASECYBERWARE_LINEARFRAME.get());
        basicItem(ModItems.EYEUPGRADES_HUDLENS.get());
        basicItem(ModItems.EYEUPGRADES_NAVIGATIONCHIP.get());
        basicItem(ModItems.EYEUPGRADES_HUDJACK.get());
        basicItem(ModItems.EYEUPGRADES_NIGHTVISION.get());
        basicItem(ModItems.EYEUPGRADES_TARGETING.get());
        basicItem(ModItems.EYEUPGRADES_UNDERWATERVISION.get());
        basicItem(ModItems.EYEUPGRADES_ZOOM.get());
        basicItem(ModItems.ARMUPGRADES_ARMCANNON.get());
        basicItem(ModItems.ARMUPGRADES_FLYWHEEL.get());
        basicItem(ModItems.ARMUPGRADES_CLAWS.get());
        basicItem(ModItems.ARMUPGRADES_CRAFTHANDS.get());
        basicItem(ModItems.ARMUPGRADES_DRILLFIST.get());
        basicItem(ModItems.ARMUPGRADES_FIRESTARTER.get());
        basicItem(ModItems.ARMUPGRADES_PNEUMATICWRIST.get());
        basicItem(ModItems.ARMUPGRADES_REINFORCEDKNUCKLES.get());
        basicItem(ModItems.LEGUPGRADES_METALDETECTOR.get());
        basicItem(ModItems.LEGUPGRADES_ANKLEBRACERS.get());
        basicItem(ModItems.LEGUPGRADES_JUMPBOOST.get());
        basicItem(ModItems.LEGUPGRADES_PROPELLERS.get());
        basicItem(ModItems.LEGUPGRADES_SPURS.get());
        basicItem(ModItems.BONEUPGRADES_BONEBATTERY.get());
        basicItem(ModItems.BONEUPGRADES_BONEFLEX.get());
        basicItem(ModItems.BONEUPGRADES_BONELACING.get());
        basicItem(ModItems.BONEUPGRADES_ELYTRA.get());
        basicItem(ModItems.BONEUPGRADES_PIEZO.get());
        basicItem(ModItems.BONEUPGRADES_SPINALINJECTOR.get());
        basicItem(ModItems.BRAINUPGRADES_EYEOFDEFENDER.get());
        basicItem(ModItems.BRAINUPGRADES_CONCIOUSNESSTRANSMITTER.get());
        basicItem(ModItems.BRAINUPGRADES_CORTICALSTACK.get());
        basicItem(ModItems.BRAINUPGRADES_ENDERJAMMER.get());
        basicItem(ModItems.BRAINUPGRADES_MATRIX.get());
        basicItem(ModItems.BRAINUPGRADES_NEURALCONTEXTUALIZER.get());
        basicItem(ModItems.BRAINUPGRADES_CYBERDECK.get());
        basicItem(ModItems.BRAINUPGRADES_IDEM.get());
        basicItem(ModItems.HEARTUPGRADES_CYBERHEART.get());
        basicItem(ModItems.HEARTUPGRADES_COUPLER.get());
        basicItem(ModItems.HEARTUPGRADES_CREEPERHEART.get());
        basicItem(ModItems.HEARTUPGRADES_DEFIBRILLATOR.get());
        basicItem(ModItems.HEARTUPGRADES_STEMCELL.get());
        basicItem(ModItems.HEARTUPGRADES_PLATELETS.get());
        basicItem(ModItems.LUNGSUPGRADES_HYPEROXYGENATION.get());
        basicItem(ModItems.LUNGSUPGRADES_OXYGEN.get());
        basicItem(ModItems.ORGANSUPGRADES_ADRENALINE.get());
        basicItem(ModItems.ORGANSUPGRADES_BATTERY.get());
        basicItem(ModItems.ORGANSUPGRADES_DIAMONDWAFERSTACK.get());
        basicItem(ModItems.ORGANSUPGRADES_DUALISTICCONVERTER.get());
        basicItem(ModItems.ORGANSUPGRADES_LIVERFILTER.get());
        basicItem(ModItems.ORGANSUPGRADES_MAGICCATALYST.get());
        basicItem(ModItems.ORGANSUPGRADES_METABOLIC.get());
        basicItem(ModItems.ORGANSUPGRADES_DENSEBATTERY.get());
        basicItem(ModItems.SKINUPGRADES_ARTERIALTURBINE.get());
        basicItem(ModItems.SKINUPGRADES_CHROMATOPHORES.get());
        basicItem(ModItems.SKINUPGRADES_SYNTHSKIN.get());
        basicItem(ModItems.SKINUPGRADES_IMMUNO.get());
        basicItem(ModItems.SKINUPGRADES_FACEPLATE.get());
        basicItem(ModItems.SKINUPGRADES_NETHERITEPLATING.get());
        basicItem(ModItems.SKINUPGRADES_SOLARSKIN.get());
        basicItem(ModItems.SKINUPGRADES_SUBDERMALARMOR.get());
        basicItem(ModItems.SKINUPGRADES_SUBDERMALSPIKES.get());
        basicItem(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get());
        basicItem(ModItems.MUSCLEUPGRADES_WIREDREFLEXES.get());
        basicItem(ModItems.WETWARE_FIREBREATHINGGLAND.get());
        basicItem(ModItems.WETWARE_GILLS.get());
        basicItem(ModItems.WETWARE_GUARDIANEYE.get());
        basicItem(ModItems.WETWARE_POLARBEARFUR.get());
        basicItem(ModItems.WETWARE_RAVAGERTENDONS.get());

    }
}
