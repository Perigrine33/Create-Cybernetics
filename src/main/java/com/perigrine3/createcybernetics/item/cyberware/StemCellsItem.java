package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.List;
import java.util.Set;

public class StemCellsItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public StemCellsItem(Properties props, int humanityCost) {
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
    public int getEnergyUsedPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return 10;
    }

    @Override
    public boolean requiresEnergyToFunction(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return true;
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

    private static final String NBT_REGEN_NEXT_TICK = "cc_regen_nextTick";
    private static final int REGEN_TICKS = 20 * 30;
    private static final int REGEN_COOLDOWN_TICKS = 20 * 180;

    @Override
    public void onTick(Player player) {
        if (player.level().isClientSide) return;
        if (!player.isAlive()) return;
        if (player.isCreative() || player.isSpectator()) return;
        if (player.getHealth() > 5.0F) return;

        long now = player.level().getGameTime();
        var tag = player.getPersistentData();
        long next = tag.getLong(NBT_REGEN_NEXT_TICK);

        if (next == 0L) next = now;
        if (now < next) return;

        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, REGEN_TICKS, 2, false, true, true));

        tag.putLong(NBT_REGEN_NEXT_TICK, now + REGEN_COOLDOWN_TICKS);
    }
}
