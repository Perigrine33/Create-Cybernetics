package com.perigrine3.createcybernetics.compat.jei;

import com.perigrine3.createcybernetics.recipe.EngineeringTableRecipe;
import com.perigrine3.createcybernetics.recipe.ModRecipes;
import com.perigrine3.createcybernetics.screen.ModMenuTypes;
import com.perigrine3.createcybernetics.screen.custom.EngineeringTableMenu;
import com.perigrine3.createcybernetics.screen.custom.EngineeringTableScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
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
                new EngineeringTableRecipeCategory(registration.getJeiHelpers().getGuiHelper())
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
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(
                EngineeringTableScreen.class,
                157, 103, 16, 11,
                EngineeringTableRecipeCategory.ENGINEERING_TABLE_RECIPE_TYPE
        );
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        // Slot layout:
        // 0 = output
        // 1..25 = grid (25 slots)
        // 26..61 = player inventory+hotbar (36 slots)
        registration.addRecipeTransferHandler(
                EngineeringTableMenu.class,
                ModMenuTypes.ENGINEERING_TABLE_MENU.get(),
                EngineeringTableRecipeCategory.ENGINEERING_TABLE_RECIPE_TYPE,
                1, 25,         26, 36);
    }
}
