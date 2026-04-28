package com.perigrine3.createcybernetics.item.cyberware.skin;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.EntityCyberwareData;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.ModMobAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class ImmunosuppressorItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final String NBT_WEAK_LAST_AMP = "cc_immuno_weak_lastAmp";
    private static final String NBT_WEAK_LAST_DUR = "cc_immuno_weak_lastDur";
    private static final String NBT_POISON_LAST_AMP = "cc_immuno_poison_lastAmp";
    private static final String NBT_POISON_LAST_DUR = "cc_immuno_poison_lastDur";

    public ImmunosuppressorItem(Properties props, int humanityCost) {
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
    public void onInstalled(LivingEntity entity) {
    }

    @Override
    public void onRemoved(LivingEntity entity) {
        if (entity.level().isClientSide) return;

        CompoundTag tag = entity.getPersistentData();
        tag.remove(NBT_WEAK_LAST_AMP);
        tag.remove(NBT_WEAK_LAST_DUR);
        tag.remove(NBT_POISON_LAST_AMP);
        tag.remove(NBT_POISON_LAST_DUR);
    }

    @Override
    public void onTick(LivingEntity entity) {
        if (entity.level().isClientSide) return;

        boolean hasImmuno;

        if (entity instanceof Player player) {
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            hasImmuno = data != null && data.hasSpecificItem(ModItems.SKINUPGRADES_IMMUNO.get(), CyberwareSlot.SKIN);
        } else {
            EntityCyberwareData data = entity.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
            hasImmuno = data != null && data.hasSpecificItem(ModItems.SKINUPGRADES_IMMUNO.get(), CyberwareSlot.SKIN);
        }

        if (!hasImmuno) {
            amplifyOncePerApplication(entity, MobEffects.WEAKNESS, NBT_WEAK_LAST_AMP, NBT_WEAK_LAST_DUR);
            amplifyOncePerApplication(entity, MobEffects.POISON, NBT_POISON_LAST_AMP, NBT_POISON_LAST_DUR);
        }
    }

    private static void amplifyOncePerApplication(LivingEntity entity, Holder<MobEffect> effect, String nbtLastAmpKey, String nbtLastDurKey) {
        MobEffectInstance inst = entity.getEffect(effect);
        CompoundTag tag = entity.getPersistentData();

        if (inst == null) {
            tag.putInt(nbtLastAmpKey, Integer.MIN_VALUE);
            tag.putInt(nbtLastDurKey, Integer.MIN_VALUE);
            return;
        }

        int lastAmp = tag.getInt(nbtLastAmpKey);
        int lastDur = tag.getInt(nbtLastDurKey);
        int curAmp = inst.getAmplifier();
        int curDur = inst.getDuration();

        boolean refreshedOrNew = (curDur > lastDur) || (curAmp != lastAmp);

        if (!refreshedOrNew) return;

        int newAmp = curAmp + 1;
        MobEffectInstance boosted = new MobEffectInstance(effect, curDur, newAmp, inst.isAmbient(), inst.isVisible(), inst.showIcon());
        entity.addEffect(boosted);

        tag.putInt(nbtLastAmpKey, newAmp);
        tag.putInt(nbtLastDurKey, curDur);
    }
}