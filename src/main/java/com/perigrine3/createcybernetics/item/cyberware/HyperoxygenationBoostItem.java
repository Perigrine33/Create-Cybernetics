package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class HyperoxygenationBoostItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final int ENERGY_PER_TICK_PER_STACK = 3;
    private static final String NBT_LAST_APPLIED_TICK = "cc_hyperox_last_tick";

    public HyperoxygenationBoostItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.translatable("tooltip.createcybernetics.lungsupgrades_hyperoxygenation.energy").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public boolean requiresEnergyToFunction(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return true;
    }

    @Override
    public Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModTags.Items.LUNGS_ITEMS);
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.LUNGS);
    }

    @Override
    public boolean replacesOrgan() {
        return false;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of();
    }

    @Override
    public int maxStacksPerSlotType(ItemStack stack, CyberwareSlot slotType) {
        return 3;
    }

    @Override
    public void onInstalled(Player player) { }

    @Override
    public void onRemoved(Player player) {
        if (player.level().isClientSide) return;

        CyberwareAttributeHelper.removeModifier(player, "hyperoxygenation_speed_1");
        CyberwareAttributeHelper.removeModifier(player, "hyperoxygenation_speed_2");
        CyberwareAttributeHelper.removeModifier(player, "hyperoxygenation_speed_3");

        player.getPersistentData().remove(NBT_LAST_APPLIED_TICK);
        onInstalled(player);
    }

    @Override
    public void onTick(Player player, ItemStack installedStack, CyberwareSlot slot, int index) {
        if (player.level().isClientSide) return;

        long now = player.level().getGameTime();
        CompoundTag ptag = player.getPersistentData();
        if (ptag.getLong(NBT_LAST_APPLIED_TICK) == now) return;
        ptag.putLong(NBT_LAST_APPLIED_TICK, now);

        CyberwareAttributeHelper.removeModifier(player, "hyperoxygenation_speed_1");
        CyberwareAttributeHelper.removeModifier(player, "hyperoxygenation_speed_2");
        CyberwareAttributeHelper.removeModifier(player, "hyperoxygenation_speed_3");

        if (!player.isSprinting()) return;

        if (!player.hasData(ModAttachments.CYBERWARE)) return;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        int stacks = 0;
        for (int i = 0; i < CyberwareSlot.LUNGS.size; i++) {
            if (data.isInstalled(ModItems.LUNGSUPGRADES_HYPEROXYGENATION.get(), CyberwareSlot.LUNGS, i)) {
                stacks++;
            }
        }

        if (stacks <= 0) return;
        if (stacks > 3) stacks = 3;

        int cost = ENERGY_PER_TICK_PER_STACK * stacks;
        if (!data.tryConsumeEnergy(cost)) {
            return;
        }

        if (stacks >= 1) CyberwareAttributeHelper.applyModifier(player, "hyperoxygenation_speed_1");
        if (stacks >= 2) CyberwareAttributeHelper.applyModifier(player, "hyperoxygenation_speed_2");
        if (stacks >= 3) CyberwareAttributeHelper.applyModifier(player, "hyperoxygenation_speed_3");
    }

    @Override
    public void onTick(Player player) {
    }
}
