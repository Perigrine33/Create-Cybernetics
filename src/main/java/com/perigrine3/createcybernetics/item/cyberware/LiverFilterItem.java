package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.EntityCyberwareData;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.ModMobAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class LiverFilterItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public LiverFilterItem(Properties props, int humanityCost) {
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
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.ORGANS);
    }

    @Override
    public boolean replacesOrgan() {
        return true;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of(CyberwareSlot.ORGANS);
    }

    @Override
    public TagKey<Item> getReplacedOrganItemTag(ItemStack installedStack, CyberwareSlot slot) {
        return ModTags.Items.LIVER_ITEMS;
    }

    @Override
    public void onInstalled(LivingEntity entity) {
    }

    @Override
    public void onRemoved(LivingEntity entity) {
    }

    @Override
    public void onTick(LivingEntity entity) {
        boolean hasImmuno = false;

        if (entity instanceof Player player) {
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data != null) {
                hasImmuno = data.hasSpecificItem(ModItems.SKINUPGRADES_IMMUNO.get(), CyberwareSlot.SKIN);
            }
        } else {
            EntityCyberwareData data = entity.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
            if (data != null) {
                hasImmuno = data.hasSpecificItem(ModItems.SKINUPGRADES_IMMUNO.get(), CyberwareSlot.SKIN);
            }
        }

        if (!hasImmuno) {
            if (entity.hasEffect(MobEffects.WEAKNESS)) {
                entity.removeEffect(MobEffects.WEAKNESS);
            }
            if (entity.hasEffect(MobEffects.POISON)) {
                entity.removeEffect(MobEffects.POISON);
            }
        }

        if (entity.hasEffect(MobEffects.HUNGER)) {
            entity.removeEffect(MobEffects.HUNGER);
        }
        if (entity.hasEffect(MobEffects.HARM)) {
            entity.removeEffect(MobEffects.HARM);
        }
        if (entity.hasEffect(MobEffects.DIG_SLOWDOWN)) {
            entity.removeEffect(MobEffects.DIG_SLOWDOWN);
        }
    }
}