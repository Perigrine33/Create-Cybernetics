package com.perigrine3.createcybernetics.entity.custom;

import com.perigrine3.createcybernetics.screen.custom.TattooMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TatHogEntity extends Piglin {

    public TatHogEntity(EntityType<? extends Piglin> entityType, Level level) {
        super(entityType, level);
        this.setCanPickUpLoot(false);
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return Piglin.createAttributes();
    }

    @Override
    public boolean isConverting() {
        return false;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        if (this.isConverting()) {
            this.finishConversion((net.minecraft.server.level.ServerLevel) this.level());
        }
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (containerId, inventory, p) -> new TattooMenu(containerId, inventory),
                    Component.translatable("gui.createcybernetics.tattoo_artist")
            ));
        }

        return InteractionResult.CONSUME;
    }
}