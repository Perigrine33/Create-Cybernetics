package com.perigrine3.createcybernetics.entity.custom;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.ai.goal.CyberPiglinGangItemInterestGoal;
import com.perigrine3.createcybernetics.entity.ai.goal.CyberentityPneumaticCalvesJumpGoal;
import com.perigrine3.createcybernetics.entity.ai.goal.CyberentitySandevistanGoal;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;

public class HogBoyEntity extends AbstractCyberPiglinGangEntity {

    public static final ResourceKey<LootTable> HOGBOY_BARTERING =
            ResourceKey.create(
                    net.minecraft.core.registries.Registries.LOOT_TABLE,
                    ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "gameplay/hogboy_bartering")
            );

    public HogBoyEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createGangAttributes(24.0D, 4.0D, 0.28D, 0.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new CyberPiglinGangItemInterestGoal(this, 1.15D));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, PunklinEntity.class, 10.0F, 1.0D, 1.25D));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, PigstromEntity.class, 12.0F, 1.0D, 1.35D));
        this.goalSelector.addGoal(3, new CyberentityPneumaticCalvesJumpGoal(this));
        this.goalSelector.addGoal(4, new CyberentitySandevistanGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    @Override
    protected String getGangTextureFolder() {
        return "hogboy";
    }

    @Override
    protected boolean canBarter() {
        return true;
    }

    @Override
    protected boolean isDistractedByGold() {
        return false;
    }

    @Override
    protected boolean isDistractedByCyberware() {
        return true;
    }

    @Override
    protected ResourceKey<LootTable> getBarterLootTable() {
        return HOGBOY_BARTERING;
    }
}