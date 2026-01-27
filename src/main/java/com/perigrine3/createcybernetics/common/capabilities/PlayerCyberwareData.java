package com.perigrine3.createcybernetics.common.capabilities;

import com.perigrine3.createcybernetics.ConfigValues;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareData;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.surgery.DefaultOrgans;
import com.perigrine3.createcybernetics.item.cyberware.ArmCannonItem;
import com.perigrine3.createcybernetics.item.cyberware.SpinalInjectorItem;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.EnumMap;
import java.util.Map;

public class PlayerCyberwareData implements ICyberwareData {

    private static final String NBT_CYBERWARE = "Cyberware";
    private static final String NBT_HUMANITY = "Humanity";
    private static final String NBT_ENERGY = "Energy";

    private boolean forcedChamberCrouch = false;

    private static final String NBT_NEUROPOZYNE_APPLY_COUNT = "NeuropozyneApplyCount";
    private int neuropozyneApplyCount = 0;

    private static final String NBT_SPINAL_INJECTOR_INV = "SpinalInjectorInv";
    private final ItemStack[] spinalInjectorInv = new ItemStack[SpinalInjectorItem.SLOT_COUNT];

    private static final String NBT_ARM_CANNON_INV = "ArmCannonInv";
    private final ItemStack[] armCannonInv = new ItemStack[ArmCannonItem.SLOT_COUNT];

    private static final String NBT_ARM_CANNON_SELECTED = "ArmCannonSelected";
    private int armCannonSelected = 0;

    // ---- Copernicus oxygen (display units 0..maxDisplayUnits) ----
    private static final String NBT_COPERNICUS_OXYGEN = "CopernicusOxygen";
    private int copernicusOxygen = 0;
    private boolean copernicusOxygenatedEnvironment = false;
    private int copernicusOxygenSecondTicker = 0;


    public static final int CHIPWARE_SLOT_COUNT = 2;
    private static final String NBT_CHIPWARE_INV = "ChipwareInv";
    private final ItemStack[] chipwareInv = new ItemStack[CHIPWARE_SLOT_COUNT];

    private final EnumMap<CyberwareSlot, InstalledCyberware[]> slots =
            new EnumMap<>(CyberwareSlot.class);

    private final EnumMap<CyberwareSlot, boolean[]> enabled =
            new EnumMap<>(CyberwareSlot.class);

    private boolean dirty = false;
    private int humanity = ConfigValues.BASE_HUMANITY;
    private int humanityBonus = 0;

    private int energyStored = 0;

    public PlayerCyberwareData() {
        for (CyberwareSlot slot : CyberwareSlot.values()) {
            slots.put(slot, new InstalledCyberware[slot.size]);

            boolean[] en = new boolean[slot.size];
            for (int i = 0; i < en.length; i++) en[i] = true;
            enabled.put(slot, en);
        }

        for (int i = 0; i < spinalInjectorInv.length; i++) {
            spinalInjectorInv[i] = ItemStack.EMPTY;
        }
        for (int i = 0; i < armCannonInv.length; i++) {
            armCannonInv[i] = ItemStack.EMPTY;
        }
        for (int i = 0; i < chipwareInv.length; i++) {
            chipwareInv[i] = ItemStack.EMPTY;
        }

        armCannonSelected = 0;
    }

    @Override
    public InstalledCyberware get(CyberwareSlot slot, int index) {
        return slots.get(slot)[index];
    }

    @Override
    public void set(CyberwareSlot slot, int index, InstalledCyberware cyberware) {
        slots.get(slot)[index] = cyberware;

        boolean[] en = enabled.get(slot);
        if (en != null && index >= 0 && index < en.length) {
            boolean enabledByDefault = true;

            if (cyberware != null) {
                ItemStack st = cyberware.getItem();
                if (st != null && !st.isEmpty() && st.getItem() instanceof ICyberwareItem cwItem) {
                    // Wheel-toggleables should start DISABLED by default.
                    if (cwItem.isToggleableByWheel(st, slot)) {
                        enabledByDefault = false;
                    }
                }
            }

            en[index] = enabledByDefault;
        }

        dirty = true;
    }

    @Override
    public InstalledCyberware remove(CyberwareSlot slot, int index) {
        InstalledCyberware old = slots.get(slot)[index];
        slots.get(slot)[index] = null;

        boolean[] en = enabled.get(slot);
        if (en != null && index >= 0 && index < en.length) {
            en[index] = true;
        }

        dirty = true;
        return old;
    }

