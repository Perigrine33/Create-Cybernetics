package com.perigrine3.createcybernetics.item.cyberware.wetware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class RavagerTendonsItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public RavagerTendonsItem(Properties props, int humanityCost) {
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
        return true;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of(CyberwareSlot.MUSCLE);
    }

    @Override
    public Set<Item> incompatibleCyberware(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get());
    }

    @Override
    public void onInstalled(LivingEntity entity) {
        CyberwareAttributeHelper.applyModifier(entity, "ravager_tendons_size");
        CyberwareAttributeHelper.applyModifier(entity, "ravager_tendons_strength");
        CyberwareAttributeHelper.applyModifier(entity, "ravager_tendons_knockback_resist");
        CyberwareAttributeHelper.applyModifier(entity, "ravager_tendons_knockback");
    }

    @Override
    public void onRemoved(LivingEntity entity) {
        CyberwareAttributeHelper.removeModifier(entity, "ravager_tendons_size");
        CyberwareAttributeHelper.removeModifier(entity, "ravager_tendons_strength");
        CyberwareAttributeHelper.removeModifier(entity, "ravager_tendons_knockback_resist");
        CyberwareAttributeHelper.removeModifier(entity, "ravager_tendons_knockback");
    }

    @Override
    public void onTick(LivingEntity entity) {
        ICyberwareItem.super.onTick(entity);
    }
}