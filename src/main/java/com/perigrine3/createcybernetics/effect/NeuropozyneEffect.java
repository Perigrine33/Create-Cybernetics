package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class NeuropozyneEffect extends MobEffect {

    private static final int HUMANITY_PER_LEVEL = 25;

    private static final int SIDE_EFFECT_START_AMP = 5;
    private static final int SIDE_EFFECT_ROLL_INTERVAL_TICKS = 20;

    private static final float BASE_CHANCE_AT_START = 0.05f;
    private static final float CHANCE_PER_EXTRA_AMP = 0.05f;
    private static final float MAX_CHANCE = 0.75f;

    private static final int DEBUFF_DURATION_BASE = 80;
    private static final int DEBUFF_DURATION_PER_LEVEL = 40;

    public NeuropozyneEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity living, int amplifier) {
        if (!(living instanceof Player player)) return true;
        if (player.level().isClientSide) return true;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data != null) {
            int bonus = (amplifier + 1) * HUMANITY_PER_LEVEL;
            data.setHumanityBonus(bonus);
        }

        if ((player.tickCount % 20) == 0) {
            MobEffectInstance rejection = player.getEffect(ModEffects.CYBERWARE_REJECTION);
            if (rejection != null) {
                player.removeEffect(ModEffects.CYBERWARE_REJECTION);
            }
        }

        if (amplifier >= SIDE_EFFECT_START_AMP && (player.tickCount % SIDE_EFFECT_ROLL_INTERVAL_TICKS == 0)) {
            rollSideEffects(player, amplifier);
        }

        return true;
    }

    @Override
    public void onMobRemoved(LivingEntity living, int amplifier, Entity.RemovalReason reason) {
        if (!(living instanceof Player player)) return;
        if (player.level().isClientSide) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data != null) data.clearHumanityBonus();
    }

    private static void rollSideEffects(Player player, int amplifier) {
        int level = (amplifier - SIDE_EFFECT_START_AMP) + 1;

        float chance = BASE_CHANCE_AT_START + (level - 1) * CHANCE_PER_EXTRA_AMP;
        chance = Mth.clamp(chance, 0f, MAX_CHANCE);

        int debuffDuration = DEBUFF_DURATION_BASE + (level - 1) * DEBUFF_DURATION_PER_LEVEL;
        int debuffAmp = Math.min(2, (level - 1) / 2);

        maybeApply(player, MobEffects.WEAKNESS, chance * 1.00f, debuffDuration, debuffAmp);
        maybeApply(player, MobEffects.DIG_SLOWDOWN, chance * 0.85f, debuffDuration, debuffAmp);
        maybeApply(player, MobEffects.CONFUSION, chance * 0.70f, debuffDuration, 0);
    }

    private static void maybeApply(Player player, Holder<MobEffect> effect, float chance, int duration, int amplifier) {
        if (chance <= 0f) return;
        if (player.getRandom().nextFloat() >= chance) return;
        MobEffectInstance existing = player.getEffect(effect);

        if (existing != null && existing.getDuration() > duration / 2) return;
        player.addEffect(new MobEffectInstance(effect, duration, amplifier, false, true, true));
    }
}
