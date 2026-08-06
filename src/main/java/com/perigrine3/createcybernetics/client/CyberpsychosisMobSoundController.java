package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class CyberpsychosisMobSoundController {

    private CyberpsychosisMobSoundController() {
    }

    @SubscribeEvent
    public static void onPlaySoundAtEntity(PlayLevelSoundEvent.AtEntity event) {
        if (!CyberpsychosisMobHallucinationController.isHallucinated(event.getEntity())) return;

        CyberpsychosisMobHallucinationController.HallucinationKind kind = CyberpsychosisMobHallucinationController.getHallucinationKind(event.getEntity());

        if (kind == null) return;

        Holder<SoundEvent> replacementSound = BuiltInRegistries.SOUND_EVENT.wrapAsHolder(kind.getAmbientSound());

        event.setSound(replacementSound);
        event.setSource(SoundSource.HOSTILE);
        event.setNewVolume(Math.max(0.65F, event.getOriginalVolume()));
        event.setNewPitch(Mth.clamp(event.getOriginalPitch() * 0.82F, 0.55F, 1.15F));
    }
}