package com.perigrine3.createcybernetics.screen.custom.visual_overlays;

import com.perigrine3.createcybernetics.client.render.rejection.CyberwareRejectionPostState;

public final class CyberwareRejectionOverlay {

    private CyberwareRejectionOverlay() {
    }

    public static void triggerForcedPulse(int duration) {
        CyberwareRejectionPostState.triggerForcedPulse(duration);
    }
}