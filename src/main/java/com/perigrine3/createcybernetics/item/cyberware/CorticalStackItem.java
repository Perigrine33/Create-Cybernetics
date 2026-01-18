package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.item.generic.XPCapsuleItem;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

import java.util.List;
import java.util.Set;

public class CorticalStackItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public CorticalStackItem(Properties props, int humanityCost) {
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
    public Set<Item> incompatibleCyberware(ItemStack installedStack, CyberwareSlot slot) {
        assert ModItems.BRAINUPGRADES_CONSCIOUSNESSTRANSMITTER != null;
        return Set.of(ModItems.BRAINUPGRADES_CONSCIOUSNESSTRANSMITTER.get());
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
    }

    @Override
    public boolean dropsOnDeath(ItemStack installedStack, CyberwareSlot slot) {
        return false;
    }

    @Override
    public void onInstalled(Player player, ItemStack installedStack) {
        if (player.level().isClientSide) return;
        if (!(player instanceof ServerPlayer sp)) return;
        if (installedStack == null || installedStack.isEmpty()) return;

        int xp = XPCapsuleItem.getStoredXp(installedStack);

        if (xp > 0) {
            sp.giveExperiencePoints(xp);
        }

        clearXpPayload(installedStack);
    }

    private static void clearXpPayload(ItemStack st) {
        CustomData cd = st.get(DataComponents.CUSTOM_DATA);
        if (cd != null && !cd.isEmpty()) {
            CustomData.update(DataComponents.CUSTOM_DATA, st, tag -> {
                tag.remove(XPCapsuleItem.NBT_XP_POINTS);
                tag.remove(XPCapsuleItem.NBT_OWNER);
            });

            CustomData after = st.get(DataComponents.CUSTOM_DATA);
            if (after == null || after.isEmpty()) {
                st.remove(DataComponents.CUSTOM_DATA);
            }
        }

        st.remove(DataComponents.CUSTOM_NAME);
    }
}