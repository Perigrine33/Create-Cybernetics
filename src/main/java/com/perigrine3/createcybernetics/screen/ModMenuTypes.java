package com.perigrine3.createcybernetics.screen;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.screen.custom.TattooMenu;
import com.perigrine3.createcybernetics.screen.custom.arm_cannon.ArmCannonMenu;
import com.perigrine3.createcybernetics.screen.custom.chipware.ChipwareMiniMenu;
import com.perigrine3.createcybernetics.screen.custom.crafting.EngineeringTableMenu;
import com.perigrine3.createcybernetics.screen.custom.crafting.ExpandedInventoryMenu;
import com.perigrine3.createcybernetics.screen.custom.crafting.GraftingTableMenu;
import com.perigrine3.createcybernetics.screen.custom.cyberdeck.CyberdeckMenu;
import com.perigrine3.createcybernetics.screen.custom.heat_engine.HeatEngineMenu;
import com.perigrine3.createcybernetics.screen.custom.spinal_injector.SpinalInjectorMenu;
import com.perigrine3.createcybernetics.screen.custom.surgery.ripper.RipperTradeMenu;
import com.perigrine3.createcybernetics.screen.custom.surgery.ripper.SurgeryPaymentMenu;
import com.perigrine3.createcybernetics.screen.custom.surgery.robosurgeon.RobosurgeonMenu;
import com.perigrine3.createcybernetics.screen.custom.surgery.surgery_table.SurgeryTableMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, CreateCybernetics.MODID);


    public static final DeferredHolder<MenuType<?>, MenuType<RobosurgeonMenu>> ROBOSURGEON_MENU =
            registerMenuType("robosurgeon_menu", RobosurgeonMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<EngineeringTableMenu>> ENGINEERING_TABLE_MENU =
            registerMenuType("engineering_table_menu", EngineeringTableMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<GraftingTableMenu>> GRAFTING_TABLE_MENU =
            registerMenuType("grafting_table_menu", GraftingTableMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<SurgeryTableMenu>> SURGERY_TABLE_MENU =
            registerMenuType("surgery_table_menu", SurgeryTableMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<SurgeryPaymentMenu>> SURGERY_PAYMENT_MENU =
            registerMenuType("surgery_payment_menu", SurgeryPaymentMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<ExpandedInventoryMenu>> EXPANDED_INVENTORY_MENU =
            registerMenuType("expanded_inventory_menu", ExpandedInventoryMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<ChipwareMiniMenu>> CHIPWARE_MINI_MENU =
            registerMenuType("chipware_mini_menu", ChipwareMiniMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<SpinalInjectorMenu>> SPINAL_INJECTOR_MENU =
            registerMenuType("spinal_injector_menu", SpinalInjectorMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<ArmCannonMenu>> ARM_CANNON_MENU =
            registerMenuType("arm_cannon_menu", ArmCannonMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<HeatEngineMenu>> HEAT_ENGINE_MENU =
            registerMenuType("heat_engine_menu", HeatEngineMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<CyberdeckMenu>> CYBERDECK_MENU =
            registerMenuType("cyberdeck_menu", CyberdeckMenu::new);


    public static final DeferredHolder<MenuType<?>, MenuType<RipperTradeMenu>> RIPPER_TRADE_MENU =
            registerMenuType("ripper_trade_menu", RipperTradeMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<TattooMenu>> TATTOO_MENU =
            registerMenuType("tattoo_menu", TattooMenu::new);


    private static <T extends AbstractContainerMenu>DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
