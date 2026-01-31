package com.perigrine3.createcybernetics.datagen;

import com.perigrine3.createcybernetics.block.ModBlocks;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        List<ItemLike> TITANIUM_SMELTABLES = List.of(ModItems.RAWTITANIUM, ModItems.CRUSHEDTITANIUM,
                ModBlocks.TITANIUMORE_BLOCK, ModBlocks.DEEPSLATE_TITANIUMORE_BLOCK);

//TITANIUM BLOCK FROM INGOTS
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.TITANIUM_BLOCK.get())
                .pattern("TTT")
                .pattern("TTT")
                .pattern("TTT")
                .define('T', ModItems.TITANIUMINGOT.get())
                .unlockedBy("has_titaniumingot", has(ModItems.TITANIUMINGOT))
                .save(recipeOutput);
//TITANIUM CLAD COPPER
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.TITANIUM_CLAD_COPPER.get())
                .pattern("TTT")
                .pattern("TCT")
                .pattern("TTT")
                .define('T', ModItems.TITANIUMINGOT.get())
                .define('C', Blocks.COPPER_BLOCK)
                .unlockedBy("has_titaniumingot", has(ModItems.TITANIUMINGOT))
                .unlockedBy("has_copper_block", has(Blocks.COPPER_BLOCK))
                .save(recipeOutput);
//RAW TITANIUM BLOCK FROM RAW TITANIUM
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RAW_TITANIUM_BLOCK.get())
                .pattern("TTT")
                .pattern("TTT")
                .pattern("TTT")
                .define('T', ModItems.RAWTITANIUM.get())
                .unlockedBy("has_rawtitanium", has(ModItems.RAWTITANIUM))
                .save(recipeOutput);
//TITANIUM INGOT FROM NUGGETS
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.TITANIUMINGOT.get())
                .pattern("TTT")
                .pattern("TTT")
                .pattern("TTT")
                .define('T', ModItems.TITANIUMNUGGET.get())
                .unlockedBy("has_titaniumnugget", has(ModItems.TITANIUMNUGGET))
                .save(recipeOutput, "createcybernetics:titaniumingot_from_titaniumnuggets");

//COPPER TEMPLATE
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COPPER_UPGRADE_TEMPLATE.get())
                .pattern("TTT")
                .pattern("TMT")
                .pattern("TZT")
                .define('M', ModItems.SKINUPGRADES_METALPLATING.get())
                .define('T', Items.COPPER_INGOT)
                .define('Z', Items.COPPER_BLOCK)
                .unlockedBy("has_metal_plating", has(ModItems.SKINUPGRADES_METALPLATING))
                .save(recipeOutput, "createcybernetics:copper_template");
//IRON TEMPLATE
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.IRON_UPGRADE_TEMPLATE.get())
                .pattern("TTT")
                .pattern("TMT")
                .pattern("TZT")
                .define('M', ModItems.SKINUPGRADES_METALPLATING.get())
                .define('T', Items.IRON_INGOT)
                .define('Z', Items.IRON_BLOCK)
                .unlockedBy("has_metal_plating", has(ModItems.SKINUPGRADES_METALPLATING))
                .save(recipeOutput, "createcybernetics:iron_template");
//GOLD TEMPLATE
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GOLD_UPGRADE_TEMPLATE.get())
                .pattern("TTT")
                .pattern("TMT")
                .pattern("TZT")
                .define('M', ModItems.SKINUPGRADES_METALPLATING.get())
                .define('T', Items.GOLD_INGOT)
                .define('Z', Items.GOLD_BLOCK)
                .unlockedBy("has_metal_plating", has(ModItems.SKINUPGRADES_METALPLATING))
                .save(recipeOutput, "createcybernetics:gold_template");


//TITANIUM INGOT FROM BLOCK
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.TITANIUMINGOT.get(), 9)
                .requires(ModBlocks.TITANIUM_BLOCK.get())
                .unlockedBy("has_titanium_block", has(ModBlocks.TITANIUM_BLOCK))
                .save(recipeOutput, "createcybernetics:titaniumingot_from_titanium_block");
