package com.perigrine3.createcybernetics.common.capabilities;

import com.perigrine3.createcybernetics.api.ICyberwareData;
import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class ModCapabilities {

    public static final EntityCapability<ICyberwareData, Void> CYBERWARE_CAPABILITY =
            EntityCapability.create(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware_data"), ICyberwareData.class, Void.class);

    public static void register(IEventBus eventBus) {
        eventBus.addListener(ModCapabilities::onRegisterCapabilities);
    }

    private static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerEntity(CYBERWARE_CAPABILITY, EntityType.PLAYER,
                (player, context) -> new PlayerCyberwareData());
    }

    public static ICyberwareData getFromPlayer(Player player) {
        return player.getCapability(CYBERWARE_CAPABILITY, null);
    }
}
