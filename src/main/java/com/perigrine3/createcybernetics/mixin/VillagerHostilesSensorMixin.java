
package com.perigrine3.createcybernetics.mixin;

import com.google.common.collect.ImmutableMap;
import com.perigrine3.createcybernetics.entity.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.sensing.VillagerHostilesSensor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

    @Mixin(VillagerHostilesSensor.class)
    public class VillagerHostilesSensorMixin {

        @Shadow @Final @Mutable
        private static ImmutableMap<EntityType<?>, Float> ACCEPTABLE_DISTANCE_FROM_HOSTILES;

        @Inject(method = "<clinit>", at = @At("TAIL"))
        private static void createcybernetics$addCustomHostiles(CallbackInfo ci) {
            var builder = ImmutableMap.<EntityType<?>, Float>builder();
            builder.putAll(ACCEPTABLE_DISTANCE_FROM_HOSTILES);

            builder.put(ModEntities.SMASHER.get(), 20.0F);
            builder.put(ModEntities.CYBERZOMBIE.get(), 12.0F);
            builder.put(ModEntities.CYBERSKELETON.get(), 12.0F);

            ACCEPTABLE_DISTANCE_FROM_HOSTILES = builder.build();
        }
    }
