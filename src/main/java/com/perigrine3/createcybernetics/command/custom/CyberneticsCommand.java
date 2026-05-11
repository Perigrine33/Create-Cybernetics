package com.perigrine3.createcybernetics.command.custom;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.perigrine3.createcybernetics.ConfigValues;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.EntityCyberwareData;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.ModMobAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.common.surgery.DefaultOrgans;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public final class CyberneticsCommand {
    private CyberneticsCommand() {}

    private static final String KEY_KEEP_QUERY = "command.createcybernetics.keep_cyberware.query";
    private static final String KEY_KEEP_SET = "command.createcybernetics.keep_cyberware.set";

    private static final String KEY_INVALID_ENTITY = "commands.createcybernetics.implants.invalid_entity_target";
    private static final String KEY_WRONG_ITEM = "commands.createcybernetics.implants.wrong_item";
    private static final String KEY_NO_CYBERWARE = "commands.createcybernetics.implants.no_cyberware";
    private static final String KEY_INSTALL_FAIL = "commands.createcybernetics.implants.install_fail";
    private static final String KEY_INSTALL_OK = "commands.createcybernetics.implants.install_success";
    private static final String KEY_REMOVE_FAIL = "commands.createcybernetics.implants.remove_fail";
    private static final String KEY_REMOVE_OK = "commands.createcybernetics.implants.remove_success";
    private static final String KEY_CLEAR_OK = "commands.createcybernetics.implants.clear_success";

    private static final String PKEY_ENERGY_DEBUG = "cc_energy_debug_enabled";
    private static final String KEY_ENERGY_DEBUG_SET = "commands.createcybernetics.energy_debug.set";

    private static final String[] FBC_NAMES = {
            "gemini",
            "samson",
            "eclipse",
            "spyder",
            "wingman",
            "aquarius",
            "dymond",
            "dragoon",
            "copernicus",
            "genos",
            "kildare"
    };

    private static final SuggestionProvider<CommandSourceStack> CYBERWARE_ITEM_SUGGESTIONS =
            (context, builder) -> SharedSuggestionProvider.suggestResource(
                    BuiltInRegistries.ITEM.entrySet().stream()
                            .filter(e -> CreateCybernetics.MODID.equals(e.getKey().location().getNamespace()))
                            .filter(e -> e.getValue() instanceof ICyberwareItem)
                            .map(e -> e.getKey().location())
                            .sorted(), builder);

    private static final SuggestionProvider<CommandSourceStack> FBC_SUGGESTIONS =
            (context, builder) -> SharedSuggestionProvider.suggest(FBC_NAMES, builder);

    private record FbcRequirement(Supplier<? extends Item> item, int count, CyberwareSlot... slots) {}

    private record InstallPlacement(CyberwareSlot slot, int index, boolean replacingDefault) {}

    private static boolean isCreateCyberneticsCyberwareItem(Item item) {
        if (!(item instanceof ICyberwareItem)) return false;
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
        return key != null && CreateCybernetics.MODID.equals(key.getNamespace());
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx) {
        dispatcher.register(Commands.literal("cybernetics").requires(src -> src.hasPermission(2))

                .then(Commands.literal("implants")

                        .then(Commands.literal("install")
                                .then(Commands.argument("target", EntityArgument.entity())
                                        .then(Commands.argument("item", ItemArgument.item(ctx))
                                                .suggests(CYBERWARE_ITEM_SUGGESTIONS)
                                                .executes(c -> {
                                                    Entity target = EntityArgument.getEntity(c, "target");
                                                    Item item = ItemArgument.getItem(c, "item").getItem();
                                                    return install(c.getSource(), target, item);
                                                })
                                        )
                                )
                        )

                        .then(Commands.literal("remove")
                                .then(Commands.argument("target", EntityArgument.entity())
                                        .then(Commands.argument("item", ItemArgument.item(ctx))
                                                .suggests(CYBERWARE_ITEM_SUGGESTIONS)
                                                .executes(c -> {
                                                    Entity target = EntityArgument.getEntity(c, "target");
                                                    Item item = ItemArgument.getItem(c, "item").getItem();
                                                    return remove(c.getSource(), target, item);
                                                })
                                        )
                                )
                        )

                        .then(Commands.literal("list")
                                .then(Commands.argument("target", EntityArgument.entity())
                                        .executes(c -> {
                                            Entity target = EntityArgument.getEntity(c, "target");
                                            return list(c.getSource(), target);
                                        })
                                )
                        )

                        .then(Commands.literal("clear")
                                .then(Commands.argument("target", EntityArgument.entity())
                                        .executes(c -> {
                                            Entity target = EntityArgument.getEntity(c, "target");
                                            return clear(c.getSource(), target);
                                        })
                                )
                        )

                        .then(Commands.literal("fbc")
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .suggests(FBC_SUGGESTIONS)
                                        .executes(c -> installFbc(
                                                c.getSource(),
                                                StringArgumentType.getString(c, "name")
                                        ))
                                )
                        )
                )

                .then(Commands.literal("keepCyberware")
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(c -> {
                                    boolean value = BoolArgumentType.getBool(c, "value");
                                    ConfigValues.KEEP_CYBERWARE = value;

                                    Component state = Component.translatable(value ? "options.on" : "options.off");
                                    c.getSource().sendSuccess(
                                            () -> Component.translatable(KEY_KEEP_SET, state),
                                            true);
                                    return 1;
                                })
                        )
                )

                .then(Commands.literal("energyDebug")
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(c -> {
                                    ServerPlayer target = c.getSource().getPlayerOrException();
                                    boolean value = BoolArgumentType.getBool(c, "value");
                                    setEnergyDebug(target, value);

                                    Component state = Component.translatable(value ? "options.on" : "options.off");
                                    c.getSource().sendSuccess(
                                            () -> Component.translatable(KEY_ENERGY_DEBUG_SET, state),
                                            false);
                                    return 1;
                                })
                        )
                )
        );
    }

    private static int installFbc(CommandSourceStack src, String rawName) {
        ServerPlayer player;

        try {
            player = src.getPlayerOrException();
        } catch (Exception e) {
            src.sendFailure(Component.literal("This command must be run as a player."));
            return 0;
        }

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) {
            src.sendFailure(Component.translatable(KEY_NO_CYBERWARE));
            return 0;
        }

        String name = rawName == null ? "" : rawName.toLowerCase(Locale.ROOT);
        List<FbcRequirement> requirements = fbcRequirements(name);

        if (requirements == null) {
            src.sendFailure(Component.literal("Unknown FBC: " + rawName));
            return 0;
        }

        if (requirements.isEmpty()) {
            src.sendFailure(Component.literal("FBC is unavailable: " + rawName));
            return 0;
        }

        String invalid = validateFbcRequirements(requirements);
        if (invalid != null) {
            src.sendFailure(Component.literal("FBC " + name + " has an invalid requirement: " + invalid));
            return 0;
        }

        int installed = 0;

        for (FbcRequirement requirement : requirements) {
            Item item = requirement.item().get();

            int alreadyInstalled = countInstalled(data, item, requirement.slots());
            int missing = Math.max(0, requirement.count() - alreadyInstalled);

            for (int i = 0; i < missing; i++) {
                ItemStack stack = new ItemStack(item);

                boolean ok = installFbcStackRecipeSafe(player, data, stack, requirement.slots());

                if (!ok) {
                    finishPlayerCyberwareUpdate(player, data);

                    src.sendFailure(Component.literal(
                            "Failed to install " + stack.getHoverName().getString()
                                    + " for FBC " + name
                                    + ". Missing after install attempt: "
                                    + describeMissingFbcRequirements(data, requirements)
                    ));
                    return 0;
                }

                installed++;
            }
        }

        finishPlayerCyberwareUpdate(player, data);

        if (!allFbcRequirementsInstalled(data, requirements)) {
            src.sendFailure(Component.literal(
                    "FBC " + name + " was only partially installed. Missing: "
                            + describeMissingFbcRequirements(data, requirements)
            ));
            return 0;
        }

        final int installedCount = installed;
        final String fbcName = name;
        final Component playerName = player.getDisplayName();

        src.sendSuccess(() -> Component.literal(
                "Installed FBC " + fbcName + " on " + playerName.getString()
                        + " (" + installedCount + " implant" + (installedCount == 1 ? "" : "s") + ")."
        ), false);

        return 1;
    }

    private static boolean installFbcStackRecipeSafe(ServerPlayer player, PlayerCyberwareData data, ItemStack stack, CyberwareSlot... allowedSlots) {
        if (player == null) return false;
        if (data == null) return false;
        if (stack == null || stack.isEmpty()) return false;
        if (allowedSlots == null || allowedSlots.length == 0) return false;

        Item item = stack.getItem();
        if (!(item instanceof ICyberwareItem cyberwareItem)) return false;

        int before = countInstalled(data, item, allowedSlots);

        ItemStack installStack = stack.copy();
        installStack.setCount(1);

        InstallPlacement placement = findFbcRecipePlacement(data, installStack, allowedSlots);
        if (placement == null) return false;

        if (placement.replacingDefault()) {
            InstalledCyberware existing = data.get(placement.slot(), placement.index());
            if (existing != null) {
                ItemStack existingStack = existing.getItem();
                if (existingStack != null && !existingStack.isEmpty() && existingStack.getItem() instanceof ICyberwareItem existingCyberware) {
                    existingCyberware.onRemoved(player);
                }
            }
        }

        int humanityCost = cyberwareItem.getHumanityCost();

        InstalledCyberware installed = new InstalledCyberware(
                installStack,
                placement.slot(),
                placement.index(),
                humanityCost
        );
        installed.setPowered(true);

        data.set(placement.slot(), placement.index(), installed);
        cyberwareItem.onInstalled(player, installed.getItem());

        data.recomputeHumanityBaseFromInstalled(player);
        data.setDirty();

        int after = countInstalled(data, item, allowedSlots);
        return after > before;
    }

    private static InstallPlacement findFbcRecipePlacement(PlayerCyberwareData data, ItemStack incoming, CyberwareSlot... allowedSlots) {
        if (data == null) return null;
        if (incoming == null || incoming.isEmpty()) return null;

        InstallPlacement empty = findEmptyPlacement(data, allowedSlots);
        if (empty != null) {
            return empty;
        }

        return findDefaultReplacementPlacement(data, allowedSlots);
    }

    private static InstallPlacement findEmptyPlacement(PlayerCyberwareData data, CyberwareSlot... allowedSlots) {
        if (data == null || allowedSlots == null) return null;

        for (CyberwareSlot slot : allowedSlots) {
            if (slot == null) continue;

            InstalledCyberware[] arr = data.getAll().get(slot);
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {
                InstalledCyberware existing = data.get(slot, i);

                if (existing == null || existing.getItem() == null || existing.getItem().isEmpty()) {
                    return new InstallPlacement(slot, i, false);
                }
            }
        }

        return null;
    }

    private static InstallPlacement findDefaultReplacementPlacement(PlayerCyberwareData data, CyberwareSlot... allowedSlots) {
        if (data == null || allowedSlots == null) return null;

        for (CyberwareSlot slot : allowedSlots) {
            if (slot == null) continue;

            InstalledCyberware[] arr = data.getAll().get(slot);
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {
                InstalledCyberware existing = data.get(slot, i);
                if (existing == null) continue;

                ItemStack existingStack = existing.getItem();
                if (existingStack == null || existingStack.isEmpty()) continue;

                if (isDefaultOrganStack(slot, i, existingStack)) {
                    return new InstallPlacement(slot, i, true);
                }
            }
        }

        return null;
    }

    private static boolean isDefaultOrganStack(CyberwareSlot slot, int index, ItemStack stack) {
        if (slot == null) return false;
        if (stack == null || stack.isEmpty()) return false;

        ItemStack def = DefaultOrgans.get(slot, index);
        if (def == null || def.isEmpty()) return false;

        return ItemStack.isSameItemSameComponents(stack, def);
    }

    private static String validateFbcRequirements(List<FbcRequirement> requirements) {
        for (FbcRequirement requirement : requirements) {
            if (requirement == null) {
                return "null requirement";
            }

            if (requirement.item() == null) {
                return "null item supplier";
            }

            Item item = requirement.item().get();
            if (item == null) {
                return "null item";
            }

            if (!isCreateCyberneticsCyberwareItem(item)) {
                return item.getDescription().getString() + " is not a Create Cybernetics cyberware item";
            }

            if (requirement.count() <= 0) {
                return item.getDescription().getString() + " has invalid count " + requirement.count();
            }

            if (requirement.slots() == null || requirement.slots().length == 0) {
                return item.getDescription().getString() + " has no allowed slots";
            }

            int capacity = 0;
            for (CyberwareSlot slot : requirement.slots()) {
                if (slot != null) {
                    capacity += slot.size;
                }
            }

            if (requirement.count() > capacity) {
                return item.getDescription().getString() + " requires " + requirement.count()
                        + " but only has " + capacity + " allowed slot(s)";
            }
        }

        return null;
    }

    private static boolean allFbcRequirementsInstalled(PlayerCyberwareData data, List<FbcRequirement> requirements) {
        for (FbcRequirement requirement : requirements) {
            Item item = requirement.item().get();
            int installed = countInstalled(data, item, requirement.slots());

            if (installed < requirement.count()) {
                return false;
            }
        }

        return true;
    }

    private static String describeMissingFbcRequirements(PlayerCyberwareData data, List<FbcRequirement> requirements) {
        ArrayList<String> missing = new ArrayList<>();

        for (FbcRequirement requirement : requirements) {
            Item item = requirement.item().get();
            int installed = countInstalled(data, item, requirement.slots());
            int needed = requirement.count();

            if (installed >= needed) continue;

            missing.add(item.getDescription().getString() + " " + installed + "/" + needed);
        }

        return missing.isEmpty() ? "none" : String.join(", ", missing);
    }

    private static void finishPlayerCyberwareUpdate(ServerPlayer player, PlayerCyberwareData data) {
        data.recomputeHumanityBaseFromInstalled(player);
        data.setDirty();
        ModAttachments.syncCyberware(player);
        player.syncData(ModAttachments.CYBERWARE);
    }

    private static int countInstalled(PlayerCyberwareData data, Item item, CyberwareSlot... slots) {
        int count = 0;

        if (data == null || item == null || slots == null) {
            return 0;
        }

        for (CyberwareSlot slot : slots) {
            if (slot == null) continue;

            for (int i = 0; i < slot.size; i++) {
                InstalledCyberware installed = data.get(slot, i);
                if (installed == null || installed.getItem() == null || installed.getItem().isEmpty()) continue;

                if (installed.getItem().is(item)) {
                    count++;
                }
            }
        }

        return count;
    }

    private static List<FbcRequirement> fbcRequirements(String name) {
        return switch (name) {
            case "gemini" -> geminiRequirements();
            case "samson" -> samsonRequirements();
            case "eclipse" -> eclipseRequirements();
            case "spyder" -> spyderRequirements();
            case "wingman" -> wingmanRequirements();
            case "aquarius" -> aquariusRequirements();
            case "dymond" -> dymondRequirements();
            case "dragoon" -> dragoonRequirements();
            case "copernicus" -> copernicusRequirements();
            case "genos" -> genosRequirements();
            case "kildare" -> kildareRequirements();
            default -> null;
        };
    }

    private static ArrayList<FbcRequirement> baseRequirements(Supplier<? extends Item> skin) {
        ArrayList<FbcRequirement> list = new ArrayList<>();

        list.add(req(ModItems.ORGANSUPGRADES_MAGICCATALYST, CyberwareSlot.ORGANS));
        list.add(req(ModItems.BASECYBERWARE_RIGHTARM, CyberwareSlot.RARM));
        list.add(req(ModItems.BASECYBERWARE_LEFTARM, CyberwareSlot.LARM));
        list.add(req(ModItems.BASECYBERWARE_RIGHTLEG, CyberwareSlot.RLEG));
        list.add(req(ModItems.BASECYBERWARE_LEFTLEG, CyberwareSlot.LLEG));
        list.add(req(skin, CyberwareSlot.SKIN));
        list.add(req(ModItems.MUSCLEUPGRADES_SYNTHMUSCLE, CyberwareSlot.MUSCLE));
        list.add(req(ModItems.HEARTUPGRADES_CYBERHEART, CyberwareSlot.HEART));
        list.add(req(ModItems.BASECYBERWARE_LINEARFRAME, CyberwareSlot.BONE));
        list.add(req(ModItems.BASECYBERWARE_CYBEREYES, CyberwareSlot.EYES));

        return list;
    }

    private static List<FbcRequirement> geminiRequirements() {
        ArrayList<FbcRequirement> list = baseRequirements(ModItems.SKINUPGRADES_SYNTHSKIN);
        list.add(req(ModItems.BONEUPGRADES_BONELACING, CyberwareSlot.BONE));
        return list;
    }

    private static List<FbcRequirement> samsonRequirements() {
        ArrayList<FbcRequirement> list = baseRequirements(ModItems.SKINUPGRADES_METALPLATING);
        list.add(req(ModItems.BONEUPGRADES_BONELACING, 3, CyberwareSlot.BONE));
        list.add(req(ModItems.SKINUPGRADES_SUBDERMALARMOR, 3, CyberwareSlot.SKIN));
        list.add(req(ModItems.ARMUPGRADES_PNEUMATICWRIST, 2, CyberwareSlot.RARM, CyberwareSlot.LARM));
        return list;
    }

    private static List<FbcRequirement> eclipseRequirements() {
        ArrayList<FbcRequirement> list = baseRequirements(ModItems.SKINUPGRADES_METALPLATING);
        list.add(req(ModItems.BONEUPGRADES_BONELACING, CyberwareSlot.BONE));
        list.add(req(ModItems.LUNGSUPGRADES_HYPEROXYGENATION, 3, CyberwareSlot.LUNGS));
        list.add(req(ModItems.LEGUPGRADES_OCELOTPAWS, 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG));
        list.add(req(ModItems.SKINUPGRADES_CHROMATOPHORES, CyberwareSlot.SKIN));
        list.add(req(ModItems.BONEUPGRADES_SANDEVISTAN, CyberwareSlot.BONE));
        return list;
    }

    private static List<FbcRequirement> spyderRequirements() {
        ArrayList<FbcRequirement> list = baseRequirements(ModItems.SKINUPGRADES_METALPLATING);
        list.add(req(ModItems.BASECYBERWARE_CYBEREYES, 3, CyberwareSlot.EYES));
        list.add(req(ModItems.BONEUPGRADES_BONELACING, CyberwareSlot.BONE));
        list.add(req(ModItems.LEGUPGRADES_JUMPBOOST, 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG));
        list.add(req(ModItems.LEGUPGRADES_ANKLEBRACERS, 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG));
        list.add(req(ModItems.LEGUPGRADES_OCELOTPAWS, 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG));
        list.add(req(ModItems.SKINUPGRADES_CHROMATOPHORES, CyberwareSlot.SKIN));
        list.add(req(ModItems.SKINUPGRADES_SYNTHETICSETULES, CyberwareSlot.SKIN));
        return list;
    }

    private static List<FbcRequirement> wingmanRequirements() {
        if (ModItems.BONEUPGRADES_ELYTRA == null) {
            return List.of();
        }

        ArrayList<FbcRequirement> list = baseRequirements(ModItems.SKINUPGRADES_METALPLATING);
        list.add(req(ModItems.BONEUPGRADES_BONELACING, CyberwareSlot.BONE));
        list.add(req(ModItems.BONEUPGRADES_CYBERSKULL, CyberwareSlot.BONE));
        list.add(req(ModItems.BONEUPGRADES_ELYTRA, CyberwareSlot.BONE));
        list.add(req(ModItems.LEGUPGRADES_JUMPBOOST, 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG));
        return list;
    }

    private static List<FbcRequirement> aquariusRequirements() {
        ArrayList<FbcRequirement> list = baseRequirements(ModItems.SKINUPGRADES_METALPLATING);
        list.add(req(ModItems.BONEUPGRADES_BONELACING, CyberwareSlot.BONE));
        list.add(req(ModItems.LEGUPGRADES_PROPELLERS, 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG));
        list.add(req(ModItems.EYEUPGRADES_UNDERWATERVISION, CyberwareSlot.EYES));
        list.add(req(ModItems.LUNGSUPGRADES_OXYGEN, CyberwareSlot.LUNGS));
        list.add(req(ModItems.WETWARE_WATERBREATHINGLUNGS, CyberwareSlot.LUNGS));
        return list;
    }

    private static List<FbcRequirement> dymondRequirements() {
        ArrayList<FbcRequirement> list = baseRequirements(ModItems.SKINUPGRADES_METALPLATING);
        list.add(req(ModItems.BONEUPGRADES_BONELACING, CyberwareSlot.BONE));
        list.add(req(ModItems.ARMUPGRADES_PNEUMATICWRIST, 2, CyberwareSlot.RARM, CyberwareSlot.LARM));
        list.add(req(ModItems.LEGUPGRADES_ANKLEBRACERS, 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG));
        list.add(req(ModItems.LEGUPGRADES_METALDETECTOR, CyberwareSlot.RLEG, CyberwareSlot.LLEG));
        list.add(req(ModItems.BRAINUPGRADES_MATRIX, CyberwareSlot.BRAIN));
        list.add(req(ModItems.ARMUPGRADES_DRILLFIST, CyberwareSlot.RARM, CyberwareSlot.LARM));
        list.add(req(ModItems.ARMUPGRADES_REINFORCEDKNUCKLES, 2, CyberwareSlot.RARM, CyberwareSlot.LARM));
        return list;
    }

    private static List<FbcRequirement> dragoonRequirements() {
        ArrayList<FbcRequirement> list = baseRequirements(ModItems.SKINUPGRADES_METALPLATING);
        list.add(req(ModItems.BONEUPGRADES_BONELACING, 3, CyberwareSlot.BONE));
        list.add(req(ModItems.ARMUPGRADES_PNEUMATICWRIST, 2, CyberwareSlot.RARM, CyberwareSlot.LARM));
        list.add(req(ModItems.LEGUPGRADES_ANKLEBRACERS, 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG));
        list.add(req(ModItems.LEGUPGRADES_JUMPBOOST, 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG));
        list.add(req(ModItems.ARMUPGRADES_ARMCANNON, CyberwareSlot.RARM, CyberwareSlot.LARM));
        list.add(req(ModItems.EYEUPGRADES_TARGETING, CyberwareSlot.EYES));
        list.add(req(ModItems.BRAINUPGRADES_MATRIX, CyberwareSlot.BRAIN));
        list.add(req(ModItems.BONEUPGRADES_SANDEVISTAN, CyberwareSlot.BONE));
        return list;
    }

    private static List<FbcRequirement> copernicusRequirements() {
        ArrayList<FbcRequirement> list = baseRequirements(ModItems.SKINUPGRADES_METALPLATING);
        list.add(req(ModItems.LUNGSUPGRADES_OXYGEN, 3, CyberwareSlot.LUNGS));
        list.add(req(ModItems.SKINUPGRADES_SOLARSKIN, CyberwareSlot.SKIN));
        list.add(req(ModItems.SKINUPGRADES_NETHERITEPLATING, CyberwareSlot.SKIN));
        list.add(req(ModItems.EYEUPGRADES_ZOOM, CyberwareSlot.EYES));
        list.add(req(ModItems.EYEUPGRADES_HUDJACK, CyberwareSlot.EYES));
        list.add(req(ModItems.ARMUPGRADES_CRAFTHANDS, CyberwareSlot.LARM, CyberwareSlot.RARM));
        return list;
    }

    private static List<FbcRequirement> genosRequirements() {
        ArrayList<FbcRequirement> list = baseRequirements(ModItems.SKINUPGRADES_METALPLATING);
        list.add(req(ModItems.BONEUPGRADES_BONELACING, 2, CyberwareSlot.BONE));
        list.add(req(ModItems.ARMUPGRADES_PNEUMATICWRIST, 2, CyberwareSlot.RARM, CyberwareSlot.LARM));
        list.add(req(ModItems.LUNGSUPGRADES_HYPEROXYGENATION, 3, CyberwareSlot.LUNGS));
        list.add(req(ModItems.LEGUPGRADES_ANKLEBRACERS, 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG));
        list.add(req(ModItems.LEGUPGRADES_JUMPBOOST, 2, CyberwareSlot.RLEG, CyberwareSlot.LLEG));
        list.add(req(ModItems.ARMUPGRADES_ARMCANNON, CyberwareSlot.RARM, CyberwareSlot.LARM));
        list.add(req(ModItems.EYEUPGRADES_TARGETING, CyberwareSlot.EYES));
        list.add(req(ModItems.EYEUPGRADES_HUDJACK, CyberwareSlot.EYES));
        list.add(req(ModItems.MUSCLEUPGRADES_WIREDREFLEXES, CyberwareSlot.MUSCLE));
        list.add(req(ModItems.BRAINUPGRADES_MATRIX, CyberwareSlot.BRAIN));
        return list;
    }

    private static List<FbcRequirement> kildareRequirements() {
        ArrayList<FbcRequirement> list = baseRequirements(ModItems.SKINUPGRADES_METALPLATING);
        list.add(req(ModItems.EYEUPGRADES_ZOOM, CyberwareSlot.EYES));
        list.add(req(ModItems.EYEUPGRADES_HUDJACK, CyberwareSlot.EYES));
        list.add(req(ModItems.ARMUPGRADES_CRAFTHANDS, CyberwareSlot.LARM, CyberwareSlot.RARM));
        list.add(req(ModItems.ARMUPGRADES_RIPPERCLAW, CyberwareSlot.LARM, CyberwareSlot.RARM));
        return list;
    }

    private static FbcRequirement req(Supplier<? extends Item> item, CyberwareSlot... slots) {
        return new FbcRequirement(item, 1, slots);
    }

    private static FbcRequirement req(Supplier<? extends Item> item, int count, CyberwareSlot... slots) {
        return new FbcRequirement(item, count, slots);
    }

    private static int install(CommandSourceStack src, Entity target, Item item) {
        if (!isCreateCyberneticsCyberwareItem(item)) {
            src.sendFailure(Component.translatable(KEY_WRONG_ITEM));
            return 0;
        }

        if (target instanceof ServerPlayer player) {
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) {
                src.sendFailure(Component.translatable(KEY_NO_CYBERWARE));
                return 0;
            }

            ItemStack stack = new ItemStack(item);
            boolean ok = data.commandInstall(player, stack);

            if (!ok) {
                src.sendFailure(Component.translatable(KEY_INSTALL_FAIL));
                return 0;
            }

            finishPlayerCyberwareUpdate(player, data);

            src.sendSuccess(() -> Component.translatable(KEY_INSTALL_OK, stack.getHoverName(), player.getDisplayName()), false);
            return 1;
        }

        if (!isValidCyberwareEntityTarget(target)) {
            src.sendFailure(Component.translatable(KEY_INVALID_ENTITY, target.getDisplayName()));
            return 0;
        }

        EntityCyberwareData data = target.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
        if (data == null) {
            src.sendFailure(Component.translatable(KEY_NO_CYBERWARE));
            return 0;
        }

        ItemStack stack = new ItemStack(item);
        boolean ok = data.commandInstall(target, stack);

        if (!ok) {
            src.sendFailure(Component.translatable(KEY_INSTALL_FAIL));
            return 0;
        }

        data.setDirty();
        target.syncData(ModMobAttachments.CYBERENTITY_CYBERWARE);

        src.sendSuccess(() -> Component.translatable(KEY_INSTALL_OK, stack.getHoverName(), target.getDisplayName()), false);
        return 1;
    }

    private static int remove(CommandSourceStack src, Entity target, Item item) {
        if (!isCreateCyberneticsCyberwareItem(item)) {
            src.sendFailure(Component.translatable(KEY_WRONG_ITEM));
            return 0;
        }

        if (target instanceof ServerPlayer player) {
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) {
                src.sendFailure(Component.translatable(KEY_NO_CYBERWARE));
                return 0;
            }

            boolean ok = data.commandRemove(player, item);

            if (!ok) {
                src.sendFailure(Component.translatable(KEY_REMOVE_FAIL, player.getDisplayName()));
                return 0;
            }

            fireRemovedHook(player, item);
            finishPlayerCyberwareUpdate(player, data);

            Component itemName = item.getDescription();
            src.sendSuccess(() -> Component.translatable(KEY_REMOVE_OK, itemName, player.getDisplayName()), false);
            return 1;
        }

        if (!isValidCyberwareEntityTarget(target)) {
            src.sendFailure(Component.translatable(KEY_INVALID_ENTITY, target.getDisplayName()));
            return 0;
        }

        EntityCyberwareData data = target.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
        if (data == null) {
            src.sendFailure(Component.translatable(KEY_NO_CYBERWARE));
            return 0;
        }

        boolean ok = data.commandRemove(target, item);

        if (!ok) {
            src.sendFailure(Component.translatable(KEY_REMOVE_FAIL, target.getDisplayName()));
            return 0;
        }

        if (target instanceof LivingEntity living) {
            fireRemovedHook(living, item);
        }

        data.setDirty();
        target.syncData(ModMobAttachments.CYBERENTITY_CYBERWARE);

        Component itemName = item.getDescription();
        src.sendSuccess(() -> Component.translatable(KEY_REMOVE_OK, itemName, target.getDisplayName()), false);
        return 1;
    }

    private static int list(CommandSourceStack src, Entity target) {
        if (target instanceof ServerPlayer player) {
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) {
                src.sendFailure(Component.translatable(KEY_NO_CYBERWARE));
                return 0;
            }

            Component out = data.commandListComponent();
            src.sendSuccess(() -> out, false);
            return 1;
        }

        if (!isValidCyberwareEntityTarget(target)) {
            src.sendFailure(Component.translatable(KEY_INVALID_ENTITY, target.getDisplayName()));
            return 0;
        }

        EntityCyberwareData data = target.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
        if (data == null) {
            src.sendFailure(Component.translatable(KEY_NO_CYBERWARE));
            return 0;
        }

        Component out = data.commandListComponent();
        src.sendSuccess(() -> out, false);
        return 1;
    }

    private static int clear(CommandSourceStack src, Entity target) {
        if (target instanceof ServerPlayer player) {
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) {
                src.sendFailure(Component.translatable(KEY_NO_CYBERWARE));
                return 0;
            }

            List<Item> removedItems = collectInstalledCyberwareItems(data);

            data.clear();
            data.resetToDefaultOrgans();

            fireRemovedHooks(player, removedItems);
            removeAllFbcAttributeModifiers(player);

            finishPlayerCyberwareUpdate(player, data);

            src.sendSuccess(() -> Component.translatable(KEY_CLEAR_OK, player.getDisplayName()), false);
            return 1;
        }

        if (!isValidCyberwareEntityTarget(target)) {
            src.sendFailure(Component.translatable(KEY_INVALID_ENTITY, target.getDisplayName()));
            return 0;
        }

        EntityCyberwareData data = target.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
        if (data == null) {
            src.sendFailure(Component.translatable(KEY_NO_CYBERWARE));
            return 0;
        }

        List<Item> removedItems = collectInstalledCyberwareItems(data);

        data.clear();
        data.resetToDefaultOrgans();

        if (target instanceof LivingEntity living) {
            fireRemovedHooks(living, removedItems);
        }

        data.setDirty();
        target.syncData(ModMobAttachments.CYBERENTITY_CYBERWARE);

        src.sendSuccess(() -> Component.translatable(KEY_CLEAR_OK, target.getDisplayName()), false);
        return 1;
    }

    private static List<Item> collectInstalledCyberwareItems(PlayerCyberwareData data) {
        ArrayList<Item> items = new ArrayList<>();

        if (data == null || data.getAll() == null) {
            return items;
        }

        for (InstalledCyberware[] arr : data.getAll().values()) {
            if (arr == null) continue;

            for (InstalledCyberware installed : arr) {
                if (installed == null) continue;

                ItemStack stack = installed.getItem();
                if (stack == null || stack.isEmpty()) continue;

                Item item = stack.getItem();
                if (item instanceof ICyberwareItem) {
                    items.add(item);
                }
            }
        }

        return items;
    }

    private static List<Item> collectInstalledCyberwareItems(EntityCyberwareData data) {
        ArrayList<Item> items = new ArrayList<>();

        if (data == null || data.getAll() == null) {
            return items;
        }

        for (InstalledCyberware[] arr : data.getAll().values()) {
            if (arr == null) continue;

            for (InstalledCyberware installed : arr) {
                if (installed == null) continue;

                ItemStack stack = installed.getItem();
                if (stack == null || stack.isEmpty()) continue;

                Item item = stack.getItem();
                if (item instanceof ICyberwareItem) {
                    items.add(item);
                }
            }
        }

        return items;
    }

    private static void fireRemovedHooks(LivingEntity entity, List<Item> items) {
        if (entity == null || items == null || items.isEmpty()) return;

        for (Item item : items) {
            fireRemovedHook(entity, item);
        }
    }

    private static void fireRemovedHook(LivingEntity entity, Item item) {
        if (entity == null || item == null) return;

        if (item instanceof ICyberwareItem cyberwareItem) {
            cyberwareItem.onRemoved(entity);
        }
    }

    private static void removeAllFbcAttributeModifiers(ServerPlayer player) {
        if (player == null) return;

        String[] ids = {
                "gemini_attackstrength",
                "gemini_attackspeed",
                "gemini_miningstrength",
                "gemini_speed",

                "samson_attackstrength",
                "samson_miningstrength",
                "samson_durability",
                "samson_watermove",
                "samson_weight",

                "eclipse_speed",
                "eclipse_sprintspeed",
                "eclipse_crouchspeed",

                "spyder_crouchspeed",
                "spyder_jumpheight",

                "wingman_elytraspeed",
                "wingman_elytrahandling",

                "aquarius_movement",
                "aquarius_mining",
                "aquarius_swim",

                "dymond_miningspeed",
                "dymond_weight",

                "dragoon_weight",
                "dragoon_size",
                "dragoon_attack",
                "dragoon_resist",
                "dragoon_knockback",
                "dragoon_jump",

                "copernicus_oxygen",

                "genos_speed",
                "genos_strength",

                "kildare_strength",
                "kildare_speed"
        };

        for (String id : ids) {
            CyberwareAttributeHelper.removeModifier(player, id);
        }
    }

    private static void setEnergyDebug(ServerPlayer player, boolean value) {
        player.getPersistentData().putBoolean(PKEY_ENERGY_DEBUG, value);
    }

    private static boolean isValidCyberwareEntityTarget(Entity entity) {
        return true;
    }
}