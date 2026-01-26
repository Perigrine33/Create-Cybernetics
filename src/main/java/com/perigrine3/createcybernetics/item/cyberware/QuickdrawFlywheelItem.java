package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class QuickdrawFlywheelItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final int ENERGY_PER_TICK_WHILE_USING = 2;

    public QuickdrawFlywheelItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost).withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.translatable("tooltip.createcybernetics.armupgrades_flywheel.energy").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<Item> requiresCyberware(ItemStack installedStack, CyberwareSlot slot) {
        return switch (slot) {
            case RARM -> Set.of(ModItems.BASECYBERWARE_RIGHTARM.get());
            case LARM -> Set.of(ModItems.BASECYBERWARE_LEFTARM.get());
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
    public boolean requiresEnergyToFunction(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return true;
    }

    @Override
    public int getEnergyUsedPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        if (player == null) return 0;
        if (player.level().isClientSide) return 0;
        if (!player.isAlive()) return 0;

        ItemStack using = player.getUseItem();
        if (using == null || using.isEmpty()) return 0;

        Item u = using.getItem();
        if (!(u instanceof BowItem) && !(u instanceof CrossbowItem)) return 0;
        if (u instanceof CrossbowItem && CrossbowItem.isCharged(using)) return 0;

        if (!shouldChargeOnThisSlot(player, slot)) return 0;

        return ENERGY_PER_TICK_WHILE_USING;
    }

    private boolean shouldChargeOnThisSlot(Player player, CyberwareSlot slot) {
        if (!player.hasData(ModAttachments.CYBERWARE)) return false;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        boolean hasRight = false;
        for (int i = 0; i < CyberwareSlot.RARM.size; i++) {
            InstalledCyberware cw = data.get(CyberwareSlot.RARM, i);
            if (cw == null) continue;
            ItemStack st = cw.getItem();
            if (st == null || st.isEmpty()) continue;
            if (st.getItem() == this) {
                hasRight = true;
                break;
            }
        }

        if (hasRight) return slot == CyberwareSlot.RARM;
        return slot == CyberwareSlot.LARM;
    }

    @Override
    public void onInstalled(Player player) { }

    @Override
    public void onRemoved(Player player) { }

    @Override
    public void onTick(Player player) {
        ICyberwareItem.super.onTick(player);
    }
}
