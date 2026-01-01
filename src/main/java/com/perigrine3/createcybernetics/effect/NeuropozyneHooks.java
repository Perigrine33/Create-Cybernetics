package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID)
public final class NeuropozyneHooks {

    private static boolean hasNeuropozyne(Player player) {
        for (MobEffectInstance inst : player.getActiveEffects()) {
            if (inst.is(ModEffects.NEUROPOZYNE)) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        MobEffectInstance inst = event.getEffectInstance();
        if (inst == null) return;
        if (!inst.is(ModEffects.NEUROPOZYNE)) return;

        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data != null) data.clearHumanityBonus();
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;
        if (!hasNeuropozyne(player)) {
            data.clearHumanityBonus();
        }
    }
}
