package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public class SubdermalSpikesEffect extends MobEffect {

    public SubdermalSpikesEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x000000);
    }

    @SubscribeEvent
    public static void onPlayerDamaged(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;
        if (event.getNewDamage() <= 0.0f) return;

        DamageSource source = event.getSource();

        if (source.is(DamageTypes.THORNS)) return;
        if (!isMeleeAttack(source)) return;

        MobEffectInstance inst = player.getEffect(ModEffects.SUBDERMAL_SPIKES_EFFECT);
        if (inst == null) return;

        int level = Mth.clamp(inst.getAmplifier() + 1, 1, 255);

        Entity attackerEntity = source.getEntity();
        if (!(attackerEntity instanceof LivingEntity attacker)) return;

        RandomSource rand = player.getRandom();
        float chance = 1.05f * level;
        if (rand.nextFloat() >= chance) return;

        int retaliateDamage = 1 + rand.nextInt(5);
        attacker.hurt(player.damageSources().thorns(player), retaliateDamage);

        player.level().playSound(null, player.blockPosition(), SoundEvents.THORNS_HIT, SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    private static boolean isMeleeAttack(DamageSource source) {
        if (source.is(DamageTypes.PLAYER_ATTACK)) return true;
        if (source.is(DamageTypes.MOB_ATTACK)) return true;

        return source.is(DamageTypes.MOB_ATTACK_NO_AGGRO);
    }
}
