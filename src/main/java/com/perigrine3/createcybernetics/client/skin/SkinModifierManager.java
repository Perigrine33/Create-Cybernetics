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
    private static final ResourceLocation CYBEREYES_SECONDARYY =
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
    private static final ResourceLocation FULL_BODY_HIGHLIGHT_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/full_body_highlight.png");
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
    private static final ResourceLocation FULLBODYCONVERSION_TEXTURE_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/full_body_conversion_wide.png");
    private static final ResourceLocation SYNTHSKIN_TEXTURE_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/synthskin_wide.png");
    private static final ResourceLocation NETHERPLATED_SKIN_TEXTURE_WIDE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/isothermal_skin_wide.png");

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
    private static final ResourceLocation FULLBODYCONVERSION_TEXTURE_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/full_body_conversion_slim.png");
    private static final ResourceLocation SYNTHSKIN_TEXTURE_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/synthskin_slim.png");
    private static final ResourceLocation NETHERPLATED_SKIN_TEXTURE_SLIM =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/isothermal_skin_slim.png");


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
                        tint, true));
                state.addHighlight(new SkinHighlight(CYBEREYES_PRIMARY, CYBEREYES_PRIMARY,
                        tint, false));
            }
        }
// NETHERITE PLATING
        if (data.hasSpecificItem(ModItems.SKINUPGRADES_NETHERITEPLATING.get(), CyberwareSlot.SKIN)) {
            state.removeModifier(new SkinModifier(LEFT_CYBERLEG_TEXTURE, LEFT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(RIGHT_CYBERLEG_TEXTURE, RIGHT_CYBERLEG_TEXTURE));
            state.removeModifier(new SkinModifier(LEFT_CYBERARM_TEXTURE_WIDE, LEFT_CYBERARM_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(RIGHT_CYBERARM_TEXTURE_WIDE, RIGHT_CYBERARM_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(FULLBODYCONVERSION_TEXTURE_WIDE, FULLBODYCONVERSION_TEXTURE_SLIM));
            state.removeModifier(new SkinModifier(SYNTHSKIN_TEXTURE_WIDE, SYNTHSKIN_TEXTURE_SLIM));

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
            state.removeModifier(new SkinModifier(FULLBODYCONVERSION_TEXTURE_WIDE, FULLBODYCONVERSION_TEXTURE_SLIM));
            if (data.hasSpecificItem(ModItems.WETWARE_POLARBEARFUR.get(), CyberwareSlot.SKIN)) {
                state.addModifier(new SkinModifier(POLAR_BEAR_FUR_TEXTURE, POLAR_BEAR_FUR_TEXTURE));
            }
                state.addModifier(new SkinModifier(SYNTHSKIN_TEXTURE_WIDE, SYNTHSKIN_TEXTURE_SLIM,
                        tint, false, EnumSet.noneOf(SkinModifier.HideVanilla.class), EnumSet.noneOf(HumanoidArm.class), true));
            return state;
        }
// POLAR BEAR FUR
        if (data.hasSpecificItem(ModItems.WETWARE_POLARBEARFUR.get(), CyberwareSlot.SKIN)) {
            state.addModifier(new SkinModifier(POLAR_BEAR_FUR_TEXTURE, POLAR_BEAR_FUR_TEXTURE,
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
// SPINAL INJECTOR
        if (data.hasSpecificItem(ModItems.BONEUPGRADES_SPINALINJECTOR.get(), CyberwareSlot.BONE)) {
            state.addModifier(new SkinModifier(SPINAL_INJECTOR_TEXTURE, SPINAL_INJECTOR_TEXTURE,
                    0xFFFFFFFF, false));
            SkinHighlightRender.apply(state, true, SPINAL_INJECTOR_HIGHLIGHT_TEXTURE, SPINAL_INJECTOR_HIGHLIGHT_TEXTURE,
                    0xFFFFFFFF, true);
        }
// SANDEVISTAN
        if (data.hasSpecificItem(ModItems.BONEUPGRADES_SANDEVISTAN.get(), CyberwareSlot.BONE)) {
            state.addModifier(new SkinModifier(SANDEVISTAN_TEXTURE, SANDEVISTAN_TEXTURE,
                    0xFFFFFFFF, false));
            SkinHighlightRender.apply(state, true, SANDEVISTAN_HIGHLIGHT_TEXTURE, SANDEVISTAN_HIGHLIGHT_TEXTURE,
                    0xFFFFFFFF, true);
        }
// FULL BODY
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM) && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM) &&
                data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG) && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG) &&
                data.hasSpecificItem(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN) && !data.hasSpecificItem(ModItems.BODYPART_SKIN.get(), CyberwareSlot.SKIN) &&
                data.hasSpecificItem(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get(), CyberwareSlot.MUSCLE) && !data.hasSpecificItem(ModItems.BODYPART_MUSCLE.get(), CyberwareSlot.MUSCLE) &&
                data.hasSpecificItem(ModItems.HEARTUPGRADES_CYBERHEART.get(), CyberwareSlot.HEART) && !data.hasSpecificItem(ModItems.BODYPART_HEART.get(), CyberwareSlot.HEART) &&
                data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE) && !data.hasSpecificItem(ModItems.BODYPART_SKELETON.get(), CyberwareSlot.BONE) &&
                data.hasSpecificItem(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES) && !data.hasSpecificItem(ModItems.BODYPART_EYEBALLS.get(), CyberwareSlot.EYES) &&
                data.hasSpecificItem(ModItems.BONEUPGRADES_BONELACING.get(), CyberwareSlot.BONE)) {
            state.addModifier(new SkinModifier(FULLBODYCONVERSION_TEXTURE_WIDE, FULLBODYCONVERSION_TEXTURE_SLIM,
                    0xFFFFFFFF, true));
            SkinHighlightRender.apply(state, true, FULL_BODY_HIGHLIGHT_TEXTURE, FULL_BODY_HIGHLIGHT_TEXTURE,
                    0xFFFFFFFF, true);
        }



        
        return state;
    }
}