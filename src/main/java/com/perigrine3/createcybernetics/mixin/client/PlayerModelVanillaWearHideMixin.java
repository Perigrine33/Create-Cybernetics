package com.perigrine3.createcybernetics.mixin.client;

import com.perigrine3.createcybernetics.client.skin.SkinModifier;
import com.perigrine3.createcybernetics.client.skin.SkinModifierManager;
import com.perigrine3.createcybernetics.client.skin.SkinModifierState;
import com.perigrine3.createcybernetics.client.skin.SkinVanillaWearVisibility;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerModel.class)
public abstract class PlayerModelVanillaWearHideMixin {

    @Shadow
    @Final
    public ModelPart leftSleeve;

    @Shadow
    @Final
    public ModelPart rightSleeve;

    @Shadow
    @Final
    public ModelPart leftPants;

    @Shadow
    @Final
    public ModelPart rightPants;

    @Shadow
    @Final
    public ModelPart jacket;

    @Inject(method = "bodyParts", at = @At("RETURN"), cancellable = true)
    private void createcybernetics$hideVanillaWearLayerParts(CallbackInfoReturnable<Iterable<ModelPart>> cir) {
        if (SkinVanillaWearVisibility.isSuppressed()) return;

        AbstractClientPlayer player = SkinVanillaWearVisibility.currentPlayer();
        if (player == null) return;

        SkinModifierState state = SkinModifierManager.getPlayerSkinState(player);
        if (state == null) return;

        var hide = state.getHideMask();
        if (hide.isEmpty()) return;

        Iterable<ModelPart> original = cir.getReturnValue();
        if (original == null) return;

        HumanoidModel<?> model = (HumanoidModel<?>) (Object) this;

        List<ModelPart> filtered = new ArrayList<>();

        for (ModelPart part : original) {
            if (part == null) continue;

            if (part == model.hat && hide.contains(SkinModifier.HideVanilla.HAT)) continue;
            if (part == jacket && hide.contains(SkinModifier.HideVanilla.JACKET)) continue;
            if (part == leftSleeve && hide.contains(SkinModifier.HideVanilla.LEFT_SLEEVE)) continue;
            if (part == rightSleeve && hide.contains(SkinModifier.HideVanilla.RIGHT_SLEEVE)) continue;
            if (part == leftPants && hide.contains(SkinModifier.HideVanilla.LEFT_PANTS)) continue;
            if (part == rightPants && hide.contains(SkinModifier.HideVanilla.RIGHT_PANTS)) continue;

            filtered.add(part);
        }

        cir.setReturnValue(filtered);
    }
}