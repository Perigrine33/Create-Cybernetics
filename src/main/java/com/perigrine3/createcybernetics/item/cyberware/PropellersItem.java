package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class PropellersItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final int ENERGY_PER_TICK_WHEN_SWIMMING = 5;

    public PropellersItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost).withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.literal("Costs 5 Energy").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<Item> requiresCyberware(ItemStack installedStack, CyberwareSlot slot) {
        return switch (slot) {
            case RLEG -> Set.of(ModItems.BASECYBERWARE_RIGHTLEG.get());
            case LLEG -> Set.of(ModItems.BASECYBERWARE_LEFTLEG.get());
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
    public int getEnergyUsedPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return (player != null && !player.level().isClientSide && player.isSwimming())
                ? ENERGY_PER_TICK_WHEN_SWIMMING
                : 0;
    }

    @Override
    public boolean requiresEnergyToFunction(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return true;
    }

    @Override
    public void onInstalled(Player player) {
    }

    @Override
    public void onRemoved(Player player) {
        CyberwareAttributeHelper.removeModifier(player, "propeller_swim_1");
        CyberwareAttributeHelper.removeModifier(player, "propeller_swim_2");
    }

    @Override
    public void onTick(Player player) { }

    @Override
    public void onTick(Player player, ItemStack installedStack, CyberwareSlot slot, int index) {
        if (player.level().isClientSide) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        if (!player.isSwimming()) {
            CyberwareAttributeHelper.removeModifier(player, "propeller_swim_1");
            CyberwareAttributeHelper.removeModifier(player, "propeller_swim_2");
            return;
        }

        InstalledCyberware cw = data.get(slot, index);
        if (cw == null || !cw.isPowered()) {
            CyberwareAttributeHelper.removeModifier(player, "propeller_swim_1");
            CyberwareAttributeHelper.removeModifier(player, "propeller_swim_2");
            return;
        }

        int stacks = 0;

        for (int i = 0; i < CyberwareSlot.RLEG.size; i++) {
            if (data.isInstalled(ModItems.LEGUPGRADES_PROPELLERS.get(), CyberwareSlot.RLEG, i)) {
                stacks++;
            }
        }

        for (int i = 0; i < CyberwareSlot.LLEG.size; i++) {
            if (data.isInstalled(ModItems.LEGUPGRADES_PROPELLERS.get(), CyberwareSlot.LLEG, i)) {
                stacks++;
            }
        }

        if (stacks > 2) stacks = 2;

        if (stacks >= 1) CyberwareAttributeHelper.applyModifier(player, "propeller_swim_1");
        else CyberwareAttributeHelper.removeModifier(player, "propeller_swim_1");

        if (stacks >= 2) CyberwareAttributeHelper.applyModifier(player, "propeller_swim_2");
        else CyberwareAttributeHelper.removeModifier(player, "propeller_swim_2");
    }
}
