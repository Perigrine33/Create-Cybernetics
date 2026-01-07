package com.perigrine3.createcybernetics.mixin;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.IChipwareSlotsMenu;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.screen.slot.DataShardSlot;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuChipwareSlotsMixin implements IChipwareSlotsMenu {

    @Shadow private Player owner;

    @Unique private static final int CC_SLOT_X = 77;
    @Unique private static final int CC_SLOT_Y0 = 8;
    @Unique private static final int CC_SLOT_SPACING = 18;

    @Unique private Container cc_chipInv;

    // menu-index where the two chip slots live (computed at construction)
    @Unique private int cc_chipStart = -1;

    @Inject(
            method = "<init>(Lnet/minecraft/world/entity/player/Inventory;ZLnet/minecraft/world/entity/player/Player;)V",
            at = @At("TAIL")
    )
    private void cc$initChipwareSlots(Inventory inv, boolean active, Player ownerParam, CallbackInfo ci) {
        this.cc_chipInv = ownerParam.level().isClientSide
                ? new net.minecraft.world.SimpleContainer(PlayerCyberwareData.CHIPWARE_SLOT_COUNT)
                : new com.perigrine3.createcybernetics.screen.container.ChipwareContainer(ownerParam);

        this.cc_chipStart = ((AbstractContainerMenuAccessor) (Object) this).cc$getSlots().size();

        var activeCheck = (java.util.function.BooleanSupplier) this::cc$chipwareSlotsActive;

        ((AbstractContainerMenuInvoker) (Object) this).cc$invokeAddSlot(new DataShardSlot(this.cc_chipInv, 0, CC_SLOT_X, CC_SLOT_Y0, activeCheck));
        ((AbstractContainerMenuInvoker) (Object) this).cc$invokeAddSlot(new DataShardSlot(this.cc_chipInv, 1, CC_SLOT_X, CC_SLOT_Y0 + CC_SLOT_SPACING, activeCheck));
    }

    @Override
    public boolean cc$chipwareSlotsActive() {
        Player p = this.owner;
        if (p == null) return false;

        // Client: don't create an empty attachment before the initial sync arrives.
        // Server: always use getData() so the attachment exists and can be written/saved.
        PlayerCyberwareData data = (!p.level().isClientSide || p.hasData(ModAttachments.CYBERWARE))
                ? p.getData(ModAttachments.CYBERWARE)
                : null;

        if (data == null) return false;

        return data.hasSpecificItem(ModItems.BRAINUPGRADES_CHIPWARESLOTS.get(), CyberwareSlot.BRAIN);
    }

    // ===== Shift-click routing =====
    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void cc$quickMoveIntoChipSlots(Player player, int index, CallbackInfoReturnable<ItemStack> cir) {
        if (player != null && (player.isCreative() || player.getAbilities().instabuild)) {
            return;
        }

        if (cc_chipStart < 0) return;
        if (!cc$chipwareSlotsActive()) return;

        Slot slot = ((InventoryMenu) (Object) this).getSlot(index);
        if (slot == null || !slot.hasItem()) return;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        final int chipStart = cc_chipStart;
        final int chipEnd = chipStart + 2;

        final int PLAYER_INV_START = 9;
        final int PLAYER_INV_END_EXCL = 45;

        AbstractContainerMenuInvoker invoker = (AbstractContainerMenuInvoker) (Object) this;

        boolean moved;

        if (index >= chipStart && index < chipEnd) {
            moved = invoker.cc$invokeMoveItemStackTo(stack, PLAYER_INV_START, PLAYER_INV_END_EXCL, false);
        }
        // If shift-clicking a data shard from anywhere else -> try move into our chip slots first.
        else if (stack.is(ModTags.Items.DATA_SHARDS)) {
            moved = invoker.cc$invokeMoveItemStackTo(stack, chipStart, chipEnd, false);
        } else {
            return; // not our concern; let vanilla handle
        }

        if (!moved) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        if (stack.getCount() == copy.getCount()) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }

        slot.onTake(player, stack);
        cir.setReturnValue(copy);
    }
}
