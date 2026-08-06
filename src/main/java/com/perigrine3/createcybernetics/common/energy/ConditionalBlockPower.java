package com.perigrine3.createcybernetics.common.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.Set;

public final class ConditionalBlockPower {
    private ConditionalBlockPower() {
    }

    //List of mods that use alt energy (FE & RF)
    private static final Set<String> TECH_ENERGY_MOD_IDS = Set.of(
            "mekanism",
            "thermal",
            "immersiveengineering",
            "powah",
            "enderio",
            "rftoolsbase",
            "rftoolspower",
            "fluxnetworks",
            "industrialforegoing",
            "ae2",
            "createaddition",
            "create_new_age",
            "modern_industrialization",
            "powergrid",
            "oritech",
            "industrialupgrade",
            "electroenergetics"
    );

    public static boolean hasTechEnergyModLoaded() {
        ModList modList = ModList.get();

        for (String modId : TECH_ENERGY_MOD_IDS) {
            if (modList.isLoaded(modId)) {
                return true;
            }
        }

        return false;
    }

    public static boolean shouldUseEnergyInsteadOfRedstone() {
        return hasTechEnergyModLoaded();
    }

    public static boolean hasRedstonePower(Level level, BlockPos pos) {
        return level.hasNeighborSignal(pos);
    }

    public static boolean hasRequiredPower(Level level, BlockPos pos, IEnergyStorage energyStorage, int requiredEnergy) {
        if (shouldUseEnergyInsteadOfRedstone()) {
            return energyStorage != null && energyStorage.getEnergyStored() >= requiredEnergy;
        }

        return hasRedstonePower(level, pos);
    }

    public static boolean consumeRequiredPower(Level level, BlockPos pos, IEnergyStorage energyStorage, int requiredEnergy) {
        if (shouldUseEnergyInsteadOfRedstone()) {
            return energyStorage != null && energyStorage.extractEnergy(requiredEnergy, false) == requiredEnergy;
        }

        return hasRedstonePower(level, pos);
    }
}