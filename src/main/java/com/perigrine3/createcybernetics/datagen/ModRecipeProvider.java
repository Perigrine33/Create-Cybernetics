package com.perigrine3.createcybernetics.datagen;

import com.perigrine3.createcybernetics.block.ModBlocks;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.level.ItemLike;
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


//TITANIUM SMELTING
        oreSmelting(recipeOutput, TITANIUM_SMELTABLES, RecipeCategory.MISC,
                ModItems.TITANIUMINGOT.get(), 0.25f, 200, "titanium");
//TITANIUM BLASTING
        oreBlasting(recipeOutput, TITANIUM_SMELTABLES, RecipeCategory.MISC,
                ModItems.TITANIUMINGOT.get(), 0.25f, 100, "titanium");
    }
}
