package com.perigrine3.createcybernetics.compat.coldsweat;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

import java.util.Optional;

public final class ColdSweatCompat {
    private ColdSweatCompat() {}

    public static final String MODID = "cold_sweat";

    // Your modifier IDs (stable RLs)
    private static final ResourceLocation HEAT_RESIST_ID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cc_heat_resist");
    private static final ResourceLocation COLD_RESIST_ID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cc_cold_resist");

    private static final ResourceLocation HEAT_DAMPEN_ID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cc_heat_dampen");
    private static final ResourceLocation COLD_DAMPEN_ID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cc_cold_dampen");

    // Cold Sweat attributes (resolved dynamically from registry)
    private static Holder<Attribute> heatResistAttr;
    private static Holder<Attribute> coldResistAttr;
    private static Holder<Attribute> heatDampenAttr;
    private static Holder<Attribute> coldDampenAttr;

    private static boolean resolved = false;

    public static boolean isLoaded() {
        return ModList.get().isLoaded(MODID);
    }

    /* ---------------- APPLY ---------------- */

    public static void applyHeatResistance(Player player, double amount0to1) {
        if (!isLoaded() || player == null) return;
        resolveOnce();
        if (heatResistAttr == null) return;

        applyOrUpdate(player, heatResistAttr, HEAT_RESIST_ID, clamp01(amount0to1));
    }

    public static void applyColdResistance(Player player, double amount0to1) {
        if (!isLoaded() || player == null) return;
        resolveOnce();
        if (coldResistAttr == null) return;

        applyOrUpdate(player, coldResistAttr, COLD_RESIST_ID, clamp01(amount0to1));
    }

    // Dampening is typically (-infinity .. 1.0], where 1.0 is “no dampening” and lower is stronger dampening.
    // We’ll clamp only the upper bound to 1.0 to avoid breaking expected semantics.
    public static void applyHeatDampening(Player player, double dampening) {
        if (!isLoaded() || player == null) return;
        resolveOnce();
        if (heatDampenAttr == null) return;

        applyOrUpdate(player, heatDampenAttr, HEAT_DAMPEN_ID, clampDampening(dampening));
    }

    public static void applyColdDampening(Player player, double dampening) {
        if (!isLoaded() || player == null) return;
        resolveOnce();
        if (coldDampenAttr == null) return;

        applyOrUpdate(player, coldDampenAttr, COLD_DAMPEN_ID, clampDampening(dampening));
    }

    /* ---------------- CLEAR (per-effect) ---------------- */

    public static void clearHeat(Player player) {
        if (!isLoaded() || player == null) return;
        resolveOnce();

        if (heatResistAttr != null) remove(player, heatResistAttr, HEAT_RESIST_ID);
        if (heatDampenAttr != null) remove(player, heatDampenAttr, HEAT_DAMPEN_ID);
    }

    public static void clearCold(Player player) {
        if (!isLoaded() || player == null) return;
        resolveOnce();

        if (coldResistAttr != null) remove(player, coldResistAttr, COLD_RESIST_ID);
        if (coldDampenAttr != null) remove(player, coldDampenAttr, COLD_DAMPEN_ID);
    }

    /* ---------------- INTERNALS ---------------- */

    private static void applyOrUpdate(Player player, Holder<Attribute> attr, ResourceLocation id, double amount) {
        AttributeInstance inst = player.getAttribute(attr);
        if (inst == null) return;

        AttributeModifier existing = inst.getModifier(id);
        if (existing != null) {
            if (Double.compare(existing.amount(), amount) == 0 && existing.operation() == Operation.ADD_VALUE) return;
            inst.removeModifier(id);
        }

        // IMPORTANT: do NOT set base values for Cold Sweat attributes.
        inst.addPermanentModifier(new AttributeModifier(id, amount, Operation.ADD_VALUE));
    }

    private static void remove(Player player, Holder<Attribute> attr, ResourceLocation id) {
        AttributeInstance inst = player.getAttribute(attr);
        if (inst == null) return;
        if (inst.getModifier(id) != null) inst.removeModifier(id);
    }

    private static void resolveOnce() {
        if (resolved) return;
        resolved = true;

        // Primary expected IDs
        heatResistAttr = resolveAttribute("heat_resistance");
        coldResistAttr = resolveAttribute("cold_resistance");
        heatDampenAttr = resolveAttribute("heat_dampening");
        coldDampenAttr = resolveAttribute("cold_dampening");

        // Fallback fuzzy match (helps if Cold Sweat uses slightly different paths)
        if (heatResistAttr == null) heatResistAttr = resolveAttributeContains("heat_resist");
        if (coldResistAttr == null) coldResistAttr = resolveAttributeContains("cold_resist");
        if (heatDampenAttr == null) heatDampenAttr = resolveAttributeContains("heat_dampen");
        if (coldDampenAttr == null) coldDampenAttr = resolveAttributeContains("cold_dampen");
    }

    private static Holder<Attribute> resolveAttribute(String path) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MODID, path);
        Optional<Holder.Reference<Attribute>> holder = BuiltInRegistries.ATTRIBUTE.getHolder(id);
        return holder.orElse(null);
    }

    private static Holder<Attribute> resolveAttributeContains(String needle) {
        for (ResourceLocation id : BuiltInRegistries.ATTRIBUTE.keySet()) {
            if (!MODID.equals(id.getNamespace())) continue;
            if (id.getPath().contains(needle)) {
                return BuiltInRegistries.ATTRIBUTE.getHolder(id).orElse(null);
            }
        }
        return null;
    }

    private static double clamp01(double v) {
        if (v < 0.0) return 0.0;
        if (v > 1.0) return 1.0;
        return v;
    }

    private static double clampDampening(double v) {
        // Only enforce the upper bound. Strong dampening may be negative depending on CS semantics/config.
        return (v > 1.0) ? 1.0 : v;
    }
}
