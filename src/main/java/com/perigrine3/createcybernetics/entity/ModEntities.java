package com.perigrine3.createcybernetics.entity;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.custom.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, CreateCybernetics.MODID);



    public static final Supplier<EntityType<GuardianBeamEntity>> GUARDIAN_BEAM =
            ENTITY_TYPES.register("guardian_beam", () -> EntityType.Builder.<GuardianBeamEntity>of(GuardianBeamEntity::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F).clientTrackingRange(64).updateInterval(1).build("guardian_beam"));



    public static final Supplier<EntityType<SmasherEntity>> SMASHER =
            ENTITY_TYPES.register("smasher", () -> EntityType.Builder.of(SmasherEntity::new, MobCategory.MONSTER)
                    .sized(1.1F, 2.5F).build("smasher"));

    public static final Supplier<EntityType<CyberzombieEntity>> CYBERZOMBIE =
            ENTITY_TYPES.register("cyberzombie", () -> EntityType.Builder.of(CyberzombieEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.8F).build("cyberzombie"));

    public static final Supplier<EntityType<CyberskeletonEntity>> CYBERSKELETON =
            ENTITY_TYPES.register("cyberskeleton", () -> EntityType.Builder.of(CyberskeletonEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.8F).build("cyberskeleton"));



    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
