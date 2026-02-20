package com.perigrine3.createcybernetics.client.render;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class CyberwareLimbHider {

    private CyberwareLimbHider() {}

    private static final Map<Integer, VisibilitySnapshot> SNAPSHOTS = new HashMap<>();

    private static final String ENTITY_HOLO_SNAPSHOT_KEY = "cc_holo_snapshot";

    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;
        if (!(event.getRenderer() instanceof PlayerRenderer renderer)) return;
        PlayerModel<?> model = (PlayerModel<?>) renderer.getModel();

        PlayerCyberwareData data = player.hasData(ModAttachments.CYBERWARE) ? player.getData(ModAttachments.CYBERWARE) : null;

        SNAPSHOTS.put(player.getId(), VisibilitySnapshot.capture(model));

        if (data != null) {
            boolean hasLeftArm  = data.hasAnyTagged(ModTags.Items.LEFTARM_ITEMS,  CyberwareSlot.LARM);
            boolean hasRightArm = data.hasAnyTagged(ModTags.Items.RIGHTARM_ITEMS, CyberwareSlot.RARM);
            boolean hasLeftLeg  = data.hasAnyTagged(ModTags.Items.LEFTLEG_ITEMS,  CyberwareSlot.LLEG);
            boolean hasRightLeg = data.hasAnyTagged(ModTags.Items.RIGHTLEG_ITEMS, CyberwareSlot.RLEG);

            apply(model, event, hasLeftArm, hasRightArm, hasLeftLeg, hasRightLeg);
            return;
        }

        CompoundTag snap = player.getPersistentData().getCompound(ENTITY_HOLO_SNAPSHOT_KEY);
        if (snap.isEmpty()) return;
    }

    private static void apply(PlayerModel<?> model, RenderLivingEvent.Pre<?, ?> event,
                              boolean hasLeftArm, boolean hasRightArm, boolean hasLeftLeg, boolean hasRightLeg) {
        setLeftArmVisible(model, hasLeftArm);
        setRightArmVisible(model, hasRightArm);
        setLeftLegVisible(model, hasLeftLeg);
        setRightLegVisible(model, hasRightLeg);

        if (!hasLeftLeg && !hasRightLeg) event.getPoseStack().translate(0.0D, -0.75D, 0.0D);
    }

    @SubscribeEvent
    public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
        if (!(event.getEntity() instanceof Player player)) return;

        VisibilitySnapshot snap = SNAPSHOTS.remove(player.getId());
        if (snap == null) return;

        if (!(event.getRenderer() instanceof PlayerRenderer renderer)) return;
        if (!(renderer.getModel() instanceof PlayerModel<?> model)) return;

        snap.restore(model);
    }

    public static void setLeftArmVisible(PlayerModel<?> model, boolean visible) {
        model.leftArm.visible = visible;
        model.leftSleeve.visible = visible;
    }

    public static void setRightArmVisible(PlayerModel<?> model, boolean visible) {
        model.rightArm.visible = visible;
        model.rightSleeve.visible = visible;
    }

    public static void setLeftLegVisible(PlayerModel<?> model, boolean visible) {
        model.leftLeg.visible = visible;
        model.leftPants.visible = visible;
    }

    public static void setRightLegVisible(PlayerModel<?> model, boolean visible) {
        model.rightLeg.visible = visible;
        model.rightPants.visible = visible;
    }

    private static final class VisibilitySnapshot {
        private final boolean leftArm, rightArm, leftLeg, rightLeg;
        private final boolean leftSleeve, rightSleeve, leftPants, rightPants;

        private VisibilitySnapshot(
                boolean leftArm, boolean rightArm, boolean leftLeg, boolean rightLeg,
                boolean leftSleeve, boolean rightSleeve, boolean leftPants, boolean rightPants
        ) {
            this.leftArm = leftArm;
            this.rightArm = rightArm;
            this.leftLeg = leftLeg;
            this.rightLeg = rightLeg;
            this.leftSleeve = leftSleeve;
            this.rightSleeve = rightSleeve;
            this.leftPants = leftPants;
            this.rightPants = rightPants;
        }

        static VisibilitySnapshot capture(PlayerModel<?> model) {
            return new VisibilitySnapshot(
                    model.leftArm.visible,
                    model.rightArm.visible,
                    model.leftLeg.visible,
                    model.rightLeg.visible,
                    model.leftSleeve.visible,
                    model.rightSleeve.visible,
                    model.leftPants.visible,
                    model.rightPants.visible
            );
        }

        void restore(PlayerModel<?> model) {
            model.leftArm.visible = leftArm;
            model.rightArm.visible = rightArm;
            model.leftLeg.visible = leftLeg;
            model.rightLeg.visible = rightLeg;

            model.leftSleeve.visible = leftSleeve;
            model.rightSleeve.visible = rightSleeve;
            model.leftPants.visible = leftPants;
            model.rightPants.visible = rightPants;
        }
    }
}
