package com.perigrine3.createcybernetics.effect.quickhacks;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.item.cyberware.ICEProtocolItem;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class OverheatQuickhackEffect extends MobEffect {
    private static final int DEFAULT_DURATION = 100;
    private static final int DEFAULT_AMPLIFIER = 0;
    private static final int FIRE_SECONDS = 5;
    private static final int ENERGY_DRAIN_PER_TICK = 8;
    private static final float SUCCESS_CHANCE = 0.75f;

    public OverheatQuickhackEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF6A00);
    }

    public static boolean applyQuickhack(LivingEntity target) {
        if (!(target instanceof ServerPlayer player)) return false;
        if (ICEProtocolItem.negatesQuickhack(player)) return false;
        if (!hasValidCyberwareTarget(target)) {
            return false;
        }

        RandomSource random = target.getRandom();
        if (random.nextFloat() > SUCCESS_CHANCE) {
            return false;
        }

        target.addEffect(new MobEffectInstance(ModEffects.OVERHEAT_HACK, DEFAULT_DURATION, DEFAULT_AMPLIFIER, false, false, true));

        return true;
    }

    private static boolean hasValidCyberwareTarget(LivingEntity target) {
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

        return data.hasAnyTagged(
                ModTags.Items.CYBERWARE_ITEM,
                CyberwareSlot.BRAIN,
                CyberwareSlot.EYES,
                CyberwareSlot.HEART,
                CyberwareSlot.LUNGS,
                CyberwareSlot.ORGANS,
                CyberwareSlot.BONE,
                CyberwareSlot.SKIN,
                CyberwareSlot.LARM,
                CyberwareSlot.RARM,
                CyberwareSlot.LLEG,
                CyberwareSlot.RLEG
        );
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

        player.setRemainingFireTicks(Math.max(player.getRemainingFireTicks(), FIRE_SECONDS * 20));
        data.extractEnergy(ENERGY_DRAIN_PER_TICK + (amplifier * 4));
        data.setDirty();
        player.syncData(ModAttachments.CYBERWARE);

        return true;
    }
}