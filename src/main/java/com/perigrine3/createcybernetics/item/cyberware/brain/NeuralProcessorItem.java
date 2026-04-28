package com.perigrine3.createcybernetics.item.cyberware.brain;

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

public class NeuralProcessorItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public NeuralProcessorItem(Properties props, int humanityCost) {
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
    public Set<Item> requiresCyberware(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModItems.BODYPART_BRAIN.get());
    }

    @Override
    public void onInstalled(LivingEntity entity) {
        CyberwareAttributeHelper.applyModifier(entity, "neuralprocessor_learn");
        CyberwareAttributeHelper.applyModifier(entity, "neuralprocessor_insomnia");
        CyberwareAttributeHelper.applyModifier(entity, "neuralprocessor_speed");
    }

    @Override
    public void onRemoved(LivingEntity entity) {
        CyberwareAttributeHelper.removeModifier(entity, "neuralprocessor_learn");
        CyberwareAttributeHelper.removeModifier(entity, "neuralprocessor_insomnia");
        CyberwareAttributeHelper.removeModifier(entity, "neuralprocessor_speed");
    }

    @Override
    public void onTick(LivingEntity entity) {

    }
}