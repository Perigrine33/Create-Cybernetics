package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.compat.coldsweat.ColdSweatCompat;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.effects.AllOf;

import java.util.List;
import java.util.Set;

public class IsothermalSkinItem extends Item implements ICyberwareItem {
    private final int humanityCost;
    private boolean isInstalled = false;

    public IsothermalSkinItem(Properties props, int humanityCost) {
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
        CyberwareAttributeHelper.applyModifier(player, "isothermal_burning");
    }

    @Override
    public void onRemoved(Player player) {
        CyberwareAttributeHelper.removeModifier(player, "isothermal_burning");
    }

    @Override
    public void onTick(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, 0, false, false, false));
    }

    @Override
    public void onTick(Player player, ItemStack installedStack, CyberwareSlot slot, int index) {
        if (player.level().isClientSide) return;
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, 0, false, false, false));

        ColdSweatCompat.applyHeatResistance(player, 1.0);
        ColdSweatCompat.applyHeatDampening(player, 0.0);

    }
}