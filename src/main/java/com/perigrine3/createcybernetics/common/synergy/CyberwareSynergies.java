package com.perigrine3.createcybernetics.common.synergy;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class CyberwareSynergies {

    private CyberwareSynergies() {}

    public static final List<Synergy> ALL = new ArrayList<>();

    /* ------------------------------- API ------------------------------- */

    public interface Requirement {
        boolean test(Player player, PlayerCyberwareData data);
    }

    public record Synergy(ResourceLocation id, List<Requirement> requirements, List<String> modifierIds) {
        public boolean isActive(Player player, PlayerCyberwareData data) {
            for (Requirement r : requirements) {
                if (!r.test(player, data)) return false;
            }
            return true;
        }

        public void apply(Player player) {
            for (String modId : modifierIds) {
                CyberwareAttributeHelper.applyModifier(player, modId);
            }
        }

        public void remove(Player player) {
            for (String modId : modifierIds) {
                CyberwareAttributeHelper.removeModifier(player, modId);
            }
        }
    }

    public static void register(Synergy synergy) {
        ALL.add(synergy);
    }

    /* -------------------------- Requirements --------------------------- */

    public static Requirement installed(Supplier<? extends Item> item) {
        return (player, data) -> countInstalledItem(data, item.get()) > 0;
    }

    public static Requirement installedCount(Supplier<? extends Item> item, int count) {
        return (player, data) -> countInstalledItem(data, item.get()) >= count;
    }

    public static Requirement installedTag(TagKey<Item> tag) {
        return (player, data) -> countInstalledTag(data, tag) > 0;
    }

    public static Requirement activePowered(Supplier<? extends Item> item) {
        return (player, data) -> countActivePoweredItem(player, data, item.get()) > 0;
    }

    /* ------------------------- Scan helpers ---------------------------- */

    private static int countInstalledItem(PlayerCyberwareData data, Item item) {
        int found = 0;
        for (var entry : data.getAll().entrySet()) {
            InstalledCyberware[] arr = entry.getValue();
            if (arr == null) continue;

            for (InstalledCyberware cw : arr) {
                if (cw == null) continue;
                ItemStack st = cw.getItem();
                if (st == null || st.isEmpty()) continue;
                if (st.is(item)) found++;
            }
        }
        return found;
    }

    private static int countInstalledTag(PlayerCyberwareData data, TagKey<Item> tag) {
        int found = 0;
        for (var entry : data.getAll().entrySet()) {
            InstalledCyberware[] arr = entry.getValue();
            if (arr == null) continue;

            for (InstalledCyberware cw : arr) {
                if (cw == null) continue;
                ItemStack st = cw.getItem();
                if (st == null || st.isEmpty()) continue;
                if (st.is(tag)) found++;
            }
        }
        return found;
    }

    private static int countActivePoweredItem(Player player, PlayerCyberwareData data, Item item) {
        int found = 0;

        for (var entry : data.getAll().entrySet()) {
            var slot = entry.getKey();
            InstalledCyberware[] arr = entry.getValue();
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {
                InstalledCyberware cw = arr[i];
                if (cw == null) continue;

                ItemStack st = cw.getItem();
                if (st == null || st.isEmpty()) continue;
                if (!st.is(item)) continue;

                if (st.getItem() instanceof ICyberwareItem cyberItem) {
                    if (cyberItem.isToggleableByWheel(st, slot) && !data.isEnabled(slot, i)) continue;
                }

                if (!cw.isPowered()) continue;

                found++;
            }
        }

        return found;
    }

    /* ---------------------- Define your synergies ---------------------- */

    static {

        //register(new Synergy(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "dragoon_set"),
        //        List.of(
        //                installed(ModItems.BASECYBERWARE_LEFTARM), installed(ModItems.BASECYBERWARE_RIGHTARM),
        //                installed(ModItems.BASECYBERWARE_LEFTLEG), installed(ModItems.BASECYBERWARE_RIGHTLEG),
        //                installed(ModItems.SKINUPGRADES_METALPLATING),
        //                installed(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE),
        //                installed(ModItems.HEARTUPGRADES_CYBERHEART),
        //                installed(ModItems.HEARTUPGRADES_CYBERHEART),
        //                installed(ModItems.BASECYBERWARE_LINEARFRAME),
        //                installed(ModItems.BASECYBERWARE_CYBEREYES),
        //                installedCount(ModItems.BONEUPGRADES_BONELACING, 3),
        //                installedCount(ModItems.ARMUPGRADES_PNEUMATICWRIST, 2),
        //                installedCount(ModItems.LEGUPGRADES_JUMPBOOST, 2),
        //                installed(ModItems.ARMUPGRADES_ARMCANNON),
        //                installed(ModItems.EYEUPGRADES_TARGETING),
        //                installed(ModItems.BRAINUPGRADES_MATRIX),
        //                installed(ModItems.BONEUPGRADES_SANDEVISTAN)
        //        ),
        //        List.of("dragoon_size")
        //));
    }
}
