package com.perigrine3.createcybernetics.common.energy;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class EnergyDebug {
    private EnergyDebug() {}

//TRUE = DEBUG MODE
//FALSE = RELEASE MODE
    public static boolean ENABLED = true;

    public record TickSnapshot(int generatedPerTick, int consumedPerTick, int storedBefore, int storedAfter, int capacity, int netDeltaPerTick) {}

    public static void send(ServerPlayer player, TickSnapshot s) {
        if (!ENABLED) return;
        String msg =
                ChatFormatting.AQUA + "Gen " + ChatFormatting.GREEN + "+" + s.generatedPerTick() + "/t" + ChatFormatting.RESET + "  " +
                        ChatFormatting.AQUA + "Use " + ChatFormatting.RED + "-" + s.consumedPerTick() + "/t" + ChatFormatting.RESET + "  " +
                        ChatFormatting.AQUA + "Δ" + (s.netDeltaPerTick() >= 0 ? ChatFormatting.GREEN : ChatFormatting.RED) +
                        (s.netDeltaPerTick() >= 0 ? "+" : "") + s.netDeltaPerTick() + "/t" + ChatFormatting.RESET + "  " +
                        ChatFormatting.AQUA + "Stored " + ChatFormatting.YELLOW + s.storedBefore() + "/" + s.capacity() +
                        ChatFormatting.GRAY + " -> " +
                        ChatFormatting.YELLOW + s.storedAfter() + "/" + s.capacity();

        player.displayClientMessage(Component.literal(msg), true);
    }
}
