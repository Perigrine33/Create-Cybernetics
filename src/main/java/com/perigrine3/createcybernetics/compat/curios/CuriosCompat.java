package com.perigrine3.createcybernetics.compat.curios;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

public final class CuriosCompat {

    private static final String CURIOS_MODID = "curios";

    // Reflection cache
    private static boolean attemptedInit = false;
    private static boolean available = false;

    private static Method curiosApi_getCuriosInventory;     // static CuriosApi.getCuriosInventory(LivingEntity)
    private static Method curiosHandler_getCurios;          // ICuriosItemHandler#getCurios()
    private static Method curiosHandler_getStacksHandler;   // ICuriosItemHandler#getStacksHandler(String)

    private static Method optional_isPresent;               // Optional#isPresent()
    private static Method optional_get;                     // Optional#get()

    // Curios handler variants (new + old)
    private static Method itemHandler_getSlots;             // (ICurioStacksHandler|getSlots) OR (ICurioStackHandler|getSlots)
    private static Method itemHandler_getStackInSlot;       // (ICurioStacksHandler|getStackInSlot) OR (ICurioStackHandler|getStackInSlot)

    private static Method stacksHandler_getStacks;          // ICurioStacksHandler#getStacks() (some builds)
    private static Method neoItemHandler_getSlots;          // net.neoforged.neoforge.items.IItemHandler#getSlots()
    private static Method neoItemHandler_getStackInSlot;    // net.neoforged.neoforge.items.IItemHandler#getStackInSlot(int)

    private CuriosCompat() {}

    public static boolean isLoaded() {
        return ModList.get().isLoaded(CURIOS_MODID);
    }

    /**
     * Returns a map: slotId -> list of stacks in that slot inventory (non-empty only).
     * Example keys: "ring", "belt", etc. (whatever exists on the entity)
     */
    public static Map<String, List<ItemStack>> getAllCuriosBySlot(LivingEntity entity) {
        if (entity == null) return Map.of();
        if (!isLoaded()) return Map.of();
        if (!initReflection()) return Map.of();

        Object curiosInv = getCuriosInventory(entity);
        if (curiosInv == null) return Map.of();

        Object curiosMapObj = invoke(curiosHandler_getCurios, curiosInv);
        if (!(curiosMapObj instanceof Map<?, ?> curiosMap)) return Map.of();

        Map<String, List<ItemStack>> out = new LinkedHashMap<>();
        for (Map.Entry<?, ?> e : curiosMap.entrySet()) {
            Object key = e.getKey();
            Object slotInv = e.getValue();
            if (!(key instanceof String slotId) || slotInv == null) continue;

            List<ItemStack> stacks = readAllStacks(slotInv);
            out.put(slotId, stacks);
        }

        return out;
    }

    /**
     * Returns all non-empty stacks in a specific curios slot inventory (e.g. "ring").
     */
    public static List<ItemStack> getCuriosInSlot(LivingEntity entity, String slotId) {
        if (entity == null || slotId == null || slotId.isEmpty()) return List.of();
        if (!isLoaded()) return List.of();
        if (!initReflection()) return List.of();

        Object curiosInv = getCuriosInventory(entity);
        if (curiosInv == null) return List.of();

        Object opt = invoke(curiosHandler_getStacksHandler, curiosInv, slotId);
        Object slotInv = unwrapOptional(opt);
        if (slotInv == null) return List.of();

        return readAllStacks(slotInv);
    }

    /**
     * True if the entity has at least one matching item equipped in the given curios slot id.
     */
    public static boolean hasCurio(LivingEntity entity, String slotId, Item item) {
        if (item == null) return false;
        return findFirstCurio(entity, slotId, st -> st.is(item)).isPresent();
    }

    /**
     * Finds the first stack in the given curios slot id that matches the predicate.
     */
    public static Optional<ItemStack> findFirstCurio(LivingEntity entity, String slotId, Predicate<ItemStack> predicate) {
        if (predicate == null) return Optional.empty();
        for (ItemStack st : getCuriosInSlot(entity, slotId)) {
            if (!st.isEmpty() && predicate.test(st)) {
                return Optional.of(st);
            }
        }
        return Optional.empty();
    }

    /**
     * Finds the first stack in ANY curios slot inventory that matches the predicate.
     * Returns (slotId, stack) pair.
     */
    public static Optional<FoundCurio> findFirstCurioAnywhere(LivingEntity entity, Predicate<ItemStack> predicate) {
        if (entity == null || predicate == null) return Optional.empty();

        Map<String, List<ItemStack>> all = getAllCuriosBySlot(entity);
        for (var entry : all.entrySet()) {
            String slotId = entry.getKey();
            for (ItemStack st : entry.getValue()) {
                if (!st.isEmpty() && predicate.test(st)) {
                    return Optional.of(new FoundCurio(slotId, st));
                }
            }
        }
        return Optional.empty();
    }

