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
    public static final Supplier<SoundEvent> MANTIS_OPEN = registerSoundEvent("mantis_open");
    public static final Supplier<SoundEvent> SANDY_STARTUP = registerSoundEvent("sandy_startup");
//UI SOUNDS
    public static final Supplier<SoundEvent> METAL_CRUSHING = registerSoundEvent("metal_crushing");
    public static final Supplier<SoundEvent> SURGERY = registerSoundEvent("surgery");
    public static final Supplier<SoundEvent> SURGERY_TABLE = registerSoundEvent("surgery_table");
//EFFECT SOUNDS
    public static final Supplier<SoundEvent> AUTOINJECTOR = registerSoundEvent("autoinjector");
    public static final Supplier<SoundEvent> GLITCHY = registerSoundEvent("glitchy");
    public static final Supplier<SoundEvent> CYBERPSYCHOSIS_SCARES = registerSoundEvent("cyberpsychosis_scares");

//ENTITY SOUNDS
    public static final Supplier<SoundEvent> SMASHER_IDLE = registerSoundEvent("smasher_idle");
    public static final Supplier<SoundEvent> SMASHER_STEP = registerSoundEvent("smasher_step");
    public static final Supplier<SoundEvent> PIGSTROM_IDLE = registerSoundEvent("pigstrom_idle");


//MUSIC DISCS
    public static final Supplier<SoundEvent> CYBERPSYCHO = registerSoundEvent("cyberpsycho");
    public static final ResourceKey<JukeboxSong> CYBERPSYCHO_KEY = createSong("cyberpsycho");
    public static final Supplier<SoundEvent> NEON_OVERLORDS = registerSoundEvent("neon_overlords");
    public static final ResourceKey<JukeboxSong> NEON_OVERLORDS_KEY = createSong("neon_overlords");
    public static final Supplier<SoundEvent> NEUROHACK = registerSoundEvent("neurohack");
    public static final ResourceKey<JukeboxSong> NEUROHACK_KEY = createSong("neurohack");
    public static final Supplier<SoundEvent> THE_GRID = registerSoundEvent("the_grid");
    public static final ResourceKey<JukeboxSong> THE_GRID_KEY = createSong("the_grid");

    public static final Supplier<SoundEvent> DANGER_SNOW = registerSoundEvent("danger_snow");
    public static final ResourceKey<JukeboxSong> DANGER_SNOW_KEY = createSong("danger_snow");
    public static final Supplier<SoundEvent> DESCENT_INTO_MADNESS = registerSoundEvent("descent_into_madness");
    public static final ResourceKey<JukeboxSong> DESCENT_INTO_MADNESS_KEY = createSong("descent_into_madness");
    public static final Supplier<SoundEvent> DARK = registerSoundEvent("dark");
    public static final ResourceKey<JukeboxSong> DARK_KEY = createSong("dark");
    public static final Supplier<SoundEvent> MY_RETRO_JACKET = registerSoundEvent("my_retro_jacket");
    public static final ResourceKey<JukeboxSong> MY_RETRO_JACKET_KEY = createSong("my_retro_jacket");
    public static final Supplier<SoundEvent> GAMEBOY_GIRL = registerSoundEvent("gameboy_girl");
    public static final ResourceKey<JukeboxSong> GAMEBOY_GIRL_KEY = createSong("gameboy_girl");
    public static final Supplier<SoundEvent> NIGHTMARES_ON_HAUPTSTRASSE = registerSoundEvent("nightmares_on_hauptstrasse");
    public static final ResourceKey<JukeboxSong> NIGHTMARES_ON_HAUPTSTRASSE_KEY = createSong("nightmares_on_hauptstrasse");
    public static final Supplier<SoundEvent> INTERGALACTIC_WALK = registerSoundEvent("intergalactic_walk");
    public static final ResourceKey<JukeboxSong> INTERGALACTIC_WALK_KEY = createSong("intergalactic_walk");
    public static final Supplier<SoundEvent> SENSATION = registerSoundEvent("sensation");
    public static final ResourceKey<JukeboxSong> SENSATION_KEY = createSong("sensation");
    public static final Supplier<SoundEvent> SHOTGUN = registerSoundEvent("shotgun");
    public static final ResourceKey<JukeboxSong> SHOTGUN_KEY = createSong("shotgun");

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