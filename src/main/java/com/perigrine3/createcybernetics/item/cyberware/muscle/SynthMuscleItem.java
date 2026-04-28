package com.perigrine3.createcybernetics.item.cyberware.muscle;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.EntityCyberwareData;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.ModMobAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
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
    public int getEnergyUsedPerTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return 3;
    }

    @Override
    public boolean requiresEnergyToFunction(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
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
    public void onInstalled(LivingEntity entity) {
        if (entity.level().isClientSide) return;
        setBaseModifiers(entity, false);
        setSprintModifier(entity, false);
    }

    @Override
    public void onRemoved(LivingEntity entity) {
        setBaseModifiers(entity, false);
        setSprintModifier(entity, false);
    }

    @Override
    public void onTick(LivingEntity entity) { }

    @Override
    public void onTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot, int index) {
        if (entity.level().isClientSide) return;

        InstalledCyberware cw;
        if (entity instanceof Player player) {
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return;
            cw = data.get(slot, index);
        } else {
            EntityCyberwareData data = entity.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
            if (data == null) return;
            cw = data.get(slot, index);
        }

        boolean powered = cw != null && cw.isPowered();

        setBaseModifiers(entity, powered);
        setSprintModifier(entity, powered && entity.isSprinting());
    }

    private static void setBaseModifiers(LivingEntity entity, boolean on) {
        if (on) {
            CyberwareAttributeHelper.applyModifier(entity, "synthmuscle_strength");
            CyberwareAttributeHelper.applyModifier(entity, "synthmuscle_size");
            CyberwareAttributeHelper.applyModifier(entity, "synthmuscle_knockback_resist");
            CyberwareAttributeHelper.applyModifier(entity, "synthmuscle_knockback");
            CyberwareAttributeHelper.applyModifier(entity, "synthmuscle_jump");
        } else {
            CyberwareAttributeHelper.removeModifier(entity, "synthmuscle_strength");
            CyberwareAttributeHelper.removeModifier(entity, "synthmuscle_size");
            CyberwareAttributeHelper.removeModifier(entity, "synthmuscle_knockback_resist");
            CyberwareAttributeHelper.removeModifier(entity, "synthmuscle_knockback");
            CyberwareAttributeHelper.removeModifier(entity, "synthmuscle_jump");
        }
    }

    private static void setSprintModifier(LivingEntity entity, boolean on) {
        if (on) {
            CyberwareAttributeHelper.applyModifier(entity, "synthmuscle_speed");
        } else {
            CyberwareAttributeHelper.removeModifier(entity, "synthmuscle_speed");
        }
    }
}