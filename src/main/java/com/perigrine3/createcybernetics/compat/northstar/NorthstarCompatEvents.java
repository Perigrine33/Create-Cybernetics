package com.perigrine3.createcybernetics.compat.northstar;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Locale;
import java.util.Optional;

public final class NorthstarCompatEvents {

    @SubscribeEvent
    public void onLivingBreathe(LivingBreatheEvent event) {
        if (!NorthstarCompat.isEnabled()) return;

        if (!(event.getEntity() instanceof Player player)) return;
        if (!NorthstarCompat.isNorthstarDimension(player.level())) return;
        if (!NorthstarCompat.hasSuitEquivalent(player)) return;

        event.setCanBreathe(true);
        event.setConsumeAirAmount(0);

        event.setRefillAirAmount(Math.max(event.getRefillAirAmount(), 4));
    }

    @SubscribeEvent
    public void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!NorthstarCompat.isEnabled()) return;

        if (!(event.getEntity() instanceof Player player)) return;
        if (!NorthstarCompat.isNorthstarDimension(player.level())) return;
        if (!NorthstarCompat.hasSuitEquivalent(player)) return;

        DamageSource source = event.getSource();

        if (NorthstarCompat.isNorthstarOxygenDamage(source)) {
            event.setCanceled(true);
            return;
        }

        Optional<ResourceKey<DamageType>> keyOpt = source.typeHolder().unwrapKey();
        if (keyOpt.isEmpty()) return;

        ResourceKey<DamageType> key = keyOpt.get();

        if (key.equals(DamageTypes.FREEZE)) {
            event.setCanceled(true);
            return;
        }

        if (key.equals(DamageTypes.ON_FIRE)
                || key.equals(DamageTypes.IN_FIRE)
                || key.equals(DamageTypes.LAVA)
                || key.equals(DamageTypes.HOT_FLOOR)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (!NorthstarCompat.isEnabled()) return;

        Player player = event.getEntity();
        if (!NorthstarCompat.isNorthstarDimension(player.level())) return;
        if (!NorthstarCompat.hasSuitEquivalent(player)) return;

        if (player.getTicksFrozen() > 0) {
            player.setTicksFrozen(0);
        }

        if (player.isOnFire()) {
            player.clearFire();
        }
    }
}
