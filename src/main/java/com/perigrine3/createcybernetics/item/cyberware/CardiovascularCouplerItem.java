package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.item.ModItems;
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

public class CardiovascularCouplerItem extends Item implements ICyberwareItem {

    private final int humanityCost;

    private static final int ENERGY_PER_PULSE = 6;

    private static final int PULSE_TICKS_STILL = 20;
    private static final int PULSE_TICKS_WALK = 16;
    private static final int PULSE_TICKS_EXERTION = 12;
    private static final int PULSE_TICKS_FEAR = 8;

    private static final float FEAR_FALL_DISTANCE_THRESHOLD = 8.0F;
    private static final double WALKING_SPEED_SQR_EPS = 1.0E-4D;

    public CardiovascularCouplerItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.translatable("tooltip.createcybernetics.heartupgrades_coupler.energy").withStyle(ChatFormatting.DARK_GREEN));
        }
    }

    @Override
    public int getEnergyGeneratedPerTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        int pulseTicks = computePulseTicks(entity);
        return (entity.tickCount % pulseTicks) == 0 ? ENERGY_PER_PULSE : 0;
    }

    private static int computePulseTicks(LivingEntity entity) {
        boolean attacked = entity.hurtTime > 0;

        boolean playerFlying = entity instanceof Player player && player.getAbilities().flying;

        boolean meaningfulFalling =
                !entity.onGround()
                        && !entity.isSwimming()
                        && !entity.isFallFlying()
                        && !playerFlying
                        && entity.getDeltaMovement().y < 0.0D
                        && entity.fallDistance >= FEAR_FALL_DISTANCE_THRESHOLD;

        if (attacked || meaningfulFalling) {
            return PULSE_TICKS_FEAR;
        }

        if (entity.isSprinting() || entity.isSwimming()) {
            return PULSE_TICKS_EXERTION;
        }

        double horizontalSpeedSqr = entity.getDeltaMovement().horizontalDistanceSqr();
        if (horizontalSpeedSqr > WALKING_SPEED_SQR_EPS) {
            return PULSE_TICKS_WALK;
        }

        return PULSE_TICKS_STILL;
    }

    @Override
    public int getEnergyCapacity(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return 0;
    }

    @Override
    public boolean acceptsGeneratedEnergy(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return false;
    }

    @Override
    public boolean acceptsChargerEnergy(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return false;
    }

    @Override
    public int getChargerEnergyReceivePerTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return 0;
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<Item> requiresCyberware(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModItems.BODYPART_HEART.get());
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.HEART);
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
    public int maxStacksPerSlotType(ItemStack stack, CyberwareSlot slotType) {
        return 1;
    }

    @Override
    public void onInstalled(LivingEntity entity) {}

    @Override
    public void onRemoved(LivingEntity entity) {}

    @Override
    public void onTick(LivingEntity entity) {}
}