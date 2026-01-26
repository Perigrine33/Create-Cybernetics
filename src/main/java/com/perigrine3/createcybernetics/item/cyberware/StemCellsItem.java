package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class StemCellsItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final String NBT_REGEN_NEXT_TICK = "cc_regen_nextTick";
    private static final String NBT_REGEN_PAID_TICK = "cc_regen_paidTick";

    private static final int REGEN_TICKS = 20 * 30;
    private static final int REGEN_COOLDOWN_TICKS = 20 * 180;

    private static final int ENERGY_ON_ACTIVATION = 5;

    public StemCellsItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.translatable("tooltip.createcybernetics.heartupgrades_stemcell.energy").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public boolean requiresEnergyToFunction(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return true;
    }

    @Override
    public String getActivationPaidNbtKey(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return NBT_REGEN_PAID_TICK;
    }

    @Override
    public int getEnergyActivationCost(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return ENERGY_ON_ACTIVATION;
    }

    @Override
    public boolean shouldConsumeActivationEnergyThisTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        if (player.level().isClientSide) return false;
        if (!player.isAlive()) return false;
        if (player.isCreative() || player.isSpectator()) return false;
        if (player.getHealth() > 5.0F) return false;

        long now = player.level().getGameTime();
        CompoundTag tag = player.getPersistentData();

        long next = tag.getLong(NBT_REGEN_NEXT_TICK);
        if (next != 0L && now < next) return false;

        return tag.getLong(NBT_REGEN_PAID_TICK) != now;
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
    public void onTick(Player player) {
        if (player.level().isClientSide) return;
        if (!player.isAlive()) return;
        if (player.isCreative() || player.isSpectator()) return;

        if (player.getHealth() > 5.0F) return;

        long now = player.level().getGameTime();
        CompoundTag tag = player.getPersistentData();

        long next = tag.getLong(NBT_REGEN_NEXT_TICK);
        if (next != 0L && now < next) return;

        if (tag.getLong(NBT_REGEN_PAID_TICK) != now) return;

        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, REGEN_TICKS, 2, false, true, true));
        tag.putLong(NBT_REGEN_NEXT_TICK, now + REGEN_COOLDOWN_TICKS);

        tag.remove(NBT_REGEN_PAID_TICK);
    }
}
