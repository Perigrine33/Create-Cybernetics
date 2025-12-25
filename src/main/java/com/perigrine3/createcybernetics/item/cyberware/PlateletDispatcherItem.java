package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;
import java.util.Set;

public class PlateletDispatcherItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public PlateletDispatcherItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost).withStyle(ChatFormatting.GOLD));
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
    public void onInstalled(Player player) {
    }

    @Override
    public void onRemoved(Player player) {
    }

    private static final String NBT_LAST_COMBAT_TICK = "cc_platelet_lastCombatTick";
    private static final int OUT_OF_COMBAT_TICKS = 20 * 120;
    private static final int REGEN_TICKS = 20 * 30;

    @Override
    public void onTick(Player player) {
        if (player.level().isClientSide) return;
        if (!player.isAlive()) return;
        if (player.isCreative() || player.isSpectator()) return;

        long now = player.level().getGameTime();
        long lastCombat = player.getPersistentData().getLong(NBT_LAST_COMBAT_TICK);

        boolean inCombatWindow = lastCombat != 0L && (now - lastCombat) < OUT_OF_COMBAT_TICKS;

        if (inCombatWindow) {
            if (player.hasEffect(MobEffects.REGENERATION)) {
                player.removeEffect(MobEffects.REGENERATION);
            }
            return;
        }

        if (player.getHealth() >= player.getMaxHealth()) return;

        MobEffectInstance existing = player.getEffect(MobEffects.REGENERATION);
        if (existing == null || existing.getDuration() < 40) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, REGEN_TICKS, 0, false, true, true));
        }
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class Events {

        @SubscribeEvent
        public static void onLivingDamagePost(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post event) {
            if (!(event.getEntity() instanceof Player victim)) return;
            if (victim.level().isClientSide) return;

            long now = victim.level().getGameTime();
            victim.getPersistentData().putLong(NBT_LAST_COMBAT_TICK, now);

            if (victim.hasEffect(MobEffects.REGENERATION)) {
                victim.removeEffect(MobEffects.REGENERATION);
            }

            Entity src = event.getSource().getEntity();
            if (src instanceof Player attacker && !attacker.level().isClientSide) {
                attacker.getPersistentData().putLong(NBT_LAST_COMBAT_TICK, attacker.level().getGameTime());

                if (attacker.hasEffect(MobEffects.REGENERATION)) {
                    attacker.removeEffect(MobEffects.REGENERATION);
                }
            }
        }

        private Events() {}
    }
}
