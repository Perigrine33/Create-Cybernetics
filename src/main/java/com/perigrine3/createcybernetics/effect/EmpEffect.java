package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class EmpEffect extends MobEffect {

    private static final ResourceLocation CYBERZOMBIE_ID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberzombie");
    private static final ResourceLocation CYBERSKELETON_ID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberskeleton");
    private static final ResourceLocation SMASHER_ID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "smasher");

    public EmpEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF4AB3FF);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide) {
            if (entity instanceof Player player) {
                if (player.hasData(ModAttachments.CYBERWARE)) {
                    PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
                    if (data != null) {
                        data.setEnergyStored(player, 0);
                    }
                }
            } else if (entity instanceof Mob mob && isEmpImmobilizedMob(mob)) {
                freezeMob(mob);
            }
        }
        return true;
    }

    private static boolean isEmpImmobilizedMob(Mob mob) {
        ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
        if (key == null) return false;

        return key.equals(CYBERZOMBIE_ID) || key.equals(CYBERSKELETON_ID) || key.equals(SMASHER_ID);
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
