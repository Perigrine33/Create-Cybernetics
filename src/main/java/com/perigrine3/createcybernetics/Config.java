package com.perigrine3.createcybernetics;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue HUMANITY = BUILDER
            .comment("Base Humanity Value")
            .defineInRange("humanity", 100, 50, 1000);

    public static final ModConfigSpec.BooleanValue KEEP_CYBERWARE = BUILDER
            .comment("Keep Cyberware on Death")
            .comment("If true, cyberware will not drop and will persist through death.")
            .define("keepCyberware", false);

    public static final ModConfigSpec.BooleanValue SURGERY_DAMAGE_SCALING = BUILDER
            .comment("Scale Surgery Damage")
            .comment("If true, the surgery chamber will apply damage that scales with the amount of cyberware being removed.")
            .comment("If false, the surgery chamber will apply 10 damage.")
            .define("scaleSurgeryDamage", false);

    public static final ModConfigSpec.BooleanValue EPILEPSY_MODE = BUILDER
            .comment("Epilepsy Mode")
            .comment("Activate this to disable the cyberware rejection overlay. Good if you or a friend has epilepsy.")
            .define("epilepsyMode", false);

    public static final ModConfigSpec.EnumValue<ConfigValues.TattooUploadMode> TATTOO_UPLOAD_MODE = BUILDER
            .comment("Tattoo Upload Mode")
            .comment("SERVER_FILES_ONLY: Only PNGs manually placed in the server tattoo folder are available.")
            .comment("OP_ONLY_AUTO_APPROVE: Only operators may upload tattoos, and uploads are immediately approved.")
            .comment("ANY_PLAYER_PENDING_APPROVAL: Any player may upload tattoos, but uploads must be approved by an admin before appearing.")
            .comment("ANY_PLAYER_AUTO_APPROVE: Any player may upload tattoos, and uploads are immediately approved. Not recommended for public servers.")
            .defineEnum("tattooUploadMode", ConfigValues.TattooUploadMode.OP_ONLY_AUTO_APPROVE);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> ENGINEERING_DECONSTRUCT_ROLLS = BUILDER
            .comment("Engineering Table deconstruction rolls for regular cyberware.")
            .comment("Format per entry: item_id,min,max,weight")
            .comment("Example: minecraft:iron_ingot,0,3,3")
            .defineListAllowEmpty(
                    List.of("engineeringDeconstructRolls"),
                    defaultEngineeringDeconstructRolls(),
                    obj -> obj instanceof String
            );

    public static final ModConfigSpec.ConfigValue<List<? extends String>> ENGINEERING_SCAVENGED_DECONSTRUCT_ROLLS = BUILDER
            .comment("Engineering Table deconstruction rolls for scavenged cyberware.")
            .comment("Format per entry: item_id,min,max,weight")
            .comment("Example: minecraft:iron_ingot,0,3,3")
            .defineListAllowEmpty(
                    List.of("engineeringScavengedDeconstructRolls"),
                    defaultEngineeringScavengedDeconstructRolls(),
                    obj -> obj instanceof String
            );

    public static final ModConfigSpec.ConfigValue<List<? extends String>> ENTITY_SLOT_ROLLS = BUILDER
            .comment("Entity cyberware slot roll settings.")
            .comment("Format per entry: tableId,slot,rollChance,minRolls,maxRolls")
            .comment("Example: cyberzombie,BRAIN,0.25,1,3")
            .defineListAllowEmpty(
                    List.of("entitySlotRolls"),
                    defaultEntitySlotRolls(),
                    obj -> obj instanceof String
            );

    public static final ModConfigSpec.ConfigValue<List<? extends String>> ENTITY_CYBERWARE_ROLLS = BUILDER
            .comment("Entity cyberware weighted roll entries.")
            .comment("Format per entry: tableId,item_id,slot,weight")
            .comment("Optional mod gate format: tableId,item_id,slot,weight,required_modid")
            .comment("Example: cyberzombie,createcybernetics:brainupgrades_cyberbrain,BRAIN,2")
            .comment("Example: cyberzombie,createcybernetics:brainupgrades_spelljammer,BRAIN,2,irons_spellbooks")
            .defineListAllowEmpty(
                    List.of("entityCyberwareRolls"),
                    defaultEntityCyberwareRolls(),
                    obj -> obj instanceof String
            );

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static void bake() {
        ConfigValues.BASE_HUMANITY = HUMANITY.get();
        ConfigValues.KEEP_CYBERWARE = KEEP_CYBERWARE.get();
        ConfigValues.SURGERY_DAMAGE_SCALING = SURGERY_DAMAGE_SCALING.get();

        ConfigValues.ENGINEERING_DECONSTRUCT_ROLLS = parseEngineeringRolls(ENGINEERING_DECONSTRUCT_ROLLS.get());
        ConfigValues.ENGINEERING_SCAVENGED_DECONSTRUCT_ROLLS = parseEngineeringRolls(ENGINEERING_SCAVENGED_DECONSTRUCT_ROLLS.get());

        ConfigValues.ENTITY_SLOT_ROLLS = parseEntitySlotRolls(ENTITY_SLOT_ROLLS.get());
        ConfigValues.ENTITY_CYBERWARE_ROLLS = parseEntityCyberwareRolls(ENTITY_CYBERWARE_ROLLS.get());
    }

    @SubscribeEvent
    public static void onConfigLoading(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == SPEC) bake();
    }

    @SubscribeEvent
    public static void onConfigReloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) bake();
    }

    private static List<String> defaultEngineeringDeconstructRolls() {
        List<String> defaults = new ArrayList<>();

        defaults.add("createcybernetics:component_actuator,0,5,4");
        defaults.add("createcybernetics:component_fiberoptics,0,5,4");
        defaults.add("createcybernetics:component_wiring,1,5,5");
        defaults.add("createcybernetics:component_diodes,0,5,4");
        defaults.add("createcybernetics:component_plating,0,5,5");
        defaults.add("createcybernetics:component_graphicscard,0,5,4");
        defaults.add("createcybernetics:component_ssd,0,5,4");
        defaults.add("createcybernetics:component_storage,0,5,4");
        defaults.add("createcybernetics:component_synthnerves,0,5,3");
        defaults.add("createcybernetics:component_mesh,0,5,2");
        defaults.add("createcybernetics:component_led,0,5,3");
        defaults.add("createcybernetics:component_titaniumrod,0,5,2");

        return defaults;
    }

    private static List<String> defaultEngineeringScavengedDeconstructRolls() {
        List<String> defaults = new ArrayList<>();

        defaults.add("createcybernetics:component_actuator,0,2,3");
        defaults.add("createcybernetics:component_fiberoptics,0,2,3");
        defaults.add("createcybernetics:component_wiring,0,3,5");
        defaults.add("createcybernetics:component_diodes,0,2,2");
        defaults.add("createcybernetics:component_plating,0,2,5");
        defaults.add("createcybernetics:component_graphicscard,0,2,2");
        defaults.add("createcybernetics:component_ssd,0,2,3");
        defaults.add("createcybernetics:component_storage,0,2,4");
        defaults.add("createcybernetics:component_synthnerves,0,2,1");
        defaults.add("createcybernetics:component_mesh,0,2,2");
        defaults.add("createcybernetics:component_led,0,2,3");
        defaults.add("createcybernetics:component_titaniumrod,0,2,2");

        return defaults;
    }

    private static List<ConfigValues.EngineeringRoll> parseEngineeringRolls(List<? extends String> rawEntries) {
        List<ConfigValues.EngineeringRoll> parsed = new ArrayList<>();
        if (rawEntries == null) return parsed;

        for (String raw : rawEntries) {
            if (raw == null) continue;

            String line = raw.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(",");
            if (parts.length != 4) {
                CreateCybernetics.LOGGER.warn("Skipping invalid engineering roll config entry '{}': expected 4 comma-separated values", line);
                continue;
            }

            String itemId = parts[0].trim();
            String minText = parts[1].trim();
            String maxText = parts[2].trim();
            String weightText = parts[3].trim();

            try {
                ResourceLocation id = ResourceLocation.parse(itemId);
                Item item = BuiltInRegistries.ITEM.get(id);
                if (item == null || item == BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace("air"))) {
                    CreateCybernetics.LOGGER.warn("Skipping engineering roll config entry '{}': item '{}' was not found", line, itemId);
                    continue;
                }

                int min = Integer.parseInt(minText);
                int max = Integer.parseInt(maxText);
                int weight = Integer.parseInt(weightText);

                if (min < 0) {
                    CreateCybernetics.LOGGER.warn("Skipping engineering roll config entry '{}': min cannot be negative", line);
                    continue;
                }
                if (max < min) {
                    CreateCybernetics.LOGGER.warn("Skipping engineering roll config entry '{}': max cannot be less than min", line);
                    continue;
                }
                if (weight <= 0) {
                    CreateCybernetics.LOGGER.warn("Skipping engineering roll config entry '{}': weight must be > 0", line);
                    continue;
                }

                parsed.add(new ConfigValues.EngineeringRoll(item, min, max, weight));
            } catch (Exception ex) {
                CreateCybernetics.LOGGER.warn("Skipping invalid engineering roll config entry '{}'", line, ex);
            }
        }

        return parsed;
    }

    private static List<String> defaultEntitySlotRolls() {
        List<String> defaults = new ArrayList<>();

        addDefaultEntitySlotRolls(defaults, "cyberzombie");
        addDefaultEntitySlotRolls(defaults, "cyberskeleton");
        addDefaultEntitySlotRolls(defaults, "smasher");

        addDefaultEntitySlotRolls(defaults, "hogboy");
        addDefaultEntitySlotRolls(defaults, "punklin");
        addDefaultEntitySlotRolls(defaults, "pigstrom");

        return defaults;
    }

    private static void addDefaultEntitySlotRolls(List<String> defaults, String tableId) {
        if ("cyberzombie".equals(tableId)) {
            defaults.add(tableId + ",BRAIN,0.25,1,3");
            defaults.add(tableId + ",EYES,0.50,1,3");
            defaults.add(tableId + ",HEART,0.25,1,5");
            defaults.add(tableId + ",LUNGS,0.25,1,5");
            defaults.add(tableId + ",ORGANS,0.45,1,3");
            defaults.add(tableId + ",BONE,0.25,1,5");
            defaults.add(tableId + ",SKIN,0.25,1,4");
            defaults.add(tableId + ",MUSCLE,0.25,1,2");
            defaults.add(tableId + ",RLEG,0.65,1,3");
            defaults.add(tableId + ",LLEG,0.65,1,3");
            defaults.add(tableId + ",RARM,0.65,1,4");
            defaults.add(tableId + ",LARM,0.65,1,4");
            return;
        }

        if ("cyberskeleton".equals(tableId)) {
            defaults.add(tableId + ",BRAIN,0.25,1,3");
            defaults.add(tableId + ",EYES,0.50,1,3");
            defaults.add(tableId + ",ORGANS,0.1,1,3");
            defaults.add(tableId + ",BONE,1.0,4,5");
            defaults.add(tableId + ",RLEG,0.65,1,3");
            defaults.add(tableId + ",LLEG,0.65,1,3");
            defaults.add(tableId + ",RARM,0.65,1,4");
            defaults.add(tableId + ",LARM,0.65,1,4");
            return;
        }

        if ("smasher".equals(tableId)) {
            defaults.add(tableId + ",BRAIN,0.5,1,3");
            defaults.add(tableId + ",EYES,0.75,1,3");
            defaults.add(tableId + ",HEART,0.5,1,5");
            defaults.add(tableId + ",LUNGS,0.5,1,5");
            defaults.add(tableId + ",ORGANS,0.75,1,3");
            defaults.add(tableId + ",BONE,0.5,1,5");
            defaults.add(tableId + ",SKIN,0.5,1,4");
            defaults.add(tableId + ",MUSCLE,0.5,1,2");
            defaults.add(tableId + ",RLEG,0.85,1,3");
            defaults.add(tableId + ",LLEG,0.85,1,3");
            defaults.add(tableId + ",RARM,0.85,1,4");
            defaults.add(tableId + ",LARM,0.85,1,4");
        }

        if ("hogboy".equals(tableId)) {
            defaults.add(tableId + ",BRAIN,0.25,1,2");
            defaults.add(tableId + ",EYES,0.50,1,3");
            defaults.add(tableId + ",HEART,0.25,1,2");
            defaults.add(tableId + ",LUNGS,0.25,1,2");
            defaults.add(tableId + ",ORGANS,0.25,1,2");
            defaults.add(tableId + ",BONE,0.25,1,2");
            defaults.add(tableId + ",SKIN,0.25,1,2");
            defaults.add(tableId + ",MUSCLE,0.25,1,2");
            defaults.add(tableId + ",RLEG,0.15,1,2");
            defaults.add(tableId + ",LLEG,0.15,1,2");
            defaults.add(tableId + ",RARM,0.15,1,2");
            defaults.add(tableId + ",LARM,0.15,1,2");
            return;
        }

        if ("punklin".equals(tableId)) {
            defaults.add(tableId + ",BRAIN,0.25,1,3");
            defaults.add(tableId + ",EYES,0.50,1,3");
            defaults.add(tableId + ",HEART,0.25,1,5");
            defaults.add(tableId + ",LUNGS,0.25,1,5");
            defaults.add(tableId + ",ORGANS,0.45,1,3");
            defaults.add(tableId + ",BONE,0.25,1,5");
            defaults.add(tableId + ",SKIN,0.25,1,4");
            defaults.add(tableId + ",MUSCLE,0.25,1,2");
            defaults.add(tableId + ",RLEG,0.65,1,3");
            defaults.add(tableId + ",LLEG,0.65,1,3");
            defaults.add(tableId + ",RARM,0.65,1,4");
            defaults.add(tableId + ",LARM,0.65,1,4");
            return;
        }

        if ("pigstrom".equals(tableId)) {
            defaults.add(tableId + ",BRAIN,0.5,1,3");
            defaults.add(tableId + ",EYES,0.75,1,3");
            defaults.add(tableId + ",HEART,0.5,1,5");
            defaults.add(tableId + ",LUNGS,0.5,1,5");
            defaults.add(tableId + ",ORGANS,0.75,1,3");
            defaults.add(tableId + ",BONE,0.5,1,5");
            defaults.add(tableId + ",SKIN,0.5,1,4");
            defaults.add(tableId + ",MUSCLE,0.5,1,2");
            defaults.add(tableId + ",RLEG,0.85,1,3");
            defaults.add(tableId + ",LLEG,0.85,1,3");
            defaults.add(tableId + ",RARM,0.85,1,4");
            defaults.add(tableId + ",LARM,0.85,1,4");
        }

    }

    private static List<String> defaultEntityCyberwareRolls() {
        List<String> defaults = new ArrayList<>();

        addDefaultEntityCyberwareRolls(defaults, "cyberzombie");
        addDefaultEntityCyberwareRolls(defaults, "cyberskeleton");
        addDefaultEntityCyberwareRolls(defaults, "smasher");

        addDefaultEntityCyberwareRolls(defaults, "hogboy");
        addDefaultEntityCyberwareRolls(defaults, "punklin");
        addDefaultEntityCyberwareRolls(defaults, "pigstrom");

        return defaults;
    }

    private static void addDefaultEntityCyberwareRolls(List<String> defaults, String tableId) {
        if ("cyberzombie".equals(tableId)) {
            defaults.add(tableId + ",createcybernetics:brainupgrades_cyberbrain,BRAIN,2");
            defaults.add(tableId + ",createcybernetics:brainupgrades_eyeofdefender,BRAIN,4");
            defaults.add(tableId + ",createcybernetics:brainupgrades_consciousnesstransmitter,BRAIN,4");
            defaults.add(tableId + ",createcybernetics:brainupgrades_corticalstack,BRAIN,3");
            defaults.add(tableId + ",createcybernetics:brainupgrades_enderjammer,BRAIN,3");
            defaults.add(tableId + ",createcybernetics:brainupgrades_matrix,BRAIN,4");
            defaults.add(tableId + ",createcybernetics:brainupgrades_neuralcontextualizer,BRAIN,4");
            defaults.add(tableId + ",createcybernetics:brainupgrades_cyberdeck,BRAIN,2");
            defaults.add(tableId + ",createcybernetics:brainupgrades_idem,BRAIN,2");
            defaults.add(tableId + ",createcybernetics:brainupgrades_chipwareslots,BRAIN,8");
            defaults.add(tableId + ",createcybernetics:brainupgrades_neuralprocessor,BRAIN,5");
            defaults.add(tableId + ",createcybernetics:brainupgrades_iceprotocol,BRAIN,3");
            defaults.add(tableId + ",createcybernetics:brainupgrades_spelljammer,BRAIN,2,irons_spellbooks");

            defaults.add(tableId + ",createcybernetics:basecyberware_cybereyes,EYES,12");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_hudlens,EYES,6");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_navigationchip,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_hudjack,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_nightvision,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_targeting,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_underwatervision,EYES,4");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_zoom,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_trajectorycalculator,EYES,4");

            defaults.add(tableId + ",createcybernetics:heartupgrades_cyberheart,HEART,8");
            defaults.add(tableId + ",createcybernetics:heartupgrades_coupler,HEART,5");
            defaults.add(tableId + ",createcybernetics:heartupgrades_creeperheart,HEART,1");
            defaults.add(tableId + ",createcybernetics:heartupgrades_defibrillator,HEART,4");
            defaults.add(tableId + ",createcybernetics:heartupgrades_stemcell,HEART,4");
            defaults.add(tableId + ",createcybernetics:heartupgrades_platelets,HEART,4");

            defaults.add(tableId + ",createcybernetics:lungsupgrades_hyperoxygenation,LUNGS,6");
            defaults.add(tableId + ",createcybernetics:lungsupgrades_oxygen,LUNGS,5");

            defaults.add(tableId + ",createcybernetics:organsupgrades_adrenaline,ORGANS,5");
            defaults.add(tableId + ",createcybernetics:organsupgrades_battery,ORGANS,6");
            defaults.add(tableId + ",createcybernetics:organsupgrades_diamondwaferstack,ORGANS,3");
            defaults.add(tableId + ",createcybernetics:organsupgrades_liverfilter,ORGANS,4");
            defaults.add(tableId + ",createcybernetics:organsupgrades_metabolic,ORGANS,4");
            defaults.add(tableId + ",createcybernetics:organsupgrades_densebattery,ORGANS,4");
            defaults.add(tableId + ",createcybernetics:organsupgrades_heatengine,ORGANS,5");
            defaults.add(tableId + ",createcybernetics:organsupgrades_manabattery,ORGANS,3,irons_spellbooks");

            defaults.add(tableId + ",createcybernetics:basecyberware_linearframe,BONE,10");
            defaults.add(tableId + ",createcybernetics:boneupgrades_bonebattery,BONE,4");
            defaults.add(tableId + ",createcybernetics:boneupgrades_boneflex,BONE,4");
            defaults.add(tableId + ",createcybernetics:boneupgrades_bonelacing,BONE,5");
            defaults.add(tableId + ",createcybernetics:boneupgrades_capacitorframe,BONE,5");
            defaults.add(tableId + ",createcybernetics:boneupgrades_elytra,BONE,2,caelus");
            defaults.add(tableId + ",createcybernetics:boneupgrades_piezo,BONE,4");
            defaults.add(tableId + ",createcybernetics:boneupgrades_spinalinjector,BONE,3");
            defaults.add(tableId + ",createcybernetics:boneupgrades_sandevistan,BONE,2");
            defaults.add(tableId + ",createcybernetics:boneupgrades_cyberskull,BONE,3");

            defaults.add(tableId + ",createcybernetics:skinupgrades_arterialturbine,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_chromatophores,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_synthskin,SKIN,12");
            defaults.add(tableId + ",createcybernetics:skinupgrades_immuno,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_faceplate,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_netheriteplating,SKIN,2");
            defaults.add(tableId + ",createcybernetics:skinupgrades_solarskin,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_subdermalarmor,SKIN,5");
            defaults.add(tableId + ",createcybernetics:skinupgrades_subdermalspikes,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_syntheticsetules,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_sweat,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_metalplating,SKIN,8");
            defaults.add(tableId + ",createcybernetics:skinupgrades_manaskin,SKIN,2,irons_spellbooks");

            defaults.add(tableId + ",createcybernetics:muscleupgrades_synthmuscle,MUSCLE,5");
            defaults.add(tableId + ",createcybernetics:muscleupgrades_wiredreflexes,MUSCLE,4");

            defaults.add(tableId + ",createcybernetics:basecyberware_rightarm,RARM,12");
            defaults.add(tableId + ",createcybernetics:armupgrades_armcannon,RARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_flywheel,RARM,5");
            defaults.add(tableId + ",createcybernetics:armupgrades_claws,RARM,4");
            defaults.add(tableId + ",createcybernetics:armupgrades_crafthands,RARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_drillfist,RARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_firestarter,RARM,5");
            defaults.add(tableId + ",createcybernetics:armupgrades_pneumaticwrist,RARM,4");
            defaults.add(tableId + ",createcybernetics:armupgrades_reinforcedknuckles,RARM,6");

            defaults.add(tableId + ",createcybernetics:basecyberware_leftarm,LARM,12");
            defaults.add(tableId + ",createcybernetics:armupgrades_armcannon,LARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_flywheel,LARM,5");
            defaults.add(tableId + ",createcybernetics:armupgrades_claws,LARM,4");
            defaults.add(tableId + ",createcybernetics:armupgrades_crafthands,LARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_drillfist,LARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_firestarter,LARM,5");
            defaults.add(tableId + ",createcybernetics:armupgrades_pneumaticwrist,LARM,4");
            defaults.add(tableId + ",createcybernetics:armupgrades_reinforcedknuckles,LARM,6");

            defaults.add(tableId + ",createcybernetics:basecyberware_rightleg,RLEG,12");
            defaults.add(tableId + ",createcybernetics:legupgrades_metaldetector,RLEG,3");
            defaults.add(tableId + ",createcybernetics:legupgrades_anklebracers,RLEG,4");
            defaults.add(tableId + ",createcybernetics:legupgrades_jumpboost,RLEG,5");
            defaults.add(tableId + ",createcybernetics:legupgrades_propellers,RLEG,3");
            defaults.add(tableId + ",createcybernetics:legupgrades_spurs,RLEG,4");
            defaults.add(tableId + ",createcybernetics:legupgrades_ocelotpaws,RLEG,4");

            defaults.add(tableId + ",createcybernetics:basecyberware_leftleg,LLEG,12");
            defaults.add(tableId + ",createcybernetics:legupgrades_metaldetector,LLEG,3");
            defaults.add(tableId + ",createcybernetics:legupgrades_anklebracers,LLEG,4");
            defaults.add(tableId + ",createcybernetics:legupgrades_jumpboost,LLEG,5");
            defaults.add(tableId + ",createcybernetics:legupgrades_propellers,LLEG,3");
            defaults.add(tableId + ",createcybernetics:legupgrades_spurs,LLEG,4");
            defaults.add(tableId + ",createcybernetics:legupgrades_ocelotpaws,LLEG,4");
            return;
        }

        if ("cyberskeleton".equals(tableId)) {
            defaults.add(tableId + ",createcybernetics:brainupgrades_cyberbrain,BRAIN,2");
            defaults.add(tableId + ",createcybernetics:brainupgrades_eyeofdefender,BRAIN,4");
            defaults.add(tableId + ",createcybernetics:brainupgrades_consciousnesstransmitter,BRAIN,4");
            defaults.add(tableId + ",createcybernetics:brainupgrades_corticalstack,BRAIN,3");
            defaults.add(tableId + ",createcybernetics:brainupgrades_enderjammer,BRAIN,3");
            defaults.add(tableId + ",createcybernetics:brainupgrades_matrix,BRAIN,4");
            defaults.add(tableId + ",createcybernetics:brainupgrades_neuralcontextualizer,BRAIN,4");
            defaults.add(tableId + ",createcybernetics:brainupgrades_cyberdeck,BRAIN,2");
            defaults.add(tableId + ",createcybernetics:brainupgrades_idem,BRAIN,2");
            defaults.add(tableId + ",createcybernetics:brainupgrades_chipwareslots,BRAIN,8");
            defaults.add(tableId + ",createcybernetics:brainupgrades_neuralprocessor,BRAIN,5");
            defaults.add(tableId + ",createcybernetics:brainupgrades_iceprotocol,BRAIN,3");
            defaults.add(tableId + ",createcybernetics:brainupgrades_spelljammer,BRAIN,2,irons_spellbooks");

            defaults.add(tableId + ",createcybernetics:basecyberware_cybereyes,EYES,12");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_hudlens,EYES,6");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_navigationchip,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_hudjack,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_nightvision,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_targeting,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_underwatervision,EYES,4");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_zoom,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_trajectorycalculator,EYES,4");

            defaults.add(tableId + ",createcybernetics:organsupgrades_adrenaline,ORGANS,5");
            defaults.add(tableId + ",createcybernetics:organsupgrades_battery,ORGANS,6");
            defaults.add(tableId + ",createcybernetics:organsupgrades_diamondwaferstack,ORGANS,3");
            defaults.add(tableId + ",createcybernetics:organsupgrades_liverfilter,ORGANS,4");
            defaults.add(tableId + ",createcybernetics:organsupgrades_metabolic,ORGANS,4");
            defaults.add(tableId + ",createcybernetics:organsupgrades_densebattery,ORGANS,4");
            defaults.add(tableId + ",createcybernetics:organsupgrades_heatengine,ORGANS,5");
            defaults.add(tableId + ",createcybernetics:organsupgrades_manabattery,ORGANS,3,irons_spellbooks");

            defaults.add(tableId + ",createcybernetics:basecyberware_linearframe,BONE,10");
            defaults.add(tableId + ",createcybernetics:boneupgrades_bonebattery,BONE,4");
            defaults.add(tableId + ",createcybernetics:boneupgrades_boneflex,BONE,4");
            defaults.add(tableId + ",createcybernetics:boneupgrades_bonelacing,BONE,5");
            defaults.add(tableId + ",createcybernetics:boneupgrades_capacitorframe,BONE,5");
            defaults.add(tableId + ",createcybernetics:boneupgrades_elytra,BONE,2,caelus");
            defaults.add(tableId + ",createcybernetics:boneupgrades_piezo,BONE,4");
            defaults.add(tableId + ",createcybernetics:boneupgrades_spinalinjector,BONE,3");
            defaults.add(tableId + ",createcybernetics:boneupgrades_sandevistan,BONE,2");
            defaults.add(tableId + ",createcybernetics:boneupgrades_cyberskull,BONE,3");

            defaults.add(tableId + ",createcybernetics:basecyberware_rightarm,RARM,12");
            defaults.add(tableId + ",createcybernetics:armupgrades_armcannon,RARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_flywheel,RARM,5");
            defaults.add(tableId + ",createcybernetics:armupgrades_claws,RARM,4");
            defaults.add(tableId + ",createcybernetics:armupgrades_crafthands,RARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_drillfist,RARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_firestarter,RARM,5");
            defaults.add(tableId + ",createcybernetics:armupgrades_pneumaticwrist,RARM,4");
            defaults.add(tableId + ",createcybernetics:armupgrades_reinforcedknuckles,RARM,6");

            defaults.add(tableId + ",createcybernetics:basecyberware_leftarm,LARM,12");
            defaults.add(tableId + ",createcybernetics:armupgrades_armcannon,LARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_flywheel,LARM,5");
            defaults.add(tableId + ",createcybernetics:armupgrades_claws,LARM,4");
            defaults.add(tableId + ",createcybernetics:armupgrades_crafthands,LARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_drillfist,LARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_firestarter,LARM,5");
            defaults.add(tableId + ",createcybernetics:armupgrades_pneumaticwrist,LARM,4");
            defaults.add(tableId + ",createcybernetics:armupgrades_reinforcedknuckles,LARM,6");

            defaults.add(tableId + ",createcybernetics:basecyberware_rightleg,RLEG,12");
            defaults.add(tableId + ",createcybernetics:legupgrades_metaldetector,RLEG,3");
            defaults.add(tableId + ",createcybernetics:legupgrades_anklebracers,RLEG,4");
            defaults.add(tableId + ",createcybernetics:legupgrades_jumpboost,RLEG,5");
            defaults.add(tableId + ",createcybernetics:legupgrades_propellers,RLEG,3");
            defaults.add(tableId + ",createcybernetics:legupgrades_spurs,RLEG,4");
            defaults.add(tableId + ",createcybernetics:legupgrades_ocelotpaws,RLEG,4");

            defaults.add(tableId + ",createcybernetics:basecyberware_leftleg,LLEG,12");
            defaults.add(tableId + ",createcybernetics:legupgrades_metaldetector,LLEG,3");
            defaults.add(tableId + ",createcybernetics:legupgrades_anklebracers,LLEG,4");
            defaults.add(tableId + ",createcybernetics:legupgrades_jumpboost,LLEG,5");
            defaults.add(tableId + ",createcybernetics:legupgrades_propellers,LLEG,3");
            defaults.add(tableId + ",createcybernetics:legupgrades_spurs,LLEG,4");
            defaults.add(tableId + ",createcybernetics:legupgrades_ocelotpaws,LLEG,4");
            return;
        }

        if ("smasher".equals(tableId)) {
            defaults.add(tableId + ",createcybernetics:brainupgrades_cyberbrain,BRAIN,2");
            defaults.add(tableId + ",createcybernetics:brainupgrades_eyeofdefender,BRAIN,4");
            defaults.add(tableId + ",createcybernetics:brainupgrades_consciousnesstransmitter,BRAIN,4");
            defaults.add(tableId + ",createcybernetics:brainupgrades_corticalstack,BRAIN,3");
            defaults.add(tableId + ",createcybernetics:brainupgrades_enderjammer,BRAIN,3");
            defaults.add(tableId + ",createcybernetics:brainupgrades_matrix,BRAIN,4");
            defaults.add(tableId + ",createcybernetics:brainupgrades_neuralcontextualizer,BRAIN,4");
            defaults.add(tableId + ",createcybernetics:brainupgrades_cyberdeck,BRAIN,2");
            defaults.add(tableId + ",createcybernetics:brainupgrades_idem,BRAIN,2");
            defaults.add(tableId + ",createcybernetics:brainupgrades_chipwareslots,BRAIN,8");
            defaults.add(tableId + ",createcybernetics:brainupgrades_neuralprocessor,BRAIN,5");
            defaults.add(tableId + ",createcybernetics:brainupgrades_iceprotocol,BRAIN,3");
            defaults.add(tableId + ",createcybernetics:brainupgrades_spelljammer,BRAIN,2,irons_spellbooks");

            defaults.add(tableId + ",createcybernetics:basecyberware_cybereyes,EYES,12");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_hudlens,EYES,6");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_navigationchip,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_hudjack,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_nightvision,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_targeting,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_underwatervision,EYES,4");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_zoom,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_trajectorycalculator,EYES,4");

            defaults.add(tableId + ",createcybernetics:heartupgrades_cyberheart,HEART,8");
            defaults.add(tableId + ",createcybernetics:heartupgrades_coupler,HEART,5");
            defaults.add(tableId + ",createcybernetics:heartupgrades_creeperheart,HEART,1");
            defaults.add(tableId + ",createcybernetics:heartupgrades_defibrillator,HEART,4");
            defaults.add(tableId + ",createcybernetics:heartupgrades_stemcell,HEART,4");
            defaults.add(tableId + ",createcybernetics:heartupgrades_platelets,HEART,4");

            defaults.add(tableId + ",createcybernetics:lungsupgrades_hyperoxygenation,LUNGS,6");
            defaults.add(tableId + ",createcybernetics:lungsupgrades_oxygen,LUNGS,5");

            defaults.add(tableId + ",createcybernetics:organsupgrades_adrenaline,ORGANS,5");
            defaults.add(tableId + ",createcybernetics:organsupgrades_battery,ORGANS,6");
            defaults.add(tableId + ",createcybernetics:organsupgrades_diamondwaferstack,ORGANS,3");
            defaults.add(tableId + ",createcybernetics:organsupgrades_liverfilter,ORGANS,4");
            defaults.add(tableId + ",createcybernetics:organsupgrades_metabolic,ORGANS,4");
            defaults.add(tableId + ",createcybernetics:organsupgrades_densebattery,ORGANS,4");
            defaults.add(tableId + ",createcybernetics:organsupgrades_heatengine,ORGANS,5");
            defaults.add(tableId + ",createcybernetics:organsupgrades_manabattery,ORGANS,3,irons_spellbooks");

            defaults.add(tableId + ",createcybernetics:basecyberware_linearframe,BONE,10");
            defaults.add(tableId + ",createcybernetics:boneupgrades_bonebattery,BONE,4");
            defaults.add(tableId + ",createcybernetics:boneupgrades_boneflex,BONE,4");
            defaults.add(tableId + ",createcybernetics:boneupgrades_bonelacing,BONE,5");
            defaults.add(tableId + ",createcybernetics:boneupgrades_capacitorframe,BONE,5");
            defaults.add(tableId + ",createcybernetics:boneupgrades_piezo,BONE,4");
            defaults.add(tableId + ",createcybernetics:boneupgrades_spinalinjector,BONE,3");
            defaults.add(tableId + ",createcybernetics:boneupgrades_sandevistan,BONE,2");
            defaults.add(tableId + ",createcybernetics:boneupgrades_cyberskull,BONE,3");

            defaults.add(tableId + ",createcybernetics:skinupgrades_arterialturbine,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_chromatophores,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_synthskin,SKIN,12");
            defaults.add(tableId + ",createcybernetics:skinupgrades_immuno,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_faceplate,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_netheriteplating,SKIN,2");
            defaults.add(tableId + ",createcybernetics:skinupgrades_solarskin,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_subdermalarmor,SKIN,5");
            defaults.add(tableId + ",createcybernetics:skinupgrades_subdermalspikes,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_syntheticsetules,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_sweat,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_metalplating,SKIN,8");
            defaults.add(tableId + ",createcybernetics:skinupgrades_manaskin,SKIN,2,irons_spellbooks");

            defaults.add(tableId + ",createcybernetics:muscleupgrades_synthmuscle,MUSCLE,5");
            defaults.add(tableId + ",createcybernetics:muscleupgrades_wiredreflexes,MUSCLE,4");

            defaults.add(tableId + ",createcybernetics:basecyberware_rightarm,RARM,12");
            defaults.add(tableId + ",createcybernetics:armupgrades_armcannon,RARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_flywheel,RARM,5");
            defaults.add(tableId + ",createcybernetics:armupgrades_claws,RARM,4");
            defaults.add(tableId + ",createcybernetics:armupgrades_crafthands,RARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_drillfist,RARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_firestarter,RARM,5");
            defaults.add(tableId + ",createcybernetics:armupgrades_pneumaticwrist,RARM,4");
            defaults.add(tableId + ",createcybernetics:armupgrades_reinforcedknuckles,RARM,6");

            defaults.add(tableId + ",createcybernetics:basecyberware_leftarm,LARM,12");
            defaults.add(tableId + ",createcybernetics:armupgrades_armcannon,LARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_flywheel,LARM,5");
            defaults.add(tableId + ",createcybernetics:armupgrades_claws,LARM,4");
            defaults.add(tableId + ",createcybernetics:armupgrades_crafthands,LARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_drillfist,LARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_firestarter,LARM,5");
            defaults.add(tableId + ",createcybernetics:armupgrades_pneumaticwrist,LARM,4");
            defaults.add(tableId + ",createcybernetics:armupgrades_reinforcedknuckles,LARM,6");

            defaults.add(tableId + ",createcybernetics:basecyberware_rightleg,RLEG,12");
            defaults.add(tableId + ",createcybernetics:legupgrades_metaldetector,RLEG,3");
            defaults.add(tableId + ",createcybernetics:legupgrades_anklebracers,RLEG,4");
            defaults.add(tableId + ",createcybernetics:legupgrades_jumpboost,RLEG,5");
            defaults.add(tableId + ",createcybernetics:legupgrades_propellers,RLEG,3");
            defaults.add(tableId + ",createcybernetics:legupgrades_spurs,RLEG,4");
            defaults.add(tableId + ",createcybernetics:legupgrades_ocelotpaws,RLEG,4");

            defaults.add(tableId + ",createcybernetics:basecyberware_leftleg,LLEG,12");
            defaults.add(tableId + ",createcybernetics:legupgrades_metaldetector,LLEG,3");
            defaults.add(tableId + ",createcybernetics:legupgrades_anklebracers,LLEG,4");
            defaults.add(tableId + ",createcybernetics:legupgrades_jumpboost,LLEG,5");
            defaults.add(tableId + ",createcybernetics:legupgrades_propellers,LLEG,3");
            defaults.add(tableId + ",createcybernetics:legupgrades_spurs,LLEG,4");
            defaults.add(tableId + ",createcybernetics:legupgrades_ocelotpaws,LLEG,4");
        }

        if ("hogboy".equals(tableId)) {
            defaults.add(tableId + ",createcybernetics:brainupgrades_cyberbrain,BRAIN,2");
            defaults.add(tableId + ",createcybernetics:brainupgrades_eyeofdefender,BRAIN,4");
            defaults.add(tableId + ",createcybernetics:brainupgrades_consciousnesstransmitter,BRAIN,4");
            defaults.add(tableId + ",createcybernetics:brainupgrades_corticalstack,BRAIN,3");
            defaults.add(tableId + ",createcybernetics:brainupgrades_enderjammer,BRAIN,3");
            defaults.add(tableId + ",createcybernetics:brainupgrades_matrix,BRAIN,4");
            defaults.add(tableId + ",createcybernetics:brainupgrades_neuralcontextualizer,BRAIN,4");
            defaults.add(tableId + ",createcybernetics:brainupgrades_cyberdeck,BRAIN,2");
            defaults.add(tableId + ",createcybernetics:brainupgrades_chipwareslots,BRAIN,8");
            defaults.add(tableId + ",createcybernetics:brainupgrades_neuralprocessor,BRAIN,5");
            defaults.add(tableId + ",createcybernetics:brainupgrades_iceprotocol,BRAIN,3");
            defaults.add(tableId + ",createcybernetics:brainupgrades_spelljammer,BRAIN,2,irons_spellbooks");

            defaults.add(tableId + ",createcybernetics:basecyberware_cybereyes,EYES,12");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_hudlens,EYES,6");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_navigationchip,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_hudjack,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_nightvision,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_targeting,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_underwatervision,EYES,4");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_zoom,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_trajectorycalculator,EYES,4");

            defaults.add(tableId + ",createcybernetics:heartupgrades_cyberheart,HEART,8");
            defaults.add(tableId + ",createcybernetics:heartupgrades_coupler,HEART,5");
            defaults.add(tableId + ",createcybernetics:heartupgrades_creeperheart,HEART,1");
            defaults.add(tableId + ",createcybernetics:heartupgrades_defibrillator,HEART,4");
            defaults.add(tableId + ",createcybernetics:heartupgrades_stemcell,HEART,4");
            defaults.add(tableId + ",createcybernetics:heartupgrades_platelets,HEART,4");

            defaults.add(tableId + ",createcybernetics:lungsupgrades_hyperoxygenation,LUNGS,6");
            defaults.add(tableId + ",createcybernetics:lungsupgrades_oxygen,LUNGS,5");

            defaults.add(tableId + ",createcybernetics:organsupgrades_adrenaline,ORGANS,5");
            defaults.add(tableId + ",createcybernetics:organsupgrades_battery,ORGANS,6");
            defaults.add(tableId + ",createcybernetics:organsupgrades_diamondwaferstack,ORGANS,3");
            defaults.add(tableId + ",createcybernetics:organsupgrades_liverfilter,ORGANS,4");
            defaults.add(tableId + ",createcybernetics:organsupgrades_metabolic,ORGANS,4");
            defaults.add(tableId + ",createcybernetics:organsupgrades_densebattery,ORGANS,4");
            defaults.add(tableId + ",createcybernetics:organsupgrades_heatengine,ORGANS,5");
            defaults.add(tableId + ",createcybernetics:organsupgrades_manabattery,ORGANS,3,irons_spellbooks");

            defaults.add(tableId + ",createcybernetics:basecyberware_linearframe,BONE,10");
            defaults.add(tableId + ",createcybernetics:boneupgrades_bonebattery,BONE,4");
            defaults.add(tableId + ",createcybernetics:boneupgrades_boneflex,BONE,4");
            defaults.add(tableId + ",createcybernetics:boneupgrades_bonelacing,BONE,5");
            defaults.add(tableId + ",createcybernetics:boneupgrades_capacitorframe,BONE,5");
            defaults.add(tableId + ",createcybernetics:boneupgrades_piezo,BONE,4");
            defaults.add(tableId + ",createcybernetics:boneupgrades_spinalinjector,BONE,3");
            defaults.add(tableId + ",createcybernetics:boneupgrades_sandevistan,BONE,2");
            defaults.add(tableId + ",createcybernetics:boneupgrades_cyberskull,BONE,3");

            defaults.add(tableId + ",createcybernetics:skinupgrades_arterialturbine,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_chromatophores,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_synthskin,SKIN,12");
            defaults.add(tableId + ",createcybernetics:skinupgrades_immuno,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_faceplate,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_netheriteplating,SKIN,2");
            defaults.add(tableId + ",createcybernetics:skinupgrades_solarskin,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_subdermalarmor,SKIN,5");
            defaults.add(tableId + ",createcybernetics:skinupgrades_subdermalspikes,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_syntheticsetules,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_sweat,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_metalplating,SKIN,8");
            defaults.add(tableId + ",createcybernetics:skinupgrades_manaskin,SKIN,2,irons_spellbooks");

            defaults.add(tableId + ",createcybernetics:muscleupgrades_synthmuscle,MUSCLE,5");
            defaults.add(tableId + ",createcybernetics:muscleupgrades_wiredreflexes,MUSCLE,4");

            defaults.add(tableId + ",createcybernetics:basecyberware_rightarm,RARM,12");
            defaults.add(tableId + ",createcybernetics:armupgrades_armcannon,RARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_flywheel,RARM,5");
            defaults.add(tableId + ",createcybernetics:armupgrades_claws,RARM,4");
            defaults.add(tableId + ",createcybernetics:armupgrades_crafthands,RARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_drillfist,RARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_firestarter,RARM,5");
            defaults.add(tableId + ",createcybernetics:armupgrades_pneumaticwrist,RARM,4");
            defaults.add(tableId + ",createcybernetics:armupgrades_reinforcedknuckles,RARM,6");

            defaults.add(tableId + ",createcybernetics:basecyberware_leftarm,LARM,12");
            defaults.add(tableId + ",createcybernetics:armupgrades_armcannon,LARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_flywheel,LARM,5");
            defaults.add(tableId + ",createcybernetics:armupgrades_claws,LARM,4");
            defaults.add(tableId + ",createcybernetics:armupgrades_crafthands,LARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_drillfist,LARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_firestarter,LARM,5");
            defaults.add(tableId + ",createcybernetics:armupgrades_pneumaticwrist,LARM,4");
            defaults.add(tableId + ",createcybernetics:armupgrades_reinforcedknuckles,LARM,6");

            defaults.add(tableId + ",createcybernetics:basecyberware_rightleg,RLEG,12");
            defaults.add(tableId + ",createcybernetics:legupgrades_metaldetector,RLEG,3");
            defaults.add(tableId + ",createcybernetics:legupgrades_anklebracers,RLEG,4");
            defaults.add(tableId + ",createcybernetics:legupgrades_jumpboost,RLEG,5");
            defaults.add(tableId + ",createcybernetics:legupgrades_propellers,RLEG,3");
            defaults.add(tableId + ",createcybernetics:legupgrades_spurs,RLEG,4");
            defaults.add(tableId + ",createcybernetics:legupgrades_ocelotpaws,RLEG,4");

            defaults.add(tableId + ",createcybernetics:basecyberware_leftleg,LLEG,12");
            defaults.add(tableId + ",createcybernetics:legupgrades_metaldetector,LLEG,3");
            defaults.add(tableId + ",createcybernetics:legupgrades_anklebracers,LLEG,4");
            defaults.add(tableId + ",createcybernetics:legupgrades_jumpboost,LLEG,5");
            defaults.add(tableId + ",createcybernetics:legupgrades_propellers,LLEG,3");
            defaults.add(tableId + ",createcybernetics:legupgrades_spurs,LLEG,4");
            defaults.add(tableId + ",createcybernetics:legupgrades_ocelotpaws,LLEG,4");
        }

        if ("punklin".equals(tableId)) {
            defaults.add(tableId + ",createcybernetics:brainupgrades_cyberbrain,BRAIN,2");
            defaults.add(tableId + ",createcybernetics:brainupgrades_eyeofdefender,BRAIN,4");
            defaults.add(tableId + ",createcybernetics:brainupgrades_consciousnesstransmitter,BRAIN,4");
            defaults.add(tableId + ",createcybernetics:brainupgrades_corticalstack,BRAIN,3");
            defaults.add(tableId + ",createcybernetics:brainupgrades_enderjammer,BRAIN,3");
            defaults.add(tableId + ",createcybernetics:brainupgrades_matrix,BRAIN,4");
            defaults.add(tableId + ",createcybernetics:brainupgrades_neuralcontextualizer,BRAIN,4");
            defaults.add(tableId + ",createcybernetics:brainupgrades_cyberdeck,BRAIN,2");
            defaults.add(tableId + ",createcybernetics:brainupgrades_chipwareslots,BRAIN,8");
            defaults.add(tableId + ",createcybernetics:brainupgrades_neuralprocessor,BRAIN,5");
            defaults.add(tableId + ",createcybernetics:brainupgrades_iceprotocol,BRAIN,3");
            defaults.add(tableId + ",createcybernetics:brainupgrades_spelljammer,BRAIN,2,irons_spellbooks");

            defaults.add(tableId + ",createcybernetics:basecyberware_cybereyes,EYES,12");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_hudlens,EYES,6");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_navigationchip,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_hudjack,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_nightvision,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_targeting,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_underwatervision,EYES,4");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_zoom,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_trajectorycalculator,EYES,4");

            defaults.add(tableId + ",createcybernetics:heartupgrades_cyberheart,HEART,8");
            defaults.add(tableId + ",createcybernetics:heartupgrades_coupler,HEART,5");
            defaults.add(tableId + ",createcybernetics:heartupgrades_creeperheart,HEART,1");
            defaults.add(tableId + ",createcybernetics:heartupgrades_defibrillator,HEART,4");
            defaults.add(tableId + ",createcybernetics:heartupgrades_stemcell,HEART,4");
            defaults.add(tableId + ",createcybernetics:heartupgrades_platelets,HEART,4");

            defaults.add(tableId + ",createcybernetics:lungsupgrades_hyperoxygenation,LUNGS,6");
            defaults.add(tableId + ",createcybernetics:lungsupgrades_oxygen,LUNGS,5");

            defaults.add(tableId + ",createcybernetics:organsupgrades_adrenaline,ORGANS,5");
            defaults.add(tableId + ",createcybernetics:organsupgrades_battery,ORGANS,6");
            defaults.add(tableId + ",createcybernetics:organsupgrades_diamondwaferstack,ORGANS,3");
            defaults.add(tableId + ",createcybernetics:organsupgrades_liverfilter,ORGANS,4");
            defaults.add(tableId + ",createcybernetics:organsupgrades_metabolic,ORGANS,4");
            defaults.add(tableId + ",createcybernetics:organsupgrades_densebattery,ORGANS,4");
            defaults.add(tableId + ",createcybernetics:organsupgrades_heatengine,ORGANS,5");
            defaults.add(tableId + ",createcybernetics:organsupgrades_manabattery,ORGANS,3,irons_spellbooks");

            defaults.add(tableId + ",createcybernetics:basecyberware_linearframe,BONE,10");
            defaults.add(tableId + ",createcybernetics:boneupgrades_bonebattery,BONE,4");
            defaults.add(tableId + ",createcybernetics:boneupgrades_boneflex,BONE,4");
            defaults.add(tableId + ",createcybernetics:boneupgrades_bonelacing,BONE,5");
            defaults.add(tableId + ",createcybernetics:boneupgrades_capacitorframe,BONE,5");
            defaults.add(tableId + ",createcybernetics:boneupgrades_piezo,BONE,4");
            defaults.add(tableId + ",createcybernetics:boneupgrades_spinalinjector,BONE,3");
            defaults.add(tableId + ",createcybernetics:boneupgrades_sandevistan,BONE,2");
            defaults.add(tableId + ",createcybernetics:boneupgrades_cyberskull,BONE,3");

            defaults.add(tableId + ",createcybernetics:skinupgrades_arterialturbine,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_chromatophores,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_synthskin,SKIN,12");
            defaults.add(tableId + ",createcybernetics:skinupgrades_immuno,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_faceplate,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_netheriteplating,SKIN,2");
            defaults.add(tableId + ",createcybernetics:skinupgrades_solarskin,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_subdermalarmor,SKIN,5");
            defaults.add(tableId + ",createcybernetics:skinupgrades_subdermalspikes,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_syntheticsetules,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_sweat,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_metalplating,SKIN,8");
            defaults.add(tableId + ",createcybernetics:skinupgrades_manaskin,SKIN,2,irons_spellbooks");

            defaults.add(tableId + ",createcybernetics:muscleupgrades_synthmuscle,MUSCLE,5");
            defaults.add(tableId + ",createcybernetics:muscleupgrades_wiredreflexes,MUSCLE,4");

            defaults.add(tableId + ",createcybernetics:basecyberware_rightarm_goldplated,RARM,12");
            defaults.add(tableId + ",createcybernetics:armupgrades_armcannon,RARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_flywheel,RARM,5");
            defaults.add(tableId + ",createcybernetics:armupgrades_claws,RARM,4");
            defaults.add(tableId + ",createcybernetics:armupgrades_crafthands,RARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_drillfist,RARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_firestarter,RARM,5");
            defaults.add(tableId + ",createcybernetics:armupgrades_pneumaticwrist,RARM,4");
            defaults.add(tableId + ",createcybernetics:armupgrades_reinforcedknuckles,RARM,6");

            defaults.add(tableId + ",createcybernetics:basecyberware_leftarm_goldplated,LARM,12");
            defaults.add(tableId + ",createcybernetics:armupgrades_armcannon,LARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_flywheel,LARM,5");
            defaults.add(tableId + ",createcybernetics:armupgrades_claws,LARM,4");
            defaults.add(tableId + ",createcybernetics:armupgrades_crafthands,LARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_drillfist,LARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_firestarter,LARM,5");
            defaults.add(tableId + ",createcybernetics:armupgrades_pneumaticwrist,LARM,4");
            defaults.add(tableId + ",createcybernetics:armupgrades_reinforcedknuckles,LARM,6");

            defaults.add(tableId + ",createcybernetics:basecyberware_rightleg_goldplated,RLEG,12");
            defaults.add(tableId + ",createcybernetics:legupgrades_metaldetector,RLEG,3");
            defaults.add(tableId + ",createcybernetics:legupgrades_anklebracers,RLEG,4");
            defaults.add(tableId + ",createcybernetics:legupgrades_jumpboost,RLEG,5");
            defaults.add(tableId + ",createcybernetics:legupgrades_propellers,RLEG,3");
            defaults.add(tableId + ",createcybernetics:legupgrades_spurs,RLEG,4");
            defaults.add(tableId + ",createcybernetics:legupgrades_ocelotpaws,RLEG,4");

            defaults.add(tableId + ",createcybernetics:basecyberware_leftleg_goldplated,LLEG,12");
            defaults.add(tableId + ",createcybernetics:legupgrades_metaldetector,LLEG,3");
            defaults.add(tableId + ",createcybernetics:legupgrades_anklebracers,LLEG,4");
            defaults.add(tableId + ",createcybernetics:legupgrades_jumpboost,LLEG,5");
            defaults.add(tableId + ",createcybernetics:legupgrades_propellers,LLEG,3");
            defaults.add(tableId + ",createcybernetics:legupgrades_spurs,LLEG,4");
            defaults.add(tableId + ",createcybernetics:legupgrades_ocelotpaws,LLEG,4");
        }

        if ("pigstrom".equals(tableId)) {
            defaults.add(tableId + ",createcybernetics:brainupgrades_cyberbrain,BRAIN,2");
            defaults.add(tableId + ",createcybernetics:brainupgrades_eyeofdefender,BRAIN,4");
            defaults.add(tableId + ",createcybernetics:brainupgrades_consciousnesstransmitter,BRAIN,4");
            defaults.add(tableId + ",createcybernetics:brainupgrades_corticalstack,BRAIN,3");
            defaults.add(tableId + ",createcybernetics:brainupgrades_enderjammer,BRAIN,3");
            defaults.add(tableId + ",createcybernetics:brainupgrades_matrix,BRAIN,4");
            defaults.add(tableId + ",createcybernetics:brainupgrades_neuralcontextualizer,BRAIN,4");
            defaults.add(tableId + ",createcybernetics:brainupgrades_cyberdeck,BRAIN,2");
            defaults.add(tableId + ",createcybernetics:brainupgrades_chipwareslots,BRAIN,8");
            defaults.add(tableId + ",createcybernetics:brainupgrades_neuralprocessor,BRAIN,5");
            defaults.add(tableId + ",createcybernetics:brainupgrades_iceprotocol,BRAIN,3");
            defaults.add(tableId + ",createcybernetics:brainupgrades_spelljammer,BRAIN,2,irons_spellbooks");

            defaults.add(tableId + ",createcybernetics:basecyberware_cybereyes,EYES,12");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_hudlens,EYES,6");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_navigationchip,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_hudjack,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_nightvision,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_targeting,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_underwatervision,EYES,4");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_zoom,EYES,5");
            defaults.add(tableId + ",createcybernetics:eyeupgrades_trajectorycalculator,EYES,4");

            defaults.add(tableId + ",createcybernetics:heartupgrades_cyberheart,HEART,8");
            defaults.add(tableId + ",createcybernetics:heartupgrades_coupler,HEART,5");
            defaults.add(tableId + ",createcybernetics:heartupgrades_creeperheart,HEART,1");
            defaults.add(tableId + ",createcybernetics:heartupgrades_defibrillator,HEART,4");
            defaults.add(tableId + ",createcybernetics:heartupgrades_stemcell,HEART,4");
            defaults.add(tableId + ",createcybernetics:heartupgrades_platelets,HEART,4");

            defaults.add(tableId + ",createcybernetics:lungsupgrades_hyperoxygenation,LUNGS,6");
            defaults.add(tableId + ",createcybernetics:lungsupgrades_oxygen,LUNGS,5");

            defaults.add(tableId + ",createcybernetics:organsupgrades_adrenaline,ORGANS,5");
            defaults.add(tableId + ",createcybernetics:organsupgrades_battery,ORGANS,6");
            defaults.add(tableId + ",createcybernetics:organsupgrades_diamondwaferstack,ORGANS,3");
            defaults.add(tableId + ",createcybernetics:organsupgrades_liverfilter,ORGANS,4");
            defaults.add(tableId + ",createcybernetics:organsupgrades_metabolic,ORGANS,4");
            defaults.add(tableId + ",createcybernetics:organsupgrades_densebattery,ORGANS,4");
            defaults.add(tableId + ",createcybernetics:organsupgrades_heatengine,ORGANS,5");
            defaults.add(tableId + ",createcybernetics:organsupgrades_manabattery,ORGANS,3,irons_spellbooks");

            defaults.add(tableId + ",createcybernetics:basecyberware_linearframe,BONE,10");
            defaults.add(tableId + ",createcybernetics:boneupgrades_bonebattery,BONE,4");
            defaults.add(tableId + ",createcybernetics:boneupgrades_boneflex,BONE,4");
            defaults.add(tableId + ",createcybernetics:boneupgrades_bonelacing,BONE,5");
            defaults.add(tableId + ",createcybernetics:boneupgrades_capacitorframe,BONE,5");
            defaults.add(tableId + ",createcybernetics:boneupgrades_piezo,BONE,4");
            defaults.add(tableId + ",createcybernetics:boneupgrades_spinalinjector,BONE,3");
            defaults.add(tableId + ",createcybernetics:boneupgrades_sandevistan,BONE,2");
            defaults.add(tableId + ",createcybernetics:boneupgrades_cyberskull,BONE,3");

            defaults.add(tableId + ",createcybernetics:skinupgrades_arterialturbine,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_chromatophores,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_synthskin,SKIN,12");
            defaults.add(tableId + ",createcybernetics:skinupgrades_immuno,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_faceplate,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_netheriteplating,SKIN,2");
            defaults.add(tableId + ",createcybernetics:skinupgrades_solarskin,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_subdermalarmor,SKIN,5");
            defaults.add(tableId + ",createcybernetics:skinupgrades_subdermalspikes,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_syntheticsetules,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_sweat,SKIN,3");
            defaults.add(tableId + ",createcybernetics:skinupgrades_metalplating,SKIN,8");
            defaults.add(tableId + ",createcybernetics:skinupgrades_manaskin,SKIN,2,irons_spellbooks");

            defaults.add(tableId + ",createcybernetics:muscleupgrades_synthmuscle,MUSCLE,5");
            defaults.add(tableId + ",createcybernetics:muscleupgrades_wiredreflexes,MUSCLE,4");

            defaults.add(tableId + ",createcybernetics:basecyberware_rightarm,RARM,12");
            defaults.add(tableId + ",createcybernetics:armupgrades_armcannon,RARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_flywheel,RARM,5");
            defaults.add(tableId + ",createcybernetics:armupgrades_claws,RARM,4");
            defaults.add(tableId + ",createcybernetics:armupgrades_crafthands,RARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_drillfist,RARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_firestarter,RARM,5");
            defaults.add(tableId + ",createcybernetics:armupgrades_pneumaticwrist,RARM,4");
            defaults.add(tableId + ",createcybernetics:armupgrades_reinforcedknuckles,RARM,6");

            defaults.add(tableId + ",createcybernetics:basecyberware_leftarm,LARM,12");
            defaults.add(tableId + ",createcybernetics:armupgrades_armcannon,LARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_flywheel,LARM,5");
            defaults.add(tableId + ",createcybernetics:armupgrades_claws,LARM,4");
            defaults.add(tableId + ",createcybernetics:armupgrades_crafthands,LARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_drillfist,LARM,3");
            defaults.add(tableId + ",createcybernetics:armupgrades_firestarter,LARM,5");
            defaults.add(tableId + ",createcybernetics:armupgrades_pneumaticwrist,LARM,4");
            defaults.add(tableId + ",createcybernetics:armupgrades_reinforcedknuckles,LARM,6");

            defaults.add(tableId + ",createcybernetics:basecyberware_rightleg,RLEG,12");
            defaults.add(tableId + ",createcybernetics:legupgrades_metaldetector,RLEG,3");
            defaults.add(tableId + ",createcybernetics:legupgrades_anklebracers,RLEG,4");
            defaults.add(tableId + ",createcybernetics:legupgrades_jumpboost,RLEG,5");
            defaults.add(tableId + ",createcybernetics:legupgrades_propellers,RLEG,3");
            defaults.add(tableId + ",createcybernetics:legupgrades_spurs,RLEG,4");
            defaults.add(tableId + ",createcybernetics:legupgrades_ocelotpaws,RLEG,4");

            defaults.add(tableId + ",createcybernetics:basecyberware_leftleg,LLEG,12");
            defaults.add(tableId + ",createcybernetics:legupgrades_metaldetector,LLEG,3");
            defaults.add(tableId + ",createcybernetics:legupgrades_anklebracers,LLEG,4");
            defaults.add(tableId + ",createcybernetics:legupgrades_jumpboost,LLEG,5");
            defaults.add(tableId + ",createcybernetics:legupgrades_propellers,LLEG,3");
            defaults.add(tableId + ",createcybernetics:legupgrades_spurs,LLEG,4");
            defaults.add(tableId + ",createcybernetics:legupgrades_ocelotpaws,LLEG,4");
        }

    }

    private static List<ConfigValues.EntitySlotRoll> parseEntitySlotRolls(List<? extends String> rawEntries) {
        List<ConfigValues.EntitySlotRoll> parsed = new ArrayList<>();
        if (rawEntries == null) return parsed;

        for (String raw : rawEntries) {
            if (raw == null) continue;

            String line = raw.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(",");
            if (parts.length != 5) {
                CreateCybernetics.LOGGER.warn("Skipping invalid entity slot roll config entry '{}': expected 5 comma-separated values", line);
                continue;
            }

            try {
                String tableId = parts[0].trim();
                CyberwareSlot slot = CyberwareSlot.valueOf(parts[1].trim());
                float rollChance = Float.parseFloat(parts[2].trim());
                int minRolls = Integer.parseInt(parts[3].trim());
                int maxRolls = Integer.parseInt(parts[4].trim());

                if (tableId.isEmpty()) continue;
                if (rollChance < 0.0F || rollChance > 1.0F) continue;
                if (minRolls < 0) continue;
                if (maxRolls < minRolls) continue;

                parsed.add(new ConfigValues.EntitySlotRoll(tableId, slot, rollChance, minRolls, maxRolls));
            } catch (Exception ex) {
                CreateCybernetics.LOGGER.warn("Skipping invalid entity slot roll config entry '{}'", line, ex);
            }
        }

        return parsed;
    }

    private static List<ConfigValues.EntityCyberwareRoll> parseEntityCyberwareRolls(List<? extends String> rawEntries) {
        List<ConfigValues.EntityCyberwareRoll> parsed = new ArrayList<>();
        if (rawEntries == null) return parsed;

        for (String raw : rawEntries) {
            if (raw == null) continue;

            String line = raw.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(",");
            if (parts.length != 4 && parts.length != 5) {
                CreateCybernetics.LOGGER.warn("Skipping invalid entity cyberware roll config entry '{}': expected 4 or 5 comma-separated values", line);
                continue;
            }

            try {
                String tableId = parts[0].trim();
                ResourceLocation id = ResourceLocation.parse(parts[1].trim());
                Item item = BuiltInRegistries.ITEM.get(id);
                if (item == null || item == BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace("air"))) {
                    CreateCybernetics.LOGGER.warn("Skipping entity cyberware roll config entry '{}': item '{}' was not found", line, id);
                    continue;
                }

                CyberwareSlot slot = CyberwareSlot.valueOf(parts[2].trim());
                int weight = Integer.parseInt(parts[3].trim());
                String requiredModId = parts.length == 5 ? parts[4].trim() : "";

                if (tableId.isEmpty()) continue;
                if (weight <= 0) continue;
                if (!requiredModId.isEmpty() && !ModList.get().isLoaded(requiredModId)) continue;

                parsed.add(new ConfigValues.EntityCyberwareRoll(tableId, item, slot, weight, requiredModId));
            } catch (Exception ex) {
                CreateCybernetics.LOGGER.warn("Skipping invalid entity cyberware roll config entry '{}'", line, ex);
            }
        }

        return parsed;
    }
}