//RAW TITANIUM FROM RAW TITANIUM BLOCK
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.RAWTITANIUM.get(), 9)
                .requires(ModBlocks.RAW_TITANIUM_BLOCK.get())
                .unlockedBy("has_titanium_block", has(ModBlocks.RAW_TITANIUM_BLOCK))
                .save(recipeOutput, "createcybernetics:rawtitanium_from_raw_titanium_block");
//TITANIUM NUGGET FROM INGOT
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.TITANIUMNUGGET.get(), 9)
                .requires(ModItems.TITANIUMINGOT.get())
                .unlockedBy("has_titaniumingot", has(ModItems.TITANIUMINGOT))
                .save(recipeOutput, "createcybernetics:titaniumnugget_from_titaniumingot");
//ANDOUILLE SAUSAGE
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ANDOUILLE_SAUSAGE.get(), 4)
                .requires(ModItems.GROUND_OFFAL.get()).requires(ModItems.BODYPART_INTESTINES.get())
                .unlockedBy("has_ground_offal", has(ModItems.GROUND_OFFAL)).unlockedBy("has_intestines", has(ModItems.BODYPART_INTESTINES))
                        .save(recipeOutput, "createcybernetics:andouille_sausage_recipe");
//GROUND OFFAL
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GROUND_OFFAL.get(), 4)
                .pattern("OOO")
                .pattern("OOO")
                .define('O', ModTags.Items.OFFAL)
                .unlockedBy("has_offal", has(ModTags.Items.OFFAL))
                .save(recipeOutput, "createcybernetics:ground_offal_recipe");

//TITANIUM SMELTING
        oreSmelting(recipeOutput, TITANIUM_SMELTABLES, RecipeCategory.MISC,
                ModItems.TITANIUMINGOT.get(), 0.25f, 200, "titanium");
//TITANIUM BLASTING
        oreBlasting(recipeOutput, TITANIUM_SMELTABLES, RecipeCategory.MISC,
                ModItems.TITANIUMINGOT.get(), 0.25f, 100, "titanium");

//SMOOTH TITANIUM
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.TITANIUM_BLOCK.get()),
                        RecipeCategory.MISC,
                        ModBlocks.SMOOTH_TITANIUM.get(), 4)
                .unlockedBy("has_titanium_block", has(ModBlocks.TITANIUM_BLOCK.get()))
                .save(recipeOutput, "createcybernetics:smooth_titanium_from_stonecutter");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.SMOOTH_TITANIUM.get()),
                        RecipeCategory.MISC,
                        ModBlocks.SMOOTH_TITANIUM_STAIRS.get(), 2)
                .unlockedBy("has_smooth_titanium", has(ModBlocks.SMOOTH_TITANIUM.get()))
                .save(recipeOutput, "createcybernetics:smooth_titanium_stairs_from_stonecutter");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.SMOOTH_TITANIUM.get()),
                        RecipeCategory.MISC,
                        ModBlocks.SMOOTH_TITANIUM_SLAB.get(), 2)
                .unlockedBy("has_smooth_titanium", has(ModBlocks.SMOOTH_TITANIUM.get()))
                .save(recipeOutput, "createcybernetics:smooth_titanium_slab_from_stonecutter");
//TITANIUM GRATE
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.SMOOTH_TITANIUM.get()),
                        RecipeCategory.MISC,
                        ModBlocks.TITANIUM_GRATE.get(), 4)
                .unlockedBy("has_smooth_titanium", has(ModBlocks.SMOOTH_TITANIUM.get()))
                .save(recipeOutput, "createcybernetics:titanium_grate_from_stonecutter");
