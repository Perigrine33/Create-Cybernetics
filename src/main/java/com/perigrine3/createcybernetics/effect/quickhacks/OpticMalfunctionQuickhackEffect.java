package com.perigrine3.createcybernetics.effect.quickhacks;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.EntityCyberwareData;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.ModMobAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.item.cyberware.ICEProtocolItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class OpticMalfunctionQuickhackEffect extends MobEffect {
    private static final int DEFAULT_DURATION = 100;
    private static final int DEFAULT_AMPLIFIER = 0;
    private static final float SUCCESS_CHANCE = 0.80f;

    private static final int BLINDNESS_DURATION_ON_CAST = 100;
    private static final int BLINDNESS_DURATION_REFRESH = 100;
    private static final int BLINDNESS_AMPLIFIER = 0;

    private static final String NBT_LAST_BLIND_STUMBLE_TICK = "cc_opticmalf_last_stumble_tick";
    private static final int STUMBLE_INTERVAL_TICKS = 10;
    private static final double STUMBLE_RADIUS = 2.5D;
    private static final double STUMBLE_SPEED = 0.8D;

    public OpticMalfunctionQuickhackEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF4AB3FF);
    }

    public static boolean applyQuickhack(LivingEntity target) {
        if (target == null) return false;
        if (target.level().isClientSide) return false;
        if (ICEProtocolItem.negatesQuickhack(target)) return false;
        if (!hasValidCybereyeTarget(target)) return false;

        RandomSource random = target.getRandom();
        if (random.nextFloat() > SUCCESS_CHANCE) {
            return false;
        }

        target.addEffect(new MobEffectInstance(
                ModEffects.OPTICMALFUNCTION_HACK,
                DEFAULT_DURATION,
                DEFAULT_AMPLIFIER,
                false,
                false,
                true
        ));

        target.addEffect(new MobEffectInstance(
                MobEffects.BLINDNESS,
                BLINDNESS_DURATION_ON_CAST,
                BLINDNESS_AMPLIFIER,
                false,
                false,
                true
        ));

        return true;
    }

    private static boolean hasValidCybereyeTarget(LivingEntity target) {
        if (target instanceof ServerPlayer player) {
            if (!player.hasData(ModAttachments.CYBERWARE)) {
                return false;
            }

            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) {
                return false;
            }

            return data.hasSpecificItem(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES);
        }

        if (!target.hasData(ModMobAttachments.CYBERENTITY_CYBERWARE)) {
            return false;
        }

        EntityCyberwareData data = target.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
        if (data == null) {
            return false;
        }

        return data.hasSpecificItem(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity == null || entity.level().isClientSide) {
            return true;
        }

        if (!hasValidCybereyeTarget(entity)) {
            return true;
        }

        entity.addEffect(new MobEffectInstance(
                MobEffects.BLINDNESS,
                BLINDNESS_DURATION_REFRESH,
                BLINDNESS_AMPLIFIER,
                false,
                false,
                true
        ));

        if (entity instanceof Mob mob) {
            applyBlindMobBehavior(mob);
        }

        return true;
    }

    private static void applyBlindMobBehavior(Mob mob) {
        long now = mob.level().getGameTime();
        long last = mob.getPersistentData().getLong(NBT_LAST_BLIND_STUMBLE_TICK);

        mob.getNavigation().stop();

        if (mob.getMoveControl().hasWanted()) {
            mob.getMoveControl().setWantedPosition(mob.getX(), mob.getY(), mob.getZ(), 0.0D);
        }

        if (now - last < STUMBLE_INTERVAL_TICKS) {
            return;
        }

        mob.getPersistentData().putLong(NBT_LAST_BLIND_STUMBLE_TICK, now);

        RandomSource random = mob.getRandom();
        double angle = random.nextDouble() * (Math.PI * 2.0D);
        double dist = 0.75D + random.nextDouble() * STUMBLE_RADIUS;

        double x = mob.getX() + Math.cos(angle) * dist;
        double z = mob.getZ() + Math.sin(angle) * dist;
        double y = mob.getY();

        mob.getNavigation().moveTo(x, y, z, STUMBLE_SPEED);
        mob.getLookControl().setLookAt(x, y, z, 10.0F, 10.0F);
    }
}