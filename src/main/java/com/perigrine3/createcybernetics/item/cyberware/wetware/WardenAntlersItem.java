package com.perigrine3.createcybernetics.item.cyberware.wetware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class WardenAntlersItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public WardenAntlersItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.EYES);
    }

    @Override
    public boolean replacesOrgan() {
        return true;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of(CyberwareSlot.EYES);
    }

    @Override
    public Set<TagKey<Item>> incompatibleCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModTags.Items.EYE_ITEMS);
    }

    @Override
    public void onInstalled(LivingEntity entity) {

    }

    @Override
    public void onRemoved(LivingEntity entity) {

    }

    @Override
    public void onTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot, int index) {
        if (entity.hasEffect(MobEffects.BLINDNESS)) {
            entity.removeEffect(MobEffects.BLINDNESS);
        }
        if (entity.hasEffect(MobEffects.DARKNESS)) {
            entity.removeEffect(MobEffects.DARKNESS);
        }
    }

    @Override
    public void onTick(LivingEntity entity) {

    }
}