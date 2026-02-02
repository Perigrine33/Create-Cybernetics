package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public class OcelotPawsItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public OcelotPawsItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));
        }
    }

    @Override
    public boolean isDyeable(ItemStack stack, CyberwareSlot slot) {
        return true;
    }

    @Override
    public boolean isDyeable(ItemStack stack) {
        return true;
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return switch (slot) {
            case RLEG -> Set.of(ModTags.Items.RIGHTLEG_REPLACEMENTS);
            case LLEG -> Set.of(ModTags.Items.LEFTLEG_REPLACEMENTS);
            default -> Set.of();
        };
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.RLEG, CyberwareSlot.LLEG);
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
        ICyberwareItem.super.onTick(player);
    }

    public static boolean shouldSuppressVibration(Player player, Holder<GameEvent> event, GameEvent.Context context) {
        if (player.level().isClientSide) return false;
        if (!isInstalled(player)) return false;

        return event == GameEvent.STEP || event == GameEvent.HIT_GROUND;
    }

    public static boolean cancelFeetSounds(Player player, Holder<SoundEvent> sound, SoundSource source) {
        if (!isInstalled(player)) return false;

        if (source != SoundSource.PLAYERS) return false;

        ResourceLocation id = sound.unwrapKey().map(ResourceKey::location).orElse(null);
        if (id == null) return false;

        String path = id.getPath();

        boolean isFootstep = path.contains(".step");
        boolean isLanding = path.contains(".fall") || path.contains("small_fall") || path.contains("big_fall");

        return isFootstep || isLanding;
    }

    @SubscribeEvent
    public static void onPlayLevelSoundAtEntity(PlayLevelSoundEvent.AtEntity event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (cancelFeetSounds(player, event.getSound(), event.getSource())) {
            event.setCanceled(true);
        }
    }

    private static boolean isInstalled(Player player) {
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        return data.hasSpecificItem(ModItems.LEGUPGRADES_OCELOTPAWS.get(), CyberwareSlot.RLEG)
                && data.hasSpecificItem(ModItems.LEGUPGRADES_OCELOTPAWS.get(), CyberwareSlot.LLEG);
    }
}
