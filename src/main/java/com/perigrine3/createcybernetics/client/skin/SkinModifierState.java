package com.perigrine3.createcybernetics.client.skin;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class SkinModifierState {
    private final List<SkinModifier> modifiers = new ArrayList<>();
    private final List<SkinHighlight> highlights = new ArrayList<>();

    public void addModifier(SkinModifier modifier) {
        if (modifier != null) {
            modifiers.add(modifier);
        }
    }

    public boolean removeModifier(SkinModifier modifier) {
        if (modifier == null) return false;
        return modifiers.remove(modifier);
    }

    public void clearModifiers() {
        modifiers.clear();
        highlights.clear();
    }

    public boolean hasModifiers() {
        return !modifiers.isEmpty();
    }

    public List<SkinModifier> getModifiers() {
        return modifiers;
    }

    public boolean shouldHideVanillaLayers() {
        return !getHideMask().isEmpty();
    }

    public EnumSet<SkinModifier.HideVanilla> getHideMask() {
        EnumSet<SkinModifier.HideVanilla> mask = EnumSet.noneOf(SkinModifier.HideVanilla.class);

        for (SkinModifier modifier : modifiers) {
            if (modifier == null) continue;
            mask.addAll(modifier.getHideMask());
        }

        return mask;
    }

    public void addHighlight(SkinHighlight highlight) {
        if (highlight != null) {
            highlights.add(highlight);
        }
    }

    public void clearHighlights() {
        highlights.clear();
    }

    public boolean hasHighlights() {
        return !highlights.isEmpty();
    }

    public List<SkinHighlight> getHighlights() {
        return highlights;
    }
}