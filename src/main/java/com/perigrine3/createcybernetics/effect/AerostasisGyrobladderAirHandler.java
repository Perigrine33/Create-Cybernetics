package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class AerostasisGyrobladderAirHandler {

    public static final String NBT_O2 = "cc_gyro_o2_ticks";

    private AerostasisGyrobladderAirHandler() {}

    public static int getO2(Player player) {
        int maxAir = player.getMaxAirSupply();
        CompoundTag tag = player.getPersistentData();
        if (!tag.contains(NBT_O2)) tag.putInt(NBT_O2, maxAir);
        return Mth.clamp(tag.getInt(NBT_O2), 0, maxAir);
    }

    public static void setO2(Player player, int value) {
        int maxAir = player.getMaxAirSupply();
        player.getPersistentData().putInt(NBT_O2, Mth.clamp(value, 0, maxAir));
    }

    @SubscribeEvent
    public static void onPlayerTickPre(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        if (!player.hasEffect(ModEffects.AEROSTASIS_GYROBLADDER_EFFECT)) return;
        if (player.isCreative() || player.isSpectator()) return;

        if (player.isInWaterOrBubble()) return;

        final int maxAir = player.getMaxAirSupply();
        int o2 = getO2(player);

        boolean holdingBreath = AerostasisGyrobladderEffect.isJumpHeldServer(player);

        if (holdingBreath) {
            o2 = Math.max(0, o2 - 1);
        } else {
            o2 = Math.min(maxAir, o2 + 4);
        }

        setO2(player, o2);
        int preAir = Mth.clamp(o2 - 4, -20, maxAir);
        player.setAirSupply(preAir);
    }

    @SubscribeEvent
    public static void onPlayerTickPost(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        if (!player.hasEffect(ModEffects.AEROSTASIS_GYROBLADDER_EFFECT)) return;
        if (player.isCreative() || player.isSpectator()) return;
        if (player.isInWaterOrBubble()) return;

        player.setAirSupply(getO2(player));
    }
}
