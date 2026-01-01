package com.perigrine3.createcybernetics.util;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {

        public static final TagKey<Block> METAL_DETECTABLE = createTag("metal_detectable");


        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, name));
        }
    }

    public static class Items {

        public static final TagKey<Item> TOGGLEABLE_CYBERWARE = createTag("toggleable_cyberware");
        public static final TagKey<Item> ARM_CANNON_AMMO = createTag("arm_cannon_ammo");

        public static final TagKey<Item> CYBERWARE_ITEM = createTag("cyberware_item");
        public static final TagKey<Item> WETWARE_ITEM = createTag("wetware_item");
        public static final TagKey<Item> BODYPART_DROPS = createTag("bodypart_drops");
        public static final TagKey<Item> HUMANOID_BODYPART_DROPS = createTag("humanoid_bodypart_drops");
        public static final TagKey<Item> GRASSFED_BODYPART_DROPS = createTag("grassfed_bodypart_drops");
        public static final TagKey<Item> FISH_BODYPART_DROPS = createTag("fish_bodypart_drops");

        public static final TagKey<Item> BODY_PARTS = createTag("body_parts");
        public static final TagKey<Item> BASE_CYBERWARE = createTag("base_cyberware");
        public static final TagKey<Item> EYE_UPGRADES = createTag("eye_upgrades");
        public static final TagKey<Item> ARM_UPGRADES = createTag("arm_upgrades");
        public static final TagKey<Item> LEG_UPGRADES = createTag("leg_upgrades");
        public static final TagKey<Item> BONE_UPGRADES = createTag("bone_upgrades");
        public static final TagKey<Item> BRAIN_UPGRADES = createTag("brain_upgrades");
        public static final TagKey<Item> HEART_UPGRADES = createTag("heart_upgrades");
        public static final TagKey<Item> LUNG_UPGRADES = createTag("lung_upgrades");
        public static final TagKey<Item> ORGAN_UPGRADES = createTag("organ_upgrades");
        public static final TagKey<Item> SKIN_UPGRADES = createTag("skin_upgrades");
        public static final TagKey<Item> MUSCLE_UPGRADES = createTag("muscle_upgrades");
        public static final TagKey<Item> SCAVENGED_CYBERWARE = createTag("scavenged_cyberware");

        public static final TagKey<Item> BRAIN_ITEMS = createTag("brain_items");
        public static final TagKey<Item> EYE_ITEMS = createTag("eye_items");
        public static final TagKey<Item> SKIN_ITEMS = createTag("skin_items");
        public static final TagKey<Item> MUSCLE_ITEMS = createTag("muscle_items");
        public static final TagKey<Item> BONE_ITEMS = createTag("bone_items");
        public static final TagKey<Item> HEART_ITEMS = createTag("heart_items");
        public static final TagKey<Item> LUNGS_ITEMS = createTag("lungs_items");
        public static final TagKey<Item> LIVER_ITEMS = createTag("liver_items");
        public static final TagKey<Item> INTESTINES_ITEMS = createTag("intestines_items");
        public static final TagKey<Item> LEFTARM_ITEMS = createTag("leftarm_items");
        public static final TagKey<Item> RIGHTARM_ITEMS = createTag("rightarm_items");
        public static final TagKey<Item> LEFTLEG_ITEMS = createTag("leftleg_items");
        public static final TagKey<Item> RIGHTLEG_ITEMS = createTag("rightleg_items");

        public static final TagKey<Item> EYE_REPLACEMENTS = createTag("eye_replacements");
        public static final TagKey<Item> SKIN_REPLACEMENTS = createTag("skin_replacements");
        public static final TagKey<Item> MUSCLE_REPLACEMENTS = createTag("muscle_replacements");
        public static final TagKey<Item> BONE_REPLACEMENTS = createTag("bone_replacements");
        public static final TagKey<Item> HEART_REPLACEMENTS = createTag("heart_replacements");
        public static final TagKey<Item> LIVER_REPLACEMENTS = createTag("liver_replacements");
        public static final TagKey<Item> LEFTARM_REPLACEMENTS = createTag("leftarm_replacements");
        public static final TagKey<Item> RIGHTARM_REPLACEMENTS = createTag("rightarm_replacements");
        public static final TagKey<Item> LEFTLEG_REPLACEMENTS = createTag("leftleg_replacements");
        public static final TagKey<Item> RIGHTLEG_REPLACEMENTS = createTag("rightleg_replacements");
        public static final TagKey<Item> DEFAULTS_FAIL_AS_MISSING_WHEN_UNPOWERED = createTag("defaults_fail_as_missing_when_unpowered");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, name));
        }
    }
}
