package com.perigrine3.createcybernetics.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class NuggetProjectile extends ThrowableItemProjectile {

    private static final float DAMAGE = 6.0f;

    public NuggetProjectile(EntityType<? extends NuggetProjectile> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public NuggetProjectile(EntityType<? extends NuggetProjectile> type, Level level, LivingEntity owner, ItemStack ammo) {
        super(type, owner, level);
        setAmmo(ammo);
        this.setNoGravity(true);
    }

    public void setAmmo(ItemStack ammo) {
        if (ammo == null || ammo.isEmpty()) {
            this.setItem(ItemStack.EMPTY);
            return;
        }

        ItemStack one = ammo.copy();
        one.setCount(1);
        this.setItem(one);
    }

    public ItemStack getAmmo() {
        ItemStack st = this.getItem();
        return st == null ? ItemStack.EMPTY : st;
    }

    @Override
    protected Item getDefaultItem() {
        return Items.IRON_NUGGET;
    }

    @Override
    public void tick() {
        // Ensure gravity stays off (since getGravity() is final in your version).
        this.setNoGravity(true);

        super.tick();

        // Keep render rotation aligned to motion to reduce "flies at weird angle" visuals.
        var v = this.getDeltaMovement();
        double dx = v.x;
        double dy = v.y;
        double dz = v.z;
        double h = Math.sqrt(dx * dx + dz * dz);

        if (h > 1.0e-6) {
            float yaw = (float)(Mth.atan2(dx, dz) * (180.0 / Math.PI));
            float pitch = (float)(Mth.atan2(dy, h) * (180.0 / Math.PI));
            this.setYRot(yaw);
            this.setXRot(pitch);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        if (this.level().isClientSide) return;

        Entity target = result.getEntity();
        Entity owner = this.getOwner();

        target.hurt(this.damageSources().thrown(this, owner), DAMAGE);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide) {
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(
                        ParticleTypes.CRIT,
                        this.getX(), this.getY(), this.getZ(),
                        4,
                        0.05, 0.05, 0.05,
                        0.02
                );
            }

            this.discard();
        }
    }
}
