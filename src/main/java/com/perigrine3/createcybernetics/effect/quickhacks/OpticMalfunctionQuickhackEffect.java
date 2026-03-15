package com.perigrine3.createcybernetics.effect.quickhacks;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
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

public class OpticMalfunctionQuickhackEffect extends MobEffect {
    private static final int DEFAULT_DURATION = 100;
    private static final int DEFAULT_AMPLIFIER = 0;
    private static final float SUCCESS_CHANCE = 0.80f;

    private static final int CONFUSION_DURATION_ON_CAST = 100;
    private static final int CONFUSION_DURATION_REFRESH = 100;
    private static final int CONFUSION_AMPLIFIER = 0;

    public OpticMalfunctionQuickhackEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF55D9FF);
    }

    public static boolean applyQuickhack(LivingEntity target) {
        if (!(target instanceof ServerPlayer player)) return false;
        if (ICEProtocolItem.negatesQuickhack(player)) return false;

        if (!hasValidCybereyeTarget(target)) {
            return false;
        }

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
                MobEffects.DARKNESS,
                CONFUSION_DURATION_ON_CAST,
                CONFUSION_AMPLIFIER,
                false,
                false,
                true
        ));

        return true;
    }

    private static boolean hasValidCybereyeTarget(LivingEntity target) {
        if (!(target instanceof ServerPlayer player)) {
            return false;
        }

        if (!player.hasData(ModAttachments.CYBERWARE)) {
            return false;
        }

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
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
        if (!(entity instanceof ServerPlayer player)) {
            return true;
        }

        if (!player.hasData(ModAttachments.CYBERWARE)) {
            return true;
        }

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) {
            return true;
        }

        if (!data.hasSpecificItem(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES)) {
            return true;
        }

        player.addEffect(new MobEffectInstance(
                MobEffects.DARKNESS,
                CONFUSION_DURATION_REFRESH,
                CONFUSION_AMPLIFIER,
                false,
                false,
                true
        ));

        return true;
    }
}