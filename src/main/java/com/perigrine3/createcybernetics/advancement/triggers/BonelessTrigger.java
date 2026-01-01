package com.perigrine3.createcybernetics.advancement.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.perigrine3.createcybernetics.advancement.ModCriteria;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.event.custom.CyberwareSurgeryEvent;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.Optional;

public final class BonelessTrigger extends SimpleCriterionTrigger<BonelessTrigger.Instance> {

    public void trigger(ServerPlayer player) {
        this.trigger(player, inst -> inst.matches());
    }

    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public record Instance(Optional<ContextAwarePredicate> player)
            implements SimpleInstance {

        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(b -> b.group(EntityPredicate.ADVANCEMENT_CODEC
                .optionalFieldOf("player").forGetter(Instance::player)).apply(b, Instance::new));

        public boolean matches() {
            return true;
        }

        public static Criterion<Instance> any() {
            return ModCriteria.BONELESS.get()
                    .createCriterion(new Instance(Optional.empty()));
        }
    }
}
