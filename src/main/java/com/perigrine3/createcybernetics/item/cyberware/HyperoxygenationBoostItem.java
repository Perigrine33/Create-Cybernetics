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

    public HyperoxygenationBoostItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost).withStyle(ChatFormatting.GOLD));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
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

    public int maxStacksPerSlotType(ItemStack stack, CyberwareSlot slotType) {
        return 3;
    }

    @Override
    public void onInstalled(Player player) {

    }

    @Override
    public void onRemoved(Player player) {

    }

    @Override
    public void onTick(Player player) {
        if (player.level().isClientSide) return;

        CyberwareAttributeHelper.removeModifier(player, "hyperoxygenation_speed_1");
        CyberwareAttributeHelper.removeModifier(player, "hyperoxygenation_speed_2");
        CyberwareAttributeHelper.removeModifier(player, "hyperoxygenation_speed_3");

        if (!player.isSprinting()) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);

        int stacks = 0;

        for (int i = 0; i < CyberwareSlot.LUNGS.size; i++) {
            if (data.isInstalled(ModItems.LUNGSUPGRADES_HYPEROXYGENATION.get(), CyberwareSlot.LUNGS, i)) {
                stacks++;
            }
        }

        if (stacks > 3) stacks = 3;

        if (stacks >= 1) CyberwareAttributeHelper.applyModifier(player, "hyperoxygenation_speed_1");
        if (stacks >= 2) CyberwareAttributeHelper.applyModifier(player, "hyperoxygenation_speed_2");
        if (stacks >= 3) CyberwareAttributeHelper.applyModifier(player, "hyperoxygenation_speed_3");
    }

}