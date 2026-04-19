package com.perigrine3.createcybernetics.common.capabilities;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.ModEntities;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID)
public final class CyberentitySpawnEvents {

    private static final String NBT_CYBERWARE_ROLLED = "cc_cyberware_rolled";

    private CyberentitySpawnEvents() {}

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof Mob mob)) return;
        if (!shouldRollCyberware(mob)) return;
        if (mob.getPersistentData().getBoolean(NBT_CYBERWARE_ROLLED)) return;

        EntityCyberwareData data = mob.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
        CyberentityRolls.generateRandomCyberware(mob, data, mob.getRandom());
        mob.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 20, false, false, false));
        data.setDirty();

        mob.getPersistentData().putBoolean(NBT_CYBERWARE_ROLLED, true);
        mob.syncData(ModMobAttachments.CYBERENTITY_CYBERWARE);
        data.clean();
    }

    private static boolean shouldRollCyberware(Mob mob) {
        EntityType<?> type = mob.getType();
        return type == ModEntities.CYBERZOMBIE.get()
                || type == ModEntities.CYBERSKELETON.get()
                || type == ModEntities.SMASHER.get();
    }
}