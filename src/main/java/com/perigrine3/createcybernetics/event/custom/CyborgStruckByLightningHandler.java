package com.perigrine3.createcybernetics.event.custom;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.entity.custom.CyberskeletonEntity;
import com.perigrine3.createcybernetics.entity.custom.CyberzombieEntity;
import com.perigrine3.createcybernetics.entity.custom.SmasherEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class CyborgStruckByLightningHandler {
    private CyborgStruckByLightningHandler() {}

    @SubscribeEvent
    public static void onLightningStrike(EntityStruckByLightningEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getEntity() instanceof LivingEntity living)) return;

        if (living instanceof CyberskeletonEntity || living instanceof CyberzombieEntity || living instanceof SmasherEntity) {
            applyEmp(living);
            return;
        }

        if (living instanceof Player player) {
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data != null && data.getAll() != null && !data.getAll().isEmpty()) {
                applyEmp(player);
            }
        }
    }



    private static void applyEmp(LivingEntity living) {
        living.addEffect(new MobEffectInstance(ModEffects.EMP, 200, 0, true, true, true));
    }
}
