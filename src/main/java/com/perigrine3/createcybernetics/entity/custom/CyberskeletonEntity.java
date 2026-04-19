package com.perigrine3.createcybernetics.entity.custom;

import com.perigrine3.createcybernetics.common.attributes.ModAttributes;
import com.perigrine3.createcybernetics.entity.ai.goal.CyberentityPneumaticCalvesJumpGoal;
import com.perigrine3.createcybernetics.entity.ai.goal.CyberentitySandevistanGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForgeMod;

public class CyberskeletonEntity extends Skeleton {
    public CyberskeletonEntity(EntityType<? extends Skeleton> type, Level level) {
        super(type, level);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Skeleton.createAttributes()
                .add(Attributes.MAX_HEALTH, 35.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.25D)

                .add(Attributes.ARMOR, 0.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 0.0D)
                .add(Attributes.OXYGEN_BONUS, 0.0D)
                .add(Attributes.JUMP_STRENGTH, 0.42D)
                .add(Attributes.ATTACK_SPEED, 4.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.0D)
                .add(Attributes.LUCK, 0.0D)
                .add(Attributes.BLOCK_INTERACTION_RANGE, 4.5D)
                .add(Attributes.ENTITY_INTERACTION_RANGE, 3.0D)
                .add(Attributes.STEP_HEIGHT, 0.6D)
                .add(Attributes.GRAVITY, 0.08D)
                .add(Attributes.SCALE, 1.0D)
                .add(Attributes.FLYING_SPEED, 0.4D)
                .add(Attributes.BLOCK_BREAK_SPEED, 1.0D)
                .add(Attributes.SAFE_FALL_DISTANCE, 3.0D)
                .add(Attributes.BURNING_TIME, 1.0D)
                .add(Attributes.SUBMERGED_MINING_SPEED, 0.2D)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.0D)
                .add(Attributes.MINING_EFFICIENCY, 0.0D)
                .add(Attributes.SNEAKING_SPEED, 0.3D)

                .add(NeoForgeMod.SWIM_SPEED, 1.0D)

                .add(ModAttributes.XP_GAIN_MULTIPLIER, 0.0D)
                .add(ModAttributes.ORE_DROP_MULTIPLIER, 0.0D)
                .add(ModAttributes.HAGGLING, 0.0D)
                .add(ModAttributes.ARROW_INACCURACY, 0.0D)
                .add(ModAttributes.BREEDING_MULTIPLIER, 0.0D)
                .add(ModAttributes.CROP_MULTIPLIER, 0.0D)
                .add(ModAttributes.ELYTRA_SPEED, 0.0D)
                .add(ModAttributes.ELYTRA_HANDLING, 0.0D)
                .add(ModAttributes.INSOMNIA, 0.0D)
                .add(ModAttributes.ENDER_PEARL_DAMAGE, 0.0D);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        super.playStepSound(pos, blockIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(3, new CyberentityPneumaticCalvesJumpGoal(this));
        this.goalSelector.addGoal(3, new CyberentitySandevistanGoal(this));
    }
}
