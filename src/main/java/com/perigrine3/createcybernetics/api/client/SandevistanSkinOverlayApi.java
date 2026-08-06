package com.perigrine3.createcybernetics.api.client;

import net.minecraft.client.player.AbstractClientPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class SandevistanSkinOverlayApi {

    private static final List<ISandevistanSkinOverlayProvider> PROVIDERS = new CopyOnWriteArrayList<>();

    private SandevistanSkinOverlayApi() {}

    public static void register(ISandevistanSkinOverlayProvider provider) {
        if (provider == null || PROVIDERS.contains(provider)) return;
        PROVIDERS.add(provider);
    }

    public static List<ISandevistanSkinOverlayProvider.OverlayPass> capture(AbstractClientPlayer player) {
        List<ISandevistanSkinOverlayProvider.OverlayPass> passes = new ArrayList<>();

        for (ISandevistanSkinOverlayProvider provider : PROVIDERS) {
            if (provider == null) continue;

            try {
                List<ISandevistanSkinOverlayProvider.OverlayPass> captured = provider.capture(player);
                if (captured != null) passes.addAll(captured);
            } catch (Throwable ignored) {}
        }

        return passes;
    }
}