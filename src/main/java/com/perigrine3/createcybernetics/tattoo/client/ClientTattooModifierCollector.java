package com.perigrine3.createcybernetics.tattoo.client;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.tattoo.TattooData;
import com.perigrine3.createcybernetics.tattoo.TattooLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class ClientTattooModifierCollector {
    private ClientTattooModifierCollector() {
    }

    public static TattooRenderData getInstalledTattoo(Player player) {
        if (player == null) {
            return null;
        }

        PlayerCyberwareData data = PlayerCyberwareData.getForVisual(player, player.registryAccess());
        if (data == null) {
            return null;
        }

        for (int i = 0; i < CyberwareSlot.SKIN.size; i++) {
            InstalledCyberware installed = data.get(CyberwareSlot.SKIN, i);
            if (installed == null) {
                continue;
            }

            ItemStack stack = installed.getItem();
            if (stack == null || stack.isEmpty() || !TattooData.has(stack)) {
                continue;
            }

            ResourceLocation tattooId = TattooData.getTattooId(stack);
            String hash = TattooData.getHash(stack);

            if (tattooId == null || hash == null || hash.isBlank()) {
                continue;
            }

            ResourceLocation texture = ClientTattooTextureCache.getTexture(tattooId, hash);
            if (texture == null) {
                return null;
            }

            return new TattooRenderData(
                    tattooId,
                    hash,
                    texture,
                    TattooData.getLayer(stack)
            );
        }

        return null;
    }

    public record TattooRenderData(
            ResourceLocation tattooId,
            String hash,
            ResourceLocation texture,
            TattooLayer layer
    ) {
    }
}