package com.perigrine3.createcybernetics.mixin;

import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerSprintWhileCrouchingMixin {

    @Inject(method = "canSprint", at = @At("HEAD"), cancellable = true)
    private void cc$allowSprintWhileCrouching(CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player) (Object) this;

        if (player.isSprinting()) return;

        if (!player.hasData(ModAttachments.CYBERWARE)) return;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (!data.hasChipwareShardExact(ModItems.DATA_SHARD_BLACK.get())) return;

        cir.setReturnValue(true);
    }
}
