package com.perigrine3.createcybernetics.network.handler;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.effect.quickhacks.OpticMalfunctionQuickhackEffect;
import com.perigrine3.createcybernetics.effect.quickhacks.OverheatQuickhackEffect;
import com.perigrine3.createcybernetics.effect.quickhacks.RebootQuickhackEffect;
import com.perigrine3.createcybernetics.effect.quickhacks.ScrambleQuickhackEffect;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.item.cyberware.brain.ICEProtocolItem;
import com.perigrine3.createcybernetics.network.payload.CastCyberdeckQuickhackPayload;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class CastCyberdeckQuickhackHandler {
    private static final int CAST_COOLDOWN_TICKS = 200;

    private CastCyberdeckQuickhackHandler() {
    }

    public static void handle(CastCyberdeckQuickhackPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer sp)) return;
            if (!sp.hasData(ModAttachments.CYBERWARE)) return;

            PlayerCyberwareData data = sp.getData(ModAttachments.CYBERWARE);
            if (data == null) return;

            Item cyberdeckItem = ModItems.BRAINUPGRADES_CYBERDECK.get();

            if (!data.hasSpecificItem(cyberdeckItem, CyberwareSlot.BRAIN)) return;
            if (sp.getCooldowns().isOnCooldown(cyberdeckItem)) return;

            int slot = payload.cyberdeckSlot();
            if (slot < 0 || slot >= PlayerCyberwareData.CYBERDECK_SLOT_COUNT) return;

            ItemStack quickhack = data.getCyberdeckStack(slot);
            if (quickhack.isEmpty()) return;
            if (!quickhack.is(ModTags.Items.QUICKHACK_SHARDS)) return;

            Entity target = sp.level().getEntity(payload.targetEntityId());
            if (!(target instanceof LivingEntity livingTarget)) return;
            if (!target.isAlive()) return;
            if (target == sp) return;

            if (!isKnownQuickhack(quickhack)) return;

            Component quickhackName = quickhackName(quickhack);

            sp.getCooldowns().addCooldown(cyberdeckItem, CAST_COOLDOWN_TICKS);

            if (ICEProtocolItem.negatesQuickhack(livingTarget)) {
                sendCasterIceMessage(sp, quickhackName);
                sendTargetIceMessage(livingTarget, quickhackName);
                return;
            }

            boolean applied = applyQuickhack(quickhack, livingTarget);

            if (!applied) {
                sendCasterFailureMessage(sp, quickhackName);
                sendTargetFailureMessage(livingTarget);
                return;
            }

            sendCasterSuccessMessage(sp, quickhackName);
            sendTargetSuccessMessage(livingTarget, quickhackName);
        });
    }

    private static boolean isKnownQuickhack(ItemStack quickhack) {
        return quickhack.is(ModItems.QUICKHACK_OVERHEAT.get())
                || quickhack.is(ModItems.QUICKHACK_REBOOT.get())
                || quickhack.is(ModItems.QUICKHACK_SCRAMBLE.get())
                || quickhack.is(ModItems.QUICKHACK_OPTICMALFUNCTION.get());
    }

    private static boolean applyQuickhack(ItemStack quickhack, LivingEntity target) {
        if (quickhack.is(ModItems.QUICKHACK_OVERHEAT.get())) {
            return OverheatQuickhackEffect.applyQuickhack(target);
        }

        if (quickhack.is(ModItems.QUICKHACK_REBOOT.get())) {
            return RebootQuickhackEffect.applyQuickhack(target);
        }

        if (quickhack.is(ModItems.QUICKHACK_SCRAMBLE.get())) {
            return ScrambleQuickhackEffect.applyQuickhack(target);
        }

        if (quickhack.is(ModItems.QUICKHACK_OPTICMALFUNCTION.get())) {
            return OpticMalfunctionQuickhackEffect.applyQuickhack(target);
        }

        return false;
    }

    private static Component quickhackName(ItemStack quickhack) {
        if (quickhack == null || quickhack.isEmpty()) {
            return Component.literal("Quickhack");
        }

        return Component.translatable(quickhack.getDescriptionId() + ".desc");
    }

    private static void sendCasterSuccessMessage(ServerPlayer caster, Component quickhackName) {
        caster.displayClientMessage(Component.translatable("message.createcybernetics.quickhack.caster.success", quickhackName).withStyle(ChatFormatting.DARK_GREEN),
                true);
    }

    private static void sendCasterFailureMessage(ServerPlayer caster, Component quickhackName) {
        caster.displayClientMessage(Component.translatable("message.createcybernetics.quickhack.caster.failure", quickhackName).withStyle(ChatFormatting.DARK_RED),
                true);
    }

    private static void sendCasterIceMessage(ServerPlayer caster, Component quickhackName) {
        caster.displayClientMessage(
                Component.translatable("message.createcybernetics.quickhack.caster.ice_blocked", quickhackName).withStyle(ChatFormatting.DARK_RED),
                true);
    }

    private static void sendTargetSuccessMessage(LivingEntity target, Component quickhackName) {
        if (!(target instanceof ServerPlayer targetPlayer)) return;

        targetPlayer.displayClientMessage(Component.translatable("message.createcybernetics.quickhack.target.success", quickhackName).withStyle(ChatFormatting.DARK_PURPLE),
                true
        );
    }

    private static void sendTargetFailureMessage(LivingEntity target) {
        if (!(target instanceof ServerPlayer targetPlayer)) return;

        targetPlayer.displayClientMessage(Component.translatable("message.createcybernetics.quickhack.target.failure").withStyle(ChatFormatting.DARK_GREEN),
                true);
    }

    private static void sendTargetIceMessage(LivingEntity target, Component quickhackName) {
        if (!(target instanceof ServerPlayer targetPlayer)) return;

        targetPlayer.displayClientMessage(
                Component.translatable("message.createcybernetics.quickhack.target.ice_blocked", quickhackName).withStyle(ChatFormatting.DARK_GREEN),
                true
        );
    }
}