package com.perigrine3.createcybernetics.util;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.common.attributes.ModAttributes;
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
        Holder<Attribute> safeFallDistanceAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.SAFE_FALL_DISTANCE.getKey())
                .orElseThrow(() -> new IllegalStateException("Safe fall distance attribute not found in registry"));
        Holder<Attribute> burningTimeAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.BURNING_TIME.getKey())
                .orElseThrow(() -> new IllegalStateException("Burning time attribute not found in registry"));




        registerModifier("cyberleg_speed", new AttributeModifierData(speedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberleg_speed_boost"),
                0.01, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("cyberleg_jump", new AttributeModifierData(jumpStrengthAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberleg_jump_boost"),
                0.05, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("cyberarm_strength", new AttributeModifierData(attackDamageAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberarm_strength_boost"),
                1.0, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("cyberarm_blockbreak", new AttributeModifierData(attackDamageAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberarm_blockbreak_speed"),
                0.5, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("linear_frame_health", new AttributeModifierData(maxHealthAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "linear_frame_health_boost"),
                8.0, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("linear_frame_knockback_resist", new AttributeModifierData(knockbackResistAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "linear_frame_knockback_resistance"),
                1.0, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("linear_frame_blockbreak", new AttributeModifierData(blockBreakSpeedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "linear_frame_blockbreak_speed"),
                1.0, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("linear_frame_speed", new AttributeModifierData(speedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "linear_frame_speed_stifle"),
                -0.02, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("subdermalarmor_armor", new AttributeModifierData(armorAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "subdermal_armor_boost"),
                6.0, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("bonelacing_health_1", new AttributeModifierData(maxHealthAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "bonelacing_health_boost_1"),
                4.0, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("bonelacing_health_2", new AttributeModifierData(maxHealthAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "bonelacing_health_boost_2"),
                4.0, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("bonelacing_health_3", new AttributeModifierData(maxHealthAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "bonelacing_health_boost_3"),
                4.0, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("boneflex_fall_1", new AttributeModifierData(safeFallDistanceAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "boneflex_fall_save_1"),
                3, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("boneflex_fall_2", new AttributeModifierData(safeFallDistanceAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "boneflex_fall_save_2"),
                3, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("boneflex_fall_3", new AttributeModifierData(safeFallDistanceAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "boneflex_fall_save_3"),
                3, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("fall_bracer_fall_1", new AttributeModifierData(safeFallDistanceAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "fall_bracer_fall_save_1"),
                11, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("fall_bracer_fall_2", new AttributeModifierData(safeFallDistanceAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "fall_bracer_fall_save_2"),
                11, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("pneumatic_wrist_block", new AttributeModifierData(blockReachAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "pneumatic_wrist_block_reach"),
                2.0, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("pneumatic_wrist_entity", new AttributeModifierData(entityReachAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "pneumatic_wrist_entity_reach"),
                2.0, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("pneumatic_wrist_knockback", new AttributeModifierData(attackKnockbackAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "pneumatic_wrist_knockback_bonus"),
                2.0, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("oxygen_tank_oxygen", new AttributeModifierData(oxygenBonusAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "oxygen_tank_oxygen_bonus"),
                10.0, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("propeller_swim_1", new AttributeModifierData(swimSpeedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "propeller_swim_speed_1"),
                3, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("propeller_swim_2", new AttributeModifierData(swimSpeedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "propeller_swim_speed_2"),
                3, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("isothermal_burning", new AttributeModifierData(burningTimeAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "isothermal_burning_time"),
                -0.95, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("ravager_tendons_size", new AttributeModifierData(scaleAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "ravager_tendons_size_increase"),
                0.2, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("ravager_tendons_strength", new AttributeModifierData(attackDamageAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "ravager_tendons_attack_boost"),
                5, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("ravager_tendons_knockback_resist", new AttributeModifierData(knockbackResistAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "ravager_tendons_knockback_resistance"),
                3, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("ravager_tendons_knockback", new AttributeModifierData(attackKnockbackAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "ravager_tendons_attack_knockback"),
                3, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("hyperoxygenation_speed_1", new AttributeModifierData(speedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "hyperoxygenation_speed_boost_1"),
                0.03, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("hyperoxygenation_speed_2", new AttributeModifierData(speedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "hyperoxygenation_speed_boost_2"),
                0.03, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("hyperoxygenation_speed_3", new AttributeModifierData(speedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "hyperoxygenation_speed_boost_3"),
                0.03, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("synthmuscle_strength", new AttributeModifierData(attackDamageAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "synthmuscle_strength_boost"),
                2, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("synthmuscle_size", new AttributeModifierData(scaleAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "synthmuscle_size_boost"),
                0.1, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("synthmuscle_knockback_resist", new AttributeModifierData(knockbackResistAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "synthmuscle_knockback_resistance"),
                1.5, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("synthmuscle_knockback", new AttributeModifierData(attackKnockbackAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "synthmuscle_attack_knockback"),
                1.5, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("synthmuscle_speed", new AttributeModifierData(attackKnockbackAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "synthmuscle_speed_boost"),
                0.15, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("synthmuscle_jump", new AttributeModifierData(jumpStrengthAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "synthmuscle_jump_boost"),
                0.1, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("reinforced_knuckles_damage", new AttributeModifierData(attackDamageAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "reinforced_knuckles_damage_boost"),
                3.0, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("calves_sprint", new AttributeModifierData(speedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "calves_sprint_boost"),
                0.05, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("gyrobladder_speed", new AttributeModifierData(speedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "gyrobladder_speed_stifle"),
                -0.75, AttributeModifier.Operation.ADD_VALUE));

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

        player.getAttribute(data.attribute).addOrReplacePermanentModifier(
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