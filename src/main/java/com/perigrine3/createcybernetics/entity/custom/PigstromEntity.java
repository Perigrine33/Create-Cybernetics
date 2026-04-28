package com.perigrine3.createcybernetics.entity.custom;

import com.perigrine3.createcybernetics.entity.ai.goal.CyberPiglinGangItemInterestGoal;
import com.perigrine3.createcybernetics.entity.ai.goal.CyberentityFirestarterAttackGoal;
import com.perigrine3.createcybernetics.entity.ai.goal.CyberentityPneumaticCalvesJumpGoal;
import com.perigrine3.createcybernetics.entity.ai.goal.CyberentitySandevistanGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PigstromEntity extends AbstractCyberPiglinGangEntity {

    public PigstromEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createGangAttributes(45.0D, 7.0D, 0.32D, 4.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new CyberPiglinGangItemInterestGoal(this, 1.25D));
        this.goalSelector.addGoal(2, new CyberentityFirestarterAttackGoal(this, 1.15D, false));
        this.goalSelector.addGoal(3, new CyberentityPneumaticCalvesJumpGoal(this));
        this.goalSelector.addGoal(4, new CyberentitySandevistanGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                this,
                LivingEntity.class,
                10,
                true,
                false,
                target -> !this.isDistracted() && isValidPigstromTarget(target)
        ));
    }

    private boolean isValidPigstromTarget(LivingEntity target) {
        if (target == null) return false;
        if (!target.isAlive()) return false;
        if (target == this) return false;
        return !(target instanceof PigstromEntity);
    }

    @Override
    protected String getGangTextureFolder() {
        return "pigstrom";
    }

    @Override
    protected boolean canBarter() {
        return false;
    }

    @Override
    protected boolean isDistractedByGold() {
        return false;
    }

    @Override
    protected boolean isDistractedByCyberware() {
        return true;
    }
}