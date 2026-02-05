package com.perigrine3.createcybernetics.compat.ironsspells;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.Set;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class IronsSpellbooksCastSuppressCompat {
    private IronsSpellbooksCastSuppressCompat() {}

    public static final String MODID = "irons_spellbooks";
    public static final String SUPPRESSED_UNTIL_TAG = "cc_spelljammer_suppressed_until";

    public static boolean isLoaded() {
        return ModList.get().isLoaded(MODID);
    }

    public static boolean isSuppressed(LivingEntity e) {
        if (e == null) return false;
        long until = e.getPersistentData().getLong(SUPPRESSED_UNTIL_TAG);
        return until > e.level().getGameTime();
    }

    private static final Set<String> SPELL_ENTITY_PATHS = Set.of(
            "acid_orb",
            "ball_lightning",
            "black_hole",
            "blood_needle",
            "blood_slash",
            "comet",
            "cone_of_cold",
            "creeper_head",
            "devour_jaw",
            "dragon_breath",
            "eldritch_blast",
            "electrocute",
            "fiery_dagger",
            "fire_arrow",
            "fire_breath",
            "fireball",
            "firebolt",
            "firefly_swarm",
            "guiding_bolt",
            "gust",
            "ice_block",
            "ice_spike",
            "ice_tomb",
            "icicle",
            "lightning_lance",
            "magic_arrow",
            "magic_missile",
            "magma_ball",
            "poison_arrow",
            "poison_breath",
            "poison_cloud",
            "portal",
            "ray_of_frost",
            "root",
            "shield",
            "skull_projectile",
            "small_magic_arrow",
            "snowball",
            "spectral_hammer",
            "summoned_weapons",
            "sunbeam",
            "target_area",
            "thrown_item",
            "thrown_spear",
            "thunderstep",
            "void_tentacle",
            "wall_of_fire",
            "wisp",

            "aoe_entity",
            "arrow_volley",
            "chain_lightning",
            "cone_part",
            "earthquake_aoe",
            "echoing_strike",
            "extended_evoker_fang",
            "extended_firework_rocket",
            "extended_lightning_bolt",
            "fire_eruption_aoe",
            "healing_aoe",
            "lightning_strike",
            "shield_part",
            "stomp_aoe",
            "wither_skull_projectile"
    );

    private static boolean isTargetedSpellEntity(Entity e) {
        ResourceLocation typeId = e.getType().builtInRegistryHolder().key().location();
        if (!MODID.equals(typeId.getNamespace())) return false;
        return SPELL_ENTITY_PATHS.contains(typeId.getPath());
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!isLoaded()) return;

        Level level = event.getLevel();
        if (level.isClientSide) return;

        Entity spawned = event.getEntity();

        if (!isTargetedSpellEntity(spawned)) {
            return;
        }

        if (spawned instanceof Projectile proj) {
            Entity owner = proj.getOwner();
            if (owner instanceof LivingEntity caster && isSuppressed(caster)) {
                event.setCanceled(true);
                return;
            }
        }

        final double suppressionRadius = 30.0;
        boolean nearSuppressed = !level.getEntitiesOfClass(
                LivingEntity.class,
                spawned.getBoundingBox().inflate(suppressionRadius),
                IronsSpellbooksCastSuppressCompat::isSuppressed
        ).isEmpty();

        if (nearSuppressed) {
            event.setCanceled(true);
        }
    }
}
