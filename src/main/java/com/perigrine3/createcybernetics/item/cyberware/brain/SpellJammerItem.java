package com.perigrine3.createcybernetics.item.cyberware.brain;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareData;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.EntityCyberwareData;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.ModMobAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.compat.ironsspells.IronsSpellbooksManaCompat;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class SpellJammerItem extends Item implements ICyberwareItem {
    private static final String NBT_SUPPRESSED_UNTIL = "cc_spelljammer_suppressed_until";

    private final int humanityCost;

    public SpellJammerItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.translatable("tooltip.createcybernetics.brainupgrades_enderjammer.energy")
                    .withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public boolean requiresEnergyToFunction(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return true;
    }

    @Override
    public int getEnergyUsedPerTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return 5;
    }

    @Override
    public Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModTags.Items.BRAIN_ITEMS);
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.BRAIN);
    }

    @Override
    public boolean replacesOrgan() {
        return false;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of();
    }

    @Override
    public void onInstalled(LivingEntity entity) {
    }

    @Override
    public void onRemoved(LivingEntity entity) {
    }

    @Override
    public void onTick(LivingEntity entity) {
    }

    @Override
    public void onTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot, int index) {
        if (entity.level().isClientSide) return;
        if ((entity.tickCount % 10) != 0) return;
        if (!isThisInstallPowered(entity, installedStack, slot, index)) return;

        double radius = 16.0D;
        var aabb = entity.getBoundingBox().inflate(radius);
        var targets = entity.level().getEntitiesOfClass(LivingEntity.class, aabb, LivingEntity::isAlive);
        long expireAt = entity.level().getGameTime() + 20L;

        for (LivingEntity target : targets) {
            target.getPersistentData().putLong(NBT_SUPPRESSED_UNTIL, expireAt);
            IronsSpellbooksManaCompat.drainMana(target, 50.0F);
        }
    }

    private static boolean isThisInstallPowered(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot, int index) {
        ICyberwareData data = getCyberwareData(entity);
        if (data == null) return false;

        InstalledCyberware installed = data.get(slot, index);
        if (installed == null) return false;

        ItemStack actualStack = installed.getItem();
        if (actualStack == null || actualStack.isEmpty()) return false;
        if (installedStack == null || installedStack.isEmpty()) return false;

        if (!ItemStack.isSameItemSameComponents(actualStack, installedStack)) return false;

        return installed.isPowered();
    }

    private static ICyberwareData getCyberwareData(LivingEntity entity) {
        if (entity == null) return null;

        if (entity instanceof Player player) {
            if (!player.hasData(ModAttachments.CYBERWARE)) return null;

            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            return data;
        }

        if (!entity.hasData(ModMobAttachments.CYBERENTITY_CYBERWARE)) return null;

        EntityCyberwareData data = entity.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
        return data;
    }
}