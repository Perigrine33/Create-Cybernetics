package com.perigrine3.createcybernetics.network.handler;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.effect.quickhacks.OpticMalfunctionQuickhackEffect;
import com.perigrine3.createcybernetics.effect.quickhacks.OverheatQuickhackEffect;
import com.perigrine3.createcybernetics.effect.quickhacks.RebootQuickhackEffect;
import com.perigrine3.createcybernetics.effect.quickhacks.ScrambleQuickhackEffect;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.network.payload.CastCyberdeckQuickhackPayload;
import com.perigrine3.createcybernetics.util.ModTags;
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

            if (quickhack.is(ModItems.QUICKHACK_OVERHEAT.get())) {
                boolean applied = OverheatQuickhackEffect.applyQuickhack(livingTarget);
                if (!applied) return;

                sp.getCooldowns().addCooldown(cyberdeckItem, CAST_COOLDOWN_TICKS);
            }
            if (quickhack.is(ModItems.QUICKHACK_REBOOT.get())) {
                boolean applied = RebootQuickhackEffect.applyQuickhack(livingTarget);
                if (!applied) return;

                sp.getCooldowns().addCooldown(cyberdeckItem, CAST_COOLDOWN_TICKS);
            }
            if (quickhack.is(ModItems.QUICKHACK_SCRAMBLE.get())) {
                boolean applied = ScrambleQuickhackEffect.applyQuickhack(livingTarget);
                if (!applied) return;

                sp.getCooldowns().addCooldown(cyberdeckItem, CAST_COOLDOWN_TICKS);
            }
            if (quickhack.is(ModItems.QUICKHACK_OPTICMALFUNCTION.get())) {
                boolean applied = OpticMalfunctionQuickhackEffect.applyQuickhack(livingTarget);
                if (!applied) return;

                sp.getCooldowns().addCooldown(cyberdeckItem, CAST_COOLDOWN_TICKS);
            }


        });
    }
}