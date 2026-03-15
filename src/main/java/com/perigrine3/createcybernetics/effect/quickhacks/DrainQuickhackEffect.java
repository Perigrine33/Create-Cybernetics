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

public class DrainQuickhackEffect extends MobEffect {
    private static final int DEFAULT_DURATION = 100;
    private static final int DEFAULT_AMPLIFIER = 0;
    private static final float SUCCESS_CHANCE = 0.75f;

    public DrainQuickhackEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF00C4B3);
    }

    public static boolean applyQuickhack(LivingEntity target) {
        if (!(target instanceof ServerPlayer player)) return false;
        if (ICEProtocolItem.negatesQuickhack(player)) return false;
        if (!player.hasData(ModAttachments.CYBERWARE)) return false;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);

        if (!data.hasAnyTagged(
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
        )) return false;

        RandomSource random = player.getRandom();
        if (random.nextFloat() > SUCCESS_CHANCE) return false;

        player.addEffect(new MobEffectInstance(
                ModEffects.DRAIN_HACK,
                DEFAULT_DURATION,
                DEFAULT_AMPLIFIER,
                false,
                false,
                true
        ));
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return false;
    }
}