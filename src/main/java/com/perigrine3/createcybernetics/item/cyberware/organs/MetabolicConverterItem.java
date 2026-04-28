package com.perigrine3.createcybernetics.item.cyberware.organs;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class MetabolicConverterItem extends Item implements ICyberwareItem {

    private final int humanityCost;

    private static final int ENERGY_PER_TICK = 25;
    private static final float EXTRA_EXHAUSTION_PACKET = 0.25F;

    public MetabolicConverterItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost).withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("tooltip.createcybernetics.organsupgrades_metabolic.energy").withStyle(ChatFormatting.DARK_GREEN));
        }
    }

    @Override
    public int getEnergyGeneratedPerTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        if (entity instanceof Player player) {
            return player.getFoodData().getFoodLevel() > 0 ? ENERGY_PER_TICK : 0;
        }
        return 0;
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.ORGANS);
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
    public void onInstalled(LivingEntity entity) {}

    @Override
    public void onRemoved(LivingEntity entity) {}

    @Override
    public void onTick(LivingEntity entity) {
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide) return;
        if (!player.hasData(ModAttachments.CYBERWARE)) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;
        if (!data.hasSpecificItem(this, CyberwareSlot.ORGANS)) return;
        if (player.getFoodData().getFoodLevel() <= 0) return;

        if (player.getRandom().nextFloat() < 0.05F) {
            player.causeFoodExhaustion(EXTRA_EXHAUSTION_PACKET);
        }
    }
}