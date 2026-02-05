package com.perigrine3.createcybernetics.client.skin;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.compat.ModCompats;
import com.perigrine3.createcybernetics.event.custom.FullBorgHandler;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.HumanoidArm;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Central manager for all skin modifications in the game.
 * This class determines which skin modifiers should be active for each player.
 *
 * How to Add New Skin Modifications:
 * 1. Define your texture constant:
 *    private static final ResourceLocation YOUR_SKIN_TEXTURE = 
 *        new ResourceLocation(CreateCybernetics.MODID, "textures/entity/your_texture.png");
 *
 * 2. Create a SkinModifier instance:
 *    private static final SkinModifier YOUR_MODIFIER = new SkinModifier(
 *        YOUR_SKIN_TEXTURE,
 *        FastColor.ARGB32.color(255, r, g, b), // Color tint (optional)
 *        hideVanillaLayers // true/false
 *    );
 *
 * 3. Add your condition in getPlayerSkinState:
 *    // For cyberware-based modifications:
 *    if (data.hasAnyTagged(ModTags.Items.YOUR_ITEMS, CyberwareSlot.YOUR_SLOT)) {
 *        state.addModifier(YOUR_MODIFIER);
 *    }
 *
 *    // For effect-based modifications:
 *    if (player.hasEffect(ModEffects.YOUR_EFFECT)) {
 *        state.addModifier(YOUR_MODIFIER);
 *    }
 *
 *    // For conditional modifications:
 *    if (shouldApplyCondition(player)) {
 *        state.addModifier(YOUR_MODIFIER);
 *    }
 *
 * Example Implementations:
 * 1. Metallic Skin Overlay:
 *    private static final ResourceLocation METALLIC_SKIN = 
 *        new ResourceLocation(CreateCybernetics.MODID, "textures/entity/metallic_skin.png");
 *    private static final SkinModifier METALLIC_MODIFIER = new SkinModifier(
 *        METALLIC_SKIN,
 *        FastColor.ARGB32.color(255, 192, 192, 192),
 *        false
 *    );
 *
 * 2. Glowing Circuits:
 *    private static final ResourceLocation CIRCUIT_PATTERN = 
 *        new ResourceLocation(CreateCybernetics.MODID, "textures/entity/circuit_pattern.png");
 *    private static final SkinModifier CIRCUIT_MODIFIER = new SkinModifier(
 *        CIRCUIT_PATTERN,
 *        FastColor.ARGB32.color(255, 0, 255, 255),
 *        false
 *    );
 *
 * Important Notes:
 * - Modifiers are rendered in the order they're added to the state
 * - Later modifiers will render on top of earlier ones
 * - Use alpha channels in textures for partial transparency
 * - Color tints can be used to create variations of the same texture
 * - Consider performance when adding multiple modifiers
 */
public class SkinModifierManager {
    private static final Map<UUID, SkinModifierState> PLAYER_STATES = new HashMap<>();

    //INTERCHANGEABLES
    private static final ResourceLocation MISSING_SKIN_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/playermuscles_wide.png");
    private static final ResourceLocation CYBEREYES_PRIMARY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/cybereyes_dye_primary.png");
    private static final ResourceLocation CYBEREYES_SECONDARY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/cybereyes_dye_secondary.png");
    private static final ResourceLocation RIGHT_CYBERLEG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/right_cyberleg.png");
    private static final ResourceLocation COPPER_PLATED_RIGHT_CYBERLEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/copper_rightleg.png");
    private static final ResourceLocation IRON_PLATED_RIGHT_CYBERLEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/iron_rightleg.png");
    private static final ResourceLocation GOLD_PLATED_RIGHT_CYBERLEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/gold_rightleg.png");
    private static final ResourceLocation RIGHT_CYBERLEG_PRIMARY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/right_cyberleg_dye_primary.png");
    private static final ResourceLocation RIGHT_CYBERLEG_SECONDARY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/right_cyberleg_dye_secondary.png");
    private static final ResourceLocation LEFT_CYBERLEG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/left_cyberleg.png");
    private static final ResourceLocation COPPER_PLATED_LEFT_CYBERLEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/copper_leftleg.png");
    private static final ResourceLocation IRON_PLATED_LEFT_CYBERLEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/iron_leftleg.png");
    private static final ResourceLocation GOLD_PLATED_LEFT_CYBERLEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/gold_leftleg.png");
    private static final ResourceLocation LEFT_CYBERLEG_PRIMARY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/left_cyberleg_dye_primary.png");
    private static final ResourceLocation LEFT_CYBERLEG_SECONDARY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/left_cyberleg_dye_secondary.png");
    private static final ResourceLocation POLAR_BEAR_FUR_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/polar_bear_fur.png");
    private static final ResourceLocation SPINAL_INJECTOR_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/spinal_injector.png");
    private static final ResourceLocation SPINAL_INJECTOR_HIGHLIGHT_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/spinal_injector_highlight.png");
    private static final ResourceLocation SANDEVISTAN_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/sandevistan.png");
    private static final ResourceLocation SANDEVISTAN_HIGHLIGHT_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/sandevistan_highlight.png");
    private static final ResourceLocation DEPLOYABLE_ELYTRA_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/deployable_elytra.png");
    private static final ResourceLocation DEPLOYABLE_ELYTRA_HIGHLIGHT_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/deployable_elytra_highlight.png");
    private static final ResourceLocation CYBERDECK_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/cyberdeck.png");
    private static final ResourceLocation GILLS_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/gills.png");
    private static final ResourceLocation CHIPWARE_INACTIVE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/chipware_inactive.png");
    private static final ResourceLocation CHIPWARE_ACTIVE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/chipware_active.png");
    private static final ResourceLocation FURNACE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/furnace.png");
    private static final ResourceLocation FURNACE_ACTIVE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/furnace_lit.png");
    private static final ResourceLocation FURNACE_HIGHLIGHT =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/furnace_lit_highlight.png");
    private static final ResourceLocation SPIDER_EYES =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/spider_eyes.png");

    private static final ResourceLocation SAMSON_EYES_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/samson_eyes_dyed.png");
    private static final ResourceLocation ECLIPSE_EYES_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/eclipse_eyes_dyed.png");
    private static final ResourceLocation SPYDER_EYES_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/spyder_eyes_dyed.png");
    private static final ResourceLocation AQUARIUS_EYES_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/aquarius_eyes_dyed.png");
    private static final ResourceLocation DYMOND_EYES_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/dymond_eyes_dyed.png");
    private static final ResourceLocation DRAGOON_EYES_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/dragoon_eyes_dyed.png");
    private static final ResourceLocation COPERNICUS_EYES_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/copernicus_eyes_dyed.png");
    private static final ResourceLocation GENOS_EYES_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/genos_eyes_dyed.png");
    private static final ResourceLocation GENOS_HIGHLIGHT =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/genos_highlight.png");

    //WIDE VARIANTS
    private static final ResourceLocation LEFT_CYBERARM_TEXTURE_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/left_cyberarm_wide.png");
    private static final ResourceLocation COPPER_PLATED_LEFT_CYBERARM_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/copper_leftarm_wide.png");
    private static final ResourceLocation IRON_PLATED_LEFT_CYBERARM_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/iron_leftarm_wide.png");
    private static final ResourceLocation GOLD_PLATED_LEFT_CYBERARM_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/gold_leftarm_wide.png");
    private static final ResourceLocation LEFT_CYBERARM_PRIMARY_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/left_cyberarm_dye_primary_wide.png");
    private static final ResourceLocation LEFT_CYBERARM_SECONDARY_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/left_cyberarm_dye_secondary_wide.png");
    private static final ResourceLocation RIGHT_CYBERARM_TEXTURE_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/right_cyberarm_wide.png");
    private static final ResourceLocation COPPER_PLATED_RIGHT_CYBERARM_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/copper_rightarm_wide.png");
    private static final ResourceLocation IRON_PLATED_RIGHT_CYBERARM_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/iron_rightarm_wide.png");
    private static final ResourceLocation GOLD_PLATED_RIGHT_CYBERARM_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/gold_rightarm_wide.png");
    private static final ResourceLocation RIGHT_CYBERARM_PRIMARY_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/right_cyberarm_dye_primary_wide.png");
    private static final ResourceLocation RIGHT_CYBERARM_SECONDARY_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/right_cyberarm_dye_secondary_wide.png");
    private static final ResourceLocation KNUCKLES_LARM_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/knuckles_larm_wide.png");
    private static final ResourceLocation KNUCKLES_RARM_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/knuckles_rarm_wide.png");
    private static final ResourceLocation SYNTHSKIN_TEXTURE_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/synthskin_wide.png");
    private static final ResourceLocation NETHERPLATED_SKIN_TEXTURE_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/isothermal_skin_wide.png");
    private static final ResourceLocation FIRESTARTER_LARM_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/firestarter_larm_wide.png");
    private static final ResourceLocation FIRESTARTER_RARM_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/firestarter_rarm_wide.png");
    private static final ResourceLocation FLYWHEEL_LARM_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/flywheel_larm_wide.png");
    private static final ResourceLocation FLYWHEEL_RARM_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/flywheel_rarm_wide.png");
    private static final ResourceLocation MANASKIN_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/manaskin.png");

