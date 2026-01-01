package com.perigrine3.createcybernetics.screen.custom;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.item.cyberware.ArmCannonItem;
import com.perigrine3.createcybernetics.screen.ModMenuTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ArmCannonMenu extends AbstractContainerMenu {

    private static final int DATA_X0 = 192;
    private static final int DATA_Y0 = 86;
    private static final int SLOT_SPACING = 18;

    private static final int INV_X0 = 48;
    private static final int INV_Y0 = 142;
    private static final int HOTBAR_Y = 200;

    private final Player owner;
    private final Container armInv;
    private final HolderLookup.Provider provider;

    private final CyberwareSlot installedSlot;
    private final int installedIndex;

    public ArmCannonMenu(int id, Inventory playerInv, RegistryFriendlyByteBuf buf) {
        this(id, playerInv,
                CyberwareSlot.valueOf(buf.readUtf()),
                buf.readVarInt());
    }

    public ArmCannonMenu(int id, Inventory playerInv, CyberwareSlot installedSlot, int installedIndex) {
        super(ModMenuTypes.ARM_CANNON_MENU.get(), id);

        this.owner = playerInv.player;
        this.installedSlot = installedSlot;
        this.installedIndex = installedIndex;
        this.provider = owner.level().registryAccess();
        this.armInv = new SimpleContainer(ArmCannonItem.SLOT_COUNT) {
            @Override public boolean stillValid(Player p) { return true; }
        };

        if (owner instanceof ServerPlayer sp) {
            ItemStack real = getRealInstalledArmCannonStack(sp);
            ArmCannonItem.loadFromInstalledStack(real, provider, armInv);
        }

        for (int i = 0; i < ArmCannonItem.SLOT_COUNT; i++) {
            final int idx = i;
            int x = DATA_X0 + (idx % 2) * SLOT_SPACING;
            int y = DATA_Y0 + (idx / 2) * SLOT_SPACING;

            this.addSlot(new Slot(armInv, idx, x, y) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return ArmCannonItem.isValidStoredItem(stack);
                }

                @Override
                public int getMaxStackSize(ItemStack stack) {
                    return Math.max(1, stack.getMaxStackSize());
                }
            });
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int x = INV_X0 + col * SLOT_SPACING;
                int y = INV_Y0 + row * SLOT_SPACING;
                this.addSlot(new Slot(playerInv, 9 + row * 9 + col, x, y));
            }
        }

        for (int col = 0; col < 9; col++) {
            int x = INV_X0 + col * SLOT_SPACING;
            this.addSlot(new Slot(playerInv, col, x, HOTBAR_Y));
        }
    }

    private ItemStack getRealInstalledArmCannonStack(ServerPlayer sp) {
        if (!sp.hasData(ModAttachments.CYBERWARE)) return ItemStack.EMPTY;
        PlayerCyberwareData data = sp.getData(ModAttachments.CYBERWARE);
        if (data == null) return ItemStack.EMPTY;
        var inst = data.get(installedSlot, installedIndex);
        if (inst == null) return ItemStack.EMPTY;
        ItemStack real = inst.getItem();
        return real == null ? ItemStack.EMPTY : real;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (player.level().isClientSide) return ItemStack.EMPTY;

        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stackInSlot = slot.getItem();
        ItemStack copy = stackInSlot.copy();

        int armEnd = ArmCannonItem.SLOT_COUNT;
        int playerStart = armEnd;
        int playerEnd = this.slots.size();

        if (index < armEnd) {
            if (!this.moveItemStackTo(stackInSlot, playerStart, playerEnd, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!ArmCannonItem.isValidStoredItem(stackInSlot)) return ItemStack.EMPTY;
            if (!this.moveItemStackTo(stackInSlot, 0, armEnd, false)) return ItemStack.EMPTY;
        }

        if (stackInSlot.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        return copy;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!(player instanceof ServerPlayer sp)) return;
        if (!sp.hasData(ModAttachments.CYBERWARE)) return;
        PlayerCyberwareData data = sp.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        ItemStack real = getRealInstalledArmCannonStack(sp);
        boolean stillInstalled = !real.isEmpty() && real.getItem() == ModItems.ARMUPGRADES_ARMCANNON.get();

        if (stillInstalled) {
            ArmCannonItem.saveIntoInstalledStack(real, provider, armInv);
            data.setDirty();
            sp.syncData(ModAttachments.CYBERWARE);
            return;
        }

        if (!real.isEmpty()) {
            ArmCannonItem.saveIntoInstalledStack(real, provider, armInv);
            ArmCannonItem.dropAndClearInstalledStack(sp, provider, real);
        } else {
            for (int i = 0; i < ArmCannonItem.SLOT_COUNT; i++) {
                ItemStack st = armInv.getItem(i);
                if (!st.isEmpty()) sp.drop(st, false);
            }
        }

        data.setDirty();
        sp.syncData(ModAttachments.CYBERWARE);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
