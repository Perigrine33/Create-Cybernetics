package com.perigrine3.createcybernetics.command.custom;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.perigrine3.createcybernetics.ConfigValues;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class CyberneticsCommand {
    private CyberneticsCommand() {}

    // ---- Translation keys for KeepCyberware ----
    private static final String KEY_KEEP_QUERY = "command.createcybernetics.keep_cyberware.query";
    private static final String KEY_KEEP_SET   = "command.createcybernetics.keep_cyberware.set";

    // ---- Translation keys for Implants ----
    private static final String KEY_WRONG_ITEM   = "commands.createcybernetics.implants.wrong_item";
    private static final String KEY_NO_CYBERWARE = "commands.createcybernetics.implants.no_cyberware";
    private static final String KEY_INSTALL_FAIL = "commands.createcybernetics.implants.install_fail";
    private static final String KEY_INSTALL_OK   = "commands.createcybernetics.implants.install_success";
    private static final String KEY_REMOVE_FAIL  = "commands.createcybernetics.implants.remove_fail";
    private static final String KEY_REMOVE_OK    = "commands.createcybernetics.implants.remove_success";
    private static final String KEY_CLEAR_OK     = "commands.createcybernetics.implants.clear_success";

    // ---- Persistent key + translation key for Energy Debug ----
    private static final String PKEY_ENERGY_DEBUG = "cc_energy_debug_enabled";
    private static final String KEY_ENERGY_DEBUG_SET = "commands.createcybernetics.energy_debug.set";


    private static final SuggestionProvider<CommandSourceStack> CYBERWARE_ITEM_SUGGESTIONS =
            (context, builder) -> SharedSuggestionProvider.suggestResource(
                    BuiltInRegistries.ITEM.entrySet().stream()
                            .filter(e -> CreateCybernetics.MODID.equals(e.getKey().location().getNamespace()))
                            .filter(e -> e.getValue() instanceof ICyberwareItem)
                            .map(e -> e.getKey().location())
                            .sorted(), builder);

    private static boolean isCreateCyberneticsCyberwareItem(Item item) {
        if (!(item instanceof ICyberwareItem)) return false;
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
        return key != null && CreateCybernetics.MODID.equals(key.getNamespace());
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx) {
        dispatcher.register(Commands.literal("cybernetics").requires(src -> src.hasPermission(2))

                                .then(Commands.literal("implants")

                                        .then(Commands.literal("install")
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .then(Commands.argument("item", ItemArgument.item(ctx))
                                                                .suggests(CYBERWARE_ITEM_SUGGESTIONS)
                                                                .executes(c -> {
                                                                    ServerPlayer target = EntityArgument.getPlayer(c, "player");
                                                                    Item item = ItemArgument.getItem(c, "item").getItem();
                                                                    return install(c.getSource(), target, item);
                                                                })
                                                        )
                                                )
                                        )

                                        .then(Commands.literal("remove")
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .then(Commands.argument("item", ItemArgument.item(ctx))
                                                                .suggests(CYBERWARE_ITEM_SUGGESTIONS)
                                                                .executes(c -> {
                                                                    ServerPlayer target = EntityArgument.getPlayer(c, "player");
                                                                    Item item = ItemArgument.getItem(c, "item").getItem();
                                                                    return remove(c.getSource(), target, item);
                                                                })
                                                        )
                                                )
                                        )

                                        .then(Commands.literal("list")
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .executes(c -> {
                                                            ServerPlayer target = EntityArgument.getPlayer(c, "player");
                                                            return list(c.getSource(), target);
                                                        })
                                                )
                                        )

                                        .then(Commands.literal("clear")
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .executes(c -> {
                                                            ServerPlayer target = EntityArgument.getPlayer(c, "player");
                                                            return clear(c.getSource(), target);
                                                        })
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






    private static int install(CommandSourceStack src, ServerPlayer target, Item item) {
        if (!isCreateCyberneticsCyberwareItem(item)) {
            src.sendFailure(Component.translatable(KEY_WRONG_ITEM));
            return 0;
        }

        PlayerCyberwareData data = target.getData(ModAttachments.CYBERWARE);
        if (data == null) {
            src.sendFailure(Component.translatable(KEY_NO_CYBERWARE));
            return 0;
        }

        ItemStack stack = new ItemStack(item);
        boolean ok = data.commandInstall(target, stack);

        if (!ok) {
            src.sendFailure(Component.translatable(KEY_INSTALL_FAIL, stack.getHoverName()));
            return 0;
        }

        target.syncData(ModAttachments.CYBERWARE);
        src.sendSuccess(() -> Component.translatable(KEY_INSTALL_OK, stack.getHoverName(), target.getDisplayName()), false);
        return 1;
    }



    private static int remove(CommandSourceStack src, ServerPlayer target, Item item) {
        if (!isCreateCyberneticsCyberwareItem(item)) {
            src.sendFailure(Component.translatable(KEY_WRONG_ITEM));
            return 0;
        }

        PlayerCyberwareData data = target.getData(ModAttachments.CYBERWARE);
        if (data == null) {
            src.sendFailure(Component.translatable(KEY_NO_CYBERWARE));
            return 0;
        }

        boolean ok = data.commandRemove(target, item);

        if (!ok) {
            src.sendFailure(Component.translatable(KEY_REMOVE_FAIL, target.getDisplayName()));
            return 0;
        }

        target.syncData(ModAttachments.CYBERWARE);

        Component itemName = item.getDescription();
        src.sendSuccess(() -> Component.translatable(KEY_REMOVE_OK, itemName, target.getDisplayName()), false);
        return 1;
    }



    private static int list(CommandSourceStack src, ServerPlayer target) {
        PlayerCyberwareData data = target.getData(ModAttachments.CYBERWARE);
        if (data == null) {
            src.sendFailure(Component.translatable(KEY_NO_CYBERWARE));
            return 0;
        }

        Component out = data.commandListComponent();
        src.sendSuccess(() -> out, false);
        return 1;
    }



    private static int clear(CommandSourceStack src, ServerPlayer target) {
        PlayerCyberwareData data = target.getData(ModAttachments.CYBERWARE);
        if (data == null) {
            src.sendFailure(Component.translatable(KEY_NO_CYBERWARE));
            return 0;
        }

        data.clear();
        data.resetToDefaultOrgans();
        target.syncData(ModAttachments.CYBERWARE);

        src.sendSuccess(() -> Component.translatable(KEY_CLEAR_OK, target.getDisplayName()), false);
        return 1;
    }

    private static void setEnergyDebug(ServerPlayer player, boolean value) {
        player.getPersistentData().putBoolean(PKEY_ENERGY_DEBUG, value);
    }

}
