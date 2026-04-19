package com.perigrine3.createcybernetics.compat.corpse;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CorpseVisualSnapshotRequestClientCache {

    private static final Set<UUID> REQUESTED = ConcurrentHashMap.newKeySet();

    private CorpseVisualSnapshotRequestClientCache() {
    }

    public static boolean markRequested(UUID corpseEntityUuid) {
        if (corpseEntityUuid == null) return false;
        return REQUESTED.add(corpseEntityUuid);
    }

    public static void clear(UUID corpseEntityUuid) {
        if (corpseEntityUuid == null) return;
        REQUESTED.remove(corpseEntityUuid);
    }

    public static void clearAll() {
        REQUESTED.clear();
    }
}