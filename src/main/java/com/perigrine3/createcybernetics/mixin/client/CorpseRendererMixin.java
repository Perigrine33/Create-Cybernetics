package com.perigrine3.createcybernetics.mixin.client;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.perigrine3.createcybernetics.compat.corpse.CorpseVisualSnapshotClientCache;
import com.perigrine3.createcybernetics.compat.corpse.CorpseVisualSnapshotRequestClientCache;
import com.perigrine3.createcybernetics.compat.corpse.RequestCorpseVisualSnapshotPayload;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Pseudo
@Mixin(targets = "de.maxhenkel.corpse.entities.CorpseRenderer")
public abstract class CorpseRendererMixin {

    @Unique
    private static final String CORPSE_RENDERER_CLASS = "de.maxhenkel.corpse.entities.CorpseRenderer";
    @Unique
    private static final String CORPSE_ENTITY_CLASS = "de.maxhenkel.corpse.entities.CorpseEntity";
    @Unique
    private static final String DUMMY_PLAYER_CLASS = "de.maxhenkel.corpse.entities.DummyPlayer";

    @Unique private static Field createcybernetics$playersField;
    @Unique private static boolean createcybernetics$playersFieldResolved = false;
    @Unique private static Method createcybernetics$cachedMapGetMethod;
    @Unique private static boolean createcybernetics$cachedMapGetMethodResolved = false;
    @Unique private static Method createcybernetics$isSkeletonMethod;
    @Unique private static boolean createcybernetics$isSkeletonMethodResolved = false;
    @Unique private static Method createcybernetics$getCorpseUuidMethod;
    @Unique private static boolean createcybernetics$getCorpseUuidMethodResolved = false;
    @Unique private static Method createcybernetics$getCorpseNameMethod;
    @Unique private static boolean createcybernetics$getCorpseNameMethodResolved = false;
    @Unique private static Method createcybernetics$getEquipmentMethod;
    @Unique private static boolean createcybernetics$getEquipmentMethodResolved = false;
    @Unique private static Method createcybernetics$getCorpseModelMethod;
    @Unique private static boolean createcybernetics$getCorpseModelMethodResolved = false;
    @Unique private static Constructor<?> createcybernetics$dummyPlayerConstructor;
    @Unique private static boolean createcybernetics$dummyPlayerConstructorResolved = false;

    @Dynamic
    @Inject(method = "render", at = @At("HEAD"))
    private void createcybernetics$applyCorpseVisualSnapshot(
            @Coerce Object entity,
            float entityYaw,
            float partialTicks,
            PoseStack matrixStack,
            MultiBufferSource buffer,
            int packedLightIn,
            CallbackInfo ci
    ) {
        if (!(entity instanceof Entity mcEntity)) {
            return;
        }

        if (!createcybernetics$isCorpseEntity(entity) || createcybernetics$isSkeleton(entity)) {
            return;
        }

        CompoundTag cached = CorpseVisualSnapshotClientCache.get(mcEntity.getUUID());
        if (cached.isEmpty()) {
            if (CorpseVisualSnapshotRequestClientCache.markRequested(mcEntity.getUUID())) {
                PacketDistributor.sendToServer(new RequestCorpseVisualSnapshotPayload(mcEntity.getUUID()));
            }
        }

        AbstractClientPlayer visualPlayer = createcybernetics$getOrCreateDummyPlayer(entity, mcEntity);
        if (visualPlayer == null) {
            return;
        }

        CorpseVisualSnapshotClientCache.applyToPlayer(visualPlayer, mcEntity.getUUID());
    }