//ETCHED TITANIUM COPPER
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.TITANIUM_CLAD_COPPER.get()),
                        RecipeCategory.MISC,
                        ModBlocks.ETCHED_TITANIUM_COPPER.get(), 4)
                .unlockedBy("has_titanium_clad_copper", has(ModBlocks.TITANIUM_CLAD_COPPER.get()))
                .save(recipeOutput, "createcybernetics:etched_titanium_from_stonecutter");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.ETCHED_TITANIUM_COPPER.get()),
                        RecipeCategory.MISC,
                        ModBlocks.ETCHED_TITANIUM_COPPER_STAIRS.get(), 2)
                .unlockedBy("has_etched_titanium_copper", has(ModBlocks.ETCHED_TITANIUM_COPPER.get()))
                .save(recipeOutput, "createcybernetics:etched_titanium_stairs_from_stonecutter");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.ETCHED_TITANIUM_COPPER.get()),
                        RecipeCategory.MISC,
                        ModBlocks.ETCHED_TITANIUM_COPPER_SLAB.get(), 2)
                .unlockedBy("has_etched_titanium_copper", has(ModBlocks.ETCHED_TITANIUM_COPPER.get()))
                .save(recipeOutput, "createcybernetics:etched_titanium_slab_from_stonecutter");

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.TITANIUM_CLAD_COPPER.get()),
                        RecipeCategory.MISC,
                        ModBlocks.TITANIUM_CLAD_COPPER_STAIRS.get(), 2)
                .unlockedBy("has_titanium_clad_copper", has(ModBlocks.TITANIUM_CLAD_COPPER.get()))
                .save(recipeOutput, "createcybernetics:titanium_clad_copper_stairs_from_stonecutter");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.TITANIUM_CLAD_COPPER.get()),
                        RecipeCategory.MISC,
                        ModBlocks.TITANIUM_CLAD_COPPER_SLAB.get(), 2)
                .unlockedBy("has_titanium_clad_copper", has(ModBlocks.TITANIUM_CLAD_COPPER.get()))
                .save(recipeOutput, "createcybernetics:titanium_clad_copper_slab_from_stonecutter");



