package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID)
public final class NeuropozyneStackingHandler {

    private static final String NBT_STACK_GUARD_TICK = "cc_neuropozyne_stack_guard_tick";
    private static final int MAX_AMP = 255;

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;

        MobEffectInstance added = event.getEffectInstance();
        if (added == null) return;
        if (!added.is(ModEffects.NEUROPOZYNE)) return;

        MobEffectInstance old = event.getOldEffectInstance();
        if (old == null) return;

        long now = entity.level().getGameTime();
        var pd = entity.getPersistentData();
        if (pd.getLong(NBT_STACK_GUARD_TICK) == now) return;

        int desiredAmp = Math.min(old.getAmplifier() + 1, MAX_AMP);
        if (added.getAmplifier() == desiredAmp) return;

        pd.putLong(NBT_STACK_GUARD_TICK, now);

        entity.addEffect(new MobEffectInstance(
                added.getEffect(),
                added.getDuration(),
                desiredAmp,
                added.isAmbient(),
                added.isVisible(),
                added.showIcon()
        ));
    }
}
