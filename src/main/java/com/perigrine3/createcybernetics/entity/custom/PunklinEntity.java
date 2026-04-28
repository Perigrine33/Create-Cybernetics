package com.perigrine3.createcybernetics.entity.custom;

import com.perigrine3.createcybernetics.entity.ai.goal.CyberPiglinGangItemInterestGoal;
import com.perigrine3.createcybernetics.entity.ai.goal.CyberentityFirestarterAttackGoal;
import com.perigrine3.createcybernetics.entity.ai.goal.CyberentityPneumaticCalvesJumpGoal;
import com.perigrine3.createcybernetics.entity.ai.goal.CyberentitySandevistanGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PunklinEntity extends AbstractCyberPiglinGangEntity {

    private static final int PLAYER_IMPLANT_ATTACK_THRESHOLD = 25;

    public PunklinEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createGangAttributes(32.0D, 5.0D, 0.30D, 2.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new CyberPiglinGangItemInterestGoal(this, 1.2D));
        this.goalSelector.addGoal(2, new CyberentityFirestarterAttackGoal(this, 1.05D, false));
        this.goalSelector.addGoal(3, new CyberentityPneumaticCalvesJumpGoal(this));
        this.goalSelector.addGoal(4, new CyberentitySandevistanGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.85D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PigstromEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, HogBoyEntity.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(
                this,
                Player.class,
                10,
                true,
                false,
                player -> !this.isDistracted()
                        && player instanceof Player p
                        && countPlayerNonWetwareCyberware(p) > PLAYER_IMPLANT_ATTACK_THRESHOLD
        ));
        this.targetSelector.addGoal(5, new PunklinHarassPiglinTargetGoal(this));
    }

    @Override
    protected String getGangTextureFolder() {
        return "punklin";
    }

    @Override
    protected boolean canBarter() {
        return false;
    }

    @Override
    protected boolean isDistractedByGold() {
        return true;
    }

    @Override
    protected boolean isDistractedByCyberware() {
        return true;
    }

    private static final class PunklinHarassPiglinTargetGoal extends NearestAttackableTargetGoal<Piglin> {
        private final PunklinEntity punklin;

        private PunklinHarassPiglinTargetGoal(PunklinEntity punklin) {
            super(punklin, Piglin.class, 20, true, false, piglin -> !punklin.isDistracted());
            this.punklin = punklin;
        }

        @Override
        public boolean canUse() {
            if (punklin.isDistracted()) return false;
            if (punklin.getRandom().nextInt(100) != 0) return false;
            return super.canUse();
        }
    }
}