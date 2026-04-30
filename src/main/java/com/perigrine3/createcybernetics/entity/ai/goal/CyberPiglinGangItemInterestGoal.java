package com.perigrine3.createcybernetics.entity.ai.goal;

import com.perigrine3.createcybernetics.entity.custom.AbstractCyberPiglinGangEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class CyberPiglinGangItemInterestGoal extends Goal {

    private static final double SEARCH_RANGE = 8.0D;
    private static final double PICKUP_DISTANCE_SQ = 2.25D;
    private static final int DISTRACTION_TICKS = 80;

    private final AbstractCyberPiglinGangEntity mob;
    private final double speedModifier;

    private ItemEntity targetItem;

    public CyberPiglinGangItemInterestGoal(AbstractCyberPiglinGangEntity mob, double speedModifier) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (!mob.isAlive()) return false;
        if (mob.level().isClientSide) return false;
        if (mob.isDistracted()) return false;

        targetItem = findNearestInterestingItem();
        return targetItem != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (!mob.isAlive()) return false;
        if (mob.level().isClientSide) return false;
        if (targetItem == null || !targetItem.isAlive()) return false;
        if (targetItem.getItem().isEmpty()) return false;
        if (mob.isDistracted()) return false;
        if (mob.shouldIgnoreGroundItem(targetItem)) return false;

        return mob.isInterestedInGroundItem(targetItem)
                && mob.distanceToSqr(targetItem) <= SEARCH_RANGE * SEARCH_RANGE;
    }

    @Override
    public void start() {
        mob.setTarget(null);
        mob.getNavigation().moveTo(targetItem, speedModifier);
    }

    @Override
    public void tick() {
        if (targetItem == null || !targetItem.isAlive()) return;

        if (mob.shouldIgnoreGroundItem(targetItem)) {
            targetItem = null;
            mob.getNavigation().stop();
            return;
        }

        mob.setTarget(null);
        mob.getLookControl().setLookAt(targetItem, 30.0F, 30.0F);

        if (mob.distanceToSqr(targetItem) > PICKUP_DISTANCE_SQ) {
            mob.getNavigation().moveTo(targetItem, speedModifier);
            return;
        }

        consumeInterestingItem(targetItem);
    }

    @Override
    public void stop() {
        targetItem = null;
    }

    private ItemEntity findNearestInterestingItem() {
        List<ItemEntity> items = mob.level().getEntitiesOfClass(
                ItemEntity.class,
                mob.getBoundingBox().inflate(SEARCH_RANGE),
                itemEntity -> itemEntity != null
                        && itemEntity.isAlive()
                        && !itemEntity.getItem().isEmpty()
                        && !mob.shouldIgnoreGroundItem(itemEntity)
                        && mob.isInterestedInGroundItem(itemEntity)
        );

        return items.stream()
                .min(Comparator.comparingDouble(mob::distanceToSqr))
                .orElse(null);
    }

    private void consumeInterestingItem(ItemEntity itemEntity) {
        if (itemEntity == null || !itemEntity.isAlive()) return;
        if (mob.shouldIgnoreGroundItem(itemEntity)) return;

        ItemStack stack = itemEntity.getItem();
        if (stack.isEmpty()) return;

        boolean shouldBarter = mob.isBarterItem(stack);

        stack.shrink(1);
        if (stack.isEmpty()) {
            itemEntity.discard();
        }

        if (shouldBarter) {
            mob.startBarterDelay();
        } else {
            mob.startDistraction(DISTRACTION_TICKS);
        }

        targetItem = null;
    }
}