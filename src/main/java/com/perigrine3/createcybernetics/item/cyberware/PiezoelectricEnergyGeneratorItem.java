package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
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

public class PiezoelectricEnergyGeneratorItem extends Item implements ICyberwareItem {

    private final int humanityCost;

    private static final int ENERGY_PER_PULSE = 2;

    private static final int PULSE_TICKS_WALK = 12;
    private static final int PULSE_TICKS_SPRINT = 8;
    private static final int PULSE_TICKS_SWIM = 8;

    private static final double WALKING_SPEED_SQR_EPS = 2.0E-4D;

    private static final int MOVE_DAMAGE_CHECK_TICKS = 100;
    private static final float MOVE_DAMAGE_CHANCE = 0.03F;

    private static final float LAND_DAMAGE_CHANCE = 0.10F;

    private static final int MOVE_DAMAGE = 1;
    private static final int LAND_DAMAGE = 2;

    private static final double MIN_FALL_BLOCKS_FOR_LAND_DAMAGE = 5.0D;

    private static final String NBT_IN_AIR = "cc_piezo_in_air";
    private static final String NBT_PEAK_Y = "cc_piezo_peak_y";

    public PiezoelectricEnergyGeneratorItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.literal("Adds 2 Energy While Moving").withStyle(ChatFormatting.DARK_GREEN));
        }
    }

    @Override
    public int getEnergyGeneratedPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        int pulseTicks = computePulseTicks(player);
        return pulseTicks > 0 && (player.tickCount % pulseTicks) == 0 ? ENERGY_PER_PULSE : 0;
    }

    private static int computePulseTicks(Player player) {
        if (player.isSprinting()) return PULSE_TICKS_SPRINT;
        if (player.isSwimming()) return PULSE_TICKS_SWIM;

        if (!player.onGround()) return 0;

        double horizontalSpeedSqr = player.getDeltaMovement().horizontalDistanceSqr();
        if (horizontalSpeedSqr > WALKING_SPEED_SQR_EPS) return PULSE_TICKS_WALK;

        return 0;
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
    public Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModTags.Items.BONE_ITEMS);
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.BONE);
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
    public void onInstalled(Player player) {}

    @Override
    public void onRemoved(Player player) {}

    @Override
    public void onTick(Player player) {
        if (player.level().isClientSide) return;
        if (!player.hasData(ModAttachments.CYBERWARE)) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;
        if (!data.hasSpecificItem(this, CyberwareSlot.BONE)) return;

        boolean movingNow = player.isSwimming()
                || (player.onGround() && player.getDeltaMovement().horizontalDistanceSqr() > WALKING_SPEED_SQR_EPS);

        if (movingNow && (player.tickCount % MOVE_DAMAGE_CHECK_TICKS) == 0) {
            if (player.getRandom().nextFloat() < MOVE_DAMAGE_CHANCE) {
                player.hurt(player.damageSources().generic(), MOVE_DAMAGE);
            }
        }

        boolean swimming = player.isSwimming();
        if (swimming || player.getAbilities().flying || player.isFallFlying()) {
            player.getPersistentData().remove(NBT_IN_AIR);
            player.getPersistentData().remove(NBT_PEAK_Y);
            return;
        }

        boolean onGround = player.onGround();
        boolean inAir = player.getPersistentData().getBoolean(NBT_IN_AIR);

        if (!onGround) {
            if (!inAir) {
                player.getPersistentData().putBoolean(NBT_IN_AIR, true);
                player.getPersistentData().putDouble(NBT_PEAK_Y, player.getY());
            } else {
                double peak = player.getPersistentData().getDouble(NBT_PEAK_Y);
                double y = player.getY();
                if (y > peak) player.getPersistentData().putDouble(NBT_PEAK_Y, y);
            }
        } else {
            if (inAir) {
                double peak = player.getPersistentData().getDouble(NBT_PEAK_Y);
                double drop = peak - player.getY();

                if (drop >= MIN_FALL_BLOCKS_FOR_LAND_DAMAGE && player.getRandom().nextFloat() < LAND_DAMAGE_CHANCE) {
                    player.hurt(player.damageSources().generic(), LAND_DAMAGE);
                }

                player.getPersistentData().remove(NBT_IN_AIR);
                player.getPersistentData().remove(NBT_PEAK_Y);
            }
        }
    }
}
