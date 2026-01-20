package com.perigrine3.createcybernetics.common.organs;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.common.damage.ModDamageSources;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class MissingOrganController {

    private MissingOrganController() {}
    private static final ResourceLocation NO_BONE_SPEED = ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "missing_bone_speed");
    private static final ResourceLocation NO_BONE_JUMP = ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "missing_bone_jump");
    private static final ResourceLocation NO_LEFT_LEG_SPEED = ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "missing_left_leg_speed");
    private static final ResourceLocation NO_RIGHT_LEG_SPEED = ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "missing_right_leg_speed");
    private static final ResourceLocation NO_BOTH_LEGS_JUMP = ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "missing_both_legs_jump");
    private static final String NO_LUNGS_AIR = "cc_no_lungs_air";
    private static final String FORCED_PRONE = "cc_forced_prone";
    private static final ResourceLocation NO_MUSCLE_SPEED = ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "missing_muscle_speed");
    private static final ResourceLocation NO_MUSCLE_ATTACK = ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "missing_muscle_attack");
    private static final ResourceLocation NO_MUSCLE_JUMP = ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "missing_muscle_jump");


    private static final String DEFAULTS_PATCHED = "cc_defaults_patched";


    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;


// Resolve organ presence
        boolean hasBrain = data.hasAnyTagged(ModTags.Items.BRAIN_ITEMS, CyberwareSlot.BRAIN) || data.hasAnyInSlots(CyberwareSlot.BRAIN);
        boolean hasEyes = data.hasAnyTagged(ModTags.Items.EYE_ITEMS, CyberwareSlot.EYES);
        boolean hasHeart = data.hasAnyTagged(ModTags.Items.HEART_ITEMS, CyberwareSlot.HEART);
        boolean hasLungs = data.hasAnyTagged(ModTags.Items.LUNGS_ITEMS, CyberwareSlot.LUNGS);

        boolean hasLiver = data.hasAnyTagged(ModTags.Items.LIVER_ITEMS, CyberwareSlot.ORGANS);
        boolean hasIntestines = data.hasAnyTagged(ModTags.Items.INTESTINES_ITEMS, CyberwareSlot.ORGANS);

        boolean hasBone = data.hasAnyTagged(ModTags.Items.BONE_ITEMS, CyberwareSlot.BONE);
        boolean hasMuscle = data.hasAnyTagged(ModTags.Items.MUSCLE_ITEMS, CyberwareSlot.MUSCLE);
        boolean hasSkin = data.hasAnyTagged(ModTags.Items.SKIN_ITEMS, CyberwareSlot.SKIN);

        boolean hasLeftArm = data.hasAnyTagged(ModTags.Items.LEFTARM_ITEMS, CyberwareSlot.LARM);
        boolean hasRightArm = data.hasAnyTagged(ModTags.Items.RIGHTARM_ITEMS, CyberwareSlot.RARM);
        boolean hasLeftLeg = data.hasAnyTagged(ModTags.Items.LEFTLEG_ITEMS, CyberwareSlot.LLEG);
        boolean hasRightLeg = data.hasAnyTagged(ModTags.Items.RIGHTLEG_ITEMS, CyberwareSlot.RLEG);

        boolean hasArms = hasLeftArm || hasRightArm;
        boolean hasLegs = hasLeftLeg || hasRightLeg;


        /* -------------------- SAFETY PATCH -------------------- */
        if (!player.getPersistentData().getBoolean(DEFAULTS_PATCHED)) {
            boolean hasBrainNow = hasAnyTagged(data, ModTags.Items.BRAIN_ITEMS, CyberwareSlot.BRAIN);
            if (!hasBrainNow) {
                data.resetToDefaultOrgans();
                data.setDirty();
                player.syncData(ModAttachments.CYBERWARE);

            }
            player.getPersistentData().putBoolean(DEFAULTS_PATCHED, true);
        }


        /* -------------------- BRAIN -------------------- */
        if (!hasBrain) {
            player.hurt(ModDamageSources.brainDamage(player.level(), player, null), 500000);
            return;
        }

        /* -------------------- EYES -------------------- */
        if (!hasEyes) {
            refreshEffect(player, MobEffects.BLINDNESS, 220, 1);
            refreshEffect(player, MobEffects.DARKNESS, 220, 0);

        } else {
            player.removeEffect(MobEffects.BLINDNESS);
            player.removeEffect(MobEffects.DARKNESS);
        }


        /* -------------------- HEART -------------------- */
        if (!hasHeart && player.tickCount % 20 == 0) {
            player.hurt(ModDamageSources.heartAttack(player.level(), player, null), 4);
        }

        /* -------------------- LUNGS -------------------- */
        boolean hasGills = data.hasSpecificItem(ModItems.WETWARE_GILLS.get(), CyberwareSlot.LUNGS);
        boolean underwater = player.isUnderWater();

        if (!hasLungs) {
            boolean canBreatheHere = hasGills && underwater;

            if (canBreatheHere) {
                player.getPersistentData().remove(NO_LUNGS_AIR);
                player.setAirSupply(player.getMaxAirSupply());
            } else {
                CompoundTag pd = player.getPersistentData();

                int air = pd.contains(NO_LUNGS_AIR, Tag.TAG_INT)
                        ? pd.getInt(NO_LUNGS_AIR)
                        : player.getAirSupply();

                air -= 1;

                if (air <= -20) {
                    player.hurt(ModDamageSources.missingLungs(player.level(), player, null), 2);
                    air = 0;
                }

                pd.putInt(NO_LUNGS_AIR, air);
                player.setAirSupply(air);
            }
        } else {
            // Lungs present: clear custom tracking.
            player.getPersistentData().remove(NO_LUNGS_AIR);
        }

        /* -------------------- LIVER -------------------- */
        if (!hasLiver) {
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 60, 1, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0, true, false));
            player.hurt(ModDamageSources.liverFailure(player.level(), player, null), 6);
        }

        /* -------------------- INTESTINES -------------------- */
        // This is enforced by onItemUse

        /* -------------------- BONE -------------------- */
        if (!hasBone) {
            applyOrRemoveModifier(player, Attributes.MOVEMENT_SPEED, NO_BONE_SPEED, true, -0.85);
            applyOrRemoveModifier(player, Attributes.JUMP_STRENGTH, NO_BONE_JUMP, true, -0.7);

            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 4, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 1, true, false));

            player.hurt(ModDamageSources.boneless(player.level(), player, null), 8);

            forceProneLike(player);
        } else {
            applyOrRemoveModifier(player, Attributes.MOVEMENT_SPEED, NO_BONE_SPEED, false, 0);
            applyOrRemoveModifier(player, Attributes.JUMP_STRENGTH, NO_BONE_JUMP, false, 0);

            clearProneLike(player);
        }

        /* -------------------- MUSCLE -------------------- */
        if (!hasMuscle) {
            applyOrRemoveModifier(player, Attributes.MOVEMENT_SPEED, NO_MUSCLE_SPEED, true, -1.0);
            applyOrRemoveModifier(player, Attributes.ATTACK_SPEED, NO_MUSCLE_ATTACK, true, -1.0);
            applyOrRemoveModifier(player, Attributes.JUMP_STRENGTH, NO_MUSCLE_JUMP, true, -1.0);

            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 4, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 1, true, false));

            player.hurt(ModDamageSources.missingMuscle(player.level(), player, null), 11);

            forceProneLike(player);
        } else {
            applyOrRemoveModifier(player, Attributes.MOVEMENT_SPEED, NO_MUSCLE_SPEED, false, 0);
            applyOrRemoveModifier(player, Attributes.ATTACK_SPEED, NO_MUSCLE_ATTACK, false, 0);
            applyOrRemoveModifier(player, Attributes.JUMP_STRENGTH, NO_MUSCLE_JUMP, false, 0);

            clearProneLike(player);
        }

        /* -------------------- SKIN -------------------- */
        if (!hasSkin) {
            if (player.horizontalCollision || player.verticalCollision || player.tickCount % 100 == 0) {
                player.hurt(ModDamageSources.missingSkin(player.level(), player, null), 2f);
            }
        }

        /* -------------------- ARMS -------------------- */
        if (!hasLeftArm) {
            enforceOffhandEmpty(player);
        }


        /* -------------------- LEGS -------------------- */
        applyOrRemoveModifier(player, Attributes.MOVEMENT_SPEED, NO_LEFT_LEG_SPEED, !hasLeftLeg, -1.0);
        applyOrRemoveModifier(player, Attributes.MOVEMENT_SPEED, NO_RIGHT_LEG_SPEED, !hasRightLeg, -1.0);

        applyOrRemoveModifier(player, Attributes.JUMP_STRENGTH, NO_BOTH_LEGS_JUMP, (!hasLeftLeg && !hasRightLeg), -0.35);

        if (!hasLegs) {
            player.setSprinting(false);
        }

    }


    /* -------------------- HELPERS -------------------- */
    private static void forceProneLike(Player player) {
        CompoundTag pd = player.getPersistentData();
        pd.putBoolean(FORCED_PRONE, true);

        player.setSprinting(false);
        player.setSwimming(true);

        try {
            player.setPose(Pose.SWIMMING);
        } catch (Throwable ignored) {}
    }

    private static void refreshEffect(Player player, Holder<MobEffect> effect, int durationTicks, int amplifier) {
        MobEffectInstance cur = player.getEffect(effect);

        if (cur == null || cur.getDuration() < 40 || cur.getAmplifier() != amplifier) {
            player.addEffect(new MobEffectInstance(effect, durationTicks, amplifier, true, false, false));
        }
    }

    private static void clearProneLike(Player player) {
        CompoundTag pd = player.getPersistentData();
        if (!pd.getBoolean(FORCED_PRONE)) return;

        pd.remove(FORCED_PRONE);
        player.setSwimming(false);
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        boolean hasMuscle = data.hasAnyTagged(ModTags.Items.MUSCLE_ITEMS, CyberwareSlot.MUSCLE);
        if (!hasMuscle) {
            event.setNewSpeed(0.0F);
        }
    }

    @SubscribeEvent
    public static void onBreakBlock(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;
        if (player.level().isClientSide) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        boolean hasMuscle = data.hasAnyTagged(ModTags.Items.MUSCLE_ITEMS, CyberwareSlot.MUSCLE);
        if (!hasMuscle) {
            event.setCanceled(true);
        }
    }


    @SubscribeEvent
    public static void onItemUse(LivingEntityUseItemEvent.Start event) {
        if (!(event.getEntity() instanceof Player player)) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        if (!data.hasAnyTagged(ModTags.Items.INTESTINES_ITEMS, CyberwareSlot.ORGANS)
                && event.getItem().getFoodProperties(player) != null) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        boolean hasMuscle = data.hasAnyTagged(ModTags.Items.MUSCLE_ITEMS, CyberwareSlot.MUSCLE);
        if (!hasMuscle) {
            event.setCanceled(true);
            return;
        }

        boolean hasLeftArm = data.hasAnyTagged(ModTags.Items.LEFTARM_ITEMS, CyberwareSlot.LARM);
        boolean hasRightArm = data.hasAnyTagged(ModTags.Items.RIGHTARM_ITEMS, CyberwareSlot.RARM);

        net.minecraft.world.entity.HumanoidArm mainArm = player.getMainArm();

        boolean mainHandWorks =
                (mainArm == net.minecraft.world.entity.HumanoidArm.RIGHT && hasRightArm)
                        || (mainArm == net.minecraft.world.entity.HumanoidArm.LEFT && hasLeftArm);

        if (!mainHandWorks) {
            event.setCanceled(true);
        }
    }

    private static boolean hasAny(PlayerCyberwareData data, CyberwareSlot... slots) {
        for (CyberwareSlot slot : slots) {
            var arr = data.getAll().get(slot);
            if (arr == null) continue;

            for (var installed : arr) {
                if (installed != null && !installed.getItem().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasAnyTagged(PlayerCyberwareData data, TagKey<Item> tag, CyberwareSlot... slots) {
        for (CyberwareSlot slot : slots) {
            var arr = data.getAll().get(slot);
            if (arr == null) continue;

            for (var installed : arr) {
                if (installed == null) continue;
                if (installed.getItem().isEmpty()) continue;
                if (installed.getItem().is(tag)) return true;
            }
        }
        return false;
    }

    private static void enforceOffhandEmpty(Player player) {
        ItemStack off = player.getOffhandItem();
        if (off.isEmpty()) return;

        ItemStack removed = off.copy();
        player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);

        if (!player.getInventory().add(removed)) {
            player.drop(removed, false);
        }

        player.inventoryMenu.broadcastChanges();
    }


    private static void applyOrRemoveModifier(Player player, net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute, ResourceLocation id, boolean apply, double amount) {
        var attr = player.getAttribute(attribute);
        if (attr == null) return;

        if (apply) {
            if (attr.getModifier(id) == null) {
                attr.addTransientModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
        } else {
            attr.removeModifier(id);
        }
    }

    private static boolean handWorks(Player player, net.minecraft.world.InteractionHand hand, boolean hasLeftArm, boolean hasRightArm) {

        net.minecraft.world.entity.HumanoidArm mainArm = player.getMainArm();

        boolean usingRightArm =
                (hand == net.minecraft.world.InteractionHand.MAIN_HAND && mainArm == net.minecraft.world.entity.HumanoidArm.RIGHT)
                        || (hand == net.minecraft.world.InteractionHand.OFF_HAND && mainArm == net.minecraft.world.entity.HumanoidArm.LEFT);

        return usingRightArm ? hasRightArm : hasLeftArm;
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        boolean hasMuscle = data.hasAnyTagged(ModTags.Items.MUSCLE_ITEMS, CyberwareSlot.MUSCLE);
        if (!hasMuscle) {
            event.setCanceled(true);
            return;
        }

        boolean hasLeftArm = data.hasAnyTagged(ModTags.Items.LEFTARM_ITEMS, CyberwareSlot.LARM);
        boolean hasRightArm = data.hasAnyTagged(ModTags.Items.RIGHTARM_ITEMS, CyberwareSlot.RARM);

        if (!handWorks(player, event.getHand(), hasLeftArm, hasRightArm)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        boolean hasMuscle = data.hasAnyTagged(ModTags.Items.MUSCLE_ITEMS, CyberwareSlot.MUSCLE);
        if (!hasMuscle) {
            event.setCanceled(true);
            return;
        }

        boolean hasLeftArm = data.hasAnyTagged(ModTags.Items.LEFTARM_ITEMS, CyberwareSlot.LARM);
        boolean hasRightArm = data.hasAnyTagged(ModTags.Items.RIGHTARM_ITEMS, CyberwareSlot.RARM);

        if (!handWorks(player, event.getHand(), hasLeftArm, hasRightArm)) {
            event.setCanceled(true);
        }
    }
}
