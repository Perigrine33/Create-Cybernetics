package com.perigrine3.createcybernetics.event.custom;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

import java.util.List;

public final class CyberwareSurgeryEvent extends Event {
    private final ServerPlayer player;

    private final int installs;
    private final int removals;

    private final List<Change> installed;
    private final List<Change> removed;

    public CyberwareSurgeryEvent(ServerPlayer player, int installs, int removals, List<Change> installed, List<Change> removed) {
        this.player = player;
        this.installs = installs;
        this.removals = removals;
        this.installed = installed;
        this.removed = removed;
    }

    public ServerPlayer getPlayer() { return player; }
    public int getInstalls() { return installs; }
    public int getRemovals() { return removals; }

    public List<Change> getInstalled() { return installed; }
    public List<Change> getRemoved() { return removed; }

    public record Change(CyberwareSlot slot, int index, ItemStack stack) {}
}