//SMITHING
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.TITANIUMSHEET),
                        Ingredient.of(ModItems.XP_CAPSULE.get()),
                        Ingredient.of(ModItems.COMPONENT_WIRING.get()),
                        RecipeCategory.MISC,
                        ModItems.BRAINUPGRADES_CORTICALSTACK.get())
                .unlocks("has_xp_capsule", has(ModItems.XP_CAPSULE.get()))
                .save(recipeOutput, "createcybernetics:cortical_stack_from_xp_capsule");

        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.COPPER_UPGRADE_TEMPLATE.get()),
                        Ingredient.of(ModItems.BASECYBERWARE_LEFTARM.get()),
                        Ingredient.of(Items.COPPER_INGOT),
                        RecipeCategory.MISC, ModItems.BASECYBERWARE_LEFTARM_COPPERPLATED.get())
                .unlocks("has_copper_template", has(ModItems.COPPER_UPGRADE_TEMPLATE.get()))
                .save(recipeOutput, "createcybernetics:copperplated_leftarm");
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.COPPER_UPGRADE_TEMPLATE.get()),
                        Ingredient.of(ModItems.BASECYBERWARE_RIGHTARM.get()),
                        Ingredient.of(Items.COPPER_INGOT),
                        RecipeCategory.MISC, ModItems.BASECYBERWARE_RIGHTARM_COPPERPLATED.get())
                .unlocks("has_copper_template", has(ModItems.COPPER_UPGRADE_TEMPLATE.get()))
                .save(recipeOutput, "createcybernetics:copperplated_rightarm");
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.COPPER_UPGRADE_TEMPLATE.get()),
                        Ingredient.of(ModItems.BASECYBERWARE_LEFTLEG.get()),
                        Ingredient.of(Items.COPPER_INGOT),
                        RecipeCategory.MISC, ModItems.BASECYBERWARE_LEFTLEG_COPPERPLATED.get())
                .unlocks("has_copper_template", has(ModItems.COPPER_UPGRADE_TEMPLATE.get()))
                .save(recipeOutput, "createcybernetics:copperplated_leftleg");
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.COPPER_UPGRADE_TEMPLATE.get()),
                        Ingredient.of(ModItems.BASECYBERWARE_RIGHTLEG.get()),
                        Ingredient.of(Items.COPPER_INGOT),
                        RecipeCategory.MISC, ModItems.BASECYBERWARE_RIGHTLEG_COPPERPLATED.get())
                .unlocks("has_copper_template", has(ModItems.COPPER_UPGRADE_TEMPLATE.get()))
                .save(recipeOutput, "createcybernetics:copperplated_rightleg");

        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.IRON_UPGRADE_TEMPLATE.get()),
                        Ingredient.of(ModItems.BASECYBERWARE_LEFTARM.get()),
                        Ingredient.of(Items.IRON_INGOT),
                        RecipeCategory.MISC, ModItems.BASECYBERWARE_LEFTARM_IRONPLATED.get())
                .unlocks("has_iron_template", has(ModItems.IRON_UPGRADE_TEMPLATE.get()))
                .save(recipeOutput, "createcybernetics:ironplated_leftarm");
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.IRON_UPGRADE_TEMPLATE.get()),
                        Ingredient.of(ModItems.BASECYBERWARE_RIGHTARM.get()),
                        Ingredient.of(Items.IRON_INGOT),
                        RecipeCategory.MISC, ModItems.BASECYBERWARE_RIGHTARM_IRONPLATED.get())
                .unlocks("has_iron_template", has(ModItems.IRON_UPGRADE_TEMPLATE.get()))
                .save(recipeOutput, "createcybernetics:ironplated_rightarm");
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.IRON_UPGRADE_TEMPLATE.get()),
                        Ingredient.of(ModItems.BASECYBERWARE_LEFTLEG.get()),
                        Ingredient.of(Items.IRON_INGOT),
                        RecipeCategory.MISC, ModItems.BASECYBERWARE_LEFTLEG_IRONPLATED.get())
                .unlocks("has_iron_template", has(ModItems.IRON_UPGRADE_TEMPLATE.get()))
                .save(recipeOutput, "createcybernetics:ironplated_leftleg");
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.IRON_UPGRADE_TEMPLATE.get()),
                        Ingredient.of(ModItems.BASECYBERWARE_RIGHTLEG.get()),
                        Ingredient.of(Items.IRON_INGOT),
                        RecipeCategory.MISC, ModItems.BASECYBERWARE_RIGHTLEG_IRONPLATED.get())
                .unlocks("has_iron_template", has(ModItems.IRON_UPGRADE_TEMPLATE.get()))
                .save(recipeOutput, "createcybernetics:ironplated_rightleg");

        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOLD_UPGRADE_TEMPLATE.get()),
                        Ingredient.of(ModItems.BASECYBERWARE_LEFTARM.get()),
                        Ingredient.of(Items.GOLD_INGOT),
                        RecipeCategory.MISC, ModItems.BASECYBERWARE_LEFTARM_GOLDPLATED.get())
                .unlocks("has_gold_template", has(ModItems.GOLD_UPGRADE_TEMPLATE.get()))
                .save(recipeOutput, "createcybernetics:goldplated_leftarm");
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOLD_UPGRADE_TEMPLATE.get()),
                        Ingredient.of(ModItems.BASECYBERWARE_RIGHTARM.get()),
                        Ingredient.of(Items.GOLD_INGOT),
                        RecipeCategory.MISC, ModItems.BASECYBERWARE_RIGHTARM_GOLDPLATED.get())
                .unlocks("has_gold_template", has(ModItems.GOLD_UPGRADE_TEMPLATE.get()))
                .save(recipeOutput, "createcybernetics:goldplated_rightarm");
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOLD_UPGRADE_TEMPLATE.get()),
                        Ingredient.of(ModItems.BASECYBERWARE_LEFTLEG.get()),
                        Ingredient.of(Items.GOLD_INGOT),
                        RecipeCategory.MISC, ModItems.BASECYBERWARE_LEFTLEG_GOLDPLATED.get())
                .unlocks("has_gold_template", has(ModItems.GOLD_UPGRADE_TEMPLATE.get()))
                .save(recipeOutput, "createcybernetics:goldplated_leftleg");
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOLD_UPGRADE_TEMPLATE.get()),
                        Ingredient.of(ModItems.BASECYBERWARE_RIGHTLEG.get()),
                        Ingredient.of(Items.GOLD_INGOT),
                        RecipeCategory.MISC, ModItems.BASECYBERWARE_RIGHTLEG_GOLDPLATED.get())
                .unlocks("has_gold_template", has(ModItems.GOLD_UPGRADE_TEMPLATE.get()))
                .save(recipeOutput, "createcybernetics:goldplated_rightleg");


