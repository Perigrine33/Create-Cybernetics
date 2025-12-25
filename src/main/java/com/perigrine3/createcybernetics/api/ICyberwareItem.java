package com.perigrine3.createcybernetics.api;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public interface ICyberwareItem {

    Set<CyberwareSlot> getSupportedSlots();

    default boolean supportsSlot(CyberwareSlot slot) {
        return getSupportedSlots().contains(slot);
    }

    default Set<Item> requiresCyberware(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of();
    }

    default Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of();
    }

    boolean replacesOrgan();

    Set<CyberwareSlot> getReplacedOrgans();

    default TagKey<Item> getReplacedOrganItemTag(ItemStack installedStack, CyberwareSlot slot) {
        return null;
    }

    default int maxStacksPerSlotType(ItemStack stack, CyberwareSlot slotType) {
        return 1;
    }

    int getHumanityCost();

    default void onInstalled(Player player) {}

    default void onRemoved(Player player) {}

    default void onTick(Player player) {}

    default void onInstalled(Player player, ItemStack installedStack) {
        onInstalled(player);
    }

    default boolean dropsOnDeath(ItemStack installedStack, CyberwareSlot slot) {
        return true;
    }

    /* -------------------- ENERGY -------------------- */
    default int getEnergyGeneratedPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return 0;
    }

    default int getEnergyUsedPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return 0;
    }

    default int getEnergyCapacity(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return 0;
    }

    default boolean storesEnergy(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return getEnergyCapacity(player, installedStack, slot) > 0;
    }

    default int getMaxEnergyReceivePerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        // how fast energy can be inserted into this cyberware's storage
        return getEnergyCapacity(player, installedStack, slot);
    }

    default int getMaxEnergyExtractPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        // how fast energy can be pulled from this cyberware's storage
        return getEnergyCapacity(player, installedStack, slot);
    }

    default boolean usesEnergySystem(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return getEnergyGeneratedPerTick(player, installedStack, slot) > 0
                || getEnergyUsedPerTick(player, installedStack, slot) > 0
                || getEnergyCapacity(player, installedStack, slot) > 0;
    }

    default boolean requiresEnergyToFunction(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return getEnergyUsedPerTick(player, installedStack, slot) > 0;
    }}
