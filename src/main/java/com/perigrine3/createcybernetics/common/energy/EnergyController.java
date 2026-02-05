package com.perigrine3.createcybernetics.common.energy;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.block.ModBlocks;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.effect.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class EnergyController {

    private EnergyController() {}

    private static final Map<Class<?>, Boolean> OVERRIDES_SHOULD_GENERATE = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        if (!player.hasData(ModAttachments.CYBERWARE)) return;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        // ================================================================
        // EMP: wipe stored energy, clear activation-paid flags, and mark all unpowered.
        // ================================================================
        if (hasEmpEffect(player)) {
            data.setEnergyStored(player, 0);

            for (var entry : data.getAll().entrySet()) {
                CyberwareSlot slot = entry.getKey();
                InstalledCyberware[] arr = entry.getValue();
                if (arr == null) continue;

                for (int idx = 0; idx < arr.length; idx++) {
                    InstalledCyberware cw = arr[idx];
                    if (cw == null) continue;

                    ItemStack stack = cw.getItem();
                    if (stack == null || stack.isEmpty()) {
                        cw.setPowered(false);
                        continue;
                    }

                    if (stack.getItem() instanceof ICyberwareItem item) {
                        String paidKey = item.getActivationPaidNbtKey(player, stack, slot);
                        if (paidKey != null && !paidKey.isBlank()) {
                            String persistentKey = buildActivationPersistentKey(paidKey, slot, idx);
                            player.getPersistentData().remove(persistentKey);
                        }
                    }

                    cw.setPowered(false);
                }
            }

            return;
        }

        // ================================================================
        // Normal flow
        // ================================================================
        data.clampEnergyToCapacity(player);

        final boolean onCharger = isOnChargerBlock(player);

        // ================================================================
        // Compute charger "charge rate" (how much can be stored this tick).
        // This is independent from powering implants while on the charger.
        // ================================================================
        int chargerCharge = 0;
        if (onCharger) {
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
                    if (req > 0) chargerCharge += req;
                }
            }
        }

        // ================================================================
        // Generator pool (existing behavior)
        // ================================================================
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

        MutableInt genPool = new MutableInt(tickGenerated);

        // ================================================================
        // “Mains” power while on the charger:
        // If on the charging block, implants can draw unlimited external power.
        // This prevents stored energy from being drained and allows batteries to charge.
        // ================================================================
        MutableInt mainsPool = new MutableInt(onCharger ? Integer.MAX_VALUE : 0);

        // ================================================================
        // Pay per-tick energy + activation costs:
        // Priority:
        //   1) mainsPool (only when on charger)
        //   2) genPool
        //   3) stored energy
        // ================================================================
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
                    powered = tryPayEnergy(data, mainsPool, genPool, use);
                }

                if (powered && item.shouldConsumeActivationEnergyThisTick(player, stack, slot)) {
                    int actCost = item.getEnergyActivationCost(player, stack, slot);
                    if (actCost > 0) {
                        String paidKey = item.getActivationPaidNbtKey(player, stack, slot);

                        if (paidKey == null || paidKey.isBlank()) {
                            powered = tryPayEnergy(data, mainsPool, genPool, actCost);
                        } else {
                            String persistentKey = buildActivationPersistentKey(paidKey, slot, idx);
                            CompoundTag ptag = player.getPersistentData();
                            boolean alreadyPaid = ptag.getBoolean(persistentKey);

                            if (!alreadyPaid) {
                                if (tryPayEnergy(data, mainsPool, genPool, actCost)) {
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

                cw.setPowered(powered);
            }
        }

        // ================================================================
        // Deposit generator surplus (existing rule)
        // ================================================================
        int genLeftover = genPool.value;
        if (genLeftover > 0) {
            if (canAcceptGeneratedSurplus(player, data)) {
                data.receiveEnergy(player, genLeftover);
            }
        }

        // ================================================================
        // Deposit charger charge into stored energy.
        // This is the ONLY place charger affects stored energy now.
        // ================================================================
        if (onCharger && chargerCharge > 0) {
            data.receiveEnergy(player, chargerCharge);
        }

        data.clampEnergyToCapacity(player);
    }

    /**
     * Spend energy in priority order:
     *   1) mainsPool (only available while on charging block)
     *   2) genPool
     *   3) stored energy
     */
    private static boolean tryPayEnergy(PlayerCyberwareData data, MutableInt mainsPool, MutableInt genPool, int amount) {
        if (amount <= 0) return true;

        int fromMains = Math.min(mainsPool.value, amount);
        mainsPool.value -= fromMains;
        amount -= fromMains;

        if (amount <= 0) return true;

        int fromGen = Math.min(genPool.value, amount);
        genPool.value -= fromGen;
        amount -= fromGen;

        if (amount <= 0) return true;

        return data.tryConsumeEnergy(amount);
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

    private static final class MutableInt {
        int value;
        MutableInt(int value) { this.value = value; }
    }

    private static boolean hasEmpEffect(Player player) {
        return player.hasEffect(ModEffects.EMP);
    }
}
