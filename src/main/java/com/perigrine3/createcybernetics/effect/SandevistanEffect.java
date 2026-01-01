package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public class SandevistanEffect extends MobEffect {

    public SandevistanEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFFFFF);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!(entity instanceof Player player)) return true;
        if (player.level().isClientSide()) return true;

        applyAll(player);
        return true;
    }

    private static void applyAll(Player player) {
        CyberwareAttributeHelper.applyModifier(player, "sandevistan_speed");
        CyberwareAttributeHelper.applyModifier(player, "sandevistan_stepheight");
        CyberwareAttributeHelper.applyModifier(player, "sandevistan_jump");
    }

    private static void removeAll(Player player) {
        CyberwareAttributeHelper.removeModifier(player, "sandevistan_speed");
        CyberwareAttributeHelper.removeModifier(player, "sandevistan_stepheight");
        CyberwareAttributeHelper.removeModifier(player, "sandevistan_jump");
    }

    private static boolean isSandevistan(MobEffect effect) {
        return effect == ModEffects.SANDEVISTAN_EFFECT;
    }

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        var inst = event.getEffectInstance();
        if (inst == null) return;

        if (isSandevistan(inst.getEffect().value())) {
            removeAll(player);
        }
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        var inst = event.getEffectInstance();
        if (inst == null) return;

        if (isSandevistan(inst.getEffect().value())) {
            removeAll(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (!player.hasEffect(ModEffects.SANDEVISTAN_EFFECT)) {
            removeAll(player);
        }
    }
}
