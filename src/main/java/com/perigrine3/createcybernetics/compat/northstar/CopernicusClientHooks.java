package com.perigrine3.createcybernetics.compat.northstar;

import com.perigrine3.createcybernetics.screen.custom.hud.CyberwareHudLayer;

public final class CopernicusClientHooks {
    private CopernicusClientHooks() {}

    public static void setOxygenHud(int oxygen) {
        CyberwareHudLayer.ClientCopernicusOxygenState.set(oxygen);
    }
}
