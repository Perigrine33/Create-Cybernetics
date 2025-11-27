package com.perigrine3.createcybernetics.item;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateCybernetics.MODID);


//BASIC ITEMS & COMPONENTS
    public static final DeferredItem<Item> RAWTITANIUM = ITEMS.register("rawtitanium",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TITANIUMINGOT = ITEMS.register("titaniumingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CRUSHEDTITANIUM = ITEMS.register("crushedtitanium",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TITANIUMNUGGET = ITEMS.register("titaniumnugget",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TITANIUMSHEET = ITEMS.register("titaniumsheet",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> CYBERPUNK_DISC = ITEMS.register("cyberpunk_disc",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> COMPONENT_ACTUATOR = ITEMS.register("component_actuator",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COMPONENT_FIBEROPTICS = ITEMS.register("component_fiberoptics",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COMPONENT_FULLERENE = ITEMS.register("component_fullerene",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COMPONENT_DIODES = ITEMS.register("component_diodes",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COMPONENT_PLATING = ITEMS.register("component_plating",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COMPONENT_GRAPHICSCARD = ITEMS.register("component_graphicscard",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COMPONENT_SSD = ITEMS.register("component_ssd",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COMPONENT_STORAGE = ITEMS.register("component_storage",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COMPONENT_SYNTHNERVES = ITEMS.register("component_synthnerves",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COMPONENT_MESH = ITEMS.register("component_mesh",
            () -> new Item(new Item.Properties()));



//BODY PARTS
    public static final DeferredItem<Item> BODYPART_RIGHTLEG = ITEMS.register("bodypart_rightleg",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BODYPART_LEFTLEG = ITEMS.register("bodypart_leftleg",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BODYPART_RIGHTARM = ITEMS.register("bodypart_rightarm",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BODYPART_LEFTARM = ITEMS.register("bodypart_leftarm",
            () -> new Item(new Item.Properties()));


//INTERNAL WETWARE
    public static final DeferredItem<Item> BODYPART_SKELETON = ITEMS.register("bodypart_skeleton",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BODYPART_BRAIN = ITEMS.register("bodypart_brain",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BODYPART_EYEBALLS = ITEMS.register("bodypart_eyeballs",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BODYPART_HEART = ITEMS.register("bodypart_heart",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BODYPART_LUNGS = ITEMS.register("bodypart_lungs",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BODYPART_LIVER = ITEMS.register("bodypart_liver",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BODYPART_INTESTINES = ITEMS.register("bodypart_intestines",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BODYPART_MUSCLE = ITEMS.register("bodypart_muscle",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BODYPART_SKIN = ITEMS.register("bodypart_skin",
            () -> new Item(new Item.Properties()));

//BASE CYBERWARE
    public static final DeferredItem<Item> BASECYBERWARE_RIGHTLEG = ITEMS.register("basecyberware_rightleg",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BASECYBERWARE_LEFTLEG = ITEMS.register("basecyberware_leftleg",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BASECYBERWARE_RIGHTARM = ITEMS.register("basecyberware_rightarm",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BASECYBERWARE_LEFTARM = ITEMS.register("basecyberware_leftarm",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BASECYBERWARE_LINEARFRAME = ITEMS.register("basecyberware_linearframe",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BASECYBERWARE_CYBEREYES = ITEMS.register("basecyberware_cybereyes",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BASECYBERWARE_CYBERHEART = ITEMS.register("basecyberware_cyberheart",
            () -> new Item(new Item.Properties()));

//EYE UPGRADES
    public static final DeferredItem<Item> EYEUPGRADES_HUDLENS = ITEMS.register("eyeupgrades_hudlens",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> EYEUPGRADES_NAVIGATIONCHIP = ITEMS.register("eyeupgrades_navigationchip",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> EYEUPGRADES_HUDJACK = ITEMS.register("eyeupgrades_hudjack",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> EYEUPGRADES_NIGHTVISION = ITEMS.register("eyeupgrades_nightvision",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> EYEUPGRADES_TARGETING = ITEMS.register("eyeupgrades_targeting",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> EYEUPGRADES_UNDERWATERVISION = ITEMS.register("eyeupgrades_underwatervision",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> EYEUPGRADES_ZOOM = ITEMS.register("eyeupgrades_zoom",
        () -> new Item(new Item.Properties()));

//ARM UPGRADES
    public static final DeferredItem<Item> ARMUPGRADES_ARMCANNON = ITEMS.register("armupgrades_armcannon",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ARMUPGRADES_FLYWHEEL = ITEMS.register("armupgrades_flywheel",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ARMUPGRADES_CLAWS = ITEMS.register("armupgrades_claws",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ARMUPGRADES_CRAFTHANDS = ITEMS.register("armupgrades_crafthands",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ARMUPGRADES_DRILLFIST = ITEMS.register("armupgrades_drillfist",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ARMUPGRADES_ENGINEERSPHALANGES = ITEMS.register("armupgrades_engineersphalanges",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ARMUPGRADES_FIRESTARTER = ITEMS.register("armupgrades_firestarter",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ARMUPGRADES_PNEUMATICWRIST = ITEMS.register("armupgrades_pneumaticwrist",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ARMUPGRADES_REINFORCEDKNUCKLES = ITEMS.register("armupgrades_reinforcedknuckles",
        () -> new Item(new Item.Properties()));

//LEG UPGRADES
    public static final DeferredItem<Item> LEGUPGRADES_METALDETECTOR = ITEMS.register("legupgrades_metaldetector",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LEGUPGRADES_ANKLEBRACERS = ITEMS.register("legupgrades_anklebracers",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LEGUPGRADES_JUMPBOOST = ITEMS.register("legupgrades_jumpboost",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LEGUPGRADES_PROPELLERS = ITEMS.register("legupgrades_propellers",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LEGUPGRADES_SPURS = ITEMS.register("legupgrades_spurs",
        () -> new Item(new Item.Properties()));

//BONE UPGRADES
    public static final DeferredItem<Item> BONEUPGRADES_BONEBATTERY = ITEMS.register("boneupgrades_bonebattery",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BONEUPGRADES_BONEFLEX = ITEMS.register("boneupgrades_boneflex",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BONEUPGRADES_BONELACING = ITEMS.register("boneupgrades_bonelacing",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BONEUPGRADES_ELYTRA = ITEMS.register("boneupgrades_elytra",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BONEUPGRADES_PIEZO = ITEMS.register("boneupgrades_piezo",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BONEUPGRADES_SPINALINJECTOR = ITEMS.register("boneupgrades_spinalinjector",
        () -> new Item(new Item.Properties()));

//BRAIN UPGRADES
    public static final DeferredItem<Item> BRAINUPGRADES_EYEOFDEFENDER = ITEMS.register("brainupgrade_eyeofdefender",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BRAINUPGRADES_CONCIOUSNESSTRANSMITTER = ITEMS.register("brainupgrades_consciousnesstransmitter",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BRAINUPGRADES_CORTICALSTACK = ITEMS.register("brainupgrades_corticalstack",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BRAINUPGRADES_ENDERJAMMER = ITEMS.register("brainupgrades_enderjammer",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BRAINUPGRADES_MATRIX = ITEMS.register("brainupgrades_matrix",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BRAINUPGRADES_NEURALCONTEXTUALIZER = ITEMS.register("brainupgrades_neuralcontextualizer",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BRAINUPGRADES_CYBERDECK = ITEMS.register("brainupgrades_cyberdeck",
        () -> new Item(new Item.Properties()));

//HEART UPGRADES
    public static final DeferredItem<Item> HEARTUPGRADES_COUPLER = ITEMS.register("heartupgrades_coupler",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> HEARTUPGRADES_CREEPERHEART = ITEMS.register("heartupgrades_creeperheart",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> HEARTUPGRADES_DEFIBRILLATOR = ITEMS.register("heartupgrades_defibrillator",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> HEARTUPGRADES_STEMCELL = ITEMS.register("heartupgrades_stemcell",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> HEARTUPGRADES_PLATELETS = ITEMS.register("heartupgrades_platelets",
        () -> new Item(new Item.Properties()));

//LUNG UPGRADES
    public static final DeferredItem<Item> LUNGSUPGRADES_HYPEROXYGENATION = ITEMS.register("lungsupgrades_hyperoxygenation",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LUNGSUPGRADES_OXYGEN = ITEMS.register("lungsupgrades_oxygen",
        () -> new Item(new Item.Properties()));

//ORGAN UPGRADES
    public static final DeferredItem<Item> ORGANSUPGRADES_ADRENALINE = ITEMS.register("organsupgrades_adrenaline",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ORGANSUPGRADES_BATTERY = ITEMS.register("organsupgrades_battery",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ORGANSUPGRADES_DIAMONDWAFERSTACK = ITEMS.register("organsupgrades_diamondwaferstack",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ORGANSUPGRADES_DUALISTICCONVERTER = ITEMS.register("organsupgrades_dualisticconverter",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ORGANSUPGRADES_LIVERFILTER = ITEMS.register("organsupgrades_liverfilter",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ORGANSUPGRADES_MAGICCATALYST = ITEMS.register("organsupgrades_magiccatalyst",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ORGANSUPGRADES_METABOLIC = ITEMS.register("organsupgrades_metabolic",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ORGANSUPGRADES_DENSEBATTERY = ITEMS.register("organsupgrade_densebattery",
        () -> new Item(new Item.Properties()));

//SKIN UPGRADES
    public static final DeferredItem<Item> SKINUPGRADES_ARTERIALTURBINE = ITEMS.register("skinupgrades_arterialturbine",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SKINUPGRADES_CHROMATOPHORES = ITEMS.register("skinupgrades_chromatophores",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SKINUPGRADES_SYNTHSKIN = ITEMS.register("skinupgrades_synthskin",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SKINUPGRADES_IMMUNO = ITEMS.register("skinupgrades_immuno",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SKINUPGRADES_FACEPLATE = ITEMS.register("skinupgrades_faceplate",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SKINUPGRADES_NETHERITEPLATING = ITEMS.register("skinupgrades_netheriteplating",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SKINUPGRADES_SOLARSKIN = ITEMS.register("skinupgrades_solarskin",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SKINUPGRADES_SUBDERMALARMOR = ITEMS.register("skinupgrades_subdermalarmor",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SKINUPGRADES_SUBDERMALSPIKES = ITEMS.register("skinupgrades_subdermalspikes",
        () -> new Item(new Item.Properties()));

//MUSCLE UPGRADES
    public static final DeferredItem<Item> MUSCLEUPGRADES_SYNTHMUSCLE = ITEMS.register("muscleupgrades_synthmuscle",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MUSCLEUPGRADES_WIREDREFLEXES = ITEMS.register("muscleupgrades_wiredreflexes",
        () -> new Item(new Item.Properties()));

//WETWARE UPGRADES
    public static final DeferredItem<Item> WETWARE_FIREBREATHINGGLAND = ITEMS.register("wetware_firebreathinggland",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> WETWARE_GILLS = ITEMS.register("wetware_gills",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> WETWARE_GUARDIANEYE = ITEMS.register("wetware_guardianeye",
        () -> new Item(new Item.Properties()));




    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
