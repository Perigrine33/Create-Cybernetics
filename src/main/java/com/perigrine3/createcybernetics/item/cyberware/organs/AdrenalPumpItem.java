package com.perigrine3.createcybernetics.item.cyberware.organs;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.EntityCyberwareData;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.ModMobAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;
import java.util.Set;

public class AdrenalPumpItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final String NBT_INSTALLED = "cc_adrenal_installed";
    private static final String NBT_ACTIVE_UNTIL = "cc_adrenal_active_until";
    private static final String NBT_NEXT_TRIGGER = "cc_adrenal_next_trigger";
    private static final String NBT_WAS_ACTIVE = "cc_adrenal_was_active";

    private static final int BUFF_TICKS = 4 * 60 * 20;
    private static final int COOLDOWN_TICKS = 5 * 60 * 20;
    private static final int WEAKNESS_TICKS = 2 * 60 * 20;
    private static final int SPEED_AMP = 0;
    private static final int STRENGTH_AMP = 0;

    private static final int ENERGY_ACTIVATION_COST = 10;

    public AdrenalPumpItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost).withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("tooltip.createcybernetics.organsupgrades_adrenaline.energy").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public int getEnergyActivationCost(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return ENERGY_ACTIVATION_COST;
    }

    @Override
    public boolean requiresEnergyToFunction(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return true;
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.ORGANS);
    }

    @Override
    public boolean replacesOrgan() {
        return false;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of();
    }

    @Override
    public void onInstalled(LivingEntity entity) {
        entity.getPersistentData().putBoolean(NBT_INSTALLED, true);
    }

    @Override
    public void onRemoved(LivingEntity entity) {
        CompoundTag tag = entity.getPersistentData();
        tag.remove(NBT_INSTALLED);
        tag.remove(NBT_ACTIVE_UNTIL);
        tag.remove(NBT_NEXT_TRIGGER);
        tag.remove(NBT_WAS_ACTIVE);
    }

    @Override
    public void onTick(LivingEntity entity) {
        if (!(entity.level() instanceof ServerLevel)) return;
        if (!entity.isAlive()) return;

        if (entity instanceof Player player) {
            if (player.isCreative() || player.isSpectator()) return;
        }

        CompoundTag tag = entity.getPersistentData();
        if (!tag.getBoolean(NBT_INSTALLED)) return;

        long now = entity.level().getGameTime();
        long activeUntil = tag.getLong(NBT_ACTIVE_UNTIL);

        boolean active = activeUntil > 0L && now < activeUntil;
        boolean wasActive = tag.getBoolean(NBT_WAS_ACTIVE);

        if (active) {
            tag.putBoolean(NBT_WAS_ACTIVE, true);

            if ((now % 20L) == 0L) {
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, SPEED_AMP, false, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, STRENGTH_AMP, false, false, false));
            }

            return;
        }

        if (wasActive) {
            tag.putBoolean(NBT_WAS_ACTIVE, false);
            tag.remove(NBT_ACTIVE_UNTIL);

            entity.removeEffect(MobEffects.MOVEMENT_SPEED);
            entity.removeEffect(MobEffects.DAMAGE_BOOST);

            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, WEAKNESS_TICKS, 0, false, false, true));
        }
    }

    private static boolean tryConsumeActivationEnergy(LivingEntity entity, int amount) {
        if (entity instanceof Player player) {
            if (!player.hasData(ModAttachments.CYBERWARE)) return false;

            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return false;

            return data.tryConsumeEnergy(amount);
        }

        if (!entity.hasData(ModMobAttachments.CYBERENTITY_CYBERWARE)) return false;

        EntityCyberwareData data = entity.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
        if (data == null) return false;

        return data.tryConsumeEnergy(amount);
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class Events {

        @SubscribeEvent
        public static void onLivingDamagePost(LivingDamageEvent.Post event) {
            LivingEntity entity = event.getEntity();
            if (!(entity.level() instanceof ServerLevel)) return;
            if (!entity.isAlive()) return;

            if (entity instanceof Player player) {
                if (player.isCreative() || player.isSpectator()) return;
            }

            CompoundTag tag = entity.getPersistentData();
            if (!tag.getBoolean(NBT_INSTALLED)) return;

            DamageSource source = event.getSource();
            Entity attacker = source.getEntity();
            if (attacker == null) return;

            if (event.getNewDamage() <= 0.0F) return;

            long now = entity.level().getGameTime();
            long next = tag.getLong(NBT_NEXT_TRIGGER);
            if (next != 0L && now < next) return;

            if (!tryConsumeActivationEnergy(entity, ENERGY_ACTIVATION_COST)) return;

            tag.putLong(NBT_ACTIVE_UNTIL, now + BUFF_TICKS);
            tag.putLong(NBT_NEXT_TRIGGER, now + COOLDOWN_TICKS);
            tag.putBoolean(NBT_WAS_ACTIVE, true);

            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, SPEED_AMP, false, true, false));
            entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, STRENGTH_AMP, false, true, false));
            entity.removeEffect(MobEffects.WEAKNESS);
        }

        private Events() {}
    }
}