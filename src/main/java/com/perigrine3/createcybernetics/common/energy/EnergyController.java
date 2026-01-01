package com.perigrine3.createcybernetics.common.energy;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.block.ModBlocks;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class EnergyController {

    private EnergyController() {}

    /* ===================== DEBUG (TEMPORARY) ===================== */
    // Enable:  /tag @s add cc_energy_debug
    // Disable: /tag @s remove cc_energy_debug
    private static final String DEBUG_TAG = "cc_energy_debug";

    // true = actionbar line, false = chat line (spammy)
    private static final boolean DEBUG_ACTIONBAR = true;

    // If you want fewer updates, set to e.g. 5, 10, 20
    private static final int DEBUG_EVERY_TICKS = 1;
    /* ============================================================= */

    private static final Map<Class<?>, Boolean> OVERRIDES_SHOULD_GENERATE = new ConcurrentHashMap<>();

    private static final Map<Class<?>, Method> ENERGY_GETTER_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Field>  ENERGY_FIELD_CACHE  = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        if (!player.hasData(ModAttachments.CYBERWARE)) return;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        data.clampEnergyToCapacity(player);

        final boolean debug = player.getTags().contains(DEBUG_TAG);
        final TickStats stats = debug ? new TickStats() : null;

        if (stats != null) {
            stats.storedStart = readStoredEnergySafe(data);
        }

        if (isOnChargerBlock(player)) {
            int chargerIn = 0;

            for (var entry : data.getAll().entrySet()) {
                CyberwareSlot slot = entry.getKey();
                InstalledCyberware[] arr = entry.getValue();
                if (arr == null) continue;

                for (int idx = 0; idx < arr.length; idx++) {
                    InstalledCyberware cw = arr[idx];
                    if (cw == null) continue;

                    ItemStack stack = cw.getItem();
                    if (stack == null || stack.isEmpty()) continue;

                    if (!(stack.getItem() instanceof ICyberwareItem item)) continue;

                    if (!item.acceptsChargerEnergy(player, stack, slot)) continue;

                    int req = item.getChargerEnergyReceivePerTick(player, stack, slot);
                    if (req > 0) chargerIn += req;
                }
            }

            if (chargerIn > 0) {
                data.receiveEnergy(player, chargerIn);
            }

            data.clampEnergyToCapacity(player);
        }

        int totalCapacity = 0;
        for (var entry : data.getAll().entrySet()) {
            CyberwareSlot slot = entry.getKey();
            InstalledCyberware[] arr = entry.getValue();
            if (arr == null) continue;

            for (int idx = 0; idx < arr.length; idx++) {
                InstalledCyberware cw = arr[idx];
                if (cw == null) continue;

                ItemStack stack = cw.getItem();
                if (stack == null || stack.isEmpty()) continue;

                if (!(stack.getItem() instanceof ICyberwareItem item)) continue;

                int cap = item.getEnergyCapacity(player, stack, slot);
                if (cap > 0) totalCapacity += cap;
            }
        }
        if (stats != null) stats.capacityTotal = totalCapacity;

        int tickGenerated = 0;

        for (var entry : data.getAll().entrySet()) {
            CyberwareSlot slot = entry.getKey();
            InstalledCyberware[] arr = entry.getValue();
            if (arr == null) continue;

            for (int idx = 0; idx < arr.length; idx++) {
                InstalledCyberware cw = arr[idx];
                if (cw == null) continue;

                ItemStack stack = cw.getItem();
                if (stack == null || stack.isEmpty()) continue;

                if (!(stack.getItem() instanceof ICyberwareItem item)) continue;

                int gen = item.getEnergyGeneratedPerTick(player, stack, slot);
                if (gen <= 0) continue;

                if (shouldGenerate(item, player, stack, slot)) {
                    tickGenerated += gen;
                }
            }
        }

        MutableInt tickPool = new MutableInt(tickGenerated);
        if (stats != null) stats.generated = tickGenerated;

        for (var entry : data.getAll().entrySet()) {
            CyberwareSlot slot = entry.getKey();
            InstalledCyberware[] arr = entry.getValue();
            if (arr == null) continue;

            for (int idx = 0; idx < arr.length; idx++) {
                InstalledCyberware cw = arr[idx];
                if (cw == null) continue;

                ItemStack stack = cw.getItem();
                if (stack == null || stack.isEmpty()) continue;

                if (!(stack.getItem() instanceof ICyberwareItem item)) continue;

                boolean powered = true;

                int use = item.getEnergyUsedPerTick(player, stack, slot);
                if (use > 0) {
                    if (stats != null) stats.requestedUse += use;
                    powered = tryPayEnergy(data, tickPool, use, stats);
                }

                if (powered && item.shouldConsumeActivationEnergyThisTick(player, stack, slot)) {
                    int actCost = item.getEnergyActivationCost(player, stack, slot);
                    if (actCost > 0) {
                        String paidKey = item.getActivationPaidNbtKey(player, stack, slot);

                        if (paidKey == null || paidKey.isBlank()) {
                            if (stats != null) stats.requestedActivation += actCost;
                            powered = tryPayEnergy(data, tickPool, actCost, stats);
                        } else {
                            String persistentKey = buildActivationPersistentKey(paidKey, slot, idx);
                            CompoundTag ptag = player.getPersistentData();
                            boolean alreadyPaid = ptag.getBoolean(persistentKey);

                            if (!alreadyPaid) {
                                if (stats != null) stats.requestedActivation += actCost;
                                if (tryPayEnergy(data, tickPool, actCost, stats)) {
                                    ptag.putBoolean(persistentKey, true);
                                } else {
                                    powered = false;
                                }
                            }
                        }
                    }
                } else {
                    String paidKey = item.getActivationPaidNbtKey(player, stack, slot);
                    if (paidKey != null && !paidKey.isBlank()) {
                        String persistentKey = buildActivationPersistentKey(paidKey, slot, idx);
                        player.getPersistentData().remove(persistentKey);
                    }
                }

                if (!powered && stats != null) stats.unpoweredCount++;
                cw.setPowered(powered);
            }
        }

        int leftover = tickPool.value;
        if (leftover > 0) {
            if (canAcceptGeneratedSurplus(player, data)) {
                if (stats != null) stats.surplusDeposited = leftover;
                data.receiveEnergy(player, leftover);
            } else {
                if (stats != null) stats.surplusDeposited = 0;
            }
        }

        data.clampEnergyToCapacity(player);

        if (stats != null) {
            stats.storedEnd = readStoredEnergySafe(data);
            maybeSendDebug(player, stats);
        }
    }

    private static boolean tryPayEnergy(PlayerCyberwareData data, MutableInt tickPool, int amount, TickStats stats) {
        if (amount <= 0) return true;

        int fromPool = Math.min(tickPool.value, amount);
        tickPool.value -= fromPool;
        amount -= fromPool;

        if (stats != null) stats.usedFromPool += fromPool;

        if (amount <= 0) return true;

        boolean ok = data.tryConsumeEnergy(amount);
        if (ok) {
            if (stats != null) stats.usedFromStored += amount;
            return true;
        } else {
            if (stats != null) stats.failedSpend += amount;
            return false;
        }
    }

    private static boolean canAcceptGeneratedSurplus(Player player, PlayerCyberwareData data) {
        for (var entry : data.getAll().entrySet()) {
            CyberwareSlot slot = entry.getKey();
            InstalledCyberware[] arr = entry.getValue();
            if (arr == null) continue;

            for (int idx = 0; idx < arr.length; idx++) {
                InstalledCyberware cw = arr[idx];
                if (cw == null) continue;

                ItemStack stack = cw.getItem();
                if (stack == null || stack.isEmpty()) continue;

                if (!(stack.getItem() instanceof ICyberwareItem item)) continue;

                if (item.acceptsGeneratedEnergy(player, stack, slot)) return true;
            }
        }
        return false;
    }

    private static boolean shouldGenerate(ICyberwareItem item, Player player, ItemStack stack, CyberwareSlot slot) {
        if (!overridesShouldGenerate(item.getClass())) return true;
        return item.shouldGenerateEnergyThisTick(player, stack, slot);
    }

    private static boolean overridesShouldGenerate(Class<?> cls) {
        return OVERRIDES_SHOULD_GENERATE.computeIfAbsent(cls, c -> {
            try {
                Method m = c.getMethod("shouldGenerateEnergyThisTick", Player.class, ItemStack.class, CyberwareSlot.class);
                return m.getDeclaringClass() != ICyberwareItem.class;
            } catch (NoSuchMethodException e) {
                return false;
            }
        });
    }

    private static String buildActivationPersistentKey(String key, CyberwareSlot slot, int index) {
        return "cc_energy_actpaid_" + key + "_" + slot.name() + "_" + index;
    }

    private static boolean isOnChargerBlock(Player player) {
        Level level = player.level();
        BlockPos below = player.blockPosition().below();
        return level.getBlockState(below).is(ModBlocks.CHARGING_BLOCK.get());
    }

    /* ===================== DEBUG HELPERS ===================== */

    private static void maybeSendDebug(Player player, TickStats s) {
        if (!player.getTags().contains(DEBUG_TAG)) return;

        if (DEBUG_EVERY_TICKS > 1 && (player.tickCount % DEBUG_EVERY_TICKS) != 0) return;

        int usedTotal = s.usedFromPool + s.usedFromStored;
        int requestedTotal = s.requestedUse + s.requestedActivation;
        int net = s.generated - usedTotal;
        String text =
                "Energy " + s.storedEnd + "/" + s.capacityTotal +
                        " | +" + s.generated +
                        " -" + usedTotal +
                        " = " + net +
                        " | Req " + requestedTotal +
                        " | Dep " + s.surplusDeposited +
                        " | Unp " + s.unpoweredCount;

        player.displayClientMessage(Component.literal(text), DEBUG_ACTIONBAR);
    }

    private static int readStoredEnergySafe(PlayerCyberwareData data) {
        try {
            Method m = ENERGY_GETTER_CACHE.computeIfAbsent(data.getClass(), cls -> {
                Method found = findNoArgIntGetter(cls,
                        "getEnergyStored",
                        "getStoredEnergy",
                        "getEnergy",
                        "getEnergyAmount");
                if (found != null) {
                    found.setAccessible(true);
                    return found;
                }
                return null;
            });

            if (m != null) {
                Object v = m.invoke(data);
                if (v instanceof Integer i) return i;
                if (v instanceof Number n) return n.intValue();
            }

            Field f = ENERGY_FIELD_CACHE.computeIfAbsent(data.getClass(), cls -> {
                Field ff = findIntField(cls, "energyStored", "storedEnergy", "energy", "power", "currentEnergy");
                if (ff != null) ff.setAccessible(true);
                return ff;
            });

            if (f != null) {
                return f.getInt(data);
            }

            return 0;
        } catch (Throwable t) {
            return 0;
        }
    }

    private static Method findNoArgIntGetter(Class<?> cls, String... names) {
        for (String name : names) {
            try {
                Method m = cls.getMethod(name);
                Class<?> rt = m.getReturnType();
                if (rt == int.class || Number.class.isAssignableFrom(rt)) return m;
            } catch (NoSuchMethodException ignored) {}
        }
        return null;
    }

    private static Field findIntField(Class<?> cls, String... names) {
        for (String name : names) {
            try {
                Field f = cls.getDeclaredField(name);
                if (f.getType() == int.class) return f;
            } catch (NoSuchFieldException ignored) {}
        }
        return null;
    }

    private static final class TickStats {
        int generated;
        int requestedUse;
        int requestedActivation;

        int usedFromPool;
        int usedFromStored;
        int surplusDeposited;

        int storedStart;
        int storedEnd;
        int capacityTotal;

        int unpoweredCount;
        int failedSpend;
    }

    private static final class MutableInt {
        int value;
        MutableInt(int value) { this.value = value; }
    }
}
