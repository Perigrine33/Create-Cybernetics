package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.*;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class EmpEffect extends MobEffect {

    public EmpEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF4AB3FF);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) {
            return true;
        }

        if (entity instanceof Player player) {
            applyToPlayer(player);
            return true;
        }

        applyToCyberentity(entity);
        return true;
    }

    private static void applyToPlayer(Player player) {
        if (!player.hasData(ModAttachments.CYBERWARE)) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;
        if (!hasAnyInstalledCyberware(data)) return;

        if (!data.hasSpecificItem(ModItems.BONEUPGRADES_CAPACITORFRAME.get(), CyberwareSlot.BONE)) {
            data.setEnergyStored(player, 0);
        }
    }

    private static void applyToCyberentity(LivingEntity entity) {
        if (!entity.hasData(ModMobAttachments.CYBERENTITY_CYBERWARE)) return;

        EntityCyberwareData data = entity.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
        if (data == null) return;
        if (!hasAnyInstalledCyberware(data)) return;

        if (!data.hasSpecificItem(ModItems.BONEUPGRADES_CAPACITORFRAME.get(), CyberwareSlot.BONE)) {
            data.setEnergyStored(entity, 0);
            if (entity instanceof Mob mob) {
                freezeMob(mob);
            }
        }
    }

    private static boolean hasAnyInstalledCyberware(PlayerCyberwareData data) {
        if (data == null) return false;

        for (var entry : data.getAll().values()) {
            if (entry == null) continue;

            for (var installed : entry) {
                if (installed == null) continue;
                if (installed.getItem() == null || installed.getItem().isEmpty()) continue;
                return true;
            }
        }

        return false;
    }

    private static boolean hasAnyInstalledCyberware(EntityCyberwareData data) {
        if (data == null) return false;

        for (var entry : data.getAll().values()) {
            if (entry == null) continue;

            for (var installed : entry) {
                if (installed == null) continue;
                if (installed.getItem() == null || installed.getItem().isEmpty()) continue;
                return true;
            }
        }

        return false;
    }

    private static void freezeMob(Mob mob) {
        mob.getNavigation().stop();
        mob.setTarget(null);

        Vec3 v = mob.getDeltaMovement();
        if (v.x != 0.0D || v.z != 0.0D) {
            mob.setDeltaMovement(0.0D, v.y, 0.0D);
            mob.hurtMarked = true;
        }

        mob.getMoveControl().setWantedPosition(mob.getX(), mob.getY(), mob.getZ(), 0.0D);
        mob.setSprinting(false);
    }
}