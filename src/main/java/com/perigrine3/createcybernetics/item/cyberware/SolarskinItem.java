package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Set;

public class SolarskinItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final int SOLAR_ENERGY_PER_TICK = 15;
    private static final int MIN_SKY_LIGHT_FOR_SUN = 14;

    public SolarskinItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.translatable("tooltip.createcybernetics.skinupgrades_solarskin.energy").withStyle(ChatFormatting.DARK_GREEN));

        }
    }

    @Override
    public boolean shouldGenerateEnergyThisTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return isInDirectSunlight(player);
    }

    @Override
    public int getEnergyGeneratedPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return isInDirectSunlight(player) ? SOLAR_ENERGY_PER_TICK : 0;
    }

    private static boolean isInDirectSunlight(Player player) {
        Level level = player.level();

        if (!level.dimensionType().hasSkyLight()) return false;
        if (!level.isDay()) return false;

        BlockPos pos = player.blockPosition();
        if (!level.canSeeSky(pos)) return false;
        if (level.isRainingAt(pos)) return false;
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        return skyLight >= MIN_SKY_LIGHT_FOR_SUN;
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModTags.Items.SKIN_ITEMS);
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.SKIN);
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
    public void onInstalled(Player player) {}

    @Override
    public void onRemoved(Player player) {}

    @Override
    public void onTick(Player player) {}
}
