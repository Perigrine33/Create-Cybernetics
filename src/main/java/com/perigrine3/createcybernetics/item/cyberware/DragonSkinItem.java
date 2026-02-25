package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;
import java.util.Set;

public class DragonSkinItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public DragonSkinItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModTags.Items.SKIN_ITEMS);
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.SKIN);
    }

    @Override
    public boolean replacesOrgan() {
        return true;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of(CyberwareSlot.SKIN);
    }

    @Override
    public Set<Item> incompatibleCyberware(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModItems.WETWARE_POLARBEARFUR.get());
    }

    @Override
    public int maxStacksPerSlotType(ItemStack stack, CyberwareSlot slotType) {
        return 1;
    }

    @Override
    public void onInstalled(Player player) {
        CyberwareAttributeHelper.applyModifier(player, "dragonskin_armor");
        CyberwareAttributeHelper.applyModifier(player, "dragonskin_toughness");
    }

    @Override
    public void onRemoved(Player player) {
        CyberwareAttributeHelper.removeModifier(player, "dragonskin_armor");
        CyberwareAttributeHelper.removeModifier(player, "dragonskin_toughness");
    }

    @Override
    public void onTick(Player player) {
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class DamageHooks {
        private DamageHooks() {}

        @SubscribeEvent
        public static void onIncomingDamage(LivingIncomingDamageEvent event) {
            LivingEntity living = event.getEntity();
            if (!(living instanceof Player player)) return;
            if (player.level().isClientSide) return;
            if (!isDragonSkinInstalled(player)) return;

            DamageSource src = event.getSource();
            if (!isDragonFireDamage(src)) return;

            event.setCanceled(true);
            event.setAmount(0.0F);
            event.setInvulnerabilityTicks(0);
        }
    }

    private static boolean isDragonFireDamage(DamageSource src) {
        if (src == null) return false;

        if (src.is(DamageTypes.DRAGON_BREATH)) return true;

        if (src.getDirectEntity() instanceof DragonFireball) return true;

        if (src.getDirectEntity() instanceof AreaEffectCloud cloud) {
            if (cloud.getParticle() != null && cloud.getParticle().getType() == ParticleTypes.DRAGON_BREATH) {
                return true;
            }
        }

        return false;
    }

    private static boolean isDragonSkinInstalled(Player player) {
        if (player == null) return false;
        if (!player.hasData(ModAttachments.CYBERWARE)) return false;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);

        InstalledCyberware[] arr = data.getAll().get(CyberwareSlot.SKIN);
        if (arr == null) return false;

        for (InstalledCyberware cw : arr) {
            if (cw == null) continue;

            ItemStack stack = cw.getItem();
            if (stack == null || stack.isEmpty()) continue;

            if (stack.is(ModItems.WETWARE_DRAGONSKIN.get())) return true;
        }

        return false;
    }
}