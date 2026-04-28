package com.perigrine3.createcybernetics.item.cyberware.organs;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class DenseBatteryItem extends Item implements ICyberwareItem {

    private final int humanityCost;

    private static final int CAPACITY = 1200000;
    private static final int CHARGE_AMOUNT_PER_TICK = 1000;

    public DenseBatteryItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.translatable("tooltip.createcybernetics.organsupgrade_densebattery.energy").withStyle(ChatFormatting.DARK_PURPLE));
        }
    }

    @Override
    public int getEnergyCapacity(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return CAPACITY;
    }

    @Override
    public boolean acceptsGeneratedEnergy(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return false;
    }

    @Override
    public boolean acceptsChargerEnergy(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return true;
    }

    @Override
    public int getChargerEnergyReceivePerTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return CHARGE_AMOUNT_PER_TICK;
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public int maxStacksPerSlotType(ItemStack stack, CyberwareSlot slotType) {
        return 3;
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.ORGANS);
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
    public Set<Item> incompatibleCyberware(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModItems.ORGANSUPGRADES_BATTERY.get(), ModItems.BONEUPGRADES_BONEBATTERY.get());
    }

    @Override
    public void onInstalled(LivingEntity entity) {}

    @Override
    public void onRemoved(LivingEntity entity) {}

    @Override
    public void onTick(LivingEntity entity) {}
}