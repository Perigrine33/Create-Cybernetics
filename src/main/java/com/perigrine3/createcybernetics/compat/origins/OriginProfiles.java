package com.perigrine3.createcybernetics.compat.origins;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public final class OriginProfiles {
    private static final Map<ResourceLocation, OriginProfile> PROFILES = new HashMap<>();

    private OriginProfiles() {}

    public static void replaceAll(Map<ResourceLocation, OriginProfile> profiles) {
        PROFILES.clear();

        if (profiles == null || profiles.isEmpty()) {
            return;
        }

        PROFILES.putAll(profiles);
    }

    public static int getStartingHumanity(ResourceLocation origin, int fallback) {
        OriginProfile profile = PROFILES.get(origin);
        if (profile == null || profile.startingHumanity() == null) {
            return fallback;
        }

        return profile.startingHumanity();
    }

    public static ItemStack getDefaultOrgan(ResourceLocation origin, CyberwareSlot slot, int index) {
        OriginProfile profile = PROFILES.get(origin);
        if (profile == null) {
            return ItemStack.EMPTY;
        }

        return profile.getDefaultOrgan(slot, index);
    }

    public static boolean isConfiguredOrganForSlot(ItemStack stack, CyberwareSlot slot) {
        if (stack == null || stack.isEmpty() || slot == null) {
            return false;
        }

        for (OriginProfile profile : PROFILES.values()) {
            if (profile.hasOrganForSlot(stack, slot)) {
                return true;
            }
        }

        return false;
    }

    public static Map<ResourceLocation, OriginProfile> view() {
        return Collections.unmodifiableMap(PROFILES);
    }

    public record OriginProfile(
            ResourceLocation origin,
            Integer startingHumanity,
            Map<CyberwareSlot, ItemStack[]> defaultOrgans
    ) {
        public ItemStack getDefaultOrgan(CyberwareSlot slot, int index) {
            if (slot == null || index < 0 || defaultOrgans == null) {
                return ItemStack.EMPTY;
            }

            ItemStack[] stacks = defaultOrgans.get(slot);
            if (stacks == null || index >= stacks.length) {
                return ItemStack.EMPTY;
            }

            ItemStack stack = stacks[index];
            return stack == null || stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
        }

        public boolean hasOrganForSlot(ItemStack stack, CyberwareSlot slot) {
            if (stack == null || stack.isEmpty() || slot == null || defaultOrgans == null) {
                return false;
            }

            ItemStack[] stacks = defaultOrgans.get(slot);
            if (stacks == null) {
                return false;
            }

            for (ItemStack configured : stacks) {
                if (configured != null && !configured.isEmpty() && stack.is(configured.getItem())) {
                    return true;
                }
            }

            return false;
        }

        public Map<CyberwareSlot, ItemStack[]> safeDefaultOrgans() {
            if (defaultOrgans == null) {
                return Collections.emptyMap();
            }

            return Collections.unmodifiableMap(defaultOrgans);
        }
    }

    public static Map<CyberwareSlot, ItemStack[]> newOrganMap() {
        return new EnumMap<>(CyberwareSlot.class);
    }
}