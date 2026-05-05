package com.perigrine3.createcybernetics.client.skin;

import net.minecraft.client.player.AbstractClientPlayer;

import java.util.ArrayDeque;
import java.util.Deque;

public final class SkinVanillaWearVisibility {

    private static final ThreadLocal<Deque<AbstractClientPlayer>> CURRENT_PLAYER =
            ThreadLocal.withInitial(ArrayDeque::new);

    private static final ThreadLocal<Integer> SUPPRESS_DEPTH =
            ThreadLocal.withInitial(() -> 0);

    private SkinVanillaWearVisibility() {}

    public static void pushPlayer(AbstractClientPlayer player) {
        if (player != null) {
            CURRENT_PLAYER.get().push(player);
        }
    }

    public static void popPlayer(AbstractClientPlayer player) {
        Deque<AbstractClientPlayer> stack = CURRENT_PLAYER.get();
        if (stack.isEmpty()) return;

        AbstractClientPlayer top = stack.peek();
        if (top == player) {
            stack.pop();
        } else {
            stack.remove(player);
        }

        if (stack.isEmpty()) {
            CURRENT_PLAYER.remove();
        }
    }

    public static AbstractClientPlayer currentPlayer() {
        Deque<AbstractClientPlayer> stack = CURRENT_PLAYER.get();
        return stack.isEmpty() ? null : stack.peek();
    }

    public static void pushSuppress() {
        SUPPRESS_DEPTH.set(SUPPRESS_DEPTH.get() + 1);
    }

    public static void popSuppress() {
        int next = Math.max(0, SUPPRESS_DEPTH.get() - 1);
        if (next == 0) {
            SUPPRESS_DEPTH.remove();
        } else {
            SUPPRESS_DEPTH.set(next);
        }
    }

    public static boolean isSuppressed() {
        return SUPPRESS_DEPTH.get() > 0;
    }
}