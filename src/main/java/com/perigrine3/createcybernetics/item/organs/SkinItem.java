package com.perigrine3.createcybernetics.item.organs;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.tattoo.TattooData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class SkinItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public SkinItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
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
    public void onInstalled(Player player) {
    }

    @Override
    public void onRemoved(Player player) {
    }

    @Override
    public void onTick(Player player) {
    }

    @Override
    public boolean dropsOnDeath(ItemStack installedStack, CyberwareSlot slot) {
        return RandomSource.create().nextFloat() < 0.1F;
    }
}