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

public class AnkleBracerItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public AnkleBracerItem(Properties props, int humanityCost) {
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
        return switch (slot) {
            case RLEG -> Set.of(ModTags.Items.RIGHTLEG_REPLACEMENTS);
            case LLEG -> Set.of(ModTags.Items.LEFTLEG_REPLACEMENTS);
            default -> Set.of();
        };
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.RLEG, CyberwareSlot.LLEG);
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
    public void onInstalled(Player player) {
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);

        if (data.hasSpecificItem(ModItems.LEGUPGRADES_ANKLEBRACERS.get(), CyberwareSlot.RLEG) && data.hasSpecificItem(ModItems.LEGUPGRADES_ANKLEBRACERS.get(), CyberwareSlot.LLEG)) {
            CyberwareAttributeHelper.applyModifier(player, "fall_bracer_fall_1");
            CyberwareAttributeHelper.applyModifier(player, "fall_bracer_fall_2");
        } else if (data.hasSpecificItem(ModItems.LEGUPGRADES_ANKLEBRACERS.get(), CyberwareSlot.RLEG) || data.hasSpecificItem(ModItems.LEGUPGRADES_ANKLEBRACERS.get(), CyberwareSlot.LLEG)) {
            CyberwareAttributeHelper.applyModifier(player, "fall_bracer_fall_1");

        }
    }

    @Override
    public void onRemoved(Player player) {
        CyberwareAttributeHelper.removeModifier(player, "fall_bracer_fall_1");
        CyberwareAttributeHelper.removeModifier(player, "fall_bracer_fall_2");
    }

    @Override
    public void onTick(Player player) {
        ICyberwareItem.super.onTick(player);
    }
}