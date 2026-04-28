package com.perigrine3.createcybernetics.item.cyberware.heart;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.EntityCyberwareData;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.ModMobAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;
import java.util.Set;

public class PlateletDispatcherItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final String NBT_LAST_COMBAT_TICK = "cc_platelet_lastCombatTick";
    private static final String NBT_ACTIVE = "cc_platelet_active";

    private static final int OUT_OF_COMBAT_TICKS = 20 * 120;
    private static final int REGEN_TICKS = 20 * 30;

    private static final int ENERGY_PER_TICK_WHEN_ACTIVE = 5;

    public PlateletDispatcherItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost).withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("tooltip.createcybernetics.heartupgrades_platelets.energy").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModTags.Items.HEART_ITEMS);
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.HEART);
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
    public boolean requiresEnergyToFunction(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return true;
    }

    @Override
    public int getEnergyUsedPerTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        if (entity == null) return 0;
        if (entity.level().isClientSide) return 0;
        if (!entity.isAlive()) return 0;

        if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return 0;
        }

        long now = entity.level().getGameTime();
        long lastCombat = entity.getPersistentData().getLong(NBT_LAST_COMBAT_TICK);
        boolean inCombatWindow = lastCombat != 0L && (now - lastCombat) < OUT_OF_COMBAT_TICKS;
        if (inCombatWindow) return 0;

        boolean active = entity.getPersistentData().getBoolean(NBT_ACTIVE);
        boolean needsHeal = entity.getHealth() < entity.getMaxHealth();

        return (active || needsHeal) ? ENERGY_PER_TICK_WHEN_ACTIVE : 0;
    }

    @Override
    public void onInstalled(LivingEntity entity) {
        if (!entity.level().isClientSide) {
            entity.getPersistentData().remove(NBT_ACTIVE);
        }
    }

    @Override
    public void onRemoved(LivingEntity entity) {
        if (!entity.level().isClientSide) {
            entity.getPersistentData().remove(NBT_ACTIVE);
            entity.removeEffect(MobEffects.REGENERATION);
        }
    }

    @Override
    public void onTick(LivingEntity entity) {
    }

    @Override
    public void onTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot, int index) {
        if (entity.level().isClientSide) return;
        if (!entity.isAlive()) return;

        if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return;
        }

        InstalledCyberware cw;

        if (entity instanceof Player player) {
            if (!player.hasData(ModAttachments.CYBERWARE)) return;
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return;
            cw = data.get(slot, index);
        } else {
            if (!entity.hasData(ModMobAttachments.CYBERENTITY_CYBERWARE)) return;
            EntityCyberwareData data = entity.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
            if (data == null) return;
            cw = data.get(slot, index);
        }

        if (cw == null) return;

        long now = entity.level().getGameTime();
        long lastCombat = entity.getPersistentData().getLong(NBT_LAST_COMBAT_TICK);
        boolean inCombatWindow = lastCombat != 0L && (now - lastCombat) < OUT_OF_COMBAT_TICKS;

        if (inCombatWindow) {
            if (entity.hasEffect(MobEffects.REGENERATION)) {
                entity.removeEffect(MobEffects.REGENERATION);
            }
            entity.getPersistentData().putBoolean(NBT_ACTIVE, false);
            return;
        }

        boolean active = entity.getPersistentData().getBoolean(NBT_ACTIVE);
        MobEffectInstance existing = entity.getEffect(MobEffects.REGENERATION);

        if (active && existing == null) {
            entity.getPersistentData().putBoolean(NBT_ACTIVE, false);
            active = false;
        }

        if (!cw.isPowered()) {
            if (entity.hasEffect(MobEffects.REGENERATION)) {
                entity.removeEffect(MobEffects.REGENERATION);
            }
            entity.getPersistentData().putBoolean(NBT_ACTIVE, false);
            return;
        }

        if (entity.getHealth() >= entity.getMaxHealth()) {
            return;
        }

        if (existing == null || existing.getDuration() < 40) {
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, REGEN_TICKS, 0, false, true, true));
            entity.getPersistentData().putBoolean(NBT_ACTIVE, true);
        }
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class Events {

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onLivingDamagePost(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post event) {
            if (!(event.getEntity() instanceof LivingEntity victim)) return;
            if (victim.level().isClientSide) return;

            long now = victim.level().getGameTime();
            victim.getPersistentData().putLong(NBT_LAST_COMBAT_TICK, now);

            if (victim.hasEffect(MobEffects.REGENERATION)) {
                victim.removeEffect(MobEffects.REGENERATION);
            }
            victim.getPersistentData().putBoolean(NBT_ACTIVE, false);

            Entity src = event.getSource().getEntity();
            if (src instanceof LivingEntity attacker && !attacker.level().isClientSide) {
                attacker.getPersistentData().putLong(NBT_LAST_COMBAT_TICK, attacker.level().getGameTime());

                if (attacker.hasEffect(MobEffects.REGENERATION)) {
                    attacker.removeEffect(MobEffects.REGENERATION);
                }
                attacker.getPersistentData().putBoolean(NBT_ACTIVE, false);
            }
        }

        private Events() {}
    }
}