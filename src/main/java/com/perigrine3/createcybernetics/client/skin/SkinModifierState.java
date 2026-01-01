package com.perigrine3.createcybernetics.client.skin;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class SkinModifierState {
    private final List<SkinModifier> modifiers = new ArrayList<>();
    private boolean hideVanillaLayers = false;

    public void addModifier(SkinModifier modifier) {
        modifiers.add(modifier);
        if (modifier.shouldHideVanillaLayers()) {
            hideVanillaLayers = true;
        }
    }

    public boolean removeModifier(SkinModifier modifier) {
        boolean removed = modifiers.remove(modifier);
        if (removed && modifier.shouldHideVanillaLayers()) {
            hideVanillaLayers = modifiers.stream().anyMatch(SkinModifier::shouldHideVanillaLayers);
        }
        return removed;
    }

    public void clearModifiers() {
        modifiers.clear();
        hideVanillaLayers = false;
    }

    public boolean hasModifiers() {
        return !modifiers.isEmpty();
    }

    public List<SkinModifier> getModifiers() {
        return modifiers;
    }

    public boolean shouldHideVanillaLayers() {
        return hideVanillaLayers;
    }

    public EnumSet<SkinModifier.HideVanilla> getHideMask() {
        if (hideVanillaLayers) {
            return EnumSet.allOf(SkinModifier.HideVanilla.class);
        }

        EnumSet<SkinModifier.HideVanilla> mask = EnumSet.noneOf(SkinModifier.HideVanilla.class);
        for (SkinModifier m : modifiers) {
            mask.addAll(m.getHideMask());
        }
        return mask;
    }
}
