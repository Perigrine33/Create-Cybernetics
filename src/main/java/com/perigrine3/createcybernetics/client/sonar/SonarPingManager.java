package com.perigrine3.createcybernetics.client.sonar;

import net.minecraft.Util;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public final class SonarPingManager {
    public static final int MAX_PINGS = 16;

    public static final class Ping {
        public final Vec3 worldPos;
        public final long timeNanos;

        public Ping(Vec3 worldPos, long timeNanos) {
            this.worldPos = worldPos;
            this.timeNanos = timeNanos;
        }
    }

    private static final ArrayList<Ping> PINGS = new ArrayList<>(MAX_PINGS);

    private SonarPingManager() {}

    /** Push newest ping (ring buffer). */
    public static void push(Vec3 worldPos) {
        long now = Util.getNanos();
        if (PINGS.size() >= MAX_PINGS) PINGS.remove(0);
        PINGS.add(new Ping(worldPos, now));
    }

    /** Remove pings older than lifeSeconds (prevents dead junk). */
    public static void prune(float lifeSeconds) {
        long now = Util.getNanos();
        long maxAgeNanos = (long) (lifeSeconds * 1_000_000_000L);

        for (int i = PINGS.size() - 1; i >= 0; i--) {
            Ping p = PINGS.get(i);
            if (now - p.timeNanos > maxAgeNanos) {
                PINGS.remove(i);
            }
        }
    }

    /** Newest-first snapshot (stable order for uniform filling). */
    public static List<Ping> snapshotNewestFirst() {
        int n = PINGS.size();
        ArrayList<Ping> out = new ArrayList<>(n);
        for (int i = n - 1; i >= 0; i--) out.add(PINGS.get(i));
        return out;
    }

    public static void clear() {
        PINGS.clear();
    }
}