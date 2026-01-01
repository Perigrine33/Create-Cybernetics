package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

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

            tooltip.add(Component.literal("Costs 3-5 Energy").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<Item> requiresCyberware(ItemStack installedStack, CyberwareSlot slot) {
        return switch (slot) {
            case RLEG -> Set.of(ModItems.BASECYBERWARE_RIGHTLEG.get());
            case LLEG -> Set.of(ModItems.BASECYBERWARE_LEFTLEG.get());
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
    }

    @Override
    public void onRemoved(Player player) {
        if (player.level().isClientSide) return;
        CyberwareAttributeHelper.removeModifier(player, "calves_sprint");
        player.removeEffect(ModEffects.PNEUMATIC_CALVES_EFFECT);
        player.getPersistentData().remove(NBT_LAST_JUMP_CHARGE_TICK);
    }

    @Override
    public void onTick(Player player) {
        if (player.level().isClientSide) return;

        player.addEffect(new MobEffectInstance(ModEffects.PNEUMATIC_CALVES_EFFECT, 100, 0, false, false, false));

        if (player.isSprinting()) {
            CyberwareAttributeHelper.applyModifier(player, "calves_sprint");
        } else {
            CyberwareAttributeHelper.removeModifier(player, "calves_sprint");
        }
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class Events {

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
            if (!(event.getEntity() instanceof Player player)) return;
            if (player.level().isClientSide) return;
            if (!player.isAlive()) return;
            if (player.isCreative() || player.isSpectator()) return;

            if (!player.hasData(ModAttachments.CYBERWARE)) return;
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return;

            if (!hasPneumaticCalvesInstalled(data)) return;

            final boolean crouchJump = player.isCrouching();
            final boolean sprintJump = !crouchJump && player.isSprinting();

            final int cost = crouchJump ? ENERGY_CROUCH_JUMP : (sprintJump ? ENERGY_SPRINT_JUMP : 0);
            if (cost <= 0) return;

            long now = player.level().getGameTime();
            CompoundTag ptag = player.getPersistentData();
            if (ptag.getLong(NBT_LAST_JUMP_CHARGE_TICK) == now) return;
            ptag.putLong(NBT_LAST_JUMP_CHARGE_TICK, now);

            if (!data.tryConsumeEnergy(cost)) {
                player.removeEffect(ModEffects.PNEUMATIC_CALVES_EFFECT);
                return;
            }

            data.setDirty();
            player.syncData(ModAttachments.CYBERWARE);
        }

        private static boolean hasPneumaticCalvesInstalled(PlayerCyberwareData data) {
            for (CyberwareSlot slot : new CyberwareSlot[]{CyberwareSlot.RLEG, CyberwareSlot.LLEG}) {
                for (int i = 0; i < slot.size; i++) {
                    InstalledCyberware cw = data.get(slot, i);
                    if (cw == null) continue;

                    ItemStack st = cw.getItem();
                    if (st == null || st.isEmpty()) continue;

                    if (st.getItem() instanceof PneumaticCalvesItem) {
                        return true;
                    }
                }
            }
            return false;
        }

        private Events() {}
    }
}
