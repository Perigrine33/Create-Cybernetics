package com.perigrine3.createcybernetics.api;

import com.perigrine3.createcybernetics.common.capabilities.CyberwareAccess;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

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

    default Set<Item> incompatibleCyberware(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of();
    }

    default Set<TagKey<Item>> incompatibleCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of();
    }

    default boolean matchesCyberwareTagAsInstalled(ItemStack installedStack, CyberwareSlot installedSlot, TagKey<Item> tag) {
        if (installedStack == null || installedStack.isEmpty() || installedSlot == null || tag == null) {
            return false;
        }

        if (installedStack.is(tag)) {
            return true;
        }

        Item item = installedStack.getItem();

        if (item instanceof ICyberwareItem cyberwareItem) {
            TagKey<Item> contextualReplacementTag = cyberwareItem.getReplacedOrganItemTag(installedStack, installedSlot);
            return tag.equals(contextualReplacementTag);
        }

        return false;
    }

    default boolean matchesAnyCyberwareTagAsInstalled(ItemStack installedStack, CyberwareSlot installedSlot, Set<TagKey<Item>> tags) {
        if (tags == null || tags.isEmpty()) {
            return false;
        }

        for (TagKey<Item> tag : tags) {
            if (matchesCyberwareTagAsInstalled(installedStack, installedSlot, tag)) {
                return true;
            }
        }

        return false;
    }

    default boolean hasRequiredCyberware(
            ItemStack thisStack,
            CyberwareSlot thisSlot,
            ItemStack otherStack,
            CyberwareSlot otherSlot
    ) {
        if (thisStack == null || thisStack.isEmpty()) {
            return false;
        }

        if (otherStack == null || otherStack.isEmpty()) {
            return false;
        }

        if (requiresCyberware(thisStack, thisSlot).contains(otherStack.getItem())) {
            return true;
        }

        return matchesAnyCyberwareTagAsInstalled(otherStack, otherSlot, requiresCyberwareTags(thisStack, thisSlot));
    }

    default boolean isIncompatibleWith(
            ItemStack thisStack,
            CyberwareSlot thisSlot,
            ItemStack otherStack,
            CyberwareSlot otherSlot
    ) {
        if (thisStack == null || thisStack.isEmpty()) {
            return false;
        }

        if (otherStack == null || otherStack.isEmpty()) {
            return false;
        }

        if (incompatibleCyberware(thisStack, thisSlot).contains(otherStack.getItem())) {
            return true;
        }

        return matchesAnyCyberwareTagAsInstalled(otherStack, otherSlot, incompatibleCyberwareTags(thisStack, thisSlot));
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

    /* -------------------- ENTITY-GENERAL LIFECYCLE -------------------- */

    default void onInstalled(LivingEntity entity) {}

    default void onRemoved(LivingEntity entity) {}

    default void onTick(LivingEntity entity) {}

    default void onTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot, int index) {
        onTick(entity);
    }

    default void onInstalled(LivingEntity entity, ItemStack installedStack) {
        onInstalled(entity);
    }

    default void onRemoved(LivingEntity entity, ItemStack installedStack) {
        onRemoved(entity);
    }

    default boolean dropsOnDeath(ItemStack installedStack, CyberwareSlot slot) {
        return true;
    }

    default boolean isToggleableByWheel(ItemStack installedStack, CyberwareSlot slot) {
        return installedStack.is(ModTags.Items.TOGGLEABLE_CYBERWARE);
    }

    default boolean isEnabledByWheel(LivingEntity entity) {
        if (entity == null) return false;

        for (var entry : CyberwareAccess.getAll(entity).entrySet()) {
            CyberwareSlot slot = entry.getKey();
            InstalledCyberware[] arr = entry.getValue();
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {
                InstalledCyberware installed = arr[i];
                if (installed == null) continue;

                ItemStack st = installed.getItem();
                if (st == null || st.isEmpty()) continue;
                if (st.getItem() != (Item) this) continue;

                if (!isToggleableByWheel(st, slot)) return true;

                return CyberwareAccess.isEnabled(entity, slot, i);
            }
        }

        return false;
    }

    /* -------------------- ENERGY -------------------- */

    default int getEnergyGeneratedPerTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return 0;
    }

    default boolean shouldGenerateEnergyThisTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return getEnergyGeneratedPerTick(entity, installedStack, slot) > 0;
    }

    default int getEnergyGeneratedThisTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return shouldGenerateEnergyThisTick(entity, installedStack, slot)
                ? Math.max(0, getEnergyGeneratedPerTick(entity, installedStack, slot))
                : 0;
    }

    default int getEnergyUsedPerTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return 0;
    }

    default boolean shouldUseEnergyThisTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return getEnergyUsedPerTick(entity, installedStack, slot) > 0;
    }

    default int getEnergyUsedThisTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return shouldUseEnergyThisTick(entity, installedStack, slot)
                ? Math.max(0, getEnergyUsedPerTick(entity, installedStack, slot))
                : 0;
    }

    default int getEnergyCapacity(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return 0;
    }

    default boolean shouldContributeCapacityThisTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return getEnergyCapacity(entity, installedStack, slot) > 0;
    }

    default boolean storesEnergy(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return shouldContributeCapacityThisTick(entity, installedStack, slot)
                && getEnergyCapacity(entity, installedStack, slot) > 0;
    }

    default int getMaxEnergyReceivePerTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return storesEnergy(entity, installedStack, slot)
                ? getEnergyCapacity(entity, installedStack, slot)
                : 0;
    }

    default int getMaxEnergyExtractPerTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return storesEnergy(entity, installedStack, slot)
                ? getEnergyCapacity(entity, installedStack, slot)
                : 0;
    }

    default int getEnergyActivationCost(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return 0;
    }

    default boolean shouldConsumeActivationEnergyThisTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return false;
    }

    default String getActivationPaidNbtKey(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        String cls = this.getClass().getName().replace('.', '_');
        return "cc_energy_actpaid_" + cls + "_" + slot.name();
    }

    default int getEnergyPriority(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return 0;
    }

    default boolean requiresEnergyToFunction(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return getEnergyUsedPerTick(entity, installedStack, slot) > 0
                || getEnergyActivationCost(entity, installedStack, slot) > 0;
    }

    default void onPowerLost(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {}

    default void onPowerRestored(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {}

    default void onUnpoweredTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {}

    default void onPoweredTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {}

    default boolean acceptsGeneratedEnergy(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return storesEnergy(entity, installedStack, slot);
    }

    default boolean acceptsChargerEnergy(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return false;
    }

    default int getChargerEnergyReceivePerTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return 0;
    }

    /* -------------------- DYE -------------------- */

    default boolean isDyeable(ItemStack stack, CyberwareSlot slot) {
        return false;
    }

    default boolean isDyeable(ItemStack stack) {
        return false;
    }

    default boolean isDyed(ItemStack installedStack, CyberwareSlot slot) {
        if (installedStack == null || installedStack.isEmpty()) return false;
        if (!isDyeable(installedStack, slot)) return false;
        return installedStack.has(DataComponents.DYED_COLOR);
    }

    default int dyeColor(ItemStack installedStack, CyberwareSlot slot) {
        if (!isDyed(installedStack, slot)) return 0xFFFFFFFF;

        DyedItemColor dyed = installedStack.get(DataComponents.DYED_COLOR);
        if (dyed == null) return 0xFFFFFFFF;

        return 0xFF000000 | dyed.rgb();
    }

    /* -------------------- TEMP PLAYER BRIDGES -------------------- */

    @Deprecated
    default void onInstalled(Player player) {
        onInstalled((LivingEntity) player);
    }

    @Deprecated
    default void onRemoved(Player player) {
        onRemoved((LivingEntity) player);
    }

    @Deprecated
    default void onTick(Player player) {
        onTick((LivingEntity) player);
    }

    @Deprecated
    default void onTick(Player player, ItemStack installedStack, CyberwareSlot slot, int index) {
        onTick((LivingEntity) player, installedStack, slot, index);
    }

    @Deprecated
    default void onInstalled(Player player, ItemStack installedStack) {
        onInstalled((LivingEntity) player, installedStack);
    }

    @Deprecated
    default void onRemoved(Player player, ItemStack installedStack) {
        onRemoved((LivingEntity) player, installedStack);
    }

    @Deprecated
    default int getEnergyGeneratedPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return getEnergyGeneratedPerTick((LivingEntity) player, installedStack, slot);
    }

    @Deprecated
    default boolean shouldGenerateEnergyThisTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return shouldGenerateEnergyThisTick((LivingEntity) player, installedStack, slot);
    }

    @Deprecated
    default int getEnergyGeneratedThisTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return getEnergyGeneratedThisTick((LivingEntity) player, installedStack, slot);
    }

    @Deprecated
    default int getEnergyUsedPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return getEnergyUsedPerTick((LivingEntity) player, installedStack, slot);
    }

    @Deprecated
    default boolean shouldUseEnergyThisTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return shouldUseEnergyThisTick((LivingEntity) player, installedStack, slot);
    }

    @Deprecated
    default int getEnergyUsedThisTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return getEnergyUsedThisTick((LivingEntity) player, installedStack, slot);
    }

    @Deprecated
    default int getEnergyCapacity(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return getEnergyCapacity((LivingEntity) player, installedStack, slot);
    }

    @Deprecated
    default boolean shouldContributeCapacityThisTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return shouldContributeCapacityThisTick((LivingEntity) player, installedStack, slot);
    }

    @Deprecated
    default boolean storesEnergy(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return storesEnergy((LivingEntity) player, installedStack, slot);
    }

    @Deprecated
    default int getMaxEnergyReceivePerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return getMaxEnergyReceivePerTick((LivingEntity) player, installedStack, slot);
    }

    @Deprecated
    default int getMaxEnergyExtractPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return getMaxEnergyExtractPerTick((LivingEntity) player, installedStack, slot);
    }

    @Deprecated
    default int getEnergyActivationCost(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return getEnergyActivationCost((LivingEntity) player, installedStack, slot);
    }

    @Deprecated
    default boolean shouldConsumeActivationEnergyThisTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return shouldConsumeActivationEnergyThisTick((LivingEntity) player, installedStack, slot);
    }

    @Deprecated
    default String getActivationPaidNbtKey(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return getActivationPaidNbtKey((LivingEntity) player, installedStack, slot);
    }

    @Deprecated
    default int getEnergyPriority(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return getEnergyPriority((LivingEntity) player, installedStack, slot);
    }

    @Deprecated
    default boolean requiresEnergyToFunction(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return requiresEnergyToFunction((LivingEntity) player, installedStack, slot);
    }

    @Deprecated
    default void onPowerLost(Player player, ItemStack installedStack, CyberwareSlot slot) {
        onPowerLost((LivingEntity) player, installedStack, slot);
    }

    @Deprecated
    default void onPowerRestored(Player player, ItemStack installedStack, CyberwareSlot slot) {
        onPowerRestored((LivingEntity) player, installedStack, slot);
    }

    @Deprecated
    default void onUnpoweredTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        onUnpoweredTick((LivingEntity) player, installedStack, slot);
    }

    @Deprecated
    default void onPoweredTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        onPoweredTick((LivingEntity) player, installedStack, slot);
    }

    @Deprecated
    default boolean acceptsGeneratedEnergy(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return acceptsGeneratedEnergy((LivingEntity) player, installedStack, slot);
    }

    @Deprecated
    default boolean acceptsChargerEnergy(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return acceptsChargerEnergy((LivingEntity) player, installedStack, slot);
    }

    @Deprecated
    default int getChargerEnergyReceivePerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return getChargerEnergyReceivePerTick((LivingEntity) player, installedStack, slot);
    }
}