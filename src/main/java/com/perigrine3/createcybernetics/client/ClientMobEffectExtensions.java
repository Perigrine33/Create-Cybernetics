package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.effect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientMobEffectExtensions {
    private ClientMobEffectExtensions() {}

    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerMobEffect(new IClientMobEffectExtensions() {
            @Override
            public boolean isVisibleInInventory(MobEffectInstance instance) {
                return false;
            }
        },
            //HIDDEN EFFECTS
                ModEffects.AEROSTASIS_GYROBLADDER_EFFECT,
                ModEffects.SYNTHETIC_SETULES_EFFECT,
                ModEffects.PNEUMATIC_CALVES_EFFECT,
                ModEffects.SPURS_EFFECT,
                ModEffects.NEURAL_CONTEXTUALIZER_EFFECT,
                ModEffects.SUBDERMAL_SPIKES_EFFECT,
                ModEffects.GUARDIAN_EYE_EFFECT,
                ModEffects.PROJECTILE_DODGE_EFFECT,
                ModEffects.BREATHLESS_EFFECT,
                ModEffects.SCULK_LUNGS_EFFECT,
                ModEffects.SANDEVISTAN_EFFECT
        );
    }
}
