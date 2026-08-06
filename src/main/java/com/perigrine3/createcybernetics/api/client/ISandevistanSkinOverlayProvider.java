package com.perigrine3.createcybernetics.api.client;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface ISandevistanSkinOverlayProvider {

    List<OverlayPass> capture(AbstractClientPlayer player);

    record OverlayPass(ResourceLocation texture, int color, boolean fullBright) {}
}