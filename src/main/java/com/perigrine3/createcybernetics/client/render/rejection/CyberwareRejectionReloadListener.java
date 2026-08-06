package com.perigrine3.createcybernetics.client.render.rejection;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public final class CyberwareRejectionReloadListener implements ResourceManagerReloadListener {

    public static final CyberwareRejectionReloadListener INSTANCE = new CyberwareRejectionReloadListener();

    private CyberwareRejectionReloadListener() {
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        CyberwareRejectionPostProcessor.reload();
    }
}