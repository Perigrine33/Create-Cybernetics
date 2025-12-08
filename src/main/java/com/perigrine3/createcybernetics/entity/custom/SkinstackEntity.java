package com.perigrine3.createcybernetics.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.network.syncher.SynchedEntityData;

public class SkinstackEntity extends Mob {

    public SkinstackEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.noCulling = true;
    }

    // REQUIRED as of 1.21.1
    @Override
    public void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
    }

    // ---- Dummy attributes (Mob requires this) ----
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }
}
