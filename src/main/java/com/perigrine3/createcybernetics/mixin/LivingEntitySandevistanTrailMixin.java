package com.perigrine3.createcybernetics.mixin;

import com.perigrine3.createcybernetics.api.ISandevistanTrailState;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntitySandevistanTrailMixin implements ISandevistanTrailState {

    @Unique
    private static final EntityDataAccessor<Boolean> CREATECYBERNETICS_SANDE_TRAIL =
            SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.BOOLEAN);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void createcybernetics$defineTrailData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(CREATECYBERNETICS_SANDE_TRAIL, false);
    }

    @Override
    public boolean createcybernetics$isSandevistanTrailActive() {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Player) return false;
        return self.getEntityData().get(CREATECYBERNETICS_SANDE_TRAIL);
    }

    @Override
    public void createcybernetics$setSandevistanTrailActive(boolean active) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Player) return;
        self.getEntityData().set(CREATECYBERNETICS_SANDE_TRAIL, active);
    }
}