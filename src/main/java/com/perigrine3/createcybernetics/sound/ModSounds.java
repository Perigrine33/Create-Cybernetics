package com.perigrine3.createcybernetics.sound;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, CreateCybernetics.MODID);

//CYBERWARE SOUNDS
    public static final Supplier<SoundEvent> METAL_DETECTOR_BEEPS = registerSoundEvent("metal_detector_beeps");
    public static final Supplier<SoundEvent> RETRACTABLE_CLAWS_SNIKT = registerSoundEvent("retractable_claws_snikt");
    public static final Supplier<SoundEvent> SANDY_STARTUP = registerSoundEvent("sandy_startup");
//UI SOUNDS
    public static final Supplier<SoundEvent> METAL_CRUSHING = registerSoundEvent("metal_crushing");
//EFFECT SOUNDS
    public static final Supplier<SoundEvent> GLITCHY = registerSoundEvent("glitchy");


//MUSIC DISCS
    public static final Supplier<SoundEvent> CYBERPSYCHO = registerSoundEvent("cyberpsycho");
    public static final ResourceKey<JukeboxSong> CYBERPSYCHO_KEY = createSong("cyberpsycho");
    public static final Supplier<SoundEvent> NEON_OVERLORDS = registerSoundEvent("neon_overlords");
    public static final ResourceKey<JukeboxSong> NEON_OVERLORDS_KEY = createSong("neon_overlords");
    public static final Supplier<SoundEvent> NEUROHACK = registerSoundEvent("neurohack");
    public static final ResourceKey<JukeboxSong> NEUROHACK_KEY = createSong("neurohack");
    public static final Supplier<SoundEvent> THE_GRID = registerSoundEvent("the_grid");
    public static final ResourceKey<JukeboxSong> THE_GRID_KEY = createSong("the_grid");

    private static ResourceKey<JukeboxSong> createSong(String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, name));
    }

    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}