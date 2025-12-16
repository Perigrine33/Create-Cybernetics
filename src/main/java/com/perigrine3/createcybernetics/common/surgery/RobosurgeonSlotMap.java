package com.perigrine3.createcybernetics.common.surgery;

import com.perigrine3.createcybernetics.api.CyberwareSlot;

import java.util.EnumMap;
import java.util.Map;

public final class RobosurgeonSlotMap {

    private RobosurgeonSlotMap() {}

    private static final EnumMap<CyberwareSlot, int[]> SLOT_MAP =
            new EnumMap<>(CyberwareSlot.class);

    static {
        // Must match RobosurgeonMenu.addRobosurgeonSlots() EXACTLY.

        // HEAD
        SLOT_MAP.put(CyberwareSlot.BRAIN,  range(0, 5));   // 0-4
        SLOT_MAP.put(CyberwareSlot.EYES,   range(5, 5));   // 5-9

        // TORSO
        SLOT_MAP.put(CyberwareSlot.HEART,  range(10, 6));  // 10-15
        SLOT_MAP.put(CyberwareSlot.LUNGS,  range(16, 6));  // 16-21
        SLOT_MAP.put(CyberwareSlot.ORGANS, range(22, 6));  // 22-27

        // ARMS
        SLOT_MAP.put(CyberwareSlot.RARM,   range(28, 6));  // 28-33
        SLOT_MAP.put(CyberwareSlot.LARM,   range(34, 6));  // 34-39

        // LEGS
        SLOT_MAP.put(CyberwareSlot.RLEG,   range(40, 5));  // 40-44
        SLOT_MAP.put(CyberwareSlot.LLEG,   range(45, 5));  // 45-49

        // SKIN / STRUCTURE
        SLOT_MAP.put(CyberwareSlot.MUSCLE, range(50, 5));  // 50-54
        SLOT_MAP.put(CyberwareSlot.BONE,   range(55, 5));  // 55-59
        SLOT_MAP.put(CyberwareSlot.SKIN,   range(60, 5));  // 60-64
    }

    private static int[] range(int start, int count) {
        int[] out = new int[count];
        for (int i = 0; i < count; i++) out[i] = start + i;
        return out;
    }

    public static int toInventoryIndex(CyberwareSlot slot, int index) {
        int[] arr = SLOT_MAP.get(slot);
        if (arr == null) return -1;
        if (index < 0 || index >= arr.length) return -1;
        return arr[index];
    }

    public static CyberwareSlot getSlotForIndex(int inventoryIndex) {
        for (var entry : SLOT_MAP.entrySet()) {
            for (int i : entry.getValue()) {
                if (i == inventoryIndex) return entry.getKey();
            }
        }
        return null;
    }

    public static int mappedSize(CyberwareSlot slot) {
        int[] arr = SLOT_MAP.get(slot);
        return arr == null ? 0 : arr.length;
    }

    public static Map<CyberwareSlot, int[]> getMapping() {
        return SLOT_MAP;
    }
}
