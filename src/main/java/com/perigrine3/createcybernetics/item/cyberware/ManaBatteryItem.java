package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.EntityCyberwareData;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.ModMobAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class ManaBatteryItem extends Item implements ICyberwareItem {

    private final int humanityCost;

    private static final String NBT_LAST_APPLIED_TICK = "cc_manabattery_last_tick";

    public ManaBatteryItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.translatable("tooltip.createcybernetics.organsupgrades_manabattery.energy")
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
        }
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
    public Set<Item> incompatibleCyberware(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModItems.ORGANSUPGRADES_DENSEBATTERY.get());
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
        if (entity.level().isClientSide) return;

        CyberwareAttributeHelper.removeModifier(entity, "irons_addmana_manabattery1");
        CyberwareAttributeHelper.removeModifier(entity, "irons_addmana_manabattery2");
        CyberwareAttributeHelper.removeModifier(entity, "irons_addmana_manabattery3");
        entity.getPersistentData().remove(NBT_LAST_APPLIED_TICK);
    }

    @Override
    public void onTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot, int index) {
        if (entity.level().isClientSide) return;

        long now = entity.level().getGameTime();
        CompoundTag ptag = entity.getPersistentData();
        if (ptag.getLong(NBT_LAST_APPLIED_TICK) == now) return;
        ptag.putLong(NBT_LAST_APPLIED_TICK, now);

        CyberwareAttributeHelper.removeModifier(entity, "irons_addmana_manabattery1");
        CyberwareAttributeHelper.removeModifier(entity, "irons_addmana_manabattery2");
        CyberwareAttributeHelper.removeModifier(entity, "irons_addmana_manabattery3");

        int stacks = 0;

        if (entity instanceof Player player) {
            if (!player.hasData(ModAttachments.CYBERWARE)) return;
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return;

            for (int i = 0; i < CyberwareSlot.ORGANS.size; i++) {
                if (data.isInstalled(ModItems.ORGANSUPGRADES_MANABATTERY.get(), CyberwareSlot.ORGANS, i)) {
                    stacks++;
                }
            }
        } else {
            if (!entity.hasData(ModMobAttachments.CYBERENTITY_CYBERWARE)) return;
            EntityCyberwareData data = entity.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
            if (data == null) return;

            for (int i = 0; i < CyberwareSlot.ORGANS.size; i++) {
                InstalledCyberware installed = data.get(CyberwareSlot.ORGANS, i);
                if (installed == null) continue;

                ItemStack st = installed.getItem();
                if (st == null || st.isEmpty()) continue;

                if (st.is(ModItems.ORGANSUPGRADES_MANABATTERY.get())) {
                    stacks++;
                }
            }
        }

        if (stacks <= 0) return;
        if (stacks > 3) stacks = 3;

        if (stacks >= 1) CyberwareAttributeHelper.applyModifier(entity, "irons_addmana_manabattery1");
        if (stacks >= 2) CyberwareAttributeHelper.applyModifier(entity, "irons_addmana_manabattery2");
        if (stacks >= 3) CyberwareAttributeHelper.applyModifier(entity, "irons_addmana_manabattery3");
    }

    @Override
    public void onTick(LivingEntity entity) {
    }
}