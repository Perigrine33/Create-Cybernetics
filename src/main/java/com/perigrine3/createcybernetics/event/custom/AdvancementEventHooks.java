package com.perigrine3.createcybernetics.event.custom;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.advancement.ModCriteria;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.common.damage.ModDamageTypes;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.UUID;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class AdvancementEventHooks {
    private AdvancementEventHooks() {}

    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Post event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        ItemStack pickedUp = event.getOriginalStack();
        if (pickedUp.isEmpty()) return;

        if (pickedUp.is(ModTags.Items.SCAVENGED_CYBERWARE)) {
            ModCriteria.FIRST_SCAVENGED_CYBERWARE.get().trigger(player);
        }
        if (pickedUp.is(ModTags.Items.CYBERWARE_ITEM)) {
            ModCriteria.FIRST_CYBERWARE.get().trigger(player);
        }
    }

    @SubscribeEvent
    public static void onMobEffectAdded(MobEffectEvent.Added event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        var inst = event.getEffectInstance();
        if (inst == null) return;

        if (inst.getEffect() == ModEffects.CYBERWARE_REJECTION) {
            ModCriteria.CYBERPSYCHOSIS.get().trigger(player);
        }

        if (inst.getEffect() == ModEffects.NEUROPOZYNE) {
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return;

            int count = data.incrementNeuropozyneApplyCount();
            player.syncData(ModAttachments.CYBERWARE);

            if (count >= 100) {
                ModCriteria.CHROME_JUNKIE.get().trigger(player);
            }
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (event.getSource().is(ModDamageTypes.SURGERY)) {
            ModCriteria.TOO_MUCH_TOO_FAST.get().trigger(player);
        }
        if (event.getSource().is(ModDamageTypes.CYBERWARE_REJECTION)) {
            ModCriteria.OVER_THE_EDGE.get().trigger(player);
        }
        if (event.getSource().is(ModDamageTypes.DAVIDS_DEMISE)) {
            ModCriteria.DAVID_SPECIAL.get().trigger(player);
        }

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;
        if (ModItems.BRAINUPGRADES_CORTICALSTACK != null) {
            if (data.hasSpecificItem(ModItems.BRAINUPGRADES_CORTICALSTACK.get(), CyberwareSlot.BRAIN)) {
                ModCriteria.CORTICAL_STACK.get().trigger(player);
            }
        }
    }

    @SubscribeEvent
    public static void onSurgery(CyberwareSurgeryEvent event) {
        ServerPlayer player = event.getPlayer();

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        if (data.hasAnyTagged(ModTags.Items.CYBERWARE_ITEM, CyberwareSlot.values())) {
            ModCriteria.FIRST_RIPPERDOC_VISIT.get().trigger(player);
        }
        if (data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTARM.get(), CyberwareSlot.RARM) && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTARM.get(), CyberwareSlot.LARM) &&
                data.hasSpecificItem(ModItems.BASECYBERWARE_RIGHTLEG.get(), CyberwareSlot.RLEG) && data.hasSpecificItem(ModItems.BASECYBERWARE_LEFTLEG.get(), CyberwareSlot.LLEG) &&
                data.hasSpecificItem(ModItems.SKINUPGRADES_METALPLATING.get(), CyberwareSlot.SKIN) && !data.hasSpecificItem(ModItems.BODYPART_SKIN.get(), CyberwareSlot.SKIN) &&
                data.hasSpecificItem(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE.get(), CyberwareSlot.MUSCLE) && !data.hasSpecificItem(ModItems.BODYPART_MUSCLE.get(), CyberwareSlot.MUSCLE) &&
                data.hasSpecificItem(ModItems.HEARTUPGRADES_CYBERHEART.get(), CyberwareSlot.HEART) && !data.hasSpecificItem(ModItems.BODYPART_HEART.get(), CyberwareSlot.HEART) &&
                data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE) && !data.hasSpecificItem(ModItems.BODYPART_SKELETON.get(), CyberwareSlot.BONE) &&
                data.hasSpecificItem(ModItems.BASECYBERWARE_CYBEREYES.get(), CyberwareSlot.EYES) && !data.hasSpecificItem(ModItems.BODYPART_EYEBALLS.get(), CyberwareSlot.EYES) &&
                data.hasSpecificItem(ModItems.BONEUPGRADES_BONELACING.get(), CyberwareSlot.BONE)) {
            ModCriteria.WEAK_FLESH.get().trigger(player);
        }
        if (data.hasSpecificItem(ModItems.WETWARE_SPINNERETTE.get(), CyberwareSlot.RARM) || data.hasSpecificItem(ModItems.WETWARE_SPINNERETTE.get(), CyberwareSlot.LARM)
                && data.hasSpecificItem(ModItems.SKINUPGRADES_SYNTHETICSETULES.get(), CyberwareSlot.SKIN)) {
            ModCriteria.SPIDER_MAN.get().trigger(player);
        }

        if (!data.hasAnyTagged(ModTags.Items.BRAIN_ITEMS, CyberwareSlot.BRAIN)) {
            ModCriteria.THOUGHTLESS.get().trigger(player);
        }
        if (!data.hasAnyTagged(ModTags.Items.HEART_ITEMS, CyberwareSlot.HEART)) {
            ModCriteria.CHEST_PAINS.get().trigger(player);
        }
        if (!data.hasAnyTagged(ModTags.Items.LIVER_ITEMS, CyberwareSlot.ORGANS)) {
            ModCriteria.LIVER_REMOVED.get().trigger(player);
        }
        if (!data.hasAnyTagged(ModTags.Items.SKIN_ITEMS, CyberwareSlot.SKIN)) {
            ModCriteria.SKIN_REMOVED.get().trigger(player);
        }
        if (!data.hasAnyTagged(ModTags.Items.LUNGS_ITEMS, CyberwareSlot.LUNGS)) {
            ModCriteria.LUNGS_REMOVED.get().trigger(player);
        }
        if (!data.hasAnyTagged(ModTags.Items.BONE_ITEMS, CyberwareSlot.BONE)) {
            ModCriteria.BONELESS.get().trigger(player);
        }
        if (!data.hasAnyTagged(ModTags.Items.MUSCLE_ITEMS, CyberwareSlot.MUSCLE)) {
            ModCriteria.MISSING_MUSCLE.get().trigger(player);
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            if (!player.hasData(ModAttachments.CYBERWARE)) continue;
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) continue;

            if (data.hasChipwareShardExact(ModItems.DATA_SHARD_RED.get())) {
                ModCriteria.KUNG_FU.get().trigger(player);
            }

            if (data.hasSpecificItem(ModItems.ARMUPGRADES_ARMCANNON.get())) {
                ModCriteria.UPGRADED.get().trigger(player);
            }

            if (data.hasSpecificItem(ModItems.ARMUPGRADES_CLAWS.get())) {
                ModCriteria.SNIKT.get().trigger(player);
            }

            if (data.hasSpecificItem(ModItems.BRAINUPGRADES_CYBERBRAIN.get())) {
                ModCriteria.COGITO_ERGO_SUM.get().trigger(player);
            }
        }
    }

    @SubscribeEvent
    public static void onFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ItemStack eaten = event.getItem();
        if (eaten.isEmpty()) return;

        if (eaten.is(ModItems.BONE_MARROW.get())) {
            ModCriteria.BONES_AND_ALL.get().trigger(player);
        }
    }
}
