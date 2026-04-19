package com.perigrine3.createcybernetics.effect.quickhacks;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.EntityCyberwareData;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.ModMobAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.item.cyberware.ICEProtocolItem;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

public class ScrambleQuickhackEffect extends MobEffect {
    private static final int DEFAULT_DURATION = 100;
    private static final int DEFAULT_AMPLIFIER = 0;
    private static final float SUCCESS_CHANCE = 0.80f;

    private static final String NBT_CENTER_X = "cc_scramble_center_x";
    private static final String NBT_CENTER_Y = "cc_scramble_center_y";
    private static final String NBT_CENTER_Z = "cc_scramble_center_z";
    private static final String NBT_ANGLE = "cc_scramble_angle";
    private static final String NBT_DIRECTION = "cc_scramble_direction";

    private static final double CIRCLE_RADIUS = 2.5D;
    private static final float ANGLE_STEP_DEG = 28.0F;
    private static final double MOVE_SPEED = 1.0D;

    public ScrambleQuickhackEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF4AB3FF);
    }

    public static boolean applyQuickhack(LivingEntity target) {
        if (target == null) return false;
        if (target.level().isClientSide) return false;

        if (ICEProtocolItem.negatesQuickhack(target)) return false;
        if (!hasValidCyberlegTarget(target)) return false;

        RandomSource random = target.getRandom();
        if (random.nextFloat() > SUCCESS_CHANCE) {
            return false;
        }

        target.addEffect(new MobEffectInstance(
                ModEffects.SCRAMBLE_HACK,
                DEFAULT_DURATION,
                DEFAULT_AMPLIFIER,
                false,
                false,
                true
        ));

        return true;
    }

    private static boolean hasValidCyberlegTarget(LivingEntity target) {
        if (target instanceof Player player) {
            if (!player.hasData(ModAttachments.CYBERWARE)) return false;

            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return false;

            return data.hasAnyTagged(ModTags.Items.LEFT_CYBERLEG, CyberwareSlot.LLEG)
                    || data.hasAnyTagged(ModTags.Items.RIGHT_CYBERLEG, CyberwareSlot.RLEG)
                    || data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE);
        }

        if (!target.hasData(ModMobAttachments.CYBERENTITY_CYBERWARE)) return false;

        EntityCyberwareData data = target.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
        if (data == null) return false;

        return data.hasAnyTagged(ModTags.Items.LEFT_CYBERLEG, CyberwareSlot.LLEG)
                || data.hasAnyTagged(ModTags.Items.RIGHT_CYBERLEG, CyberwareSlot.RLEG)
                || data.hasSpecificItem(ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 4 == 0;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return true;
        if (!(entity instanceof Mob mob)) return true;

        var tag = mob.getPersistentData();

        if (!tag.contains(NBT_CENTER_X)) {
            initializeCircleState(mob);
        }

        double centerX = tag.getDouble(NBT_CENTER_X);
        double centerY = tag.getDouble(NBT_CENTER_Y);
        double centerZ = tag.getDouble(NBT_CENTER_Z);

        float angle = tag.getFloat(NBT_ANGLE);
        int direction = tag.getInt(NBT_DIRECTION);
        if (direction == 0) direction = 1;

        angle += ANGLE_STEP_DEG * direction;
        angle = Mth.wrapDegrees(angle);
        tag.putFloat(NBT_ANGLE, angle);

        double radians = Math.toRadians(angle);
        double targetX = centerX + Math.cos(radians) * CIRCLE_RADIUS;
        double targetZ = centerZ + Math.sin(radians) * CIRCLE_RADIUS;

        mob.getNavigation().moveTo(targetX, centerY, targetZ, MOVE_SPEED);
        mob.getLookControl().setLookAt(targetX, centerY, targetZ, 30.0F, 30.0F);

        return true;
    }

    private static void initializeCircleState(Mob mob) {
        var tag = mob.getPersistentData();
        RandomSource random = mob.getRandom();

        tag.putDouble(NBT_CENTER_X, mob.getX());
        tag.putDouble(NBT_CENTER_Y, mob.getY());
        tag.putDouble(NBT_CENTER_Z, mob.getZ());
        tag.putFloat(NBT_ANGLE, random.nextFloat() * 360.0F);
        tag.putInt(NBT_DIRECTION, random.nextBoolean() ? 1 : -1);
    }

    private static void clearCircleState(LivingEntity entity) {
        var tag = entity.getPersistentData();
        tag.remove(NBT_CENTER_X);
        tag.remove(NBT_CENTER_Y);
        tag.remove(NBT_CENTER_Z);
        tag.remove(NBT_ANGLE);
        tag.remove(NBT_DIRECTION);
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class Events {
        @SubscribeEvent
        public static void onEffectAdded(MobEffectEvent.Added event) {
            if (!(event.getEntity() instanceof LivingEntity living)) return;
            if (living.level().isClientSide) return;
            if (event.getEffectInstance() == null) return;
            if (event.getEffectInstance().getEffect() != ModEffects.SCRAMBLE_HACK.value()) return;

            if (living instanceof Mob mob) {
                initializeCircleState(mob);
            }
        }

        @SubscribeEvent
        public static void onEffectRemoved(MobEffectEvent.Remove event) {
            if (!(event.getEntity() instanceof LivingEntity living)) return;
            if (living.level().isClientSide) return;
            if (event.getEffectInstance() == null) return;
            if (event.getEffectInstance().getEffect() != ModEffects.SCRAMBLE_HACK.value()) return;

            clearCircleState(living);
        }

        @SubscribeEvent
        public static void onEffectExpired(MobEffectEvent.Expired event) {
            if (!(event.getEntity() instanceof LivingEntity living)) return;
            if (living.level().isClientSide) return;
            if (event.getEffectInstance() == null) return;
            if (event.getEffectInstance().getEffect() != ModEffects.SCRAMBLE_HACK.value()) return;

            clearCircleState(living);
        }

        private Events() {}
    }
}