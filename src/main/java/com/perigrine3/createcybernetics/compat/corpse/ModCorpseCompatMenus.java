package com.perigrine3.createcybernetics.compat.corpse;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCorpseCompatMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(BuiltInRegistries.MENU, CreateCybernetics.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<CorpseCyberwareMenu>> CORPSE_CYBERWARE =
            MENUS.register("corpse_cyberware",
                    () -> IMenuTypeExtension.create(CorpseCyberwareMenu::new));

    private ModCorpseCompatMenus() {}

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}