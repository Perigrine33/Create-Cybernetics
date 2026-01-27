package com.perigrine3.createcybernetics.compat.northstar;

import com.perigrine3.createcybernetics.compat.ModCompats;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class NorthstarCompat {
    private NorthstarCompat() {}

    public static final String NORTHSTAR_MODID = "northstar";

    @FunctionalInterface
    public interface SuitPredicate {
        boolean isSuitEquivalent(Player player);
    }

    private static final List<SuitPredicate> SUIT_PREDICATES = new ArrayList<>();

    private static boolean bootstrapped = false;
    private static boolean enabled = false;
    private static boolean eventsRegistered = false;

    public static void bootstrap() {
        if (bootstrapped) return;
        bootstrapped = true;

        enabled = ModCompats.isInstalled(NORTHSTAR_MODID);
        if (!enabled) return;

        SUIT_PREDICATES.add(CopernicusSuitPredicate::hasCopernicusSetInstalled);

        NeoForge.EVENT_BUS.register(Events.INSTANCE);
        eventsRegistered = true;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean isEventsRegistered() {
        return eventsRegistered;
    }

    public static void registerSuitPredicate(SuitPredicate predicate) {
        if (predicate != null) SUIT_PREDICATES.add(predicate);
    }

    public static boolean isNorthstarDimension(Level level) {
        ResourceLocation id = level.dimension().location();
        return NORTHSTAR_MODID.equals(id.getNamespace());
    }

    public static boolean hasSuitEquivalent(Player player) {
        if (!enabled || player == null) return false;
        for (SuitPredicate pred : SUIT_PREDICATES) {
            if (pred.isSuitEquivalent(player)) return true;
        }
        return false;
    }

    public static boolean isNorthstarOxygenDamage(DamageSource source) {
        if (source == null) return false;

        Optional<ResourceKey<DamageType>> keyOpt = source.typeHolder().unwrapKey();
        if (keyOpt.isEmpty()) return false;

        ResourceLocation id = keyOpt.get().location();
        if (!NORTHSTAR_MODID.equals(id.getNamespace())) return false;

        String path = id.getPath().toLowerCase(Locale.ROOT);

        return path.equals("suffocation")
                || path.contains("suffocat")
                || path.contains("vacuum")
                || path.contains("no_oxygen")
                || path.contains("asphyx")
                || path.contains("oxygen");
    }

    private static final class Events {
        private static final Events INSTANCE = new Events();
        private Events() {}

        @SubscribeEvent
        public void onLivingBreathe(LivingBreatheEvent event) {
            LivingEntity entity = event.getEntity();
            if (!(entity instanceof Player player)) return;

            Level level = player.level();
            if (!isNorthstarDimension(level)) return;
            if (!hasSuitEquivalent(player)) return;

            event.setCanBreathe(true);
            event.setConsumeAirAmount(0);

            int refill = event.getRefillAirAmount();
            if (refill < 1) {
                event.setRefillAirAmount(1);
            }
        }

        @SubscribeEvent
        public void onIncomingDamage(LivingIncomingDamageEvent event) {
            LivingEntity entity = event.getEntity();
            if (!(entity instanceof Player player)) return;

            Level level = player.level();
            if (!isNorthstarDimension(level)) return;
            if (!hasSuitEquivalent(player)) return;

            DamageSource source = event.getSource();

            if (isNorthstarOxygenDamage(source)) {
                event.setCanceled(true);
                return;
            }

            Optional<ResourceKey<DamageType>> keyOpt = source.typeHolder().unwrapKey();
            if (keyOpt.isEmpty()) return;

            ResourceKey<DamageType> key = keyOpt.get();
            if (key.equals(DamageTypes.FREEZE)
                    || key.equals(DamageTypes.IN_FIRE)
                    || key.equals(DamageTypes.ON_FIRE)
                    || key.equals(DamageTypes.HOT_FLOOR)
                    || key.equals(DamageTypes.LAVA)) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public void onPlayerTickPost(PlayerTickEvent.Post event) {
            Player player = event.getEntity();

            Level level = player.level();
            if (!isNorthstarDimension(level)) return;
            if (!hasSuitEquivalent(player)) return;

            if (player.getTicksFrozen() > 0) {
                player.setTicksFrozen(0);
            }

            if (player.isOnFire()) {
                player.clearFire();
            }
        }
    }
}