    @Override
    public Map<CyberwareSlot, InstalledCyberware[]> getAll() {
        return slots;
    }

    @Override
    public int getHumanity() {
        return humanity + humanityBonus;
    }

    @Override
    public void setHumanity(int value) {
        humanity = value;
        dirty = true;
    }

    public int getHumanityBase() {
        return humanity;
    }

    public int getHumanityBonus() {
        return humanityBonus;
    }

    public void setHumanityBonus(int bonus) {
        int clamped = Mth.clamp(bonus, 0, 1000);
        if (clamped != humanityBonus) {
            humanityBonus = clamped;
            dirty = true;
        }
    }

    public void clearHumanityBonus() {
        setHumanityBonus(0);
    }

    public void recomputeHumanityBaseFromInstalled() {
        int base = ConfigValues.BASE_HUMANITY;

        for (var entry : slots.entrySet()) {
            InstalledCyberware[] arr = entry.getValue();
            if (arr == null) continue;

            for (InstalledCyberware cw : arr) {
                if (cw == null) continue;

                ItemStack stack = cw.getItem();
                if (stack == null || stack.isEmpty()) continue;

                if (stack.getItem() instanceof ICyberwareItem item) {
                    base -= item.getHumanityCost();
                }
            }
        }

        humanity = Math.max(0, base);
        dirty = true;
    }

    /* ---------------- ARM CANNON SELECTED SLOT ---------------- */

    public int getArmCannonSelected() {
        return Mth.clamp(armCannonSelected, 0, ArmCannonItem.SLOT_COUNT - 1);
    }

    public void setArmCannonSelected(int idx) {
        int clamped = Mth.clamp(idx, 0, ArmCannonItem.SLOT_COUNT - 1);
        if (clamped != armCannonSelected) {
            armCannonSelected = clamped;
            dirty = true;
        }
    }

    /* ---------------- CHIPWARE INVENTORY (2 SLOTS) ---------------- */

    public ItemStack getChipwareStack(int slot) {
        if (slot < 0 || slot >= chipwareInv.length) return ItemStack.EMPTY;
        ItemStack st = chipwareInv[slot];
        return st == null ? ItemStack.EMPTY : st;
    }

    public void setChipwareStack(int slot, ItemStack stack) {
        if (slot < 0 || slot >= chipwareInv.length) return;

        if (stack == null || stack.isEmpty()) {
            chipwareInv[slot] = ItemStack.EMPTY;
            dirty = true;
            return;
        }

        // Only allow DATA_SHARDS
        if (!stack.is(ModTags.Items.DATA_SHARDS)) {
            chipwareInv[slot] = ItemStack.EMPTY;
            dirty = true;
            return;
        }

        ItemStack copy = stack.copy();
        copy.setCount(1); // "installed shard" semantics

        chipwareInv[slot] = copy;
        dirty = true;
    }

    public void clearChipwareInventory() {
        for (int i = 0; i < chipwareInv.length; i++) {
            chipwareInv[i] = ItemStack.EMPTY;
        }
        dirty = true;
    }

    public boolean hasChipwareShard(TagKey<Item> tag) {
        for (int i = 0; i < CHIPWARE_SLOT_COUNT; i++) {
            ItemStack st = getChipwareStack(i);
            if (!st.isEmpty() && st.is(tag)) return true;
        }
        return false;
    }

    public boolean hasChipwareShardExact(Item shardItem) {
        if (shardItem == null) return false;

        for (int i = 0; i < CHIPWARE_SLOT_COUNT; i++) {
            ItemStack st = getChipwareStack(i);
            if (!st.isEmpty() && st.is(shardItem)) {
                return true;
            }
        }
        return false;
    }

    /* ---------------- ENABLED HELPERS ---------------- */

    public boolean isEnabled(CyberwareSlot slot, int index) {
        boolean[] arr = enabled.get(slot);
        if (arr == null) return true;
        if (index < 0 || index >= arr.length) return true;
        return arr[index];
    }

    public void setEnabled(CyberwareSlot slot, int index, boolean value) {
        boolean[] arr = enabled.get(slot);
        if (arr == null) return;
        if (index < 0 || index >= arr.length) return;

        if (arr[index] != value) {
            arr[index] = value;
            dirty = true;
        }
    }

    public boolean toggleEnabled(CyberwareSlot slot, int index) {
        boolean next = !isEnabled(slot, index);
        setEnabled(slot, index, next);
        return next;
    }

