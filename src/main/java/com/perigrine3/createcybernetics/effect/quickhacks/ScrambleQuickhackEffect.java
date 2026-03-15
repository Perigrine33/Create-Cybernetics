package com.perigrine3.createcybernetics.effect.quickhacks;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.item.cyberware.ICEProtocolItem;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class ScrambleQuickhackEffect extends MobEffect {
    private static final int DEFAULT_DURATION = 100;
    private static final int DEFAULT_AMPLIFIER = 0;
    private static final float SUCCESS_CHANCE = 0.80f;

    public ScrambleQuickhackEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFB84AFF);
    }

    public static boolean applyQuickhack(LivingEntity target) {
        if (!(target instanceof ServerPlayer player)) return false;
        if (ICEProtocolItem.negatesQuickhack(player)) return false;
        if (!hasValidCyberlegTarget(target)) {
            return false;
        }

        RandomSource random = target.getRandom();
        if (random.nextFloat() > SUCCESS_CHANCE) {
            return false;
        }

        target.addEffect(new MobEffectInstance(ModEffects.SCRAMBLE_HACK, DEFAULT_DURATION, DEFAULT_AMPLIFIER, false, false, true));

        return true;
    }

    private static boolean hasValidCyberlegTarget(LivingEntity target) {
        if (!(target instanceof ServerPlayer player)) {
            return false;
        }

        if (!player.hasData(ModAttachments.CYBERWARE)) {
            return false;
        }

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        return data.hasAnyTagged(ModTags.Items.LEFT_CYBERLEG, CyberwareSlot.LLEG) ||
                data.hasAnyTagged(ModTags.Items.RIGHT_CYBERLEG, CyberwareSlot.RLEG) ||
                data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return false;
    }
}