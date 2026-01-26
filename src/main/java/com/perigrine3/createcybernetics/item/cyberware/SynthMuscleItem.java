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

public class SynthMuscleItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public SynthMuscleItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost).withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.translatable("tooltip.createcybernetics.muscleupgrades_synthmuscle.energy").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public int getEnergyUsedPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return 3;
    }

    @Override
    public boolean requiresEnergyToFunction(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return true;
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
        return true;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of(CyberwareSlot.MUSCLE);
    }

    @Override
    public Set<Item> incompatibleCyberware(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModItems.WETWARE_RAVAGERTENDONS.get());
    }

    @Override
    public void onInstalled(Player player) {
        if (player.level().isClientSide) return;
        setBaseModifiers(player, false);
        setSprintModifier(player, false);
    }

    @Override
    public void onRemoved(Player player) {
        setBaseModifiers(player, false);
        setSprintModifier(player, false);
    }

    @Override
    public void onTick(Player player) { }

    @Override
    public void onTick(Player player, ItemStack installedStack, CyberwareSlot slot, int index) {
        if (player.level().isClientSide) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        InstalledCyberware cw = data.get(slot, index);
        boolean powered = cw != null && cw.isPowered();

        setBaseModifiers(player, powered);
        setSprintModifier(player, powered && player.isSprinting());
    }

    private static void setBaseModifiers(Player player, boolean on) {
        if (on) {
            CyberwareAttributeHelper.applyModifier(player, "synthmuscle_strength");
            CyberwareAttributeHelper.applyModifier(player, "synthmuscle_size");
            CyberwareAttributeHelper.applyModifier(player, "synthmuscle_knockback_resist");
            CyberwareAttributeHelper.applyModifier(player, "synthmuscle_knockback");
            CyberwareAttributeHelper.applyModifier(player, "synthmuscle_jump");
        } else {
            CyberwareAttributeHelper.removeModifier(player, "synthmuscle_strength");
            CyberwareAttributeHelper.removeModifier(player, "synthmuscle_size");
            CyberwareAttributeHelper.removeModifier(player, "synthmuscle_knockback_resist");
            CyberwareAttributeHelper.removeModifier(player, "synthmuscle_knockback");
            CyberwareAttributeHelper.removeModifier(player, "synthmuscle_jump");
        }
    }

    private static void setSprintModifier(Player player, boolean on) {
        if (on) {
            CyberwareAttributeHelper.applyModifier(player, "synthmuscle_speed");
        } else {
            CyberwareAttributeHelper.removeModifier(player, "synthmuscle_speed");
        }
    }
}
