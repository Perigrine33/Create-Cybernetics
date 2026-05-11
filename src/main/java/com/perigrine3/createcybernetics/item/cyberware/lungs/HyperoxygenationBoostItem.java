package com.perigrine3.createcybernetics.item.cyberware.lungs;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.EntityCyberwareData;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.ModMobAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class HyperoxygenationBoostItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final int ENERGY_PER_TICK_PER_STACK = 3;
    private static final String NBT_LAST_APPLIED_TICK = "cc_hyperox_last_tick";

    public HyperoxygenationBoostItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.translatable("tooltip.createcybernetics.lungsupgrades_hyperoxygenation.energy")
                    .withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public int getEnergyUsedPerTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        if (!(entity instanceof Player player)) {
            return 0;
        }

        if (!player.isSprinting()) {
            return 0;
        }

        return ENERGY_PER_TICK_PER_STACK;
    }

    @Override
    public boolean requiresEnergyToFunction(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return true;
    }

    @Override
    public Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModTags.Items.LUNGS_ITEMS);
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.LUNGS);
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
    public int maxStacksPerSlotType(ItemStack stack, CyberwareSlot slotType) {
        return 3;
    }

    @Override
    public void onInstalled(LivingEntity entity) {
    }

    @Override
    public void onRemoved(LivingEntity entity) {
        if (entity.level().isClientSide) {
            return;
        }

        clearModifiers(entity);
        entity.getPersistentData().remove(NBT_LAST_APPLIED_TICK);
    }

    @Override
    public void onTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot, int index) {
        if (entity.level().isClientSide) {
            return;
        }

        long now = entity.level().getGameTime();
        CompoundTag tag = entity.getPersistentData();
        if (tag.getLong(NBT_LAST_APPLIED_TICK) == now) {
            return;
        }

        tag.putLong(NBT_LAST_APPLIED_TICK, now);

        clearModifiers(entity);

        if (entity instanceof Player player) {
            tickPlayer(player);
        } else {
            tickEntity(entity);
        }
    }

    @Override
    public void onTick(LivingEntity entity) {
    }

    private void tickPlayer(Player player) {
        if (!player.isSprinting()) {
            return;
        }

        if (!player.hasData(ModAttachments.CYBERWARE)) {
            return;
        }

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) {
            return;
        }

        int poweredStacks = countPoweredPlayerStacks(data);
        if (poweredStacks <= 0) {
            return;
        }

        applyStacks(player, poweredStacks);
    }

    private void tickEntity(LivingEntity entity) {
        if (!entity.hasData(ModMobAttachments.CYBERENTITY_CYBERWARE)) {
            return;
        }

        EntityCyberwareData data = entity.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
        if (data == null) {
            return;
        }

        int stacks = countEntityStacks(data);
        if (stacks <= 0) {
            return;
        }

        applyStacks(entity, stacks);
    }

    private static int countPoweredPlayerStacks(PlayerCyberwareData data) {
        InstalledCyberware[] lungs = data.getAll().get(CyberwareSlot.LUNGS);
        if (lungs == null) {
            return 0;
        }

        int stacks = 0;

        for (int i = 0; i < lungs.length; i++) {
            InstalledCyberware installed = lungs[i];
            if (installed == null) {
                continue;
            }

            ItemStack stack = installed.getItem();
            if (stack == null || stack.isEmpty()) {
                continue;
            }

            if (!stack.is(ModItems.LUNGSUPGRADES_HYPEROXYGENATION.get())) {
                continue;
            }

            if (!data.isEnabled(CyberwareSlot.LUNGS, i)) {
                continue;
            }

            if (!installed.isPowered()) {
                continue;
            }

            stacks++;
        }

        return Math.min(stacks, 3);
    }

    private static int countEntityStacks(EntityCyberwareData data) {
        InstalledCyberware[] lungs = data.getAll().get(CyberwareSlot.LUNGS);
        if (lungs == null) {
            return 0;
        }

        int stacks = 0;

        for (int i = 0; i < lungs.length; i++) {
            InstalledCyberware installed = lungs[i];
            if (installed == null) {
                continue;
            }

            ItemStack stack = installed.getItem();
            if (stack == null || stack.isEmpty()) {
                continue;
            }

            if (!stack.is(ModItems.LUNGSUPGRADES_HYPEROXYGENATION.get())) {
                continue;
            }

            if (!data.isEnabled(CyberwareSlot.LUNGS, i)) {
                continue;
            }

            stacks++;
        }

        return Math.min(stacks, 3);
    }

    private static void applyStacks(LivingEntity entity, int stacks) {
        if (stacks >= 1) {
            CyberwareAttributeHelper.applyModifier(entity, "hyperoxygenation_speed_1");
        }

        if (stacks >= 2) {
            CyberwareAttributeHelper.applyModifier(entity, "hyperoxygenation_speed_2");
        }

        if (stacks >= 3) {
            CyberwareAttributeHelper.applyModifier(entity, "hyperoxygenation_speed_3");
        }
    }

    private static void clearModifiers(LivingEntity entity) {
        CyberwareAttributeHelper.removeModifier(entity, "hyperoxygenation_speed_1");
        CyberwareAttributeHelper.removeModifier(entity, "hyperoxygenation_speed_2");
        CyberwareAttributeHelper.removeModifier(entity, "hyperoxygenation_speed_3");
    }
}