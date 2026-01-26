package com.perigrine3.createcybernetics.command;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.command.custom.ImplantsCommand;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class ModCommands {
    private ModCommands() {}

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ImplantsCommand.register(event.getDispatcher(), event.getBuildContext());
    }
}
