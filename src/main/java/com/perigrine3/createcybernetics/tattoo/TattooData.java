package com.perigrine3.createcybernetics.tattoo;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class TattooData {
    private static final String NBT_ROOT = "createcybernetics_tattoo";
    private static final String NBT_ID = "id";
    private static final String NBT_HASH = "hash";
    private static final String NBT_FILE = "file";
    private static final String NBT_DISPLAY = "display";
    private static final String NBT_LAYER = "layer";

    private TattooData() {
    }

    public static void write(ItemStack stack, TattooEntry tattoo, TattooLayer layer) {
        if (stack == null || stack.isEmpty() || tattoo == null) {
            return;
        }

        TattooLayer safeLayer = layer == null ? TattooLayer.UNDER_CYBERWARE : layer;

        CompoundTag root = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

        CompoundTag tattooTag = new CompoundTag();
        tattooTag.putString(NBT_ID, tattoo.id().toString());
        tattooTag.putString(NBT_HASH, tattoo.sha256());
        tattooTag.putString(NBT_FILE, tattoo.fileName());
        tattooTag.putString(NBT_DISPLAY, tattoo.displayName());
        tattooTag.putString(NBT_LAYER, safeLayer.name());

        root.put(NBT_ROOT, tattooTag);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
    }

    public static boolean has(ItemStack stack) {
        CompoundTag tag = getTattooTag(stack);
        return tag != null && tag.contains(NBT_ID) && tag.contains(NBT_HASH);
    }

    public static ResourceLocation getTattooId(ItemStack stack) {
        CompoundTag tag = getTattooTag(stack);
        if (tag == null || !tag.contains(NBT_ID)) {
            return null;
        }

        try {
            return ResourceLocation.parse(tag.getString(NBT_ID));
        } catch (Exception ignored) {
            return null;
        }
    }

    public static String getHash(ItemStack stack) {
        CompoundTag tag = getTattooTag(stack);
        if (tag == null || !tag.contains(NBT_HASH)) {
            return "";
        }

        return tag.getString(NBT_HASH);
    }

    public static String getFileName(ItemStack stack) {
        CompoundTag tag = getTattooTag(stack);
        if (tag == null || !tag.contains(NBT_FILE)) {
            return "";
        }

        return tag.getString(NBT_FILE);
    }

    public static String getDisplayName(ItemStack stack) {
        CompoundTag tag = getTattooTag(stack);
        if (tag == null || !tag.contains(NBT_DISPLAY)) {
            return "";
        }

        return tag.getString(NBT_DISPLAY);
    }

    public static TattooLayer getLayer(ItemStack stack) {
        CompoundTag tag = getTattooTag(stack);
        if (tag == null || !tag.contains(NBT_LAYER)) {
            return TattooLayer.UNDER_CYBERWARE;
        }

        try {
            return TattooLayer.valueOf(tag.getString(NBT_LAYER));
        } catch (Exception ignored) {
            return TattooLayer.UNDER_CYBERWARE;
        }
    }

    public static void clear(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        CompoundTag root = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

        if (root.contains(NBT_ROOT)) {
            root.remove(NBT_ROOT);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
        }
    }

    private static CompoundTag getTattooTag(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        CompoundTag root = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

        if (!root.contains(NBT_ROOT)) {
            return null;
        }

        return root.getCompound(NBT_ROOT);
    }
}