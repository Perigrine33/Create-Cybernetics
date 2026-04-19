package com.perigrine3.createcybernetics.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.compat.corpse.CorpseVisualSnapshotClientCache;
import com.perigrine3.createcybernetics.compat.corpse.SkeletonCorpseOverlayRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Pseudo
@Mixin(targets = "de.maxhenkel.corpse.entities.CorpseRenderer")
public abstract class CorpseRendererSkeletonOverlayMixin {

    @Unique
    private static final String CORPSE_RENDERER_CLASS = "de.maxhenkel.corpse.entities.CorpseRenderer";
    @Unique
    private static final String CORPSE_ENTITY_CLASS = "de.maxhenkel.corpse.entities.CorpseEntity";
    @Unique
    private static final String MAIN_CLASS = "de.maxhenkel.corpse.Main";

    @Unique
    private static Field createcybernetics$skeletonsField;
    @Unique
    private static boolean createcybernetics$skeletonsFieldResolved = false;

    @Unique
    private static Method createcybernetics$cachedMapGetMethod;
    @Unique
    private static boolean createcybernetics$cachedMapGetMethodResolved = false;

    @Unique
    private static Method createcybernetics$isSkeletonMethod;
    @Unique
    private static boolean createcybernetics$isSkeletonMethodResolved = false;

    @Unique
    private static Method createcybernetics$getCorpseUuidMethod;
    @Unique
    private static boolean createcybernetics$getCorpseUuidMethodResolved = false;

    @Unique
    private static Method createcybernetics$getYRotMethod;
    @Unique
    private static boolean createcybernetics$getYRotMethodResolved = false;

    @Unique
    private static Field createcybernetics$mainServerConfigField;
    @Unique
    private static boolean createcybernetics$mainServerConfigFieldResolved = false;

    @Unique
    private static Field createcybernetics$spawnCorpseOnFaceField;
    @Unique
    private static boolean createcybernetics$spawnCorpseOnFaceFieldResolved = false;

    @Unique
    private static Method createcybernetics$configGetMethod;
    @Unique
    private static boolean createcybernetics$configGetMethodResolved = false;

    @Dynamic
    @Inject(method = "render", at = @At("TAIL"))
    private void createcybernetics$renderSkeletonOverlays(
            @Coerce Object entity,
            float entityYaw,
            float partialTicks,
            PoseStack matrixStack,
            MultiBufferSource buffer,
            int packedLightIn,
            CallbackInfo ci
    ) {
        if (!(entity instanceof Entity corpseEntity)) return;
        if (!createcybernetics$isCorpseEntity(entity)) return;
        if (!createcybernetics$isSkeleton(entity)) return;

        CompoundTag snapshot = CorpseVisualSnapshotClientCache.get(corpseEntity.getUUID());
        if (snapshot.isEmpty()) return;

        PlayerCyberwareData data = PlayerCyberwareData.fromSnapshotTag(snapshot, corpseEntity.registryAccess());
        if (data == null) return;

        Object dummySkeleton = createcybernetics$getDummySkeleton(entity);
        if (!(dummySkeleton instanceof AbstractSkeleton skeleton)) return;

        EntityRenderer<? super AbstractSkeleton> renderer =
                Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(skeleton);
        if (!(renderer instanceof LivingEntityRenderer<?, ?> livingRenderer)) return;
        if (!(livingRenderer instanceof LivingEntityRendererAccessor<?, ?> accessor)) return;

        EntityModel<?> rawModel = accessor.createcybernetics$getModel();
        if (!(rawModel instanceof SkeletonModel<?> skeletonModel)) return;

        @SuppressWarnings("unchecked")
        SkeletonModel<AbstractSkeleton> castModel = (SkeletonModel<AbstractSkeleton>) skeletonModel;

        matrixStack.pushPose();
        createcybernetics$applyCorpseTransforms(entity, matrixStack);
        matrixStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        matrixStack.translate(0.0D, -1.501D, 0.0D);
        matrixStack.scale(1.001F, 1.001F, 1.001F);
        SkeletonCorpseOverlayRenderer.render(castModel, data, matrixStack, buffer, packedLightIn);
        matrixStack.popPose();
    }

