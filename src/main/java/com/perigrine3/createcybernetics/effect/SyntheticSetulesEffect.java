package com.perigrine3.createcybernetics.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SyntheticSetulesEffect extends MobEffect {

    private static final Map<UUID, Integer> WALL_GRACE_TICKS = new ConcurrentHashMap<>();
    private static final int WALL_GRACE = 8;

    private static final double CLIMB_SPEED = 0.22D;
    private static final double MAX_UP = 0.30D;

    private static final float INPUT_EPS = 0.01F;

    private static final double WALL_SAMPLE_DIST = 0.08D;

    public SyntheticSetulesEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!(entity instanceof Player player)) return true;

        UUID id = player.getUUID();

        if (player.horizontalCollision) {
            WALL_GRACE_TICKS.put(id, WALL_GRACE);
        } else {
            Integer t = WALL_GRACE_TICKS.get(id);
            if (t != null) {
                int next = t - 1;
                if (next <= 0) WALL_GRACE_TICKS.remove(id);
                else WALL_GRACE_TICKS.put(id, next);
            }
        }

        boolean shift = player.isShiftKeyDown();

        float zza = player.zza;
        float xxa = player.xxa;
        boolean hasMoveInput = Math.abs(zza) > INPUT_EPS || Math.abs(xxa) > INPUT_EPS;

        boolean onWallCollision = player.horizontalCollision || WALL_GRACE_TICKS.containsKey(id);
        boolean nearWall = onWallCollision || isNearWall(player);

        if (!nearWall) return true;

        Vec3 v = player.getDeltaMovement();

        // Holding ONLY shift while still next to a wall: hard-stick in place (no slide, no drop).
        if (shift && !hasMoveInput) {
            player.setDeltaMovement(0.0D, 0.0D, 0.0D);
            player.hurtMarked = true;
            return true;
        }

        // Preserve existing climb behavior: only climb when the collision says you're pushing into the wall.
        // (This avoids climbing when you're merely adjacent but not actively pressing into it.)
        if (hasMoveInput && onWallCollision) {
            double speed = CLIMB_SPEED + 0.03D * amplifier;
            double newY = Math.min(speed, MAX_UP);

            player.setDeltaMovement(v.x, newY, v.z);
            player.hurtMarked = true;
            return true;
        }

        return true;
    }

    private static boolean isNearWall(Player player) {
        Level level = player.level();

        AABB box = player.getBoundingBox();

        // Focus checks on the "body" slice so floors/ceilings don't count as walls.
        double y0 = box.minY + 0.10D;
        double y1 = box.maxY - 0.10D;
        if (y1 <= y0) return false;

        AABB body = new AABB(box.minX, y0, box.minZ, box.maxX, y1, box.maxZ);

        BlockPos base = player.blockPosition();

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            // Sample two heights (feet-ish and torso-ish) to catch partial blocks.
            if (hitsWallAt(level, body, base, dir, 0) || hitsWallAt(level, body, base, dir, 1)) {
                return true;
            }
        }

        return false;
    }

    private static boolean hitsWallAt(Level level, AABB body, BlockPos base, Direction dir, int yOff) {
        BlockPos pos = base.offset(dir.getStepX(), yOff, dir.getStepZ());
        var state = level.getBlockState(pos);
        if (state.isAir()) return false;

        VoxelShape shape = state.getCollisionShape(level, pos);
        if (shape.isEmpty()) return false;

        // Move the player's body box slightly toward the direction and see if it intersects the neighbor block's collision AABBs.
        AABB probe = body.move(dir.getStepX() * WALL_SAMPLE_DIST, 0.0D, dir.getStepZ() * WALL_SAMPLE_DIST);

        for (AABB aabb : shape.toAabbs()) {
            AABB worldAabb = aabb.move(pos.getX(), pos.getY(), pos.getZ());
            if (worldAabb.intersects(probe)) return true;
        }

        return false;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
