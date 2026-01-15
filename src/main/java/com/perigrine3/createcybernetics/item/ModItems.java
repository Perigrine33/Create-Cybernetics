package com.perigrine3.createcybernetics.item;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.block.ModBlocks;
import com.perigrine3.createcybernetics.entity.ModEntities;
import com.perigrine3.createcybernetics.item.cyberware.*;
import com.perigrine3.createcybernetics.item.cyberware.upgrade_items.*;
import com.perigrine3.createcybernetics.item.generic.*;
import com.perigrine3.createcybernetics.item.organs.*;
import com.perigrine3.createcybernetics.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateCybernetics.MODID);


//BASIC ITEMS & COMPONENTS
    public static final DeferredItem<Item> RAWTITANIUM = ITEMS.register("rawtitanium",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TITANIUMINGOT = ITEMS.register("titaniumingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CRUSHEDTITANIUM = ITEMS.register("crushedtitanium",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TITANIUMNUGGET = ITEMS.register("titaniumnugget",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TITANIUMSHEET = ITEMS.register("titaniumsheet",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> EYEUPGRADEBASE = ITEMS.register("eyeupgradebase",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TITANIUM_HAND = ITEMS.register("titanium_hand",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> XP_CAPSULE = ITEMS.register("expcapsule",
            () -> new XPCapsuleItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> FACEPLATE = ITEMS.register("faceplate",
            () -> new XPCapsuleItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> NETHERITE_QPU = ITEMS.register("netherite_qpu",
            () -> new Item(new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legendarycomponent_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});
    public static final DeferredItem<Item> EMP_GRENADE = ITEMS.register("emp_grenade",
            () -> new EmpGrenadeItem(new Item.Properties()));
    public static final DeferredItem<Item> DATURA_FLOWER = ITEMS.register("datura_flower",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DATURA_SEED_POD = ITEMS.register("datura_seed_pod",
            () -> new ItemNameBlockItem(ModBlocks.DATURA_BUSH.get(), new Item.Properties()));

    public static final DeferredItem<Item> FRONTAL_LOBE = ITEMS.register("frontal_lobe",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> PARIETAL_LOBE = ITEMS.register("parietal_lobe",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TEMPORAL_LOBE = ITEMS.register("temporal_lobe",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> OCCIPITAL_LOBE = ITEMS.register("occipital_lobe",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CEREBELLUM = ITEMS.register("cerebellum",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> QUICKHACK_BURNING = ITEMS.register("quickhack_burning",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> QUICKHACK_REBOOT = ITEMS.register("quickhack_reboot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> QUICKHACK_ITEM = ITEMS.register("quickhack_item",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> NEUROPOZYNE_AUTOINJECTOR = ITEMS.register("neuropozyne_autoinjector",
            () -> new NeuropozyneAutoinjector(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<Item> EMPTY_AUTOINJECTOR = ITEMS.register("empty_autoinjector",
            () -> new Item(new Item.Properties().stacksTo(16)));

    public static final DeferredItem<Item> MUSIC_DISC_CYBERPSYCHO = ITEMS.register("music_disc_cyberpsycho",
            () -> new Item(new Item.Properties().stacksTo(1).jukeboxPlayable(ModSounds.CYBERPSYCHO_KEY)));
        public static final DeferredItem<Item> MUSIC_DISC_NEON_OVERLORDS = ITEMS.register("music_disc_neon_overlords",
            () -> new Item(new Item.Properties().stacksTo(1).jukeboxPlayable(ModSounds.NEON_OVERLORDS_KEY)));
    public static final DeferredItem<Item> MUSIC_DISC_NEUROHACK = ITEMS.register("music_disc_neurohack",
            () -> new Item(new Item.Properties().stacksTo(1).jukeboxPlayable(ModSounds.NEUROHACK_KEY)));
    public static final DeferredItem<Item> MUSIC_DISC_THE_GRID = ITEMS.register("music_disc_the_grid",
            () -> new Item(new Item.Properties().stacksTo(1).jukeboxPlayable(ModSounds.THE_GRID_KEY)));


//SPAWN EGGS
    public static final DeferredItem<Item> SMASHER_SPAWN_EGG = ITEMS.register("smasher_spawn_egg",
        () -> new DeferredSpawnEggItem(ModEntities.SMASHER, 0x7f7b7b, 0xf44336,
                new Item.Properties()));
    public static final DeferredItem<Item> CYBERZOMBIE_SPAWN_EGG = ITEMS.register("cyberzombie_spawn_egg",
        () -> new DeferredSpawnEggItem(ModEntities.CYBERZOMBIE, 0x00AFAF, 0xFFFF003C,
                new Item.Properties()));
    public static final DeferredItem<Item> CYBERSKELETON_SPAWN_EGG = ITEMS.register("cyberskeleton_spawn_egg",
        () -> new DeferredSpawnEggItem(ModEntities.CYBERSKELETON, 0xC1C1C1, 0xFFFF003C,
                new Item.Properties()));


//DATA SHARDS
    public static final DeferredItem<Item> DATA_SHARD_RED = ITEMS.register("data_shard_red",
            () -> new DataShardItem(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.literal("Combat").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xD90804))));
                    tooltipComponents.add(Component.literal("Improves combat ability"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});
    public static final DeferredItem<Item> DATA_SHARD_ORANGE = ITEMS.register("data_shard_orange",
            () -> new DataShardItem(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.literal("Mining").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xD96704))));
                    tooltipComponents.add(Component.literal("Occasionally doubles ore drops"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});
    public static final DeferredItem<Item> DATA_SHARD_YELLOW = ITEMS.register("data_shard_yellow",
            () -> new DataShardItem(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.literal("Haggling").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xE8C602))));
                    tooltipComponents.add(Component.literal("Improves villager trades"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});
    public static final DeferredItem<Item> DATA_SHARD_GREEN = ITEMS.register("data_shard_green",
            () -> new DataShardItem(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.literal("Learning").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x08B302))));
                    tooltipComponents.add(Component.literal("Doubles XP intake"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});
    public static final DeferredItem<Item> DATA_SHARD_CYAN = ITEMS.register("data_shard_cyan",
            () -> new DataShardItem(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.literal("Archery").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x02C97A))));
                    tooltipComponents.add(Component.literal("Improves accuracy with a bow"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});
    public static final DeferredItem<Item> DATA_SHARD_BLUE = ITEMS.register("data_shard_blue",
            () -> new DataShardItem(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.literal("Swimming").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x023DE0))));
                    tooltipComponents.add(Component.literal("Improves underwater actions"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});
    public static final DeferredItem<Item> DATA_SHARD_PURPLE = ITEMS.register("data_shard_purple",
            () -> new DataShardItem(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.literal("Crafting").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x9A02D6))));
                    tooltipComponents.add(Component.literal("Doubles 3x3 crafting output"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});
    public static final DeferredItem<Item> DATA_SHARD_PINK = ITEMS.register("data_shard_pink",
            () -> new DataShardItem(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.literal("Husbandry").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xE002AD))));
                    tooltipComponents.add(Component.literal("Doubles bred creatures"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});
    public static final DeferredItem<Item> DATA_SHARD_BROWN = ITEMS.register("data_shard_brown",
            () -> new DataShardItem(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.literal("Farming").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x783F19))));
                    tooltipComponents.add(Component.literal("Doubles crop drops"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});
    public static final DeferredItem<Item> DATA_SHARD_GRAY = ITEMS.register("data_shard_gray",
            () -> new DataShardItem(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.literal("Flight").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x5E5E5E))));
                    tooltipComponents.add(Component.literal("Improves Elytra flight handling"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});
    public static final DeferredItem<Item> DATA_SHARD_BLACK = ITEMS.register("data_shard_black",
            () -> new DataShardItem(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.literal("Stealth Ops").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x292929))));
                    tooltipComponents.add(Component.literal("Improves stealth abilities"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});
    public static final DeferredItem<Item> DATA_SHARD_BIOCHIP = ITEMS.register("data_shard_biochip",
            () -> new BiochipDataShardItem(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.literal("Stores consciousness if installed for 28 days"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});





//BASIC COMPONENTS
    public static final DeferredItem<Item> COMPONENT_ACTUATOR = ITEMS.register("component_actuator",
            () -> new Item(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basiccomponent_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});
    public static final DeferredItem<Item> COMPONENT_FIBEROPTICS = ITEMS.register("component_fiberoptics",
            () -> new Item(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basiccomponent_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});
    public static final DeferredItem<Item> COMPONENT_WIRING = ITEMS.register("component_wiring",
            () -> new Item(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basiccomponent_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});
    public static final DeferredItem<Item> COMPONENT_DIODES = ITEMS.register("component_diodes",
            () -> new Item(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basiccomponent_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});
    public static final DeferredItem<Item> COMPONENT_PLATING = ITEMS.register("component_plating",
            () -> new Item(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basiccomponent_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});
    public static final DeferredItem<Item> COMPONENT_GRAPHICSCARD = ITEMS.register("component_graphicscard",
            () -> new Item(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basiccomponent_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});
    public static final DeferredItem<Item> COMPONENT_SSD = ITEMS.register("component_ssd",
            () -> new Item(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basiccomponent_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});
    public static final DeferredItem<Item> COMPONENT_STORAGE = ITEMS.register("component_storage",
            () -> new Item(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basiccomponent_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});
    public static final DeferredItem<Item> COMPONENT_SYNTHNERVES = ITEMS.register("component_synthnerves",
            () -> new Item(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basiccomponent_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});
    public static final DeferredItem<Item> COMPONENT_MESH = ITEMS.register("component_mesh",
            () -> new Item(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basiccomponent_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});
    public static final DeferredItem<Item> COMPONENT_LED = registerIfNotLoaded("create", "component_led",
            () -> new Item(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basiccomponent_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});
    public static final DeferredItem<Item> COMPONENT_TITANIUMROD = registerIfNotLoaded("create", "component_titaniumrod",
            () -> new Item(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basiccomponent_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});




//BODY PARTS
    public static final DeferredItem<Item> BODYPART_RIGHTLEG = ITEMS.register("bodypart_rightleg",
            () -> new RightLegItem(new Item.Properties().stacksTo(1), 0) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BODYPART_LEFTLEG = ITEMS.register("bodypart_leftleg",
            () -> new LeftLegItem(new Item.Properties().stacksTo(1), 0) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BODYPART_RIGHTARM = ITEMS.register("bodypart_rightarm",
            () -> new RightArmItem(new Item.Properties().stacksTo(1), 0) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BODYPART_LEFTARM = ITEMS.register("bodypart_leftarm",
            () -> new LeftArmItem(new Item.Properties().stacksTo(1), 0) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

//INTERNAL WETWARE
    public static final DeferredItem<Item> BODYPART_SKELETON = ITEMS.register("bodypart_skeleton",
            () -> new SkeletonItem(new Item.Properties().stacksTo(16), 0) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BODYPART_BRAIN = ITEMS.register("bodypart_brain",
            () -> new BrainItem(new Item.Properties().stacksTo(16), 0) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BODYPART_EYEBALLS = ITEMS.register("bodypart_eyeballs",
            () -> new EyeballItem(new Item.Properties().stacksTo(16), 0) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BODYPART_HEART = ITEMS.register("bodypart_heart",
            () -> new HeartItem(new Item.Properties().stacksTo(16), 0) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BODYPART_LUNGS = ITEMS.register("bodypart_lungs",
            () -> new LungsItem(new Item.Properties().stacksTo(16), 0) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BODYPART_LIVER = ITEMS.register("bodypart_liver",
            () -> new LiverItem(new Item.Properties().stacksTo(16), 0) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BODYPART_INTESTINES = ITEMS.register("bodypart_intestines",
            () -> new IntestinesItem(new Item.Properties().stacksTo(16), 0) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BODYPART_MUSCLE = ITEMS.register("bodypart_muscle",
            () -> new MuscleItem(new Item.Properties().stacksTo(16), 0) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BODYPART_SKIN = ITEMS.register("bodypart_skin",
            () -> new SkinItem(new Item.Properties().stacksTo(16), 0) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

//ANIMAL BODYPARTS
    public static final DeferredItem<Item> BODYPART_GUARDIANRETINA = ITEMS.register("bodypart_guardianretina",
            () -> new Item(new Item.Properties().stacksTo(16)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BODYPART_WARDENESOPHAGUS = ITEMS.register("bodypart_wardenesophagus",
            () -> new Item(new Item.Properties().stacksTo(16)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BODYPART_GYROSCOPICBLADDER = ITEMS.register("bodypart_gyroscopicbladder",
            () -> new Item(new Item.Properties().stacksTo(16)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });



//BASE CYBERWARE
    public static final DeferredItem<Item> BASECYBERWARE_RIGHTLEG = ITEMS.register("basecyberware_rightleg",
            () -> new CyberlegItem(new Item.Properties().stacksTo(1), 5, CyberwareSlot.RLEG) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basecyberware_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basecyberware_rightleg.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basecyberware_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> BASECYBERWARE_LEFTLEG = ITEMS.register("basecyberware_leftleg",
            () -> new CyberlegItem(new Item.Properties().stacksTo(1), 5, CyberwareSlot.LLEG) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    if (Screen.hasShiftDown()) {
                        tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basecyberware_tooltip"));
                        tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basecyberware_leftleg.tooltip1"));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basecyberware_tooltip"));
                        tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                    }
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BASECYBERWARE_RIGHTARM = ITEMS.register("basecyberware_rightarm",
            () -> new CyberarmItem(new Item.Properties().stacksTo(1), 5, CyberwareSlot.RARM) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basecyberware_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basecyberware_rightarm.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basecyberware_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> BASECYBERWARE_LEFTARM = ITEMS.register("basecyberware_leftarm",
            () -> new CyberarmItem(new Item.Properties().stacksTo(1), 5, CyberwareSlot.LARM) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    if (Screen.hasShiftDown()) {
                        tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basecyberware_tooltip"));
                        tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basecyberware_leftarm.tooltip1"));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basecyberware_tooltip"));
                        tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                    }
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BASECYBERWARE_LINEARFRAME = ITEMS.register("basecyberware_linearframe",
            () -> new LinearFrameItem(new Item.Properties().stacksTo(1), 15) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    if (Screen.hasShiftDown()) {
                        tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basecyberware_tooltip"));
                        tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basecyberware_linearframe.tooltip1"));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basecyberware_tooltip"));
                        tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                    }
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BASECYBERWARE_CYBEREYES = ITEMS.register("basecyberware_cybereyes",
            () -> new CybereyeItem(new Item.Properties().stacksTo(1), 5) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    if (Screen.hasShiftDown()) {
                        tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basecyberware_tooltip"));
                        tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basecyberware_cybereyes.tooltip1"));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.createcybernetics.basecyberware_tooltip"));
                        tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                    }
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

//EYE UPGRADES
    public static final DeferredItem<Item> EYEUPGRADES_HUDLENS = ITEMS.register("eyeupgrades_hudlens",
        () -> new HUDlensItem(new Item.Properties().stacksTo(1), 1) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_hudlens.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_hudlens.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });

    //Xaero's Minimap compat
            public static final DeferredItem<Item> EYEUPGRADES_NAVIGATIONCHIP = registerIfLoaded("xaerominimap","eyeupgrades_navigationchip",
                () -> new NavigationChipItem(new Item.Properties().stacksTo(1), 3) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_navigationchip.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_navigationchip.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });

    public static final DeferredItem<Item> EYEUPGRADES_HUDJACK = ITEMS.register("eyeupgrades_hudjack",
        () -> new HUDjackItem(new Item.Properties().stacksTo(1), 3) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_hudjack.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_hudjack.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> EYEUPGRADES_NIGHTVISION = ITEMS.register("eyeupgrades_nightvision",
        () -> new NightVisionModuleItem(new Item.Properties().stacksTo(1), 3) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_nightvision.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_nightvision.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> EYEUPGRADES_TARGETING = ITEMS.register("eyeupgrades_targeting",
        () -> new TargetingModuleItem(new Item.Properties().stacksTo(1), 3) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_targeting.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_targeting.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> EYEUPGRADES_UNDERWATERVISION = ITEMS.register("eyeupgrades_underwatervision",
        () -> new UnderwaterVisionModuleItem(new Item.Properties().stacksTo(1), 3) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_underwatervision.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_underwatervision.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> EYEUPGRADES_ZOOM = ITEMS.register("eyeupgrades_zoom",
        () -> new OpticZoomModuleItem(new Item.Properties().stacksTo(1), 3) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_zoom.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_zoom.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> EYEUPGRADES_TRAJECTORYCALCULATOR = ITEMS.register("eyeupgrades_trajectorycalculator",
        () -> new TrajectoryCalculatorModuleItem(new Item.Properties().stacksTo(1), 3) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_trajectorycalculator.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_trajectorycalculator.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.eyeupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });

//ARM UPGRADES
    public static final DeferredItem<Item> ARMUPGRADES_ARMCANNON = ITEMS.register("armupgrades_armcannon",
        () -> new ArmCannonItem(new Item.Properties().stacksTo(1), 7) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_armcannon.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_armcannon.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> ARMUPGRADES_FLYWHEEL = ITEMS.register("armupgrades_flywheel",
        () -> new QuickdrawFlywheelItem(new Item.Properties().stacksTo(1), 3) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_flywheel.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_flywheel.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> ARMUPGRADES_CLAWS = ITEMS.register("armupgrades_claws",
        () -> new RetractableClawsItem(new Item.Properties().stacksTo(1), 5) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_claws.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_claws.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> ARMUPGRADES_CRAFTHANDS = ITEMS.register("armupgrades_crafthands",
        () -> new CraftingHandsItem(new Item.Properties().stacksTo(1), 5) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_crafthands.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_crafthands.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> ARMUPGRADES_DRILLFIST = ITEMS.register("armupgrades_drillfist",
        () -> new ArmUpgradeItem(new Item.Properties().stacksTo(1), 7) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_drillfist.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_drillfist.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> ARMUPGRADES_FIRESTARTER = ITEMS.register("armupgrades_firestarter",
        () -> new FirestarterItem(new Item.Properties().stacksTo(1), 1) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_firestarter.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_firestarter.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> ARMUPGRADES_PNEUMATICWRIST = ITEMS.register("armupgrades_pneumaticwrist",
        () -> new PneumaticWristItem(new Item.Properties().stacksTo(1), 3) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_pneumaticwrist.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_pneumaticwrist.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> ARMUPGRADES_REINFORCEDKNUCKLES = ITEMS.register("armupgrades_reinforcedknuckles",
        () -> new ReinforcedKnucklesItem(new Item.Properties().stacksTo(1), 4) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_reinforcedknuckles.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_reinforcedknuckles.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.armupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });

//LEG UPGRADES
    public static final DeferredItem<Item> LEGUPGRADES_METALDETECTOR = ITEMS.register("legupgrades_metaldetector",
        () -> new MetalDetectorItem(new Item.Properties().stacksTo(1), 3) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legupgrades_metaldetector.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legupgrades_metaldetector.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> LEGUPGRADES_ANKLEBRACERS = ITEMS.register("legupgrades_anklebracers",
        () -> new AnkleBracerItem(new Item.Properties().stacksTo(1), 5) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legupgrades_anklebracers.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legupgrades_anklebracers.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> LEGUPGRADES_JUMPBOOST = ITEMS.register("legupgrades_jumpboost",
        () -> new PneumaticCalvesItem(new Item.Properties().stacksTo(1), 5) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legupgrades_jumpboost.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legupgrades_jumpboost.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> LEGUPGRADES_PROPELLERS = ITEMS.register("legupgrades_propellers",
        () -> new PropellersItem(new Item.Properties().stacksTo(1), 3) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legupgrades_propellers.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legupgrades_propellers.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> LEGUPGRADES_SPURS = ITEMS.register("legupgrades_spurs",
        () -> new SpursItem(new Item.Properties().stacksTo(1), 2) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legupgrades_spurs.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legupgrades_spurs.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> LEGUPGRADES_OCELOTPAWS = ITEMS.register("legupgrades_ocelotpaws",
        () -> new OcelotPawsItem(new Item.Properties().stacksTo(1), 5) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legupgrades_ocelotpaws.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legupgrades_ocelotpaws.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });

//BONE UPGRADES
    public static final DeferredItem<Item> BONEUPGRADES_BONEBATTERY = ITEMS.register("boneupgrades_bonebattery",
        () -> new MarrowBatteryItem(new Item.Properties().stacksTo(1), 2) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_bonebattery.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_bonebattery.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> BONEUPGRADES_BONEFLEX = ITEMS.register("boneupgrades_boneflex",
        () -> new BoneflexItem(new Item.Properties().stacksTo(1), 3) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_boneflex.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_boneflex.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> BONEUPGRADES_BONELACING = ITEMS.register("boneupgrades_bonelacing",
        () -> new BonelacingItem(new Item.Properties().stacksTo(1), 5) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_bonelacing.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_bonelacing.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> BONEUPGRADES_ELYTRA = registerIfLoaded("caelus", "boneupgrades_elytra",
        () -> new DeployableElytraItem(new Item.Properties().stacksTo(1), 10) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_elytra.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_elytra.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> BONEUPGRADES_PIEZO = ITEMS.register("boneupgrades_piezo",
        () -> new PiezoelectricEnergyGeneratorItem(new Item.Properties().stacksTo(1), 3) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_piezo.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_piezo.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> BONEUPGRADES_SPINALINJECTOR = ITEMS.register("boneupgrades_spinalinjector",
        () -> new SpinalInjectorItem(new Item.Properties().stacksTo(1), 10) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_spinalinjector.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_spinalinjector.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> BONEUPGRADES_SANDEVISTAN = ITEMS.register("boneupgrades_sandevistan",
        () -> new SandevistanItem(new Item.Properties().stacksTo(1), 10) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_sandevistan.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.boneupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });

//BRAIN UPGRADES
    public static final DeferredItem<Item> BRAINUPGRADES_CYBERBRAIN = ITEMS.register("brainupgrades_cyberbrain",
        () -> new CerebralProcessingUnitItem(new Item.Properties().stacksTo(1), 12) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrade_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrades_cyberbrain.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrades_cyberbrain.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrade_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> BRAINUPGRADES_EYEOFDEFENDER = ITEMS.register("brainupgrades_eyeofdefender",
        () -> new EyeOfDefenderItem(new Item.Properties().stacksTo(1), 8) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrade_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrades_eyeofdefender.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrades_eyeofdefender.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrade_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });

    //Create: Enchantment Industry compat
        public static final DeferredItem<Item> BRAINUPGRADES_CONSCIOUSNESSTRANSMITTER = registerIfLoaded("create_enchantment_industry","brainupgrades_consciousnesstransmitter",
            () -> new NeedlecasterItem(new Item.Properties().stacksTo(1), 5) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrade_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrades_consciousnesstransmitter.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrades_consciousnesstransmitter.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrade_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
        public static final DeferredItem<Item> BRAINUPGRADES_CORTICALSTACK = registerIfLoaded("create_enchantment_industry","brainupgrades_corticalstack",
            () -> new CorticalStackItem(new Item.Properties().stacksTo(1), 10) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrade_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrades_corticalstack.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrades_corticalstack.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrade_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });

    public static final DeferredItem<Item> BRAINUPGRADES_ENDERJAMMER = ITEMS.register("brainupgrades_enderjammer",
        () -> new EnderJammerItem(new Item.Properties().stacksTo(1), 2) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrade_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrades_enderjammer.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrade_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> BRAINUPGRADES_MATRIX = ITEMS.register("brainupgrades_matrix",
        () -> new ThreatMatrixItem(new Item.Properties().stacksTo(1), 4) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrade_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrades_matrix.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrades_matrix.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrade_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> BRAINUPGRADES_NEURALCONTEXTUALIZER = ITEMS.register("brainupgrades_neuralcontextualizer",
        () -> new NeuralContextualizerItem(new Item.Properties().stacksTo(1), 3) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrade_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrades_neuralcontextualizer.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrades_neuralcontextualizer.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrade_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> BRAINUPGRADES_CYBERDECK = ITEMS.register("brainupgrades_cyberdeck",
        () -> new BrainUpgradeItem(new Item.Properties().stacksTo(1), 8) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrade_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrades_cyberdeck.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrades_cyberdeck.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrade_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> BRAINUPGRADES_IDEM = ITEMS.register("brainupgrades_idem",
        () -> new IDEMItem(new Item.Properties().stacksTo(1), 6) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrade_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrades_idem.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrades_idem.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrade_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> BRAINUPGRADES_CHIPWARESLOTS = ITEMS.register("brainupgrades_chipwareslots",
        () -> new ChipwareSlotsItem(new Item.Properties().stacksTo(1), 6) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrade_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrades_chipwareslots.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrades_chipwareslots.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrade_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });

//HEART UPGRADES
    public static final DeferredItem<Item> HEARTUPGRADES_CYBERHEART = ITEMS.register("heartupgrades_cyberheart",
        () -> new MechanicalHeartItem(new Item.Properties().stacksTo(1), 8) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.heartupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.heartupgrades_cyberheart.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.heartupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> HEARTUPGRADES_COUPLER = ITEMS.register("heartupgrades_coupler",
        () -> new CardiovascularCouplerItem(new Item.Properties().stacksTo(1), 3) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.heartupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.heartupgrades_coupler.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.heartupgrades_coupler.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.heartupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> HEARTUPGRADES_CREEPERHEART = ITEMS.register("heartupgrades_creeperheart",
        () -> new CreeperheartItem(new Item.Properties().stacksTo(1), 5) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.heartupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.heartupgrades_creeperheart.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.heartupgrades_creeperheart.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.heartupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> HEARTUPGRADES_DEFIBRILLATOR = ITEMS.register("heartupgrades_defibrillator",
        () -> new InternalDefibrillatorItem(new Item.Properties().stacksTo(1), 8) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.heartupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.heartupgrades_defibrillator.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.heartupgrades_defibrillator.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.heartupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> HEARTUPGRADES_STEMCELL = ITEMS.register("heartupgrades_stemcell",
        () -> new StemCellsItem(new Item.Properties().stacksTo(1), 6) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.heartupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.heartupgrades_stemcell.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.heartupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> HEARTUPGRADES_PLATELETS = ITEMS.register("heartupgrades_platelets",
        () -> new PlateletDispatcherItem(new Item.Properties().stacksTo(1), 6) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.heartupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.heartupgrades_platelets.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.heartupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });

//LUNG UPGRADES
    public static final DeferredItem<Item> LUNGSUPGRADES_HYPEROXYGENATION = ITEMS.register("lungsupgrades_hyperoxygenation",
        () -> new HyperoxygenationBoostItem(new Item.Properties().stacksTo(1), 3){
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.lungsupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.lungsupgrades_hyperoxygenation.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.lungsupgrades_hyperoxygenation.tooltip2"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.lungsupgrades_hyperoxygenation.tooltip3"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.lungsupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> LUNGSUPGRADES_OXYGEN = ITEMS.register("lungsupgrades_oxygen",
        () -> new OxygenTankItem(new Item.Properties().stacksTo(1), 5) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.lungsupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.lungsupgrades_oxygen.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.lungsupgrades_oxygen.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.lungsupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });

//ORGAN UPGRADES
    public static final DeferredItem<Item> ORGANSUPGRADES_ADRENALINE = ITEMS.register("organsupgrades_adrenaline",
        () -> new AdrenalPumpItem(new Item.Properties().stacksTo(1), 5) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_adrenaline.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> ORGANSUPGRADES_BATTERY = ITEMS.register("organsupgrades_battery",
        () -> new InternalBatteryItem(new Item.Properties().stacksTo(1), 3) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_battery.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_battery.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> ORGANSUPGRADES_DIAMONDWAFERSTACK = ITEMS.register("organsupgrades_diamondwaferstack",
        () -> new DiamondWaferstackItem(new Item.Properties().stacksTo(1), 1) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_diamondwaferstack.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_diamondwaferstack.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> ORGANSUPGRADES_DUALISTICCONVERTER = ITEMS.register("organsupgrades_dualisticconverter",
        () -> new DualisticConverterItem(new Item.Properties().stacksTo(1), 8) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_dualisticconverter.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> ORGANSUPGRADES_LIVERFILTER = ITEMS.register("organsupgrades_liverfilter",
        () -> new LiverFilterItem(new Item.Properties().stacksTo(1), 3) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_liverfilter.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> ORGANSUPGRADES_MAGICCATALYST = ITEMS.register("organsupgrades_magiccatalyst",
        () -> new MagicCatalystItem(new Item.Properties().stacksTo(1), 15) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_magiccatalyst.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> ORGANSUPGRADES_METABOLIC = ITEMS.register("organsupgrades_metabolic",
        () -> new MetabolicConverterItem(new Item.Properties().stacksTo(1), 4) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_metabolic.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_metabolic.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> ORGANSUPGRADES_DENSEBATTERY = ITEMS.register("organsupgrade_densebattery",
        () -> new DenseBatteryItem(new Item.Properties().stacksTo(1), 8) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrade_densebattery.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.organsupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });

//SKIN UPGRADES
    public static final DeferredItem<Item> SKINUPGRADES_ARTERIALTURBINE = ITEMS.register("skinupgrades_arterialturbine",
        () -> new ArterialTurbineItem(new Item.Properties().stacksTo(1), 3) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_arterialturbine.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_arterialturbine.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> SKINUPGRADES_CHROMATOPHORES = ITEMS.register("skinupgrades_chromatophores",
        () -> new SyntheticChromatophoresItem(new Item.Properties().stacksTo(1), 8) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_chromatophores.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> SKINUPGRADES_SYNTHSKIN = ITEMS.register("skinupgrades_synthskin",
        () -> new SynthSkinItem(new Item.Properties().stacksTo(1), 1) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_synthskin.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> SKINUPGRADES_IMMUNO = ITEMS.register("skinupgrades_immuno",
        () -> new ImmunosuppressorItem(new Item.Properties().stacksTo(1), -25) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_immuno.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> SKINUPGRADES_FACEPLATE = ITEMS.register("skinupgrades_faceplate",
        () -> new InterchangeableFaceplateItem(new Item.Properties().stacksTo(1), 8) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_faceplate.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> SKINUPGRADES_NETHERITEPLATING = ITEMS.register("skinupgrades_netheriteplating",
        () -> new IsothermalSkinItem(new Item.Properties().stacksTo(1), 10) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_netheriteplating.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> SKINUPGRADES_SOLARSKIN = ITEMS.register("skinupgrades_solarskin",
        () -> new SolarskinItem(new Item.Properties().stacksTo(1), 3) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_solarskin.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> SKINUPGRADES_SUBDERMALARMOR = ITEMS.register("skinupgrades_subdermalarmor",
        () -> new SubdermalArmorItem(new Item.Properties().stacksTo(1), 6) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_subdermalarmor.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_subdermalarmor.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> SKINUPGRADES_SUBDERMALSPIKES = ITEMS.register("skinupgrades_subdermalspikes",
        () -> new SubdermalSpikesItem(new Item.Properties().stacksTo(1), 7) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_subdermalspikes.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> SKINUPGRADES_SYNTHETICSETULES = ITEMS.register("skinupgrades_syntheticsetules",
        () -> new SyntheticSetulesItem(new Item.Properties().stacksTo(1), 4) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_syntheticsetules.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> SKINUPGRADES_METALPLATING = ITEMS.register("skinupgrades_metalplating",
        () -> new MetalSkinItem(new Item.Properties().stacksTo(1), 4) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_metalplating.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });

    //COLD SWEAT COMPAT
        public static final DeferredItem<Item> SKINUPGRADES_SWEAT = registerIfLoaded("cold_sweat","skinupgrades_sweat",
            () -> new SweatGlandsItem(new Item.Properties().stacksTo(1), 3) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_sweat.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.skinupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });

//MUSCLE UPGRADES
    public static final DeferredItem<Item> MUSCLEUPGRADES_SYNTHMUSCLE = ITEMS.register("muscleupgrades_synthmuscle",
        () -> new SynthMuscleItem(new Item.Properties().stacksTo(1), 5) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.muscleupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.muscleupgrades_synthmuscle.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.muscleupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> MUSCLEUPGRADES_WIREDREFLEXES = ITEMS.register("muscleupgrades_wiredreflexes",
        () -> new WiredReflexesItem(new Item.Properties().stacksTo(1), 4) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.muscleupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.muscleupgrades_wiredreflexes.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.muscleupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });

//WETWARE UPGRADES

    //COLD SWEAT COMPAT
        public static final DeferredItem<Item> WETWARE_BLUBBER = registerIfLoaded("cold_sweat","wetware_blubber",
                () -> new BlubberItem(new Item.Properties().stacksTo(1), 3) {
                    @Override
                    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                        if (Screen.hasShiftDown()) {
                            tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetwareupgrades_tooltip"));
                            tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_blubber.tooltip1"));
                        } else {
                            tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetwareupgrades_tooltip"));
                            tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                        }
                        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                    }
                });

    public static final DeferredItem<Item> WETWARE_FIREBREATHINGGLAND = ITEMS.register("wetware_firebreathinggland",
        () -> new IgniphorusGlandItem(new Item.Properties().stacksTo(1), 15) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetwareupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_firebreathinggland.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetwareupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> WETWARE_GILLS = ITEMS.register("wetware_gills",
        () -> new GillsItem(new Item.Properties().stacksTo(1), 12) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetwareupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_gills.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetwareupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> WETWARE_GUARDIANEYE = ITEMS.register("wetware_guardianeye",
        () -> new GuardianEyeItem(new Item.Properties().stacksTo(1), 15) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetwareupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_guardianeye.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetwareupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> WETWARE_POLARBEARFUR = ITEMS.register("wetware_polarbearfur",
        () -> new PolarBearFurItem(new Item.Properties().stacksTo(1), 12) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetwareupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_polarbearfur.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetwareupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> WETWARE_RAVAGERTENDONS = ITEMS.register("wetware_ravagertendons",
        () -> new RavagerTendonsItem(new Item.Properties().stacksTo(1), 15) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetwareupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_ravagertendons.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetwareupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> WETWARE_SCULKLUNGS = ITEMS.register("wetware_sculklungs",
        () -> new SculkLungsItem(new Item.Properties().stacksTo(1), 15) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetwareupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_sculklungs.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetwareupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> WETWARE_TACTICALINKSAC = ITEMS.register("wetware_tacticalinksac",
        () -> new TacticalInkSacItem(new Item.Properties().stacksTo(1), 8) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetwareupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tacticalinksac.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetwareupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> WETWARE_AEROSTASISGYROBLADDER = ITEMS.register("wetware_aerostasisgyrobladder",
        () -> new AerostasisItem(new Item.Properties().stacksTo(1), 15) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetwareupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_aerostasisgyrobladder.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetwareupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> WETWARE_GRASSFEDSTOMACH = ITEMS.register("wetware_grassfedstomach",
        () -> new GrassfedStomachItem(new Item.Properties().stacksTo(1), 5) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetwareupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_grassfedstomach.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetwareupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });
    public static final DeferredItem<Item> WETWARE_SPINNERETTE = ITEMS.register("wetware_spinnerette",
        () -> new SpinneretteItem(new Item.Properties().stacksTo(1), 3) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetwareupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_spinnerette.tooltip1"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetwareupgrades_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });



//SCAVENGED CYBERWARE
    public static final DeferredItem<Item> SCAVENGED_RIGHTLEG = ITEMS.register("scavenged_rightleg",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_LEFTLEG = ITEMS.register("scavenged_leftleg",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_RIGHTARM = ITEMS.register("scavenged_rightarm",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_LEFTARM = ITEMS.register("scavenged_leftarm",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_LINEARFRAME = ITEMS.register("scavenged_linearframe",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_CYBEREYES = ITEMS.register("scavenged_cybereyes",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_HUDLENS = ITEMS.register("scavenged_hudlens",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> SCAVENGED_NAVIGATIONCHIP = registerIfLoaded("xaerominimap","scavenged_navigationchip",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> SCAVENGED_HUDJACK = ITEMS.register("scavenged_hudjack",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_NIGHTVISION = ITEMS.register("scavenged_nightvision",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_TARGETING = ITEMS.register("scavenged_targeting",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item>SCAVENGED_UNDERWATERVISION = ITEMS.register("scavenged_underwatervision",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_ZOOM = ITEMS.register("scavenged_zoom",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_TRAJECTORYCALCULATOR = ITEMS.register("scavenged_trajectorycalculator",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_ARMCANNON = ITEMS.register("scavenged_armcannon",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_FLYWHEEL = ITEMS.register("scavenged_flywheel",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_CLAWS = ITEMS.register("scavenged_claws",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_CRAFTHANDS = ITEMS.register("scavenged_crafthands",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_DRILLFIST = ITEMS.register("scavenged_drillfist",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_FIRESTARTER = ITEMS.register("scavenged_firestarter",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_PNEUMATICWRIST = ITEMS.register("scavenged_pneumaticwrist",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_REINFORCEDKNUCKLES = ITEMS.register("scavenged_reinforcedknuckles",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_METALDETECTOR = ITEMS.register("scavenged_metaldetector",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_ANKLEBRACERS = ITEMS.register("scavenged_anklebracers",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_JUMPBOOST = ITEMS.register("scavenged_jumpboost",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_PROPELLERS = ITEMS.register("scavenged_propellers",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_SPURS = ITEMS.register("scavenged_spurs",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_OCELOTPAWS = ITEMS.register("scavenged_ocelotpaws",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_BONEBATTERY = ITEMS.register("scavenged_bonebattery",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_BONEFLEX = ITEMS.register("scavenged_boneflex",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_BONELACING = ITEMS.register("scavenged_bonelacing",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_ELYTRA = registerIfLoaded("caelus", "scavenged_elytra",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_PIEZO = ITEMS.register("scavenged_piezo",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_SPINALINJECTOR = ITEMS.register("scavenged_spinalinjector",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_SANDEVISTAN = ITEMS.register("scavenged_sandevistan",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_EYEOFDEFENDER = ITEMS.register("scavenged_eyeofdefender",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> SCAVENGED_CONSCIOUSNESSTRANSMITTER = registerIfLoaded("createenchantmentindustry","scavenged_consciousnesstransmitter",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_CORTICALSTACK = registerIfLoaded("createenchantmentindustry","scavenged_corticalstack",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> SCAVENGED_ENDERJAMMER = ITEMS.register("scavenged_enderjammer",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_MATRIX = ITEMS.register("scavenged_matrix",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_NEURALCONTEXTUALIZER = ITEMS.register("scavenged_neuralcontextualizer",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_CYBERDECK = ITEMS.register("scavenged_cyberdeck",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_IDEM = ITEMS.register("scavenged_idem",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_CHIPWARESLOTS = ITEMS.register("scavenged_chipwareslots",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_CYBERHEART = ITEMS.register("scavenged_cyberheart",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_COUPLER = ITEMS.register("scavenged_coupler",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_CREEPERHEART = ITEMS.register("scavenged_creeperheart",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_DEFIBRILLATOR = ITEMS.register("scavenged_defibrillator",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_STEMCELL = ITEMS.register("scavenged_stemcell",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_PLATELETS = ITEMS.register("scavenged_platelets",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_HYPEROXYGENATION = ITEMS.register("scavenged_hyperoxygenation",
            () -> new Item(new Item.Properties().stacksTo(1)){
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_OXYGEN = ITEMS.register("scavenged_oxygen",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_ADRENALINE = ITEMS.register("scavenged_adrenaline",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_BATTERY = ITEMS.register("scavenged_battery",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_DIAMONDWAFERSTACK = ITEMS.register("scavenged_diamondwaferstack",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_DUALISTICCONVERTER = ITEMS.register("scavenged_dualisticconverter",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_LIVERFILTER = ITEMS.register("scavenged_liverfilter",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_METABOLIC = ITEMS.register("scavenged_metabolic",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_DENSEBATTERY = ITEMS.register("scavenged_densebattery",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_ARTERIALTURBINE = ITEMS.register("scavenged_arterialturbine",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_CHROMATOPHORES = ITEMS.register("scavenged_chromatophores",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_SYNTHSKIN = ITEMS.register("scavenged_synthskin",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_IMMUNO = ITEMS.register("scavenged_immuno",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_FACEPLATE = ITEMS.register("scavenged_faceplate",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_NETHERITEPLATING = ITEMS.register("scavenged_netheriteplating",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_SOLARSKIN = ITEMS.register("scavenged_solarskin",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_SUBDERMALARMOR = ITEMS.register("scavenged_subdermalarmor",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_SUBDERMALSPIKES = ITEMS.register("scavenged_subdermalspikes",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_SYNTHETICSETULES = ITEMS.register("scavenged_syntheticsetules",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_METALPLATING = ITEMS.register("scavenged_metalplating",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> SCAVENGED_SWEAT = registerIfLoaded("cold_sweat","scavenged_sweat",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> SCAVENGED_SYNTHMUSCLE = ITEMS.register("scavenged_synthmuscle",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_WIREDREFLEXES = ITEMS.register("scavenged_wiredreflexes",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                        tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });


    public static <T extends Item> DeferredItem<T> registerIfLoaded(String modid, String name, Supplier<T> supplier) {
        if (ModList.get().isLoaded(modid)) {
            return ModItems.ITEMS.register(name, supplier);
        }
        return null;
    }

    public static <T extends Item> DeferredItem<T> registerIfNotLoaded(String modid, String name, Supplier<T> supplier) {
        if (!ModList.get().isLoaded(modid)) {
            return ModItems.ITEMS.register(name, supplier);
        }
        return null;
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
