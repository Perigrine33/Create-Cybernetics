package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.advancement.ModCriteria;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.common.surgery.DefaultOrgans;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;

import java.util.List;
import java.util.Set;

public class SculkHeartItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public SculkHeartItem(Properties props, int humanityCost) {
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
        return Set.of(CyberwareSlot.HEART);
    }

    @Override
    public boolean replacesOrgan() {
        return true;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of(CyberwareSlot.HEART);
    }

    @Override
    public void onInstalled(Player player) {
    }

    @Override
    public void onRemoved(Player player) {
    }

    @Override
    public void onTick(Player player) {
        Level level = player.level();
        if (level.isClientSide) return;
        if ((player.tickCount % 30) != 0) return;

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.PLAYERS, 1.0F, 1.0F);

        AABB box = player.getBoundingBox().inflate(10.0D);
        List<Player> nearbyPlayers = level.getEntitiesOfClass(Player.class, box, p -> p.isAlive() && p != player);

        if (!nearbyPlayers.isEmpty()) {
            MobEffectInstance darkness = new MobEffectInstance(MobEffects.DARKNESS, 30, 0, true, false, false);
            for (Player p : nearbyPlayers) {
                p.addEffect(darkness);
            }
        }

        if (!player.hasEffect(ModEffects.SCULKED_EFFECT)) {
            if ((player.tickCount % 24000) == 0) {
                if (player.getRandom().nextFloat() < 0.25f) {
                    player.addEffect(new MobEffectInstance(ModEffects.SCULKED_EFFECT, Integer.MAX_VALUE, 0, false, false, true));
                }
            }
        }
    }
}
