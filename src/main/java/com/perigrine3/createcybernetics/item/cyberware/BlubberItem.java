package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.compat.coldsweat.ColdSweatCompat;
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

public class BlubberItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public BlubberItem(Properties props, int humanityCost) {
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
        return Set.of(CyberwareSlot.MUSCLE);
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
    public void onUnpoweredTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        if (player.level().isClientSide) return;
        ColdSweatCompat.clearCold(player);
    }

    @Override
    public void onRemoved(Player player) {
        if (player.level().isClientSide) return;
        ColdSweatCompat.clearCold(player);
    }

    @Override
    public void onTick(Player player) {
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (player.level().isClientSide) return;
        if (!data.hasSpecificItem(ModItems.WETWARE_POLARBEARFUR.get(), CyberwareSlot.MUSCLE)) {
            ColdSweatCompat.applyColdResistance(player, 0.50);
            ColdSweatCompat.applyColdDampening(player, 0.3);
        } else {
            ColdSweatCompat.applyColdResistance(player, 1.0);
            ColdSweatCompat.applyColdDampening(player, 0.0);
        }
    }
}
