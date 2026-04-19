package com.perigrine3.createcybernetics.effect.quickhacks;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.EntityCyberwareData;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.ModMobAttachments;
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
        super(MobEffectCategory.HARMFUL, 0xFF4AB3FF);
    }

    public static boolean applyQuickhack(LivingEntity target) {
        if (target == null) return false;
        if (target.level().isClientSide) return false;
        if (ICEProtocolItem.negatesQuickhack(target)) return false;
        if (!hasValidCyberwareTarget(target)) return false;

        RandomSource random = target.getRandom();
        if (random.nextFloat() > SUCCESS_CHANCE) {
            return false;
        }

        target.addEffect(new MobEffectInstance(
                ModEffects.OVERHEAT_HACK,
                DEFAULT_DURATION,
                DEFAULT_AMPLIFIER,
                false,
                false,
                true
        ));

        return true;
    }

    private static boolean hasValidCyberwareTarget(LivingEntity target) {
        if (target instanceof ServerPlayer player) {
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

        if (!target.hasData(ModMobAttachments.CYBERENTITY_CYBERWARE)) {
            return false;
        }

        EntityCyberwareData data = target.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
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
        if (entity == null || entity.level().isClientSide) {
            return true;
        }

        entity.setRemainingFireTicks(Math.max(entity.getRemainingFireTicks(), FIRE_SECONDS * 20));

        int drain = ENERGY_DRAIN_PER_TICK + (amplifier * 4);

        if (entity instanceof ServerPlayer player) {
            if (!player.hasData(ModAttachments.CYBERWARE)) {
                return true;
            }

            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) {
                return true;
            }

            data.extractEnergy(drain);
            data.setDirty();
            player.syncData(ModAttachments.CYBERWARE);
            return true;
        }

        if (!entity.hasData(ModMobAttachments.CYBERENTITY_CYBERWARE)) {
            return true;
        }

        EntityCyberwareData data = entity.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
        if (data == null) {
            return true;
        }

        data.extractEnergy(drain);
        data.setDirty();

        return true;
    }
}