    private static final ResourceLocation SAMSON_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/samson_wide.png");
    private static final ResourceLocation SAMSON_WIDE_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/samson_wide_dyed.png");
    private static final ResourceLocation ECLIPSE_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/eclipse_wide.png");
    private static final ResourceLocation ECLIPSE_WIDE_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/eclipse_wide_dyed.png");
    private static final ResourceLocation SPYDER_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/spyder_wide.png");
    private static final ResourceLocation SPYDER_WIDE_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/spyder_wide_dyed.png");
    private static final ResourceLocation WINGMAN_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/wingman_wide.png");
    private static final ResourceLocation WINGMAN_WIDE_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/wingman_wide_dyed.png");
    private static final ResourceLocation AQUARIUS_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/aquarius_wide.png");
    private static final ResourceLocation AQUARIUS_WIDE_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/aquarius_wide_dyed.png");
    private static final ResourceLocation DYMOND_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/dymond_wide.png");
    private static final ResourceLocation DYMOND_WIDE_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/dymond_wide_dyed.png");
    private static final ResourceLocation DRAGOON_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/dragoon_wide.png");
    private static final ResourceLocation DRAGOON_WIDE_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/dragoon_wide_dyed.png");
    private static final ResourceLocation COPERNICUS_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/copernicus_wide.png");
    private static final ResourceLocation COPERNICUS_WIDE_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/copernicus_wide_dyed.png");
    private static final ResourceLocation GENOS_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/genos_wide.png");
    private static final ResourceLocation GENOS_WIDE_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/genos_wide_dyed.png");

    //SLIM VARIANTS
    private static final ResourceLocation LEFT_CYBERARM_TEXTURE_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/left_cyberarm_slim.png");
    private static final ResourceLocation COPPER_PLATED_LEFT_CYBERARM_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/copper_leftarm_slim.png");
    private static final ResourceLocation IRON_PLATED_LEFT_CYBERARM_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/iron_leftarm_slim.png");
    private static final ResourceLocation GOLD_PLATED_LEFT_CYBERARM_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/gold_leftarm_slim.png");
    private static final ResourceLocation LEFT_CYBERARM_PRIMARY_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/left_cyberarm_dye_primary_slim.png");
    private static final ResourceLocation LEFT_CYBERARM_SECONDARY_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/left_cyberarm_dye_secondary_slim.png");
    private static final ResourceLocation RIGHT_CYBERARM_TEXTURE_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/right_cyberarm_slim.png");
    private static final ResourceLocation COPPER_PLATED_RIGHT_CYBERARM_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/copper_rightarm_slim.png");
    private static final ResourceLocation IRON_PLATED_RIGHT_CYBERARM_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/iron_rightarm_slim.png");
    private static final ResourceLocation GOLD_PLATED_RIGHT_CYBERARM_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/gold_rightarm_slim.png");
    private static final ResourceLocation RIGHT_CYBERARM_PRIMARY_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/right_cyberarm_dye_primary_slim.png");
    private static final ResourceLocation RIGHT_CYBERARM_SECONDARY_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/right_cyberarm_dye_secondary_slim.png");
    private static final ResourceLocation KNUCKLES_LARM_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/knuckles_larm_slim.png");
    private static final ResourceLocation KNUCKLES_RARM_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/knuckles_rarm_slim.png");
    private static final ResourceLocation SYNTHSKIN_TEXTURE_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/synthskin_slim.png");
    private static final ResourceLocation NETHERPLATED_SKIN_TEXTURE_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/isothermal_skin_slim.png");
    private static final ResourceLocation FIRESTARTER_LARM_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/firestarter_larm_slim.png");
    private static final ResourceLocation FIRESTARTER_RARM_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/firestarter_rarm_slim.png");
    private static final ResourceLocation FLYWHEEL_LARM_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/flywheel_larm_slim.png");
    private static final ResourceLocation FLYWHEEL_RARM_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/flywheel_rarm_slim.png");
    private static final ResourceLocation MANASKIN_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/manaskin_slim.png");

    private static final ResourceLocation SAMSON_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/samson_slim.png");
    private static final ResourceLocation SAMSON_SLIM_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/samson_slim_dyed.png");
    private static final ResourceLocation ECLIPSE_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/eclipse_slim.png");
    private static final ResourceLocation ECLIPSE_SLIM_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/eclipse_slim_dyed.png");
    private static final ResourceLocation SPYDER_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/spyder_slim.png");
    private static final ResourceLocation SPYDER_SLIM_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/spyder_slim_dyed.png");
    private static final ResourceLocation WINGMAN_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/wingman_slim.png");
    private static final ResourceLocation WINGMAN_SLIM_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/wingman_slim_dyed.png");
    private static final ResourceLocation AQUARIUS_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/aquarius_slim.png");
    private static final ResourceLocation AQUARIUS_SLIM_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/aquarius_slim_dyed.png");
    private static final ResourceLocation DYMOND_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/dymond_slim.png");
    private static final ResourceLocation DYMOND_SLIM_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/dymond_slim_dyed.png");
    private static final ResourceLocation DRAGOON_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/dragoon_slim.png");
    private static final ResourceLocation DRAGOON_SLIM_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/dragoon_slim_dyed.png");
    private static final ResourceLocation COPERNICUS_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/copernicus_slim.png");
    private static final ResourceLocation COPERNICUS_SLIM_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/copernicus_slim_dyed.png");
    private static final ResourceLocation GENOS_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/genos_slim.png");
    private static final ResourceLocation GENOS_SLIM_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/genos_slim_dyed.png");



// CYBERWARE TRIMS
    private static final ResourceLocation BOLT_LEFT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/bolt_left_leg.png");
    private static final ResourceLocation BOLT_LEFT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/bolt_left_slim.png");
    private static final ResourceLocation BOLT_LEFT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/bolt_left_wide.png");
    private static final ResourceLocation BOLT_RIGHT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/bolt_right_leg.png");
    private static final ResourceLocation BOLT_RIGHT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/bolt_right_slim.png");
    private static final ResourceLocation BOLT_RIGHT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/bolt_right_wide.png");
    private static final ResourceLocation BOLT_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/bolt_full_body.png");
    private static final ResourceLocation BOLT_SLIM_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/bolt_slim_full_body.png");

    private static final ResourceLocation COAST_LEFT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/coast_left_leg.png");
    private static final ResourceLocation COAST_LEFT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/coast_left_slim.png");
    private static final ResourceLocation COAST_LEFT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/coast_left_wide.png");
    private static final ResourceLocation COAST_RIGHT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/coast_right_leg.png");
    private static final ResourceLocation COAST_RIGHT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/coast_right_slim.png");
    private static final ResourceLocation COAST_RIGHT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/coast_right_wide.png");
    private static final ResourceLocation COAST_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/coast_full_body.png");
    private static final ResourceLocation COAST_SLIM_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/coast_slim_full_body.png");

    private static final ResourceLocation DUNE_LEFT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/dune_left_leg.png");
    private static final ResourceLocation DUNE_LEFT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/dune_left_slim.png");
    private static final ResourceLocation DUNE_LEFT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/dune_left_wide.png");
    private static final ResourceLocation DUNE_RIGHT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/dune_right_leg.png");
    private static final ResourceLocation DUNE_RIGHT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/dune_right_slim.png");
    private static final ResourceLocation DUNE_RIGHT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/dune_right_wide.png");
    private static final ResourceLocation DUNE_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/dune_full_body.png");
    private static final ResourceLocation DUNE_SLIM_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/dune_slim_full_body.png");

    private static final ResourceLocation EYE_LEFT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/eye_left_leg.png");
    private static final ResourceLocation EYE_LEFT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/eye_left_slim.png");
    private static final ResourceLocation EYE_LEFT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/eye_left_wide.png");
    private static final ResourceLocation EYE_RIGHT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/eye_right_leg.png");
    private static final ResourceLocation EYE_RIGHT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/eye_right_slim.png");
    private static final ResourceLocation EYE_RIGHT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/eye_right_wide.png");
    private static final ResourceLocation EYE_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/eye_full_body.png");
    private static final ResourceLocation EYE_SLIM_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/eye_slim_full_body.png");

    private static final ResourceLocation FLOW_LEFT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/flow_left_leg.png");
    private static final ResourceLocation FLOW_LEFT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/flow_left_slim.png");
    private static final ResourceLocation FLOW_LEFT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/flow_left_wide.png");
    private static final ResourceLocation FLOW_RIGHT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/flow_right_leg.png");
    private static final ResourceLocation FLOW_RIGHT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/flow_right_slim.png");
    private static final ResourceLocation FLOW_RIGHT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/flow_right_wide.png");
    private static final ResourceLocation FLOW_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/flow_full_body.png");
    private static final ResourceLocation FLOW_SLIM_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/flow_slim_full_body.png");

    private static final ResourceLocation HOST_LEFT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/host_left_leg.png");
    private static final ResourceLocation HOST_LEFT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/host_left_slim.png");
    private static final ResourceLocation HOST_LEFT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/host_left_wide.png");
    private static final ResourceLocation HOST_RIGHT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/host_right_leg.png");
    private static final ResourceLocation HOST_RIGHT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/host_right_slim.png");
    private static final ResourceLocation HOST_RIGHT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/host_right_wide.png");
    private static final ResourceLocation HOST_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/host_full_body.png");
    private static final ResourceLocation HOST_SLIM_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/host_slim_full_body.png");

