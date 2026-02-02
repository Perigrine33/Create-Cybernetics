package com.perigrine3.createcybernetics.compat.jei;

import com.perigrine3.createcybernetics.block.ModBlocks;
import com.perigrine3.createcybernetics.recipe.EngineeringTableRecipe;
import com.perigrine3.createcybernetics.recipe.GraftingTableRecipe;
import com.perigrine3.createcybernetics.recipe.ModRecipes;
import com.perigrine3.createcybernetics.screen.ModMenuTypes;
import com.perigrine3.createcybernetics.screen.custom.EngineeringTableMenu;
import com.perigrine3.createcybernetics.screen.custom.EngineeringTableScreen;
import com.perigrine3.createcybernetics.screen.custom.GraftingTableMenu;
import com.perigrine3.createcybernetics.screen.custom.GraftingTableScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JEICyberneticsPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath("createcybernetics", "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new EngineeringTableRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new GraftingTableRecipeCategory(registration.getJeiHelpers().getGuiHelper())

        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (Minecraft.getInstance().level == null) return;

        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<RecipeHolder<EngineeringTableRecipe>> engineeringTableRecipes =
                recipeManager.getAllRecipesFor(ModRecipes.ENGINEERING_TABLE_TYPE.get());

        registration.addRecipes(
                EngineeringTableRecipeCategory.ENGINEERING_TABLE_RECIPE_TYPE,
                engineeringTableRecipes
        );

        List<RecipeHolder<GraftingTableRecipe>> graftingTableRecipes =
                recipeManager.getAllRecipesFor(ModRecipes.GRAFTING_TABLE_TYPE.get());

        registration.addRecipes(
                GraftingTableRecipeCategory.GRAFTING_TABLE_RECIPE_TYPE,
                graftingTableRecipes
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                new ItemStack(ModBlocks.ENGINEERING_TABLE.get()),
                EngineeringTableRecipeCategory.ENGINEERING_TABLE_RECIPE_TYPE
        );

        registration.addRecipeCatalyst(
                new ItemStack(ModBlocks.GRAFTING_TABLE.get()),
                GraftingTableRecipeCategory.GRAFTING_TABLE_RECIPE_TYPE
        );
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(
                EngineeringTableScreen.class,
                157, 103,
                16, 11,
                EngineeringTableRecipeCategory.ENGINEERING_TABLE_RECIPE_TYPE
        );

        registration.addRecipeClickArea(
                GraftingTableScreen.class,
                109, 38,
                14, 9,
                GraftingTableRecipeCategory.GRAFTING_TABLE_RECIPE_TYPE
        );
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(
                EngineeringTableMenu.class,
                ModMenuTypes.ENGINEERING_TABLE_MENU.get(),
                EngineeringTableRecipeCategory.ENGINEERING_TABLE_RECIPE_TYPE,
                1, 25,
                26, 36);

        registration.addRecipeTransferHandler(
                GraftingTableMenu.class,
                ModMenuTypes.GRAFTING_TABLE_MENU.get(),
                GraftingTableRecipeCategory.GRAFTING_TABLE_RECIPE_TYPE,
                0, 7,
                8, 36);
    }
}
