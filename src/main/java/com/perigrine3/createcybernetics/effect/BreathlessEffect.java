package com.perigrine3.createcybernetics.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class BreathlessEffect extends MobEffect {

    private static final String NBT_NO_AIR_AIR = "cc_no_air_air";

    public BreathlessEffect() {
        super(MobEffectCategory.HARMFUL, 0x000000);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!(entity instanceof Player player)) return true;
        if (player.level().isClientSide) return true;

        if (player.isCreative() || player.isSpectator()) return true;

        CompoundTag pd = player.getPersistentData();

        int air = pd.contains(NBT_NO_AIR_AIR, Tag.TAG_INT)
                ? pd.getInt(NBT_NO_AIR_AIR)
                : player.getAirSupply();

        air -= 1;

        if (air <= -20) {
            player.hurt(player.damageSources().drown(), 2.0F);
            air = 0;
        }

        pd.putInt(NBT_NO_AIR_AIR, air);
        player.setAirSupply(air);

        return true;
    }

    @Override
    public void onMobRemoved(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide) return;

        player.getPersistentData().remove(NBT_NO_AIR_AIR);
        player.setAirSupply(player.getMaxAirSupply());
    }
}