//FOOD COOKING
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModItems.BODYPART_BRAIN.get()),
                        RecipeCategory.FOOD, ModItems.COOKED_BRAIN.get(), 0.35f, 200)
                .unlockedBy("has_brain", has(ModItems.BODYPART_BRAIN.get()))
                .save(recipeOutput, "createcybernetics:cooked_brain_from_smelting");
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModItems.BODYPART_LIVER.get()),
                        RecipeCategory.FOOD, ModItems.COOKED_LIVER.get(), 0.35f, 200)
                .unlockedBy("has_liver", has(ModItems.BODYPART_LIVER.get()))
                .save(recipeOutput, "createcybernetics:cooked_liver_from_smelting");
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModItems.BODYPART_HEART.get()),
                        RecipeCategory.FOOD, ModItems.COOKED_HEART.get(), 0.35f, 200)
                .unlockedBy("has_heart", has(ModItems.BODYPART_HEART.get()))
                .save(recipeOutput, "createcybernetics:cooked_heart_from_smelting");
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.BONE),
                        RecipeCategory.FOOD, ModItems.BONE_MARROW.get(), 0.35f, 200)
                .unlockedBy("has_bone", has(Items.BONE))
                .save(recipeOutput, "createcybernetics:bone_marrow_from_smelting");
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModItems.ANDOUILLE_SAUSAGE),
                        RecipeCategory.FOOD, ModItems.ROASTED_ANDOUILLE.get(), 0.35f, 200)
                .unlockedBy("has_bone", has(ModItems.ANDOUILLE_SAUSAGE.get()))
                .save(recipeOutput, "createcybernetics:roasted_andouille_from_smelting");



//NON-BLOCK BLOCK RECIPES
        stairBuilder(ModBlocks.SMOOTH_TITANIUM_STAIRS.get(), Ingredient.of(ModBlocks.SMOOTH_TITANIUM)).group("titanium")
                .unlockedBy("has_titaniumingot", has(ModBlocks.SMOOTH_TITANIUM)).save(recipeOutput);
        slab(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SMOOTH_TITANIUM_SLAB.get(), ModBlocks.SMOOTH_TITANIUM.get());

        stairBuilder(ModBlocks.TITANIUM_CLAD_COPPER_STAIRS.get(), Ingredient.of(ModBlocks.TITANIUM_CLAD_COPPER)).group("titanium")
                .unlockedBy("has_titanium_clad_copper", has(ModBlocks.TITANIUM_CLAD_COPPER)).save(recipeOutput);
        slab(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.TITANIUM_CLAD_COPPER_SLAB.get(), ModBlocks.TITANIUM_CLAD_COPPER.get());

        stairBuilder(ModBlocks.ETCHED_TITANIUM_COPPER_STAIRS.get(), Ingredient.of(ModBlocks.ETCHED_TITANIUM_COPPER)).group("titanium")
                .unlockedBy("has_titaniumingot", has(ModBlocks.ETCHED_TITANIUM_COPPER)).save(recipeOutput);
        slab(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.ETCHED_TITANIUM_COPPER_SLAB.get(), ModBlocks.ETCHED_TITANIUM_COPPER.get());

    }
}
