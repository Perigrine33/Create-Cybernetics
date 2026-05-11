package com.perigrine3.createcybernetics.compat.origins;

import com.perigrine3.createcybernetics.ConfigValues;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class OriginProfileResolver {
    private OriginProfileResolver() {}

    public static int getStartingHumanity(Player player) {
        if (player == null) {
            return ConfigValues.BASE_HUMANITY;
        }

        ResourceLocation origin = OriginsCompat.getPrimaryOrigin(player);
        if (origin == null) {
            return ConfigValues.BASE_HUMANITY;
        }

        return OriginProfiles.getStartingHumanity(origin, ConfigValues.BASE_HUMANITY);
    }

    public static ItemStack getDefaultOrgan(Player player, CyberwareSlot slot, int index, ItemStack fallback) {
        if (slot == null || index < 0) {
            return copyOrEmpty(fallback);
        }

        if (player == null) {
            return copyOrEmpty(fallback);
        }

        ResourceLocation origin = OriginsCompat.getPrimaryOrigin(player);
        if (origin == null) {
            return copyOrEmpty(fallback);
        }

        ItemStack configured = OriginProfiles.getDefaultOrgan(origin, slot, index);
        if (!configured.isEmpty()) {
            return configured.copy();
        }

        return copyOrEmpty(fallback);
    }

    public static boolean isConfiguredOrganForSlot(ItemStack stack, CyberwareSlot slot) {
        if (stack == null || stack.isEmpty() || slot == null) {
            return false;
        }

        return OriginProfiles.isConfiguredOrganForSlot(stack, slot);
    }

    private static ItemStack copyOrEmpty(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        return stack.copy();
    }
}