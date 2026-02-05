package com.perigrine3.createcybernetics.common.energy;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.block.ModBlocks;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.minecraft.core.BlockPos;
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
public final class EnergyDebugTicker {
    private EnergyDebugTicker() {}

    // Must match the key used in CyberneticsCommand
    private static final String PKEY_ENERGY_DEBUG = "cc_energy_debug_enabled";

    private static final Map<Class<?>, Method> ENERGY_GETTER_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Field>  ENERGY_FIELD_CACHE  = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        if (!player.getPersistentData().getBoolean(PKEY_ENERGY_DEBUG)) return;

        if (!player.hasData(ModAttachments.CYBERWARE)) return;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        int stored = readStoredEnergySafe(data);
        int capacity = data.getTotalEnergyCapacity(player);

        boolean onCharger = isOnChargerBlock(player);

        int generated = 0;
        int required = 0;

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
                if (gen > 0) generated += gen;

                int use = item.getEnergyUsedPerTick(player, stack, slot);
                if (use > 0) required += use;

                // Activation request (matches your controller rule: only if it wants to consume this tick)
                if (item.shouldConsumeActivationEnergyThisTick(player, stack, slot)) {
                    int act = item.getEnergyActivationCost(player, stack, slot);
                    if (act > 0) required += act;
                }
            }
        }

        // “Total combined energy input”:
        // - Off charger: stored + generated (what you actually have available this tick)
        // - On charger: effectively unlimited due to mainsPool in EnergyController (show as ∞)
        String inputStr;
        String remainingStr;

        if (onCharger) {
            inputStr = "∞";
            remainingStr = "∞";
        } else {
            int input = stored + generated;
            int remaining = input - required;
            if (remaining < 0) remaining = 0;

            inputStr = Integer.toString(input);
            remainingStr = Integer.toString(remaining);
        }

        String line =
                "Capacity: [ " + stored + "/" + capacity + " ] " +
                        "Consumption: [ " + inputStr + "-" + required + "=" + remainingStr + " ]";

        player.displayClientMessage(Component.literal(line), true);
    }

    private static boolean isOnChargerBlock(Player player) {
        Level level = player.level();
        BlockPos below = player.blockPosition().below();
        return level.getBlockState(below).is(ModBlocks.CHARGING_BLOCK.get());
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
}
