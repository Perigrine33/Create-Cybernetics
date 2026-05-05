package com.perigrine3.createcybernetics.entity;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.custom.*;
import com.perigrine3.createcybernetics.entity.projectile.EmpGrenadeProjectile;
import com.perigrine3.createcybernetics.entity.projectile.NuggetProjectile;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
    public static final Supplier<EntityType<NuggetProjectile>> NUGGET_PROJECTILE =
            ENTITY_TYPES.register("nugget_projectile", () -> EntityType.Builder.<NuggetProjectile>of(NuggetProjectile::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10).build("nugget_projectile"));
    public static final Supplier<EntityType<EmpGrenadeProjectile>> EMP_GRENADE_PROJECTILE =
            ENTITY_TYPES.register("emp_grenade_projectile", () -> EntityType.Builder.<EmpGrenadeProjectile>of(EmpGrenadeProjectile::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f).clientTrackingRange(8).updateInterval(10).build("emp_grenade_projectile"));

    public static final DeferredHolder<EntityType<?>, EntityType<RipperEntity>> RIPPER =
            ENTITY_TYPES.register("ripper", () -> EntityType.Builder.of(RipperEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.95F).clientTrackingRange(8).build("ripper"));
    public static final DeferredHolder<EntityType<?>, EntityType<TatHogEntity>> TATHOG =
            ENTITY_TYPES.register("tathog", () -> EntityType.Builder.of(TatHogEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.95F).clientTrackingRange(8).updateInterval(3)
                    .build(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "tathog").toString()));

    public static final Supplier<EntityType<SmasherEntity>> SMASHER =
            ENTITY_TYPES.register("smasher", () -> EntityType.Builder.of(SmasherEntity::new, MobCategory.MONSTER)
                    .sized(1.1F, 2.5F).build("smasher"));
    public static final Supplier<EntityType<CyberzombieEntity>> CYBERZOMBIE =
            ENTITY_TYPES.register("cyberzombie", () -> EntityType.Builder.of(CyberzombieEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.8F).build("cyberzombie"));
    public static final Supplier<EntityType<CyberskeletonEntity>> CYBERSKELETON =
            ENTITY_TYPES.register("cyberskeleton", () -> EntityType.Builder.of(CyberskeletonEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.8F).build("cyberskeleton"));

    public static final Supplier<EntityType<HogBoyEntity>> HOGBOY =
            ENTITY_TYPES.register("hogboy", () -> EntityType.Builder.of(HogBoyEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).clientTrackingRange(8).build("hogboy"));
    public static final Supplier<EntityType<PunklinEntity>> PUNKLIN =
            ENTITY_TYPES.register("punklin", () -> EntityType.Builder.of(PunklinEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).clientTrackingRange(8).build("punklin"));
    public static final Supplier<EntityType<PigstromEntity>> PIGSTROM =
            ENTITY_TYPES.register("pigstrom", () -> EntityType.Builder.of(PigstromEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).clientTrackingRange(8).build("pigstrom"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}