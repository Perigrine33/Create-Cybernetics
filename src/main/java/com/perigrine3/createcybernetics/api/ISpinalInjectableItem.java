package com.perigrine3.createcybernetics.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface ISpinalInjectableItem {

    List<MobEffectInstance> getSpinalInjectionEffects(ItemStack stack);

    default boolean shouldSpinalInjectorInject(ServerPlayer player, ItemStack stack) {
        for (MobEffectInstance instance : getSpinalInjectionEffects(stack)) {
            if (instance == null) continue;

            MobEffect effect = instance.getEffect().value();
            if (effect == null || effect.isInstantenous()) {
                continue;
            }

            if (!player.hasEffect(instance.getEffect())) {
                return true;
            }
        }

        return false;
    }

    default void applySpinalInjection(ServerPlayer player, ItemStack stack) {
        for (MobEffectInstance instance : getSpinalInjectionEffects(stack)) {
            if (instance == null) continue;

            MobEffect effect = instance.getEffect().value();
            if (effect == null) continue;

            if (effect.isInstantenous()) {
                effect.applyInstantenousEffect(
                        player,
                        player,
                        player,
                        instance.getAmplifier(),
                        1.0D
                );
                continue;
            }

            player.addEffect(new MobEffectInstance(
                    instance.getEffect(),
                    instance.getDuration(),
                    instance.getAmplifier(),
                    instance.isAmbient(),
                    instance.isVisible(),
                    instance.showIcon()
            ));
        }
    }
}