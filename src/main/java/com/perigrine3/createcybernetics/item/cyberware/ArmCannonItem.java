package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.entity.ModEntities;
import com.perigrine3.createcybernetics.entity.projectile.NuggetProjectile;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.network.payload.ArmCannonFirePayload;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Set;

public class ArmCannonItem extends Item implements ICyberwareItem {

    public static final int SLOT_COUNT = 4;
    private static final String STACK_ROOT = "cc_arm_cannon";
    private static final String STACK_INV = "inv";

    private static final String PD_ARM_CANNON = "cc_arm_cannon_projectile";
    private static final String PD_LAST_FIRE_TICK = "cc_arm_cannon_last_fire_tick";

    private final int humanityCost;

    public ArmCannonItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override public int getHumanityCost() { return humanityCost; }
    @Override public Set<CyberwareSlot> getSupportedSlots() { return Set.of(CyberwareSlot.LARM); }
    @Override public boolean replacesOrgan() { return false; }
    @Override public Set<CyberwareSlot> getReplacedOrgans() { return Set.of(); }

    public static boolean isValidStoredItem(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.is(ModTags.Items.ARM_CANNON_AMMO);
    }

    /* ---------------- CLIENT TRIGGER: RIGHT CLICK AIR WITH EMPTY HAND ----------------
     *
     * RightClickEmpty is client-only and NOT cancellable.
     * We must tell the server ourselves via a payload.
     */
    @EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
    public static final class ClientFireTrigger {
        private ClientFireTrigger() {}

        // Client debounce: don't send twice in the same client tick
        private static long lastSentGameTime = Long.MIN_VALUE;

        @SubscribeEvent
        public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
            Player p = event.getEntity();
            if (p == null) return;

            // "empty left hand" => offhand must be empty
            if (!p.getOffhandItem().isEmpty()) return;

            // Avoid firing while any GUI is open (optional but recommended)
            if (net.minecraft.client.Minecraft.getInstance().screen != null) return;

            long now = p.level().getGameTime();
            if (now == lastSentGameTime) return;
            lastSentGameTime = now;

