package com.perigrine3.createcybernetics.client.render.attachment;

import net.minecraft.client.player.AbstractClientPlayer;

import java.util.List;

public interface CyberwarePlayerAttachmentProvider {

    List<CyberwarePlayerAttachment> getAttachments(AbstractClientPlayer player);
}