    @Unique
    private Object createcybernetics$getDummySkeleton(Object corpseEntity) {
        Object skeletons = createcybernetics$getSkeletonsFieldValue();
        if (skeletons == null) return null;

        Method getMethod = createcybernetics$getCachedMapGetMethod(skeletons.getClass());
        if (getMethod == null) return null;

        try {
            return getMethod.invoke(skeletons, corpseEntity, (Supplier<Object>) () -> null);
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Unique
    private Object createcybernetics$getSkeletonsFieldValue() {
        Field field = createcybernetics$getSkeletonsField();
        if (field == null) return null;

        try {
            return field.get(this);
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Unique
    private static void createcybernetics$applyCorpseTransforms(Object corpseEntity, PoseStack matrixStack) {
        float yRot = createcybernetics$getYRot(corpseEntity);
        matrixStack.mulPose(Axis.YP.rotationDegrees(-yRot));

        if (createcybernetics$spawnCorpseOnFace()) {
            matrixStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            matrixStack.translate(0.0D, -1.0D, -0.125625D);
        } else {
            matrixStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            matrixStack.translate(0.0D, -1.0D, 0.125625D);
        }
    }

    @Unique
    private static boolean createcybernetics$isCorpseEntity(Object entity) {
        if (!(entity instanceof Entity)) return false;

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
        if (method == null) return false;

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
        if (method == null) return Optional.empty();

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
    private static float createcybernetics$getYRot(Object corpseEntity) {
        Method method = createcybernetics$getYRotMethod(corpseEntity.getClass());
        if (method == null) return 0.0F;

        try {
            Object result = method.invoke(corpseEntity);
            return result instanceof Float f ? f : 0.0F;
        } catch (Throwable ignored) {
            return 0.0F;
        }
    }

    @Unique
    private static boolean createcybernetics$spawnCorpseOnFace() {
        Field mainField = createcybernetics$getMainServerConfigField();
        if (mainField == null) return false;

        try {
            Object serverConfig = mainField.get(null);
            if (serverConfig == null) return false;

            Field spawnField = createcybernetics$getSpawnCorpseOnFaceField(serverConfig.getClass());
            if (spawnField == null) return false;

            Object configValue = spawnField.get(serverConfig);
            if (configValue == null) return false;

            Method getMethod = createcybernetics$getConfigGetMethod(configValue.getClass());
            if (getMethod == null) return false;

            Object result = getMethod.invoke(configValue);
            return result instanceof Boolean b && b;
        } catch (Throwable ignored) {
            return false;
        }
    }

    @Unique
    private static Field createcybernetics$getSkeletonsField() {
        if (createcybernetics$skeletonsFieldResolved) {
            return createcybernetics$skeletonsField;
        }

        createcybernetics$skeletonsFieldResolved = true;

        try {
            Class<?> rendererClass = Class.forName(CORPSE_RENDERER_CLASS);
            createcybernetics$skeletonsField = rendererClass.getDeclaredField("skeletons");
            createcybernetics$skeletonsField.setAccessible(true);
        } catch (Throwable ignored) {
            createcybernetics$skeletonsField = null;
        }

        return createcybernetics$skeletonsField;
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
    private static Method createcybernetics$getYRotMethod(Class<?> corpseClass) {
        if (createcybernetics$getYRotMethodResolved) {
            return createcybernetics$getYRotMethod;
        }

        createcybernetics$getYRotMethodResolved = true;

        try {
            createcybernetics$getYRotMethod = corpseClass.getMethod("getYRot");
        } catch (Throwable ignored) {
            createcybernetics$getYRotMethod = null;
        }

        return createcybernetics$getYRotMethod;
    }

    @Unique
    private static Field createcybernetics$getMainServerConfigField() {
        if (createcybernetics$mainServerConfigFieldResolved) {
            return createcybernetics$mainServerConfigField;
        }

        createcybernetics$mainServerConfigFieldResolved = true;

        try {
            Class<?> mainClass = Class.forName(MAIN_CLASS);
            createcybernetics$mainServerConfigField = mainClass.getDeclaredField("SERVER_CONFIG");
            createcybernetics$mainServerConfigField.setAccessible(true);
        } catch (Throwable ignored) {
            createcybernetics$mainServerConfigField = null;
        }

        return createcybernetics$mainServerConfigField;
    }

    @Unique
    private static Field createcybernetics$getSpawnCorpseOnFaceField(Class<?> serverConfigClass) {
        if (createcybernetics$spawnCorpseOnFaceFieldResolved) {
            return createcybernetics$spawnCorpseOnFaceField;
        }

        createcybernetics$spawnCorpseOnFaceFieldResolved = true;

        try {
            createcybernetics$spawnCorpseOnFaceField = serverConfigClass.getDeclaredField("spawnCorpseOnFace");
            createcybernetics$spawnCorpseOnFaceField.setAccessible(true);
        } catch (Throwable ignored) {
            createcybernetics$spawnCorpseOnFaceField = null;
        }

        return createcybernetics$spawnCorpseOnFaceField;
    }

    @Unique
    private static Method createcybernetics$getConfigGetMethod(Class<?> configValueClass) {
        if (createcybernetics$configGetMethodResolved) {
            return createcybernetics$configGetMethod;
        }

        createcybernetics$configGetMethodResolved = true;

        try {
            createcybernetics$configGetMethod = configValueClass.getMethod("get");
        } catch (Throwable ignored) {
            createcybernetics$configGetMethod = null;
        }

        return createcybernetics$configGetMethod;
    }
}