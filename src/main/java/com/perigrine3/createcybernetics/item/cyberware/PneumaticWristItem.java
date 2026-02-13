package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;
import java.util.Set;

public class PneumaticWristItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final int ENERGY_PER_TICK_ACTIVE = 3;
    private static final int ACTIVE_WINDOW_TICKS = 10;

    private static final String NBT_ACTIVE_UNTIL = "cc_pwrist_active_until";
    private static final String NBT_LAST_APPLY_TICK = "cc_pwrist_last_apply_tick";

    public PneumaticWristItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost).withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.translatable("tooltip.createcybernetics.armupgrades_pneumaticwrist.energy").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return switch (slot) {
            case RARM -> Set.of(ModTags.Items.RIGHTARM_REPLACEMENTS);
            case LARM -> Set.of(ModTags.Items.LEFTARM_REPLACEMENTS);
            default -> Set.of();
        };
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.RARM, CyberwareSlot.LARM);
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
    public boolean requiresEnergyToFunction(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return true;
    }

    @Override
    public int getEnergyUsedPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        if (player == null) return 0;
        if (player.level().isClientSide) return 0;

        long now = player.level().getGameTime();
        long until = player.getPersistentData().getLong(NBT_ACTIVE_UNTIL);
        if (until <= now) return 0;

        if (!shouldChargeOnThisSlot(player, slot)) return 0;
        return ENERGY_PER_TICK_ACTIVE;
    }

    @Override
    public void onInstalled(Player player) {
        CyberwareAttributeHelper.applyModifier(player, "pneumatic_wrist_block");
        CyberwareAttributeHelper.applyModifier(player, "pneumatic_wrist_entity");
        CyberwareAttributeHelper.applyModifier(player, "pneumatic_wrist_knockback");
    }

    @Override
    public void onRemoved(Player player) {
        CyberwareAttributeHelper.removeModifier(player, "pneumatic_wrist_block");
        CyberwareAttributeHelper.removeModifier(player, "pneumatic_wrist_entity");
        CyberwareAttributeHelper.removeModifier(player, "pneumatic_wrist_knockback");

        if (!player.level().isClientSide) {
            player.getPersistentData().remove(NBT_ACTIVE_UNTIL);
            player.getPersistentData().remove(NBT_LAST_APPLY_TICK);
        }
    }

    @Override
    public void onTick(Player player) { }

    @Override
    public void onTick(Player player, ItemStack installedStack, CyberwareSlot slot, int index) {
        if (player.level().isClientSide) return;
        if (!player.isAlive()) return;

        long now = player.level().getGameTime();
        CompoundTag ptag = player.getPersistentData();
        if (ptag.getLong(NBT_LAST_APPLY_TICK) == now) return;
        ptag.putLong(NBT_LAST_APPLY_TICK, now);

        if (!player.hasData(ModAttachments.CYBERWARE)) return;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        boolean active = ptag.getLong(NBT_ACTIVE_UNTIL) > now;

        boolean powered = false;
        for (CyberwareSlot s : new CyberwareSlot[]{CyberwareSlot.RARM, CyberwareSlot.LARM}) {
            for (int i = 0; i < s.size; i++) {
                InstalledCyberware cw = data.get(s, i);
                if (cw == null) continue;

                ItemStack st = cw.getItem();
                if (st == null || st.isEmpty()) continue;
                if (st.getItem() != this) continue;

                if (cw.isPowered()) powered = true;
            }
        }

        if (active && powered) {
            CyberwareAttributeHelper.applyModifier(player, "pneumatic_wrist_block");
            CyberwareAttributeHelper.applyModifier(player, "pneumatic_wrist_entity");
            CyberwareAttributeHelper.applyModifier(player, "pneumatic_wrist_knockback");
        } else {
            CyberwareAttributeHelper.removeModifier(player, "pneumatic_wrist_block");
            CyberwareAttributeHelper.removeModifier(player, "pneumatic_wrist_entity");
            CyberwareAttributeHelper.removeModifier(player, "pneumatic_wrist_knockback");
        }
    }

    private boolean shouldChargeOnThisSlot(Player player, CyberwareSlot slot) {
        if (!player.hasData(ModAttachments.CYBERWARE)) return false;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        boolean hasRight = false;
        for (int i = 0; i < CyberwareSlot.RARM.size; i++) {
            InstalledCyberware cw = data.get(CyberwareSlot.RARM, i);
            if (cw == null) continue;
            ItemStack st = cw.getItem();
            if (st == null || st.isEmpty()) continue;
            if (st.getItem() == this) {
                hasRight = true;
                break;
            }
        }

        if (hasRight) return slot == CyberwareSlot.RARM;

        return slot == CyberwareSlot.LARM;
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class Events {

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onAttackEntity(AttackEntityEvent event) {
            if (!(event.getEntity() instanceof Player player)) return;
            if (player.level().isClientSide) return;
            markActive(player);
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
            if (!(event.getEntity() instanceof Player player)) return;
            if (player.level().isClientSide) return;
            markActive(player);
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
            if (!(event.getEntity() instanceof Player player)) return;
            if (player.level().isClientSide) return;
            markActive(player);
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
            if (!(event.getEntity() instanceof Player player)) return;
            if (player.level().isClientSide) return;
            markActive(player);
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onBlockBreak(BlockEvent.BreakEvent event) {
            if (!(event.getPlayer() instanceof Player player)) return;
            if (player.level().isClientSide) return;
            markActive(player);
        }

        private static void markActive(Player player) {
            if (!player.hasData(ModAttachments.CYBERWARE)) return;
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return;

            boolean installed = data.hasSpecificItem(ModItems.ARMUPGRADES_PNEUMATICWRIST.get(), CyberwareSlot.RARM, CyberwareSlot.LARM);
            if (!installed) return;

            long now = player.level().getGameTime();
            player.getPersistentData().putLong(NBT_ACTIVE_UNTIL, now + ACTIVE_WINDOW_TICKS);
        }

        private Events() {}
    }
}
