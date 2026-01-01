package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.common.damage.ModDamageSources;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class CyberwareRejectionEffect extends MobEffect {
    private static final float DANGER_THRESHOLD = 0.25f;

    private static final float MIN_CHANCE = 0.10f;
    private static final float MAX_CHANCE = 0.85f;

    private static final int DEBUFF_BASE = 80;
    private static final int DEBUFF_EXTRA = 240;

    private static final float DAMAGE_CHANCE_PER_TICK = 0.003f;

    public CyberwareRejectionEffect(MobEffectCategory category, int color) {
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
        if (data == null) return true;

        int currentHumanity = data.getHumanity();
        int maxHumanity = Math.max(100, currentHumanity);
        float percent = maxHumanity <= 0 ? 0f : (currentHumanity / (float) maxHumanity);

        if (percent > DANGER_THRESHOLD) return true;

        float progress = (DANGER_THRESHOLD - percent) / DANGER_THRESHOLD;
        progress = Mth.clamp(progress, 0f, 1f);

        float chance = MIN_CHANCE + progress * (MAX_CHANCE - MIN_CHANCE);
        int durationTicks = DEBUFF_BASE + Mth.floor(progress * DEBUFF_EXTRA);
        int debuffAmp = (progress >= 0.66f) ? 2 : (progress >= 0.33f) ? 1 : 0;

        maybeApply(player, MobEffects.WEAKNESS,      chance * 1.00f, durationTicks, debuffAmp);
        maybeApply(player, MobEffects.DIG_SLOWDOWN,  chance * 0.90f, durationTicks, debuffAmp);
        maybeApply(player, MobEffects.CONFUSION,     chance * 0.80f, durationTicks, 0);

        if (player.getRandom().nextFloat() < DAMAGE_CHANCE_PER_TICK) {
            float base = (float) (1 << Math.min(30, amplifier));
            float scaled = base * (0.25f + 0.75f * progress);
            player.hurt(ModDamageSources.cyberwareRejection(player.level(), player, null), scaled);
        }

        return true;
    }

    private static void maybeApply(Player player, Holder<MobEffect> effect, float chance, int duration, int amplifier) {
        if (chance <= 0f) return;
        if (player.getRandom().nextFloat() >= chance) return;
        MobEffectInstance existing = player.getEffect(effect);
        if (existing != null && existing.getDuration() > duration / 2) return;
        player.addEffect(new MobEffectInstance(effect, duration, amplifier, false, false, true));
    }
}
