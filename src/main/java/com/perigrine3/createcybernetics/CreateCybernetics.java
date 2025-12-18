package com.perigrine3.createcybernetics;

import com.perigrine3.createcybernetics.block.ModBlocks;
import com.perigrine3.createcybernetics.block.entity.ModBlockEntities;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.component.ModDataComponents;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.entity.ModEntities;
import com.perigrine3.createcybernetics.entity.client.*;
import com.perigrine3.createcybernetics.item.ModCreativeModeTabs;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.loot.ModLootModifiers;
import com.perigrine3.createcybernetics.screen.ModMenuTypes;
import com.perigrine3.createcybernetics.screen.custom.RobosurgeonScreen;
import com.perigrine3.createcybernetics.sound.ModSounds;
import net.minecraft.client.renderer.entity.EntityRenderers;
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

        eventBus.addListener(this::addCreative);

        ModCreativeModeTabs.register(eventBus);
        ModItems.register(eventBus);
        ModBlocks.register(eventBus);
        ModBlockEntities.register(eventBus);
        ModSounds.register(eventBus);
        ModEntities.register(eventBus);
        ModEffects.register(eventBus);
        ModMenuTypes.register(eventBus);

        ModLootModifiers.register(eventBus);
        ModDataComponents.register(eventBus);

        ModAttachments.register(eventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(ModBlocks.TITANIUMORE_BLOCK);
            event.accept(ModBlocks.DEEPSLATE_TITANIUMORE_BLOCK);
            event.accept(ModBlocks.RAW_TITANIUM_BLOCK);
        }

        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModBlocks.TITANIUM_BLOCK);
        }

        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.SMASHER_SPAWN_EGG);
            event.accept(ModItems.CYBERZOMBIE_SPAWN_EGG);
            event.accept(ModItems.CYBERSKELETON_SPAWN_EGG);
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
            EntityRenderers.register(ModEntities.SMASHER.get(), SmasherRenderer::new);
            EntityRenderers.register(ModEntities.CYBERZOMBIE.get(), CyberzombieRenderer::new);
            EntityRenderers.register(ModEntities.CYBERSKELETON.get(), CyberskeletonRenderer::new);
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenuTypes.ROBOSURGEON_MENU.get(), RobosurgeonScreen::new);
        }
    }


}
