package com.perigrine3.createcybernetics.client.render.attachment;

import net.minecraft.client.player.AbstractClientPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class CyberwarePlayerAttachmentRegistry {

    private static final List<CyberwarePlayerAttachmentProvider> PROVIDERS = new CopyOnWriteArrayList<>();

    private CyberwarePlayerAttachmentRegistry() {}

    public static void register(CyberwarePlayerAttachmentProvider provider) {
        if (provider == null || PROVIDERS.contains(provider)) return;
        PROVIDERS.add(provider);
    }

    public static List<CyberwarePlayerAttachment> getAttachments(AbstractClientPlayer player) {
        List<CyberwarePlayerAttachment> attachments = new ArrayList<>();

        for (CyberwarePlayerAttachmentProvider provider : PROVIDERS) {
            if (provider == null) continue;

            try {
                List<CyberwarePlayerAttachment> provided = provider.getAttachments(player);

                if (provided != null) {
                    attachments.addAll(provided);
                }
            } catch (Throwable ignored) {}
        }

        return attachments;
    }
}