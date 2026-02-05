package com.perigrine3.createcybernetics.compat.ironsspells;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public final class IronsSpellbooksCyberwareAttributes {
    private IronsSpellbooksCyberwareAttributes() {}

    private static ResourceLocation cc(String path) {
        return ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, path);
    }

    public static void register() {
        if (!IronsSpellbooksCompat.isLoaded()) return;

        CyberwareAttributeHelper.registerModifierDynamicAttribute(
                "irons_lightning_weakness", IronsSpellbooksCompat.ATTR_LIGHTNING_MAGIC_RESIST,
                cc("irons_lightning_weakness"),
                -0.10, AttributeModifier.Operation.ADD_VALUE);

        CyberwareAttributeHelper.registerModifierDynamicAttribute(
                "irons_spell_resist_manaskin", IronsSpellbooksCompat.ATTR_SPELL_RESIST,
                cc("irons_spell_resist_manaskin"),
                1, AttributeModifier.Operation.ADD_VALUE);

        CyberwareAttributeHelper.registerModifierDynamicAttribute(
                "irons_addmana_manabattery1", IronsSpellbooksCompat.ATTR_MAX_MANA,
                cc("irons_addmana_manabattery1"),
                100, AttributeModifier.Operation.ADD_VALUE);
        CyberwareAttributeHelper.registerModifierDynamicAttribute(
                "irons_addmana_manabattery2", IronsSpellbooksCompat.ATTR_MAX_MANA,
                cc("irons_addmana_manabattery2"),
                100, AttributeModifier.Operation.ADD_VALUE);
        CyberwareAttributeHelper.registerModifierDynamicAttribute(
                "irons_addmana_manabattery3", IronsSpellbooksCompat.ATTR_MAX_MANA,
                cc("irons_addmana_manabattery3"),
                100, AttributeModifier.Operation.ADD_VALUE);

    }
}
