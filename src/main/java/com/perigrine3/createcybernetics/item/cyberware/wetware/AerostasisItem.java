package com.perigrine3.createcybernetics.item.cyberware.wetware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.effect.AerostasisGyrobladderEffect;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class AerostasisItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public AerostasisItem(Properties props, int humanityCost) {
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
        return Set.of(CyberwareSlot.LUNGS);
    }

    @Override
    public boolean replacesOrgan() {
        return true;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of(CyberwareSlot.LUNGS);
    }

    @Override
    public Set<Item> incompatibleCyberware(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModItems.WETWARE_SCULKLUNGS.get(), ModItems.BODYPART_LUNGS.get());
    }

    @Override
    public void onInstalled(LivingEntity entity) {
    }

    @Override
    public void onRemoved(LivingEntity entity) {
        if (entity.level().isClientSide) return;

        entity.removeEffect(ModEffects.AEROSTASIS_GYROBLADDER_EFFECT);

        if (entity instanceof Player player) {
            AerostasisGyrobladderEffect.disableFlight(player);
        }
    }

    @Override
    public void onTick(LivingEntity entity) {
        if (entity.level().isClientSide) return;
        entity.addEffect(new MobEffectInstance(ModEffects.AEROSTASIS_GYROBLADDER_EFFECT, 20, 0, false, false, false));
    }
}