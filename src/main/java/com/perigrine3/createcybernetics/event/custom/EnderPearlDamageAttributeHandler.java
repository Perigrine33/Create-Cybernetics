package com.perigrine3.createcybernetics.event.custom;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.common.attributes.ModAttributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class EnderPearlDamageAttributeHandler {
    private EnderPearlDamageAttributeHandler() {}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide) return;

        DamageSource source = event.getSource();
        if (source == null) return;

        if (!isEnderPearlTeleportDamage(target, source)) return;

        var inst = target.getAttribute(ModAttributes.ENDER_PEARL_DAMAGE);
        if (inst == null) return;

        double mult = inst.getValue();
        if (!Double.isFinite(mult)) return;
        if (mult < 0.0D) mult = 0.0D;

        event.setNewDamage((float)(event.getNewDamage() * mult));
    }


    private static boolean isEnderPearlTeleportDamage(LivingEntity target, DamageSource source) {
        Entity direct = source.getDirectEntity();
        if (direct instanceof ThrownEnderpearl) return true;

        Entity causing = source.getEntity();
        if (causing instanceof ThrownEnderpearl) return true;

        if (target instanceof Player player) {
            if (player.getCooldowns().isOnCooldown(Items.ENDER_PEARL)) {
                if (source.is(DamageTypes.FALL)) return true;
            }
        }

        return false;
    }
}
