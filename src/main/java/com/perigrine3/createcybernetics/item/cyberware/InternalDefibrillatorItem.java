package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.advancement.ModCriteria;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.common.surgery.DefaultOrgans;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.List;
import java.util.Set;

public class InternalDefibrillatorItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final int DEFIB_ENERGY_COST = 50;

    public InternalDefibrillatorItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost).withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.literal("Costs 50 Energy").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<Item> requiresCyberware(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModItems.BODYPART_HEART.get());
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.HEART);
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
    public void onInstalled(Player player) {
    }

    @Override
    public void onRemoved(Player player) {
    }

    @Override
    public void onTick(Player player) {
    }

    @Override
    public boolean dropsOnDeath(ItemStack installedStack, CyberwareSlot slot) {
        return false;
    }

    public record DefibPopPayload(ItemStack stack) implements CustomPacketPayload {
        public static final Type<DefibPopPayload> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "defib_pop"));

        public static final StreamCodec<RegistryFriendlyByteBuf, DefibPopPayload> STREAM_CODEC =
                StreamCodec.composite(ByteBufCodecs.fromCodecWithRegistries(ItemStack.CODEC), DefibPopPayload::stack, DefibPopPayload::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static final class NetworkRegistration {
        @SubscribeEvent
        public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
            PayloadRegistrar registrar = event.registrar("1");

            registrar.playToClient(DefibPopPayload.TYPE, DefibPopPayload.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> {
                        Minecraft mc = Minecraft.getInstance();
                        mc.gameRenderer.displayItemActivation(payload.stack());
            }));
        }

        private NetworkRegistration() {}
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class Events {

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onLivingDeath(LivingDeathEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player)) return;
            if (player.level().isClientSide) return;
            if (event.isCanceled()) return;

            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return;

            int[] idx = findInstalledDefibIndex(data);
            if (idx == null) return;

            CyberwareSlot slot = CyberwareSlot.values()[idx[0]];
            InstalledCyberware inst = data.get(slot, idx[1]);
            ItemStack display = (inst != null && inst.getItem() != null && !inst.getItem().isEmpty())
                    ? inst.getItem().copy()
                    : ModItems.HEARTUPGRADES_DEFIBRILLATOR.get().getDefaultInstance();

            if (!data.tryConsumeEnergy(DEFIB_ENERGY_COST)) {
                return;
            }

            if (!tryTotemRevive(player)) {
                data.receiveEnergy(player, DEFIB_ENERGY_COST);
                data.setDirty();
                player.syncData(ModAttachments.CYBERWARE);
                return;
            }

            PacketDistributor.sendToPlayer(player, new DefibPopPayload(display));
            removeInstalledDefib(data, idx[0], idx[1]);

            data.setDirty();
            player.syncData(ModAttachments.CYBERWARE);

            event.setCanceled(true);
        }



        private static boolean tryTotemRevive(ServerPlayer player) {
            if (player.getHealth() > 0.0F) return false;

            player.setHealth(1.0F);
            player.removeAllEffects();

            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));

            ((ServerLevel) player.level()).sendParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY() + 1.0D, player.getZ(),
                    60, 0.6D, 0.8D, 0.6D, 0.15D);

            player.level().playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);

            ModCriteria.DEUS_EX_MACHINA.get().trigger(player);

            return true;
        }

        private static int[] findInstalledDefibIndex(PlayerCyberwareData data) {
            for (CyberwareSlot slot : CyberwareSlot.values()) {
                int size = slot.size;
                for (int i = 0; i < size; i++) {
                    InstalledCyberware inst = data.get(slot, i);
                    if (inst == null) continue;
                    ItemStack st = inst.getItem();
                    if (st == null || st.isEmpty()) continue;
                    if (st.is(ModItems.HEARTUPGRADES_DEFIBRILLATOR.get())) {
                        return new int[]{slot.ordinal(), i};
                    }
                }
            }
            return null;
        }

        private static void removeInstalledDefib(PlayerCyberwareData data, int slotOrdinal, int index) {
            CyberwareSlot slot = CyberwareSlot.values()[slotOrdinal];
            data.remove(slot, index);

            ItemStack def = DefaultOrgans.get(slot, index);
            if (def == null) def = ItemStack.EMPTY;
            if (!def.isEmpty()) {
                int humanity = 0;
                data.set(slot, index, new InstalledCyberware(def.copy(), slot, index, humanity));
            }
        }

        private Events() {}
    }
}
