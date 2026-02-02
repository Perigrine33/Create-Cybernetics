package com.perigrine3.createcybernetics.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public record GraftingTableRecipeInput(List<ItemStack> items) implements RecipeInput {

    public GraftingTableRecipeInput {
        // Avoid external mutation + null list
        items = (items == null) ? List.of() : List.copyOf(items);
    }

    @Override
    public ItemStack getItem(int i) {
        return (i >= 0 && i < items.size()) ? items.get(i) : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }
}
