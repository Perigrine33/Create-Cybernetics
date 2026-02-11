package com.perigrine3.createcybernetics.event.custom;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.compat.ModCompats;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class FullBorgHandler {
    private FullBorgHandler() {}

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!player.hasData(ModAttachments.CYBERWARE)) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        boolean geminiModel = isGemini(data);
        if (geminiModel) {
            CyberwareAttributeHelper.applyModifier(player, "gemini_attackstrength");
            CyberwareAttributeHelper.applyModifier(player, "gemini_attackspeed");
            CyberwareAttributeHelper.applyModifier(player, "gemini_miningstrength");
            CyberwareAttributeHelper.applyModifier(player, "gemini_speed");
        } else {
            CyberwareAttributeHelper.removeModifier(player, "gemini_attackstrength");
            CyberwareAttributeHelper.removeModifier(player, "gemini_attackspeed");
            CyberwareAttributeHelper.removeModifier(player, "gemini_miningstrength");
            CyberwareAttributeHelper.removeModifier(player, "gemini_speed");
        }

        boolean samsonModel = isSamson(data);
        if (samsonModel) {
            CyberwareAttributeHelper.applyModifier(player, "samson_attackstrength");
            CyberwareAttributeHelper.applyModifier(player, "samson_miningstrength");
            CyberwareAttributeHelper.applyModifier(player, "samson_durability");
            CyberwareAttributeHelper.applyModifier(player, "samson_watermove");
            CyberwareAttributeHelper.applyModifier(player, "samson_weight");
        } else {
            CyberwareAttributeHelper.removeModifier(player, "samson_attackstrength");
            CyberwareAttributeHelper.removeModifier(player, "samson_miningstrength");
            CyberwareAttributeHelper.removeModifier(player, "samson_durability");
            CyberwareAttributeHelper.removeModifier(player, "samson_watermove");
            CyberwareAttributeHelper.removeModifier(player, "samson_weight");
        }

        boolean eclipseModel = isEclipse(data);
        if (eclipseModel) {
            CyberwareAttributeHelper.applyModifier(player, "eclipse_crouchspeed");
            CyberwareAttributeHelper.applyModifier(player, "eclipse_speed");
            if (player.isSprinting()) {
                CyberwareAttributeHelper.applyModifier(player, "eclipse_sprintspeed");
            } else {
                CyberwareAttributeHelper.removeModifier(player, "eclipse_sprintspeed");
            }
        } else {
            CyberwareAttributeHelper.removeModifier(player, "eclipse_crouchspeed");
            CyberwareAttributeHelper.removeModifier(player, "eclipse_speed");
            CyberwareAttributeHelper.removeModifier(player, "eclipse_sprintspeed");
        }

        boolean spyderModel = isSpyder(data);
        if (spyderModel) {
            CyberwareAttributeHelper.applyModifier(player, "spyder_crouchspeed");
            CyberwareAttributeHelper.applyModifier(player, "spyder_jumpheight");
        } else {
            CyberwareAttributeHelper.removeModifier(player, "spyder_crouchspeed");
            CyberwareAttributeHelper.removeModifier(player, "spyder_jumpheight");
        }

        if (ModCompats.isInstalled("caelus")) {
            boolean wingmanModel = isWingman(data);
            if (wingmanModel) {
                CyberwareAttributeHelper.applyModifier(player, "wingman_elytraspeed");
                CyberwareAttributeHelper.applyModifier(player, "wingman_elytrahandling");
                if (player.isShiftKeyDown()) {
                    CyberwareAttributeHelper.removeModifier(player, "wingman_elytraspeed");
                }
            } else {
                CyberwareAttributeHelper.removeModifier(player, "wingman_elytraspeed");
                CyberwareAttributeHelper.removeModifier(player, "wingman_elytrahandling");
            }
        }

        boolean aquariusModel = isAquarius(data);
        if (aquariusModel) {
            CyberwareAttributeHelper.applyModifier(player, "aquarius_movement");
            CyberwareAttributeHelper.applyModifier(player, "aquarius_mining");
            CyberwareAttributeHelper.applyModifier(player, "aquarius_swim");
        } else {
            CyberwareAttributeHelper.removeModifier(player, "aquarius_movement");
            CyberwareAttributeHelper.removeModifier(player, "aquarius_mining");
            CyberwareAttributeHelper.removeModifier(player, "aquarius_swim");
        }

        boolean dymondModel = isDymond(data);
        if (dymondModel) {
            CyberwareAttributeHelper.applyModifier(player, "dymond_miningspeed");
            CyberwareAttributeHelper.applyModifier(player, "dymond_weight");
        } else {
            CyberwareAttributeHelper.removeModifier(player, "dymond_miningspeed");
            CyberwareAttributeHelper.removeModifier(player, "dymond_weight");
        }

        boolean dragoonModel = isDragoon(data);
        if (dragoonModel) {
            CyberwareAttributeHelper.applyModifier(player, "dragoon_weight");
            CyberwareAttributeHelper.applyModifier(player, "dragoon_size");
            CyberwareAttributeHelper.applyModifier(player, "dragoon_attack");
            CyberwareAttributeHelper.applyModifier(player, "dragoon_resist");
            CyberwareAttributeHelper.applyModifier(player, "dragoon_knockback");
        } else {
            CyberwareAttributeHelper.removeModifier(player, "dragoon_weight");
            CyberwareAttributeHelper.removeModifier(player, "dragoon_size");
            CyberwareAttributeHelper.removeModifier(player, "dragoon_attack");
            CyberwareAttributeHelper.removeModifier(player, "dragoon_resist");
            CyberwareAttributeHelper.removeModifier(player, "dragoon_knockback");
        }

        boolean copernicusModel = isCopernicus(data);
        if (copernicusModel) {
            CyberwareAttributeHelper.applyModifier(player, "copernicus_oxygen");
        } else {
            CyberwareAttributeHelper.removeModifier(player, "copernicus_oxygen");
        }

        boolean genosModel = isGenos(data);
        if (genosModel) {
            CyberwareAttributeHelper.applyModifier(player, "genos_strength");
            if (player.isSprinting()) {
                CyberwareAttributeHelper.applyModifier(player, "genos_speed");
            } else {
                CyberwareAttributeHelper.removeModifier(player, "genos_speed");
            }
        } else {
            CyberwareAttributeHelper.removeModifier(player, "genos_strength");
            CyberwareAttributeHelper.removeModifier(player, "genos_speed");
        }
    }

    /* -------------------- Model predicates (reused by other systems) -------------------- */

    public static boolean isGemini(PlayerCyberwareData data) {
        if (data == null) return false;
        return data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG)
                && data.hasSpecificItem(ModItems.SKINUPGRADES_SYNTHSKIN.get(), CyberwareSlot.SKIN)
                && data.hasSpecificItem(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get(), CyberwareSlot.MUSCLE)
                && data.hasSpecificItem(ModItems.HEARTUPGRADES_CYBERHEART.get(), CyberwareSlot.HEART)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES)
                && data.hasSpecificItem(ModItems.BONEUPGRADES_BONELACING.get(), CyberwareSlot.BONE);
    }

    public static boolean isSamson(PlayerCyberwareData data) {
        if (data == null) return false;
        return data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG)
                && data.hasSpecificItem(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN)
                && data.hasSpecificItem(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get(), CyberwareSlot.MUSCLE)
                && data.hasSpecificItem(ModItems.HEARTUPGRADES_CYBERHEART.get(), CyberwareSlot.HEART)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES)
                && data.hasMultipleSpecificItem(ModItems.BONEUPGRADES_BONELACING.get(), CyberwareSlot.BONE, 3)
                && data.hasMultipleSpecificItem(ModItems.SKINUPGRADES_SUBDERMALARMOR.get(), CyberwareSlot.SKIN, 3)
                && data.hasMultipleSpecificItem(ModItems.ARMUPGRADES_PNEUMATICWRIST.get(), 2, CyberwareSlot.RARM, CyberwareSlot.LARM);
    }

    public static boolean isEclipse(PlayerCyberwareData data) {
        if (data == null) return false;
        return data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG)
                && data.hasSpecificItem(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN)
                && data.hasSpecificItem(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get(), CyberwareSlot.MUSCLE)
                && data.hasSpecificItem(ModItems.HEARTUPGRADES_CYBERHEART.get(), CyberwareSlot.HEART)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES)
                && data.hasSpecificItem(ModItems.BONEUPGRADES_BONELACING.get(), CyberwareSlot.BONE)
                && data.hasMultipleSpecificItem(ModItems.LUNGSUPGRADES_HYPEROXYGENATION.get(), CyberwareSlot.LUNGS, 3)
                && data.hasMultipleSpecificItem(ModItems.LEGUPGRADES_OCELOTPAWS.get(), 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG)
                && data.hasSpecificItem(ModItems.SKINUPGRADES_CHROMATOPHORES.get(), CyberwareSlot.SKIN)
                && data.hasSpecificItem(ModItems.BONEUPGRADES_SANDEVISTAN.get(), CyberwareSlot.BONE);
    }

    public static boolean isSpyder(PlayerCyberwareData data) {
        if (data == null) return false;
        return data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG)
                && data.hasSpecificItem(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN)
                && data.hasSpecificItem(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get(), CyberwareSlot.MUSCLE)
                && data.hasSpecificItem(ModItems.HEARTUPGRADES_CYBERHEART.get(), CyberwareSlot.HEART)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE)
                && data.hasMultipleSpecificItem(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES, 3)
                && data.hasSpecificItem(ModItems.BONEUPGRADES_BONELACING.get(), CyberwareSlot.BONE)
                && data.hasMultipleSpecificItem(ModItems.LEGUPGRADES_JUMPBOOST.get(), 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG)
                && data.hasMultipleSpecificItem(ModItems.LEGUPGRADES_ANKLEBRACERS.get(), 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG)
                && data.hasMultipleSpecificItem(ModItems.LEGUPGRADES_OCELOTPAWS.get(), 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG)
                && data.hasSpecificItem(ModItems.SKINUPGRADES_CHROMATOPHORES.get(), CyberwareSlot.SKIN)
                && data.hasSpecificItem(ModItems.SKINUPGRADES_SYNTHETICSETULES.get(), CyberwareSlot.SKIN);
    }

    public static boolean isWingman(PlayerCyberwareData data) {
        if (data == null) return false;
        if (ModItems.BONEUPGRADES_ELYTRA != null) {
            return data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM)
                    && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM)
                    && data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG)
                    && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG)
                    && data.hasSpecificItem(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN)
                    && data.hasSpecificItem(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get(), CyberwareSlot.MUSCLE)
                    && data.hasSpecificItem(ModItems.HEARTUPGRADES_CYBERHEART.get(), CyberwareSlot.HEART)
                    && data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE)
                    && data.hasSpecificItem(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES)
                    && data.hasSpecificItem(ModItems.BONEUPGRADES_BONELACING.get(), CyberwareSlot.BONE)
                    && data.hasSpecificItem(ModItems.BONEUPGRADES_CYBERSKULL.get(), CyberwareSlot.BONE)
                    && data.hasSpecificItem(ModItems.BONEUPGRADES_ELYTRA.get(), CyberwareSlot.BONE)
                    && data.hasMultipleSpecificItem(ModItems.LEGUPGRADES_JUMPBOOST.get(), 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG);

        }
        return false;
    }

    public static boolean isAquarius(PlayerCyberwareData data) {
        if (data == null) return false;
        return data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG)
                && data.hasSpecificItem(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN)
                && data.hasSpecificItem(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get(), CyberwareSlot.MUSCLE)
                && data.hasSpecificItem(ModItems.HEARTUPGRADES_CYBERHEART.get(), CyberwareSlot.HEART)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES)
                && data.hasSpecificItem(ModItems.BONEUPGRADES_BONELACING.get(), CyberwareSlot.BONE)
                && data.hasMultipleSpecificItem(ModItems.LEGUPGRADES_PROPELLERS.get(), 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG)
                && data.hasSpecificItem(ModItems.EYEUPGRADES_UNDERWATERVISION.get(), CyberwareSlot.EYES)
                && data.hasSpecificItem(ModItems.LUNGSUPGRADES_OXYGEN.get(), CyberwareSlot.LUNGS)
                && data.hasSpecificItem(ModItems.WETWARE_WATERBREATHINGLUNGS.get(), CyberwareSlot.LUNGS);
    }

    public static boolean isDymond(PlayerCyberwareData data) {
        if (data == null) return false;
        return data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG)
                && data.hasSpecificItem(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN)
                && data.hasSpecificItem(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get(), CyberwareSlot.MUSCLE)
                && data.hasSpecificItem(ModItems.HEARTUPGRADES_CYBERHEART.get(), CyberwareSlot.HEART)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES)
                && data.hasSpecificItem(ModItems.BONEUPGRADES_BONELACING.get(), CyberwareSlot.BONE)
                && data.hasMultipleSpecificItem(ModItems.ARMUPGRADES_PNEUMATICWRIST.get(), 2, CyberwareSlot.RARM, CyberwareSlot.LARM)
                && data.hasMultipleSpecificItem(ModItems.LEGUPGRADES_ANKLEBRACERS.get(), 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG)
                && data.hasSpecificItem(ModItems.LEGUPGRADES_METALDETECTOR.get(), CyberwareSlot.RLEG, CyberwareSlot.LLEG)
                && data.hasSpecificItem(ModItems.BRAINUPGRADES_MATRIX.get(), CyberwareSlot.BRAIN)
                && data.hasSpecificItem(ModItems.ARMUPGRADES_DRILLFIST.get(), CyberwareSlot.RARM, CyberwareSlot.LARM)
                && data.hasMultipleSpecificItem(ModItems.ARMUPGRADES_REINFORCEDKNUCKLES.get(), 2, CyberwareSlot.RARM, CyberwareSlot.LARM);
    }

    public static boolean isDragoon(PlayerCyberwareData data) {
        if (data == null) return false;
        return data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG)
                && data.hasSpecificItem(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN)
                && data.hasSpecificItem(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get(), CyberwareSlot.MUSCLE)
                && data.hasSpecificItem(ModItems.HEARTUPGRADES_CYBERHEART.get(), CyberwareSlot.HEART)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES)
                && data.hasMultipleSpecificItem(ModItems.BONEUPGRADES_BONELACING.get(), CyberwareSlot.BONE, 3)
                && data.hasMultipleSpecificItem(ModItems.ARMUPGRADES_PNEUMATICWRIST.get(), 2, CyberwareSlot.RARM, CyberwareSlot.LARM)
                && data.hasMultipleSpecificItem(ModItems.LEGUPGRADES_ANKLEBRACERS.get(), 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG)
                && data.hasMultipleSpecificItem(ModItems.LEGUPGRADES_JUMPBOOST.get(), 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG)
                && data.hasSpecificItem(ModItems.ARMUPGRADES_ARMCANNON.get(), CyberwareSlot.RARM, CyberwareSlot.LARM)
                && data.hasSpecificItem(ModItems.EYEUPGRADES_TARGETING.get(), CyberwareSlot.EYES)
                && data.hasSpecificItem(ModItems.BRAINUPGRADES_MATRIX.get(), CyberwareSlot.BRAIN)
                && data.hasSpecificItem(ModItems.BONEUPGRADES_SANDEVISTAN.get(), CyberwareSlot.BONE);
    }

    public static boolean isCopernicus(PlayerCyberwareData data) {
        if (data == null) return false;
        return data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG)
                && data.hasSpecificItem(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN)
                && data.hasSpecificItem(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get(), CyberwareSlot.MUSCLE)
                && data.hasSpecificItem(ModItems.HEARTUPGRADES_CYBERHEART.get(), CyberwareSlot.HEART)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE)
                && data.hasMultipleSpecificItem(ModItems.LUNGSUPGRADES_OXYGEN.get(), CyberwareSlot.LUNGS, 3)
                && data.hasSpecificItem(ModItems.SKINUPGRADES_SOLARSKIN.get(), CyberwareSlot.SKIN)
                && data.hasSpecificItem(ModItems.SKINUPGRADES_NETHERITEPLATING.get(), CyberwareSlot.SKIN)
                && data.hasSpecificItem(ModItems.EYEUPGRADES_ZOOM.get(), CyberwareSlot.EYES)
                && data.hasSpecificItem(ModItems.EYEUPGRADES_HUDJACK.get(), CyberwareSlot.EYES)
                && data.hasSpecificItem(ModItems.ARMUPGRADES_CRAFTHANDS.get(), CyberwareSlot.LARM, CyberwareSlot.RARM);
    }

    public static boolean isGenos(PlayerCyberwareData data) {
        if (data == null) return false;
        return data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG)
                && data.hasSpecificItem(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN)
                && data.hasSpecificItem(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get(), CyberwareSlot.MUSCLE)
                && data.hasSpecificItem(ModItems.HEARTUPGRADES_CYBERHEART.get(), CyberwareSlot.HEART)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE)
                && data.hasSpecificItem(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES)
                && data.hasMultipleSpecificItem(ModItems.BONEUPGRADES_BONELACING.get(), CyberwareSlot.BONE, 2)
                && data.hasMultipleSpecificItem(ModItems.ARMUPGRADES_PNEUMATICWRIST.get(), 2, CyberwareSlot.RARM, CyberwareSlot.LARM)
                && data.hasMultipleSpecificItem(ModItems.LUNGSUPGRADES_HYPEROXYGENATION.get(), 3, CyberwareSlot.LUNGS)
                && data.hasMultipleSpecificItem(ModItems.LEGUPGRADES_ANKLEBRACERS.get(), 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG)
                && data.hasMultipleSpecificItem(ModItems.LEGUPGRADES_JUMPBOOST.get(), 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG)
                && data.hasSpecificItem(ModItems.ARMUPGRADES_ARMCANNON.get(), CyberwareSlot.RARM, CyberwareSlot.LARM)
                && data.hasSpecificItem(ModItems.EYEUPGRADES_TARGETING.get(), CyberwareSlot.EYES)
                && data.hasSpecificItem(ModItems.EYEUPGRADES_HUDJACK.get(), CyberwareSlot.EYES)
                && data.hasSpecificItem(ModItems.MUSCLEUPGRADES_WIREDREFLEXES.get(), CyberwareSlot.MUSCLE)
                && data.hasSpecificItem(ModItems.BRAINUPGRADES_MATRIX.get(), CyberwareSlot.BRAIN);
    }

    public static boolean isFullBorg(ServerPlayer player) {
        if (player == null) return false;
        if (!player.hasData(ModAttachments.CYBERWARE)) return false;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        if (isGemini(data)) return true;
        if (isSamson(data)) return true;
        if (isEclipse(data)) return true;
        if (isSpyder(data)) return true;
        if (ModCompats.isInstalled("caelus") && isWingman(data)) return true;
        if (isAquarius(data)) return true;
        if (isDymond(data)) return true;
        if (isDragoon(data)) return true;
        if (isCopernicus(data)) return true;
        if (isGenos(data)) return true;

        return false;
    }

    public static boolean hasAnyImplantsAtAll(ServerPlayer player) {
        if (player == null) return false;
        if (!player.hasData(ModAttachments.CYBERWARE)) return false;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data.getAll() == null || data.getAll().isEmpty()) {
            CyberwareAttributeHelper.removeModifier(player, "irons_lightning_weakness");
            return false;
        }

        boolean any = false;

        for (var entry : data.getAll().entrySet()) {
            CyberwareSlot slot = entry.getKey();
            var arr = entry.getValue();
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {
                var installed = arr[i];
                if (installed == null) continue;

                ItemStack st = installed.getItem();
                if (st == null || st.isEmpty()) continue;
                if (st.is(ModTags.Items.CYBERWARE_ITEM)) {
                    any = true;
                    break;
                }
            }

            if (any) break;
        }

        if (any) {
            CyberwareAttributeHelper.applyModifier(player, "irons_lightning_weakness");
        } else {
            CyberwareAttributeHelper.removeModifier(player, "irons_lightning_weakness");
        }

        return any;
    }

}
