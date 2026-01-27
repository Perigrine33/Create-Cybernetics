package com.perigrine3.createcybernetics.compat;

import com.perigrine3.createcybernetics.compat.northstar.NorthstarCompat;

public final class CompatBootstrap {
    private CompatBootstrap() {}

    public static void bootstrap() {
        NorthstarCompat.bootstrap();
    }
}
