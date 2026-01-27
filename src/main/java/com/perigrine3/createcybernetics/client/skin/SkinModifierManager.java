package com.perigrine3.createcybernetics.client.skin;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.HumanoidArm;
import org.checkerframework.checker.units.qual.C;

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
    private static final ResourceLocation RIGHT_CYBERLEG_PRIMARY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/right_cyberleg_dye_primary.png");
    private static final ResourceLocation RIGHT_CYBERLEG_SECONDARY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/right_cyberleg_dye_secondary.png");
    private static final ResourceLocation LEFT_CYBERLEG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/left_cyberleg.png");
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

    //WIDE VARIANTS
    private static final ResourceLocation LEFT_CYBERARM_TEXTURE_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/left_cyberarm_wide.png");
    private static final ResourceLocation LEFT_CYBERARM_PRIMARY_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/left_cyberarm_dye_primary_wide.png");
    private static final ResourceLocation LEFT_CYBERARM_SECONDARY_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/left_cyberarm_dye_secondary_wide.png");
    private static final ResourceLocation RIGHT_CYBERARM_TEXTURE_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/right_cyberarm_wide.png");
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

    //SLIM VARIANTS
    private static final ResourceLocation LEFT_CYBERARM_TEXTURE_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/left_cyberarm_slim.png");
    private static final ResourceLocation LEFT_CYBERARM_PRIMARY_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/left_cyberarm_dye_primary_slim.png");
    private static final ResourceLocation LEFT_CYBERARM_SECONDARY_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/left_cyberarm_dye_secondary_slim.png");
    private static final ResourceLocation RIGHT_CYBERARM_TEXTURE_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/right_cyberarm_slim.png");
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
// NETHERITE PLATING
        if (data.hasSpecificItem(ModItems.SKINUPGRADES_NETHERITEPLATING.get(), CyberwareSlot.SKIN)) {
            state.removeModifier(new SkinModifier(LEFT_CYBERLEG_TEXTURE, LEFT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(RIGHT_CYBERLEG_TEXTURE, RIGHT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(LEFT_CYBERARM_TEXTURE_WIDE, LEFT_CYBERARM_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(RIGHT_CYBERARM_TEXTURE_WIDE, RIGHT_CYBERARM_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(CYBEREYES_PRIMARY, CYBEREYES_PRIMARY));
            state.clearHighlights();
            state.clearModifiers();

            state.addModifier(new SkinModifier(NETHERPLATED_SKIN_TEXTURE_WIDE, NETHERPLATED_SKIN_TEXTURE_SLIM,
                    0xFFFFFFFF, true));
            return state;
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
                    tint, false, EnumSet.noneOf(SkinModifier.HideVanilla.class), EnumSet.noneOf(HumanoidArm.class), true));
            return state;
        }
// GILLS
        if (data.hasSpecificItem(ModItems.WETWARE_GILLS.get(), CyberwareSlot.LUNGS)) {
            state.addModifier(new SkinModifier(GILLS_TEXTURE, GILLS_TEXTURE,
                    0xFFFFFFFF, true));
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
        }
// RIGHT CYBERARM
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM)) {
            state.addModifier(new SkinModifier(RIGHT_CYBERARM_TEXTURE_WIDE, RIGHT_CYBERARM_TEXTURE_SLIM,
                    0xFFFFFFFF, false, EnumSet.of(SkinModifier.HideVanilla.RIGHT_SLEEVE), EnumSet.of(HumanoidArm.RIGHT)));

            if (data.isDyed(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM)) {
                int tint = data.dyeColor(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM);
                state.addModifier(new SkinModifier(RIGHT_CYBERARM_PRIMARY_WIDE, RIGHT_CYBERARM_PRIMARY_SLIM,
                        tint, false, EnumSet.of(SkinModifier.HideVanilla.LEFT_SLEEVE)));
            }
        }

        // SAMSON MODEL
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM) && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM) &&
                data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG) && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG) &&
                data.hasSpecificItem(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN) && data.hasSpecificItem(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get(), CyberwareSlot.MUSCLE) &&
                data.hasSpecificItem(ModItems.HEARTUPGRADES_CYBERHEART.get(), CyberwareSlot.HEART) && data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE) &&
                data.hasSpecificItem(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES) && data.hasMultipleSpecificItem(ModItems.BONEUPGRADES_BONELACING.get(), CyberwareSlot.BONE, 3) &&
                data.hasMultipleSpecificItem(ModItems.SKINUPGRADES_SUBDERMALARMOR.get(), CyberwareSlot.SKIN, 3) &&
                data.hasMultipleSpecificItem(ModItems.ARMUPGRADES_PNEUMATICWRIST.get(), 2, CyberwareSlot.RARM, CyberwareSlot.LARM)) {

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
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM) && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM) &&
                data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG) && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG) &&
                data.hasSpecificItem(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN) && data.hasSpecificItem(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get(), CyberwareSlot.MUSCLE) &&
                data.hasSpecificItem(ModItems.HEARTUPGRADES_CYBERHEART.get(), CyberwareSlot.HEART) && data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE) &&
                data.hasSpecificItem(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES) && data.hasSpecificItem(ModItems.BONEUPGRADES_BONELACING.get(), CyberwareSlot.BONE) &&
                data.hasMultipleSpecificItem(ModItems.LUNGSUPGRADES_HYPEROXYGENATION.get(), CyberwareSlot.LUNGS, 3) &&
                data.hasMultipleSpecificItem(ModItems.LEGUPGRADES_OCELOTPAWS.get(), 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG) &&
                data.hasSpecificItem(ModItems.SKINUPGRADES_CHROMATOPHORES.get(), CyberwareSlot.SKIN) &&
                data.hasSpecificItem(ModItems.BONEUPGRADES_SANDEVISTAN.get(), CyberwareSlot.BONE)) {

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
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM) && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM) &&
                data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG) && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG) &&
                data.hasSpecificItem(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN) && data.hasSpecificItem(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get(), CyberwareSlot.MUSCLE) &&
                data.hasSpecificItem(ModItems.HEARTUPGRADES_CYBERHEART.get(), CyberwareSlot.HEART) && data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE) &&
                data.hasMultipleSpecificItem(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES, 3) && data.hasSpecificItem(ModItems.BONEUPGRADES_BONELACING.get(), CyberwareSlot.BONE) &&
                data.hasMultipleSpecificItem(ModItems.LEGUPGRADES_JUMPBOOST.get(), 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG) &&
                data.hasMultipleSpecificItem(ModItems.LEGUPGRADES_ANKLEBRACERS.get(), 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG) &&
                data.hasMultipleSpecificItem(ModItems.LEGUPGRADES_OCELOTPAWS.get(), 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG) &&
                data.hasSpecificItem(ModItems.SKINUPGRADES_CHROMATOPHORES.get(), CyberwareSlot.SKIN) &&
                data.hasSpecificItem(ModItems.SKINUPGRADES_SYNTHETICSETULES.get(), CyberwareSlot.SKIN)) {

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
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM) && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM) &&
                data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG) && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG) &&
                data.hasSpecificItem(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN) && data.hasSpecificItem(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get(), CyberwareSlot.MUSCLE) &&
                data.hasSpecificItem(ModItems.HEARTUPGRADES_CYBERHEART.get(), CyberwareSlot.HEART) && data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE) &&
                data.hasSpecificItem(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES) && data.hasSpecificItem(ModItems.BONEUPGRADES_BONELACING.get(), CyberwareSlot.BONE)  &&
                data.hasSpecificItem(ModItems.BONEUPGRADES_CYBERSKULL.get(), CyberwareSlot.BONE) &&
                data.hasSpecificItem(ModItems.BONEUPGRADES_ELYTRA != null ? ModItems.BONEUPGRADES_ELYTRA.get() : null, CyberwareSlot.BONE) &&
                data.hasMultipleSpecificItem(ModItems.LEGUPGRADES_JUMPBOOST.get(), 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG)) {

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
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM) && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM) &&
                data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG) && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG) &&
                data.hasSpecificItem(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN) && data.hasSpecificItem(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get(), CyberwareSlot.MUSCLE) &&
                data.hasSpecificItem(ModItems.HEARTUPGRADES_CYBERHEART.get(), CyberwareSlot.HEART) && data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE) &&
                data.hasSpecificItem(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES) && data.hasSpecificItem(ModItems.BONEUPGRADES_BONELACING.get(), CyberwareSlot.BONE) &&
                data.hasMultipleSpecificItem(ModItems.LEGUPGRADES_PROPELLERS.get(), 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG) &&
                data.hasSpecificItem(ModItems.EYEUPGRADES_UNDERWATERVISION.get(), CyberwareSlot.EYES) &&
                data.hasSpecificItem(ModItems.LUNGSUPGRADES_OXYGEN.get(), CyberwareSlot.LUNGS) &&
                data.hasSpecificItem(ModItems.WETWARE_GILLS.get(), CyberwareSlot.LUNGS)) {

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
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM) && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM) &&
                data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG) && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG) &&
                data.hasSpecificItem(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN) && data.hasSpecificItem(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get(), CyberwareSlot.MUSCLE) &&
                data.hasSpecificItem(ModItems.HEARTUPGRADES_CYBERHEART.get(), CyberwareSlot.HEART) && data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE) &&
                data.hasSpecificItem(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES) && data.hasSpecificItem(ModItems.BONEUPGRADES_BONELACING.get(), CyberwareSlot.BONE) &&
                data.hasMultipleSpecificItem(ModItems.ARMUPGRADES_PNEUMATICWRIST.get(), 2, CyberwareSlot.RARM, CyberwareSlot.LARM) &&
                data.hasMultipleSpecificItem(ModItems.LEGUPGRADES_ANKLEBRACERS.get(), 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG) &&
                data.hasSpecificItem(ModItems.LEGUPGRADES_METALDETECTOR.get(), CyberwareSlot.RLEG, CyberwareSlot.LLEG) &&
                data.hasSpecificItem(ModItems.BRAINUPGRADES_MATRIX.get(), CyberwareSlot.BRAIN) &&
                data.hasSpecificItem(ModItems.ARMUPGRADES_DRILLFIST.get(), CyberwareSlot.RARM, CyberwareSlot.LARM) &&
                data.hasMultipleSpecificItem(ModItems.ARMUPGRADES_REINFORCEDKNUCKLES.get(), 2, CyberwareSlot.RARM, CyberwareSlot.LARM)) {

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
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM) && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM) &&
                data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG) && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG) &&
                data.hasSpecificItem(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN) && data.hasSpecificItem(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get(), CyberwareSlot.MUSCLE) &&
                data.hasSpecificItem(ModItems.HEARTUPGRADES_CYBERHEART.get(), CyberwareSlot.HEART) && data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE) &&
                data.hasSpecificItem(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES) && data.hasMultipleSpecificItem(ModItems.BONEUPGRADES_BONELACING.get(), CyberwareSlot.BONE, 3) &&
                data.hasMultipleSpecificItem(ModItems.ARMUPGRADES_PNEUMATICWRIST.get(), 2, CyberwareSlot.RARM, CyberwareSlot.LARM) &&
                data.hasMultipleSpecificItem(ModItems.LEGUPGRADES_ANKLEBRACERS.get(), 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG) &&
                data.hasMultipleSpecificItem(ModItems.LEGUPGRADES_JUMPBOOST.get(), 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG) &&
                data.hasSpecificItem(ModItems.ARMUPGRADES_ARMCANNON.get(), CyberwareSlot.RARM, CyberwareSlot.LARM) &&
                data.hasSpecificItem(ModItems.EYEUPGRADES_TARGETING.get(), CyberwareSlot.EYES) &&
                data.hasSpecificItem(ModItems.BRAINUPGRADES_MATRIX.get(), CyberwareSlot.BRAIN) &&
                data.hasSpecificItem(ModItems.BONEUPGRADES_SANDEVISTAN.get(), CyberwareSlot.BONE)) {

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
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM) && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM) &&
                data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG) && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG) &&
                data.hasSpecificItem(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN) && data.hasSpecificItem(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get(), CyberwareSlot.MUSCLE) &&
                data.hasSpecificItem(ModItems.HEARTUPGRADES_CYBERHEART.get(), CyberwareSlot.HEART) && data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE) &&
                data.hasMultipleSpecificItem(ModItems.LUNGSUPGRADES_OXYGEN.get(), CyberwareSlot.LUNGS, 3) &&
                data.hasSpecificItem(ModItems.SKINUPGRADES_SOLARSKIN.get(), CyberwareSlot.SKIN)) {

            state.removeModifier(new SkinModifier(LEFT_CYBERLEG_TEXTURE, LEFT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(RIGHT_CYBERLEG_TEXTURE, RIGHT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(LEFT_CYBERARM_TEXTURE_WIDE, LEFT_CYBERARM_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(RIGHT_CYBERARM_TEXTURE_WIDE, RIGHT_CYBERARM_TEXTURE_SLIM));
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
// POLAR BEAR FUR
        if (data.hasSpecificItem(ModItems.WETWARE_POLARBEARFUR.get(), CyberwareSlot.SKIN)) {
            state.addModifier(new SkinModifier(POLAR_BEAR_FUR_TEXTURE, POLAR_BEAR_FUR_TEXTURE,
                    0xFFFFFFFF, true));
        }




        return state;
    }
}