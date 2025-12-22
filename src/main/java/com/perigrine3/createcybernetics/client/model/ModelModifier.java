package com.perigrine3.createcybernetics.client.model;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;

public interface ModelModifier {

    void applyModification(AbstractClientPlayer player, PlayerModel<?> model);

    boolean shouldApply(AbstractClientPlayer player);

    default int getPriority() {
        return 0;
    }
}