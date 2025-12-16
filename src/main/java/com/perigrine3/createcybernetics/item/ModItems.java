package com.perigrine3.createcybernetics.item;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.ModEntities;
import com.perigrine3.createcybernetics.item.cyberware.*;
import com.perigrine3.createcybernetics.sound.ModSounds;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

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
    public static final DeferredItem<Item> EXPCAPSULE = ITEMS.register("expcapsule",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> NETHERITE_QPU = ITEMS.register("netherite_qpu",
            () -> new Item(new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.legendarycomponent_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }});

    public static final DeferredItem<Item> NEUROPOZYNE = ITEMS.register("neuropozyne",
            () -> new Item(new Item.Properties().stacksTo(16).food(ModFoodProperties.NEUROPOZYNE)) {
                @Override
                public UseAnim getUseAnimation(ItemStack stack) {
                    return UseAnim.BOW;
                }
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context , List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    if (Screen.hasShiftDown()) {
                        tooltipComponents.add(Component.translatable("tooltip.createcybernetics.neuropozyne.tooltip"));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                    }
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
    });

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
        () -> new DeferredSpawnEggItem(ModEntities.CYBERZOMBIE, 0x2596be, 0x3c5740,
                new Item.Properties()));


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



//BODY PARTS
    public static final DeferredItem<Item> BODYPART_RIGHTLEG = ITEMS.register("bodypart_rightleg",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BODYPART_LEFTLEG = ITEMS.register("bodypart_leftleg",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BODYPART_RIGHTARM = ITEMS.register("bodypart_rightarm",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BODYPART_LEFTARM = ITEMS.register("bodypart_leftarm",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

//INTERNAL WETWARE
    public static final DeferredItem<Item> BODYPART_SKELETON = ITEMS.register("bodypart_skeleton",
            () -> new Item(new Item.Properties().stacksTo(16)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BODYPART_BRAIN = ITEMS.register("bodypart_brain",
            () -> new Item(new Item.Properties().stacksTo(16)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BODYPART_EYEBALLS = ITEMS.register("bodypart_eyeballs",
            () -> new Item(new Item.Properties().stacksTo(16)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BODYPART_HEART = ITEMS.register("bodypart_heart",
            () -> new Item(new Item.Properties().stacksTo(16)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BODYPART_LUNGS = ITEMS.register("bodypart_lungs",
            () -> new Item(new Item.Properties().stacksTo(16)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BODYPART_LIVER = ITEMS.register("bodypart_liver",
            () -> new Item(new Item.Properties().stacksTo(16)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BODYPART_INTESTINES = ITEMS.register("bodypart_intestines",
            () -> new Item(new Item.Properties().stacksTo(16)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BODYPART_MUSCLE = ITEMS.register("bodypart_muscle",
            () -> new Item(new Item.Properties().stacksTo(16)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.wetware_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> BODYPART_SKIN = ITEMS.register("bodypart_skin",
            () -> new Item(new Item.Properties().stacksTo(16)) {
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
            () -> new CyberlegItem(new Item.Properties().stacksTo(1)) {
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
            () -> new CyberlegItem(new Item.Properties().stacksTo(1)) {
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
            () -> new CyberarmItem(new Item.Properties().stacksTo(1)) {
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
            () -> new CyberarmItem(new Item.Properties().stacksTo(1)) {
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
            () -> new LinearFrameItem(new Item.Properties().stacksTo(1)) {
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
            () -> new EyeUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new CybereyeModuleItem(new Item.Properties().stacksTo(1)) {
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
            public static final DeferredItem<Item> EYEUPGRADES_NAVIGATIONCHIP = ITEMS.register("eyeupgrades_navigationchip",
                () -> new CybereyeModuleItem(new Item.Properties().stacksTo(1)) {
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
        () -> new CybereyeModuleItem(new Item.Properties().stacksTo(1)) {
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
        () -> new CybereyeModuleItem(new Item.Properties().stacksTo(1)) {
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
        () -> new CybereyeModuleItem(new Item.Properties().stacksTo(1)) {
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
        () -> new CybereyeModuleItem(new Item.Properties().stacksTo(1)) {
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
        () -> new CybereyeModuleItem(new Item.Properties().stacksTo(1)) {
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

//ARM UPGRADES
    public static final DeferredItem<Item> ARMUPGRADES_ARMCANNON = ITEMS.register("armupgrades_armcannon",
        () -> new ArmUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new ArmUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new ArmUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new ArmUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new ArmUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new ArmUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new ArmUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new ArmUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new LegUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new LegUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new LegUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new LegUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new LegUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new LegUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new BoneUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new BoneUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new BoneUpgradeItem(new Item.Properties().stacksTo(1)) {
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
    public static final DeferredItem<Item> BONEUPGRADES_ELYTRA = ITEMS.register("boneupgrades_elytra",
        () -> new BoneUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new BoneUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new BoneUpgradeItem(new Item.Properties().stacksTo(1)) {
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

//BRAIN UPGRADES
    public static final DeferredItem<Item> BRAINUPGRADES_EYEOFDEFENDER = ITEMS.register("brainupgrade_eyeofdefender",
        () -> new BrainUpgradeItem(new Item.Properties().stacksTo(1)) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrade_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrade_eyeofdefender.tooltip1"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrade_eyeofdefender.tooltip2"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.brainupgrade_tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.hold_shift_down"));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });

    //Create: Enchantment Industry compat
        public static final DeferredItem<Item> BRAINUPGRADES_CONCIOUSNESSTRANSMITTER = ITEMS.register("brainupgrades_consciousnesstransmitter",
            () -> new BrainUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        public static final DeferredItem<Item> BRAINUPGRADES_CORTICALSTACK = ITEMS.register("brainupgrades_corticalstack",
            () -> new BrainUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new BrainUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new BrainUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new BrainUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new BrainUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new BrainUpgradeItem(new Item.Properties().stacksTo(1)) {
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

//HEART UPGRADES
    public static final DeferredItem<Item> HEARTUPGRADES_CYBERHEART = ITEMS.register("heartupgrades_cyberheart",
        () -> new HeartUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new HeartUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new HeartUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new HeartUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new HeartUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new HeartUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new LungUpgradeItem(new Item.Properties().stacksTo(1)){
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
        () -> new LungUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new OrganUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new OrganUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new OrganUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new OrganUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new OrganUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new OrganUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new OrganUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new OrganUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new SkinUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new SkinUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new SkinUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new SkinUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new SkinUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new SkinUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new SkinUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new SkinUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new SkinUpgradeItem(new Item.Properties().stacksTo(1)) {
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

    //COLD SWEAT COMPAT
        public static final DeferredItem<Item> SKINUPGRADES_SWEAT = ITEMS.register("skinupgrades_sweat",
            () -> new SkinUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new MuscleUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new MuscleUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        public static final DeferredItem<Item> WETWARE_BLUBBER = ITEMS.register("wetware_blubber",
                () -> new SkinUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new LungUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new LungUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new EyeUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new SkinUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new MuscleUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new LungReplacementItem(new Item.Properties().stacksTo(1)) {
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
        () -> new OrganUpgradeItem(new Item.Properties().stacksTo(1)) {
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
        () -> new LungReplacementItem(new Item.Properties().stacksTo(1)) {
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
    public static final DeferredItem<Item> SCAVENGED_NAVIGATIONCHIP = ITEMS.register("scavenged_navigationchip",
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
    public static final DeferredItem<Item> SCAVENGED_ELYTRA = ITEMS.register("scavenged_elytra",
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
    public static final DeferredItem<Item> SCAVENGED_EYEOFDEFENDER = ITEMS.register("scavenged_eyeofdefender",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_CONCIOUSNESSTRANSMITTER = ITEMS.register("scavenged_consciousnesstransmitter",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.createcybernetics.scavenged_tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SCAVENGED_CORTICALSTACK = ITEMS.register("scavenged_corticalstack",
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
    public static final DeferredItem<Item> SCAVENGED_SWEAT = ITEMS.register("scavenged_sweat",
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

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
