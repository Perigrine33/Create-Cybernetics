package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.item.generic.XPCapsuleItem;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

import java.util.List;
import java.util.Set;

public class ChipwareSlotsItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public ChipwareSlotsItem(Properties props, int humanityCost) {
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
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModTags.Items.BRAIN_ITEMS);
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.BRAIN);
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

    @Override
    public void onTick(Player player) {
        if (player.level().isClientSide) return;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);

        if (data.hasChipwareShardExact(ModItems.DATA_SHARD_RED.get())) {
            CyberwareAttributeHelper.applyModifier(player, "redshard_strength");
            CyberwareAttributeHelper.applyModifier(player, "redshard_speed");
            CyberwareAttributeHelper.applyModifier(player, "redshard_knockback");
        } else {
            CyberwareAttributeHelper.removeModifier(player, "redshard_strength");
            CyberwareAttributeHelper.removeModifier(player, "redshard_speed");
            CyberwareAttributeHelper.removeModifier(player, "redshard_knockback");
        }

        if (data.hasChipwareShardExact(ModItems.DATA_SHARD_ORANGE.get())) {
            CyberwareAttributeHelper.applyModifier(player, "orangeshard_ore");
            CyberwareAttributeHelper.applyModifier(player, "orangeshard_mining");
        } else {
            CyberwareAttributeHelper.removeModifier(player, "orangeshard_ore");
            CyberwareAttributeHelper.removeModifier(player, "orangeshard_mining");
        }

        if (data.hasChipwareShardExact(ModItems.DATA_SHARD_YELLOW.get())) {
            CyberwareAttributeHelper.applyModifier(player, "yellowshard_haggling");
        } else {
            CyberwareAttributeHelper.removeModifier(player, "yellowshard_haggling");
        }

        if (data.hasChipwareShardExact(ModItems.DATA_SHARD_GREEN.get())) {
            CyberwareAttributeHelper.applyModifier(player, "greenshard_xp");
        } else {
            CyberwareAttributeHelper.removeModifier(player, "greenshard_xp");
        }

        if (data.hasChipwareShardExact(ModItems.DATA_SHARD_CYAN.get())) {
            CyberwareAttributeHelper.applyModifier(player, "cyanshard_aim");
        } else {
            CyberwareAttributeHelper.removeModifier(player, "cyanshard_aim");
        }

        if (data.hasChipwareShardExact(ModItems.DATA_SHARD_BLUE.get())) {
            CyberwareAttributeHelper.applyModifier(player, "blueshard_swim");
            CyberwareAttributeHelper.applyModifier(player, "blueshard_mining");
            CyberwareAttributeHelper.applyModifier(player, "blueshard_movement");
            CyberwareAttributeHelper.applyModifier(player, "blueshard_oxygen");
        } else {
            CyberwareAttributeHelper.removeModifier(player, "blueshard_swim");
            CyberwareAttributeHelper.removeModifier(player, "blueshard_mining");
            CyberwareAttributeHelper.removeModifier(player, "blueshard_movement");
            CyberwareAttributeHelper.removeModifier(player, "blueshard_oxygen");
        }

        if (data.hasChipwareShardExact(ModItems.DATA_SHARD_PURPLE.get())) {
            CyberwareAttributeHelper.applyModifier(player, "purpleshard_pearl");
        } else {
            CyberwareAttributeHelper.removeModifier(player, "purpleshard_pearl");
        }

        if (data.hasChipwareShardExact(ModItems.DATA_SHARD_PINK.get())) {
            CyberwareAttributeHelper.applyModifier(player, "pinkshard_breeding");
        } else {
            CyberwareAttributeHelper.removeModifier(player, "pinkshard_breeding");
        }

        if (data.hasChipwareShardExact(ModItems.DATA_SHARD_BROWN.get())) {
            CyberwareAttributeHelper.applyModifier(player, "brownshard_crops");
        } else {
            CyberwareAttributeHelper.removeModifier(player, "brownshard_crops");
        }

        if (data.hasChipwareShardExact(ModItems.DATA_SHARD_GRAY.get())) {
            CyberwareAttributeHelper.applyModifier(player, "grayshard_speed");
            CyberwareAttributeHelper.applyModifier(player, "grayshard_handling");
        } else {
            CyberwareAttributeHelper.removeModifier(player, "grayshard_speed");
            CyberwareAttributeHelper.removeModifier(player, "grayshard_handling");
        }

        if (data.hasChipwareShardExact(ModItems.DATA_SHARD_BLACK.get())) {
            if (player.isSprinting()) {
                CyberwareAttributeHelper.removeModifier(player, "blackshard_crouch");
                CyberwareAttributeHelper.applyModifier(player, "blackshard_sprint");
            } else {
                CyberwareAttributeHelper.removeModifier(player, "blackshard_sprint");
                CyberwareAttributeHelper.applyModifier(player, "blackshard_crouch");
            }
        } else {
            CyberwareAttributeHelper.removeModifier(player, "blackshard_sprint");
            CyberwareAttributeHelper.removeModifier(player, "blackshard_crouch");
        }

        if (data.hasChipwareShardExact(ModItems.DATA_SHARD_BIOCHIP.get())) {

        } else {

        }
    }
}