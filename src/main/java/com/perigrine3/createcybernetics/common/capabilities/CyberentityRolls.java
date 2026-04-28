package com.perigrine3.createcybernetics.common.capabilities;

import com.perigrine3.createcybernetics.ConfigValues;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class CyberentityRolls {

    private static final String DEFAULT_TABLE_ID = "cyberzombie";

    private static final Map<String, EnumMap<CyberwareSlot, SlotSpawnTable>> SPAWN_TABLES = new HashMap<>();

    private static final ChipwareSpawnTable CHIPWARE_TABLE = new ChipwareSpawnTable();
    private static boolean chipwareBootstrapped = false;

    private CyberentityRolls() {}

    public static void generateRandomCyberware(LivingEntity entity, EntityCyberwareData data, RandomSource random) {
        if (entity == null || data == null || random == null) return;

        bootstrapDefaultSpawnTables();
        bootstrapDefaultChipwareTable();

        data.resetToDefaultOrgans();

        String tableId = getTableIdForEntity(entity);
        EnumMap<CyberwareSlot, SlotSpawnTable> tableMap = SPAWN_TABLES.get(tableId);

        if (tableMap == null && DEFAULT_TABLE_ID.equals(tableId)) {
            tableMap = SPAWN_TABLES.get(DEFAULT_TABLE_ID);
        }

        if (tableMap == null) {
            return;
        }

        rollGroup(entity, data, random, tableMap, CyberwareSlot.BRAIN);
        rollGroup(entity, data, random, tableMap, CyberwareSlot.EYES);
        rollGroup(entity, data, random, tableMap, CyberwareSlot.HEART);
        rollGroup(entity, data, random, tableMap, CyberwareSlot.LUNGS);
        rollGroup(entity, data, random, tableMap, CyberwareSlot.ORGANS);
        rollGroup(entity, data, random, tableMap, CyberwareSlot.BONE);
        rollGroup(entity, data, random, tableMap, CyberwareSlot.SKIN);
        rollGroup(entity, data, random, tableMap, CyberwareSlot.MUSCLE);
        rollGroup(entity, data, random, tableMap, CyberwareSlot.RLEG);
        rollGroup(entity, data, random, tableMap, CyberwareSlot.LLEG);
        rollGroup(entity, data, random, tableMap, CyberwareSlot.RARM);
        rollGroup(entity, data, random, tableMap, CyberwareSlot.LARM);

        if (data.hasSpecificItem(ModItems.BRAINUPGRADES_CHIPWARESLOTS.get(), CyberwareSlot.BRAIN)) {
            rollChipware(data, random);
        }

        data.setDirty();
    }

    private static void rollGroup(
            LivingEntity entity,
            EntityCyberwareData data,
            RandomSource random,
            EnumMap<CyberwareSlot, SlotSpawnTable> tableMap,
            CyberwareSlot slot
    ) {
        SlotSpawnTable table = tableMap.get(slot);
        if (table == null || table.entries.isEmpty()) return;

        if (random.nextFloat() > table.rollChance) return;

        int rolls = table.minRolls;
        if (table.maxRolls > table.minRolls) {
            rolls += random.nextInt(table.maxRolls - table.minRolls + 1);
        }

        for (int i = 0; i < rolls; i++) {
            ItemStack rolled = table.roll(random);
            if (rolled.isEmpty()) continue;
            data.commandInstall(entity, rolled);
        }
    }

    private static void rollChipware(EntityCyberwareData data, RandomSource random) {
        if (data == null || random == null) return;
        if (CHIPWARE_TABLE.entries.isEmpty()) return;

        int maxSlots = data.getChipwareSlotCount();
        if (maxSlots <= 0) return;

        int rolls = 1 + random.nextInt(maxSlots);
        for (int i = 0; i < rolls; i++) {
            ItemStack shard = CHIPWARE_TABLE.roll(random);
            if (shard.isEmpty()) continue;
            data.addChipwareStack(shard);
        }
    }

    private static void bootstrapDefaultSpawnTables() {
        SPAWN_TABLES.clear();

        for (ConfigValues.EntitySlotRoll cfg : ConfigValues.ENTITY_SLOT_ROLLS) {
            if (cfg == null || cfg.tableId() == null || cfg.slot() == null) continue;
            addTable(cfg.tableId(), cfg.slot(), cfg.rollChance(), cfg.minRolls(), cfg.maxRolls());
        }

        for (ConfigValues.EntityCyberwareRoll cfg : ConfigValues.ENTITY_CYBERWARE_ROLLS) {
            if (cfg == null || cfg.tableId() == null || cfg.slot() == null || cfg.item() == null) continue;
            addRoll(cfg.tableId(), cfg.slot(), cfg.weight(), () -> new ItemStack(cfg.item()));
        }
    }

    private static void bootstrapDefaultChipwareTable() {
        if (chipwareBootstrapped) return;
        chipwareBootstrapped = true;

        CHIPWARE_TABLE.entries.clear();

        addChipwareRoll(8, () -> new ItemStack(ModItems.DATA_SHARD_RED.get()));
        addChipwareRoll(8, () -> new ItemStack(ModItems.DATA_SHARD_ORANGE.get()));
        addChipwareRoll(8, () -> new ItemStack(ModItems.DATA_SHARD_YELLOW.get()));
        addChipwareRoll(8, () -> new ItemStack(ModItems.DATA_SHARD_GREEN.get()));
        addChipwareRoll(8, () -> new ItemStack(ModItems.DATA_SHARD_CYAN.get()));
        addChipwareRoll(8, () -> new ItemStack(ModItems.DATA_SHARD_BLUE.get()));
        addChipwareRoll(8, () -> new ItemStack(ModItems.DATA_SHARD_PURPLE.get()));
        addChipwareRoll(8, () -> new ItemStack(ModItems.DATA_SHARD_PINK.get()));
        addChipwareRoll(8, () -> new ItemStack(ModItems.DATA_SHARD_BROWN.get()));
        addChipwareRoll(8, () -> new ItemStack(ModItems.DATA_SHARD_GRAY.get()));
        addChipwareRoll(8, () -> new ItemStack(ModItems.DATA_SHARD_BLACK.get()));
    }

    public static void addTable(String tableId, CyberwareSlot slot, float rollChance, int minRolls, int maxRolls) {
        if (tableId == null || tableId.isBlank() || slot == null) return;

        EnumMap<CyberwareSlot, SlotSpawnTable> tableMap =
                SPAWN_TABLES.computeIfAbsent(tableId, k -> new EnumMap<>(CyberwareSlot.class));

        tableMap.put(slot, new SlotSpawnTable(rollChance, minRolls, maxRolls));
    }

    public static void addRoll(String tableId, CyberwareSlot slot, int weight, Supplier<ItemStack> stackSupplier) {
        if (tableId == null || tableId.isBlank() || slot == null || stackSupplier == null || weight <= 0) return;

        ItemStack preview;
        try {
            preview = stackSupplier.get();
        } catch (Exception ignored) {
            return;
        }

        if (preview == null || preview.isEmpty()) return;

        EnumMap<CyberwareSlot, SlotSpawnTable> tableMap =
                SPAWN_TABLES.computeIfAbsent(tableId, k -> new EnumMap<>(CyberwareSlot.class));

        SlotSpawnTable table = tableMap.get(slot);
        if (table == null) {
            table = new SlotSpawnTable(0.0f, 0, 0);
            tableMap.put(slot, table);
        }

        table.entries.add(new WeightedCyberwareEntry(weight, stackSupplier));
    }

    private static void addChipwareRoll(int weight, Supplier<ItemStack> stackSupplier) {
        if (stackSupplier == null || weight <= 0) return;

        ItemStack preview;
        try {
            preview = stackSupplier.get();
        } catch (Exception ignored) {
            return;
        }

        if (preview == null || preview.isEmpty()) return;

        CHIPWARE_TABLE.entries.add(new WeightedCyberwareEntry(weight, stackSupplier));
    }

    private static String getTableIdForEntity(LivingEntity entity) {
        ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (key == null) return DEFAULT_TABLE_ID;

        String path = key.getPath();
        if ("cyberzombie".equals(path)) return "cyberzombie";
        if ("cyberskeleton".equals(path)) return "cyberskeleton";
        if ("smasher".equals(path)) return "smasher";
        if ("hogboy".equals(path)) return "hogboy";
        if ("punklin".equals(path)) return "punklin";
        if ("pigstrom".equals(path)) return "pigstrom";

        return DEFAULT_TABLE_ID;
    }

    private static final class SlotSpawnTable {
        private final float rollChance;
        private final int minRolls;
        private final int maxRolls;
        private final List<WeightedCyberwareEntry> entries = new ArrayList<>();

        private SlotSpawnTable(float rollChance, int minRolls, int maxRolls) {
            this.rollChance = rollChance;
            this.minRolls = minRolls;
            this.maxRolls = maxRolls;
        }

        private ItemStack roll(RandomSource random) {
            if (entries.isEmpty()) return ItemStack.EMPTY;

            int totalWeight = 0;
            for (WeightedCyberwareEntry entry : entries) {
                totalWeight += entry.weight;
            }

            if (totalWeight <= 0) return ItemStack.EMPTY;

            int r = random.nextInt(totalWeight);

            for (WeightedCyberwareEntry entry : entries) {
                r -= entry.weight;
                if (r < 0) {
                    ItemStack out;
                    try {
                        out = entry.stackSupplier.get();
                    } catch (Exception ignored) {
                        return ItemStack.EMPTY;
                    }
                    return out == null ? ItemStack.EMPTY : out.copy();
                }
            }

            return ItemStack.EMPTY;
        }
    }

    private static final class ChipwareSpawnTable {
        private final List<WeightedCyberwareEntry> entries = new ArrayList<>();

        private ItemStack roll(RandomSource random) {
            if (entries.isEmpty()) return ItemStack.EMPTY;

            int totalWeight = 0;
            for (WeightedCyberwareEntry entry : entries) {
                totalWeight += entry.weight;
            }

            if (totalWeight <= 0) return ItemStack.EMPTY;

            int r = random.nextInt(totalWeight);

            for (WeightedCyberwareEntry entry : entries) {
                r -= entry.weight;
                if (r < 0) {
                    ItemStack out;
                    try {
                        out = entry.stackSupplier.get();
                    } catch (Exception ignored) {
                        return ItemStack.EMPTY;
                    }
                    return out == null ? ItemStack.EMPTY : out.copy();
                }
            }

            return ItemStack.EMPTY;
        }
    }

    private static final class WeightedCyberwareEntry {
        private final int weight;
        private final Supplier<ItemStack> stackSupplier;

        private WeightedCyberwareEntry(int weight, Supplier<ItemStack> stackSupplier) {
            this.weight = weight;
            this.stackSupplier = stackSupplier;
        }
    }
}