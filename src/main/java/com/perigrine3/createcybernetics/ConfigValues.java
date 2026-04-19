package com.perigrine3.createcybernetics;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public final class ConfigValues {
    private ConfigValues() {}

    /** Base Humanity Value (50-1000). */
    public static int BASE_HUMANITY = 100;

    /** Keep Cyberware on Death. */
    public static boolean KEEP_CYBERWARE = false;

    /** Surgery Damage Scaling. */
    public static boolean SURGERY_DAMAGE_SCALING = false;

    /** Epilepsy Mode. */
    public static boolean EPILEPSY_MODE = false;

    /** Engineering Table deconstruction rolls for regular cyberware. */
    public static List<EngineeringRoll> ENGINEERING_DECONSTRUCT_ROLLS = new ArrayList<>();
    /** Engineering Table deconstruction rolls for scavenged cyberware. */
    public static List<EngineeringRoll> ENGINEERING_SCAVENGED_DECONSTRUCT_ROLLS = new ArrayList<>();
    public record EngineeringRoll(Item item, int min, int max, int weight) {
    }

    public static List<EntitySlotRoll> ENTITY_SLOT_ROLLS = new ArrayList<>();
    public static List<EntityCyberwareRoll> ENTITY_CYBERWARE_ROLLS = new ArrayList<>();
    public record EntitySlotRoll(String tableId, CyberwareSlot slot, float rollChance, int minRolls, int maxRolls) {
    }
    public record EntityCyberwareRoll(String tableId, Item item, CyberwareSlot slot, int weight, String requiredModId) {
    }
}