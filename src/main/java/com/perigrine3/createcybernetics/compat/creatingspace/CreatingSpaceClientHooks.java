package com.perigrine3.createcybernetics.compat.creatingspace;

import com.perigrine3.createcybernetics.screen.custom.hud.CyberwareHudLayer;

public final class CreatingSpaceClientHooks {
    private CreatingSpaceClientHooks() {}

    public static void setOxygenHud(int oxygen) {
        CyberwareHudLayer.ClientCopernicusOxygenState.set(oxygen);
    }
}