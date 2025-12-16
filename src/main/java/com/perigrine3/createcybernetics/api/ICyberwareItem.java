package com.perigrine3.createcybernetics.api;

import net.minecraft.world.entity.player.Player;

import java.util.Set;

public interface ICyberwareItem {

    Set<CyberwareSlot> getSupportedSlots();

    default boolean supportsSlot(CyberwareSlot slot) {
        return getSupportedSlots().contains(slot);
    }

    boolean replacesOrgan();

    Set<CyberwareSlot> getReplacedOrgans();

    int getHumanityCost();

    default void onInstalled(Player player) {}

    default void onRemoved(Player player) {}

    default void onTick(Player player) {}
}
