package com.perigrine3.createcybernetics.item.cyberware.bone;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class VoltaicCapacitorFrameItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public VoltaicCapacitorFrameItem(Properties props, int humanityCost) {
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
    public int getEnergyGeneratedPerTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        if (entity.hasEffect(ModEffects.EMP)) {
            return 250;
        } else {
            return 0;
        }
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.BONE);
    }

    @Override
    public boolean replacesOrgan() {
        return true;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of(CyberwareSlot.BONE);
    }

    @Override
    public Set<TagKey<Item>> incompatibleCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModTags.Items.BONE_ITEMS);
    }

    @Override
    public void onInstalled(LivingEntity entity) {
        CyberwareAttributeHelper.applyModifier(entity, "linear_frame_health");
        CyberwareAttributeHelper.applyModifier(entity, "linear_frame_knockback_resist");
        CyberwareAttributeHelper.applyModifier(entity, "linear_frame_blockbreak");
        CyberwareAttributeHelper.applyModifier(entity, "linear_frame_speed");
    }

    @Override
    public void onRemoved(LivingEntity entity) {
        CyberwareAttributeHelper.removeModifier(entity, "linear_frame_health");
        CyberwareAttributeHelper.removeModifier(entity, "linear_frame_knockback_resist");
        CyberwareAttributeHelper.removeModifier(entity, "linear_frame_blockbreak");
        CyberwareAttributeHelper.removeModifier(entity, "linear_frame_speed");
    }

    @Override
    public void onTick(LivingEntity entity) {

    }
}