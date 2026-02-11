package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;
import java.util.Set;

public class PneumaticCalvesItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final int ENERGY_SPRINT_JUMP = 3;
    private static final int ENERGY_CROUCH_JUMP = 5;

    private static final String NBT_LAST_JUMP_CHARGE_TICK = "cc_calves_last_jump_charge_tick";

    public PneumaticCalvesItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost).withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("tooltip.createcybernetics.legupgrades_jumpboost.energy").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return switch (slot) {
            case RLEG -> Set.of(ModTags.Items.RIGHTLEG_REPLACEMENTS);
            case LLEG -> Set.of(ModTags.Items.LEFTLEG_REPLACEMENTS);
            default -> Set.of();
        };
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.RLEG, CyberwareSlot.LLEG);
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
        // no-op
    }

    @Override
    public void onRemoved(Player player) {
        if (player.level().isClientSide) return;
        cleanup(player);
    }

    @Override
    public void onTick(Player player) {
        if (player.level().isClientSide) return;

        if (!player.hasData(ModAttachments.CYBERWARE)) return;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        // Enforce the "pair" rule continuously:
        // - if exactly one installed, force-disable it and cleanup.
        // - if none or both, proceed.
        PairState pair = enforcePairRuleAndGetState(player, data);

        // Only apply functionality when BOTH installed AND enabled.
        if (!pair.bothInstalled || !pair.bothEnabled) {
            cleanup(player);
            return;
        }

        // Active behavior (unchanged, but now gated)
        player.addEffect(new MobEffectInstance(ModEffects.PNEUMATIC_CALVES_EFFECT, 100, 0, false, false, false));

        if (player.isSprinting()) {
            CyberwareAttributeHelper.applyModifier(player, "calves_sprint");
        } else {
            CyberwareAttributeHelper.removeModifier(player, "calves_sprint");
        }
    }

    private static void cleanup(Player player) {
        CyberwareAttributeHelper.removeModifier(player, "calves_sprint");
        player.removeEffect(ModEffects.PNEUMATIC_CALVES_EFFECT);
        player.getPersistentData().remove(NBT_LAST_JUMP_CHARGE_TICK);
    }

    private record Found(CyberwareSlot slot, int index) {}
    private record PairState(boolean bothInstalled, boolean bothEnabled) {}

    /**
     * Scans RLEG+LLEG for PneumaticCalvesItem, then:
     * - if exactly one is installed: force it disabled (server-side) and cleanup.
     * - returns pair state for gating.
     */
    private static PairState enforcePairRuleAndGetState(Player player, PlayerCyberwareData data) {
        Found first = null;
        Found second = null;

        // Find up to two installed calves (across both legs).
        for (CyberwareSlot slot : new CyberwareSlot[]{CyberwareSlot.RLEG, CyberwareSlot.LLEG}) {
            for (int i = 0; i < slot.size; i++) {
                InstalledCyberware cw = data.get(slot, i);
                if (cw == null) continue;

                ItemStack st = cw.getItem();
                if (st == null || st.isEmpty()) continue;

                if (!(st.getItem() instanceof PneumaticCalvesItem)) continue;

                if (first == null) first = new Found(slot, i);
                else if (second == null) second = new Found(slot, i);
                else break; // more than two installed; ignore extras
            }
        }

        // None installed
        if (first == null) {
            return new PairState(false, false);
        }

        // Exactly one installed -> FORCE DISABLED
        if (second == null) {
            forceDisabled(player, data, first.slot(), first.index());
            cleanup(player);
            return new PairState(false, false);
        }

        // Both installed: enabled if BOTH are enabled (since your wheel toggles them together).
        boolean e1 = data.isEnabled(first.slot(), first.index());
        boolean e2 = data.isEnabled(second.slot(), second.index());
        return new PairState(true, e1 && e2);
    }

    /**
     * Force a particular installed instance disabled.
     * Adjust the setter call if your API uses a different method name.
     */
    private static void forceDisabled(Player player, PlayerCyberwareData data, CyberwareSlot slot, int index) {
        if (!data.isEnabled(slot, index)) return;

        // ---- CHANGE THIS LINE IF YOUR METHOD NAME DIFFERS ----
        data.setEnabled(slot, index, false);
        // -----------------------------------------------------

        data.setDirty();
        player.syncData(ModAttachments.CYBERWARE);
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class Events {
        private Events() {}

        /**
         * Extra safety: enforce rule on tick too (covers cases where onTick isn't called for disabled items,
         * or if your cyberware ticking order changes).
         */
        @SubscribeEvent
        public static void onPlayerTick(PlayerTickEvent.Post event) {
            Player player = event.getEntity();
            if (player.level().isClientSide) return;
            if (!player.isAlive()) return;
            if (player.isCreative() || player.isSpectator()) return;

            if (!player.hasData(ModAttachments.CYBERWARE)) return;
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return;

            // If exactly one installed, this will force-disable and cleanup.
            enforcePairRuleAndGetState(player, data);
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
            if (!(event.getEntity() instanceof Player player)) return;
            if (player.level().isClientSide) return;
            if (!player.isAlive()) return;
            if (player.isCreative() || player.isSpectator()) return;

            if (!player.hasData(ModAttachments.CYBERWARE)) return;
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return;

            // Enforce pair rule; only allow jump energy drain when BOTH installed AND enabled.
            PairState pair = enforcePairRuleAndGetState(player, data);
            if (!pair.bothInstalled || !pair.bothEnabled) return;

            final boolean crouchJump = player.isCrouching();
            final boolean sprintJump = !crouchJump && player.isSprinting();

            final int cost = crouchJump ? ENERGY_CROUCH_JUMP : (sprintJump ? ENERGY_SPRINT_JUMP : 0);
            if (cost <= 0) return;

            long now = player.level().getGameTime();
            CompoundTag ptag = player.getPersistentData();
            if (ptag.getLong(NBT_LAST_JUMP_CHARGE_TICK) == now) return;
            ptag.putLong(NBT_LAST_JUMP_CHARGE_TICK, now);

            if (!data.tryConsumeEnergy(cost)) {
                cleanup(player);
                return;
            }

            data.setDirty();
            player.syncData(ModAttachments.CYBERWARE);
        }
    }
}
