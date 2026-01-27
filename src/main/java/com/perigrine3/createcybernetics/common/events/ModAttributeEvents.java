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
        event.add(EntityType.PLAYER, ModAttributes.XP_GAIN_MULTIPLIER);
        event.add(EntityType.PLAYER, ModAttributes.ORE_DROP_MULTIPLIER);
        event.add(EntityType.PLAYER, ModAttributes.HAGGLING);
        event.add(EntityType.PLAYER, ModAttributes.ENDER_PEARL_DAMAGE);
        event.add(EntityType.PLAYER, ModAttributes.ARROW_INACCURACY);
        event.add(EntityType.PLAYER, ModAttributes.BREEDING_MULTIPLIER);
        event.add(EntityType.PLAYER, ModAttributes.CROP_MULTIPLIER);
        event.add(EntityType.PLAYER, ModAttributes.ELYTRA_SPEED);
        event.add(EntityType.PLAYER, ModAttributes.ELYTRA_HANDLING);
        event.add(EntityType.PLAYER, ModAttributes.INSOMNIA);
    }
}
