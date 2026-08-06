package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.common.damage.ModDamageSources;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class CyberwareRejectionEffect extends MobEffect {

    private static final int EFFECT_ROLL_INTERVAL_TICKS = 20;

    private static final int LEVEL_1_DURATION = 80;
    private static final int LEVEL_2_DURATION = 100;
    private static final int LEVEL_3_DURATION = 1400;

    private static final float LEVEL_1_BASE_CHANCE = 0.20F;
    private static final float LEVEL_2_BASE_CHANCE = 0.55F;
    private static final float LEVEL_3_BASE_CHANCE = 0.85F;

    private static final float LEVEL_2_DAMAGE_CHANCE = 0.08F;
    private static final float LEVEL_3_DAMAGE_CHANCE = 0.16F;

    private static final float REJECTION_DAMAGE = 0.001F;

    public CyberwareRejectionEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity living, int amplifier) {
        if (!(living instanceof Player player)) {
            return true;
        }

        if (player.level().isClientSide) {
            return true;
        }

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) {
            return true;
        }

        CyberpsychosisSeverity severity = CyberpsychosisSeverity.fromPlayer(player);
        if (severity == CyberpsychosisSeverity.NONE) {
            return true;
        }

        if (player.tickCount % EFFECT_ROLL_INTERVAL_TICKS != 0) {
            return true;
        }

        switch (severity) {
            case LEVEL_1 -> applyLevelOne(player);
            case LEVEL_2 -> applyLevelTwo(player);
            case LEVEL_3 -> applyLevelThree(player);
            default -> {
            }
        }

        return true;
    }

    private static void applyLevelOne(Player player) {
        float progress = CyberpsychosisSeverity.getPositiveDangerProgress(player);
        float chance = LEVEL_1_BASE_CHANCE + progress * 0.20F;

        maybeApply(player, MobEffects.WEAKNESS, chance * 0.75F, LEVEL_1_DURATION, 0);
        maybeApply(player, MobEffects.DIG_SLOWDOWN, chance * 0.60F, LEVEL_1_DURATION, 0);
    }

    private static void applyLevelTwo(Player player) {
        float progress = CyberpsychosisSeverity.getPositiveDangerProgress(player);
        float chance = LEVEL_2_BASE_CHANCE + progress * 0.20F;

        int duration = LEVEL_2_DURATION + Mth.floor(progress * 80.0F);
        int amplifier = progress >= 0.75F ? 1 : 0;

        maybeApply(player, MobEffects.WEAKNESS, chance, duration, amplifier);
        maybeApply(player, MobEffects.DIG_SLOWDOWN, chance * 0.90F, duration, amplifier);

        if (player.getRandom().nextFloat() < LEVEL_2_DAMAGE_CHANCE) {
            applyRejectionDamage(player);
        }
    }

    private static void applyLevelThree(Player player) {
        float negativeProgress = CyberpsychosisSeverity.getNegativeProgress(player);
        float chance = LEVEL_3_BASE_CHANCE + negativeProgress * 0.10F;

        int duration = LEVEL_3_DURATION + Mth.floor(negativeProgress * 120.0F);
        int amplifier = negativeProgress >= 0.66F ? 2 : negativeProgress >= 0.33F ? 1 : 0;

        maybeApply(player, MobEffects.MOVEMENT_SPEED, 1, duration, 1);
        maybeApply(player, MobEffects.DAMAGE_BOOST, chance, duration, amplifier);
        maybeApply(player, MobEffects.DAMAGE_RESISTANCE, chance, duration, amplifier);
        maybeApply(player, MobEffects.DIG_SLOWDOWN, chance, duration, amplifier);

        if (player.getRandom().nextFloat() < LEVEL_3_DAMAGE_CHANCE) {
            applyRejectionDamage(player);
        }
    }

    private static void applyRejectionDamage(Player player) {
        player.hurt(ModDamageSources.cyberwareRejection(player.level(), player, null), REJECTION_DAMAGE);
    }

    private static void maybeApply(Player player, Holder<MobEffect> effect, float chance, int duration, int amplifier) {
        if (chance <= 0.0F) {
            return;
        }

        if (player.getRandom().nextFloat() >= chance) {
            return;
        }

        MobEffectInstance existing = player.getEffect(effect);
        if (existing != null && existing.getDuration() > duration / 2 && existing.getAmplifier() >= amplifier) {
            return;
        }

        player.addEffect(new MobEffectInstance(effect, duration, amplifier, false, false, true));
    }
}