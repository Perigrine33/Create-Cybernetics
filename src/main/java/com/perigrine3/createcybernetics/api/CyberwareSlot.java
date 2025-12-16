package com.perigrine3.createcybernetics.api;

public enum CyberwareSlot {

    BRAIN(5),
    EYES(5),
    HEART(6),
    LUNGS(6),
    ORGANS(6),
    RARM(6),
    LARM(6),
    RLEG(5),
    LLEG(5),
    MUSCLE(5),
    BONE(5),
    SKIN(5);

    public final int size;

    CyberwareSlot(int size) {
        this.size = size;
    }
}
