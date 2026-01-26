package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;      // ADDED
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData; // ADDED
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
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

    private static final int BUFF_TICKS = 4 * 60 * 20;          // 4 minutes
    private static final int COOLDOWN_TICKS = 5 * 60 * 20;      // 5 minutes
    private static final int WEAKNESS_TICKS = 2 * 60 * 20;      // 2 minutes
    private static final int SPEED_AMP = 0;                     // Speed I
    private static final int STRENGTH_AMP = 0;                  // Strength I

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
    public int getEnergyActivationCost(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return ENERGY_ACTIVATION_COST;
    }

    @Override
    public boolean requiresEnergyToFunction(Player player, ItemStack installedStack, CyberwareSlot slot) {
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
    public void onInstalled(Player player) {
        player.getPersistentData().putBoolean(NBT_INSTALLED, true);
    }

    @Override
    public void onRemoved(Player player) {
        CompoundTag tag = player.getPersistentData();
        tag.remove(NBT_INSTALLED);
        tag.remove(NBT_ACTIVE_UNTIL);
        tag.remove(NBT_NEXT_TRIGGER);
        tag.remove(NBT_WAS_ACTIVE);
    }

    @Override
    public void onTick(Player player) {
        if (player.level().isClientSide) return;
        if (!player.isAlive()) return;
        if (player.isCreative() || player.isSpectator()) return;

        CompoundTag tag = player.getPersistentData();
        if (!tag.getBoolean(NBT_INSTALLED)) return;

        long now = player.level().getGameTime();
        long activeUntil = tag.getLong(NBT_ACTIVE_UNTIL);

        boolean active = activeUntil > 0L && now < activeUntil;
        boolean wasActive = tag.getBoolean(NBT_WAS_ACTIVE);

        if (active) {
            tag.putBoolean(NBT_WAS_ACTIVE, true);

            if ((now % 20L) == 0L) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, SPEED_AMP, false, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, STRENGTH_AMP, false, false, false));
            }

            return;
        }

        if (wasActive) {
            tag.putBoolean(NBT_WAS_ACTIVE, false);
            tag.remove(NBT_ACTIVE_UNTIL);

            player.removeEffect(MobEffects.MOVEMENT_SPEED);
            player.removeEffect(MobEffects.DAMAGE_BOOST);

            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, WEAKNESS_TICKS, 0, false, false, true));
        }
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class Events {

        @SubscribeEvent
        public static void onLivingDamagePost(LivingDamageEvent.Post event) {
            if (!(event.getEntity() instanceof ServerPlayer player)) return;
            if (player.level().isClientSide) return;
            if (!player.isAlive()) return;
            if (player.isCreative() || player.isSpectator()) return;

            CompoundTag tag = player.getPersistentData();
            if (!tag.getBoolean(NBT_INSTALLED)) return;

            DamageSource source = event.getSource();
            Entity attacker = source.getEntity();
            if (attacker == null) return;

            if (event.getNewDamage() <= 0.0F) return;

            long now = player.level().getGameTime();
            long next = tag.getLong(NBT_NEXT_TRIGGER);
            if (next != 0L && now < next) return;

            if (!player.hasData(ModAttachments.CYBERWARE)) return;
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return;
            if (!data.tryConsumeEnergy(ENERGY_ACTIVATION_COST)) return;

            tag.putLong(NBT_ACTIVE_UNTIL, now + BUFF_TICKS);
            tag.putLong(NBT_NEXT_TRIGGER, now + COOLDOWN_TICKS);
            tag.putBoolean(NBT_WAS_ACTIVE, true);

            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, SPEED_AMP, false, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, STRENGTH_AMP, false, true, false));
            player.removeEffect(MobEffects.WEAKNESS);
        }

        private Events() {}
    }
}
