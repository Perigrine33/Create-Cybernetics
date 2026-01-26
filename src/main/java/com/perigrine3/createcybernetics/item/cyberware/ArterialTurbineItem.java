package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
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

public class ArterialTurbineItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    // Tuning constants
    private static final int ENERGY_STILL = 3;
    private static final int ENERGY_WALK = 10;
    private static final int ENERGY_EXERTION = 25;
    private static final int ENERGY_FEAR = 50;
    private static final float FEAR_FALL_DISTANCE_THRESHOLD = 8.0F;
    private static final double WALKING_SPEED_SQR_EPS = 1.0E-4D;

    public ArterialTurbineItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.translatable("tooltip.createcybernetics.skinupgrades_arterialturbine.energy").withStyle(ChatFormatting.DARK_GREEN));
        }
    }

    @Override
    public boolean shouldGenerateEnergyThisTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return true;
    }

    @Override
    public int getEnergyGeneratedPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return computeEnergy(player);
    }

    private static int computeEnergy(Player player) {
        boolean attacked = player.hurtTime > 0;
        boolean meaningfulFalling =
                !player.onGround() && !player.isSwimming() && !player.isFallFlying() && !player.getAbilities().flying
                        && player.getDeltaMovement().y < 0.0D && player.fallDistance >= FEAR_FALL_DISTANCE_THRESHOLD;
        if (attacked || meaningfulFalling) {
            return ENERGY_FEAR;
        }
        if (player.isSprinting() || player.isSwimming()) {
            return ENERGY_EXERTION;
        }
        double horizontalSpeedSqr = player.getDeltaMovement().horizontalDistanceSqr();
        if (horizontalSpeedSqr > WALKING_SPEED_SQR_EPS) {
            return ENERGY_WALK;
        }

        return ENERGY_STILL;
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
        return false;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of();
    }

    @Override
    public int maxStacksPerSlotType(ItemStack stack, CyberwareSlot slotType) {
        return 3;
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
}
