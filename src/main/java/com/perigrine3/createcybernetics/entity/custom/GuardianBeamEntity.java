package com.perigrine3.createcybernetics.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class GuardianBeamEntity extends Entity {
    private static final EntityDataAccessor<Integer> SHOOTER_ID =
            SynchedEntityData.defineId(GuardianBeamEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LIFE =
            SynchedEntityData.defineId(GuardianBeamEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Float> START_X =
            SynchedEntityData.defineId(GuardianBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> START_Y =
            SynchedEntityData.defineId(GuardianBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> START_Z =
            SynchedEntityData.defineId(GuardianBeamEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> END_X =
            SynchedEntityData.defineId(GuardianBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> END_Y =
            SynchedEntityData.defineId(GuardianBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> END_Z =
            SynchedEntityData.defineId(GuardianBeamEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> POWER =
            SynchedEntityData.defineId(GuardianBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> CHARGING =
            SynchedEntityData.defineId(GuardianBeamEntity.class, EntityDataSerializers.BOOLEAN);

    public GuardianBeamEntity(EntityType<? extends GuardianBeamEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SHOOTER_ID, 0);
        builder.define(LIFE, 10);

        builder.define(START_X, 0.0F);
        builder.define(START_Y, 0.0F);
        builder.define(START_Z, 0.0F);

        builder.define(END_X, 0.0F);
        builder.define(END_Y, 0.0F);
        builder.define(END_Z, 0.0F);

        builder.define(POWER, 1.0F);
        builder.define(CHARGING, false);
    }

    @Override
    public void tick() {
        super.tick();
        int life = getLife() - 1;
        setLife(life);
        if (life <= 0) discard();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {}

    public int getShooterId() {
        return this.entityData.get(SHOOTER_ID);
    }

    public void setShooterId(int id) {
        this.entityData.set(SHOOTER_ID, id);
    }

    public int getLife() {
        return this.entityData.get(LIFE);
    }

    public void setLife(int ticks) {
        this.entityData.set(LIFE, ticks);
    }

    public Vec3 getStart() {
        return new Vec3(this.entityData.get(START_X), this.entityData.get(START_Y), this.entityData.get(START_Z));
    }

    public void setStart(Vec3 v) {
        this.entityData.set(START_X, (float) v.x);
        this.entityData.set(START_Y, (float) v.y);
        this.entityData.set(START_Z, (float) v.z);
    }

    public Vec3 getEnd() {
        return new Vec3(this.entityData.get(END_X), this.entityData.get(END_Y), this.entityData.get(END_Z));
    }

    public void setEnd(Vec3 v) {
        this.entityData.set(END_X, (float) v.x);
        this.entityData.set(END_Y, (float) v.y);
        this.entityData.set(END_Z, (float) v.z);
    }

    public float getPower() {
        return this.entityData.get(POWER);
    }

    public void setPower(float power) {
        this.entityData.set(POWER, power);
    }

    public boolean isCharging() {
        return this.entityData.get(CHARGING);
    }

    public void setCharging(boolean charging) {
        this.entityData.set(CHARGING, charging);
    }
}
