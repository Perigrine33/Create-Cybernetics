package com.perigrine3.createcybernetics.compat.corpse;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.lang.reflect.Method;
import java.util.UUID;

public record OpenCorpseCyberwarePayload(UUID corpseUUID) implements CustomPacketPayload {

    private static final String CORPSE_INVENTORY_CONTAINER_CLASS = "de.maxhenkel.corpse.gui.CorpseInventoryContainer";
    private static final String CORPSE_ADDITIONAL_CONTAINER_CLASS = "de.maxhenkel.corpse.gui.CorpseAdditionalContainer";
    private static final String CORPSE_ENTITY_CLASS = "de.maxhenkel.corpse.entities.CorpseEntity";

    private static volatile Class<?> corpseInventoryContainerClass;
    private static volatile boolean triedResolveCorpseInventoryContainerClass = false;

    private static volatile Class<?> corpseAdditionalContainerClass;
    private static volatile boolean triedResolveCorpseAdditionalContainerClass = false;

    private static volatile Class<?> corpseEntityClass;
    private static volatile boolean triedResolveCorpseEntityClass = false;

    private static volatile Method getCorpseMethod;
    private static volatile boolean triedResolveGetCorpseMethod = false;

    private static volatile Method isEditableMethod;
    private static volatile boolean triedResolveIsEditableMethod = false;

    public static final Type<OpenCorpseCyberwarePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(com.perigrine3.createcybernetics.CreateCybernetics.MODID, "open_corpse_cyberware"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenCorpseCyberwarePayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> buf.writeUUID(payload.corpseUUID()),
                    buf -> new OpenCorpseCyberwarePayload(buf.readUUID())
            );

    @Override
    public Type<OpenCorpseCyberwarePayload> type() {
        return TYPE;
    }

    public static void handle(OpenCorpseCyberwarePayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            Object corpseMenu = player.containerMenu;
            if (!isSupportedCorpseContainer(corpseMenu)) return;

            Object corpse = getCorpseFromMenu(corpseMenu);
            if (!(corpse instanceof net.minecraft.world.entity.Entity corpseEntity)) return;
            if (!isCorpseEntity(corpseEntity)) return;

            if (payload.corpseUUID() == null) return;
            if (!payload.corpseUUID().equals(corpseEntity.getUUID())) return;

            CorpseCompat.ensureCorpseCyberwareLoaded(corpseEntity);

            boolean editable = isEditable(corpseMenu);

            player.openMenu(
                    new SimpleMenuProvider(
                            (id, inv, p) -> new CorpseCyberwareMenu(
                                    ModCorpseCompatMenus.CORPSE_CYBERWARE.get(),
                                    id,
                                    inv,
                                    corpseEntity,
                                    editable
                            ),
                            Component.translatable("gui.createcybernetics.corpse_cyberware")
                    ),
                    buf -> {
                        buf.writeInt(corpseEntity.getId());
                        buf.writeBoolean(editable);
                    }
            );
        });
    }

    private static boolean isSupportedCorpseContainer(Object menu) {
        if (menu == null) return false;

        Class<?> invClass = getCorpseInventoryContainerClass();
        if (invClass != null && invClass.isInstance(menu)) {
            return true;
        }

        Class<?> additionalClass = getCorpseAdditionalContainerClass();
        return additionalClass != null && additionalClass.isInstance(menu);
    }

    private static boolean isCorpseEntity(Object entity) {
        if (entity == null) return false;

        Class<?> clazz = getCorpseEntityClass();
        return clazz != null && clazz.isInstance(entity);
    }

    private static Object getCorpseFromMenu(Object menu) {
        if (menu == null) return null;

        Method method = getGetCorpseMethod(menu.getClass());
        if (method == null) return null;

        try {
            return method.invoke(menu);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static boolean isEditable(Object menu) {
        if (menu == null) return false;

        Method method = getIsEditableMethod(menu.getClass());
        if (method == null) return false;

        try {
            Object result = method.invoke(menu);
            return result instanceof Boolean b && b;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static Class<?> getCorpseInventoryContainerClass() {
        if (triedResolveCorpseInventoryContainerClass) {
            return corpseInventoryContainerClass;
        }

        triedResolveCorpseInventoryContainerClass = true;

        try {
            corpseInventoryContainerClass = Class.forName(CORPSE_INVENTORY_CONTAINER_CLASS);
        } catch (Throwable ignored) {
            corpseInventoryContainerClass = null;
        }

        return corpseInventoryContainerClass;
    }

    private static Class<?> getCorpseAdditionalContainerClass() {
        if (triedResolveCorpseAdditionalContainerClass) {
            return corpseAdditionalContainerClass;
        }

        triedResolveCorpseAdditionalContainerClass = true;

        try {
            corpseAdditionalContainerClass = Class.forName(CORPSE_ADDITIONAL_CONTAINER_CLASS);
        } catch (Throwable ignored) {
            corpseAdditionalContainerClass = null;
        }

        return corpseAdditionalContainerClass;
    }

    private static Class<?> getCorpseEntityClass() {
        if (triedResolveCorpseEntityClass) {
            return corpseEntityClass;
        }

        triedResolveCorpseEntityClass = true;

        try {
            corpseEntityClass = Class.forName(CORPSE_ENTITY_CLASS);
        } catch (Throwable ignored) {
            corpseEntityClass = null;
        }

        return corpseEntityClass;
    }

    private static Method getGetCorpseMethod(Class<?> menuClass) {
        if (triedResolveGetCorpseMethod && getCorpseMethod != null && getCorpseMethod.getDeclaringClass().isAssignableFrom(menuClass)) {
            return getCorpseMethod;
        }

        try {
            Method method = menuClass.getMethod("getCorpse");
            getCorpseMethod = method;
            triedResolveGetCorpseMethod = true;
            return method;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Method getIsEditableMethod(Class<?> menuClass) {
        if (triedResolveIsEditableMethod && isEditableMethod != null && isEditableMethod.getDeclaringClass().isAssignableFrom(menuClass)) {
            return isEditableMethod;
        }

        try {
            Method method = menuClass.getMethod("isEditable");
            isEditableMethod = method;
            triedResolveIsEditableMethod = true;
            return method;
        } catch (Throwable ignored) {
            return null;
        }
    }
}