            // Ask server to attempt firing (server will validate enabled/installed/ammo).
            PacketDistributor.sendToServer(new ArmCannonFirePayload());
        }
    }

    /* ---------------- STACK STORAGE (CUSTOM_DATA) ---------------- */

    private static CompoundTag getOrCreateRoot(ItemStack cannonStack) {
        CustomData cd = cannonStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag all = cd.copyTag();

        if (!all.contains(STACK_ROOT, Tag.TAG_COMPOUND)) {
            all.put(STACK_ROOT, new CompoundTag());
        }

        return all;
    }

    private static CompoundTag getRootView(ItemStack cannonStack) {
        CustomData cd = cannonStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag all = cd.copyTag();
        return all.contains(STACK_ROOT, Tag.TAG_COMPOUND) ? all.getCompound(STACK_ROOT) : new CompoundTag();
    }

    private static void writeBack(ItemStack cannonStack, CompoundTag all) {
        cannonStack.set(DataComponents.CUSTOM_DATA, CustomData.of(all));
    }

    public static void loadFromInstalledStack(ItemStack cannonStack, HolderLookup.Provider provider, Container intoInv) {
        for (int i = 0; i < SLOT_COUNT; i++) intoInv.setItem(i, ItemStack.EMPTY);
        if (cannonStack == null || cannonStack.isEmpty()) return;

        CompoundTag root = getRootView(cannonStack);
        if (!root.contains(STACK_INV, Tag.TAG_LIST)) return;

        ListTag list = root.getList(STACK_INV, Tag.TAG_COMPOUND);

        for (int i = 0; i < SLOT_COUNT && i < list.size(); i++) {
            CompoundTag c = list.getCompound(i);
            ItemStack st = ItemStack.parseOptional(provider, c);

            if (st.isEmpty() || !isValidStoredItem(st)) {
                intoInv.setItem(i, ItemStack.EMPTY);
            } else {
                int cap = Math.max(1, st.getMaxStackSize());
                if (st.getCount() > cap) st.setCount(cap);
                intoInv.setItem(i, st);
            }
        }
    }

    public static void saveIntoInstalledStack(ItemStack cannonStack, HolderLookup.Provider provider, Container fromInv) {
        if (cannonStack == null || cannonStack.isEmpty()) return;

        ListTag list = new ListTag();

        for (int i = 0; i < SLOT_COUNT; i++) {
            ItemStack st = fromInv.getItem(i);

            if (!st.isEmpty() && isValidStoredItem(st)) {
                ItemStack copy = st.copy();
                int cap = Math.max(1, copy.getMaxStackSize());
                if (copy.getCount() > cap) copy.setCount(cap);
                list.add(copy.save(provider));
            } else {
                list.add(new CompoundTag());
            }
        }

        CompoundTag all = getOrCreateRoot(cannonStack);
        CompoundTag root = all.getCompound(STACK_ROOT);
        root.put(STACK_INV, list);
        all.put(STACK_ROOT, root);

        writeBack(cannonStack, all);
    }

    public static void dropAndClearInstalledStack(ServerPlayer sp, HolderLookup.Provider provider, ItemStack cannonStack) {
        if (cannonStack == null || cannonStack.isEmpty()) return;

        SimpleContainer tmp = new SimpleContainer(SLOT_COUNT);
        loadFromInstalledStack(cannonStack, provider, tmp);

        for (int i = 0; i < SLOT_COUNT; i++) {
            ItemStack st = tmp.getItem(i);
            if (!st.isEmpty()) sp.drop(st, false);
        }

        CustomData cd = cannonStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag all = cd.copyTag();
        all.remove(STACK_ROOT);
        cannonStack.set(DataComponents.CUSTOM_DATA, CustomData.of(all));
    }

    @Override
    public void onRemoved(Player player) {
        if (!(player instanceof ServerPlayer sp)) return;
        if (!sp.hasData(ModAttachments.CYBERWARE)) return;

        PlayerCyberwareData data = sp.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        for (var entry : data.getAll().entrySet()) {
            var arr = entry.getValue();
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {
                var inst = arr[i];
                if (inst == null) continue;

                ItemStack st = inst.getItem();
                if (st == null || st.isEmpty()) continue;

                if (st.getItem() == this) {
                    dropAndClearInstalledStack(sp, sp.level().registryAccess(), st);
                }
            }
        }

        data.setDirty();
        sp.syncData(ModAttachments.CYBERWARE);
    }

    /* ---------------- FIRING ---------------- */

    private record CannonRef(CyberwareSlot slot, int index, ItemStack stack) {}

    public static boolean fireLoaded(ServerPlayer sp) {
        if (sp == null) return false;

        // "empty left hand" => offhand must be empty
        if (!sp.getOffhandItem().isEmpty()) return false;

        // Server debounce: at most one fire per tick per player
        long now = sp.level().getGameTime();
        CompoundTag pd = sp.getPersistentData();
        if (pd.getLong(PD_LAST_FIRE_TICK) == now) return false;
        pd.putLong(PD_LAST_FIRE_TICK, now);

        if (!sp.hasData(ModAttachments.CYBERWARE)) return false;
        PlayerCyberwareData data = sp.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        CannonRef ref = findInstalledArmCannon(data);
        if (ref == null) return false;

        // Must be enabled by toggle wheel
        if (!data.isEnabled(ref.slot(), ref.index())) return false;

        ItemStack cannonStack = ref.stack();
        if (cannonStack.isEmpty()) return false;

        int selected = data.getArmCannonSelected();
        if (selected < 0 || selected >= SLOT_COUNT) return false;

        SimpleContainer inv = new SimpleContainer(SLOT_COUNT);
        loadFromInstalledStack(cannonStack, sp.level().registryAccess(), inv);

        ItemStack ammo = inv.getItem(selected);
        if (ammo.isEmpty() || !isValidStoredItem(ammo)) return false;

        ItemStack ammoOne = ammo.copyWithCount(1);
        boolean fired = spawnAmmoProjectile(sp, ammoOne);
        if (!fired) return false;

        ammo.shrink(1);
        inv.setItem(selected, ammo.isEmpty() ? ItemStack.EMPTY : ammo);

        saveIntoInstalledStack(cannonStack, sp.level().registryAccess(), inv);

        data.setDirty();
        sp.syncData(ModAttachments.CYBERWARE);
        return true;
    }

    private static CannonRef findInstalledArmCannon(PlayerCyberwareData data) {
        for (var entry : data.getAll().entrySet()) {
            CyberwareSlot slot = entry.getKey();
            var arr = entry.getValue();
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {
                var cw = arr[i];
                if (cw == null) continue;

                ItemStack st = cw.getItem();
                if (st == null || st.isEmpty()) continue;

                if (st.getItem() == ModItems.ARMUPGRADES_ARMCANNON.get()) {
                    return new CannonRef(slot, i, st);
                }
            }
        }
        return null;
    }

    private static boolean spawnAmmoProjectile(ServerPlayer sp, ItemStack ammoOne) {
        var level = sp.level();
        Vec3 look = sp.getLookAngle().normalize();

        // Push spawn point out a bit.
        Vec3 start = sp.getEyePosition().add(look.scale(1.0));

        final float genericSpeed = 4.0f;
        final float arrowSpeed = 4.0f;
        final float fireworkSpeed = 2.2f;
        final double tntSpeed = 1.8;
        final float nuggetSpeed = 5.0f;

        // Faster for hurty projectiles; also set delta movement explicitly.
        final double hurtingProjectileSpeed = 4.0;

        final float inaccuracy = 0.0f;

        // NUGGETS: keep your existing projectile, but do server-side hitscan for consistent damage.
        if (ammoOne.is(Tags.Items.NUGGETS)) {
            NuggetProjectile bullet = new NuggetProjectile(ModEntities.NUGGET_PROJECTILE.get(), level, sp, ammoOne);
            bullet.setOwner(sp);
            bullet.setPos(start.x, start.y, start.z);
            bullet.shoot(look.x, look.y, look.z, nuggetSpeed, inaccuracy);
            level.addFreshEntity(bullet);

            // Hitscan damage (server authoritative)
            double range = 64.0;
            Vec3 eye = sp.getEyePosition();
            Vec3 end = eye.add(look.scale(range));

            BlockHitResult blockHit = level.clip(new ClipContext(
                    eye, end,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    sp
            ));

            Vec3 cappedEnd = (blockHit.getType() == HitResult.Type.MISS) ? end : blockHit.getLocation();

            EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                    level,
                    sp,
                    eye,
                    cappedEnd,
                    sp.getBoundingBox().expandTowards(look.scale(range)).inflate(1.0),
                    e -> e.isPickable() && e != sp
            );

            if (entityHit != null) {
                Entity target = entityHit.getEntity();
                if (target instanceof LivingEntity living) {
                    float damage = 6.0f;
                    living.hurt(sp.damageSources().playerAttack(sp), damage);
                }
            }

            return true;
        }

        // TNT (unchanged)
        if (ammoOne.is(Items.TNT)) {
            PrimedTnt tnt = new PrimedTnt(level, start.x, start.y, start.z, sp);
            tnt.setFuse(60);
            tnt.setDeltaMovement(look.scale(tntSpeed));
            level.addFreshEntity(tnt);
            return true;
        }

        // FIRE CHARGE: spawn a proper SmallFireball with explicit velocity
        if (ammoOne.is(Items.FIRE_CHARGE)) {
            Vec3 vel = look.scale(hurtingProjectileSpeed);
            SmallFireball fb = new SmallFireball(level, sp, vel);
            fb.setOwner(sp);
            fb.setPos(start.x, start.y, start.z);
            fb.setDeltaMovement(vel);
            level.addFreshEntity(fb);
            return true;
        }

        // WIND CHARGE: spawn a proper WindCharge with explicit velocity
        if (ammoOne.is(Items.WIND_CHARGE)) {
            Vec3 vel = look.scale(hurtingProjectileSpeed);
            WindCharge wc = new WindCharge(level, start.x, start.y, start.z, vel);
            wc.setOwner(sp);
            wc.setPos(start.x, start.y, start.z);
            wc.setDeltaMovement(vel);
            level.addFreshEntity(wc);
            return true;
        }

        // FIREWORK ROCKETS: spawn normally, but tag so our tick handler can force an impact explosion.
        if (ammoOne.getItem() instanceof FireworkRocketItem) {
            FireworkRocketEntity rocket = new FireworkRocketEntity(level, ammoOne, sp, start.x, start.y, start.z, true);
            rocket.setOwner(sp);
            rocket.setPos(start.x, start.y, start.z);
            rocket.shoot(look.x, look.y, look.z, fireworkSpeed, inaccuracy);

            rocket.getPersistentData().putBoolean(PD_ARM_CANNON, true);

            level.addFreshEntity(rocket);
            return true;
        }

        // Everything else that is a ProjectileItem
        if (ammoOne.getItem() instanceof ProjectileItem projItem) {
            Direction dir = Direction.getNearest(look.x, look.y, look.z);
            Projectile proj = projItem.asProjectile(level, start, ammoOne, dir);
            if (proj == null) return false;

            proj.setOwner(sp);
            proj.setPos(start.x, start.y, start.z);

            float speed = (proj instanceof AbstractArrow) ? arrowSpeed : genericSpeed;
            projItem.shoot(proj, look.x, look.y, look.z, speed, inaccuracy);

            level.addFreshEntity(proj);
            return true;
        }

        return false;
    }

    /**
     * Force arm-cannon-fired fireworks to explode visually at the exact impact point (block OR entity).
     * We do NOT spawn a second rocket entity; instead we:
     * - detect collision
     * - move the already-tracked rocket to the hit location
     * - broadcast the vanilla entity event used to render the explosion
     * - discard the rocket
     */
    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class ArmCannonRocketImpactFix {
        private ArmCannonRocketImpactFix() {}

        @SubscribeEvent
        public static void onEntityTick(EntityTickEvent.Post event) {
            Entity e = event.getEntity();
            if (e.level().isClientSide) return;

            if (!(e instanceof FireworkRocketEntity rocket)) return;
            if (rocket.isRemoved()) return;

            CompoundTag pd = rocket.getPersistentData();
            if (!pd.getBoolean(PD_ARM_CANNON)) return;

            Vec3 from = rocket.position();
            Vec3 motion = rocket.getDeltaMovement();
            Vec3 to = from.add(motion);

            BlockHitResult blockHit = rocket.level().clip(new ClipContext(
                    from, to,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    rocket
            ));

            EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                    rocket.level(),
                    rocket,
                    from,
                    to,
                    rocket.getBoundingBox().expandTowards(motion).inflate(0.25),
                    hit -> hit.isPickable() && hit != rocket.getOwner()
            );

            boolean hitSomething = (entityHit != null)
                    || (blockHit.getType() != HitResult.Type.MISS)
                    || rocket.horizontalCollision
                    || rocket.verticalCollision;

            if (!hitSomething) return;

            Vec3 at = (entityHit != null) ? entityHit.getLocation()
                    : (blockHit.getType() != HitResult.Type.MISS ? blockHit.getLocation() : rocket.position());

            // Move tracked rocket to impact point, then trigger the vanilla explosion render event.
            rocket.setPos(at.x, at.y, at.z);
            rocket.setDeltaMovement(Vec3.ZERO);

            ((ServerLevel) rocket.level()).broadcastEntityEvent(rocket, (byte) 17);

            rocket.discard();
        }
    }
}
