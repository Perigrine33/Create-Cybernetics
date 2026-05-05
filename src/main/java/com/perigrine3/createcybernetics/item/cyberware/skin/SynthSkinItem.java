package com.perigrine3.createcybernetics.item.cyberware.skin;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.tattoo.TattooData;
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

public class SynthSkinItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public SynthSkinItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost).withStyle(ChatFormatting.GOLD));
        }

        if (TattooData.has(stack)) {
            String displayName = TattooData.getDisplayName(stack);
            if (displayName.isBlank()) {
                displayName = "Unknown Tattoo";
            }

            tooltip.add(Component.translatable("tooltip.createcybernetics.tattoo", displayName).withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(Component.literal("Layer: " + TattooData.getLayer(stack).displayName()).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModTags.Items.SKIN_ITEMS);
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.SKIN);
    }

    @Override
    public boolean replacesOrgan() {
        return true;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of(CyberwareSlot.SKIN);
    }

    @Override
    public int maxStacksPerSlotType(ItemStack stack, CyberwareSlot slotType) {
        return 1;
    }

    @Override
    public void onInstalled(LivingEntity entity) {
    }

    @Override
    public void onRemoved(LivingEntity entity) {
    }

    @Override
    public void onTick(LivingEntity entity) {
    }
}