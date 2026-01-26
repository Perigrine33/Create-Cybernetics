package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
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
    public int getEnergyGeneratedPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        int pulseTicks = computePulseTicks(player);
        return (player.tickCount % pulseTicks) == 0 ? ENERGY_PER_PULSE : 0;
    }

    private static int computePulseTicks(Player player) {
        boolean attacked = player.hurtTime > 0;
        boolean meaningfulFalling =
                !player.onGround() && !player.isSwimming() && !player.isFallFlying() && !player.getAbilities().flying
                        && player.getDeltaMovement().y < 0.0D && player.fallDistance >= FEAR_FALL_DISTANCE_THRESHOLD;

        if (attacked || meaningfulFalling) {
            return PULSE_TICKS_FEAR;
        }

        if (player.isSprinting() || player.isSwimming()) {
            return PULSE_TICKS_EXERTION;
        }

        double horizontalSpeedSqr = player.getDeltaMovement().horizontalDistanceSqr();
        if (horizontalSpeedSqr > WALKING_SPEED_SQR_EPS) {
            return PULSE_TICKS_WALK;
        }

        return PULSE_TICKS_STILL;
    }

    @Override
    public int getEnergyCapacity(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return 0;
    }

    @Override
    public boolean acceptsGeneratedEnergy(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return false;
    }

    @Override
    public boolean acceptsChargerEnergy(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return false;
    }

    @Override
    public int getChargerEnergyReceivePerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
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
    public void onInstalled(Player player) {}

    @Override
    public void onRemoved(Player player) {}

    @Override
    public void onTick(Player player) {}
}
