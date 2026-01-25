package com.perigrine3.createcybernetics.recipe;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record EngineeringTableRecipe(
        int width,
        int height,
        NonNullList<Ingredient> ingredients, // size = width * height
        ItemStack result,
        boolean accept_mirrored
) implements Recipe<EngineeringTableRecipeInput> {

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public boolean matches(EngineeringTableRecipeInput input, net.minecraft.world.level.Level level) {
        // Allow client-side prediction; do NOT return false on client
        // Grid is expected to be exactly 25 items (5x5)
        if (width <= 0 || height <= 0) return false;

        // Try every top-left offset where pattern fits within 5x5
        int maxX = 5 - width;
        int maxY = 5 - height;

        for (int offY = 0; offY <= maxY; offY++) {
            for (int offX = 0; offX <= maxX; offX++) {
                if (matchesAt(input, offX, offY, false)) return true;
                if (accept_mirrored && matchesAt(input, offX, offY, true)) return true;
            }
        }

        return false;
    }

    private boolean matchesAt(EngineeringTableRecipeInput input, int offX, int offY, boolean mirror) {
        // Ensure everything outside the pattern area is empty (like shaped crafting).
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                int gridIndex = x + y * 5;

                boolean inside =
                        x >= offX && x < offX + width &&
                                y >= offY && y < offY + height;

                ItemStack stack = input.getItem(gridIndex);

                if (!inside) {
                    if (!stack.isEmpty()) return false;
                    continue;
                }

                int relX = x - offX;
                int relY = y - offY;

                int patX = mirror ? (width - 1 - relX) : relX;
                int patIndex = patX + relY * width;

                Ingredient ing = ingredients.get(patIndex);
                if (!ing.test(stack)) return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(EngineeringTableRecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true; // your container is fixed 5x5 anyway
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ENGINEERING_TABLE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ENGINEERING_TABLE_TYPE.get();
    }

    public static final class Serializer implements RecipeSerializer<EngineeringTableRecipe> {

        // "key" is JSON map of single-char strings -> Ingredient
        private static final Codec<Map<String, Ingredient>> KEY_CODEC =
                Codec.unboundedMap(Codec.STRING, Ingredient.CODEC_NONEMPTY)
                        .flatXmap(Serializer::validateKeyMap, Serializer::validateKeyMap);

        private static DataResult<Map<String, Ingredient>> validateKeyMap(Map<String, Ingredient> map) {
            for (String k : map.keySet()) {
                if (k.length() != 1) {
                    return DataResult.error(() -> "Key entries must be single-character strings. Got: \"" + k + "\"");
                }
                if (" ".equals(k)) {
                    return DataResult.error(() -> "Key entry \" \" (space) is reserved for empty and cannot be defined.");
                }
            }
            return DataResult.success(map);
        }

        private static final Codec<List<String>> PATTERN_CODEC =
                Codec.list(Codec.STRING).flatXmap(Serializer::validatePattern, Serializer::validatePattern);

        private static DataResult<List<String>> validatePattern(List<String> pattern) {
            if (pattern.isEmpty()) return DataResult.error(() -> "Pattern must have at least 1 row.");
            if (pattern.size() > 5) return DataResult.error(() -> "Pattern height must be <= 5.");

            int w = pattern.get(0).length();
            if (w == 0) return DataResult.error(() -> "Pattern rows must not be empty.");
            if (w > 5) return DataResult.error(() -> "Pattern width must be <= 5.");

            for (String row : pattern) {
                if (row.length() != w) {
                    return DataResult.error(() -> "All pattern rows must be the same width.");
                }
            }
            return DataResult.success(pattern);
        }

        public static final MapCodec<EngineeringTableRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                PATTERN_CODEC.fieldOf("pattern").forGetter(r -> {
                    // We do not store the raw pattern; this getter is only needed for roundtrip
                    // If you don't care about encoding back to JSON, you can omit roundtrip support.
                    throw new UnsupportedOperationException("Encoding to JSON not supported for EngineeringTableRecipe");
                }),
                KEY_CODEC.fieldOf("key").forGetter(r -> {
                    throw new UnsupportedOperationException("Encoding to JSON not supported for EngineeringTableRecipe");
                }),
                ItemStack.CODEC.fieldOf("result").forGetter(EngineeringTableRecipe::result),
                Codec.BOOL.optionalFieldOf("accept_mirrored", true).forGetter(EngineeringTableRecipe::accept_mirrored)
        ).apply(inst, (pattern, key, result, accept_mirrored) -> fromJson(pattern, key, result, accept_mirrored)));

        public static final StreamCodec<RegistryFriendlyByteBuf, EngineeringTableRecipe> STREAM_CODEC =
                new StreamCodec<>() {
                    @Override
                    public EngineeringTableRecipe decode(RegistryFriendlyByteBuf buf) {
                        int w = buf.readVarInt();
                        int h = buf.readVarInt();
                        boolean accept_mirrored = buf.readBoolean();

                        int count = buf.readVarInt();
                        NonNullList<Ingredient> ings = NonNullList.withSize(count, Ingredient.EMPTY);
                        for (int i = 0; i < count; i++) {
                            ings.set(i, Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                        }

                        ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
                        return new EngineeringTableRecipe(w, h, ings, result, accept_mirrored);
                    }

                    @Override
                    public void encode(RegistryFriendlyByteBuf buf, EngineeringTableRecipe recipe) {
                        buf.writeVarInt(recipe.width);
                        buf.writeVarInt(recipe.height);
                        buf.writeBoolean(recipe.accept_mirrored);

                        buf.writeVarInt(recipe.ingredients.size());
                        for (int i = 0; i < recipe.ingredients.size(); i++) {
                            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.ingredients.get(i));
                        }

                        ItemStack.STREAM_CODEC.encode(buf, recipe.result);
                    }
                };

        @Override
        public MapCodec<EngineeringTableRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, EngineeringTableRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static EngineeringTableRecipe fromJson(List<String> pattern, Map<String, Ingredient> key, ItemStack result, boolean accept_mirrored) {
            int h = pattern.size();
            int w = pattern.get(0).length();

            NonNullList<Ingredient> ings = NonNullList.withSize(w * h, Ingredient.EMPTY);

            for (int y = 0; y < h; y++) {
                String row = pattern.get(y);
                for (int x = 0; x < w; x++) {
                    char c = row.charAt(x);
                    if (c == ' ') {
                        ings.set(x + y * w, Ingredient.EMPTY);
                        continue;
                    }

                    Ingredient ing = key.get(String.valueOf(c));
                    if (ing == null) {
                        throw new IllegalArgumentException("Pattern uses symbol '" + c + "' but it is not defined in key.");
                    }
                    ings.set(x + y * w, ing);
                }
            }

            return new EngineeringTableRecipe(w, h, ings, result, accept_mirrored);
        }
    }
}
