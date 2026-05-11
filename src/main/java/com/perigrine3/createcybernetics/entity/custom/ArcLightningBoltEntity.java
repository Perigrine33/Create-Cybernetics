package com.perigrine3.createcybernetics.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ArcLightningBoltEntity extends Entity {
    private static final EntityDataAccessor<Float> START_X = SynchedEntityData.defineId(ArcLightningBoltEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> START_Y = SynchedEntityData.defineId(ArcLightningBoltEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> START_Z = SynchedEntityData.defineId(ArcLightningBoltEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> END_X = SynchedEntityData.defineId(ArcLightningBoltEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> END_Y = SynchedEntityData.defineId(ArcLightningBoltEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> END_Z = SynchedEntityData.defineId(ArcLightningBoltEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> LIFE = SynchedEntityData.defineId(ArcLightningBoltEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Long> SEED = SynchedEntityData.defineId(ArcLightningBoltEntity.class, EntityDataSerializers.LONG);

    public ArcLightningBoltEntity(EntityType<? extends ArcLightningBoltEntity> type, Level level) {
        super(type, level);
        this.noCulling = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(START_X, 0.0F);
        builder.define(START_Y, 0.0F);
        builder.define(START_Z, 0.0F);
        builder.define(END_X, 0.0F);
        builder.define(END_Y, 0.0F);
        builder.define(END_Z, 0.0F);
        builder.define(LIFE, 6);
        builder.define(SEED, 0L);
    }

    public void setArc(Vec3 start, Vec3 end) {
        this.entityData.set(START_X, (float) start.x);
        this.entityData.set(START_Y, (float) start.y);
        this.entityData.set(START_Z, (float) start.z);
        this.entityData.set(END_X, (float) end.x);
        this.entityData.set(END_Y, (float) end.y);
        this.entityData.set(END_Z, (float) end.z);
        this.entityData.set(LIFE, 6);
        this.entityData.set(SEED, this.random.nextLong());

        this.setPos(start.x, start.y, start.z);
    }

    public Vec3 getStart() {
        return new Vec3(
                this.entityData.get(START_X),
                this.entityData.get(START_Y),
                this.entityData.get(START_Z)
        );
    }

    public Vec3 getEnd() {
        return new Vec3(
                this.entityData.get(END_X),
                this.entityData.get(END_Y),
                this.entityData.get(END_Z)
        );
    }

    public long getRenderSeed() {
        return this.entityData.get(SEED);
    }

    @Override
    public void tick() {
        super.tick();

        int life = this.entityData.get(LIFE) - 1;
        this.entityData.set(LIFE, life);

        if (life <= 0) {
            this.discard();
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 4096.0D;
    }
}