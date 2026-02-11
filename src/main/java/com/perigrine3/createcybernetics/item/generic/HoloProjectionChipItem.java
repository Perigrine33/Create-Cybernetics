package com.perigrine3.createcybernetics.item.generic;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public class HoloProjectionChipItem extends Item {

    public static final String TAG_ROOT = "cc_holo_chip";
    public static final String TAG_ENTITY_TYPE = "entity_type";
    public static final String TAG_ENTITY_NBT  = "entity_nbt";
    public static final String TAG_ENTITY_NAME = "entity_name";
    private static final String TAG_LAST_IMPRINT_TICK = "last_imprint_tick";

    private static final Set<String> STRIP_KEYS = Set.of(
            "UUID", "Pos", "Motion", "Rotation", "FallDistance", "Fire", "Air",
            "OnGround", "PortalCooldown", "Dimension", "Invulnerable", "Silent",
            "Passengers", "Vehicle", "Leash", "Tags", "Brain",
            "HandItems", "HandDropChances", "ArmorItems", "ArmorDropChances",
            "Inventory", "Items", "Offers", "Attributes"
    );

    public HoloProjectionChipItem(Properties props) {
        super(props);
    }

    // ============================================================
    // Event Handlers (Ensures Shift+Right Click works on all entities)
    // ============================================================

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (performImprint(event.getEntity(), event.getTarget(), event.getItemStack(), event.getHand())) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        if (performImprint(event.getEntity(), event.getTarget(), event.getItemStack(), event.getHand())) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    /**
     * Internal logic handler for imprinting.
     * Returns true if the imprint was successful or already handled.
     */
    private static boolean performImprint(Player player, Entity target, ItemStack stack, InteractionHand hand) {
        if (!(stack.getItem() instanceof HoloProjectionChipItem)) return false;
        if (!(target instanceof LivingEntity livingTarget) || target instanceof Player) return false;

        // On client, we return true to swing arm and stop other interactions
        if (player.level().isClientSide) return true;

        if (player instanceof ServerPlayer sp) {
            boolean wrote = imprintFromEntity(stack, livingTarget, sp.level().getGameTime());
            if (wrote) {
                syncHeldStack(sp, hand, stack);
            }
        }
        return true;
    }

    private static boolean imprintFromEntity(ItemStack stack, LivingEntity target, long gameTime) {
        ResourceLocation typeId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());
        if (typeId == null) return false;

        CompoundTag root = getCustomDataCopy(stack);
        CompoundTag chip = root.contains(TAG_ROOT, Tag.TAG_COMPOUND) ? root.getCompound(TAG_ROOT) : new CompoundTag();

        // Prevent double-firing within the same tick
        if (chip.contains(TAG_LAST_IMPRINT_TICK, Tag.TAG_LONG) && chip.getLong(TAG_LAST_IMPRINT_TICK) == gameTime) {
            return false;
        }

        CompoundTag entityTag = new CompoundTag();
        target.saveWithoutId(entityTag);
        sanitizeEntityTag(entityTag);

        chip.putString(TAG_ENTITY_TYPE, typeId.toString());

        if (!entityTag.isEmpty()) chip.put(TAG_ENTITY_NBT, entityTag);
        else chip.remove(TAG_ENTITY_NBT);

        chip.putString(TAG_ENTITY_NAME, target.getName().getString());
        chip.putLong(TAG_LAST_IMPRINT_TICK, gameTime);

        root.put(TAG_ROOT, chip);
        setCustomData(stack, root);
        return true;
    }

    private static void syncHeldStack(ServerPlayer player, InteractionHand hand, ItemStack stack) {
        player.setItemInHand(hand, stack);
        player.getInventory().setChanged();
        player.containerMenu.broadcastChanges();
    }

    private static void sanitizeEntityTag(CompoundTag tag) {
        for (String k : STRIP_KEYS) tag.remove(k);
    }

    // ============================================================
    // Tooltip and Data Management
    // ============================================================

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
        if (!hasEntityData(stack)) return;

        CompoundTag chip = getChipTag(stack);
        String name = chip.getString(TAG_ENTITY_NAME);
        if (!name.isBlank()) {
            tooltip.add(Component.literal(name).withStyle(s -> s.withColor(0x55FFFF))); // Cyan color for flair
        }
    }

    private static CompoundTag getCustomDataCopy(ItemStack stack) {
        CustomData cd = stack.get(DataComponents.CUSTOM_DATA);
        return cd == null ? new CompoundTag() : cd.copyTag();
    }

    private static void setCustomData(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static boolean hasEntityData(ItemStack stack) {
        CompoundTag root = getCustomDataCopy(stack);
        return root.contains(TAG_ROOT, Tag.TAG_COMPOUND) &&
                root.getCompound(TAG_ROOT).contains(TAG_ENTITY_TYPE, Tag.TAG_STRING);
    }

    public static CompoundTag getChipTag(ItemStack stack) {
        CompoundTag root = getCustomDataCopy(stack);
        if (!root.contains(TAG_ROOT, Tag.TAG_COMPOUND)) return new CompoundTag();
        return root.getCompound(TAG_ROOT).copy();
    }
}