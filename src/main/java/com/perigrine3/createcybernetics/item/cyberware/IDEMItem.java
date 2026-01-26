package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IDEMItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final int ACTIVATION_COST = 50;
    private static final int RETURN_DELAY_TICKS = 15 * 20;
    private static final int COOLDOWN_TICKS = 30 * 20;

    private static final String NBT_ROOT = "cc_idem";
    private static final String NBT_ACTIVE = "active";
    private static final String NBT_RETURN_TICKS = "return_ticks";
    private static final String NBT_ORIGIN_DIM = "origin_dim";
    private static final String NBT_COOLDOWN = "cooldown_ticks";

    public IDEMItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.brainupgrades_idem.tooltip3").withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost).withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.translatable("tooltip.createcybernetics.brainupgrades_idem.energy").withStyle(ChatFormatting.RED));
        }
    }

    @Override public int getHumanityCost() { return humanityCost; }

    @Override
    public Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModTags.Items.BRAIN_ITEMS);
    }

    @Override public Set<CyberwareSlot> getSupportedSlots() { return Set.of(CyberwareSlot.BRAIN); }
    @Override public boolean replacesOrgan() { return false; }
    @Override public Set<CyberwareSlot> getReplacedOrgans() { return Set.of(); }

    @Override
    public void onRemoved(Player player) {
        if (player instanceof ServerPlayer sp) clearPending(sp);
    }

    @Override
    public void onTick(Player player) {
        if (!(player instanceof ServerPlayer sp)) return;

        tickCooldown(sp);
        tickReturnCountdown(sp);
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class Events {

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onIncomingDamage(LivingIncomingDamageEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player)) return;
            if (event.isCanceled()) return;

            if (!player.hasData(ModAttachments.CYBERWARE)) return;
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return;

            if (isActive(player)) return;

            if (getCooldownTicks(player) > 0) return;

            InstalledLoc loc = findInstalledIdem(data);
            if (loc == null) return;

            if (!loc.stack.is(ModTags.Items.TOGGLEABLE_CYBERWARE)) return;
            if (!data.isEnabled(loc.slot, loc.index)) return;

            if (!data.tryConsumeEnergy(ACTIVATION_COST)) return;

            event.setCanceled(true);

            setCooldownTicks(player, COOLDOWN_TICKS);

            ResourceKey<Level> originDim = player.level().dimension();
            armReturn(player, originDim, RETURN_DELAY_TICKS);

            ResourceKey<Level> destDim = chooseDestination(originDim, player.getRandom().nextDouble());
            ServerLevel destLevel = player.server.getLevel(destDim);
            if (destLevel == null) {
                clearPending(player);
                return;
            }

            teleportMapped(player, player.serverLevel(), destLevel);
        }

        @SubscribeEvent
        public static void onDeath(LivingDeathEvent event) {
            if (event.getEntity() instanceof ServerPlayer sp) clearPending(sp);
        }

        private static InstalledLoc findInstalledIdem(PlayerCyberwareData data) {
            InstalledCyberware[] arr = data.getAll().get(CyberwareSlot.BRAIN);
            if (arr == null) return null;

            Item idemItem = ModItems.BRAINUPGRADES_IDEM.get();

            for (int i = 0; i < arr.length; i++) {
                InstalledCyberware cw = arr[i];
                if (cw == null) continue;

                ItemStack st = cw.getItem();
                if (st == null || st.isEmpty()) continue;

                if (st.getItem() == idemItem) {
                    return new InstalledLoc(CyberwareSlot.BRAIN, i, st);
                }
            }
            return null;
        }

        private record InstalledLoc(CyberwareSlot slot, int index, ItemStack stack) {}
    }

    private static void tickCooldown(ServerPlayer player) {
        CompoundTag root = getOrCreateRoot(player, false);
        if (root == null) return;

        int cd = root.getInt(NBT_COOLDOWN);
        if (cd > 0) root.putInt(NBT_COOLDOWN, cd - 1);
    }

    private static int getCooldownTicks(ServerPlayer player) {
        CompoundTag root = getOrCreateRoot(player, false);
        return (root == null) ? 0 : Math.max(0, root.getInt(NBT_COOLDOWN));
    }

    private static void setCooldownTicks(ServerPlayer player, int ticks) {
        CompoundTag root = getOrCreateRoot(player, true);
        root.putInt(NBT_COOLDOWN, Math.max(0, ticks));
    }

    private static void tickReturnCountdown(ServerPlayer player) {
        if (!player.isAlive()) {
            clearPending(player);
            return;
        }

        CompoundTag root = getOrCreateRoot(player, false);
        if (root == null) return;
        if (!root.getBoolean(NBT_ACTIVE)) return;

        if (!stillHasIdemInstalled(player)) {
            clearPending(player);
            return;
        }

        int ticksLeft = root.getInt(NBT_RETURN_TICKS);
        if (ticksLeft > 0) {
            root.putInt(NBT_RETURN_TICKS, ticksLeft - 1);
            return;
        }

        if (!player.hasData(ModAttachments.CYBERWARE)) return;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        if (!data.tryConsumeEnergy(ACTIVATION_COST)) {
            root.putInt(NBT_RETURN_TICKS, 0);
            return;
        }

        ResourceKey<Level> origin = readOriginDim(root);
        if (origin == null) {
            clearPending(player);
            return;
        }

        ServerLevel target = player.server.getLevel(origin);
        if (target == null) {
            clearPending(player);
            return;
        }

        ServerLevel current = player.serverLevel();
        if (!current.dimension().equals(origin)) {
            teleportMapped(player, current, target);
        }

        clearPending(player);
    }

    private static boolean stillHasIdemInstalled(ServerPlayer player) {
        if (!player.hasData(ModAttachments.CYBERWARE)) return false;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        InstalledCyberware[] arr = data.getAll().get(CyberwareSlot.BRAIN);
        if (arr == null) return false;

        Item idemItem = ModItems.BRAINUPGRADES_IDEM.get();
        for (InstalledCyberware cw : arr) {
            if (cw == null) continue;
            ItemStack st = cw.getItem();
            if (st == null || st.isEmpty()) continue;
            if (st.getItem() == idemItem) return true;
        }
        return false;
    }

    private static void armReturn(ServerPlayer player, ResourceKey<Level> origin, int ticksFromNow) {
        CompoundTag root = getOrCreateRoot(player, true);
        root.putBoolean(NBT_ACTIVE, true);
        root.putInt(NBT_RETURN_TICKS, Math.max(0, ticksFromNow));
        root.putString(NBT_ORIGIN_DIM, origin.location().toString());
    }

    private static void clearPending(ServerPlayer player) {
        CompoundTag root = getOrCreateRoot(player, false);
        if (root == null) return;

        root.putBoolean(NBT_ACTIVE, false);
        root.remove(NBT_RETURN_TICKS);
        root.remove(NBT_ORIGIN_DIM);

        int cd = root.getInt(NBT_COOLDOWN);
        if (cd <= 0) {
        }
    }


    private static boolean isActive(ServerPlayer player) {
        CompoundTag root = getOrCreateRoot(player, false);
        return root != null && root.getBoolean(NBT_ACTIVE);
    }

    private static CompoundTag getOrCreateRoot(ServerPlayer player, boolean create) {
        CompoundTag pdata = player.getPersistentData();
        if (!pdata.contains(NBT_ROOT)) {
            if (!create) return null;
            pdata.put(NBT_ROOT, new CompoundTag());
        }
        return pdata.getCompound(NBT_ROOT);
    }

    private static ResourceKey<Level> readOriginDim(CompoundTag root) {
        if (!root.contains(NBT_ORIGIN_DIM)) return null;
        ResourceLocation rl = ResourceLocation.tryParse(root.getString(NBT_ORIGIN_DIM));
        if (rl == null) return null;
        return ResourceKey.create(Registries.DIMENSION, rl);
    }

    private static ResourceKey<Level> chooseDestination(ResourceKey<Level> origin, double roll01) {
        if (origin.equals(Level.NETHER) || origin.equals(Level.END)) return Level.OVERWORLD;
        return (roll01 < 0.98D) ? Level.NETHER : Level.END;
    }

    private static void teleportMapped(ServerPlayer player, ServerLevel from, ServerLevel to) {
        VecXZ mapped = mapXZ(from.dimension(), to.dimension(), player.getX(), player.getZ());

        double y = findSafeYByCollision(player, to, mapped.x, mapped.z);

        boolean ok = player.teleportTo(
                to,
                mapped.x, y, mapped.z,
                Set.<RelativeMovement>of(),
                player.getYRot(), player.getXRot()
        );

        if (ok) {
            player.fallDistance = 0.0F;
            player.setDeltaMovement(Vec3.ZERO);
        }
    }


    private record VecXZ(double x, double z) {}

    private static VecXZ mapXZ(ResourceKey<Level> fromDim, ResourceKey<Level> toDim, double x, double z) {
        boolean fromNether = fromDim.equals(Level.NETHER);
        boolean toNether = toDim.equals(Level.NETHER);

        if (fromNether && !toNether) return new VecXZ(x * 8.0D, z * 8.0D);
        if (!fromNether && toNether) return new VecXZ(x / 8.0D, z / 8.0D);
        return new VecXZ(x, z);
    }

    private static double findSafeYByCollision(ServerPlayer player, ServerLevel level, double x, double z) {
        int minY = level.getMinBuildHeight() + 1;
        int maxY = level.getMaxBuildHeight() - 2;

        for (int y = minY; y <= maxY; y++) {
            double yy = y + 0.01D;
            if (isStandingSpotSafeAt(player, level, x, yy, z)) {
                return yy;
            }
        }

        return Mth.clamp(player.getY(), minY, maxY);
    }

    private static boolean isStandingSpotSafeAt(ServerPlayer player, ServerLevel level, double x, double y, double z) {
        EntityDimensions dims = player.getDimensions(Pose.STANDING);
        double w = dims.width();
        double h = dims.height();

        BlockPos feet = BlockPos.containing(x, y, z);
        BlockPos below = feet.below();
        if (level.getBlockState(below).getCollisionShape(level, below).isEmpty()) return false;

        double jitter = Math.max(0.05D, (w * 0.5D) * 0.25D);

        double[] dx = new double[]{ 0,  jitter, -jitter, 0, 0 };
        double[] dz = new double[]{ 0,  0,      0,      jitter, -jitter };

        for (int i = 0; i < dx.length; i++) {
            double xx = x + dx[i];
            double zz = z + dz[i];

            AABB aabb = new AABB(
                    xx - w / 2.0D, y,         zz - w / 2.0D,
                    xx + w / 2.0D, y + h,     zz + w / 2.0D
            );

            if (!level.noCollision(aabb)) return false;
            if (level.containsAnyLiquid(aabb)) return false;

            if (!level.getWorldBorder().isWithinBounds(BlockPos.containing(xx, y, zz))) return false;
        }

        return true;
    }

    private static void nudgeOutOfBlocks(ServerPlayer player) {
        ServerLevel level = player.serverLevel();

        if (level.noCollision(player.getBoundingBox()) && !level.containsAnyLiquid(player.getBoundingBox())) return;

        for (int dy = 1; dy <= 12; dy++) {
            double ny = player.getY() + dy;
            AABB moved = player.getBoundingBox().move(0.0D, dy, 0.0D);
            if (level.noCollision(moved) && !level.containsAnyLiquid(moved)) {
                player.teleportTo(level, player.getX(), ny, player.getZ(),
                        Set.<RelativeMovement>of(), player.getYRot(), player.getXRot());
                player.fallDistance = 0.0F;
                player.setDeltaMovement(Vec3.ZERO);
                return;
            }
        }
    }


}
