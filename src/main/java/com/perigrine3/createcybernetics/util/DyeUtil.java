package com.perigrine3.createcybernetics.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.DyeItem;

import java.util.List;

public final class DyeUtil {
    private DyeUtil() {}

    public static int getDyeColor(ItemStack stack, int fallbackRgb) {
        return DyedItemColor.getOrDefault(stack, fallbackRgb);
    }

    public static ItemStack dyeUpgrade(ItemStack stack, List<DyeItem> dyes) {
        return DyedItemColor.applyDyes(stack, dyes);
    }

    public static void clearDye(ItemStack stack) {
        stack.remove(DataComponents.DYED_COLOR);
    }
}
