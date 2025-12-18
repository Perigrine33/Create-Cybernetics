package com.perigrine3.createcybernetics.mixin;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.ModEntities;
import com.perigrine3.createcybernetics.entity.custom.SmasherEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.raid.Raid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raid.class)
public abstract class RaidMixin {

    @Unique
    private boolean createcybernetics$spawnedSmasher = false;

    @Shadow public abstract int getBadOmenLevel();

    @Inject(method = "spawnGroup", at = @At("TAIL"))
    private void createcybernetics$maybeSpawnSmasher(ServerLevel level, BlockPos pos, int wave, CallbackInfo ci) {
        if (createcybernetics$spawnedSmasher) return;

        // Don’t spawn too early
        if (wave < 3) return;

        int omen = Math.max(1, this.getBadOmenLevel());
        float chance = createcybernetics$computeSmasherChance(level.getDifficulty(), omen, wave);

        if (level.getRandom().nextFloat() >= chance) return;

        BlockPos spawnPos = pos;

        SmasherEntity smasher = ModEntities.SMASHER.get().create(level);
        if (smasher == null) return;
        smasher.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, level.getRandom().nextFloat() * 360.0F, 0.0F);
        smasher.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.EVENT, null);
        level.addFreshEntity(smasher);
        ((Raid)(Object)this).addWaveMob(wave, smasher, false);

        createcybernetics$spawnedSmasher = true;
    }

    @Unique
    private static float createcybernetics$computeSmasherChance(Difficulty difficulty, int omenLevel, int wave) {
        float chance = 0.08f;

        chance += 0.10f * (wave - 2);
        chance += 0.07f * (omenLevel - 1);
        chance += switch (difficulty) {
            case PEACEFUL -> -1.0f;
            case EASY -> -0.01f;
            case NORMAL -> 0.00f;
            case HARD -> 0.5f;
        };

        return Mth.clamp(chance, 0.0f, 0.85f);
    }
}
