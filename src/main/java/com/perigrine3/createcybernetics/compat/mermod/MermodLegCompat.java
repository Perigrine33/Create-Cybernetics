package com.perigrine3.createcybernetics.compat.mermod;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class MermodLegCompat {

    private MermodLegCompat() {}

    private static final String MERMOD_MODID = "mermod";

    private static final ResourceLocation SEA_NECKLACE_ID =
            ResourceLocation.fromNamespaceAndPath(MERMOD_MODID, "sea_necklace");

    private static final TagKey<net.minecraft.world.item.Item> TAIL_MOISTURIZER_TAG =
            TagKey.create(net.minecraft.core.registries.Registries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(MERMOD_MODID, "tail_moisturizer_modifier"));

    private static final String MOISTURIZER_BASE_ID = "tail_moisturizer";

    private static final Map<Integer, VisibilitySnapshot> SNAPSHOTS = new HashMap<>();
    private static DataComponentType<?> CACHED_COMPONENT;

    private static boolean REFLECTION_READY = false;
    private static Method COMPONENT_MODIFIERS_METHOD;
    private static Method MAP_VALUES_METHOD;
    private static Method MODIFIER_ID_METHOD;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;
        if (!(event.getRenderer() instanceof PlayerRenderer renderer)) return;
        if (!(renderer.getModel() instanceof PlayerModel<?> model)) return;

        if (!shouldHideLegs(player)) return;

        SNAPSHOTS.put(player.getId(), VisibilitySnapshot.capture(model));

        model.leftLeg.visible = false;
        model.rightLeg.visible = false;
        model.leftPants.visible = false;
        model.rightPants.visible = false;
    }

    @SubscribeEvent
    public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;

        VisibilitySnapshot snap = SNAPSHOTS.remove(player.getId());
        if (snap == null) return;

        if (!(event.getRenderer() instanceof PlayerRenderer renderer)) return;
        if (!(renderer.getModel() instanceof PlayerModel<?> model)) return;

        snap.restore(model);
    }

    private static boolean shouldHideLegs(AbstractClientPlayer player) {
        if (!ModList.get().isLoaded(MERMOD_MODID)) return false;

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.isEmpty()) return false;

        ResourceLocation id = BuiltInRegistries.ITEM.getKey(chest.getItem());
        if (!SEA_NECKLACE_ID.equals(id)) return false;
        if (player.isInWaterOrBubble()) return true;
        boolean hasMoisturizerByItemTag = chest.getTags().anyMatch(t -> t.equals(TAIL_MOISTURIZER_TAG));
        if (hasMoisturizerByItemTag) return true;

        return hasMoisturizerModifierViaComponent(chest);
    }

    private static boolean hasMoisturizerModifierViaComponent(ItemStack necklace) {
        Object componentValue = getNecklaceModifiersComponentValue(necklace);
        if (componentValue == null) return false;

        ensureReflectionReady(componentValue.getClass());
        if (!REFLECTION_READY) return false;

        try {
            Object mapObj = COMPONENT_MODIFIERS_METHOD.invoke(componentValue);
            if (mapObj == null) return false;

            Object valuesObj = MAP_VALUES_METHOD.invoke(mapObj);
            if (!(valuesObj instanceof Collection<?> values)) return false;

            for (Object modifierObj : values) {
                if (modifierObj == null) continue;

                Object idObj = MODIFIER_ID_METHOD.invoke(modifierObj);
                if (!(idObj instanceof String rawId)) continue;

                if (isMoisturizerId(rawId)) return true;
            }
        } catch (Throwable ignored) {}

        return false;
    }

    private static boolean isMoisturizerId(String rawId) {
        if (rawId == null || rawId.isBlank()) return false;

        String s = rawId;
        int colon = s.indexOf(':');
        if (colon >= 0 && colon + 1 < s.length()) s = s.substring(colon + 1);

        if (s.endsWith("_modifier")) s = s.substring(0, s.length() - "_modifier".length());

        return MOISTURIZER_BASE_ID.equals(s);
    }

    private static Object getNecklaceModifiersComponentValue(ItemStack stack) {
        DataComponentType<?> type = getNecklaceModifiersComponentType();
        if (type == null) return null;
        return stack.get(type);
    }

    private static DataComponentType<?> getNecklaceModifiersComponentType() {
        if (CACHED_COMPONENT != null) return CACHED_COMPONENT;

        DataComponentType<?> t;

        t = BuiltInRegistries.DATA_COMPONENT_TYPE.get(ResourceLocation.fromNamespaceAndPath(MERMOD_MODID, "necklace_modifiers"));
        if (t != null) return CACHED_COMPONENT = t;

        t = BuiltInRegistries.DATA_COMPONENT_TYPE.get(ResourceLocation.fromNamespaceAndPath(MERMOD_MODID, "necklace_modifiers_component_type"));
        if (t != null) return CACHED_COMPONENT = t;

        t = BuiltInRegistries.DATA_COMPONENT_TYPE.get(ResourceLocation.fromNamespaceAndPath(MERMOD_MODID, "necklace_modifiers_component"));
        if (t != null) return CACHED_COMPONENT = t;

        DataComponentType<?> best = null;
        int bestScore = -1;

        for (ResourceLocation key : BuiltInRegistries.DATA_COMPONENT_TYPE.keySet()) {
            if (!MERMOD_MODID.equals(key.getNamespace())) continue;

            String p = key.getPath();
            int score = 0;
            if (p.contains("necklace")) score += 3;
            if (p.contains("modifier")) score += 3;
            if (p.contains("modifiers")) score += 2;
            if (p.contains("component")) score += 1;

            if (score > bestScore) {
                DataComponentType<?> candidate = BuiltInRegistries.DATA_COMPONENT_TYPE.get(key);
                if (candidate != null) {
                    best = candidate;
                    bestScore = score;
                }
            }
        }

        CACHED_COMPONENT = best;
        return CACHED_COMPONENT;
    }

    private static void ensureReflectionReady(Class<?> necklaceModifiersClass) {
        if (REFLECTION_READY) return;

        try {
            COMPONENT_MODIFIERS_METHOD = necklaceModifiersClass.getMethod("modifiers");
            MAP_VALUES_METHOD = Map.class.getMethod("values");
            Class<?> necklaceModifierClass = Class.forName("io.github.thatpreston.mermod.item.modifier.NecklaceModifier");
            MODIFIER_ID_METHOD = necklaceModifierClass.getMethod("id");

            REFLECTION_READY = true;
        } catch (Throwable t) {
            REFLECTION_READY = false;
            COMPONENT_MODIFIERS_METHOD = null;
            MAP_VALUES_METHOD = null;
            MODIFIER_ID_METHOD = null;
        }
    }

    private record VisibilitySnapshot(boolean leftLeg, boolean rightLeg, boolean leftPants, boolean rightPants) {

        static VisibilitySnapshot capture(PlayerModel<?> model) {
                return new VisibilitySnapshot(
                        model.leftLeg.visible,
                        model.rightLeg.visible,
                        model.leftPants.visible,
                        model.rightPants.visible
                );
            }

            void restore(PlayerModel<?> model) {
                model.leftLeg.visible = leftLeg;
                model.rightLeg.visible = rightLeg;
                model.leftPants.visible = leftPants;
                model.rightPants.visible = rightPants;
            }
        }
}
