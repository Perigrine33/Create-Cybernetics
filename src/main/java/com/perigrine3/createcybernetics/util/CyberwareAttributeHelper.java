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
        Holder<Attribute> underwaterMiningAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.SUBMERGED_MINING_SPEED.getKey())
                .orElseThrow(() -> new IllegalStateException("Underwater mining attribute not found in registry"));
        Holder<Attribute> underwaterMovementAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.WATER_MOVEMENT_EFFICIENCY.getKey())
                .orElseThrow(() -> new IllegalStateException("Underwater movement attribute not found in registry"));
        Holder<Attribute> miningSpeedAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.MINING_EFFICIENCY.getKey())
                .orElseThrow(() -> new IllegalStateException("Mining speed attribute not found in registry"));
        Holder<Attribute> crouchSpeedAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(Attributes.SNEAKING_SPEED.getKey())
                .orElseThrow(() -> new IllegalStateException("Crouch speed attribute not found in registry"));

        Holder<Attribute> xpMultiplierAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(ModAttributes.XP_GAIN_MULTIPLIER.getKey())
                .orElseThrow(() -> new IllegalStateException("XP multiplier attribute not found in registry"));
        Holder<Attribute> oreMultiplierAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(ModAttributes.ORE_DROP_MULTIPLIER.getKey())
                .orElseThrow(() -> new IllegalStateException("Ore drop multiplier attribute not found in registry"));
        Holder<Attribute> hagglingAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(ModAttributes.HAGGLING.getKey())
                .orElseThrow(() -> new IllegalStateException("Haggling attribute not found in registry"));
        Holder<Attribute> craftingOutputAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(ModAttributes.CRAFTING_OUTPUT.getKey())
                .orElseThrow(() -> new IllegalStateException("Crafting output attribute not found in registry"));
        Holder<Attribute> arrowInaccuracyAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(ModAttributes.ARROW_INACCURACY.getKey())
                .orElseThrow(() -> new IllegalStateException("Arrow inaccuracy attribute not found in registry"));
        Holder<Attribute> breedingMultiplierAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(ModAttributes.BREEDING_MULTIPLIER.getKey())
                .orElseThrow(() -> new IllegalStateException("Breeding multiplier attribute not found in registry"));
        Holder<Attribute> cropMultiplierAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(ModAttributes.CROP_MULTIPLIER.getKey())
                .orElseThrow(() -> new IllegalStateException("Crop multiplier attribute not found in registry"));
        Holder<Attribute> elytraSpeedAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(ModAttributes.ELYTRA_SPEED.getKey())
                .orElseThrow(() -> new IllegalStateException("Elytra speed attribute not found in registry"));
        Holder<Attribute> elytraHandlingAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(ModAttributes.ELYTRA_HANDLING.getKey())
                .orElseThrow(() -> new IllegalStateException("Elytra speed attribute not found in registry"));
        Holder<Attribute> insomniaAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(ModAttributes.INSOMNIA.getKey())
                .orElseThrow(() -> new IllegalStateException("Insomnia attribute not found in registry"));









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

        registerModifier("subdermalarmor_armor_1", new AttributeModifierData(armorAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "subdermal_armor_boost_1"),
                4.0, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("subdermalarmor_armor_2", new AttributeModifierData(armorAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "subdermal_armor_boost_2"),
                4.0, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("subdermalarmor_armor_3", new AttributeModifierData(armorAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "subdermal_armor_boost_3"),
                4.0, AttributeModifier.Operation.ADD_VALUE));

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

        registerModifier("claws_attack", new AttributeModifierData(attackDamageAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "claws_attack_boost"),
                5, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("sandevistan_speed", new AttributeModifierData(speedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "sandevistan_speed_boost"),
                0.5, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("sandevistan_stepheight", new AttributeModifierData(stepHeightAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "sandevistan_stepheight_boost"),
                2, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("sandevistan_jump", new AttributeModifierData(stepHeightAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "sandevistan_jump_boost"),
                2, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("cyberbrain_learn", new AttributeModifierData(xpMultiplierAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberbrain_learn_boost"),
                2, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("cyberbrain_insomnia", new AttributeModifierData(xpMultiplierAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberbrain_insomnia"),
                3, AttributeModifier.Operation.ADD_VALUE));




//CHIPWARE
        registerModifier("redshard_strength", new AttributeModifierData(attackDamageAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "redshard_strength_boost"),
                5, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("redshard_speed", new AttributeModifierData(attackSpeedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "redshard_speed_boost"),
                3, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("redshard_knockback", new AttributeModifierData(attackSpeedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "redshard_knockback_boost"),
                3, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("orangeshard_ore", new AttributeModifierData(oreMultiplierAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "orangeshard_ore_multiplier"),
                1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        registerModifier("orangeshard_mining", new AttributeModifierData(miningSpeedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "orangeshard_mining_speed"),
                0.5, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("yellowshard_haggling", new AttributeModifierData(hagglingAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "yellowshard_haggling_boost"),
                2, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("greenshard_xp", new AttributeModifierData(xpMultiplierAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "greenshard_xp_multiplier"),
                1, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("cyanshard_aim", new AttributeModifierData(arrowInaccuracyAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyanshard_aim_bot"),
                -1, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("blueshard_swim", new AttributeModifierData(swimSpeedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "blueshard_swim_speed"),
                3, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("blueshard_mining", new AttributeModifierData(underwaterMiningAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "blueshard_mining_speed"),
                1.5, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("blueshard_movement", new AttributeModifierData(underwaterMovementAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "blueshard_movement_speed"),
                4, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("blueshard_oxygen", new AttributeModifierData(oxygenBonusAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "blueshard_oxygen_boost"),
                7, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("purpleshard_crafting", new AttributeModifierData(craftingOutputAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "purpleshard_crafting_output"),
                1, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("pinkshard_breeding", new AttributeModifierData(breedingMultiplierAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "pinkshard_breeding_multiplier"),
                1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

        registerModifier("brownshard_crops", new AttributeModifierData(cropMultiplierAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "brownshard_crops_multiplier"),
                1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

        registerModifier("grayshard_speed", new AttributeModifierData(cropMultiplierAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "grayshard_speed_boost"),
                5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        registerModifier("grayshard_handling", new AttributeModifierData(cropMultiplierAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "grayshard_handling_boost"),
                5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

        registerModifier("blackshard_crouch", new AttributeModifierData(crouchSpeedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "blackshard_crouch_speed"),
                2.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        registerModifier("blackshard_sprint", new AttributeModifierData(crouchSpeedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "blackshard_crouch_sprint"),
                3.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));




        registerModifier("gemini_attackstrength", new AttributeModifierData(attackDamageAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "gemini_attackstrength_add"),
                1, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("gemini_attackspeed", new AttributeModifierData(miningSpeedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "gemini_attackspeed_add"),
                1, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("gemini_miningstrength", new AttributeModifierData(miningSpeedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "gemini_miningstrength_add"),
                1, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("gemini_speed", new AttributeModifierData(speedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "gemini_speed_add"),
                0.2, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("samson_attackstrength", new AttributeModifierData(attackDamageAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "samson_attackstrength_add"),
                2, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("samson_miningstrength", new AttributeModifierData(miningSpeedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "samson_miningstrength_add"),
                2, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("samson_durability", new AttributeModifierData(armorAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "samson_durability_add"),
                8, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("samson_watermove", new AttributeModifierData(underwaterMovementAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "samson_watermove_subtract"),
                -0.75, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("samson_weight", new AttributeModifierData(gravityAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "samson_weight_add"),
                0.1, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("eclipse_speed", new AttributeModifierData(speedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "eclipse_speed_add"),
                0.1, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("eclipse_sprintspeed", new AttributeModifierData(speedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "eclipse_sprintspeed_add"),
                0.2, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("eclipse_crouchspeed", new AttributeModifierData(crouchSpeedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "eclipse_crouchspeed_add"),
                0.5, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("spyder_crouchspeed", new AttributeModifierData(crouchSpeedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "spyder_crouchspeed_add"),
                0.5, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("spyder_jumpheight", new AttributeModifierData(jumpStrengthAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "spyder_jumpheight_add"),
                0.1, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("wingman_elytraspeed", new AttributeModifierData(elytraSpeedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "wingman_elytraspeed_add"),
                1, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("wingman_elytrahandling", new AttributeModifierData(elytraHandlingAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "wingman_elytrahandling_add"),
                4, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("aquarius_movement", new AttributeModifierData(underwaterMovementAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "aquarius_movement_add"),
                5, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("aquarius_mining", new AttributeModifierData(underwaterMiningAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "aquarius_mining_add"),
                2, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("aquarius_swim", new AttributeModifierData(swimSpeedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "aquarius_swim_add"),
                5, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("dymond_miningspeed", new AttributeModifierData(miningSpeedAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "dymond_miningspeed_add"),
                3, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("dymond_weight", new AttributeModifierData(gravityAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "dymond_weight_add"),
                0.01, AttributeModifier.Operation.ADD_VALUE));

        registerModifier("dragoon_weight", new AttributeModifierData(gravityAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "dragoon_weight_add"),
                0.1, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("dragoon_size", new AttributeModifierData(scaleAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "dragoon_size_add"),
                0.3, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("dragoon_attack", new AttributeModifierData(attackDamageAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "dragoon_attack_add"),
                7, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("dragoon_resist", new AttributeModifierData(knockbackResistAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "dragoon_resist_add"),
                5, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("dragoon_knockback", new AttributeModifierData(attackKnockbackAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "dragoon_knockback_add"),
                5, AttributeModifier.Operation.ADD_VALUE));
        registerModifier("dragoon_jump", new AttributeModifierData(jumpStrengthAttribute,
                ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "dragoon_jump_add"),
                5, AttributeModifier.Operation.ADD_VALUE));

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