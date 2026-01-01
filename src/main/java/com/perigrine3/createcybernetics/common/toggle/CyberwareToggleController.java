package com.perigrine3.createcybernetics.common.toggle;

import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

public final class CyberwareToggleController {
    private CyberwareToggleController() {}

    private static String key(ResourceLocation itemId) {
        return "cc_toggle_" + itemId.getNamespace() + "_" + itemId.getPath();
    }

    public static boolean isActive(ServerPlayer player, ResourceLocation itemId) {
        CompoundTag p = player.getPersistentData();
        String k = key(itemId);
        return !p.contains(k) || p.getBoolean(k);
    }

    public static boolean setActive(ServerPlayer player, ResourceLocation itemId, boolean active) {
        CompoundTag p = player.getPersistentData();
        String k = key(itemId);
        boolean before = !p.contains(k) || p.getBoolean(k);
        if (before == active) return before;

        p.putBoolean(k, active);
        return before;
    }

    public static boolean toggle(ServerPlayer player, ResourceLocation itemId) {
        boolean now = !isActive(player, itemId);
        setActive(player, itemId, now);
        return now;
    }

    public static boolean hasToggleableInstalled(ServerPlayer player, ResourceLocation itemId) {
        if (!player.hasData(ModAttachments.CYBERWARE)) return false;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        Item item = BuiltInRegistries.ITEM.get(itemId);
        if (item == null) return false;

        for (var entry : data.getAll().entrySet()) {
            var slot = entry.getKey();
            var arr = entry.getValue();
            if (arr == null) continue;

            for (var cw : arr) {
                if (cw == null) continue;
                ItemStack stack = cw.getItem();
                if (stack == null || stack.isEmpty()) continue;

                if (stack.getItem() == item && stack.is(ModTags.Items.TOGGLEABLE_CYBERWARE)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Map<ResourceLocation, ItemStack> collectToggleables(ServerPlayer player) {
        Map<ResourceLocation, ItemStack> out = new LinkedHashMap<>();
        if (!player.hasData(ModAttachments.CYBERWARE)) return out;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return out;

        for (var entry : data.getAll().entrySet()) {
            var arr = entry.getValue();
            if (arr == null) continue;

            for (var cw : arr) {
                if (cw == null) continue;
                ItemStack stack = cw.getItem();
                if (stack == null || stack.isEmpty()) continue;

                if (!stack.is(ModTags.Items.TOGGLEABLE_CYBERWARE)) continue;

                ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
                if (id == null) continue;

                out.putIfAbsent(id, stack.copy());
            }
        }

        return out;
    }
}
