package com.perigrine3.createcybernetics.entity;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.custom.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, CreateCybernetics.MODID);



    public static final Supplier<EntityType<SkinstackEntity>> SKINSTACK =
            ENTITY_TYPES.register("skinstack", () -> EntityType.Builder.of(SkinstackEntity::new, MobCategory.MISC)
                    .sized(0F, 0F).build("skinstack"));

    public static final Supplier<EntityType<SmasherEntity>> SMASHER =
            ENTITY_TYPES.register("smasher", () -> EntityType.Builder.of(SmasherEntity::new, MobCategory.MONSTER)
                    .sized(1.1F, 2.5F).build("smasher"));
    public static final Supplier<EntityType<CyberzombieEntity>> CYBERZOMBIE =
            ENTITY_TYPES.register("cyberzombie", () -> EntityType.Builder.of(CyberzombieEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.8F).build("cyberzombie"));



    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
