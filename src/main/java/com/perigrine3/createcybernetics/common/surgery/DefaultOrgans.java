package com.perigrine3.createcybernetics.common.surgery;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.compat.origins.OriginProfileResolver;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.EnumMap;
import java.util.Map;

public final class DefaultOrgans {

    private static final Map<CyberwareSlot, ItemStack[]> DEFAULTS =
            new EnumMap<>(CyberwareSlot.class);

    static {
        DEFAULTS.put(CyberwareSlot.BRAIN, new ItemStack[] {
                new ItemStack(ModItems.BODYPART_BRAIN.get()),
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY
        });

        DEFAULTS.put(CyberwareSlot.EYES, new ItemStack[] {
                new ItemStack(ModItems.BODYPART_EYEBALLS.get()),
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY
        });

        DEFAULTS.put(CyberwareSlot.HEART, new ItemStack[] {
                new ItemStack(ModItems.BODYPART_HEART.get()),
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY
        });

        DEFAULTS.put(CyberwareSlot.LUNGS, new ItemStack[] {
                new ItemStack(ModItems.BODYPART_LUNGS.get()),
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY
        });

        DEFAULTS.put(CyberwareSlot.ORGANS, new ItemStack[] {
                new ItemStack(ModItems.BODYPART_LIVER.get()),
                new ItemStack(ModItems.BODYPART_INTESTINES.get()),
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY
        });

        DEFAULTS.put(CyberwareSlot.RARM, new ItemStack[] {
                new ItemStack(ModItems.BODYPART_RIGHTARM.get()),
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY
        });

        DEFAULTS.put(CyberwareSlot.LARM, new ItemStack[] {
                new ItemStack(ModItems.BODYPART_LEFTARM.get()),
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY
        });

        DEFAULTS.put(CyberwareSlot.RLEG, new ItemStack[] {
                new ItemStack(ModItems.BODYPART_RIGHTLEG.get()),
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY
        });

        DEFAULTS.put(CyberwareSlot.LLEG, new ItemStack[] {
                new ItemStack(ModItems.BODYPART_LEFTLEG.get()),
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY
        });

        DEFAULTS.put(CyberwareSlot.MUSCLE, new ItemStack[] {
                new ItemStack(ModItems.BODYPART_MUSCLE.get()),
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY
        });

        DEFAULTS.put(CyberwareSlot.BONE, new ItemStack[] {
                new ItemStack(ModItems.BODYPART_SKELETON.get()),
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY
        });

        DEFAULTS.put(CyberwareSlot.SKIN, new ItemStack[] {
                new ItemStack(ModItems.BODYPART_SKIN.get()),
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY
        });
    }

    private DefaultOrgans() {}

    public static ItemStack get(Player player, CyberwareSlot slot, int index) {
        ItemStack fallback = getVanillaDefault(slot, index);

        if (player == null) {
            return fallback;
        }

        return OriginProfileResolver.getDefaultOrgan(player, slot, index, fallback);
    }

    public static ItemStack get(CyberwareSlot slot, int index) {
        return getVanillaDefault(slot, index);
    }

    private static ItemStack getVanillaDefault(CyberwareSlot slot, int index) {
        ItemStack[] arr = DEFAULTS.get(slot);
        if (arr == null || index < 0 || index >= arr.length) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = arr[index];
        return stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
    }

    public static boolean isOrganForSlot(ItemStack stack, CyberwareSlot slot) {
        if (stack.isEmpty()) return false;

        ItemStack[] arr = DEFAULTS.get(slot);
        if (arr != null) {
            for (ItemStack organ : arr) {
                if (!organ.isEmpty() && stack.is(organ.getItem())) {
                    return true;
                }
            }
        }

        return OriginProfileResolver.isConfiguredOrganForSlot(stack, slot);
    }
}