    public boolean hasOrgan(CyberwareSlot slot) {
        InstalledCyberware[] arr = slots.get(slot);
        for (InstalledCyberware cw : arr) {
            if (cw != null && cw.getItem() != null && !cw.getItem().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean isOrganReplaced(CyberwareSlot slot) {
        InstalledCyberware[] arr = slots.get(slot);
        for (InstalledCyberware cw : arr) {
            if (cw != null && !cw.getItem().isEmpty() && cw.getItem().getItem() instanceof ICyberwareItem item) {
                if (item.replacesOrgan() && item.getReplacedOrgans().contains(slot)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasAnyInSlots(CyberwareSlot slot) {
        InstalledCyberware[] arr = slots.get(slot);
        if (arr == null) return false;

        for (InstalledCyberware cw : arr) {
            if (cw == null) continue;
            if (!cw.getItem().isEmpty()) return true;
        }

        return false;
    }

    public boolean hasAnyTagged(TagKey<Item> tag, CyberwareSlot... slotsToCheck) {
        for (CyberwareSlot slot : slotsToCheck) {
            InstalledCyberware[] arr = slots.get(slot);
            if (arr == null) continue;

            for (InstalledCyberware cw : arr) {
                if (cw == null) continue;

                ItemStack stack = cw.getItem();
                if (stack.isEmpty()) continue;

                if (stack.is(tag)) return true;
            }
        }
        return false;
    }

    public boolean hasSpecificItem(Item item, CyberwareSlot... slotsToCheck) {
        for (CyberwareSlot slot : slotsToCheck) {
            InstalledCyberware[] arr = slots.get(slot);
            if (arr == null) continue;

            for (InstalledCyberware cw : arr) {
                if (cw == null) continue;

                ItemStack stack = cw.getItem();
                if (stack.isEmpty()) continue;

                if (stack.is(item)) return true;
            }
        }
        return false;
    }

    public boolean hasMultipleSpecificItem(Item item, CyberwareSlot slotToCheck, int requiredCount) {
        if (item == null) return false;
        if (requiredCount <= 0) return true;

        InstalledCyberware[] arr = slots.get(slotToCheck);
        if (arr == null) return false;

        int found = 0;
        for (InstalledCyberware cw : arr) {
            if (cw == null) continue;

            ItemStack stack = cw.getItem();
            if (stack == null || stack.isEmpty()) continue;

            if (stack.is(item)) {
                found++;
                if (found >= requiredCount) return true;
            }
        }

        return false;
    }

    public boolean hasMultipleSpecificItem(Item item, int requiredCount, CyberwareSlot... slotsToCheck) {
        if (item == null) return false;
        if (requiredCount <= 0) return true;
        if (slotsToCheck == null || slotsToCheck.length == 0) return false;

        int found = 0;

        for (CyberwareSlot slot : slotsToCheck) {
            InstalledCyberware[] arr = slots.get(slot);
            if (arr == null) continue;

            for (InstalledCyberware cw : arr) {
                if (cw == null) continue;

                ItemStack stack = cw.getItem();
                if (stack == null || stack.isEmpty()) continue;

                if (stack.is(item)) {
                    found++;
                    if (found >= requiredCount) return true;
                }
            }
        }

        return false;
    }

    public boolean isInstalled(Item item, CyberwareSlot slot, int index) {
        InstalledCyberware[] arr = slots.get(slot);
        if (arr == null) return false;
        if (index < 0 || index >= arr.length) return false;

        InstalledCyberware cw = arr[index];
        if (cw == null) return false;

        ItemStack stack = cw.getItem();
        if (stack == null || stack.isEmpty()) return false;

        return stack.is(item);
    }

    public boolean isInstalled(Item item, CyberwareSlot slot) {
        return hasSpecificItem(item, slot);
    }


    public boolean isDyed(CyberwareSlot slot, int index) {
        InstalledCyberware installed = get(slot, index);
        if (installed == null) return false;

        ItemStack st = installed.getItem();
        if (st == null || st.isEmpty()) return false;

        if (!(st.getItem() instanceof ICyberwareItem item)) return false;

        return item.isDyed(st, slot);
    }

    public int dyeColor(CyberwareSlot slot, int index) {
        InstalledCyberware installed = get(slot, index);
        if (installed == null) return 0xFFFFFFFF;

        ItemStack st = installed.getItem();
        if (st == null || st.isEmpty()) return 0xFFFFFFFF;

        if (!(st.getItem() instanceof ICyberwareItem item)) return 0xFFFFFFFF;

        return item.dyeColor(st, slot);
    }

    public boolean isDyed(Item item, CyberwareSlot slotToCheck) {
        InstalledCyberware[] arr = slots.get(slotToCheck);
        if (arr == null) return false;

        for (int i = 0; i < arr.length; i++) {
            InstalledCyberware installed = arr[i];
            if (installed == null) continue;

            ItemStack st = installed.getItem();
            if (st == null || st.isEmpty()) continue;

            if (!st.is(item)) continue;

            if (st.getItem() instanceof ICyberwareItem cw) {
                return cw.isDyed(st, slotToCheck);
            }
            return false;
        }
        return false;
    }

    public int dyeColor(Item item, CyberwareSlot slotToCheck) {
        InstalledCyberware[] arr = slots.get(slotToCheck);
        if (arr == null) return 0xFFFFFFFF;

        for (int i = 0; i < arr.length; i++) {
            InstalledCyberware installed = arr[i];
            if (installed == null) continue;

            ItemStack st = installed.getItem();
            if (st == null || st.isEmpty()) continue;

            if (!st.is(item)) continue;

            if (st.getItem() instanceof ICyberwareItem cw) {
                return cw.dyeColor(st, slotToCheck);
            }
            return 0xFFFFFFFF;
        }
        return 0xFFFFFFFF;
    }

    /* ---------------- SPINAL INJECTOR INVENTORY ---------------- */

    public ItemStack getSpinalInjectorStack(int slot) {
        if (slot < 0 || slot >= spinalInjectorInv.length) return ItemStack.EMPTY;
        ItemStack st = spinalInjectorInv[slot];
        return st == null ? ItemStack.EMPTY : st;
    }

    public void setSpinalInjectorStack(int slot, ItemStack stack) {
        if (slot < 0 || slot >= spinalInjectorInv.length) return;

        if (stack == null || stack.isEmpty() || !SpinalInjectorItem.isInjectable(stack)) {
            spinalInjectorInv[slot] = ItemStack.EMPTY;
            dirty = true;
            return;
        }

        ItemStack copy = stack.copy();
        int cap = SpinalInjectorItem.maxStackFor(copy);
        if (copy.getCount() > cap) copy.setCount(cap);

        spinalInjectorInv[slot] = copy;
        dirty = true;
    }

    public void clearSpinalInjectorInventory() {
        for (int i = 0; i < spinalInjectorInv.length; i++) {
            spinalInjectorInv[i] = ItemStack.EMPTY;
        }
        dirty = true;
    }

    /* ---------------- ARM CANNON 4-SLOT INVENTORY ---------------- */

    public ItemStack getArmCannonStack(int slot) {
        if (slot < 0 || slot >= armCannonInv.length) return ItemStack.EMPTY;
        ItemStack st = armCannonInv[slot];
        return st == null ? ItemStack.EMPTY : st;
    }

    public void setArmCannonStack(int slot, ItemStack stack) {
        if (slot < 0 || slot >= armCannonInv.length) return;

        if (stack == null || stack.isEmpty() || !ArmCannonItem.isValidStoredItem(stack)) {
            armCannonInv[slot] = ItemStack.EMPTY;
            dirty = true;
            return;
        }

        ItemStack copy = stack.copy();

        int cap = Math.max(1, copy.getMaxStackSize());
        if (copy.getCount() > cap) copy.setCount(cap);

        armCannonInv[slot] = copy;
        dirty = true;
    }

    public void clearArmCannonInventory() {
        for (int i = 0; i < armCannonInv.length; i++) {
            armCannonInv[i] = ItemStack.EMPTY;
        }
        dirty = true;
    }

    @Override
    public void clear() {
        for (CyberwareSlot slot : CyberwareSlot.values()) {
            slots.put(slot, new InstalledCyberware[slot.size]);

            boolean[] en = enabled.get(slot);
            if (en == null || en.length != slot.size) {
                en = new boolean[slot.size];
                enabled.put(slot, en);
            }
            for (int i = 0; i < en.length; i++) en[i] = true;
        }

        for (int i = 0; i < spinalInjectorInv.length; i++) {
            spinalInjectorInv[i] = ItemStack.EMPTY;
        }
        for (int i = 0; i < armCannonInv.length; i++) {
            armCannonInv[i] = ItemStack.EMPTY;
        }
        for (int i = 0; i < chipwareInv.length; i++) {
            chipwareInv[i] = ItemStack.EMPTY;
        }

        armCannonSelected = 0;
        copernicusOxygen = 0;
        copernicusOxygenSecondTicker = 0;

        dirty = true;
    }

    /* ---------------- COPERNICUS BREATHING STUFF ---------------- */

    public int getCopernicusOxygen() {
        return Math.max(0, copernicusOxygen);
    }

    public void setCopernicusOxygen(int value, int maxDisplayUnits) {
        int max = Math.max(0, maxDisplayUnits);
        int clamped = Mth.clamp(value, 0, max);
        if (clamped != copernicusOxygen) {
            copernicusOxygen = clamped;
            dirty = true;
        }
    }

    public boolean getCopernicusOxygenatedEnvironment() {
        return copernicusOxygenatedEnvironment;
    }

    public void setCopernicusOxygenatedEnvironment(boolean oxygenated) {
        copernicusOxygenatedEnvironment = oxygenated;
    }

    public void tickCopernicusOxygen(boolean oxygenatedEnvironment, int depletionPerSecond, int rechargePerSecond, int maxDisplayUnits) {
        copernicusOxygenSecondTicker++;
        if (copernicusOxygenSecondTicker < 20) return;
        copernicusOxygenSecondTicker = 0;

        int oxy = getCopernicusOxygen();

        if (oxygenatedEnvironment) {
            oxy = Math.min(maxDisplayUnits, oxy + Math.max(0, rechargePerSecond));
        } else {
            oxy = Math.max(0, oxy - Math.max(0, depletionPerSecond));
        }

        setCopernicusOxygen(oxy, maxDisplayUnits);
    }

    /* ---------------- DATA AND RESET ---------------- */

    @Override
    public void setDirty() {
        dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clean() {
        dirty = false;
    }

    public void resetToDefaultOrgans() {
        for (CyberwareSlot slot : CyberwareSlot.values()) {

            InstalledCyberware[] arr = getAll().get(slot);
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {

                ItemStack def = DefaultOrgans.get(slot, i);

                if (def == null || def.isEmpty()) {
                    arr[i] = null;
                    setEnabled(slot, i, true);
                    continue;
                }

                int humanityCost = 0;

                if (def.getItem() instanceof ICyberwareItem cyberwareItem) {
                    humanityCost = cyberwareItem.getHumanityCost();
                }

                arr[i] = new InstalledCyberware(def, slot, i, humanityCost);
                arr[i].setPowered(true);
                setEnabled(slot, i, true);
            }
        }

        dirty = true;
    }

    /* ---------------- ENERGY HELPERS ---------------- */

    public int getEnergyStored() {
        return energyStored;
    }

    public void setEnergyStored(Player player, int value) {
        int cap = getTotalEnergyCapacity(player);
        int clamped = Mth.clamp(value, 0, cap);
        if (clamped != this.energyStored) {
            this.energyStored = clamped;
            dirty = true;
        }
    }

    public int getTotalEnergyCapacity(Player player) {
        int total = 0;

        for (var entry : slots.entrySet()) {
            CyberwareSlot slot = entry.getKey();
            InstalledCyberware[] arr = entry.getValue();
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {
                InstalledCyberware cw = arr[i];
                if (cw == null) continue;

                ItemStack stack = cw.getItem();
                if (stack == null || stack.isEmpty()) continue;

                if (!(stack.getItem() instanceof ICyberwareItem item)) continue;

                int cap = item.getEnergyCapacity(player, stack, slot);
                if (cap > 0) total += cap;
            }
        }

        return Math.max(0, total);
    }

    public int receiveEnergy(Player player, int amount) {
        if (amount <= 0) return 0;

        int cap = getTotalEnergyCapacity(player);
        if (cap <= 0) return 0;

        int before = energyStored;
        int after = Mth.clamp(before + amount, 0, cap);

        if (after != before) {
            energyStored = after;
            dirty = true;
        }

        return after - before;
    }

    public int extractEnergy(int amount) {
        if (amount <= 0) return 0;

        int before = energyStored;
        int after = Math.max(0, before - amount);

        if (after != before) {
            energyStored = after;
            dirty = true;
        }

        return before - after;
    }

    public boolean tryConsumeEnergy(int amount) {
        if (amount <= 0) return true;
        if (energyStored < amount) return false;
        energyStored -= amount;
        dirty = true;
        return true;
    }

    public void clampEnergyToCapacity(Player player) {
        int cap = getTotalEnergyCapacity(player);
        int clamped = Mth.clamp(energyStored, 0, cap);
        if (clamped != energyStored) {
            energyStored = clamped;
            dirty = true;
        }
    }

    public int getNeuropozyneApplyCount() {
        return Math.max(0, neuropozyneApplyCount);
    }

    public int incrementNeuropozyneApplyCount() {
        neuropozyneApplyCount = Math.max(0, neuropozyneApplyCount) + 1;
        dirty = true;
        return neuropozyneApplyCount;
    }

    public void resetNeuropozyneApplyCount() {
        neuropozyneApplyCount = 0;
        dirty = true;
    }

    /* ---------------- NBT ---------------- */
    private static CompoundTag cc$saveStackToCompound(HolderLookup.Provider provider, ItemStack stack) {
        Tag t = stack.save(provider);
        return (t instanceof CompoundTag ct) ? ct : new CompoundTag();
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();

        for (var entry : slots.entrySet()) {
            CyberwareSlot slot = entry.getKey();
            InstalledCyberware[] arr = entry.getValue();
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {
                InstalledCyberware installed = arr[i];
                if (installed == null) continue;

                ItemStack stack = installed.getItem();
                if (stack == null || stack.isEmpty()) continue;

                CompoundTag c = installed.save(provider);

                c.putString("SlotGroup", slot.name());
                c.putInt("Index", i);
                c.putBoolean("Enabled", isEnabled(slot, i));

                list.add(c);
            }
        }

        tag.put(NBT_CYBERWARE, list);
        tag.putInt(NBT_HUMANITY, humanity);
        tag.putInt(NBT_ENERGY, energyStored);

        // persist selected arm cannon ammo slot
        tag.putInt(NBT_ARM_CANNON_SELECTED, getArmCannonSelected());

        ListTag inj = new ListTag();
        for (int i = 0; i < spinalInjectorInv.length; i++) {
            CompoundTag c = new CompoundTag();
            ItemStack st = spinalInjectorInv[i];
            if (st != null && !st.isEmpty() && SpinalInjectorItem.isInjectable(st)) {
                ItemStack copy = st.copy();
                int cap = SpinalInjectorItem.maxStackFor(copy);
                if (copy.getCount() > cap) copy.setCount(cap);
                copy.save(provider, c);
            }
            inj.add(c);
        }
        tag.put(NBT_SPINAL_INJECTOR_INV, inj);

        ListTag arm = new ListTag();
        for (int i = 0; i < armCannonInv.length; i++) {
            CompoundTag c = new CompoundTag();
            ItemStack st = armCannonInv[i];

            if (st != null && !st.isEmpty()) {
                ItemStack copy = st.copy();
                int cap = Math.max(1, copy.getMaxStackSize());
                if (copy.getCount() > cap) copy.setCount(cap);
                copy.save(provider, c);
            }

            arm.add(c);
        }
        tag.put(NBT_ARM_CANNON_INV, arm);

        ListTag chip = new ListTag();
        for (int i = 0; i < chipwareInv.length; i++) {
            ItemStack st = chipwareInv[i];

            if (st != null && !st.isEmpty() && st.is(ModTags.Items.DATA_SHARDS)) {
                ItemStack copy = st.copy();
                copy.setCount(1);

                chip.add(cc$saveStackToCompound(provider, copy));
            } else {
                chip.add(new CompoundTag());
            }
        }
        tag.put(NBT_CHIPWARE_INV, chip);

        tag.putInt(NBT_NEUROPOZYNE_APPLY_COUNT, neuropozyneApplyCount);

        tag.putInt(NBT_COPERNICUS_OXYGEN, copernicusOxygen);

        return tag;
    }

    public void deserializeNBT(CompoundTag tag, HolderLookup.Provider provider) {
        clear();

        ListTag list = tag.getList(NBT_CYBERWARE, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag c = list.getCompound(i);

            if (!c.contains("SlotGroup", Tag.TAG_STRING)) continue;

            CyberwareSlot slot = CyberwareSlot.valueOf(c.getString("SlotGroup"));
            int index = c.getInt("Index");

            InstalledCyberware loaded = InstalledCyberware.load(c, provider);

            InstalledCyberware[] arr = slots.get(slot);
            if (arr == null) continue;
            if (index < 0 || index >= arr.length) continue;

            arr[index] = loaded;

            boolean en = c.contains("Enabled", Tag.TAG_BYTE) ? c.getBoolean("Enabled") : true;
            setEnabled(slot, index, en);
        }

        if (tag.contains(NBT_HUMANITY, Tag.TAG_INT)) {
            humanity = tag.getInt(NBT_HUMANITY);
        } else {
            humanity = ConfigValues.BASE_HUMANITY;
            recomputeHumanityBaseFromInstalled();
        }

        humanityBonus = 0;
        energyStored = tag.contains(NBT_ENERGY, Tag.TAG_INT) ? tag.getInt(NBT_ENERGY) : 0;

        armCannonSelected = tag.contains(NBT_ARM_CANNON_SELECTED, Tag.TAG_INT)
                ? Mth.clamp(tag.getInt(NBT_ARM_CANNON_SELECTED), 0, ArmCannonItem.SLOT_COUNT - 1)
                : 0;

        for (int i = 0; i < spinalInjectorInv.length; i++) spinalInjectorInv[i] = ItemStack.EMPTY;

        if (tag.contains(NBT_SPINAL_INJECTOR_INV, Tag.TAG_LIST)) {
            ListTag inj = tag.getList(NBT_SPINAL_INJECTOR_INV, Tag.TAG_COMPOUND);
            for (int i = 0; i < spinalInjectorInv.length && i < inj.size(); i++) {
                CompoundTag c = inj.getCompound(i);
                ItemStack st = ItemStack.parseOptional(provider, c);

                if (st.isEmpty() || !SpinalInjectorItem.isInjectable(st)) {
                    spinalInjectorInv[i] = ItemStack.EMPTY;
                    continue;
                }

                int cap = SpinalInjectorItem.maxStackFor(st);
                if (st.getCount() > cap) st.setCount(cap);

                spinalInjectorInv[i] = st;
            }
        }

        if (tag.contains(NBT_ARM_CANNON_INV, Tag.TAG_LIST)) {
            ListTag arm = tag.getList(NBT_ARM_CANNON_INV, Tag.TAG_COMPOUND);

            for (int i = 0; i < armCannonInv.length && i < arm.size(); i++) {
                CompoundTag c = arm.getCompound(i);
                ItemStack st = ItemStack.parseOptional(provider, c);

                if (st.isEmpty()) {
                    armCannonInv[i] = ItemStack.EMPTY;
                    continue;
                }

                int cap = Math.max(1, st.getMaxStackSize());
                if (st.getCount() > cap) st.setCount(cap);

                armCannonInv[i] = st;
            }
        }

        for (int i = 0; i < chipwareInv.length; i++) chipwareInv[i] = ItemStack.EMPTY;

        if (tag.contains(NBT_CHIPWARE_INV, Tag.TAG_LIST)) {
            ListTag chip = tag.getList(NBT_CHIPWARE_INV, Tag.TAG_COMPOUND);
            for (int i = 0; i < chipwareInv.length && i < chip.size(); i++) {
                CompoundTag c = chip.getCompound(i);
                ItemStack st = ItemStack.parseOptional(provider, c);

                if (st.isEmpty() || !st.is(ModTags.Items.DATA_SHARDS)) {
                    chipwareInv[i] = ItemStack.EMPTY;
                    continue;
                }

                st.setCount(1);
                chipwareInv[i] = st;
            }
        }

        neuropozyneApplyCount = tag.contains(NBT_NEUROPOZYNE_APPLY_COUNT, Tag.TAG_INT)
                ? Math.max(0, tag.getInt(NBT_NEUROPOZYNE_APPLY_COUNT))
                : 0;

        copernicusOxygen = tag.contains(NBT_COPERNICUS_OXYGEN, Tag.TAG_INT)
                ? Math.max(0, tag.getInt(NBT_COPERNICUS_OXYGEN))
                : 0;

        dirty = false;
    }

    /* ---------------- COMMANDS ---------------- */

    public boolean commandInstall(Player player, ItemStack stack) {
        if (player == null) return false;
        if (stack == null || stack.isEmpty()) return false;

        Item item = stack.getItem();
        if (!(item instanceof ICyberwareItem cw)) return false;

        ItemStack installStack = stack.copy();
        installStack.setCount(1);

        InstallTarget target = findInstallTargetForCommand(installStack, cw);
        if (target == null) return false;

        if (cw.replacesOrgan()) {
            for (CyberwareSlot replaced : cw.getReplacedOrgans()) {
                InstalledCyberware removedAny = removeFirstNonEmptyInSlotGroup(player, replaced);
            }
        }

        int humanityCost = cw.getHumanityCost();
        InstalledCyberware installed = new InstalledCyberware(installStack, target.slot, target.index, humanityCost);
        installed.setPowered(true);

        set(target.slot, target.index, installed);

        cw.onInstalled(player);

        recomputeHumanityBaseFromInstalled();
        clampEnergyToCapacity(player);

        return true;
    }

    public boolean commandRemove(Player player, Item item) {
        if (player == null) return false;
        if (item == null) return false;

        for (CyberwareSlot slot : CyberwareSlot.values()) {
            InstalledCyberware[] arr = slots.get(slot);
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {
                InstalledCyberware installed = arr[i];
                if (installed == null) continue;

                ItemStack st = installed.getItem();
                if (st == null || st.isEmpty()) continue;
                if (!st.is(item)) continue;

                remove(slot, i);

                if (item instanceof ICyberwareItem cw) {
                    cw.onRemoved(player);
                }

                recomputeHumanityBaseFromInstalled();
                clampEnergyToCapacity(player);
                return true;
            }
        }

        return false;
    }

    public Component commandListComponent() {
        MutableComponent root = Component.literal("Installed Implants:");

        boolean any = false;

        for (CyberwareSlot slot : CyberwareSlot.values()) {
            InstalledCyberware[] arr = slots.get(slot);
            if (arr == null) continue;

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < arr.length; i++) {
                InstalledCyberware installed = arr[i];
                if (installed == null) continue;

                ItemStack st = installed.getItem();
                if (st == null || st.isEmpty()) continue;

                ItemStack def = DefaultOrgans.get(slot, i);
                if (def != null && !def.isEmpty() && ItemStack.isSameItemSameComponents(st, def)) {
                    continue;
                }

                if (sb.length() > 0) sb.append(", ");
                sb.append(st.getHoverName().getString());
            }

            if (sb.isEmpty()) continue;

            any = true;
            root.append(Component.literal("\n" + slot.name() + ": " + sb));
        }

        if (!any) {
            root.append(Component.literal("\n(X)"));
        }

        return root;
    }

    /* ---------------- internal helpers for commands ---------------- */

    private record InstallTarget(CyberwareSlot slot, int index) {}

    private InstallTarget findInstallTargetForCommand(ItemStack stack, ICyberwareItem cw) {
        if (cw.replacesOrgan()) {
            for (CyberwareSlot replaced : cw.getReplacedOrgans()) {
                InstallTarget t = findFirstValidSpaceInSlotGroup(stack, cw, replaced);
                if (t != null) return t;
            }
        }

        for (CyberwareSlot slot : CyberwareSlot.values()) {
            if (!cw.supportsSlot(slot)) continue;

            InstallTarget t = findFirstValidSpaceInSlotGroup(stack, cw, slot);
            if (t != null) return t;
        }

        return null;
    }

    private InstallTarget findFirstValidSpaceInSlotGroup(ItemStack incoming, ICyberwareItem cw, CyberwareSlot slot) {
        InstalledCyberware[] arr = slots.get(slot);
        if (arr == null) return null;

        int max = Math.max(1, cw.maxStacksPerSlotType(incoming, slot));
        int already = 0;

        for (InstalledCyberware installed : arr) {
            if (installed == null) continue;
            ItemStack st = installed.getItem();
            if (st == null || st.isEmpty()) continue;
            if (ItemStack.isSameItemSameComponents(st, incoming)) already++;
        }
        if (already >= max) return null;

        for (int i = 0; i < arr.length; i++) {
            InstalledCyberware installed = arr[i];
            if (installed == null || installed.getItem() == null || installed.getItem().isEmpty()) {
                return new InstallTarget(slot, i);
            }
        }

        return null;
    }

    private InstalledCyberware removeFirstNonEmptyInSlotGroup(Player player, CyberwareSlot slot) {
        InstalledCyberware[] arr = slots.get(slot);
        if (arr == null) return null;

        for (int i = 0; i < arr.length; i++) {
            InstalledCyberware installed = arr[i];
            if (installed == null) continue;

            ItemStack st = installed.getItem();
            if (st == null || st.isEmpty()) continue;

            InstalledCyberware removed = remove(slot, i);

            Item removedItem = (st != null) ? st.getItem() : null;
            if (removedItem instanceof ICyberwareItem cw) {
                cw.onRemoved(player);
            }

            return removed;
        }

        return null;
    }
}
