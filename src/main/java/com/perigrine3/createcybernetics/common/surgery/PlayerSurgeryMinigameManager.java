package com.perigrine3.createcybernetics.common.surgery;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.block.entity.SurgeryTableBlockEntity;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.event.custom.FullBorgHandler;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.network.payload.PlayerSurgeryEndPayload;
import com.perigrine3.createcybernetics.network.payload.PlayerSurgeryResultPayload;
import com.perigrine3.createcybernetics.network.payload.PlayerSurgeryRoundPayload;
import com.perigrine3.createcybernetics.network.payload.PlayerSurgeryStartPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class PlayerSurgeryMinigameManager {

    public static final int COUNTDOWN_TICKS = 60;
    public static final int ROUND_COUNT = 4;
    public static final int ROUND_DURATION_TICKS = 40;

    private static final float MIN_SUCCESS_ZONE_WIDTH = 0.10F;
    private static final float MAX_SUCCESS_ZONE_WIDTH = 0.16F;
    private static final float IMPLANT_MARGIN_MULTIPLIER = 1.5F;
    private static final float KILDARE_MARGIN_MULTIPLIER = 1.5F;

    private static final int SUCCESS_DAMAGE = 3;
    private static final int FAILURE_DAMAGE_MIN = 4;
    private static final int FAILURE_DAMAGE_MAX = 6;

    private static final Map<UUID, Session> SESSIONS_BY_SURGEON = new HashMap<>();

    private PlayerSurgeryMinigameManager() {
    }

    public static boolean hasActiveSession(ServerPlayer player) {
        if (SESSIONS_BY_SURGEON.containsKey(player.getUUID())) {
            return true;
        }

        for (Session session : SESSIONS_BY_SURGEON.values()) {
            if (session.patient.getUUID().equals(player.getUUID())) {
                return true;
            }
        }

        return false;
    }

    public static void startCountdown(ServerPlayer surgeon, ServerPlayer patient, SurgeryTableBlockEntity table) {
        if (surgeon == patient) {
            return;
        }

        if (hasActiveSession(surgeon) || hasActiveSession(patient)) {
            return;
        }

        boolean implantBonus = hasSurgicalAssistImplant(surgeon);
        boolean kildareBonus = isKildareSurgeon(surgeon);

        Session session = new Session(
                UUID.randomUUID(),
                surgeon,
                patient,
                table,
                implantBonus,
                kildareBonus
        );

        SESSIONS_BY_SURGEON.put(surgeon.getUUID(), session);

        PacketDistributor.sendToPlayer(surgeon, new PlayerSurgeryStartPayload(
                session.sessionId,
                patient.getUUID(),
                patient.getDisplayName().getString(),
                COUNTDOWN_TICKS,
                implantBonus
        ));
    }

    public static void handleClick(ServerPlayer surgeon, UUID sessionId) {
        Session session = SESSIONS_BY_SURGEON.get(surgeon.getUUID());
        if (session == null || !session.sessionId.equals(sessionId)) {
            return;
        }

        if (session.resolveClick()) {
            SESSIONS_BY_SURGEON.remove(surgeon.getUUID());
        }
    }

    public static void handleCancel(ServerPlayer surgeon, UUID sessionId) {
        Session session = SESSIONS_BY_SURGEON.get(surgeon.getUUID());
        if (session == null || !session.sessionId.equals(sessionId)) {
            return;
        }

        if (session.cancelFromSurgeon()) {
            SESSIONS_BY_SURGEON.remove(surgeon.getUUID());
        }
    }

    public static void tickAll() {
        if (SESSIONS_BY_SURGEON.isEmpty()) {
            return;
        }

        Iterator<Map.Entry<UUID, Session>> iterator = SESSIONS_BY_SURGEON.entrySet().iterator();

        while (iterator.hasNext()) {
            Session session = iterator.next().getValue();

            if (session.tick()) {
                iterator.remove();
            }
        }
    }

    private static boolean hasSurgicalAssistImplant(ServerPlayer surgeon) {
        PlayerCyberwareData data = surgeon.getData(ModAttachments.CYBERWARE);
        if (data.hasSpecificItem(ModItems.ARMUPGRADES_RIPPERCLAW.get(), CyberwareSlot.LARM) ||
                data.hasSpecificItem(ModItems.ARMUPGRADES_RIPPERCLAW.get(), CyberwareSlot.RARM)) {
            return true;
        }

        return false;
    }

    private static boolean isKildareSurgeon(ServerPlayer surgeon) {
        PlayerCyberwareData data = surgeon.getData(ModAttachments.CYBERWARE);
        return FullBorgHandler.isKildare(data);
    }

    private static final class Session {
        private final UUID sessionId;
        private final ServerPlayer surgeon;
        private final ServerPlayer patient;
        private final SurgeryTableBlockEntity table;
        private final boolean implantBonus;
        private final boolean kildareBonus;

        private int countdownTicksRemaining = COUNTDOWN_TICKS;
        private boolean countdownComplete = false;

        private int roundIndex = -1;
        private int roundTicksElapsed = 0;
        private float successCenter = 0.5F;
        private float successWidth = 0.12F;
        private boolean resolvedThisRound = false;

        private Session(
                UUID sessionId,
                ServerPlayer surgeon,
                ServerPlayer patient,
                SurgeryTableBlockEntity table,
                boolean implantBonus,
                boolean kildareBonus
        ) {
            this.sessionId = sessionId;
            this.surgeon = surgeon;
            this.patient = patient;
            this.table = table;
            this.implantBonus = implantBonus;
            this.kildareBonus = kildareBonus;
        }

        private boolean tick() {
            if (!isStillValid()) {
                cancel();
                return true;
            }

            if (!countdownComplete) {
                countdownTicksRemaining--;

                if (countdownTicksRemaining <= 0) {
                    countdownComplete = true;
                    return startNextRound();
                }

                return false;
            }

            if (resolvedThisRound) {
                return false;
            }

            roundTicksElapsed++;

            if (roundTicksElapsed >= ROUND_DURATION_TICKS) {
                int damage = rollFailureDamage();
                return resolve(false, damage);
            }

            return false;
        }

        private boolean cancelFromSurgeon() {
            if (countdownComplete) {
                return false;
            }

            cancel();
            return true;
        }

        private boolean resolveClick() {
            if (!countdownComplete || resolvedThisRound) {
                return false;
            }

            float slider = getServerSliderPosition();
            float halfWidth = successWidth * 0.5F;

            boolean success = slider >= successCenter - halfWidth
                    && slider <= successCenter + halfWidth;

            int damage = success ? SUCCESS_DAMAGE : rollFailureDamage();

            return resolve(success, damage);
        }

        private boolean resolve(boolean success, int damage) {
            resolvedThisRound = true;

            applySurgeryDamage(damage);

            PacketDistributor.sendToPlayer(surgeon, new PlayerSurgeryResultPayload(
                    sessionId,
                    roundIndex,
                    success,
                    damage
            ));

            if (!patient.isAlive()) {
                cancel();
                return true;
            }

            return startNextRound();
        }

        private void applySurgeryDamage(int damage) {
            patient.invulnerableTime = 0;
            patient.hurtTime = 0;
            patient.hurt(patient.damageSources().generic(), damage);
        }

        private boolean startNextRound() {
            roundIndex++;
            roundTicksElapsed = 0;
            resolvedThisRound = false;

            if (roundIndex >= ROUND_COUNT) {
                finish();
                return true;
            }

            float baseWidth = MIN_SUCCESS_ZONE_WIDTH
                    + surgeon.getRandom().nextFloat() * (MAX_SUCCESS_ZONE_WIDTH - MIN_SUCCESS_ZONE_WIDTH);

            successWidth = baseWidth;

            if (implantBonus) {
                successWidth *= IMPLANT_MARGIN_MULTIPLIER;
            }

            if (kildareBonus) {
                successWidth *= KILDARE_MARGIN_MULTIPLIER;
            }

            successWidth = Math.min(successWidth, 0.95F);

            float halfWidth = successWidth * 0.5F;
            successCenter = halfWidth + surgeon.getRandom().nextFloat() * (1.0F - successWidth);

            PacketDistributor.sendToPlayer(surgeon, new PlayerSurgeryRoundPayload(
                    sessionId,
                    roundIndex,
                    ROUND_COUNT,
                    ROUND_DURATION_TICKS,
                    successCenter,
                    successWidth,
                    implantBonus
            ));

            return false;
        }

        private void finish() {
            SurgeryController.performSurgery(patient, table);

            if (patient.isSleeping()) {
                patient.stopSleeping();
            }

            table.clearPatient();

            PacketDistributor.sendToPlayer(surgeon, new PlayerSurgeryEndPayload(sessionId, true));
        }

        private void cancel() {
            PacketDistributor.sendToPlayer(surgeon, new PlayerSurgeryEndPayload(sessionId, false));
        }

        private boolean isStillValid() {
            if (!surgeon.isAlive() || !patient.isAlive()) {
                return false;
            }

            if (surgeon.serverLevel() != patient.serverLevel()) {
                return false;
            }

            if (!patient.isSleeping()) {
                return false;
            }

            if (patient.getSleepingPos().isEmpty()) {
                return false;
            }

            return patient.getSleepingPos().get().equals(table.getBlockPos());
        }

        private float getServerSliderPosition() {
            float progress = Math.min(1.0F, Math.max(0.0F, roundTicksElapsed / (float) ROUND_DURATION_TICKS));

            if ((roundIndex & 1) == 1) {
                return 1.0F - progress;
            }

            return progress;
        }

        private int rollFailureDamage() {
            return FAILURE_DAMAGE_MIN + surgeon.getRandom().nextInt(FAILURE_DAMAGE_MAX - FAILURE_DAMAGE_MIN + 1);
        }
    }
}