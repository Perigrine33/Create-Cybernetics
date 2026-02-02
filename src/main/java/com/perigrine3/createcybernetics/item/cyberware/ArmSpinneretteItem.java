package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ArmSpinneretteItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public ArmSpinneretteItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost).withStyle(ChatFormatting.GOLD));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.RARM, CyberwareSlot.LARM);
    }

    @Override public boolean replacesOrgan() { return true; }
    @Override public Set<CyberwareSlot> getReplacedOrgans() { return Set.of(CyberwareSlot.RARM, CyberwareSlot.LARM); }

    @Override
    public Set<TagKey<Item>> incompatibleCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        if (slot == CyberwareSlot.LARM) {
            return Set.of(ModTags.Items.LEFTARM_REPLACEMENTS);
        }
        if (slot == CyberwareSlot.RARM) {
            return Set.of(ModTags.Items.RIGHTARM_REPLACEMENTS);
        }
        return Set.of();
    }

    /* -------------------- INTERACTION (INSTALLED CYBERWARE) -------------------- */

    private static boolean hasSpinneretteOnSide(Player player, CyberwareSlot slot) {
        if (player == null) return false;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        InstalledCyberware[] arr = data.getAll().get(slot);
        if (arr == null) return false;

        for (int i = 0; i < arr.length; i++) {
            InstalledCyberware cw = arr[i];
            if (cw == null) continue;

            ItemStack st = cw.getItem();
            if (st == null || st.isEmpty()) continue;

            if (st.getItem() instanceof ArmSpinneretteItem) {
                if (!data.isEnabled(slot, i)) continue;
                return true;
            }
        }

        return false;
    }

    private static boolean tryPlaceCobweb(Level level, Player player, BlockPos placePos) {
        if (!level.isInWorldBounds(placePos)) return false;
        if (!level.mayInteract(player, placePos)) return false;

        BlockState at = level.getBlockState(placePos);
        if (!at.canBeReplaced()) return false;

        BlockState cobweb = Blocks.COBWEB.defaultBlockState();
        level.setBlock(placePos, cobweb, 3);

        SoundType sound = cobweb.getSoundType(level, placePos, player);
        level.playSound(null, placePos, sound.getPlaceSound(), SoundSource.BLOCKS,
                (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

        return true;
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (player == null || !player.isCrouching()) return;

        CyberwareSlot slot = slotForHand(player, event.getHand());
        if (!hasSpinneretteOnSide(player, slot)) return;

        Level level = event.getLevel();
        if (level.isClientSide) return;

        BlockPos clicked = event.getPos();
        BlockPos placePos = level.getBlockState(clicked).canBeReplaced()
                ? clicked
                : clicked.relative(event.getFace());

        if (tryPlaceCobweb(level, player, placePos)) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.CONSUME);
        }
    }

    @SubscribeEvent
    public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        if (player == null || !player.isCrouching()) return;

        CyberwareSlot slot = slotForHand(player, event.getHand());
        if (!hasSpinneretteOnSide(player, slot)) return;

        Level level = player.level();
        if (level.isClientSide) return;

        BlockPos base = event.getTarget().blockPosition();
        BlockPos placePos = level.getBlockState(base).canBeReplaced() ? base : base.above();

        if (tryPlaceCobweb(level, player, placePos)) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.CONSUME);
        }
    }

    private static HumanoidArm armForHand(Player player, InteractionHand hand) {
        HumanoidArm main = player.getMainArm();
        if (hand == InteractionHand.MAIN_HAND) return main;
        return (main == HumanoidArm.RIGHT) ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
    }

    private static CyberwareSlot slotForHand(Player player, InteractionHand hand) {
        return armForHand(player, hand) == HumanoidArm.LEFT
                ? CyberwareSlot.LARM
                : CyberwareSlot.RARM;
    }
}