    private static final ResourceLocation RAISER_LEFT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/raiser_left_leg.png");
    private static final ResourceLocation RAISER_LEFT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/raiser_left_slim.png");
    private static final ResourceLocation RAISER_LEFT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/raiser_left_wide.png");
    private static final ResourceLocation RAISER_RIGHT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/raiser_right_leg.png");
    private static final ResourceLocation RAISER_RIGHT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/raiser_right_slim.png");
    private static final ResourceLocation RAISER_RIGHT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/raiser_right_wide.png");
    private static final ResourceLocation RAISER_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/raiser_full_body.png");
    private static final ResourceLocation RAISER_SLIM_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/raiser_slim_full_body.png");

    private static final ResourceLocation RIB_LEFT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/rib_left_leg.png");
    private static final ResourceLocation RIB_LEFT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/rib_left_slim.png");
    private static final ResourceLocation RIB_LEFT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/rib_left_wide.png");
    private static final ResourceLocation RIB_RIGHT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/rib_right_leg.png");
    private static final ResourceLocation RIB_RIGHT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/rib_right_slim.png");
    private static final ResourceLocation RIB_RIGHT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/rib_right_wide.png");
    private static final ResourceLocation RIB_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/rib_full_body.png");
    private static final ResourceLocation RIB_SLIM_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/rib_slim_full_body.png");

    private static final ResourceLocation SENTRY_LEFT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/sentry_left_leg.png");
    private static final ResourceLocation SENTRY_LEFT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/sentry_left_slim.png");
    private static final ResourceLocation SENTRY_LEFT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/sentry_left_wide.png");
    private static final ResourceLocation SENTRY_RIGHT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/sentry_right_leg.png");
    private static final ResourceLocation SENTRY_RIGHT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/sentry_right_slim.png");
    private static final ResourceLocation SENTRY_RIGHT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/sentry_right_wide.png");
    private static final ResourceLocation SENTRY_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/sentry_full_body.png");
    private static final ResourceLocation SENTRY_SLIM_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/sentry_slim_full_body.png");

    private static final ResourceLocation SHAPER_LEFT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/shaper_left_leg.png");
    private static final ResourceLocation SHAPER_LEFT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/shaper_left_slim.png");
    private static final ResourceLocation SHAPER_LEFT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/shaper_left_wide.png");
    private static final ResourceLocation SHAPER_RIGHT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/shaper_right_leg.png");
    private static final ResourceLocation SHAPER_RIGHT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/shaper_right_slim.png");
    private static final ResourceLocation SHAPER_RIGHT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/shaper_right_wide.png");
    private static final ResourceLocation SHAPER_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/shaper_full_body.png");
    private static final ResourceLocation SHAPER_SLIM_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/shaper_slim_full_body.png");

    private static final ResourceLocation SILENCE_LEFT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/silence_left_leg.png");
    private static final ResourceLocation SILENCE_LEFT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/silence_left_slim.png");
    private static final ResourceLocation SILENCE_LEFT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/silence_left_wide.png");
    private static final ResourceLocation SILENCE_RIGHT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/silence_right_leg.png");
    private static final ResourceLocation SILENCE_RIGHT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/silence_right_slim.png");
    private static final ResourceLocation SILENCE_RIGHT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/silence_right_wide.png");
    private static final ResourceLocation SILENCE_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/silence_full_body.png");
    private static final ResourceLocation SILENCE_SLIM_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/silence_slim_full_body.png");

    private static final ResourceLocation SNOUT_LEFT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/snout_left_leg.png");
    private static final ResourceLocation SNOUT_LEFT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/snout_left_slim.png");
    private static final ResourceLocation SNOUT_LEFT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/snout_left_wide.png");
    private static final ResourceLocation SNOUT_RIGHT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/snout_right_leg.png");
    private static final ResourceLocation SNOUT_RIGHT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/snout_right_slim.png");
    private static final ResourceLocation SNOUT_RIGHT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/snout_right_wide.png");
    private static final ResourceLocation SNOUT_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/snout_full_body.png");
    private static final ResourceLocation SNOUT_SLIM_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/snout_slim_full_body.png");

    private static final ResourceLocation SPIRE_LEFT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/spire_left_leg.png");
    private static final ResourceLocation SPIRE_LEFT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/spire_left_slim.png");
    private static final ResourceLocation SPIRE_LEFT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/spire_left_wide.png");
    private static final ResourceLocation SPIRE_RIGHT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/spire_right_leg.png");
    private static final ResourceLocation SPIRE_RIGHT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/spire_right_slim.png");
    private static final ResourceLocation SPIRE_RIGHT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/spire_right_wide.png");
    private static final ResourceLocation SPIRE_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/spire_full_body.png");
    private static final ResourceLocation SPIRE_SLIM_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/spire_slim_full_body.png");

    private static final ResourceLocation TIDE_LEFT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/tide_left_leg.png");
    private static final ResourceLocation TIDE_LEFT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/tide_left_slim.png");
    private static final ResourceLocation TIDE_LEFT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/tide_left_wide.png");
    private static final ResourceLocation TIDE_RIGHT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/tide_right_leg.png");
    private static final ResourceLocation TIDE_RIGHT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/tide_right_slim.png");
    private static final ResourceLocation TIDE_RIGHT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/tide_right_wide.png");
    private static final ResourceLocation TIDE_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/tide_full_body.png");
    private static final ResourceLocation TIDE_SLIM_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/tide_slim_full_body.png");

    private static final ResourceLocation VEX_LEFT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/vex_left_leg.png");
    private static final ResourceLocation VEX_LEFT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/vex_left_slim.png");
    private static final ResourceLocation VEX_LEFT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/vex_left_wide.png");
    private static final ResourceLocation VEX_RIGHT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/vex_right_leg.png");
    private static final ResourceLocation VEX_RIGHT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/vex_right_slim.png");
    private static final ResourceLocation VEX_RIGHT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/vex_right_wide.png");
    private static final ResourceLocation VEX_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/vex_full_body.png");
    private static final ResourceLocation VEX_SLIM_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/vex_slim_full_body.png");

    private static final ResourceLocation WARD_LEFT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/ward_left_leg.png");
    private static final ResourceLocation WARD_LEFT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/ward_left_slim.png");
    private static final ResourceLocation WARD_LEFT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/ward_left_wide.png");
    private static final ResourceLocation WARD_RIGHT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/ward_right_leg.png");
    private static final ResourceLocation WARD_RIGHT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/ward_right_slim.png");
    private static final ResourceLocation WARD_RIGHT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/ward_right_wide.png");
    private static final ResourceLocation WARD_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/ward_full_body.png");
    private static final ResourceLocation WARD_SLIM_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/ward_slim_full_body.png");

    private static final ResourceLocation WAYFINDER_LEFT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/wayfinder_left_leg.png");
    private static final ResourceLocation WAYFINDER_LEFT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/wayfinder_left_slim.png");
    private static final ResourceLocation WAYFINDER_LEFT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/wayfinder_left_wide.png");
    private static final ResourceLocation WAYFINDER_RIGHT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/wayfinder_right_leg.png");
    private static final ResourceLocation WAYFINDER_RIGHT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/wayfinder_right_slim.png");
    private static final ResourceLocation WAYFINDER_RIGHT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/wayfinder_right_wide.png");
    private static final ResourceLocation WAYFINDER_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/wayfinder_full_body.png");
    private static final ResourceLocation WAYFINDER_SLIM_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/wayfinder_slim_full_body.png");

    private static final ResourceLocation WILD_LEFT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/wild_left_leg.png");
    private static final ResourceLocation WILD_LEFT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/wild_left_slim.png");
    private static final ResourceLocation WILD_LEFT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/wild_left_wide.png");
    private static final ResourceLocation WILD_RIGHT_LEG =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/wild_right_leg.png");
    private static final ResourceLocation WILD_RIGHT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/wild_right_slim.png");
    private static final ResourceLocation WILD_RIGHT_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/wild_right_wide.png");
    private static final ResourceLocation WILD_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/wild_full_body.png");
    private static final ResourceLocation WILD_SLIM_FULL_BODY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/trims/wild_slim_full_body.png");



    public static SkinModifierState getPlayerSkinState(AbstractClientPlayer player) {
        if (!player.hasData(ModAttachments.CYBERWARE)) return null;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return null;

        UUID playerId = player.getUUID();
        SkinModifierState state = PLAYER_STATES.computeIfAbsent(playerId, k -> new SkinModifierState());
        state.clearModifiers();



// CYBEREYES
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES)) {
            if (data.isDyed(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES)) {
                int tint = data.dyeColor(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES);
                state.addModifier(new SkinModifier(CYBEREYES_PRIMARY, CYBEREYES_PRIMARY,
                        tint, false));
                state.addHighlight(new SkinHighlight(CYBEREYES_PRIMARY, CYBEREYES_PRIMARY,
                        tint, true, true));
            }
        }
