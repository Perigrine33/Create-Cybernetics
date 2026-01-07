package com.perigrine3.createcybernetics.compat.jei;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.block.ModBlocks;
import com.perigrine3.createcybernetics.recipe.EngineeringTableRecipe;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class EngineeringTableRecipeCategory implements IRecipeCategory<RecipeHolder<EngineeringTableRecipe>> {

    public static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "engineering_table");

    public static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/engineering_gui.png");

    public static final RecipeType<RecipeHolder<EngineeringTableRecipe>> ENGINEERING_TABLE_RECIPE_TYPE =
            new RecipeType<>(UID, (Class<RecipeHolder<EngineeringTableRecipe>>) (Class<?>) RecipeHolder.class);

    private final IDrawable background;
    private final IDrawable icon;

    public EngineeringTableRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 59, 60, 139, 98);
        if (ModBlocks.ENGINEERING_TABLE != null) {
            this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ENGINEERING_TABLE.get()));
        } else {
            this.icon = null;
        }
    }

    @Override
    public RecipeType<RecipeHolder<EngineeringTableRecipe>> getRecipeType() {
        return ENGINEERING_TABLE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Engineering Table");
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<EngineeringTableRecipe> holder, IFocusGroup focuses) {
        EngineeringTableRecipe recipe = holder.value();

        builder.addSlot(RecipeIngredientRole.INPUT, 7, 5)
                .addIngredients(recipe.getIngredients().get(0));

        final int startX = 7;
        final int startY = 5;
        final int spacing = 18;

        int ingredientIndex = 1;

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                if (row == 0 && col == 0) continue;

                int x = startX + col * spacing;
                int y = startY + row * spacing;

                if (ingredientIndex < recipe.getIngredients().size()) {
                    builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                            .addIngredients(recipe.getIngredients().get(ingredientIndex));
                } else {
                    builder.addSlot(RecipeIngredientRole.INPUT, x, y);
                }

                ingredientIndex++;
            }
        }

        ItemStack result = ItemStack.EMPTY;
        if (Minecraft.getInstance().level != null) {
            result = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 41)
                .addItemStack(result);
    }
}
