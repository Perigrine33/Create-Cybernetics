package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries; // ADDED
import net.minecraft.core.registries.Registries; // ADDED
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey; // ADDED
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block; // ADDED
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NeuralContextualizerEffect extends MobEffect {

    private static final int UPDATE_EVERY_TICKS = 2;
    private static final int SWAP_COOLDOWN_TICKS = 4;
    private static final double MAX_REACH_BLOCKS = 6.0D;

    private static final TagKey<Block> CREATE_WRENCH_PICKUP = // ADDED
            TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("create", "wrench_pickup")); // ADDED

    private static final TagKey<Block> CREATE_WRENCHABLE = // ADDED
            TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("create", "wrenchable")); // ADDED

    private static final TagKey<Item> CREATE_WRENCHES = // ADDED
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("create", "wrenches")); // ADDED

    private static final ResourceLocation CREATE_WRENCH_ID = // ADDED
            ResourceLocation.fromNamespaceAndPath("create", "wrench"); // ADDED

    public NeuralContextualizerEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return false;
    }

    private static boolean hasThisEffect(Player player) {
        return player.hasEffect(ModEffects.NEURAL_CONTEXTUALIZER_EFFECT);
    }

    public record SwapHotbarPayload(int slot) implements CustomPacketPayload {
        public static final Type<SwapHotbarPayload> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "neural_swap_hotbar"));

        public static final StreamCodec<RegistryFriendlyByteBuf, SwapHotbarPayload> STREAM_CODEC =
                StreamCodec.composite(ByteBufCodecs.VAR_INT, SwapHotbarPayload::slot, SwapHotbarPayload::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void handleSwapHotbarPayload(net.minecraft.server.level.ServerPlayer player, int slot) {
        if (player == null) return;
        if (!player.hasEffect(ModEffects.NEURAL_CONTEXTUALIZER_EFFECT)) return;

        if (slot < 0 || slot > 8) return;
        if (player.isSpectator()) return;

        player.getInventory().selected = slot;
        player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket(slot));
        player.inventoryMenu.broadcastChanges();
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
    public static final class ClientLogic {
        private static int cooldown = 0;
        private static boolean lastHasEffect = false;
        private static int lastSentSlot = -1;

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null) return;

            Player player = mc.player;

            boolean hasEffect = hasThisEffect(player);

            if (!hasEffect) {
                cooldown = 0;
                lastSentSlot = -1;
                lastHasEffect = false;
                return;
            }

            if (!lastHasEffect) {
                cooldown = 0;
                lastSentSlot = -1;
                lastHasEffect = true;
            }

            if ((mc.level.getGameTime() % UPDATE_EVERY_TICKS) != 0) return;
            if (cooldown > 0) { cooldown--; return; }

            if (mc.screen != null) return;
            if (player.isUsingItem()) return;
            if (mc.options.keyShift.isDown()) return;

            HitResult hit = mc.hitResult;
            if (hit == null || hit.getType() == HitResult.Type.MISS) return;

            if (hit.getType() == HitResult.Type.BLOCK && hit instanceof BlockHitResult bhr) {
                Vec3 eye = player.getEyePosition();
                Vec3 hitPos = bhr.getLocation();
                double maxSqr = MAX_REACH_BLOCKS * MAX_REACH_BLOCKS;
                if (eye.distanceToSqr(hitPos) > maxSqr) return;
            }

            int desiredSlot = -1;

            if (hit.getType() == HitResult.Type.BLOCK && hit instanceof BlockHitResult bhr) {
                BlockPos pos = bhr.getBlockPos();
                BlockState state = mc.level.getBlockState(pos);

                boolean isCreateBlock = "create".equals( // ADDED
                        BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace() // ADDED
                ); // ADDED

                if (state.is(CREATE_WRENCH_PICKUP) || state.is(CREATE_WRENCHABLE) || isCreateBlock) { // ADDED
                    int wrenchSlot = findCreateWrenchHotbarSlot(player); // ADDED
                    if (wrenchSlot != -1) desiredSlot = wrenchSlot; // ADDED
                } // ADDED

                if (desiredSlot == -1) {
                    desiredSlot = findBestToolHotbarSlot(player, state);
                }
            } else if (hit.getType() == HitResult.Type.ENTITY && hit instanceof EntityHitResult ehr) {
                if (ehr.getEntity() instanceof LivingEntity target) {
                    if (target instanceof Enemy) {
                        desiredSlot = findBestWeaponHotbarSlot(player);
                    }
                }
            }

            if (desiredSlot < 0 || desiredSlot > 8) return;

            int current = player.getInventory().selected;
            if (desiredSlot == current) return;

            if (desiredSlot == lastSentSlot) return;

            PacketDistributor.sendToServer(new SwapHotbarPayload(desiredSlot));
            lastSentSlot = desiredSlot;
            cooldown = SWAP_COOLDOWN_TICKS;
        }

        private static int findCreateWrenchHotbarSlot(Player player) { // ADDED
            for (int slot = 0; slot < 9; slot++) { // ADDED
                ItemStack stack = player.getInventory().getItem(slot); // ADDED
                if (stack.isEmpty()) continue; // ADDED
                if (stack.is(CREATE_WRENCHES)) return slot; // ADDED
                ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem()); // ADDED
                if (CREATE_WRENCH_ID.equals(key)) return slot; // ADDED
            } // ADDED
            return -1; // ADDED
        } // ADDED

        private static int findBestToolHotbarSlot(Player player, BlockState state) {
            ToolFamily family = toolFamilyFor(state);
            if (family == ToolFamily.NONE) return -1;

            int bestSlot = -1;
            float bestScore = 0.0F;

            for (int slot = 0; slot < 9; slot++) {
                ItemStack stack = player.getInventory().getItem(slot);
                if (stack.isEmpty()) continue;

                if (!family.matches(stack.getItem())) continue;

                float speed = stack.getDestroySpeed(state);
                if (speed <= 1.0F) continue;

                float correctnessBonus = 0.0F;
                try {
                    if (stack.isCorrectToolForDrops(state)) correctnessBonus = 1000.0F;
                } catch (Throwable ignored) {
                }

                float score = speed + correctnessBonus;

                if (score > bestScore) {
                    bestScore = score;
                    bestSlot = slot;
                }
            }

            return bestSlot;
        }

        private static int findBestWeaponHotbarSlot(Player player) {
            int swordSlot = findBestTieredHotbarSlot(player, SwordItem.class);
            if (swordSlot != -1) return swordSlot;

            int axeSlot = findBestTieredHotbarSlot(player, AxeItem.class);
            if (axeSlot != -1) return axeSlot;

            return -1;
        }

        private static int findBestTieredHotbarSlot(Player player, Class<? extends Item> clazz) {
            int bestSlot = -1;
            float bestScore = -Float.MAX_VALUE;

            for (int slot = 0; slot < 9; slot++) {
                ItemStack stack = player.getInventory().getItem(slot);
                if (stack.isEmpty()) continue;

                Item item = stack.getItem();
                if (!clazz.isInstance(item)) continue;

                float score = 0.0F;

                if (item instanceof TieredItem tiered) {
                    var tier = tiered.getTier();

                    score += tier.getAttackDamageBonus() * 10.0F;
                    score += tier.getSpeed();
                    score += tier.getUses() * 0.001F;
                }

                if (score > bestScore) {
                    bestScore = score;
                    bestSlot = slot;
                }
            }

            return bestSlot;
        }

        private enum ToolFamily {
            PICKAXE, AXE, SHOVEL, HOE, NONE;

            boolean matches(Item item) {
                return switch (this) {
                    case PICKAXE -> item instanceof PickaxeItem;
                    case AXE -> item instanceof AxeItem;
                    case SHOVEL -> item instanceof ShovelItem;
                    case HOE -> item instanceof HoeItem;
                    case NONE -> false;
                };
            }
        }

        private static ToolFamily toolFamilyFor(BlockState state) {
            if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) return ToolFamily.PICKAXE;
            if (state.is(BlockTags.MINEABLE_WITH_AXE)) return ToolFamily.AXE;
            if (state.is(BlockTags.MINEABLE_WITH_SHOVEL)) return ToolFamily.SHOVEL;
            if (state.is(BlockTags.MINEABLE_WITH_HOE)) return ToolFamily.HOE;
            return ToolFamily.NONE;
        }

        private ClientLogic() {}
    }
}
