package com.perigrine3.createcybernetics.item.cyberware.skin;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.EntityCyberwareData;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.ModMobAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class SyntheticSetulesItem extends Item implements ICyberwareItem {

    private final int humanityCost;

    public SyntheticSetulesItem(Properties props, int humanityCost) {
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
        return Set.of(CyberwareSlot.SKIN);
    }

    @Override
    public void onInstalled(LivingEntity entity) {
    }

    @Override
    public void onRemoved(LivingEntity entity) {
        if (entity.level().isClientSide()) return;
        removeAll(entity);
    }

    @Override
    public void onTick(LivingEntity entity) {
        if (entity.level().isClientSide()) return;

        if (entity instanceof Player player) {
            if (!player.hasData(ModAttachments.CYBERWARE)) {
                removeAll(entity);
                return;
            }

            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) {
                removeAll(entity);
                return;
            }

            InstalledRef ref = findEnabledRefForThisItem(data);
            if (ref == null) {
                removeAll(entity);
                return;
            }

            applyAll(entity);
            return;
        }

        if (!entity.hasData(ModMobAttachments.CYBERENTITY_CYBERWARE)) {
            removeAll(entity);
            return;
        }

        EntityCyberwareData data = entity.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
        if (data == null) {
            removeAll(entity);
            return;
        }

        InstalledRef ref = findEnabledRefForThisItem(data);
        if (ref == null) {
            removeAll(entity);
            return;
        }

        applyAll(entity);
    }

    private void applyAll(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(ModEffects.SYNTHETIC_SETULES_EFFECT, 20, 0, false, false, false));
    }

    private void removeAll(LivingEntity entity) {
        entity.removeEffect(ModEffects.SYNTHETIC_SETULES_EFFECT);
    }

    private InstalledRef findEnabledRefForThisItem(PlayerCyberwareData data) {
        for (var entry : data.getAll().entrySet()) {
            CyberwareSlot slot = entry.getKey();
            var arr = entry.getValue();
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {
                var inst = arr[i];
                if (inst == null) continue;

                ItemStack st = inst.getItem();
                if (st == null || st.isEmpty()) continue;
                if (st.getItem() != this) continue;
                if (!data.isEnabled(slot, i)) continue;

                return new InstalledRef(slot, i);
            }
        }
        return null;
    }

    private InstalledRef findEnabledRefForThisItem(EntityCyberwareData data) {
        for (var entry : data.getAll().entrySet()) {
            CyberwareSlot slot = entry.getKey();
            var arr = entry.getValue();
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {
                var inst = arr[i];
                if (inst == null) continue;

                ItemStack st = inst.getItem();
                if (st == null || st.isEmpty()) continue;
                if (st.getItem() != this) continue;
                if (!data.isEnabled(slot, i)) continue;

                return new InstalledRef(slot, i);
            }
        }
        return null;
    }

    private record InstalledRef(CyberwareSlot slot, int index) {}
}