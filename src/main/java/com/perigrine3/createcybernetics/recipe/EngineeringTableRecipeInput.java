package com.perigrine3.createcybernetics.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public record EngineeringTableRecipeInput(List<ItemStack> items) implements RecipeInput {

    @Override
    public ItemStack getItem(int i) {
        return (i >= 0 && i < items.size()) ? items.get(i) : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return items.size();
    }
}
