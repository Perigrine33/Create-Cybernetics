package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;
import java.util.Set;

public class EyeOfDefenderItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final int ENERGY_PER_TICK = 5;

    // keep slightly > 20 so missing 1 tick doesn’t drop the effect
    private static final int EFFECT_DURATION_TICKS = 40;

    public EyeOfDefenderItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.translatable("tooltip.createcybernetics.brainupgrades_eyeofdefender.energy")
                    .withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModTags.Items.BRAIN_ITEMS);
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.BRAIN);
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
    public int getEnergyUsedPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return ENERGY_PER_TICK;
    }

    @Override
    public void onInstalled(Player player) { }

    @Override
    public void onRemoved(Player player) {
        if (!player.level().isClientSide) {
            player.removeEffect(ModEffects.PROJECTILE_DODGE_EFFECT);
        }
    }

    /**
     * New truth-source: rely on the installed entry's powered state (EnergyController sets this).
     * Also respects wheel toggle via data.isEnabled(slot, idx).
     */
    private static boolean isActive(Player player) {
        if (!(player instanceof Player p)) return false;
        if (p.level().isClientSide) return false;
        if (!p.isAlive()) return false;
        if (p.isCreative() || p.isSpectator()) return false;

        if (!p.hasData(ModAttachments.CYBERWARE)) return false;
        PlayerCyberwareData data = p.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        InstalledCyberware[] arr = data.getAll().get(CyberwareSlot.BRAIN);
        if (arr == null) return false;

        for (int idx = 0; idx < arr.length; idx++) {
            InstalledCyberware cw = arr[idx];
            if (cw == null) continue;

            ItemStack st = cw.getItem();
            if (st == null || st.isEmpty()) continue;

            if (!(st.getItem() instanceof EyeOfDefenderItem)) continue;

            // respect wheel toggle
            if (!data.isEnabled(CyberwareSlot.BRAIN, idx)) return false;

            return cw.isPowered();
        }

        return false;
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class Events {
        private Events() {}

        @SubscribeEvent
        public static void onPlayerTick(PlayerTickEvent.Post event) {
            Player player = event.getEntity();
            if (player.level().isClientSide) return;

            if (isActive(player)) {
                // refresh effect while active
                player.addEffect(new MobEffectInstance(
                        ModEffects.PROJECTILE_DODGE_EFFECT,
                        EFFECT_DURATION_TICKS,
                        0,
                        false,
                        false,
                        false
                ));
            } else {
                // hard remove if not active
                if (player.hasEffect(ModEffects.PROJECTILE_DODGE_EFFECT)) {
                    player.removeEffect(ModEffects.PROJECTILE_DODGE_EFFECT);
                }
            }
        }
    }

    /**
     * Keep this empty; your current architecture may or may not call it.
     * The reliable behavior is handled by the PlayerTickEvent above.
     */
    @Override
    public void onTick(Player player) { }
}
