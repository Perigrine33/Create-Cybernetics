package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class AerostasisGyrobladderEffect extends MobEffect {

    private static final String NBT_ACTIVE = "cc_gyro_active";
    private static final String NBT_OLD_FLY_SPEED = "cc_gyro_oldFlySpeedBits";
    private static final String NBT_OLD_MAYFLY = "cc_gyro_oldMayfly";

    private static final float GYRO_FLY_SPEED = 0.025F;

    public AerostasisGyrobladderEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return true;
        if (!(entity instanceof Player player)) return true;

        if (player.isCreative() || player.isSpectator()) return true;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        boolean hasGyro = data.hasSpecificItem(ModItems.WETWARE_AEROSTASISGYROBLADDER.get(), CyberwareSlot.LUNGS);

        if (!hasGyro) {
            disableFlight(player);
            return true;
        }

        int air = AerostasisGyrobladderAirHandler.getO2(player);

        if (air > 0) {
            enableFlight(player);
        } else {
            disableFlight(player);
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    public static void enableFlight(Player player) {
        CompoundTag tag = player.getPersistentData();

        if (!tag.getBoolean(NBT_ACTIVE)) {
            tag.putInt(NBT_OLD_FLY_SPEED, Float.floatToIntBits(player.getAbilities().getFlyingSpeed()));
            tag.putBoolean(NBT_OLD_MAYFLY, player.getAbilities().mayfly);
            tag.putBoolean(NBT_ACTIVE, true);
        }

        if (!player.getAbilities().mayfly) {
            player.getAbilities().mayfly = true;
        }

        if (player.getAbilities().getFlyingSpeed() != GYRO_FLY_SPEED) {
            player.getAbilities().setFlyingSpeed(GYRO_FLY_SPEED);
        }

        player.onUpdateAbilities();
    }

    public static void disableFlight(Player player) {
        CompoundTag tag = player.getPersistentData();

        if (!tag.getBoolean(NBT_ACTIVE)) return;

        player.getAbilities().flying = false;
        player.getAbilities().mayfly = tag.getBoolean(NBT_OLD_MAYFLY);
        player.getAbilities().setFlyingSpeed(Float.intBitsToFloat(tag.getInt(NBT_OLD_FLY_SPEED)));

        tag.remove(NBT_ACTIVE);
        tag.remove(NBT_OLD_FLY_SPEED);
        tag.remove(NBT_OLD_MAYFLY);

        player.onUpdateAbilities();
    }
}