    public record FoundCurio(String slotId, ItemStack stack) {}

    /* ---------------- internals ---------------- */

    private static boolean initReflection() {
        if (attemptedInit) return available;
        attemptedInit = true;

        try {
            // CuriosApi.getCuriosInventory(LivingEntity) -> Optional<ICuriosItemHandler>
            Class<?> curiosApi = Class.forName("top.theillusivec4.curios.api.CuriosApi");
            curiosApi_getCuriosInventory = curiosApi.getMethod("getCuriosInventory", LivingEntity.class);

            // Optional
            optional_isPresent = Optional.class.getMethod("isPresent");
            optional_get = Optional.class.getMethod("get");

            // ICuriosItemHandler
            Class<?> iCuriosItemHandler = Class.forName("top.theillusivec4.curios.api.type.capability.ICuriosItemHandler");
            curiosHandler_getCurios = iCuriosItemHandler.getMethod("getCurios");
            curiosHandler_getStacksHandler = iCuriosItemHandler.getMethod("getStacksHandler", String.class);

            // Curio stacks handler: plural first, singular fallback
            Class<?> stacksHandlerClass;
            try {
                stacksHandlerClass = Class.forName("top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler");
            } catch (Throwable ignored) {
                stacksHandlerClass = Class.forName("top.theillusivec4.curios.api.type.inventory.ICurioStackHandler");
            }

            // Path A: handler behaves like IItemHandler directly
            try {
                itemHandler_getSlots = stacksHandlerClass.getMethod("getSlots");
                itemHandler_getStackInSlot = stacksHandlerClass.getMethod("getStackInSlot", int.class);
            } catch (Throwable ignored) {
                itemHandler_getSlots = null;
                itemHandler_getStackInSlot = null;
            }

            // Path B: handler exposes getStacks() -> IItemHandler-like
            try {
                stacksHandler_getStacks = stacksHandlerClass.getMethod("getStacks");
            } catch (Throwable ignored) {
                stacksHandler_getStacks = null;
            }

            // NeoForge IItemHandler (for getStacks() path)
            try {
                Class<?> neoIItemHandler = Class.forName("net.neoforged.neoforge.items.IItemHandler");
                neoItemHandler_getSlots = neoIItemHandler.getMethod("getSlots");
                neoItemHandler_getStackInSlot = neoIItemHandler.getMethod("getStackInSlot", int.class);
            } catch (Throwable ignored) {
                neoItemHandler_getSlots = null;
                neoItemHandler_getStackInSlot = null;
            }

            available = true;
            return true;
        } catch (Throwable t) {
            // Curios not present or API changed
            available = false;
            return false;
        }
    }

    private static Object getCuriosInventory(LivingEntity entity) {
        Object opt = invokeStatic(curiosApi_getCuriosInventory, entity);
        return unwrapOptional(opt);
    }

    private static Object unwrapOptional(Object opt) {
        if (!(opt instanceof Optional<?> o)) return null;
        return o.orElse(null);
    }

    private static List<ItemStack> readAllStacks(Object slotInv) {
        if (slotInv == null) return List.of();

        // Path A: Curios handler is already "item-handler-like"
        if (itemHandler_getSlots != null && itemHandler_getStackInSlot != null) {
            Object slotsObj = invoke(itemHandler_getSlots, slotInv);
            if (slotsObj instanceof Integer slots) {
                List<ItemStack> out = new ArrayList<>();
                for (int i = 0; i < slots; i++) {
                    Object stObj = invoke(itemHandler_getStackInSlot, slotInv, i);
                    if (stObj instanceof ItemStack st && !st.isEmpty()) out.add(st);
                }
                return out;
            }
        }

        // Path B: Curios handler exposes getStacks() -> NeoForge IItemHandler
        if (stacksHandler_getStacks != null && neoItemHandler_getSlots != null && neoItemHandler_getStackInSlot != null) {
            Object stacksObj = invoke(stacksHandler_getStacks, slotInv);
            if (stacksObj != null) {
                Object slotsObj = invoke(neoItemHandler_getSlots, stacksObj);
                if (slotsObj instanceof Integer slots) {
                    List<ItemStack> out = new ArrayList<>();
                    for (int i = 0; i < slots; i++) {
                        Object stObj = invoke(neoItemHandler_getStackInSlot, stacksObj, i);
                        if (stObj instanceof ItemStack st && !st.isEmpty()) out.add(st);
                    }
                    return out;
                }
            }
        }

        return List.of();
    }

    private static Object invokeStatic(Method m, Object... args) {
        if (m == null) return null;
        try {
            return m.invoke(null, args);
        } catch (Throwable t) {
            return null;
        }
    }

    private static Object invoke(Method m, Object target, Object... args) {
        if (m == null || target == null) return null;
        try {
            return m.invoke(target, args);
        } catch (Throwable t) {
            return null;
        }
    }
}