// SYNTHSKIN
        if (data.hasSpecificItem(ModItems.SKINUPGRADES_SYNTHSKIN.get(), CyberwareSlot.SKIN)) {
            int alpha = 100;
            int tint = FastColor.ARGB32.color(alpha, 255, 255, 255);

            state.removeModifier(new SkinModifier(LEFT_CYBERLEG_TEXTURE, LEFT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(RIGHT_CYBERLEG_TEXTURE, RIGHT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(LEFT_CYBERARM_TEXTURE_WIDE, LEFT_CYBERARM_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(RIGHT_CYBERARM_TEXTURE_WIDE, RIGHT_CYBERARM_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(CYBEREYES_PRIMARY, CYBEREYES_PRIMARY));
            state.clearModifiers();

            if (data.hasSpecificItem(ModItems.WETWARE_POLARBEARFUR.get(), CyberwareSlot.SKIN)) {
                state.addModifier(new SkinModifier(POLAR_BEAR_FUR_TEXTURE, POLAR_BEAR_FUR_TEXTURE));
            }
            state.addModifier(new SkinModifier(SYNTHSKIN_TEXTURE_WIDE, SYNTHSKIN_TEXTURE_SLIM,
                    tint, false, false, EnumSet.noneOf(SkinModifier.HideVanilla.class), EnumSet.noneOf(HumanoidArm.class), true));
            return state;
        }
// GILLS
        if (data.hasSpecificItem(ModItems.WETWARE_WATERBREATHINGLUNGS.get(), CyberwareSlot.LUNGS)) {
            state.addModifier(new SkinModifier(GILLS_TEXTURE, GILLS_TEXTURE,
                    0xFFFFFFFF, false));
        }
// MISSING SKIN
        if (!data.hasAnyTagged(ModTags.Items.SKIN_ITEMS, CyberwareSlot.SKIN)) {
            state.addModifier(new SkinModifier(MISSING_SKIN_TEXTURE, MISSING_SKIN_TEXTURE,
                    0xFFFFFFFF, true));
        }
// LEFT CYBERLEG
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG)) {
            state.addModifier(new SkinModifier(LEFT_CYBERLEG_TEXTURE, LEFT_CYBERLEG_TEXTURE,
                    0xFFFFFFFF, false, EnumSet.of(SkinModifier.HideVanilla.LEFT_PANTS)));

            if (data.isDyed(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG)) {
                int tint = data.dyeColor(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG);
                state.addModifier(new SkinModifier(LEFT_CYBERLEG_PRIMARY, LEFT_CYBERLEG_PRIMARY,
                        tint, false, EnumSet.of(SkinModifier.HideVanilla.LEFT_PANTS)));
            }

            if (data.isTrimmed(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG)) {
                ResourceLocation patternId = data.trimPatternId(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG);
                int tint = data.trimColor(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG);

                ResourceLocation tex = resolveTrimOverlay(patternId, true, Limb.LEG, false);
                if (tex != null) {
                    state.addModifier(new SkinModifier(tex, tex, tint, false,
                            EnumSet.of(SkinModifier.HideVanilla.LEFT_PANTS)));
                }
            }
        }
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG_COPPERPLATED.get(), CyberwareSlot.LLEG)) {
            state.addModifier(new SkinModifier(COPPER_PLATED_LEFT_CYBERLEG, COPPER_PLATED_LEFT_CYBERLEG,
                    0xFFFFFFFF, false, EnumSet.of(SkinModifier.HideVanilla.LEFT_PANTS)));

            if (data.isTrimmed(ModItems.BASECYBERWARE_LEFTLEG_COPPERPLATED.get(), CyberwareSlot.LLEG)) {
                ResourceLocation patternId = data.trimPatternId(ModItems.BASECYBERWARE_LEFTLEG_COPPERPLATED.get(), CyberwareSlot.LLEG);
                int tint = data.trimColor(ModItems.BASECYBERWARE_LEFTLEG_COPPERPLATED.get(), CyberwareSlot.LLEG);

                ResourceLocation tex = resolveTrimOverlay(patternId, true, Limb.LEG, false);
                if (tex != null) {
                    state.addModifier(new SkinModifier(tex, tex, tint, false,
                            EnumSet.of(SkinModifier.HideVanilla.LEFT_PANTS)));
                }
            }
        }
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG_IRONPLATED.get(), CyberwareSlot.LLEG)) {
            state.addModifier(new SkinModifier(IRON_PLATED_LEFT_CYBERLEG, IRON_PLATED_LEFT_CYBERLEG,
                    0xFFFFFFFF, false, EnumSet.of(SkinModifier.HideVanilla.LEFT_PANTS)));

            if (data.isTrimmed(ModItems.BASECYBERWARE_LEFTLEG_IRONPLATED.get(), CyberwareSlot.LLEG)) {
                ResourceLocation patternId = data.trimPatternId(ModItems.BASECYBERWARE_LEFTLEG_IRONPLATED.get(), CyberwareSlot.LLEG);
                int tint = data.trimColor(ModItems.BASECYBERWARE_LEFTLEG_IRONPLATED.get(), CyberwareSlot.LLEG);

                ResourceLocation tex = resolveTrimOverlay(patternId, true, Limb.LEG, false);
                if (tex != null) {
                    state.addModifier(new SkinModifier(tex, tex, tint, false,
                            EnumSet.of(SkinModifier.HideVanilla.LEFT_PANTS)));
                }
            }
        }
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG_GOLDPLATED.get(), CyberwareSlot.LLEG)) {
            state.addModifier(new SkinModifier(GOLD_PLATED_LEFT_CYBERLEG, GOLD_PLATED_LEFT_CYBERLEG,
                    0xFFFFFFFF, false, EnumSet.of(SkinModifier.HideVanilla.LEFT_PANTS)));

            if (data.isTrimmed(ModItems.BASECYBERWARE_LEFTLEG_GOLDPLATED.get(), CyberwareSlot.LLEG)) {
                ResourceLocation patternId = data.trimPatternId(ModItems.BASECYBERWARE_LEFTLEG_GOLDPLATED.get(), CyberwareSlot.LLEG);
                int tint = data.trimColor(ModItems.BASECYBERWARE_LEFTLEG_GOLDPLATED.get(), CyberwareSlot.LLEG);

                ResourceLocation tex = resolveTrimOverlay(patternId, true, Limb.LEG, false);
                if (tex != null) {
                    state.addModifier(new SkinModifier(tex, tex, tint, false,
                            EnumSet.of(SkinModifier.HideVanilla.LEFT_PANTS)));
                }
            }
        }
// RIGHT CYBERLEG
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG)) {
            state.addModifier(new SkinModifier(RIGHT_CYBERLEG_TEXTURE, RIGHT_CYBERLEG_TEXTURE,
                    0xFFFFFFFF, false, EnumSet.of(SkinModifier.HideVanilla.RIGHT_PANTS)));

            if (data.isDyed(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG)) {
                int tint = data.dyeColor(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG);
                state.addModifier(new SkinModifier(RIGHT_CYBERLEG_PRIMARY, RIGHT_CYBERLEG_PRIMARY,
                        tint, false, EnumSet.of(SkinModifier.HideVanilla.RIGHT_PANTS)));
            }

            if (data.isTrimmed(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG)) {
                ResourceLocation patternId = data.trimPatternId(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG);
                int tint = data.trimColor(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG);

                ResourceLocation tex = resolveTrimOverlay(patternId, false, Limb.LEG, false);
                if (tex != null) {
                    state.addModifier(new SkinModifier(tex, tex, tint, false,
                            EnumSet.of(SkinModifier.HideVanilla.RIGHT_PANTS)));
                }
            }
        }
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG_COPPERPLATED.get(), CyberwareSlot.RLEG)) {
            state.addModifier(new SkinModifier(COPPER_PLATED_RIGHT_CYBERLEG, COPPER_PLATED_RIGHT_CYBERLEG,
                    0xFFFFFFFF, false, EnumSet.of(SkinModifier.HideVanilla.RIGHT_PANTS)));

            if (data.isTrimmed(ModItems.BASECYBERWARE_RIGHTLEG_COPPERPLATED.get(), CyberwareSlot.RLEG)) {
                ResourceLocation patternId = data.trimPatternId(ModItems.BASECYBERWARE_RIGHTLEG_COPPERPLATED.get(), CyberwareSlot.RLEG);
                int tint = data.trimColor(ModItems.BASECYBERWARE_RIGHTLEG_COPPERPLATED.get(), CyberwareSlot.RLEG);

                ResourceLocation tex = resolveTrimOverlay(patternId, false, Limb.LEG, false);
                if (tex != null) {
                    state.addModifier(new SkinModifier(tex, tex, tint, false,
                            EnumSet.of(SkinModifier.HideVanilla.RIGHT_PANTS)));
                }
            }
        }
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG_IRONPLATED.get(), CyberwareSlot.RLEG)) {
            state.addModifier(new SkinModifier(IRON_PLATED_RIGHT_CYBERLEG, IRON_PLATED_RIGHT_CYBERLEG,
                    0xFFFFFFFF, false, EnumSet.of(SkinModifier.HideVanilla.RIGHT_PANTS)));

            if (data.isTrimmed(ModItems.BASECYBERWARE_RIGHTLEG_IRONPLATED.get(), CyberwareSlot.RLEG)) {
                ResourceLocation patternId = data.trimPatternId(ModItems.BASECYBERWARE_RIGHTLEG_IRONPLATED.get(), CyberwareSlot.RLEG);
                int tint = data.trimColor(ModItems.BASECYBERWARE_RIGHTLEG_IRONPLATED.get(), CyberwareSlot.RLEG);

                ResourceLocation tex = resolveTrimOverlay(patternId, false, Limb.LEG, false);
                if (tex != null) {
                    state.addModifier(new SkinModifier(tex, tex, tint, false,
                            EnumSet.of(SkinModifier.HideVanilla.RIGHT_PANTS)));
                }
            }
        }
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG_GOLDPLATED.get(), CyberwareSlot.RLEG)) {
            state.addModifier(new SkinModifier(GOLD_PLATED_RIGHT_CYBERLEG, GOLD_PLATED_RIGHT_CYBERLEG,
                    0xFFFFFFFF, false, EnumSet.of(SkinModifier.HideVanilla.RIGHT_PANTS)));

            if (data.isTrimmed(ModItems.BASECYBERWARE_RIGHTLEG_GOLDPLATED.get(), CyberwareSlot.RLEG)) {
                ResourceLocation patternId = data.trimPatternId(ModItems.BASECYBERWARE_RIGHTLEG_GOLDPLATED.get(), CyberwareSlot.RLEG);
                int tint = data.trimColor(ModItems.BASECYBERWARE_RIGHTLEG_GOLDPLATED.get(), CyberwareSlot.RLEG);

                ResourceLocation tex = resolveTrimOverlay(patternId, false, Limb.LEG, false);
                if (tex != null) {
                    state.addModifier(new SkinModifier(tex, tex, tint, false,
                            EnumSet.of(SkinModifier.HideVanilla.RIGHT_PANTS)));
                }
            }
        }
