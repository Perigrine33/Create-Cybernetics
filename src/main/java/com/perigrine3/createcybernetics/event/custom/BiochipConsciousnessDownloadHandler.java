package com.perigrine3.createcybernetics.event.custom;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.item.generic.BiochipDataShardItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class BiochipConsciousnessDownloadHandler {
    private BiochipConsciousnessDownloadHandler() {}

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        for (ServerPlayer sp : event.getServer().getPlayerList().getPlayers()) {
            if (!sp.hasData(ModAttachments.CYBERWARE)) continue;

            PlayerCyberwareData data = sp.getData(ModAttachments.CYBERWARE);

            boolean changed = false;

            Set<String> slottedIds = new HashSet<>();

            for (int i = 0; i < PlayerCyberwareData.CHIPWARE_SLOT_COUNT; i++) {
                ItemStack stack = data.getChipwareStack(i);
                if (stack.isEmpty()) continue;
                if (!stack.is(ModItems.DATA_SHARD_BIOCHIP.get())) continue;

                CompoundTag tag = BiochipDataShardItem.getOrCreateTag(stack);

                String id = tag.getString(BiochipDataShardItem.TAG_ID);
                if (id == null || id.isBlank()) {
                    id = UUID.randomUUID().toString();
                    tag.putString(BiochipDataShardItem.TAG_ID, id);
                    changed = true;
                }
                slottedIds.add(id);

                if (!tag.contains(BiochipDataShardItem.TAG_PROGRESS)) {
                    tag.putLong(BiochipDataShardItem.TAG_PROGRESS, 0L);
                    changed = true;
                }

                boolean done = tag.getBoolean(BiochipDataShardItem.TAG_DONE);

                if (!done) {
                    long ticks = tag.getLong(BiochipDataShardItem.TAG_PROGRESS);
                    if (ticks < 0) ticks = 0;

                    ticks++;

                    if (ticks >= BiochipDataShardItem.TOTAL_TICKS) {
                        ticks = BiochipDataShardItem.TOTAL_TICKS;
                        tag.putBoolean(BiochipDataShardItem.TAG_DONE, true);
                        writeOwner(tag, sp);
                    }

                    tag.putLong(BiochipDataShardItem.TAG_PROGRESS, ticks);
                    changed = true;
                }

                BiochipDataShardItem.setTag(stack, tag);
                data.setChipwareStack(i, stack);
            }

            changed |= resetUnslottedBiochips(sp, slottedIds);

            if (changed) {
                data.setDirty();

                if ((sp.serverLevel().getGameTime() % 20L) == 0L) {
                    sp.syncData(ModAttachments.CYBERWARE);
                }
            }
        }
    }

    private static void writeOwner(CompoundTag tag, ServerPlayer sp) {
        tag.putString(BiochipDataShardItem.TAG_OWNER_UUID, sp.getUUID().toString());
        tag.putString(BiochipDataShardItem.TAG_OWNER_NAME, sp.getGameProfile().getName());
    }

    private static boolean resetUnslottedBiochips(ServerPlayer sp, Set<String> slottedIds) {
        boolean changed = false;
        Inventory inv = sp.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.isEmpty()) continue;
            if (!s.is(ModItems.DATA_SHARD_BIOCHIP.get())) continue;

            CompoundTag tag = BiochipDataShardItem.getTagOrNull(s);
            if (tag == null) continue;

            // If it's already done, never reset it again (even when not slotted).
            if (tag.getBoolean(BiochipDataShardItem.TAG_DONE)) {
                continue;
            }

            String id = tag.getString(BiochipDataShardItem.TAG_ID);
            if (id == null || id.isBlank()) continue;

            if (!slottedIds.contains(id)) {
                resetDownload(tag);
                BiochipDataShardItem.setTag(s, tag);
                changed = true;
            }
        }

        return changed;
    }

    private static void resetDownload(CompoundTag tag) {
        // Keep the ID so it remains the "same chip", but wipe progress + owner + done state.
        tag.putLong(BiochipDataShardItem.TAG_PROGRESS, 0L);
        tag.putBoolean(BiochipDataShardItem.TAG_DONE, false);

        tag.remove(BiochipDataShardItem.TAG_OWNER_UUID);
        tag.remove(BiochipDataShardItem.TAG_OWNER_NAME);

        // If you want the tooltip to disappear entirely when reset, you can also remove ID:
        // tag.remove(BiochipDataShardItem.TAG_ID);
    }
}
