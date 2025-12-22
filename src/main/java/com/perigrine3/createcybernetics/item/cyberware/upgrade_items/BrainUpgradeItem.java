package com.perigrine3.createcybernetics.item.cyberware.upgrade_items;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class BrainUpgradeItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public BrainUpgradeItem(Properties props, int humanityCost) {
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
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.BRAIN);
    }

    @Override
    public boolean replacesOrgan() {
        return false;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of(CyberwareSlot.BRAIN);
    }

    @Override
    public void onInstalled(Player player) {
        // grant strength modifier
    }

    @Override
    public void onRemoved(Player player) {
        // remove strength modifier
    }

    @Override
    public void onTick(Player player) {
        if (!player.level().isClientSide) return;
        if (ModItems.BRAINUPGRADES_CONSCIOUSNESSTRANSMITTER != null && ModItems.BRAINUPGRADES_CORTICALSTACK != null) {
            if (net.neoforged.fml.ModList.get().isLoaded("createenchantmentindustry")) {
                // functionality
            }
        }

        ICyberwareItem.super.onTick(player);
    }
}