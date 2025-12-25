package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
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
            hideVanillaLayers = modifiers.stream()
                    .anyMatch(SkinModifier::shouldHideVanillaLayers);
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

}