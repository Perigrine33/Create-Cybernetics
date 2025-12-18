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

        public static final TagKey<Item> CYBERWARE_ITEM = createTag("cyberware_item");
        public static final TagKey<Item> WETWARE_ITEM = createTag("wetware_item");

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

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, name));
        }
    }
}
