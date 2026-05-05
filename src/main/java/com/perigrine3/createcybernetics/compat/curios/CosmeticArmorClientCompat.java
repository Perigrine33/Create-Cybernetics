package com.perigrine3.createcybernetics.compat.curios;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public final class CosmeticArmorClientCompat {

    private static final String[] MODIDS = {
            "cosmeticarmorreworked",
            "cosmeticarmorreworkedforked"
    };

    private static final String[] SCREEN_CLASSES = {
            "lain.mods.cos.impl.client.gui.GuiCosArmorInventory",
            "lain.mods.cos.impl.client.gui.GuiCosArmorScreen",
            "lain.mods.cos.impl.client.gui.CosArmorScreen",
            "lain.mods.cos.impl.client.gui.GuiCosArmor"
    };

    private static final String[] STATIC_OPEN_CLASSES = {
            "lain.mods.cos.impl.client.GuiHandler",
            "lain.mods.cos.impl.client.KeyHandler",
            "lain.mods.cos.impl.ModObjects",
            "lain.mods.cos.impl.CosmeticArmorReworked"
    };

    private static final String[] STATIC_OPEN_METHODS = {
            "openCosArmorInventory",
            "openCosArmorScreen",
            "openCosArmor",
            "openGui",
            "openScreen"
    };

    private CosmeticArmorClientCompat() {}

    public static boolean isLoaded() {
        for (String modid : MODIDS) {
            if (ModList.get().isLoaded(modid)) return true;
        }
        return false;
    }

    public static boolean open(Screen parent) {
        if (!isLoaded()) return false;

        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return false;

        if (tryStaticOpen(parent, mc)) {
            return true;
        }

        Screen screen = tryCreateScreen(parent, mc);
        if (screen != null) {
            mc.setScreen(screen);
            return true;
        }

        mc.player.displayClientMessage(Component.literal("Cosmetic Armor is installed, but its inventory screen could not be opened."), true);
        return false;
    }

    private static boolean tryStaticOpen(Screen parent, Minecraft mc) {
        for (String className : STATIC_OPEN_CLASSES) {
            Class<?> clazz = tryClass(className);
            if (clazz == null) continue;

            for (Method method : clazz.getDeclaredMethods()) {
                for (String wanted : STATIC_OPEN_METHODS) {
                    if (!method.getName().equals(wanted)) continue;

                    try {
                        method.setAccessible(true);
                        Class<?>[] params = method.getParameterTypes();

                        if (params.length == 0) {
                            method.invoke(null);
                            return true;
                        }

                        if (params.length == 1) {
                            Object arg = resolveArg(params[0], parent, mc);
                            if (arg != null) {
                                method.invoke(null, arg);
                                return true;
                            }
                        }

                        if (params.length == 2) {
                            Object a = resolveArg(params[0], parent, mc);
                            Object b = resolveArg(params[1], parent, mc);
                            if (a != null && b != null) {
                                method.invoke(null, a, b);
                                return true;
                            }
                        }
                    } catch (Throwable ignored) {
                    }
                }
            }
        }

        return false;
    }

    private static Screen tryCreateScreen(Screen parent, Minecraft mc) {
        for (String className : SCREEN_CLASSES) {
            Class<?> clazz = tryClass(className);
            if (clazz == null) continue;

            for (Constructor<?> ctor : clazz.getDeclaredConstructors()) {
                try {
                    ctor.setAccessible(true);
                    Class<?>[] params = ctor.getParameterTypes();

                    if (params.length == 0) {
                        Object obj = ctor.newInstance();
                        if (obj instanceof Screen screen) return screen;
                    }

                    if (params.length == 1) {
                        Object arg = resolveArg(params[0], parent, mc);
                        if (arg != null) {
                            Object obj = ctor.newInstance(arg);
                            if (obj instanceof Screen screen) return screen;
                        }
                    }

                    if (params.length == 2) {
                        Object a = resolveArg(params[0], parent, mc);
                        Object b = resolveArg(params[1], parent, mc);
                        if (a != null && b != null) {
                            Object obj = ctor.newInstance(a, b);
                            if (obj instanceof Screen screen) return screen;
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
        }

        return null;
    }

    private static Object resolveArg(Class<?> param, Screen parent, Minecraft mc) {
        if (parent != null && param.isInstance(parent)) return parent;
        if (param == Screen.class) return parent;
        if (param == Minecraft.class) return mc;
        if (param == Player.class && mc.player != null) return mc.player;
        return null;
    }

    private static Class<?> tryClass(String name) {
        try {
            return Class.forName(name);
        } catch (Throwable ignored) {
            return null;
        }
    }
}