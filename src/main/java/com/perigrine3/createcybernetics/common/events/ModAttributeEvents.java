package com.perigrine3.createcybernetics.common.events;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.common.attributes.ModAttributes;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModAttributeEvents {
    private ModAttributeEvents() {}

    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {

    }
}
