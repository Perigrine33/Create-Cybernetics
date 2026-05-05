package com.perigrine3.createcybernetics.worldgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.perigrine3.createcybernetics.worldgen.ModStructureTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.List;
import java.util.Optional;

public class NetherCavernJigsawStructure extends Structure {

    public static final MapCodec<NetherCavernJigsawStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 20).fieldOf("size").forGetter(structure -> structure.maxDepth),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Codec.BOOL.fieldOf("use_expansion_hack").forGetter(structure -> structure.useExpansionHack),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
                    PoolAliasBinding.CODEC.listOf().optionalFieldOf("pool_aliases", List.of()).forGetter(structure -> structure.poolAliases),
                    DimensionPadding.CODEC.optionalFieldOf("dimension_padding", JigsawStructure.DEFAULT_DIMENSION_PADDING).forGetter(structure -> structure.dimensionPadding),
                    LiquidSettings.CODEC.optionalFieldOf("liquid_settings", JigsawStructure.DEFAULT_LIQUID_SETTINGS).forGetter(structure -> structure.liquidSettings)
            ).apply(instance, NetherCavernJigsawStructure::new)
    );

    private static final int MIN_SCAN_Y = 32;
    private static final int MAX_SCAN_Y = 112;

    /*
     * This is the area checked around the jigsaw start.
     *
     * Increase these if the hut is large and still clips into terrain.
     * Decrease them if generation becomes too rare.
     */
    private static final int CLEAR_RADIUS_XZ = 5;
    private static final int CLEAR_HEIGHT = 6;

    /*
     * These extra candidate offsets make the structure try several nearby positions
     * inside the selected structure chunk instead of giving up immediately if the
     * exact chunk center is bad.
     */
    private static final int[][] CANDIDATE_OFFSETS = {
            {0, 0},
            {4, 0},
            {-4, 0},
            {0, 4},
            {0, -4},
            {4, 4},
            {4, -4},
            {-4, 4},
            {-4, -4}
    };

    private final Holder<StructureTemplatePool> startPool;
    private final Optional<ResourceLocation> startJigsawName;
    private final int maxDepth;
    private final HeightProvider startHeight;
    private final boolean useExpansionHack;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final int maxDistanceFromCenter;
    private final List<PoolAliasBinding> poolAliases;
    private final DimensionPadding dimensionPadding;
    private final LiquidSettings liquidSettings;

    public NetherCavernJigsawStructure(
            StructureSettings settings,
            Holder<StructureTemplatePool> startPool,
            Optional<ResourceLocation> startJigsawName,
            int maxDepth,
            HeightProvider startHeight,
            boolean useExpansionHack,
            Optional<Heightmap.Types> projectStartToHeightmap,
            int maxDistanceFromCenter,
            List<PoolAliasBinding> poolAliases,
            DimensionPadding dimensionPadding,
            LiquidSettings liquidSettings
    ) {
        super(settings);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.maxDepth = maxDepth;
        this.startHeight = startHeight;
        this.useExpansionHack = useExpansionHack;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.poolAliases = poolAliases;
        this.dimensionPadding = dimensionPadding;
        this.liquidSettings = liquidSettings;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();

        int centerX = chunkPos.getMiddleBlockX();
        int centerZ = chunkPos.getMiddleBlockZ();

        for (int[] offset : CANDIDATE_OFFSETS) {
            int x = centerX + offset[0];
            int z = centerZ + offset[1];

            BlockPos validStart = findValidCavernStart(context, x, z);
            if (validStart == null) {
                continue;
            }

            PoolAliasLookup poolAliasLookup = PoolAliasLookup.create(this.poolAliases, validStart, context.seed());

            return JigsawPlacement.addPieces(
                    context,
                    this.startPool,
                    this.startJigsawName,
                    this.maxDepth,
                    validStart,
                    this.useExpansionHack,
                    Optional.empty(),
                    this.maxDistanceFromCenter,
                    poolAliasLookup,
                    this.dimensionPadding,
                    this.liquidSettings
            );
        }

        return Optional.empty();
    }

    private static BlockPos findValidCavernStart(GenerationContext context, int x, int z) {
        LevelHeightAccessor heightAccessor = context.heightAccessor();

        int minY = Math.max(MIN_SCAN_Y, heightAccessor.getMinBuildHeight() + 1);
        int maxY = Math.min(MAX_SCAN_Y, heightAccessor.getMaxBuildHeight() - CLEAR_HEIGHT - 1);

        for (int y = maxY; y >= minY; y--) {
            if (isValidCavernStart(context, x, y, z)) {
                return new BlockPos(x, y, z);
            }
        }

        return null;
    }

    private static boolean isValidCavernStart(GenerationContext context, int centerX, int startY, int centerZ) {
        /*
         * Start position means:
         * - y - 1 is the floor
         * - y through y + CLEAR_HEIGHT is the open space the hut starts in
         */
        for (int dx = -CLEAR_RADIUS_XZ; dx <= CLEAR_RADIUS_XZ; dx++) {
            for (int dz = -CLEAR_RADIUS_XZ; dz <= CLEAR_RADIUS_XZ; dz++) {
                int x = centerX + dx;
                int z = centerZ + dz;

                NoiseColumn column = context.chunkGenerator().getBaseColumn(
                        x,
                        z,
                        context.heightAccessor(),
                        context.randomState()
                );

                BlockState floor = column.getBlock(startY - 1);

                if (!isAcceptableNetherFloor(floor)) {
                    return false;
                }

                for (int dy = 0; dy < CLEAR_HEIGHT; dy++) {
                    BlockState clearance = column.getBlock(startY + dy);

                    if (!isOpenCavernSpace(clearance)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private static boolean isAcceptableNetherFloor(BlockState state) {
        if (state.isAir()) {
            return false;
        }

        if (state.is(Blocks.BEDROCK)) {
            return false;
        }

        if (state.is(Blocks.LAVA)) {
            return false;
        }

        if (state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE)) {
            return false;
        }

        return !state.getFluidState().isSource();
    }

    private static boolean isOpenCavernSpace(BlockState state) {
        if (state.is(Blocks.LAVA)) {
            return false;
        }

        if (!state.getFluidState().isEmpty()) {
            return false;
        }

        return state.isAir();
    }

    @Override
    public StructureType<?> type() {
        return ModStructureTypes.NETHER_CAVERN_JIGSAW.get();
    }
}