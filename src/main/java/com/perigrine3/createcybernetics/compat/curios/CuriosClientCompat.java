package com.perigrine3.createcybernetics.compat.curios;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT)
public final class CuriosClientCompat {

    private static final String CURIOS_MODID = "curios";

    private static boolean pendingCosmeticToggle = false;
    private static int pendingCosmeticToggleTicks = 0;

    private CuriosClientCompat() {}

    public static boolean isLoaded() {
        return ModList.get().isLoaded(CURIOS_MODID);
    }

    public static int buttonOffsetX() {
        Object tuple = getCuriosButtonOffset();
        Integer x = readTupleInt(tuple, "getA");
        return x != null ? x : 28;
    }

    public static int buttonOffsetY() {
        Object tuple = getCuriosButtonOffset();
        Integer y = readTupleInt(tuple, "getB");
        return y != null ? y : -74;
    }

    public static int curiosButtonX(AbstractContainerScreen<?> parent) {
        return parent.getGuiLeft() + buttonOffsetX() + 2;
    }

    public static int curiosButtonY(AbstractContainerScreen<?> parent) {
        return parent.getGuiTop() + buttonOffsetY() + 85;
    }

    public static AbstractWidget createNativeCuriosButton(AbstractContainerScreen<?> parent) {
        if (!isLoaded() || parent == null) return null;

        try {
            Class<?> curiosButtonClass = Class.forName("top.theillusivec4.curios.client.gui.CuriosButton");

            Object sprites = readStaticField(curiosButtonClass, "BIG");
            if (sprites == null) return null;

            int x = curiosButtonX(parent);
            int y = curiosButtonY(parent);

            for (Constructor<?> ctor : curiosButtonClass.getDeclaredConstructors()) {
                Class<?>[] params = ctor.getParameterTypes();

                if (params.length != 6) continue;
                if (!params[0].isAssignableFrom(parent.getClass()) && !params[0].isAssignableFrom(AbstractContainerScreen.class)) continue;
                if (params[1] != int.class) continue;
                if (params[2] != int.class) continue;
                if (params[3] != int.class) continue;
                if (params[4] != int.class) continue;
                if (!"net.minecraft.client.gui.components.WidgetSprites".equals(params[5].getName())) continue;

                ctor.setAccessible(true);

                Object obj = ctor.newInstance(parent, x, y, 10, 10, sprites);
                if (obj instanceof AbstractWidget widget) {
                    return widget;
                }
            }
        } catch (Throwable ignored) {
        }

        return null;
    }

    public static void pressCuriosButton(AbstractWidget curiosButton) {
        if (curiosButton == null) return;
        curiosButton.onClick(curiosButton.getX() + 1.0D, curiosButton.getY() + 1.0D);
    }

    public static void openCuriosCosmetics(AbstractWidget curiosButton) {
        if (!isLoaded()) return;

        pendingCosmeticToggle = true;
        pendingCosmeticToggleTicks = 40;

        pressCuriosButton(curiosButton);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (!pendingCosmeticToggle) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            clearPendingCosmeticToggle();
            return;
        }

        if (pendingCosmeticToggleTicks-- <= 0) {
            clearPendingCosmeticToggle();
            return;
        }

        Screen screen = mc.screen;
        if (screen == null) return;

        if (!"top.theillusivec4.curios.client.gui.CuriosScreen".equals(screen.getClass().getName())) {
            return;
        }

        if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) {
            clearPendingCosmeticToggle();
            return;
        }

        AbstractContainerMenu menu = containerScreen.getMenu();
        if (menu == null) {
            clearPendingCosmeticToggle();
            return;
        }

        if (!"top.theillusivec4.curios.common.inventory.container.CuriosContainer".equals(menu.getClass().getName())) {
            clearPendingCosmeticToggle();
            return;
        }

        forceCosmeticsAvailable(menu);
        refreshCuriosScreenButtons(screen);
        tryToggleCosmetics(menu);
        refreshCuriosScreenButtons(screen);

        clearPendingCosmeticToggle();
    }

    private static void forceCosmeticsAvailable(AbstractContainerMenu menu) {
        setBooleanField(menu, "hasCosmetics", true);
    }

    private static void refreshCuriosScreenButtons(Screen screen) {
        if (screen == null) return;

        try {
            Method method = screen.getClass().getMethod("updateRenderButtons");
            method.setAccessible(true);
            method.invoke(screen);
        } catch (Throwable ignored) {
        }
    }

    private static void tryToggleCosmetics(AbstractContainerMenu menu) {
        try {
            Boolean viewingCosmetics = readBooleanField(menu, "isViewingCosmetics");
            if (Boolean.TRUE.equals(viewingCosmetics)) {
                return;
            }

            Method toggleCosmetics = menu.getClass().getMethod("toggleCosmetics");
            toggleCosmetics.setAccessible(true);
            toggleCosmetics.invoke(menu);

            sendToggleCosmeticsPacket(menu.containerId);
        } catch (Throwable ignored) {
        }
    }

    private static void sendToggleCosmeticsPacket(int containerId) {
        try {
            Class<?> packetClass = Class.forName("top.theillusivec4.curios.common.network.client.CPacketToggleCosmetics");
            Constructor<?> ctor = packetClass.getConstructor(int.class);
            Object payload = ctor.newInstance(containerId);

            if (payload instanceof CustomPacketPayload packetPayload) {
                PacketDistributor.sendToServer(packetPayload);
            }
        } catch (Throwable ignored) {
        }
    }

    private static void clearPendingCosmeticToggle() {
        pendingCosmeticToggle = false;
        pendingCosmeticToggleTicks = 0;
    }

    private static Object getCuriosButtonOffset() {
        if (!isLoaded()) return null;

        try {
            Class<?> curiosScreen = Class.forName("top.theillusivec4.curios.client.gui.CuriosScreen");
            Method method = curiosScreen.getMethod("getButtonOffset", boolean.class);
            return method.invoke(null, false);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Integer readTupleInt(Object tuple, String methodName) {
        if (tuple == null) return null;

        try {
            Method method = tuple.getClass().getMethod(methodName);
            Object out = method.invoke(tuple);
            return out instanceof Integer i ? i : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Object readStaticField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(null);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Boolean readBooleanField(Object target, String name) {
        if (target == null) return null;

        try {
            Field field = target.getClass().getField(name);
            field.setAccessible(true);
            Object value = field.get(target);
            return value instanceof Boolean b ? b : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static void setBooleanField(Object target, String name, boolean value) {
        if (target == null) return;

        try {
            Field field = target.getClass().getField(name);
            field.setAccessible(true);
            field.setBoolean(target, value);
        } catch (Throwable ignored) {
        }
    }
}