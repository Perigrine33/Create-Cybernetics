package com.perigrine3.createcybernetics.event.custom;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.compat.ironsspells.IronsSpellbooksCompat;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.entity.custom.CyberskeletonEntity;
import com.perigrine3.createcybernetics.entity.custom.CyberzombieEntity;
import com.perigrine3.createcybernetics.entity.custom.SmasherEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class CyborgStruckByLightningHandler {
    private CyborgStruckByLightningHandler() {}

    private static final ResourceLocation IRONS_LIGHTNING_MAGIC_ID =
            ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "lightning_magic");

    private static final ResourceKey<DamageType> IRONS_LIGHTNING_MAGIC_KEY =
            ResourceKey.create(Registries.DAMAGE_TYPE, IRONS_LIGHTNING_MAGIC_ID);

    private static boolean isIronsLightningMagic(DamageSource src) {
        if (src == null) return false;
        Holder<DamageType> holder = src.typeHolder();
        return holder.unwrapKey().filter(IRONS_LIGHTNING_MAGIC_KEY::equals).isPresent();
    }

    private static boolean isAnyLightning(DamageSource src) {
        if (src == null) return false;
        if (src.is(DamageTypes.LIGHTNING_BOLT)) return true;
        return isIronsLightningMagic(src);
    }

    @SubscribeEvent
    public static void onLightningStrike(EntityStruckByLightningEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getEntity() instanceof LivingEntity living)) return;

        if (living instanceof CyberskeletonEntity || living instanceof CyberzombieEntity || living instanceof SmasherEntity) {
            applyEmp(living);
            return;
        }

        if (living instanceof ServerPlayer player) {
            if (FullBorgHandler.hasAnyImplantsAtAll(player)) {
                applyEmp(player);
                if (!FullBorgHandler.isFullBorg(player)) {
                    player.hurt(player.damageSources().lightningBolt(), 7.0F);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLightningSpell(LivingIncomingDamageEvent event) {
        LivingEntity living = event.getEntity();
        if (living.level().isClientSide) return;
        if (!isAnyLightning(event.getSource())) return;

        if (living instanceof CyberskeletonEntity || living instanceof CyberzombieEntity || living instanceof SmasherEntity) {
            applyEmp(living);
            return;
        }

        if (living instanceof ServerPlayer player) {
            if (FullBorgHandler.isFullBorg(player)) {
                applyEmp(player);
            }
        }
    }

    private static void applyEmp(LivingEntity living) {
        living.addEffect(new MobEffectInstance(ModEffects.EMP, 200, 0, true, true, true));
    }
}
