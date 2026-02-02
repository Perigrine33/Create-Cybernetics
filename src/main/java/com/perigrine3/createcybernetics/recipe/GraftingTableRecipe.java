package com.perigrine3.createcybernetics.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public record GraftingTableRecipe(
        NonNullList<Ingredient> wetware,
        ItemStack result
) implements Recipe<GraftingTableRecipeInput> {

    public static final int WETWARE_SLOTS = 4;
    public static final int INPUT_COUNT = 7;
    public static final int SLOT_MESH = 4;
    public static final int SLOT_STRING = 5;
    public static final int SLOT_TEAR = 6;

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return wetware;
    }

    @Override
    public boolean matches(GraftingTableRecipeInput input, Level level) {
        if (input.size() < INPUT_COUNT) return false;

        if (!input.getItem(SLOT_MESH).is(ModItems.COMPONENT_MESH.get())) return false;
        if (!input.getItem(SLOT_STRING).is(Items.STRING)) return false;
        if (!input.getItem(SLOT_TEAR).is(Items.GHAST_TEAR)) return false;

        List<ItemStack> stacks = new ArrayList<>(WETWARE_SLOTS);
        for (int i = 0; i < WETWARE_SLOTS; i++) {
            ItemStack s = input.getItem(i);
            if (!s.isEmpty()) stacks.add(s);
        }

        if (stacks.size() != wetware.size()) return false;

        boolean[] used = new boolean[stacks.size()];
        for (Ingredient ing : wetware) {
            boolean matched = false;
            for (int i = 0; i < stacks.size(); i++) {
                if (used[i]) continue;
                if (ing.test(stacks.get(i))) {
                    used[i] = true;
                    matched = true;
                    break;
                }
            }
            if (!matched) return false;
        }

        return true;
    }

    @Override
    public ItemStack assemble(GraftingTableRecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.GRAFTING_TABLE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.GRAFTING_TABLE_TYPE.get();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(GraftingTableRecipeInput input) {
        NonNullList<ItemStack> remains = NonNullList.withSize(INPUT_COUNT, ItemStack.EMPTY);

        for (int i = 0; i < INPUT_COUNT; i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && stack.hasCraftingRemainingItem()) {
                remains.set(i, stack.getCraftingRemainingItem());
            }
        }

        return remains;
    }

    public static final class Serializer implements RecipeSerializer<GraftingTableRecipe> {

        private static final Codec<List<Ingredient>> WETWARE_LIST_CODEC =
                Ingredient.CODEC_NONEMPTY.listOf().flatXmap(Serializer::validateWetwareSize, Serializer::validateWetwareSize);

        private static DataResult<List<Ingredient>> validateWetwareSize(List<Ingredient> list) {
            if (list.isEmpty()) {
                return DataResult.error(() -> "wetware must have at least 1 ingredient");
            }
            if (list.size() > WETWARE_SLOTS) {
                return DataResult.error(() -> "wetware must have 1.." + WETWARE_SLOTS + " ingredients, got " + list.size());
            }
            return DataResult.success(list);
        }

        public static final MapCodec<GraftingTableRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                WETWARE_LIST_CODEC.fieldOf("wetware").forGetter(r -> r.wetware),
                ItemStack.CODEC.fieldOf("result").forGetter(GraftingTableRecipe::result)
        ).apply(inst, (wetware, result) -> new GraftingTableRecipe(toNNL(wetware), result)));

        public static final StreamCodec<RegistryFriendlyByteBuf, GraftingTableRecipe> STREAM_CODEC =
                new StreamCodec<>() {
                    @Override
                    public GraftingTableRecipe decode(RegistryFriendlyByteBuf buf) {
                        int n = buf.readVarInt();
                        NonNullList<Ingredient> wetware = NonNullList.withSize(n, Ingredient.EMPTY);
                        for (int i = 0; i < n; i++) {
                            wetware.set(i, Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                        }
                        ItemStack res = ItemStack.STREAM_CODEC.decode(buf);
                        return new GraftingTableRecipe(wetware, res);
                    }

                    @Override
                    public void encode(RegistryFriendlyByteBuf buf, GraftingTableRecipe recipe) {
                        buf.writeVarInt(recipe.wetware.size());
                        for (Ingredient ing : recipe.wetware) {
                            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ing);
                        }
                        ItemStack.STREAM_CODEC.encode(buf, recipe.result);
                    }
                };

        @Override
        public MapCodec<GraftingTableRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GraftingTableRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static NonNullList<Ingredient> toNNL(List<Ingredient> list) {
            NonNullList<Ingredient> nn = NonNullList.withSize(list.size(), Ingredient.EMPTY);
            for (int i = 0; i < list.size(); i++) nn.set(i, list.get(i));
            return nn;
        }
    }
}
