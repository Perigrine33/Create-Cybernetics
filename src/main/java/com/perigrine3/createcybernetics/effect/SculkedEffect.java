package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.common.surgery.DefaultOrgans;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.ArrayList;
import java.util.List;

public class SculkedEffect extends MobEffect {

    private static final float CHANCE = 0.5f;

    public SculkedEffect() {
        super(MobEffectCategory.HARMFUL, 0x0A0F14);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration > 0 && (duration % 12000) == 0;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!(entity instanceof ServerPlayer player)) return true;

        RandomSource rng = player.getRandom();
        if (rng.nextFloat() >= CHANCE) return true;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return true;

        List<Replacement> eligible = findEligibleReplacements(data);
        if (eligible.isEmpty()) return true;

        Replacement chosen = eligible.get(rng.nextInt(eligible.size()));
        if (replaceIfStillDefault(player, data, chosen)) {
            data.recomputeHumanityBaseFromInstalled();
            data.clampEnergyToCapacity(player);
            data.setDirty();
        }

        return true;
    }

    /* ---------------- replacement selection ---------------- */

    private record Replacement(CyberwareSlot slot, int index, Item sculkItem) {}

    private static List<Replacement> findEligibleReplacements(PlayerCyberwareData data) {
        List<Replacement> out = new ArrayList<>(16);

        // INTERNALS
        addIfEligible(data, out, CyberwareSlot.BRAIN, 0, ModItems.BODYPART_SCULKBRAIN.get());
        addIfEligible(data, out, CyberwareSlot.ORGANS, 0, ModItems.BODYPART_SCULKLIVER.get());
        addIfEligible(data, out, CyberwareSlot.ORGANS, 1, ModItems.BODYPART_SCULKINTESTINES.get());
        addIfEligible(data, out, CyberwareSlot.MUSCLE, 0, ModItems.BODYPART_SCULKMUSCLE.get());
        addIfEligible(data, out, CyberwareSlot.SKIN, 0, ModItems.BODYPART_SCULKSKIN.get());

        // LIMBS
        addIfEligible(data, out, CyberwareSlot.RLEG, 0, ModItems.BODYPART_SCULKRIGHTLEG.get());
        addIfEligible(data, out, CyberwareSlot.LLEG, 0, ModItems.BODYPART_SCULKLEFTLEG.get());
        addIfEligible(data, out, CyberwareSlot.RARM, 0, ModItems.BODYPART_SCULKRIGHTARM.get());
        addIfEligible(data, out, CyberwareSlot.LARM, 0, ModItems.BODYPART_SCULKLEFTARM.get());

        return out;
    }

    private static void addIfEligible(PlayerCyberwareData data, List<Replacement> out, CyberwareSlot slot, int index, Item sculkItem) {
        if (sculkItem == null) return;

        InstalledCyberware cur = data.get(slot, index);
        ItemStack curStack = (cur == null || cur.getItem() == null) ? ItemStack.EMPTY : cur.getItem();

        ItemStack def = DefaultOrgans.get(slot, index);
        if (isStillDefault(curStack, def)) {
            out.add(new Replacement(slot, index, sculkItem));
        }
    }

    private static boolean replaceIfStillDefault(ServerPlayer player, PlayerCyberwareData data, Replacement r) {
        InstalledCyberware cur = data.get(r.slot, r.index);
        ItemStack curStack = (cur == null || cur.getItem() == null) ? ItemStack.EMPTY : cur.getItem();

        ItemStack def = DefaultOrgans.get(r.slot, r.index);
        if (!isStillDefault(curStack, def)) return false;

        // NOTE: we overwrite the slot directly (no remove) because this is a default organ swap.
        ItemStack replacement = new ItemStack(r.sculkItem);
        replacement.setCount(1);

        int humanityCost = 0;
        if (replacement.getItem() instanceof ICyberwareItem cw) {
            humanityCost = cw.getHumanityCost();
        }

        InstalledCyberware installed = new InstalledCyberware(replacement, r.slot, r.index, humanityCost);
        installed.setPowered(true);

        data.set(r.slot, r.index, installed);

        if (replacement.getItem() instanceof ICyberwareItem cw) {
            cw.onInstalled(player);
        }

        return true;
    }

    private static boolean isStillDefault(ItemStack curStack, ItemStack def) {
        if (def == null || def.isEmpty()) {
            return curStack == null || curStack.isEmpty();
        }
        if (curStack == null || curStack.isEmpty()) return false;
        return curStack.is(def.getItem());
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class SculkedEffectGuard {

        private SculkedEffectGuard() {}

        @SubscribeEvent
        public static void onEffectRemove(MobEffectEvent.Remove event) {
            LivingEntity entity = event.getEntity();
            if (entity.level().isClientSide) return;

            MobEffectInstance inst = event.getEffectInstance();
            if (inst == null) return;
            if (inst.getEffect() != ModEffects.SCULKED_EFFECT) return;

            // "milk can't remove it, but dying can"
            if (entity.isAlive()) {
                event.setCanceled(true);
            }
        }
    }
}