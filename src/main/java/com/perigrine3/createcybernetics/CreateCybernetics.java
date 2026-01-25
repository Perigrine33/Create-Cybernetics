package com.perigrine3.createcybernetics;

import com.perigrine3.createcybernetics.advancement.ModCriteria;
import com.perigrine3.createcybernetics.block.ModBlocks;
import com.perigrine3.createcybernetics.block.entity.ModBlockEntities;
import com.perigrine3.createcybernetics.common.attributes.ModAttributes;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.component.ModDataComponents;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.effect.PneumaticCalvesEffect;
import com.perigrine3.createcybernetics.enchantment.ModEnchantmentEffects;
import com.perigrine3.createcybernetics.entity.ModEntities;
import com.perigrine3.createcybernetics.entity.client.*;
import com.perigrine3.createcybernetics.item.ModCreativeModeTabs;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.loot.ModLootModifiers;
import com.perigrine3.createcybernetics.potion.ModPotions;
import com.perigrine3.createcybernetics.recipe.ModRecipeSerializers;
import com.perigrine3.createcybernetics.recipe.ModRecipes;
import com.perigrine3.createcybernetics.screen.ModMenuTypes;
import com.perigrine3.createcybernetics.screen.custom.*;
import com.perigrine3.createcybernetics.sound.ModSounds;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(CreateCybernetics.MODID)
public class CreateCybernetics {
    public static final String MODID = "createcybernetics";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public CreateCybernetics(IEventBus eventBus, ModContainer modContainer) {
        eventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);

        NeoForge.EVENT_BUS.addListener(PneumaticCalvesEffect.Events::onLivingJump);
        NeoForge.EVENT_BUS.register(PneumaticCalvesEffect.Events.class);


        eventBus.addListener(this::addCreative);

        ModCreativeModeTabs.register(eventBus);
        ModItems.register(eventBus);
        ModBlocks.register(eventBus);
        ModBlockEntities.register(eventBus);
        ModSounds.register(eventBus);
        ModEntities.register(eventBus);
        ModEffects.register(eventBus);
        ModMenuTypes.register(eventBus);
        ModEnchantmentEffects.register(eventBus);
        ModPotions.register(eventBus);

        ModLootModifiers.register(eventBus);
        ModCriteria.register(eventBus);
        ModDataComponents.register(eventBus);
        ModRecipes.register(eventBus);
        ModRecipeSerializers.register(eventBus);

