package com.perigrine3.createcybernetics.item.cyberware.skin;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.EntityCyberwareData;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.ModMobAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class SyntheticChromatophoresItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final int ENERGY_PER_TICK_ACTIVE = 7;

    public SyntheticChromatophoresItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("tooltip.createcybernetics.skinupgrades_chromatophores.energy").withStyle(ChatFormatting.RED));
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
        return true;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of(CyberwareSlot.SKIN);
    }

    @Override
    public int maxStacksPerSlotType(ItemStack stack, CyberwareSlot slotType) {
        return 1;
    }

    private int findEnabledIndex(PlayerCyberwareData data, CyberwareSlot slot) {
        InstalledCyberware[] arr = data.getAll().get(slot);
        if (arr == null) return -1;

        for (int i = 0; i < arr.length; i++) {
            InstalledCyberware inst = arr[i];
            if (inst == null) continue;

            ItemStack st = inst.getItem();
            if (st == null || st.isEmpty()) continue;

            if (st.getItem() != this) continue;

            return data.isEnabled(slot, i) ? i : -1;
        }

        return -1;
    }

    private int findEnabledIndex(EntityCyberwareData data, CyberwareSlot slot) {
        InstalledCyberware[] arr = data.getAll().get(slot);
        if (arr == null) return -1;

        for (int i = 0; i < arr.length; i++) {
            InstalledCyberware inst = arr[i];
            if (inst == null) continue;

            ItemStack st = inst.getItem();
            if (st == null || st.isEmpty()) continue;

            if (st.getItem() != this) continue;

            return data.isEnabled(slot, i) ? i : -1;
        }

        return -1;
    }

    @Override
    public int getEnergyUsedPerTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        if (entity == null) return 0;
        if (slot != CyberwareSlot.SKIN) return 0;

        if (entity instanceof Player player) {
            if (!player.hasData(ModAttachments.CYBERWARE)) return 0;
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return 0;

            return findEnabledIndex(data, slot) >= 0 ? ENERGY_PER_TICK_ACTIVE : 0;
        }

        if (!entity.hasData(ModMobAttachments.CYBERENTITY_CYBERWARE)) return 0;
        EntityCyberwareData data = entity.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
        if (data == null) return 0;

        return findEnabledIndex(data, slot) >= 0 ? ENERGY_PER_TICK_ACTIVE : 0;
    }

    @Override
    public void onTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot, int index) {
        if (entity.level().isClientSide) return;

        if (slot != CyberwareSlot.SKIN) {
            entity.removeEffect(MobEffects.INVISIBILITY);
            return;
        }

        boolean enabled;
        InstalledCyberware cw;

        if (entity instanceof Player player) {
            if (!player.hasData(ModAttachments.CYBERWARE)) {
                entity.removeEffect(MobEffects.INVISIBILITY);
                return;
            }

            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) {
                entity.removeEffect(MobEffects.INVISIBILITY);
                return;
            }

            enabled = data.isEnabled(slot, index);
            cw = data.get(slot, index);
        } else {
            if (!entity.hasData(ModMobAttachments.CYBERENTITY_CYBERWARE)) {
                entity.removeEffect(MobEffects.INVISIBILITY);
                return;
            }

            EntityCyberwareData data = entity.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
            if (data == null) {
                entity.removeEffect(MobEffects.INVISIBILITY);
                return;
            }

            enabled = data.isEnabled(slot, index);
            cw = data.get(slot, index);
        }

        boolean powered = cw != null && cw.isPowered();

        if (!enabled || !powered) {
            entity.removeEffect(MobEffects.INVISIBILITY);
            return;
        }

        entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 220, 0, false, false, false));
    }

    @Override
    public void onRemoved(LivingEntity entity) {
        if (!entity.level().isClientSide) {
            entity.removeEffect(MobEffects.INVISIBILITY);
        }
    }
}