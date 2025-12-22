package com.perigrine3.createcybernetics.client.model;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModelModifierManager {
    private static final List<ModelModifier> MODIFIERS = new ArrayList<>();

    public static void registerModifier(ModelModifier modifier) {
        MODIFIERS.add(modifier);
        MODIFIERS.sort(Comparator.comparingInt(ModelModifier::getPriority));
    }

    public static void applyModifiers(AbstractClientPlayer player, PlayerModel<?> model) {
        for (ModelModifier modifier : MODIFIERS) {
            if (modifier.shouldApply(player)) {
                modifier.applyModification(player, model);
            }
        }
    }
}