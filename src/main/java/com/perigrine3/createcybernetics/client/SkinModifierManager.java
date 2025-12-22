package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

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
    
    private static final ResourceLocation MISSING_SKIN_TEXTURE = 
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/playermuscles.png");
    private static final ResourceLocation SYNTHSKIN_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/playermuscles.png");
    private static final ResourceLocation NETHERPLATED_SKIN_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/playermuscles.png");


    public static SkinModifierState getPlayerSkinState(AbstractClientPlayer player) {
        if (!player.hasData(ModAttachments.CYBERWARE)) return null;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return null;

        UUID playerId = player.getUUID();
        SkinModifierState state = PLAYER_STATES.computeIfAbsent(playerId, k -> new SkinModifierState());
        state.clearModifiers();
        
// MISSING SKIN
        if (!data.hasAnyTagged(ModTags.Items.SKIN_ITEMS, CyberwareSlot.SKIN)) {
            state.addModifier(new SkinModifier(MISSING_SKIN_TEXTURE));
        }
// SYNTHSKIN
        if (data.hasSpecificItem(ModItems.SKINUPGRADES_SYNTHSKIN.get(), CyberwareSlot.SKIN)) {
            state.addModifier(new SkinModifier(SYNTHSKIN_TEXTURE));
            return state;
        }
// NETHERITE PLATING
        if (data.hasSpecificItem(ModItems.SKINUPGRADES_NETHERITEPLATING.get(), CyberwareSlot.SKIN)) {
            state.addModifier(new SkinModifier(NETHERPLATED_SKIN_TEXTURE));
            return state;
        }
        
        return state;
    }
}