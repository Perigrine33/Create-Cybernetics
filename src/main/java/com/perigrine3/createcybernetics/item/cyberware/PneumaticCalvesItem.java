package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Set;

public class PneumaticCalvesItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public PneumaticCalvesItem(Properties props, int humanityCost) {
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
    public Set<Item> requiresCyberware(ItemStack installedStack, CyberwareSlot slot) {
        return switch (slot) {
            case RLEG -> Set.of(ModItems.BASECYBERWARE_RIGHTLEG.get());
            case LLEG -> Set.of(ModItems.BASECYBERWARE_LEFTLEG.get());
            default -> Set.of();
        };
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.RLEG, CyberwareSlot.LLEG);
    }

    @Override
    public boolean replacesOrgan() {
        return false;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of();
    }

    @Override
    public void onInstalled(Player player) {

    }

    @Override
    public void onRemoved(Player player) {
        if (player.level().isClientSide) return;
        CyberwareAttributeHelper.removeModifier(player, "calves_sprint");
    }

    @Override
    public void onTick(Player player) {
        if (player.level().isClientSide) return;
        player.addEffect(new MobEffectInstance(ModEffects.PNEUMATIC_CALVES_EFFECT, 100, 0, false, false, false));

        if (player.isSprinting()) {
            CyberwareAttributeHelper.applyModifier(player, "calves_sprint");
        } else {
            CyberwareAttributeHelper.removeModifier(player, "calves_sprint");
        }
    }
}