    @Unique
    private AbstractClientPlayer createcybernetics$getOrCreateDummyPlayer(Object corpseEntity, Entity mcEntity) {
        Object players = createcybernetics$getPlayersFieldValue();
        if (players == null) {
            return null;
        }

        Method getMethod = createcybernetics$getCachedMapGetMethod(players.getClass());
        if (getMethod == null) {
            return null;
        }

        try {
            Object value = getMethod.invoke(players, corpseEntity, (Supplier<Object>) () -> createcybernetics$createDummyPlayer(corpseEntity, mcEntity));
            return value instanceof AbstractClientPlayer player ? player : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Unique
    private Object createcybernetics$createDummyPlayer(Object corpseEntity, Entity mcEntity) {
        Constructor<?> ctor = createcybernetics$getDummyPlayerConstructor();
        if (ctor == null) {
            return null;
        }

        try {
            UUID corpseUuid = createcybernetics$getCorpseOwnerUuid(corpseEntity).orElse(new UUID(0L, 0L));
            String corpseName = createcybernetics$getCorpseName(corpseEntity);
            Object equipment = createcybernetics$getEquipment(corpseEntity);
            Object corpseModel = createcybernetics$getCorpseModel(corpseEntity);

            return ctor.newInstance(
                    (ClientLevel) mcEntity.level(),
                    new GameProfile(corpseUuid, corpseName),
                    equipment,
                    corpseModel
            );
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Unique
    private Object createcybernetics$getPlayersFieldValue() {
        Field field = createcybernetics$getPlayersField();
        if (field == null) {
            return null;
        }

        try {
            return field.get(this);
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Unique
    private static boolean createcybernetics$isCorpseEntity(Object entity) {
        if (!(entity instanceof Entity)) {
            return false;
        }

        try {
            Class<?> corpseClass = Class.forName(CORPSE_ENTITY_CLASS);
            return corpseClass.isInstance(entity);
        } catch (Throwable ignored) {
            return false;
        }
    }

    @Unique
    private static boolean createcybernetics$isSkeleton(Object corpseEntity) {
        Method method = createcybernetics$getIsSkeletonMethod(corpseEntity.getClass());
        if (method == null) {
            return false;
        }

        try {
            Object result = method.invoke(corpseEntity);
            return result instanceof Boolean b && b;
        } catch (Throwable ignored) {
            return false;
        }
    }

    @Unique
    private static Optional<UUID> createcybernetics$getCorpseOwnerUuid(Object corpseEntity) {
        Method method = createcybernetics$getCorpseUuidMethod(corpseEntity.getClass());
        if (method == null) {
            return Optional.empty();
        }

        try {
            Object result = method.invoke(corpseEntity);
            if (result instanceof Optional<?> optional) {
                Object value = optional.orElse(null);
                if (value instanceof UUID uuid) {
                    return Optional.of(uuid);
                }
            }
        } catch (Throwable ignored) {
        }

        return Optional.empty();
    }

    @Unique
    private static String createcybernetics$getCorpseName(Object corpseEntity) {
        Method method = createcybernetics$getCorpseNameMethod(corpseEntity.getClass());
        if (method == null) {
            return "";
        }

        try {
            Object result = method.invoke(corpseEntity);
            return result instanceof String s ? s : "";
        } catch (Throwable ignored) {
            return "";
        }
    }

    @Unique
    private static Object createcybernetics$getEquipment(Object corpseEntity) {
        Method method = createcybernetics$getEquipmentMethod(corpseEntity.getClass());
        if (method == null) {
            return null;
        }

        try {
            return method.invoke(corpseEntity);
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Unique
    private static Object createcybernetics$getCorpseModel(Object corpseEntity) {
        Method method = createcybernetics$getCorpseModelMethod(corpseEntity.getClass());
        if (method == null) {
            return null;
        }

        try {
            return method.invoke(corpseEntity);
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Unique
    private static Field createcybernetics$getPlayersField() {
        if (createcybernetics$playersFieldResolved) {
            return createcybernetics$playersField;
        }

        createcybernetics$playersFieldResolved = true;

        try {
            Class<?> rendererClass = Class.forName(CORPSE_RENDERER_CLASS);
            createcybernetics$playersField = rendererClass.getDeclaredField("players");
            createcybernetics$playersField.setAccessible(true);
        } catch (Throwable ignored) {
            createcybernetics$playersField = null;
        }

        return createcybernetics$playersField;
    }

    @Unique
    private static Method createcybernetics$getCachedMapGetMethod(Class<?> cachedMapClass) {
        if (createcybernetics$cachedMapGetMethodResolved) {
            return createcybernetics$cachedMapGetMethod;
        }

        createcybernetics$cachedMapGetMethodResolved = true;

        try {
            createcybernetics$cachedMapGetMethod = cachedMapClass.getMethod("get", Object.class, Supplier.class);
        } catch (Throwable ignored) {
            createcybernetics$cachedMapGetMethod = null;
        }

        return createcybernetics$cachedMapGetMethod;
    }

    @Unique
    private static Method createcybernetics$getIsSkeletonMethod(Class<?> corpseClass) {
        if (createcybernetics$isSkeletonMethodResolved) {
            return createcybernetics$isSkeletonMethod;
        }

        createcybernetics$isSkeletonMethodResolved = true;

        try {
            createcybernetics$isSkeletonMethod = corpseClass.getMethod("isSkeleton");
        } catch (Throwable ignored) {
            createcybernetics$isSkeletonMethod = null;
        }

        return createcybernetics$isSkeletonMethod;
    }

    @Unique
    private static Method createcybernetics$getCorpseUuidMethod(Class<?> corpseClass) {
        if (createcybernetics$getCorpseUuidMethodResolved) {
            return createcybernetics$getCorpseUuidMethod;
        }

        createcybernetics$getCorpseUuidMethodResolved = true;

        try {
            createcybernetics$getCorpseUuidMethod = corpseClass.getMethod("getCorpseUUID");
        } catch (Throwable ignored) {
            createcybernetics$getCorpseUuidMethod = null;
        }

        return createcybernetics$getCorpseUuidMethod;
    }

    @Unique
    private static Method createcybernetics$getCorpseNameMethod(Class<?> corpseClass) {
        if (createcybernetics$getCorpseNameMethodResolved) {
            return createcybernetics$getCorpseNameMethod;
        }

        createcybernetics$getCorpseNameMethodResolved = true;

        try {
            createcybernetics$getCorpseNameMethod = corpseClass.getMethod("getCorpseName");
        } catch (Throwable ignored) {
            createcybernetics$getCorpseNameMethod = null;
        }

        return createcybernetics$getCorpseNameMethod;
    }

    @Unique
    private static Method createcybernetics$getEquipmentMethod(Class<?> corpseClass) {
        if (createcybernetics$getEquipmentMethodResolved) {
            return createcybernetics$getEquipmentMethod;
        }

        createcybernetics$getEquipmentMethodResolved = true;

        try {
            createcybernetics$getEquipmentMethod = corpseClass.getMethod("getEquipment");
        } catch (Throwable ignored) {
            createcybernetics$getEquipmentMethod = null;
        }

        return createcybernetics$getEquipmentMethod;
    }

    @Unique
    private static Method createcybernetics$getCorpseModelMethod(Class<?> corpseClass) {
        if (createcybernetics$getCorpseModelMethodResolved) {
            return createcybernetics$getCorpseModelMethod;
        }

        createcybernetics$getCorpseModelMethodResolved = true;

        try {
            createcybernetics$getCorpseModelMethod = corpseClass.getMethod("getCorpseModel");
        } catch (Throwable ignored) {
            createcybernetics$getCorpseModelMethod = null;
        }

        return createcybernetics$getCorpseModelMethod;
    }

    @Unique
    private static Constructor<?> createcybernetics$getDummyPlayerConstructor() {
        if (createcybernetics$dummyPlayerConstructorResolved) {
            return createcybernetics$dummyPlayerConstructor;
        }

        createcybernetics$dummyPlayerConstructorResolved = true;

        try {
            Class<?> dummyPlayerClass = Class.forName(DUMMY_PLAYER_CLASS);

            for (Constructor<?> ctor : dummyPlayerClass.getConstructors()) {
                Class<?>[] params = ctor.getParameterTypes();
                if (params.length == 4
                        && ClientLevel.class.isAssignableFrom(params[0])
                        && GameProfile.class.isAssignableFrom(params[1])) {
                    createcybernetics$dummyPlayerConstructor = ctor;
                    break;
                }
            }
        } catch (Throwable ignored) {
            createcybernetics$dummyPlayerConstructor = null;
        }

        return createcybernetics$dummyPlayerConstructor;
    }
}