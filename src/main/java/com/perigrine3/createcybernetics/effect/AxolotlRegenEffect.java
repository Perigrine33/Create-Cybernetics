package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.common.capabilities.EntityCyberwareData;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.ModMobAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AxolotlRegenEffect extends MobEffect {
    private static final TagKey<Item> COUNT_TAG = ModTags.Items.CYBERWARE_ITEM;
    private static final float EXHAUSTION_PER_HEALTH = 2.0f;

    public AxolotlRegenEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x000000);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return true;

        if (entity instanceof ServerPlayer sp) {
            applyPlayerRegen(sp);
            return true;
        }

        applyEntityRegen(entity);
        return true;
    }

    private static void applyPlayerRegen(ServerPlayer sp) {
        PlayerCyberwareData data = sp.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        int implantsTagged = countInstalledTagged(data, COUNT_TAG);
        Tier tier = Tier.fromImplantCount(implantsTagged);
        if (tier == null) return;

        if ((sp.tickCount % tier.intervalTicks) != 0) return;

        float max = sp.getMaxHealth();
        float cur = sp.getHealth();
        if (cur >= max) return;

        float appliedHeal = Math.min(max - cur, tier.healAmount);
        if (appliedHeal <= 0.0f) return;

        sp.heal(appliedHeal);
        sp.causeFoodExhaustion(appliedHeal * EXHAUSTION_PER_HEALTH);
    }

    private static void applyEntityRegen(LivingEntity entity) {
        if (!entity.hasData(ModMobAttachments.CYBERENTITY_CYBERWARE)) return;

        EntityCyberwareData data = entity.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
        if (data == null) return;

        int implantsTagged = countInstalledTagged(data, COUNT_TAG);
        Tier tier = Tier.fromImplantCount(implantsTagged);
        if (tier == null) return;

        if ((entity.tickCount % tier.intervalTicks) != 0) return;

        float max = entity.getMaxHealth();
        float cur = entity.getHealth();
        if (cur >= max) return;

        float appliedHeal = Math.min(max - cur, tier.healAmount);
        if (appliedHeal <= 0.0f) return;

        entity.heal(appliedHeal);
    }

    private static int countInstalledTagged(PlayerCyberwareData data, TagKey<Item> tag) {
        int count = 0;

        for (var entry : data.getAll().entrySet()) {
            var arr = entry.getValue();
            if (arr == null) continue;

            for (var cw : arr) {
                if (cw == null) continue;

                ItemStack st = cw.getItem();
                if (st == null || st.isEmpty()) continue;
                if (st.is(tag)) count++;
            }
        }

        return count;
    }

    private static int countInstalledTagged(EntityCyberwareData data, TagKey<Item> tag) {
        int count = 0;

        for (var entry : data.getAll().entrySet()) {
            var arr = entry.getValue();
            if (arr == null) continue;

            for (var cw : arr) {
                if (cw == null) continue;

                ItemStack st = cw.getItem();
                if (st == null || st.isEmpty()) continue;
                if (st.is(tag)) count++;
            }
        }

        return count;
    }

    private enum Tier {
        TIER_3(20, 3.0f),
        TIER_2(60, 2.0f),
        TIER_1(100, 1.0f);

        final int intervalTicks;
        final float healAmount;

        Tier(int intervalTicks, float healAmount) {
            this.intervalTicks = intervalTicks;
            this.healAmount = healAmount;
        }

        static Tier fromImplantCount(int implantsTagged) {
            if (implantsTagged <= 2) return TIER_3;
            if (implantsTagged <= 5) return TIER_2;
            if (implantsTagged <= 8) return TIER_1;
            return null;
        }
    }
}