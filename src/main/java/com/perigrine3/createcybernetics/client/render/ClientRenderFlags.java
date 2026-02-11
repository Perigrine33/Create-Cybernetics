package com.perigrine3.createcybernetics.client.render;

public final class ClientRenderFlags {
    private static final ThreadLocal<Boolean> GUI_ENTITY_RENDER =
            ThreadLocal.withInitial(() -> Boolean.FALSE);

    public static boolean isGuiEntityRender() {
        return GUI_ENTITY_RENDER.get();
    }

    public static void setGuiEntityRender(boolean value) {
        GUI_ENTITY_RENDER.set(value);
    }

    private ClientRenderFlags() {}
}
