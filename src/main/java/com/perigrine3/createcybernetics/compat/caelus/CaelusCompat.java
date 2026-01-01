package com.perigrine3.createcybernetics.compat.caelus;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

public final class CaelusCompat {
    private CaelusCompat() {}

    public static final String MODID = "caelus";

    private static Holder<Attribute> fallFlyingAttr;
    private static boolean resolved = false;

    public static boolean isLoaded() {
        return ModList.get().isLoaded(MODID);
    }

    public static AttributeInstance getFallFlyingInstance(Player player) {
        if (!isLoaded() || player == null) return null;
        resolveOnce();
        if (fallFlyingAttr == null) return null;
        return player.getAttribute(fallFlyingAttr);
    }

    public static void addOrUpdateFallFlyingTransient(Player player, ResourceLocation modifierId, double amount) {
        AttributeInstance inst = getFallFlyingInstance(player);
        if (inst == null) return;

        inst.addOrUpdateTransientModifier(new AttributeModifier(modifierId, amount, Operation.ADD_VALUE));
    }

    public static void removeFallFlyingModifier(Player player, ResourceLocation modifierId) {
        AttributeInstance inst = getFallFlyingInstance(player);
        if (inst == null) return;

        inst.removeModifier(modifierId);
    }

    @SuppressWarnings("unchecked")
    private static void resolveOnce() {
        if (resolved) return;
        resolved = true;

        try {
            Class<?> apiClass = Class.forName("com.illusivesoulworks.caelus.api.CaelusApi");
            Object api = apiClass.getMethod("getInstance").invoke(null);
            Object holderObj = apiClass.getMethod("getFallFlyingAttribute").invoke(api);

            fallFlyingAttr = (Holder<Attribute>) holderObj;
        } catch (Throwable t) {
            fallFlyingAttr = null;
        }
    }
}
