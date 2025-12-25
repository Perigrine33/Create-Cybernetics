package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.UUID;

public class SpursEffect extends MobEffect {

    public static final ResourceLocation SPURS_EFFECT_ID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "spurs_effect");
    public static final ResourceLocation MOUNT_SPEED_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "spurs_mount_speed");

    private static final String NBT_LAST_MOUNT_UUID = "cc_spurs_lastMountUuid";
    private static final double SPEED_MULT_PER_LEVEL = 0.75;

    public SpursEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x000000);
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class Events {

        @SubscribeEvent
        public static void onMount(EntityMountEvent event) {
            if (!(event.getEntityMounting() instanceof Player player)) return;
            if (player.level().isClientSide) return;

            Entity beingMounted = event.getEntityBeingMounted();

            if (event.isMounting()) {
                if (!(beingMounted instanceof LivingEntity mount)) return;

                MobEffectInstance spurs = getEffectById(player, SPURS_EFFECT_ID);
                if (spurs == null) return;

                applyToMount(mount, spurs.getAmplifier());
                player.getPersistentData().putUUID(NBT_LAST_MOUNT_UUID, mount.getUUID());
                return;
            }

            if (event.isDismounting()) {
                if (beingMounted instanceof LivingEntity mount) {
                    removeFromMount(mount);
                } else {
                    cleanupLastMount(player);
                }
                player.getPersistentData().remove(NBT_LAST_MOUNT_UUID);
            }
        }

        @SubscribeEvent
        public static void onEffectRemoved(MobEffectEvent.Remove event) {
            cleanupIfSpurs(event.getEffectInstance(), event.getEntity());
        }

        @SubscribeEvent
        public static void onEffectExpired(MobEffectEvent.Expired event) {
            cleanupIfSpurs(event.getEffectInstance(), event.getEntity());
        }

        private static void cleanupIfSpurs(MobEffectInstance inst, LivingEntity entity) {
            if (!(entity instanceof Player player)) return;
            if (player.level().isClientSide) return;
            if (inst == null) return;

            ResourceLocation key = BuiltInRegistries.MOB_EFFECT.getKey(inst.getEffect().value());
            if (!SPURS_EFFECT_ID.equals(key)) return;

            Entity vehicle = player.getVehicle();
            if (vehicle instanceof LivingEntity mount) {
                removeFromMount(mount);
            }
            cleanupLastMount(player);
            player.getPersistentData().remove(NBT_LAST_MOUNT_UUID);
        }

        private static void cleanupLastMount(Player player) {
            CompoundTag tag = player.getPersistentData();
            if (!tag.hasUUID(NBT_LAST_MOUNT_UUID)) return;

            UUID uuid = tag.getUUID(NBT_LAST_MOUNT_UUID);

            if (player.level() instanceof ServerLevel serverLevel) {
                Entity e = serverLevel.getEntity(uuid);
                if (e instanceof LivingEntity mount) {
                    removeFromMount(mount);
                }
            }
        }

        private static MobEffectInstance getEffectById(LivingEntity entity, ResourceLocation effectId) {
            for (var entry : entity.getActiveEffectsMap().entrySet()) {
                MobEffectInstance inst = entry.getValue();
                if (inst == null) continue;
                ResourceLocation key = BuiltInRegistries.MOB_EFFECT.getKey(inst.getEffect().value());
                if (effectId.equals(key)) return inst;
            }
            return null;
        }

        private static void applyToMount(LivingEntity mount, int amplifier) {
            AttributeInstance speed = mount.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed == null) return;

            speed.removeModifier(MOUNT_SPEED_MODIFIER_ID);

            double amount = SPEED_MULT_PER_LEVEL * (amplifier + 1);

            AttributeModifier modifier = new AttributeModifier(
                    MOUNT_SPEED_MODIFIER_ID,
                    amount,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE
            );

            speed.addTransientModifier(modifier);
        }

        private static void removeFromMount(LivingEntity mount) {
            AttributeInstance speed = mount.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed == null) return;
            speed.removeModifier(MOUNT_SPEED_MODIFIER_ID);
        }
    }
}
