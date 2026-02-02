package com.perigrine3.createcybernetics.compat.jei;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.block.ModBlocks;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.recipe.GraftingTableRecipe;
import com.perigrine3.createcybernetics.screen.custom.GraftingTableMenu;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public final class GraftingTableRecipeCategory implements IRecipeCategory<RecipeHolder<GraftingTableRecipe>> {

    public static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "grafting_table");

    public static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/grafting_table_gui.png");

    public static final RecipeType<RecipeHolder<GraftingTableRecipe>> GRAFTING_TABLE_RECIPE_TYPE =
            new RecipeType<>(UID, (Class<RecipeHolder<GraftingTableRecipe>>) (Class<?>) RecipeHolder.class);

    private final IDrawable background;
    private final IDrawable icon;

    public GraftingTableRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 4, 3, 150, 78);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.GRAFTING_TABLE.get()));
    }

    @Override
    public RecipeType<RecipeHolder<GraftingTableRecipe>> getRecipeType() {
        return GRAFTING_TABLE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.createcybernetics.grafting_table.title");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<GraftingTableRecipe> holder, IFocusGroup focuses) {
        GraftingTableRecipe recipe = holder.value();

        // Wetware slots (0..3) — recipe.wetware can be 1..4
        var wetware = recipe.getIngredients(); // returns only wetware list
        builder.addSlot(RecipeIngredientRole.INPUT, GraftingTableMenu.Layout.IN00_X - 4, GraftingTableMenu.Layout.IN00_Y - 3)
                .addIngredients(wetware.size() > 0 ? wetware.get(0) : Ingredient.EMPTY);
        builder.addSlot(RecipeIngredientRole.INPUT, GraftingTableMenu.Layout.IN01_X - 4, GraftingTableMenu.Layout.IN01_Y - 3)
                .addIngredients(wetware.size() > 1 ? wetware.get(1) : Ingredient.EMPTY);
        builder.addSlot(RecipeIngredientRole.INPUT, GraftingTableMenu.Layout.IN10_X - 4, GraftingTableMenu.Layout.IN10_Y - 3)
                .addIngredients(wetware.size() > 2 ? wetware.get(2) : Ingredient.EMPTY);
        builder.addSlot(RecipeIngredientRole.INPUT, GraftingTableMenu.Layout.IN11_X - 4, GraftingTableMenu.Layout.IN11_Y - 3)
                .addIngredients(wetware.size() > 3 ? wetware.get(3) : Ingredient.EMPTY);

        // Fixed component slots (4..6)
        builder.addSlot(RecipeIngredientRole.INPUT, GraftingTableMenu.Layout.MESH_X - 4, GraftingTableMenu.Layout.MESH_Y - 3)
                .addItemStack(new ItemStack(ModItems.COMPONENT_MESH.get()));
        builder.addSlot(RecipeIngredientRole.INPUT, GraftingTableMenu.Layout.STRING_X - 4, GraftingTableMenu.Layout.STRING_Y - 3)
                .addItemStack(new ItemStack(Items.STRING));
        builder.addSlot(RecipeIngredientRole.INPUT, GraftingTableMenu.Layout.TEAR_X - 4, GraftingTableMenu.Layout.TEAR_Y - 3)
                .addItemStack(new ItemStack(Items.GHAST_TEAR));

        ItemStack out = ItemStack.EMPTY;
        if (Minecraft.getInstance().level != null) {
            out = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, GraftingTableMenu.Layout.OUT_X - 4, GraftingTableMenu.Layout.OUT_Y - 3)
                .addItemStack(out);
    }

}
