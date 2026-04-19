package com.perigrine3.createcybernetics.entity.custom;

import com.perigrine3.createcybernetics.common.attributes.ModAttributes;
import com.perigrine3.createcybernetics.entity.ai.goal.CyberentityFirestarterAttackGoal;
import com.perigrine3.createcybernetics.entity.ai.goal.CyberentityPneumaticCalvesJumpGoal;
import com.perigrine3.createcybernetics.entity.ai.goal.CyberentitySandevistanGoal;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;

public class SmasherEntity extends AbstractIllager {
    public static final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

public SmasherEntity(EntityType<? extends AbstractIllager> entityType, Level level) {
        super(entityType, level);
        this.setCanJoinRaid(true);
        this.xpReward = 50;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 25.0F));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Villager.class, true));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.goalSelector.addGoal(1, new CyberentityPneumaticCalvesJumpGoal(this));
        this.goalSelector.addGoal(1, new CyberentityFirestarterAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(1, new CyberentitySandevistanGoal(this));
}

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 150.0D)
                .add(Attributes.ATTACK_DAMAGE, 18.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 2.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)

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
    public void applyRaidBuffs(ServerLevel serverLevel, int i, boolean b) {

    }

    @Override
    public SoundEvent getCelebrateSound() {
        return net.minecraft.sounds.SoundEvents.VINDICATOR_CELEBRATE;
    }

    @Override
    public float getVoicePitch() {
        // Lower = deeper, higher = squeakier
        return 0.5F + (this.random.nextFloat() - 0.5F) * 0.05F;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean flag = super.doHurtTarget(target);

        if (flag && target instanceof LivingEntity living) {
            living.hurt(this.level().damageSources().mobAttack(this), 6.0F);
        }

        return flag;
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (entity instanceof net.minecraft.world.entity.raid.Raider) return true;
        if (entity instanceof net.minecraft.world.entity.monster.Ravager) return true;
        return super.isAlliedTo(entity);
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return true;
    }

    private void setupAnimationStates() {
        if(this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }

    }

    public void tick() {
        super.tick();

        if(this.level().isClientSide) {
            this.setupAnimationStates();
        }
    }

}
