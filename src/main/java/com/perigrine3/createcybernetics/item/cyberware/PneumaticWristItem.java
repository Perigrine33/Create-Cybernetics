package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class PneumaticWristItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public PneumaticWristItem(Properties props, int humanityCost) {
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
    public Set<Item> requiresCyberware(ItemStack installedStack, CyberwareSlot slot) {
        return switch (slot) {
            case RLEG -> Set.of(ModItems.BASECYBERWARE_RIGHTARM.get());
            case LLEG -> Set.of(ModItems.BASECYBERWARE_LEFTARM.get());
            default -> Set.of();
        };
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.RARM, CyberwareSlot.LARM);
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
        CyberwareAttributeHelper.applyModifier(player, "pneumatic_wrist_block");
        CyberwareAttributeHelper.applyModifier(player, "pneumatic_wrist_entity");
        CyberwareAttributeHelper.applyModifier(player, "pneumatic_wrist_knockback");
    }

    @Override
    public void onRemoved(Player player) {
        CyberwareAttributeHelper.removeModifier(player, "pneumatic_wrist_block");
        CyberwareAttributeHelper.removeModifier(player, "pneumatic_wrist_entity");
        CyberwareAttributeHelper.removeModifier(player, "pneumatic_wrist_knockback");
    }

    @Override
    public void onTick(Player player) {
        ICyberwareItem.super.onTick(player);

    }
}