package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
    public void onInstalled(Player player) {

    }

    @Override
    public void onRemoved(Player player) {
        if (player.level().isClientSide) return;

        CyberwareAttributeHelper.removeModifier(player, "irons_addmana_manabattery1");
        CyberwareAttributeHelper.removeModifier(player, "irons_addmana_manabattery2");
        CyberwareAttributeHelper.removeModifier(player, "irons_addmana_manabattery3");
        player.getPersistentData().remove(NBT_LAST_APPLIED_TICK);
    }

    @Override
    public void onTick(Player player, ItemStack installedStack, CyberwareSlot slot, int index) {
        if (player.level().isClientSide) return;

        long now = player.level().getGameTime();
        CompoundTag ptag = player.getPersistentData();
        if (ptag.getLong(NBT_LAST_APPLIED_TICK) == now) return;
        ptag.putLong(NBT_LAST_APPLIED_TICK, now);

        CyberwareAttributeHelper.removeModifier(player, "irons_addmana_manabattery1");
        CyberwareAttributeHelper.removeModifier(player, "irons_addmana_manabattery2");
        CyberwareAttributeHelper.removeModifier(player, "irons_addmana_manabattery3");

        if (!player.hasData(ModAttachments.CYBERWARE)) return;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        int stacks = 0;
        for (int i = 0; i < CyberwareSlot.ORGANS.size; i++) {
            if (data.isInstalled(ModItems.ORGANSUPGRADES_MANABATTERY.get(), CyberwareSlot.ORGANS, i)) {
                stacks++;
            }
        }

        if (stacks <= 0) return;
        if (stacks > 3) stacks = 3;
        if (stacks >= 1) CyberwareAttributeHelper.applyModifier(player, "irons_addmana_manabattery1");
        if (stacks >= 2) CyberwareAttributeHelper.applyModifier(player, "irons_addmana_manabattery2");
        if (stacks >= 3) CyberwareAttributeHelper.applyModifier(player, "irons_addmana_manabattery3");
    }

    @Override
    public void onTick(Player player) {

    }
}