// LEFT CYBERARM
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM)) {
            state.addModifier(new SkinModifier(LEFT_CYBERARM_TEXTURE_WIDE, LEFT_CYBERARM_TEXTURE_SLIM,
                    0xFFFFFFFF, false, EnumSet.of(SkinModifier.HideVanilla.LEFT_SLEEVE), EnumSet.of(HumanoidArm.LEFT)));

            if (data.isDyed(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM)) {
                int tint = data.dyeColor(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM);
                state.addModifier(new SkinModifier(LEFT_CYBERARM_PRIMARY_WIDE, LEFT_CYBERARM_PRIMARY_SLIM,
                        tint, false, EnumSet.of(SkinModifier.HideVanilla.LEFT_SLEEVE)));
            }

            boolean slim = isSlimArms(player);

            if (data.isTrimmed(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM)) {
                ResourceLocation patternId = data.trimPatternId(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM);
                int tint = data.trimColor(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM);

                ResourceLocation tex = resolveTrimOverlay(patternId, true, Limb.ARM, slim);
                if (tex != null) {
                    state.addModifier(new SkinModifier(tex, tex, tint, false,
                            EnumSet.of(SkinModifier.HideVanilla.LEFT_SLEEVE)));
                }
            }
        }
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM_COPPERPLATED.get(), CyberwareSlot.LARM)) {
            state.addModifier(new SkinModifier(COPPER_PLATED_LEFT_CYBERARM_WIDE, COPPER_PLATED_LEFT_CYBERARM_SLIM,
                    0xFFFFFFFF, false, EnumSet.of(SkinModifier.HideVanilla.LEFT_SLEEVE)));

            if (data.isTrimmed(ModItems.BASECYBERWARE_LEFTARM_COPPERPLATED.get(), CyberwareSlot.LARM)) {
                ResourceLocation patternId = data.trimPatternId(ModItems.BASECYBERWARE_LEFTARM_COPPERPLATED.get(), CyberwareSlot.LARM);
                int tint = data.trimColor(ModItems.BASECYBERWARE_LEFTARM_COPPERPLATED.get(), CyberwareSlot.LARM);

                boolean slim = isSlimArms(player);

                ResourceLocation tex = resolveTrimOverlay(patternId, true, Limb.ARM, slim);
                if (tex != null) {
                    state.addModifier(new SkinModifier(tex, tex, tint, false,
                            EnumSet.of(SkinModifier.HideVanilla.LEFT_SLEEVE)));
                }
            }
        }
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM_IRONPLATED.get(), CyberwareSlot.LARM)) {
            state.addModifier(new SkinModifier(IRON_PLATED_LEFT_CYBERARM_WIDE, IRON_PLATED_LEFT_CYBERARM_SLIM,
                    0xFFFFFFFF, false, EnumSet.of(SkinModifier.HideVanilla.LEFT_SLEEVE)));

            if (data.isTrimmed(ModItems.BASECYBERWARE_LEFTARM_IRONPLATED.get(), CyberwareSlot.LARM)) {
                ResourceLocation patternId = data.trimPatternId(ModItems.BASECYBERWARE_LEFTARM_IRONPLATED.get(), CyberwareSlot.LARM);
                int tint = data.trimColor(ModItems.BASECYBERWARE_LEFTARM_IRONPLATED.get(), CyberwareSlot.LARM);

                boolean slim = isSlimArms(player);

                ResourceLocation tex = resolveTrimOverlay(patternId, true, Limb.ARM, slim);
                if (tex != null) {
                    state.addModifier(new SkinModifier(tex, tex, tint, false,
                            EnumSet.of(SkinModifier.HideVanilla.LEFT_SLEEVE)));
                }
            }
        }
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM_GOLDPLATED.get(), CyberwareSlot.LARM)) {
            state.addModifier(new SkinModifier(GOLD_PLATED_LEFT_CYBERARM_WIDE, GOLD_PLATED_LEFT_CYBERARM_SLIM,
                    0xFFFFFFFF, false, EnumSet.of(SkinModifier.HideVanilla.LEFT_SLEEVE)));

            if (data.isTrimmed(ModItems.BASECYBERWARE_LEFTARM_GOLDPLATED.get(), CyberwareSlot.LARM)) {
                ResourceLocation patternId = data.trimPatternId(ModItems.BASECYBERWARE_LEFTARM_GOLDPLATED.get(), CyberwareSlot.LARM);
                int tint = data.trimColor(ModItems.BASECYBERWARE_LEFTARM_GOLDPLATED.get(), CyberwareSlot.LARM);

                boolean slim = isSlimArms(player);

                ResourceLocation tex = resolveTrimOverlay(patternId, true, Limb.ARM, slim);
                if (tex != null) {
                    state.addModifier(new SkinModifier(tex, tex, tint, false,
                            EnumSet.of(SkinModifier.HideVanilla.LEFT_SLEEVE)));
                }
            }
        }
// RIGHT CYBERARM
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM)) {
            state.addModifier(new SkinModifier(RIGHT_CYBERARM_TEXTURE_WIDE, RIGHT_CYBERARM_TEXTURE_SLIM,
                    0xFFFFFFFF, false, EnumSet.of(SkinModifier.HideVanilla.RIGHT_SLEEVE), EnumSet.of(HumanoidArm.RIGHT)));

            if (data.isDyed(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM)) {
                int tint = data.dyeColor(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM);
                state.addModifier(new SkinModifier(RIGHT_CYBERARM_PRIMARY_WIDE, RIGHT_CYBERARM_PRIMARY_SLIM,
                        tint, false, EnumSet.of(SkinModifier.HideVanilla.RIGHT_SLEEVE)));
            }

            boolean slim = isSlimArms(player);

            if (data.isTrimmed(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM)) {
                ResourceLocation patternId = data.trimPatternId(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM);
                int tint = data.trimColor(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM);

                ResourceLocation tex = resolveTrimOverlay(patternId, false, Limb.ARM, slim);
                if (tex != null) {
                    state.addModifier(new SkinModifier(tex, tex, tint, false,
                            EnumSet.of(SkinModifier.HideVanilla.RIGHT_SLEEVE), EnumSet.of(HumanoidArm.RIGHT)));
                }
            }
        }
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM_COPPERPLATED.get(), CyberwareSlot.RARM)) {
            state.addModifier(new SkinModifier(COPPER_PLATED_RIGHT_CYBERARM_WIDE, COPPER_PLATED_RIGHT_CYBERARM_SLIM,
                    0xFFFFFFFF, false, EnumSet.of(SkinModifier.HideVanilla.RIGHT_SLEEVE)));

            if (data.isTrimmed(ModItems.BASECYBERWARE_RIGHTARM_COPPERPLATED.get(), CyberwareSlot.RARM)) {
                ResourceLocation patternId = data.trimPatternId(ModItems.BASECYBERWARE_RIGHTARM_COPPERPLATED.get(), CyberwareSlot.RARM);
                int tint = data.trimColor(ModItems.BASECYBERWARE_RIGHTARM_COPPERPLATED.get(), CyberwareSlot.RARM);

                boolean slim = isSlimArms(player);

                ResourceLocation tex = resolveTrimOverlay(patternId, false, Limb.ARM, slim);
                if (tex != null) {
                    state.addModifier(new SkinModifier(tex, tex, tint, false,
                            EnumSet.of(SkinModifier.HideVanilla.RIGHT_SLEEVE)));
                }
            }
        }
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM_IRONPLATED.get(), CyberwareSlot.RARM)) {
            state.addModifier(new SkinModifier(IRON_PLATED_RIGHT_CYBERARM_WIDE, IRON_PLATED_RIGHT_CYBERARM_SLIM,
                    0xFFFFFFFF, false, EnumSet.of(SkinModifier.HideVanilla.RIGHT_SLEEVE)));

            if (data.isTrimmed(ModItems.BASECYBERWARE_RIGHTARM_IRONPLATED.get(), CyberwareSlot.RARM)) {
                ResourceLocation patternId = data.trimPatternId(ModItems.BASECYBERWARE_RIGHTARM_IRONPLATED.get(), CyberwareSlot.RARM);
                int tint = data.trimColor(ModItems.BASECYBERWARE_RIGHTARM_IRONPLATED.get(), CyberwareSlot.RARM);

                boolean slim = isSlimArms(player);

                ResourceLocation tex = resolveTrimOverlay(patternId, false, Limb.ARM, slim);
                if (tex != null) {
                    state.addModifier(new SkinModifier(tex, tex, tint, false,
                            EnumSet.of(SkinModifier.HideVanilla.RIGHT_SLEEVE)));
                }
            }
        }
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM_GOLDPLATED.get(), CyberwareSlot.RARM)) {
            state.addModifier(new SkinModifier(GOLD_PLATED_RIGHT_CYBERARM_WIDE, GOLD_PLATED_RIGHT_CYBERARM_SLIM,
                    0xFFFFFFFF, false, EnumSet.of(SkinModifier.HideVanilla.RIGHT_SLEEVE)));

            if (data.isTrimmed(ModItems.BASECYBERWARE_RIGHTARM_GOLDPLATED.get(), CyberwareSlot.RARM)) {
                ResourceLocation patternId = data.trimPatternId(ModItems.BASECYBERWARE_RIGHTARM_GOLDPLATED.get(), CyberwareSlot.RARM);
                int tint = data.trimColor(ModItems.BASECYBERWARE_RIGHTARM_GOLDPLATED.get(), CyberwareSlot.RARM);

                boolean slim = isSlimArms(player);

                ResourceLocation tex = resolveTrimOverlay(patternId, false, Limb.ARM, slim);
                if (tex != null) {
                    state.addModifier(new SkinModifier(tex, tex, tint, false,
                            EnumSet.of(SkinModifier.HideVanilla.RIGHT_SLEEVE)));
                }
            }
        }

