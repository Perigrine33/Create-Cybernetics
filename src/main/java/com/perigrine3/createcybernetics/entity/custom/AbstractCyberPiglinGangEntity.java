package com.perigrine3.createcybernetics.entity.custom;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.attributes.ModAttributes;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractCyberPiglinGangEntity extends Monster {

    private static final String NBT_VARIANT = "CyberPiglinGangVariant";
    private static final String NBT_DISTRACTED_TICKS = "CyberPiglinGangDistractedTicks";
    private static final String NBT_BARTER_TICKS = "CyberPiglinGangBarterTicks";

    private static final int VARIANT_COUNT = 4;
    private static final int DEFAULT_DISTRACTION_TICKS = 80;
    private static final int DEFAULT_BARTER_TICKS = 60;

    private static final net.minecraft.network.syncher.EntityDataAccessor<Integer> DATA_VARIANT =
            net.minecraft.network.syncher.SynchedEntityData.defineId(AbstractCyberPiglinGangEntity.class, net.minecraft.network.syncher.EntityDataSerializers.INT);

    private int distractedTicks;
    private int barterTicks;

    protected AbstractCyberPiglinGangEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setCanPickUpLoot(false);
        this.xpReward = 8;
    }

    public static AttributeSupplier.Builder createGangAttributes(double maxHealth, double attackDamage, double movementSpeed, double armor) {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, maxHealth)
                .add(Attributes.ATTACK_DAMAGE, attackDamage)
                .add(Attributes.MOVEMENT_SPEED, movementSpeed)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.15D)
                .add(Attributes.ARMOR, armor)
                .add(Attributes.ARMOR_TOUGHNESS, 0.0D)
                .add(Attributes.OXYGEN_BONUS, 0.0D)
                .add(Attributes.JUMP_STRENGTH, 0.42D)
                .add(Attributes.ATTACK_SPEED, 4.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.0D)
                .add(Attributes.LUCK, 0.0D)
                .add(Attributes.BLOCK_INTERACTION_RANGE, 4.5D)
                .add(Attributes.ENTITY_INTERACTION_RANGE, 3.0D)
                .add(Attributes.STEP_HEIGHT, 0.6D)
                .add(Attributes.GRAVITY, 0.08D)
                .add(Attributes.SCALE, 1.0D)
                .add(Attributes.FLYING_SPEED, 0.4D)
                .add(Attributes.BLOCK_BREAK_SPEED, 1.0D)
                .add(Attributes.SAFE_FALL_DISTANCE, 3.0D)
                .add(Attributes.BURNING_TIME, 1.0D)
                .add(Attributes.SUBMERGED_MINING_SPEED, 0.2D)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.0D)
                .add(Attributes.MINING_EFFICIENCY, 0.0D)
                .add(Attributes.SNEAKING_SPEED, 0.3D)

                .add(NeoForgeMod.SWIM_SPEED, 1.0D)

                .add(ModAttributes.XP_GAIN_MULTIPLIER, 0.0D)
                .add(ModAttributes.ORE_DROP_MULTIPLIER, 0.0D)
                .add(ModAttributes.HAGGLING, 0.0D)
                .add(ModAttributes.ARROW_INACCURACY, 0.0D)
                .add(ModAttributes.BREEDING_MULTIPLIER, 0.0D)
                .add(ModAttributes.CROP_MULTIPLIER, 0.0D)
                .add(ModAttributes.ELYTRA_SPEED, 0.0D)
                .add(ModAttributes.ELYTRA_HANDLING, 0.0D)
                .add(ModAttributes.INSOMNIA, 0.0D)
                .add(ModAttributes.ENDER_PEARL_DAMAGE, 0.0D);
    }

    public static boolean checkCyberPiglinSpawnRules(
            EntityType<? extends Mob> type,
            ServerLevelAccessor level,
            MobSpawnType reason,
            BlockPos pos,
            RandomSource random
    ) {
        return level.getDifficulty() != Difficulty.PEACEFUL
                && Mob.checkMobSpawnRules(type, level, reason, pos, random);
    }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, 0);
    }

    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            MobSpawnType spawnType,
            @Nullable SpawnGroupData spawnGroupData
    ) {
        SpawnGroupData out = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        setTextureVariant(this.getRandom().nextInt(VARIANT_COUNT));
        return out;
    }

    public int getTextureVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    public void setTextureVariant(int variant) {
        this.entityData.set(DATA_VARIANT, Math.floorMod(variant, VARIANT_COUNT));
    }

    public ResourceLocation getGangTextureLocation() {
        return ResourceLocation.fromNamespaceAndPath(
                CreateCybernetics.MODID,
                "textures/entity/punklin/" + getGangTextureFolder() + "_" + getTextureVariant() + ".png"
        );
    }

    protected abstract String getGangTextureFolder();

    protected abstract boolean canBarter();

    protected abstract boolean isDistractedByGold();

    protected abstract boolean isDistractedByCyberware();

    protected ResourceKey<LootTable> getBarterLootTable() {
        return BuiltInLootTables.PIGLIN_BARTERING;
    }

    public boolean isInterestedInGroundItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (isDistractedByCyberware() && isCyberware(stack)) return true;
        return isDistractedByGold() && isGold(stack);
    }

    public boolean isBarterItem(ItemStack stack) {
        return canBarter() && isCyberware(stack);
    }

    public static boolean isCyberware(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.is(ModTags.Items.CYBERWARE_ITEM);
    }

    public static boolean isGold(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.is(net.minecraft.tags.ItemTags.PIGLIN_LOVED);
    }

    public boolean isDistracted() {
        return distractedTicks > 0;
    }

    public void startDistraction(int ticks) {
        distractedTicks = Math.max(distractedTicks, ticks);
        this.setTarget(null);
        this.getNavigation().stop();
    }

    public void startBarterDelay() {
        barterTicks = DEFAULT_BARTER_TICKS;
        startDistraction(DEFAULT_DISTRACTION_TICKS);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!this.level().isClientSide && isBarterItem(stack)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            this.swing(hand);
            this.startBarterDelay();
            return InteractionResult.CONSUME;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide) return;

        if (distractedTicks > 0) {
            distractedTicks--;
            this.setTarget(null);
            this.getNavigation().stop();
        }

        if (barterTicks > 0) {
            barterTicks--;

            if (barterTicks == 0) {
                dropPiglinBarterLoot();
            }
        }
    }

    private void dropPiglinBarterLoot() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        LootTable lootTable = serverLevel.getServer()
                .reloadableRegistries()
                .getLootTable(getBarterLootTable());

        LootParams params = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.THIS_ENTITY, this)
                .create(LootContextParamSets.PIGLIN_BARTER);

        List<ItemStack> stacks = lootTable.getRandomItems(params);
        if (stacks.isEmpty()) return;

        Player nearestPlayer = this.level().getNearestPlayer(this, 12.0D);

        for (ItemStack stack : stacks) {
            if (stack == null || stack.isEmpty()) continue;

            if (nearestPlayer != null) {
                throwStackToward(stack, nearestPlayer.position());
            } else {
                this.spawnAtLocation(stack);
            }
        }
    }

    private void throwStackToward(ItemStack stack, Vec3 target) {
        if (stack == null || stack.isEmpty()) return;

        ItemEntity itemEntity = new ItemEntity(
                this.level(),
                this.getX(),
                this.getEyeY() - 0.3D,
                this.getZ(),
                stack.copy()
        );

        Vec3 direction = target.subtract(this.position());
        if (direction.lengthSqr() > 1.0E-6D) {
            direction = direction.normalize();
        } else {
            direction = Vec3.directionFromRotation(0.0F, this.getYRot());
        }

        itemEntity.setDeltaMovement(direction.x * 0.3D, 0.25D, direction.z * 0.3D);
        this.level().addFreshEntity(itemEntity);
    }

    public static int countPlayerNonWetwareCyberware(Player player) {
        if (player == null) return 0;
        if (!player.hasData(ModAttachments.CYBERWARE)) return 0;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return 0;

        int count = 0;

        for (var entry : data.getAll().entrySet()) {
            CyberwareSlot slot = entry.getKey();
            InstalledCyberware[] arr = entry.getValue();
            if (slot == null || arr == null) continue;

            for (InstalledCyberware installed : arr) {
                if (installed == null) continue;

                ItemStack stack = installed.getItem();
                if (stack == null || stack.isEmpty()) continue;
                if (!stack.is(ModTags.Items.CYBERWARE_ITEM)) continue;
                if (stack.is(ModTags.Items.WETWARE_ITEM)) continue;

                count++;
            }
        }

        return count;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    @Override
    protected net.minecraft.sounds.SoundEvent getAmbientSound() {
        return net.minecraft.sounds.SoundEvents.PIGLIN_AMBIENT;
    }

    @Override
    protected net.minecraft.sounds.SoundEvent getHurtSound(DamageSource damageSource) {
        return net.minecraft.sounds.SoundEvents.PIGLIN_HURT;
    }

    @Override
    protected net.minecraft.sounds.SoundEvent getDeathSound() {
        return net.minecraft.sounds.SoundEvents.PIGLIN_DEATH;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt(NBT_VARIANT, getTextureVariant());
        compound.putInt(NBT_DISTRACTED_TICKS, distractedTicks);
        compound.putInt(NBT_BARTER_TICKS, barterTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        if (compound.contains(NBT_VARIANT)) {
            setTextureVariant(compound.getInt(NBT_VARIANT));
        }

        distractedTicks = compound.getInt(NBT_DISTRACTED_TICKS);
        barterTicks = compound.getInt(NBT_BARTER_TICKS);
    }
}