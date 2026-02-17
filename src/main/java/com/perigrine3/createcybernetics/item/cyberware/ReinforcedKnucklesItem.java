package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems; // adjust to your actual ModItems path
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.List;
import java.util.Set;

public class ReinforcedKnucklesItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public ReinforcedKnucklesItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));
        }
    }

    @Override public int getHumanityCost() { return humanityCost; }

    @Override
    public Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return switch (slot) {
            case RLEG -> Set.of(ModTags.Items.RIGHTARM_ITEMS);
            case LLEG -> Set.of(ModTags.Items.LEFTARM_ITEMS);
            default -> Set.of();
        };
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.RARM, CyberwareSlot.LARM);
    }

    @Override public boolean replacesOrgan() { return false; }
    @Override public Set<CyberwareSlot> getReplacedOrgans() { return Set.of(); }

    @Override
    public void onInstalled(Player player) {
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);

        if (data.hasSpecificItem(ModItems.ARMUPGRADES_REINFORCEDKNUCKLES.get(), CyberwareSlot.RARM) && data.hasSpecificItem(ModItems.ARMUPGRADES_REINFORCEDKNUCKLES.get(), CyberwareSlot.LARM)) {
            CyberwareAttributeHelper.applyModifier(player, "reinforced_knuckles_damage1");
            CyberwareAttributeHelper.applyModifier(player, "reinforced_knuckles_damage2");
        } else if (data.hasSpecificItem(ModItems.ARMUPGRADES_REINFORCEDKNUCKLES.get(), CyberwareSlot.RARM) || data.hasSpecificItem(ModItems.ARMUPGRADES_REINFORCEDKNUCKLES.get(), CyberwareSlot.LARM)) {
            CyberwareAttributeHelper.applyModifier(player, "reinforced_knuckles_damage1");
            CyberwareAttributeHelper.removeModifier(player, "reinforced_knuckles_damage2");
        }
    }

    @Override
    public void onRemoved(Player player) {
        CyberwareAttributeHelper.removeModifier(player, "reinforced_knuckles_damage1");
        CyberwareAttributeHelper.removeModifier(player, "reinforced_knuckles_damage2");

        onInstalled(player);
    }

    @Override
    public void onTick(Player player) {
    }

    private static int countInstalledKnuckles(Player player) {
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return 0;

        int count = 0;
        if (data.hasSpecificItem(ModItems.ARMUPGRADES_REINFORCEDKNUCKLES.get(), CyberwareSlot.LARM)) count++;
        if (data.hasSpecificItem(ModItems.ARMUPGRADES_REINFORCEDKNUCKLES.get(), CyberwareSlot.RARM)) count++;
        return count;
    }

    private static boolean hasKnuckles(Player player) {
        return countInstalledKnuckles(player) > 0;
    }

    private static boolean bareHanded(Player player) {
        return player.getMainHandItem().isEmpty();
    }

    private static boolean isKnucklesEligible(BlockState state) {
        if (!state.is(BlockTags.MINEABLE_WITH_PICKAXE)) return false;
        return !state.is(BlockTags.INCORRECT_FOR_STONE_TOOL);
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class MiningHooks {

        @SubscribeEvent
        public static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
            Player player = event.getEntity();
            if (!bareHanded(player)) return;
            if (!hasKnuckles(player)) return;

            BlockState state = event.getTargetBlock();
            if (!isKnucklesEligible(state)) return;

            event.setCanHarvest(true);
        }

        @SubscribeEvent
        public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
            Player player = event.getEntity();
            if (!bareHanded(player)) return;
            if (!hasKnuckles(player)) return;

            BlockState state = event.getState();
            if (!isKnucklesEligible(state)) return;

            float original = event.getOriginalSpeed();
            if (original <= 0) return;

            event.setNewSpeed(original * 6.0F);
        }
    }
}
