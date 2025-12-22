package com.perigrine3.createcybernetics.util;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for managing attribute modifiers related to cyberware.
 * 
 * <p>This class provides a centralized system for registering, applying, and removing
 * attribute modifiers for cyberware effects on players.
 *
 * <h2>Usage Guide:</h2>
 *
 * <h3>1. Registering a New Modifier</h3>
 * Modifiers are automatically registered in the static initializer. To add a new modifier:
 * <pre>
 * static {
 *     registerModifier("your_modifier_id",
 *         new AttributeModifierData(
 *             attributeHolder,  // The attribute to modify
 *             new ResourceLocation(CreateCybernetics.MODID, "your_modifier_name"),
 *             value,           // The modification amount
 *             operation        // The operation type (ADD, MULTIPLY_BASE, etc.)
 *         ));
 * }
 * </pre>
 *
 * <h3>2. Applying Modifiers</h3>
 * To apply a modifier to a player, use:
 * <pre>
 * CyberwareAttributeHelper.applyModifier(player, "your_modifier_id");
 * </pre>
 * This is typically done in {@code onInstalled()} methods of cyberware items.
 *
 * <h3>3. Removing Modifiers</h3>
 * To remove a modifier from a player, use:
 * <pre>
 * CyberwareAttributeHelper.removeModifier(player, "your_modifier_id");
 * </pre>
 * This is typically done in {@code onRemoved()} methods of cyberware items.
 *
 * <h3>4. Checking Modifier Status</h3>
 * To check if a player has a specific modifier:
 * <pre>
 * boolean hasModifier = CyberwareAttributeHelper.hasModifier(player, "your_modifier_id");
 * </pre>
 *
 * <h2>Example Implementation:</h2>
 * <pre>
 * public class CyberLegItem implements ICyberwareItem {
 *     @Override
 *     public void onInstalled(Player player) {
 *         CyberwareAttributeHelper.applyModifier(player, "cyberleg_speed");
 *     }
 *
 *     @Override
 */
public class CyberwareAttributeHelper {
    private static final Map<String, AttributeModifierData> MODIFIER_REGISTRY = new HashMap<>();

    static {
        Holder<Attribute> maxHealthAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.MAX_HEALTH.getKey())
                .orElseThrow(() -> new IllegalStateException("Max health attribute not found in registry"));
        Holder<Attribute> armorAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.ARMOR.getKey())
                .orElseThrow(() -> new IllegalStateException("Armor attribute not found in registry"));
        Holder<Attribute> armorToughnessAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.ARMOR_TOUGHNESS.getKey())
                .orElseThrow(() -> new IllegalStateException("Armor toughness attribute not found in registry"));
        Holder<Attribute> oxygenBonusAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.OXYGEN_BONUS.getKey())
                .orElseThrow(() -> new IllegalStateException("Oxygen bonus attribute not found in registry"));
        Holder<Attribute> speedAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.MOVEMENT_SPEED.getKey())
                .orElseThrow(() -> new IllegalStateException("Movement speed attribute not found in registry"));
        Holder<Attribute> knockbackResistAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.KNOCKBACK_RESISTANCE.getKey())
                .orElseThrow(() -> new IllegalStateException("Knockback resistance attribute not found in registry"));
        Holder<Attribute> jumpStrengthAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.JUMP_STRENGTH.getKey())
                .orElseThrow(() -> new IllegalStateException("Jump strength attribute not found in registry"));
        Holder<Attribute> attackDamageAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.ATTACK_DAMAGE.getKey())
                .orElseThrow(() -> new IllegalStateException("Attack damage attribute not found in registry"));
        Holder<Attribute> attackSpeedAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.ATTACK_SPEED.getKey())
                .orElseThrow(() -> new IllegalStateException("Attack speed attribute not found in registry"));
        Holder<Attribute> attackKnockbackAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.ATTACK_KNOCKBACK.getKey())
                .orElseThrow(() -> new IllegalStateException("Attack knockback attribute not found in registry"));
        Holder<Attribute> luckAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.LUCK.getKey())
                .orElseThrow(() -> new IllegalStateException("Luck attribute not found in registry"));
        Holder<Attribute> blockReachAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.BLOCK_INTERACTION_RANGE.getKey())
                .orElseThrow(() -> new IllegalStateException("Block reach attribute not found in registry"));
        Holder<Attribute> entityReachAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.ENTITY_INTERACTION_RANGE.getKey())
                .orElseThrow(() -> new IllegalStateException("Entity reach attribute not found in registry"));
        Holder<Attribute> stepHeightAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.STEP_HEIGHT.getKey())
                .orElseThrow(() -> new IllegalStateException("Step height attribute not found in registry"));
        Holder<Attribute> swimSpeedAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.WATER_MOVEMENT_EFFICIENCY.getKey())
                .orElseThrow(() -> new IllegalStateException("Swim speed attribute not found in registry"));
        Holder<Attribute> gravityAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.GRAVITY.getKey())
                .orElseThrow(() -> new IllegalStateException("Gravity attribute not found in registry"));
        Holder<Attribute> scaleAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.SCALE.getKey())
                .orElseThrow(() -> new IllegalStateException("Scale attribute not found in registry"));
        Holder<Attribute> flyingSpeedAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.FLYING_SPEED.getKey())
                .orElseThrow(() -> new IllegalStateException("Flying speed attribute not found in registry"));
        Holder<Attribute> blockBreakSpeedAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.BLOCK_BREAK_SPEED.getKey())
                .orElseThrow(() -> new IllegalStateException("Block break speed attribute not found in registry"));



      //registerModifier("modifierID", new AttributeModifierData(attributeHolder, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID,
      //                              "modifierPathName"), int, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("cyberleg_speed", new AttributeModifierData(speedAttribute, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID,
                                "cyberleg_speed_boost"), 0.3, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("cyberleg_jump", new AttributeModifierData(jumpStrengthAttribute, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID,
                                "cyberleg_jump_boost"), 0.5, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("cyberarm_strength", new AttributeModifierData(attackDamageAttribute, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID,
                                "cyberarm_strength_boost"), 1.0, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("linear_frame_health", new AttributeModifierData(maxHealthAttribute, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID,
                                "linear_frame_health_boost"), 4.0, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("subdermalarmor_armor", new AttributeModifierData(armorAttribute, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID,
                                "subdermal_armor_boost"), 6.0, AttributeModifier.Operation.ADD_VALUE));
    }

    public static void registerModifier(String id, AttributeModifierData data) {
        MODIFIER_REGISTRY.put(id, data);
    }

    public static void applyModifier(Player player, String modifierId) {
        AttributeModifierData data = MODIFIER_REGISTRY.get(modifierId);
        if (data == null) {
            CreateCybernetics.LOGGER.error("Attempted to apply unknown modifier: " + modifierId);
            return;
        }

        removeModifier(player, modifierId);

        player.getAttribute(data.attribute).addTransientModifier(
                new AttributeModifier(data.name, data.amount, data.operation)
        );
    }

    public static void removeModifier(Player player, String modifierId) {
        AttributeModifierData data = MODIFIER_REGISTRY.get(modifierId);
        if (data == null) {
            CreateCybernetics.LOGGER.error("Attempted to remove unknown modifier: " + modifierId);
            return;
        }

        player.getAttribute(data.attribute).removeModifier(data.name);
    }

    public static boolean hasModifier(Player player, String modifierId) {
        AttributeModifierData data = MODIFIER_REGISTRY.get(modifierId);
        if (data == null) return false;

        return player.getAttribute(data.attribute).getModifier(data.name) != null;
    }

    private static class AttributeModifierData {
        final Holder<Attribute> attribute;
        final ResourceLocation name;
        final double amount;
        final AttributeModifier.Operation operation;

        AttributeModifierData(Holder<Attribute> attribute, ResourceLocation name, 
                            double amount, AttributeModifier.Operation operation) {
            this.attribute = attribute;
            this.name = name;
            this.amount = amount;
            this.operation = operation;
        }
    }
}