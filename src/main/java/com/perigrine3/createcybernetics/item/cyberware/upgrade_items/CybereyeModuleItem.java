package com.perigrine3.createcybernetics.item.cyberware.upgrade_items;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class CybereyeModuleItem extends Item implements ICyberwareItem {
    private final int humanityCost;
    private boolean nightVisionModule;
    private boolean underwaterVisionModule;

    public CybereyeModuleItem(Properties props, int humanityCost) {
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
        return Set.of(CyberwareSlot.EYES);
    }

    @Override
    public boolean replacesOrgan() {
        return false;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of(CyberwareSlot.EYES);
    }

    @Override
    public void onInstalled(Player player) {

    }

    @Override
    public void onRemoved(Player player) {
        // remove strength modifier
    }

    @Override
    public void onTick(Player player) {
        if (!player.level().isClientSide) return;
        if (ModItems.EYEUPGRADES_NAVIGATIONCHIP != null) {
            if (net.neoforged.fml.ModList.get().isLoaded("xaerominimap")) {
                // interact with minimap indirectly (capabilities, packets, etc.)
            }
        }

        ICyberwareItem.super.onTick(player);
    }
}