package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = CreateCybernetics.MODID)
public class TacticalInkSacItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final String NBT_INSTALLED = "cc_tactical_inksac_installed";
    private static final double TRIGGER_RADIUS = 3.0D;

    public TacticalInkSacItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost).withStyle(ChatFormatting.GOLD));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.ORGANS);
    }

    @Override
    public boolean replacesOrgan() {
        return true;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of(CyberwareSlot.ORGANS);
    }

    @Override
    public TagKey<Item> getReplacedOrganItemTag(ItemStack installedStack, CyberwareSlot slot) {
        return ModTags.Items.INTESTINES_ITEMS;
    }

    @Override
    public void onInstalled(Player player) {
        if (!player.level().isClientSide) {
            player.getPersistentData().putBoolean(NBT_INSTALLED, true);
        }
    }

    @Override
    public void onRemoved(Player player) {
        if (!player.level().isClientSide) {
            player.getPersistentData().putBoolean(NBT_INSTALLED, false);
        }
    }

    @Override
    public void onTick(Player player) {
    }

    private static boolean isInstalled(Player player) {
        return player.getPersistentData().getBoolean(NBT_INSTALLED);
    }

    @SubscribeEvent
    public static void onPlayerDamaged(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;
        if (!isInstalled(player)) return;

        LivingEntity attacker = resolveLivingAttacker(event.getSource());
        if (attacker == null || attacker == player || !attacker.isAlive()) return;

        double r2 = TRIGGER_RADIUS * TRIGGER_RADIUS;
        if (attacker.distanceToSqr(player) > r2) return;

        MobEffectInstance existing = attacker.getEffect(MobEffects.BLINDNESS);
        if (existing == null || existing.getDuration() < 10) {
            attacker.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 600, 3, true, true, true));
            attacker.addEffect(new MobEffectInstance(ModEffects.INKED_EFFECT, 600, 0, true, true, true));
        }

        if (player.level() instanceof ServerLevel serverLevel) {
            spawnInkSpray(serverLevel, player, attacker);
        }
    }

    private static LivingEntity resolveLivingAttacker(DamageSource source) {
        Entity credited = source.getEntity();
        if (credited instanceof LivingEntity le) return le;

        Entity direct = source.getDirectEntity();
        if (direct instanceof LivingEntity le) return le;

        return null;
    }

    private static void spawnInkSpray(ServerLevel level, Player player, LivingEntity attacker) {
        Vec3 start = player.getEyePosition();
        Vec3 end = attacker.getEyePosition();

        Vec3 delta = end.subtract(start);
        double len = delta.length();
        if (len < 0.001D) return;

        Vec3 dir = delta.scale(1.0D / len);
        int steps = Mth.clamp((int) (len * 4.0D), 6, 20);

        for (int i = 0; i <= steps; i++) {
            double t = (double) i / (double) steps;
            Vec3 p = start.add(dir.scale(len * t));
            level.sendParticles(ParticleTypes.SQUID_INK, p.x, p.y, p.z, 6, 0.06D, 0.06D, 0.06D, 0.0D);
        }

        level.sendParticles(ParticleTypes.SQUID_INK, end.x, end.y, end.z, 12, 0.20D, 0.20D, 0.20D, 0.0D);
    }
}
