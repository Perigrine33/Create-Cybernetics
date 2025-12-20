package com.perigrine3.createcybernetics.mixin;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.client.Camera;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class PlayerEyeHeightMixin {

    @Shadow private Vec3 position;

    @Inject(method = "setup", at = @At("TAIL"))
    private void createcybernetics$applyLegCameraSink(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        if (!(entity instanceof LocalPlayer localPlayer)) return;

        PlayerCyberwareData data = localPlayer.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        double sink = computeLegSink(data);
        if (sink <= 0.0D) return;

        this.position = this.position.add(0.0D, -sink, 0.0D);
    }

    private static double computeLegSink(PlayerCyberwareData data) {
        boolean hasLeftLeg = data.hasAnyTagged(ModTags.Items.LEFTLEG_ITEMS, CyberwareSlot.LLEG);
        boolean hasRightLeg = data.hasAnyTagged(ModTags.Items.RIGHTLEG_ITEMS, CyberwareSlot.RLEG);

        if (!hasLeftLeg && !hasRightLeg) return 0.75D;
        return 0.0D;
    }
}
