package com.perigrine3.createcybernetics.recipe;

import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.item.generic.InfologDataShardItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public final class CyberwarePrimaryDyeRecipe extends CustomRecipe {

    public CyberwarePrimaryDyeRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        ItemStack target = ItemStack.EMPTY;
        boolean hasDye = false;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof DyeItem) {
                hasDye = true;
                continue;
            }

            // Only allow exactly one non-dye item in the grid.
            if (!target.isEmpty()) return false;

            if (!isValidTarget(stack)) return false;
            target = stack;
        }

        return !target.isEmpty() && hasDye;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack target = ItemStack.EMPTY;
        List<DyeItem> dyes = new ArrayList<>();

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof DyeItem dye) {
                dyes.add(dye);
                continue;
            }

            // Only allow exactly one non-dye item in the grid.
            if (!target.isEmpty()) return ItemStack.EMPTY;

            if (!isValidTarget(stack)) return ItemStack.EMPTY;
            target = stack;
        }

        if (target.isEmpty() || dyes.isEmpty()) return ItemStack.EMPTY;

        ItemStack out = target.copy();
        out.setCount(1);

        int rgb = cc$mixDyeRgb(out, dyes);
        out.set(DataComponents.DYED_COLOR, new DyedItemColor(rgb, true));

        return out;
    }

    private static boolean isValidTarget(ItemStack stack) {
        // Case 1: Cyberware item that opts into dyeing (your existing ICyberwareItem API)
        if (stack.getItem() instanceof ICyberwareItem cyberwareItem) {
            return cyberwareItem.isDyeable(stack);
        }

        // Case 2: Infolog data shard (or similar) that has its own dyeable flag
        if (stack.getItem() instanceof InfologDataShardItem dataShardItem) {
            return dataShardItem.isDyeable(stack);
        }

        return false;
    }

    private static int cc$mixDyeRgb(ItemStack base, List<DyeItem> dyes) {
        int rTotal = 0;
        int gTotal = 0;
        int bTotal = 0;

        int maxTotal = 0;
        int count = 0;

        DyedItemColor existing = base.get(DataComponents.DYED_COLOR);
        if (existing != null) {
            int rgb = existing.rgb();
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;

            rTotal += r;
            gTotal += g;
            bTotal += b;

            maxTotal += Math.max(r, Math.max(g, b));
            count++;
        }

        for (DyeItem dye : dyes) {
            int rgb = dye.getDyeColor().getTextureDiffuseColor();
            rgb &= 0x00FFFFFF;

            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;

            rTotal += r;
            gTotal += g;
            bTotal += b;

            maxTotal += Math.max(r, Math.max(g, b));
            count++;
        }

        if (count <= 0) return 0xFFFFFF;

        int rAvg = rTotal / count;
        int gAvg = gTotal / count;
        int bAvg = bTotal / count;

        float brightnessAvg = (float) maxTotal / (float) count;
        float maxAvg = (float) Math.max(rAvg, Math.max(gAvg, bAvg));

        if (maxAvg > 0.0F) {
            rAvg = (int) (rAvg * (brightnessAvg / maxAvg));
            gAvg = (int) (gAvg * (brightnessAvg / maxAvg));
            bAvg = (int) (bAvg * (brightnessAvg / maxAvg));
        }

        rAvg = Math.max(0, Math.min(255, rAvg));
        gAvg = Math.max(0, Math.min(255, gAvg));
        bAvg = Math.max(0, Math.min(255, bAvg));

        return (rAvg << 16) | (gAvg << 8) | bAvg;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CYBERWARE_PRIMARY_DYE.get();
    }
}
