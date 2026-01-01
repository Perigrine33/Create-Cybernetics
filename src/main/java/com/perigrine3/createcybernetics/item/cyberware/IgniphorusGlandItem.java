package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.ModTags;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.List;
import java.util.Set;

public class IgniphorusGlandItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final String NBT_LAST_SHOT_TICK = "cc_igniphorus_lastDragonFireballTick";
    // “About as long as the dragon” — tune as desired; 100 ticks = 5 seconds.
    private static final int COOLDOWN_TICKS = 100;

    public IgniphorusGlandItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
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
        return Set.of(ModTags.Items.LUNGS_ITEMS);
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.LUNGS);
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
    public Set<Item> incompatibleCyberware(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModItems.WETWARE_SCULKLUNGS.get(), ModItems.WETWARE_GUARDIANEYE.get());
    }

    @Override
    public void onInstalled(Player player) { }

    @Override
    public void onRemoved(Player player) { }

    @Override
    public void onTick(Player player) {
        ICyberwareItem.super.onTick(player);
    }

    public record DragonBreathFireballPayload() implements net.minecraft.network.protocol.common.custom.CustomPacketPayload {
        public static final net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<DragonBreathFireballPayload> TYPE =
                new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(
                        ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "igniphorus_dragon_fireball"));

        public static final StreamCodec<ByteBuf, DragonBreathFireballPayload> STREAM_CODEC =
                StreamCodec.unit(new DragonBreathFireballPayload());

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static final class NetworkRegistration {
        @SubscribeEvent
        public static void registerPayloads(RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar("1");
            registrar.playToServer(DragonBreathFireballPayload.TYPE, DragonBreathFireballPayload.STREAM_CODEC, NetworkRegistration::handle);
        }

        private static void handle(final DragonBreathFireballPayload payload, final IPayloadContext context) {
            context.enqueueWork(() -> {
                if (!(context.player() instanceof ServerPlayer sp)) return;
                tryShoot(sp);
            });
        }
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static final class ClientInput {
        private static boolean wasUseDown = false;

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            final Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null) return;
            if (mc.screen != null) return;

            final boolean useDown = mc.options.keyUse.isDown();
            final boolean risingEdge = useDown && !wasUseDown;
            wasUseDown = useDown;

            if (!risingEdge) return;
            if (!mc.player.isCrouching()) return;

            if (mc.hitResult == null) return;
            HitResult.Type type = mc.hitResult.getType();
            if (type != HitResult.Type.MISS && type != HitResult.Type.ENTITY && type != HitResult.Type.BLOCK) return;
            if (!mc.player.getMainHandItem().isEmpty() || !mc.player.getOffhandItem().isEmpty()) return;
            if (mc.getConnection() != null) {
                mc.getConnection().send(new ServerboundCustomPayloadPacket(new DragonBreathFireballPayload()));
            }
        }
    }

    private static void tryShoot(ServerPlayer player) {
        final ServerLevel level = player.serverLevel();
        if (!player.isCrouching()) return;
        final PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null || !data.hasSpecificItem(ModItems.WETWARE_FIREBREATHINGGLAND.get(), CyberwareSlot.LUNGS)) return;

        final double reach = 5.0D;
        final Vec3 start = player.getEyePosition();
        final Vec3 look = player.getLookAngle();
        final Vec3 end = start.add(look.scale(reach));

        final BlockHitResult blockHit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        if (blockHit.getType() != HitResult.Type.MISS) return;

        final EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(level, player, start, end, player.getBoundingBox().expandTowards(look.scale(reach)).inflate(1.0D), e -> e.isPickable() && e != player);
        if (entityHit != null) return;

        final long now = level.getGameTime();
        final CompoundTag tag = player.getPersistentData();
        final long last = tag.getLong(NBT_LAST_SHOT_TICK);
        if (now - last < COOLDOWN_TICKS) return;
        tag.putLong(NBT_LAST_SHOT_TICK, now);

        final Vec3 power = look.scale(5.0D);
        final DragonFireball fireball = new DragonFireball(level, player, power);
        final Vec3 spawnPos = start.add(look.scale(0.6D));
        fireball.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, player.getYRot(), player.getXRot());

        level.addFreshEntity(fireball);
        level.levelEvent(null, 1017, player.blockPosition(), 0);
        player.swing(InteractionHand.MAIN_HAND, false);
    }
}
