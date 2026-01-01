package com.perigrine3.createcybernetics.api;

import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
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

    default Set<Item> requiresCyberware(ItemStack installedStack, CyberwareSlot slot) { return Set.of(); }

    default Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) { return Set.of(); }

    default Set<Item> incompatibleCyberware(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of();
    }

    default Set<TagKey<Item>> incompatibleCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of();
    }

    boolean replacesOrgan();

    Set<CyberwareSlot> getReplacedOrgans();

    default TagKey<Item> getReplacedOrganItemTag(ItemStack installedStack, CyberwareSlot slot) { return null; }

    default int maxStacksPerSlotType(ItemStack stack, CyberwareSlot slotType) { return 1; }

    int getHumanityCost();

    default void onInstalled(Player player) {}
    default void onRemoved(Player player) {}
    default void onTick(Player player) {}

    default void onTick(Player player, ItemStack installedStack, CyberwareSlot slot, int index) {
        onTick(player);
    }

    default void onInstalled(Player player, ItemStack installedStack) { onInstalled(player); }

    default boolean dropsOnDeath(ItemStack installedStack, CyberwareSlot slot) { return true; }

    default boolean isToggleableByWheel(ItemStack installedStack, CyberwareSlot slot) {
        return installedStack.is(com.perigrine3.createcybernetics.util.ModTags.Items.TOGGLEABLE_CYBERWARE);
    }

    default boolean isEnabledByWheel(Player player) {
        if (player == null) return false;
        if (!player.hasData(ModAttachments.CYBERWARE)) return false;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        for (var entry : data.getAll().entrySet()) {
            CyberwareSlot slot = entry.getKey();
            var arr = entry.getValue();
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {
                var installed = arr[i];
                if (installed == null) continue;

                ItemStack st = installed.getItem();
                if (st == null || st.isEmpty()) continue;

                if (st.getItem() != (Item) this) continue;

                // Only wheel-toggleable items consult the enabled flag.
                if (!isToggleableByWheel(st, slot)) return true;

                return data.isEnabled(slot, i);
            }
        }

        return false;
    }

    /* -------------------- ENERGY -------------------- */

    default int getEnergyGeneratedPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) { return 0; }

    default boolean shouldGenerateEnergyThisTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return getEnergyGeneratedPerTick(player, installedStack, slot) > 0;
    }

    default int getEnergyGeneratedThisTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return shouldGenerateEnergyThisTick(player, installedStack, slot)
                ? Math.max(0, getEnergyGeneratedPerTick(player, installedStack, slot))
                : 0;
    }

    default int getEnergyUsedPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) { return 0; }

    default boolean shouldUseEnergyThisTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return getEnergyUsedPerTick(player, installedStack, slot) > 0;
    }

    default int getEnergyUsedThisTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return shouldUseEnergyThisTick(player, installedStack, slot)
                ? Math.max(0, getEnergyUsedPerTick(player, installedStack, slot))
                : 0;
    }

    default int getEnergyCapacity(Player player, ItemStack installedStack, CyberwareSlot slot) { return 0; }

    default boolean shouldContributeCapacityThisTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return getEnergyCapacity(player, installedStack, slot) > 0;
    }

    default boolean storesEnergy(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return shouldContributeCapacityThisTick(player, installedStack, slot)
                && getEnergyCapacity(player, installedStack, slot) > 0;
    }

    default int getMaxEnergyReceivePerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return storesEnergy(player, installedStack, slot) ? getEnergyCapacity(player, installedStack, slot) : 0;
    }

    default int getMaxEnergyExtractPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return storesEnergy(player, installedStack, slot) ? getEnergyCapacity(player, installedStack, slot) : 0;
    }

    default int getEnergyActivationCost(Player player, ItemStack installedStack, CyberwareSlot slot) { return 0; }

    default boolean shouldConsumeActivationEnergyThisTick(Player player, ItemStack installedStack, CyberwareSlot slot) { return false; }

    default String getActivationPaidNbtKey(Player player, ItemStack installedStack, CyberwareSlot slot) {
        // If you have a registry name helper, use it. This is a safe-ish default.
        String cls = this.getClass().getName().replace('.', '_');
        return "cc_energy_actpaid_" + cls + "_" + slot.name();
    }

    default int getEnergyPriority(Player player, ItemStack installedStack, CyberwareSlot slot) { return 0; }

    default boolean requiresEnergyToFunction(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return getEnergyUsedPerTick(player, installedStack, slot) > 0
                || getEnergyActivationCost(player, installedStack, slot) > 0;
    }

    default void onPowerLost(Player player, ItemStack installedStack, CyberwareSlot slot) {}

    default void onPowerRestored(Player player, ItemStack installedStack, CyberwareSlot slot) {}

    default void onUnpoweredTick(Player player, ItemStack installedStack, CyberwareSlot slot) {}

    default void onPoweredTick(Player player, ItemStack installedStack, CyberwareSlot slot) {}

    default boolean acceptsGeneratedEnergy(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return storesEnergy(player, installedStack, slot);
    }

    default boolean acceptsChargerEnergy(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return false;
    }

    default int getChargerEnergyReceivePerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return 0;
    }
}
