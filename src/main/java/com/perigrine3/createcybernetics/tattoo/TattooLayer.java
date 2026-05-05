package com.perigrine3.createcybernetics.tattoo;

public enum TattooLayer {
    UNDER_CYBERWARE,
    OVER_CYBERWARE;

    public static TattooLayer byId(int id) {
        TattooLayer[] values = values();
        if (id < 0 || id >= values.length) {
            return UNDER_CYBERWARE;
        }
        return values[id];
    }

    public int id() {
        return ordinal();
    }

    public TattooLayer next() {
        return this == UNDER_CYBERWARE ? OVER_CYBERWARE : UNDER_CYBERWARE;
    }

    public String displayName() {
        return this == UNDER_CYBERWARE ? "Under Cyberware" : "Over Cyberware";
    }
}