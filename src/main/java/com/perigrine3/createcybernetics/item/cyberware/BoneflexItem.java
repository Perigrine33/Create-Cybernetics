package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.EntityCyberwareData;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.ModMobAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class BoneflexItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public BoneflexItem(Properties props, int humanityCost) {
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
        return Set.of(ModTags.Items.BONE_ITEMS);
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.BONE);
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
        int stacks = 0;

        if (entity instanceof Player player) {
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);

            for (int i = 0; i < CyberwareSlot.BONE.size; i++) {
                if (data.isInstalled(ModItems.BONEUPGRADES_BONEFLEX.get(), CyberwareSlot.BONE, i)) {
                    stacks++;
                }
            }
        } else {
            EntityCyberwareData data = entity.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);

            for (int i = 0; i < CyberwareSlot.BONE.size; i++) {
                if (data.isInstalled(ModItems.BONEUPGRADES_BONEFLEX.get(), CyberwareSlot.BONE)) {
                    stacks++;
                }
            }
        }

        if (stacks > 3) stacks = 3;

        CyberwareAttributeHelper.removeModifier(entity, "boneflex_fall_1");
        CyberwareAttributeHelper.removeModifier(entity, "boneflex_fall_2");
        CyberwareAttributeHelper.removeModifier(entity, "boneflex_fall_3");

        if (stacks >= 1) CyberwareAttributeHelper.applyModifier(entity, "boneflex_fall_1");
        if (stacks >= 2) CyberwareAttributeHelper.applyModifier(entity, "boneflex_fall_2");
        if (stacks >= 3) CyberwareAttributeHelper.applyModifier(entity, "boneflex_fall_3");
    }

    @Override
    public void onRemoved(LivingEntity entity) {
        CyberwareAttributeHelper.removeModifier(entity, "boneflex_fall_1");
        CyberwareAttributeHelper.removeModifier(entity, "boneflex_fall_2");
        CyberwareAttributeHelper.removeModifier(entity, "boneflex_fall_3");

        onInstalled(entity);
    }

    @Override
    public void onTick(LivingEntity entity) {
        ICyberwareItem.super.onTick(entity);
    }
}