        ModAttachments.register(eventBus);
        ModAttributes.register(eventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.insertAfter(Items.PITCHER_POD.getDefaultInstance(),
                    ModItems.DATURA_SEED_POD.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            event.insertAfter(Blocks.DEEPSLATE_IRON_ORE.asItem().getDefaultInstance(),
                    ModBlocks.TITANIUMORE_BLOCK.get().asItem().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModBlocks.TITANIUMORE_BLOCK.asItem().getDefaultInstance(),
                    ModBlocks.DEEPSLATE_TITANIUMORE_BLOCK.get().asItem().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(Blocks.RAW_IRON_BLOCK.asItem().getDefaultInstance(),
                    ModBlocks.RAW_TITANIUM_BLOCK.get().asItem().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.insertAfter(Items.CHAIN.getDefaultInstance(),
                    ModBlocks.TITANIUM_BLOCK.get().asItem().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModBlocks.TITANIUM_BLOCK.asItem().getDefaultInstance(),
                    ModBlocks.SMOOTH_TITANIUM.get().asItem().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModBlocks.SMOOTH_TITANIUM.asItem().getDefaultInstance(),
                    ModBlocks.TITANIUM_GRATE.get().asItem().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModBlocks.TITANIUM_GRATE.asItem().getDefaultInstance(),
                    ModBlocks.TITANIUM_CLAD_COPPER.get().asItem().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModBlocks.TITANIUM_CLAD_COPPER.asItem().getDefaultInstance(),
                    ModBlocks.ETCHED_TITANIUM_COPPER.get().asItem().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);


            event.insertAfter(ModBlocks.SMOOTH_TITANIUM.asItem().getDefaultInstance(),
                    ModBlocks.SMOOTH_TITANIUM_STAIRS.get().asItem().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModBlocks.SMOOTH_TITANIUM_STAIRS.asItem().getDefaultInstance(),
                    ModBlocks.SMOOTH_TITANIUM_SLAB.get().asItem().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModBlocks.TITANIUM_CLAD_COPPER.asItem().getDefaultInstance(),
                    ModBlocks.TITANIUM_CLAD_COPPER_STAIRS.get().asItem().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModBlocks.TITANIUM_CLAD_COPPER_STAIRS.asItem().getDefaultInstance(),
                    ModBlocks.TITANIUM_CLAD_COPPER_SLAB.get().asItem().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModBlocks.ETCHED_TITANIUM_COPPER.asItem().getDefaultInstance(),
                    ModBlocks.ETCHED_TITANIUM_COPPER_STAIRS.get().asItem().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModBlocks.ETCHED_TITANIUM_COPPER_STAIRS.asItem().getDefaultInstance(),
                    ModBlocks.ETCHED_TITANIUM_COPPER_SLAB.get().asItem().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.SMASHER_SPAWN_EGG);
            event.accept(ModItems.CYBERZOMBIE_SPAWN_EGG);
            event.accept(ModItems.CYBERSKELETON_SPAWN_EGG);
        }

        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.insertAfter(Items.PHANTOM_MEMBRANE.getDefaultInstance(),
                    ModItems.DATURA_FLOWER.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.insertAfter(Items.COOKED_RABBIT.getDefaultInstance(),
                    ModItems.BODYPART_BRAIN.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.BODYPART_BRAIN.get().getDefaultInstance(),
                    ModItems.COOKED_BRAIN.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.COOKED_BRAIN.get().getDefaultInstance(),
                    ModItems.BODYPART_HEART.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.BODYPART_HEART.get().getDefaultInstance(),
                    ModItems.COOKED_HEART.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.COOKED_HEART.get().getDefaultInstance(),
                    ModItems.BODYPART_LIVER.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.BODYPART_LIVER.get().getDefaultInstance(),
                    ModItems.COOKED_LIVER.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.COOKED_LIVER.get().getDefaultInstance(),
                    ModItems.BONE_MARROW.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            if (ModItems.ANDOUILLE_SAUSAGE != null && ModItems.ROASTED_ANDOUILLE != null && ModItems.GROUND_OFFAL != null && ModItems.BRAIN_STEW != null ) {
                event.insertAfter(ModItems.BONE_MARROW.get().getDefaultInstance(),
                        ModItems.ANDOUILLE_SAUSAGE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                event.insertAfter(ModItems.ANDOUILLE_SAUSAGE.get().getDefaultInstance(),
                        ModItems.ROASTED_ANDOUILLE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                event.insertAfter(ModItems.ROASTED_ANDOUILLE.get().getDefaultInstance(),
                        ModItems.GROUND_OFFAL.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                event.insertAfter(ModItems.GROUND_OFFAL.get().getDefaultInstance(),
                        ModItems.BRAIN_STEW.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntities.NUGGET_PROJECTILE.get(), NuggetProjectileRenderer::new);
            EntityRenderers.register(ModEntities.EMP_GRENADE_PROJECTILE.get(), ThrownItemRenderer::new);

            EntityRenderers.register(ModEntities.SMASHER.get(), SmasherRenderer::new);
            EntityRenderers.register(ModEntities.CYBERZOMBIE.get(), CyberzombieRenderer::new);
            EntityRenderers.register(ModEntities.CYBERSKELETON.get(), CyberskeletonRenderer::new);
            EntityRenderers.register(ModEntities.GUARDIAN_BEAM.get(), GuardianBeamRenderer::new);
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenuTypes.ROBOSURGEON_MENU.get(), RobosurgeonScreen::new);
            event.register(ModMenuTypes.ENGINEERING_TABLE_MENU.get(), EngineeringTableScreen::new);
            event.register(ModMenuTypes.EXPANDED_INVENTORY_MENU.get(), ExpandedInventoryScreen::new);
            event.register(ModMenuTypes.CHIPWARE_MINI_MENU.get(), ChipwareMiniScreen::new);
            event.register(ModMenuTypes.SPINAL_INJECTOR_MENU.get(), SpinalInjectorScreen::new);
            event.register(ModMenuTypes.ARM_CANNON_MENU.get(), ArmCannonScreen::new);
        }
    }


}
