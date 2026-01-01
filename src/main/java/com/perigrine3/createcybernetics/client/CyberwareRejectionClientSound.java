package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.sounds.SoundSource;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class CyberwareRejectionClientSound {

    private static final int MIN_INTERVAL_TICKS = 3 * 20;
    private static final int MAX_INTERVAL_TICKS = 7 * 20;

    private static final int CHANCE_DENOM = 3;
    private static int nextAttemptTick = -1;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.isPaused()) return;

        if (!hasEffect(player, ModEffects.CYBERWARE_REJECTION)) {
            nextAttemptTick = -1;
            return;
        }

        int now = player.tickCount;

        if (nextAttemptTick < 0) {
            nextAttemptTick = now + Mth.nextInt(player.getRandom(), MIN_INTERVAL_TICKS, MAX_INTERVAL_TICKS);
            return;
        }

        if (now < nextAttemptTick) return;
        nextAttemptTick = now + Mth.nextInt(player.getRandom(), MIN_INTERVAL_TICKS, MAX_INTERVAL_TICKS);

        if (player.getRandom().nextInt(CHANCE_DENOM) != 0) return;

        player.level().playLocalSound(player.getX(), player.getY(), player.getZ(), ModSounds.GLITCHY.get(), SoundSource.PLAYERS,
                0.9f, 0.9f + player.getRandom().nextFloat() * 0.2f, false);
    }

    private static boolean hasEffect(LocalPlayer player, Holder<MobEffect> effect) {
        for (MobEffectInstance inst : player.getActiveEffects()) {
            if (inst != null && inst.is(effect)) return true;
        }
        return false;
    }

    private CyberwareRejectionClientSound() {}
}
