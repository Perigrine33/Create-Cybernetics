package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class CrouchSprintClientHandler {
    private CrouchSprintClientHandler() {}

    private static int lastForwardTapTick = -40;
    private static boolean wasForwardPressed = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        if (!player.hasData(ModAttachments.CYBERWARE)) return;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (!data.hasChipwareShardExact(ModItems.DATA_SHARD_BLACK.get())) return;

        KeyMapping sprintKey = mc.options.keySprint;
        KeyMapping forwardKey = mc.options.keyUp;

        boolean forwardPressed = forwardKey.isDown();
        if (forwardPressed && !wasForwardPressed) {
            int tick = mc.level.getGameTime() > Integer.MAX_VALUE ? 0 : (int) mc.level.getGameTime();
            if (tick - lastForwardTapTick <= 7) {
                if (player.isCrouching()) {
                    player.setSprinting(true);
                }
            }
            lastForwardTapTick = tick;
        }
        wasForwardPressed = forwardPressed;

        if (player.isCrouching() && sprintKey.isDown() && player.input.forwardImpulse > 0.8F) {
            player.setSprinting(true);
        }
    }
}