// NETHERITE PLATING
        if (data.hasSpecificItem(ModItems.SKINUPGRADES_NETHERITEPLATING.get(), CyberwareSlot.SKIN)) {
            state.removeModifier(new SkinModifier(LEFT_CYBERLEG_TEXTURE, LEFT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(RIGHT_CYBERLEG_TEXTURE, RIGHT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(LEFT_CYBERARM_TEXTURE_WIDE, LEFT_CYBERARM_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(RIGHT_CYBERARM_TEXTURE_WIDE, RIGHT_CYBERARM_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(CYBEREYES_PRIMARY, CYBEREYES_PRIMARY));
            state.clearHighlights();
            state.clearModifiers();

            if (data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM) && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM) &&
                    data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG) && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG) &&
                    data.hasSpecificItem(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN) && data.hasSpecificItem(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get(), CyberwareSlot.MUSCLE) &&
                    data.hasSpecificItem(ModItems.HEARTUPGRADES_CYBERHEART.get(), CyberwareSlot.HEART) && data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE) &&
                    data.hasMultipleSpecificItem(ModItems.LUNGSUPGRADES_OXYGEN.get(), CyberwareSlot.LUNGS, 3) &&
                    data.hasSpecificItem(ModItems.SKINUPGRADES_SOLARSKIN.get(), CyberwareSlot.SKIN) &&
                    data.hasSpecificItem(ModItems.SKINUPGRADES_NETHERITEPLATING.get(), CyberwareSlot.SKIN)) {
                state.removeModifier(new SkinModifier(NETHERPLATED_SKIN_TEXTURE_WIDE, NETHERPLATED_SKIN_TEXTURE_SLIM));
            } else {
                state.addModifier(new SkinModifier(NETHERPLATED_SKIN_TEXTURE_WIDE, NETHERPLATED_SKIN_TEXTURE_SLIM,
                        0xFFFFFFFF, true));
            }
        }

        // SAMSON MODEL
        if (FullBorgHandler.isSamson(player.getData(ModAttachments.CYBERWARE))) {

            state.removeModifier(new SkinModifier(LEFT_CYBERLEG_TEXTURE, LEFT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(RIGHT_CYBERLEG_TEXTURE, RIGHT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(LEFT_CYBERARM_TEXTURE_WIDE, LEFT_CYBERARM_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(RIGHT_CYBERARM_TEXTURE_WIDE, RIGHT_CYBERARM_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(CYBEREYES_PRIMARY, CYBEREYES_PRIMARY));
            state.clearHighlights();
            state.clearModifiers();

            state.addModifier(new SkinModifier(SAMSON_WIDE, SAMSON_SLIM,
                    0xFFFFFFFF, true));

            if (data.isDyed(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN)) {
                int tint = data.dyeColor(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN);

                state.addModifier(new SkinModifier(SAMSON_WIDE_DYED, SAMSON_SLIM_DYED,
                        tint, true));
            }

            if (data.isDyed(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES)) {
                int tint = data.dyeColor(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES);

                state.addModifier(new SkinModifier(SAMSON_EYES_DYED, SAMSON_EYES_DYED,
                        tint, true));
                state.addHighlight(new SkinHighlight(SAMSON_EYES_DYED, SAMSON_EYES_DYED,
                        tint, true, true));
            }
        }
// ECLIPSE MODEL
        if (FullBorgHandler.isEclipse(player.getData(ModAttachments.CYBERWARE))) {

            state.removeModifier(new SkinModifier(LEFT_CYBERLEG_TEXTURE, LEFT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(RIGHT_CYBERLEG_TEXTURE, RIGHT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(LEFT_CYBERARM_TEXTURE_WIDE, LEFT_CYBERARM_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(RIGHT_CYBERARM_TEXTURE_WIDE, RIGHT_CYBERARM_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(CYBEREYES_PRIMARY, CYBEREYES_PRIMARY));
            state.clearHighlights();
            state.clearModifiers();

            state.addModifier(new SkinModifier(ECLIPSE_WIDE, ECLIPSE_SLIM,
                    0xFFFFFFFF, true));

            if (data.isDyed(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN)) {
                int tint = data.dyeColor(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN);

                state.addModifier(new SkinModifier(ECLIPSE_WIDE_DYED, ECLIPSE_SLIM_DYED,
                        tint, true));
            }

            if (data.isDyed(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES)) {
                int tint = data.dyeColor(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES);

                state.addModifier(new SkinModifier(ECLIPSE_EYES_DYED, ECLIPSE_EYES_DYED,
                        tint, true));
                state.addHighlight(new SkinHighlight(ECLIPSE_EYES_DYED, ECLIPSE_EYES_DYED,
                        tint, true, true));
            }
        }
// SPYDER MODEL
        if (FullBorgHandler.isSpyder(player.getData(ModAttachments.CYBERWARE))) {

            state.removeModifier(new SkinModifier(LEFT_CYBERLEG_TEXTURE, LEFT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(RIGHT_CYBERLEG_TEXTURE, RIGHT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(LEFT_CYBERARM_TEXTURE_WIDE, LEFT_CYBERARM_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(RIGHT_CYBERARM_TEXTURE_WIDE, RIGHT_CYBERARM_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(CYBEREYES_PRIMARY, CYBEREYES_PRIMARY));
            state.clearHighlights();
            state.clearModifiers();

            state.addModifier(new SkinModifier(SPYDER_WIDE, SPYDER_SLIM,
                    0xFFFFFFFF, true));

            if (data.isDyed(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN)) {
                int tint = data.dyeColor(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN);

                state.addModifier(new SkinModifier(SPYDER_WIDE_DYED, SPYDER_SLIM_DYED,
                        tint, true));
            }

            if (data.isDyed(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES)) {
                int tint = data.dyeColor(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES);

                state.addModifier(new SkinModifier(SPYDER_EYES_DYED, SPYDER_EYES_DYED,
                        tint, true));
                state.addHighlight(new SkinHighlight(SPYDER_EYES_DYED, SPYDER_EYES_DYED,
                        tint, true, true));
            }
        }
// WINGMAN MODEL
        if (FullBorgHandler.isAquarius(player.getData(ModAttachments.CYBERWARE))) {

            state.removeModifier(new SkinModifier(LEFT_CYBERLEG_TEXTURE, LEFT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(RIGHT_CYBERLEG_TEXTURE, RIGHT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(LEFT_CYBERARM_TEXTURE_WIDE, LEFT_CYBERARM_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(RIGHT_CYBERARM_TEXTURE_WIDE, RIGHT_CYBERARM_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(CYBEREYES_PRIMARY, CYBEREYES_PRIMARY));
            state.clearHighlights();
            state.clearModifiers();

            state.addModifier(new SkinModifier(WINGMAN_WIDE, WINGMAN_SLIM,
                    0xFFFFFFFF, true));

            if (data.isDyed(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN)) {
                int tint = data.dyeColor(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN);

                state.addModifier(new SkinModifier(WINGMAN_WIDE_DYED, WINGMAN_SLIM_DYED,
                        tint, true));
            }
        }
// AQUARIUS MODEL
        if (FullBorgHandler.isAquarius(player.getData(ModAttachments.CYBERWARE))) {

            state.removeModifier(new SkinModifier(LEFT_CYBERLEG_TEXTURE, LEFT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(RIGHT_CYBERLEG_TEXTURE, RIGHT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(LEFT_CYBERARM_TEXTURE_WIDE, LEFT_CYBERARM_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(RIGHT_CYBERARM_TEXTURE_WIDE, RIGHT_CYBERARM_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(CYBEREYES_PRIMARY, CYBEREYES_PRIMARY));
            state.clearHighlights();
            state.clearModifiers();

            state.addModifier(new SkinModifier(AQUARIUS_WIDE, AQUARIUS_SLIM,
                    0xFFFFFFFF, true));

            if (data.isDyed(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN)) {
                int tint = data.dyeColor(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN);

                state.addModifier(new SkinModifier(AQUARIUS_WIDE_DYED, AQUARIUS_SLIM_DYED,
                        tint, true));
            }

            if (data.isDyed(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES)) {
                int tint = data.dyeColor(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES);

                state.addModifier(new SkinModifier(AQUARIUS_EYES_DYED, AQUARIUS_EYES_DYED,
                        tint, true));
                state.addHighlight(new SkinHighlight(AQUARIUS_EYES_DYED, AQUARIUS_EYES_DYED,
                        tint, true, true));
            }
        }
// DYMOND MODEL
        if (FullBorgHandler.isDymond(player.getData(ModAttachments.CYBERWARE))) {

            state.removeModifier(new SkinModifier(LEFT_CYBERLEG_TEXTURE, LEFT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(RIGHT_CYBERLEG_TEXTURE, RIGHT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(LEFT_CYBERARM_TEXTURE_WIDE, LEFT_CYBERARM_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(RIGHT_CYBERARM_TEXTURE_WIDE, RIGHT_CYBERARM_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(CYBEREYES_PRIMARY, CYBEREYES_PRIMARY));
            state.clearHighlights();
            state.clearModifiers();

            state.addModifier(new SkinModifier(DYMOND_WIDE, DYMOND_SLIM,
                    0xFFFFFFFF, true));

            if (data.isDyed(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN)) {
                int tint = data.dyeColor(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN);

                state.addModifier(new SkinModifier(DYMOND_WIDE_DYED, DYMOND_SLIM_DYED,
                        tint, true));
            }

            if (data.isDyed(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES)) {
                int tint = data.dyeColor(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES);

                state.addModifier(new SkinModifier(DYMOND_EYES_DYED, DYMOND_EYES_DYED,
                        tint, true));
                state.addHighlight(new SkinHighlight(DYMOND_EYES_DYED, DYMOND_EYES_DYED,
                        tint, true, true));
            }
        }
// DRAGOON MODEL
        if (FullBorgHandler.isDragoon(player.getData(ModAttachments.CYBERWARE))) {

            state.removeModifier(new SkinModifier(LEFT_CYBERLEG_TEXTURE, LEFT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(RIGHT_CYBERLEG_TEXTURE, RIGHT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(LEFT_CYBERARM_TEXTURE_WIDE, LEFT_CYBERARM_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(RIGHT_CYBERARM_TEXTURE_WIDE, RIGHT_CYBERARM_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(CYBEREYES_PRIMARY, CYBEREYES_PRIMARY));
            state.clearHighlights();
            state.clearModifiers();

            state.addModifier(new SkinModifier(DRAGOON_WIDE, DRAGOON_SLIM,
                    0xFFFFFFFF, true));

            if (data.isDyed(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN)) {
                int tint = data.dyeColor(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN);

                state.addModifier(new SkinModifier(DRAGOON_WIDE_DYED, DRAGOON_SLIM_DYED,
                        tint, true));
            }

            if (data.isDyed(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES)) {
                int tint = data.dyeColor(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES);

                state.addModifier(new SkinModifier(DRAGOON_EYES_DYED, DRAGOON_EYES_DYED,
                        tint, true));
                state.addHighlight(new SkinHighlight(DRAGOON_EYES_DYED, DRAGOON_EYES_DYED,
                        tint, true, true));
            }
        }
// COPERNICUS MODEL
        if (ModCompats.isInstalled("northstar")) {
            if (FullBorgHandler.isCopernicus(player.getData(ModAttachments.CYBERWARE))) {

                state.removeModifier(new SkinModifier(LEFT_CYBERLEG_TEXTURE, LEFT_CYBERLEG_TEXTURE));
                state.removeModifier(new SkinModifier(RIGHT_CYBERLEG_TEXTURE, RIGHT_CYBERLEG_TEXTURE));
                state.removeModifier(new SkinModifier(LEFT_CYBERARM_TEXTURE_WIDE, LEFT_CYBERARM_TEXTURE_SLIM));
                state.removeModifier(new SkinModifier(RIGHT_CYBERARM_TEXTURE_WIDE, RIGHT_CYBERARM_TEXTURE_SLIM));
                state.removeModifier(new SkinModifier(NETHERPLATED_SKIN_TEXTURE_WIDE, NETHERPLATED_SKIN_TEXTURE_SLIM));
                state.removeModifier(new SkinModifier(CYBEREYES_PRIMARY, CYBEREYES_PRIMARY));
                state.clearHighlights();
                state.clearModifiers();

                state.addModifier(new SkinModifier(COPERNICUS_WIDE, COPERNICUS_SLIM,
                        0xFFFFFFFF, true));

                if (data.isDyed(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN)) {
                    int tint = data.dyeColor(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN);

                    state.addModifier(new SkinModifier(COPERNICUS_WIDE_DYED, COPERNICUS_SLIM_DYED,
                            tint, true));
                }

                if (data.isDyed(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES)) {
                    int tint = data.dyeColor(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES);

                    state.addModifier(new SkinModifier(COPERNICUS_EYES_DYED, COPERNICUS_EYES_DYED,
                            tint, true));
                }
            }
        }

// GENOS MODEL
        if (FullBorgHandler.isGenos(player.getData(ModAttachments.CYBERWARE))) {

            state.removeModifier(new SkinModifier(LEFT_CYBERLEG_TEXTURE, LEFT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(RIGHT_CYBERLEG_TEXTURE, RIGHT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(LEFT_CYBERARM_TEXTURE_WIDE, LEFT_CYBERARM_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(RIGHT_CYBERARM_TEXTURE_WIDE, RIGHT_CYBERARM_TEXTURE_SLIM));
            state.clearModifiers();

            state.addModifier(new SkinModifier(GENOS_WIDE, GENOS_SLIM,
                    0xFFFFFFFF, false, EnumSet.of(SkinModifier.HideVanilla.RIGHT_PANTS, SkinModifier.HideVanilla.LEFT_PANTS,
                    SkinModifier.HideVanilla.RIGHT_SLEEVE, SkinModifier.HideVanilla.LEFT_SLEEVE, SkinModifier.HideVanilla.JACKET)));
            state.addHighlight(new SkinHighlight(GENOS_HIGHLIGHT, GENOS_HIGHLIGHT,
                    0xFFFFFF, true, false));

            if (data.isDyed(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN)) {
                int tint = data.dyeColor(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN);

                state.addModifier(new SkinModifier(GENOS_WIDE_DYED, GENOS_SLIM_DYED,
                        tint, false, EnumSet.of(SkinModifier.HideVanilla.RIGHT_PANTS, SkinModifier.HideVanilla.LEFT_PANTS,
                        SkinModifier.HideVanilla.RIGHT_SLEEVE, SkinModifier.HideVanilla.LEFT_SLEEVE, SkinModifier.HideVanilla.JACKET)));
            }

            if (data.isDyed(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES)) {
                int tint = data.dyeColor(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES);
                state.clearHighlights();

                state.addModifier(new SkinModifier(GENOS_EYES_DYED, GENOS_EYES_DYED,
                        tint, false, EnumSet.of(SkinModifier.HideVanilla.RIGHT_PANTS, SkinModifier.HideVanilla.LEFT_PANTS,
                        SkinModifier.HideVanilla.RIGHT_SLEEVE, SkinModifier.HideVanilla.LEFT_SLEEVE, SkinModifier.HideVanilla.JACKET)));
                state.addHighlight(new SkinHighlight(GENOS_EYES_DYED, GENOS_EYES_DYED,
                        tint, true, true));
                state.addModifier(new SkinModifier(CYBEREYES_PRIMARY, CYBEREYES_PRIMARY,
                        tint, false));
                state.addHighlight(new SkinHighlight(CYBEREYES_PRIMARY, CYBEREYES_PRIMARY,
                        tint, true, true));
            }
        }


// METAL PLATING (TRIMMED)
        if (data.hasSpecificItem(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN)) {
            if (data.isTrimmed(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN)) {
                ResourceLocation patternId = data.trimPatternId(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN);
                int tint = data.trimColor(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN);

                ResourceLocation wide = resolveBodyTrimOverlay(patternId, false);
                ResourceLocation slim = resolveBodyTrimOverlay(patternId, true);

                if (wide != null && slim != null) {
                    tint = (tint & 0x00FFFFFF) | 0xFF000000;
                    state.addModifier(new SkinModifier(wide, slim,
                            tint, false, false, EnumSet.noneOf(SkinModifier.HideVanilla.class), EnumSet.noneOf(HumanoidArm.class), true));
                }
            }
        }

// POLAR BEAR FUR
        if (data.hasSpecificItem(ModItems.WETWARE_POLARBEARFUR.get(), CyberwareSlot.SKIN)) {
            state.addModifier(new SkinModifier(POLAR_BEAR_FUR_TEXTURE, POLAR_BEAR_FUR_TEXTURE,
                    0xFFFFFFFF, true));
        }
// MANA ASSIMILATOR
        if (ModItems.SKINUPGRADES_MANASKIN != null) {
            if (data.hasSpecificItem(ModItems.SKINUPGRADES_MANASKIN.get(), CyberwareSlot.SKIN)) {
                state.addModifier(new SkinModifier(MANASKIN_WIDE, MANASKIN_SLIM,
                        0xFFFFFFFF, true, false, null, null, false));
            }
        }
// SPINAL INJECTOR
        if (data.hasSpecificItem(ModItems.BONEUPGRADES_SPINALINJECTOR.get(), CyberwareSlot.BONE)) {
            state.addModifier(new SkinModifier(SPINAL_INJECTOR_TEXTURE, SPINAL_INJECTOR_TEXTURE,
                    0xFFFFFFFF, false));
            SkinHighlightRender.apply(state, true, SPINAL_INJECTOR_HIGHLIGHT_TEXTURE, SPINAL_INJECTOR_HIGHLIGHT_TEXTURE,
                    0xFFFFFFFF, true);
        }
// DEPLOYABLE ELYTRA
        if (data.hasSpecificItem(ModItems.BONEUPGRADES_SPINALINJECTOR.get(), CyberwareSlot.BONE)) {
            state.addModifier(new SkinModifier(DEPLOYABLE_ELYTRA_TEXTURE, DEPLOYABLE_ELYTRA_TEXTURE,
                    0xFFFFFFFF, false));
            SkinHighlightRender.apply(state, true, DEPLOYABLE_ELYTRA_HIGHLIGHT_TEXTURE, DEPLOYABLE_ELYTRA_HIGHLIGHT_TEXTURE,
                    0xFFFFFFFF, true);
        }
// SANDEVISTAN
        if (data.hasSpecificItem(ModItems.BONEUPGRADES_SANDEVISTAN.get(), CyberwareSlot.BONE)) {
            state.addModifier(new SkinModifier(SANDEVISTAN_TEXTURE, SANDEVISTAN_TEXTURE,
                    0xFFFFFFFF, false));
            SkinHighlightRender.apply(state, true, SANDEVISTAN_HIGHLIGHT_TEXTURE, SANDEVISTAN_HIGHLIGHT_TEXTURE,
                    0xFFFFFFFF, true);
        }
// CHIPWARE SLOTS
        if (data.hasSpecificItem(ModItems.BRAINUPGRADES_CHIPWARESLOTS.get(), CyberwareSlot.BRAIN)) {
            if (data.hasChipwareShard(ModTags.Items.DATA_SHARDS)) {
                state.addModifier(new SkinModifier(CHIPWARE_ACTIVE, CHIPWARE_ACTIVE,
                        0xFFFFFFFF, false));
                state.addHighlight(new SkinHighlight(CHIPWARE_ACTIVE, CHIPWARE_ACTIVE,
                        0xFFFFFFFF, true));
            } else {
                state.addModifier(new SkinModifier(CHIPWARE_INACTIVE, CHIPWARE_INACTIVE,
                        0xFFFFFFFF, false));
                state.addHighlight(new SkinHighlight(CHIPWARE_INACTIVE, CHIPWARE_INACTIVE,
                        0xFFFFFFFF, true));
            }
        }
// LEFT REINFORCED KNUCKLES
        if (data.hasSpecificItem(ModItems.ARMUPGRADES_REINFORCEDKNUCKLES.get(), CyberwareSlot.LARM)) {
            state.addModifier(new SkinModifier(KNUCKLES_LARM_WIDE, KNUCKLES_LARM_SLIM,
                    0xFFFFFFFF, false));
        }
// RIGHT REINFORCED KNUCKLES
        if (data.hasSpecificItem(ModItems.ARMUPGRADES_REINFORCEDKNUCKLES.get(), CyberwareSlot.RARM)) {
            state.addModifier(new SkinModifier(KNUCKLES_RARM_WIDE, KNUCKLES_RARM_SLIM,
                    0xFFFFFFFF, false));
        }
// LEFT FIRESTARTER
        if (data.hasSpecificItem(ModItems.ARMUPGRADES_FIRESTARTER.get(), CyberwareSlot.LARM)) {
            state.addModifier(new SkinModifier(FIRESTARTER_LARM_WIDE, FIRESTARTER_LARM_SLIM,
                    0xFFFFFFFF, false));
        }
// RIGHT FIRESTARTER
        if (data.hasSpecificItem(ModItems.ARMUPGRADES_FIRESTARTER.get(), CyberwareSlot.RARM)) {
            state.addModifier(new SkinModifier(FIRESTARTER_RARM_WIDE, FIRESTARTER_RARM_SLIM,
                    0xFFFFFFFF, false));
        }
// LEFT FLYWHEEL
        if (data.hasSpecificItem(ModItems.ARMUPGRADES_FLYWHEEL.get(), CyberwareSlot.LARM)) {
            state.addModifier(new SkinModifier(FLYWHEEL_LARM_WIDE, FLYWHEEL_LARM_SLIM,
                    0xFFFFFFFF, false));
        }
// RIGHT FLYWHEEL
        if (data.hasSpecificItem(ModItems.ARMUPGRADES_FLYWHEEL.get(), CyberwareSlot.RARM)) {
            state.addModifier(new SkinModifier(FLYWHEEL_RARM_WIDE, FLYWHEEL_RARM_SLIM,
                    0xFFFFFFFF, false));
        }
// CYBERDECK
        if (data.hasSpecificItem(ModItems.BRAINUPGRADES_CYBERDECK.get(), CyberwareSlot.BRAIN)) {
            state.addModifier(new SkinModifier(CYBERDECK_TEXTURE, CYBERDECK_TEXTURE,
                    0xFFFFFFFF, true));
            state.addHighlight(new SkinHighlight(CYBERDECK_TEXTURE, CYBERDECK_TEXTURE,
                    0xFFFFFFFF, true));
        }
// HEAT ENGINE
        if (data.hasSpecificItem(ModItems.ORGANSUPGRADES_HEATENGINE.get(), CyberwareSlot.ORGANS)) {
            state.addModifier(new SkinModifier(FURNACE, FURNACE,
                    0xFFFFFFFF, false));

            if (data.isHeatEngineActive()) {
                state.addModifier(new SkinModifier(FURNACE_ACTIVE, FURNACE_ACTIVE,
                        0xFFFFFFFF, false));
                state.addHighlight(new SkinHighlight(FURNACE_HIGHLIGHT, FURNACE_HIGHLIGHT,
                        0xFFFFFFFF, true));
            }
        }
// SPIDER EYES
        if (data.hasSpecificItem(ModItems.WETWARE_SPIDEREYES.get(), CyberwareSlot.EYES)) {
            state.addModifier(new SkinModifier(SPIDER_EYES, SPIDER_EYES,
                    0xFFFFFFFF, false));
            state.addHighlight(new SkinHighlight(SPIDER_EYES, SPIDER_EYES,
                    0xFFFFFFFF, true));
        }



        return state;
    }



    private enum Limb {
        ARM("arm"),
        LEG("leg");

        final String id;
        Limb(String id) { this.id = id; }
    }

    private static ResourceLocation resolveTrimOverlay(ResourceLocation patternId, boolean left, Limb limb, boolean isSlimModelForArmsOrBody) {
        if (patternId == null) return null;

        String pattern = patternId.getPath(); // e.g. "flow"
        String file;

            String side = left ? "left" : "right";
            if (limb == Limb.LEG) {
                file = pattern + "_" + side + "_leg.png";
            } else {
                file = pattern + "_" + side + "_" + (isSlimModelForArmsOrBody ? "slim" : "wide") + ".png";
            }

        return ResourceLocation.fromNamespaceAndPath(
                CreateCybernetics.MODID,
                "textures/entity/trims/" + file
        );
    }

    private static boolean isSlimArms(AbstractClientPlayer player) {
        return player.getSkin().model() == PlayerSkin.Model.SLIM;
    }

    private record TrimInfo(ResourceLocation patternId, int tint) {}

    private static TrimInfo getLimbTrim(PlayerCyberwareData data, AbstractClientPlayer player,
                                        CyberwareSlot slot, net.minecraft.world.item.Item item) {
        if (!data.isTrimmed(item, slot)) return null;
        ResourceLocation patternId = data.trimPatternId(item, slot);
        int tint = data.trimColor(item, slot);
        if (patternId == null) return null;
        return new TrimInfo(patternId, tint);
    }

    private static TrimInfo getUnifiedLimbTrim(PlayerCyberwareData data, AbstractClientPlayer player) {
        TrimInfo lArm = getLimbTrim(data, player, CyberwareSlot.LARM, ModItems.BASECYBERWARE_LEFTARM.get());
        TrimInfo rArm = getLimbTrim(data, player, CyberwareSlot.RARM, ModItems.BASECYBERWARE_RIGHTARM.get());
        TrimInfo lLeg = getLimbTrim(data, player, CyberwareSlot.LLEG, ModItems.BASECYBERWARE_LEFTLEG.get());
        TrimInfo rLeg = getLimbTrim(data, player, CyberwareSlot.RLEG, ModItems.BASECYBERWARE_RIGHTLEG.get());

        if (lArm == null || rArm == null || lLeg == null || rLeg == null) return null;

        if (!lArm.patternId().equals(rArm.patternId())) return null;
        if (!lArm.patternId().equals(lLeg.patternId())) return null;
        if (!lArm.patternId().equals(rLeg.patternId())) return null;

        if (lArm.tint() != rArm.tint() || lArm.tint() != lLeg.tint() || lArm.tint() != rLeg.tint()) return null;

        return lArm;
    }

    private static ResourceLocation resolveBodyTrimOverlay(ResourceLocation patternId, boolean slim) {
        if (patternId == null) return null;

        String pattern = patternId.getPath();
        String file = slim
                ? pattern + "_slim_full_body.png"
                : pattern + "_full_body.png";

        return ResourceLocation.fromNamespaceAndPath(
                CreateCybernetics.MODID,
                "textures/entity/trims/" + file
        );
    }

}