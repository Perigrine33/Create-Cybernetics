package com.perigrine3.createcybernetics.advancement.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.perigrine3.createcybernetics.advancement.ModCriteria;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public final class ThoughtsNotFoundTrigger extends SimpleCriterionTrigger<ThoughtsNotFoundTrigger.Instance> {

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
            return ModCriteria.THOUGHTS_NOT_FOUND.get()
                    .createCriterion(new Instance(Optional.empty()));
        }
    }
}
