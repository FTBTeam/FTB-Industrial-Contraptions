package dev.ftb.mods.ftbic.block.entity.machine;

import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.net.MoveLaserPayload;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class DiggingBaseBlockEntity extends BasicMachineBlockEntity {
    public static final int INVALID_Y = Integer.MIN_VALUE;
    public static final int LANDMARK_SEARCH_RADIUS = 128;
    public static final int MAX_AREA_SIZE = LANDMARK_SEARCH_RADIUS * 2 + 1;
    private static final int LANDMARK_SEARCH_DEPTH = 128;
    private static final int LANDMARK_SEARCH_HEIGHT = 4;

    private static final WeakHashMap<Level, Set<DiggingBaseBlockEntity>> PER_LEVEL = new WeakHashMap<>();

    public static Iterable<DiggingBaseBlockEntity> forLevel(Level level) {
        synchronized (PER_LEVEL) {
            Set<DiggingBaseBlockEntity> set = PER_LEVEL.get(level);
            return set == null ? List.of() : new ArrayList<>(set);
        }
    }

    private static void register(DiggingBaseBlockEntity be) {
        if (be.level == null) return;
        synchronized (PER_LEVEL) {
            PER_LEVEL
                    .computeIfAbsent(be.level, k -> Collections.newSetFromMap(new ConcurrentHashMap<>()))
                    .add(be);
        }
    }

    private static void unregister(DiggingBaseBlockEntity be) {
        synchronized (PER_LEVEL) {
            Set<DiggingBaseBlockEntity> set = PER_LEVEL.get(be.level);
            if (set != null) set.remove(be);
        }
    }

    public boolean paused = false;
    public boolean redstonePaused = false;
    public long tick = 0L;
    public int boundaryHighlightTicks = 0;
    public float laserX = 0.5F;
    public float laserZ = 0.5F;
    public int laserY = Integer.MIN_VALUE;
    public int offsetX = 0;
    public int offsetZ = 0;
    public int sizeX = 0;
    public int sizeZ = 0;
    public int skippedBlocks = 0;
    public int diggingMineTicks;
    public int diggingMoveTicks;

    public DiggingBaseBlockEntity(ElectricBlockInstance type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (!level.isClientSide()) register(this);
    }

    @Override
    public void setRemoved() {
        unregister(this);
        super.setRemoved();
    }

    @Override
    public void initProperties() {
        super.initProperties();
        diggingMineTicks = FTBICConfig.MACHINES.QUARRY_MINE_TICKS.get();
        diggingMoveTicks = FTBICConfig.MACHINES.QUARRY_MOVE_TICKS.get();
    }

    @Override
    public boolean savePlacer() {
        return true;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("Paused", paused);
        output.putLong("Tick", tick);
        output.putInt("OffsetX", offsetX);
        output.putInt("OffsetZ", offsetZ);
        output.putInt("SizeX", sizeX);
        output.putInt("SizeZ", sizeZ);
        if (skippedBlocks > 0) output.putShort("SkippedBlocks", (short) skippedBlocks);
        if (boundaryHighlightTicks > 0) output.putInt("BoundaryHighlight", boundaryHighlightTicks);
        output.putFloat("LaserX", laserX);
        output.putFloat("LaserZ", laserZ);
        if (laserY != Integer.MIN_VALUE) output.putInt("LaserY", laserY);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        paused = input.getBooleanOr("Paused", false);
        tick = input.getLongOr("Tick", 0L);
        offsetX = Mth.clamp(input.getIntOr("OffsetX", 0), -LANDMARK_SEARCH_RADIUS, LANDMARK_SEARCH_RADIUS);
        offsetZ = Mth.clamp(input.getIntOr("OffsetZ", 0), -LANDMARK_SEARCH_RADIUS, LANDMARK_SEARCH_RADIUS);
        sizeX = readAreaSize(input, "SizeX");
        sizeZ = readAreaSize(input, "SizeZ");
        skippedBlocks = input.getShortOr("SkippedBlocks", (short) 0);
        boundaryHighlightTicks = input.getIntOr("BoundaryHighlight", 0);
        laserX = input.getFloatOr("LaserX", 0.5F);
        laserZ = input.getFloatOr("LaserZ", 0.5F);
        laserY = input.getIntOr("LaserY", Integer.MIN_VALUE);
    }

    private static int readAreaSize(ValueInput input, String key) {
        int size = input.getIntOr(key, 0);
        return Mth.clamp(size < 0 ? Byte.toUnsignedInt((byte) size) : size, 0, MAX_AREA_SIZE);
    }

    public boolean isEffectivelyPaused() {
        return paused || redstonePaused;
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide()) {
            return;
        }
        boolean signal = level.hasNeighborSignal(worldPosition);
        if (signal != redstonePaused) {
            redstonePaused = signal;
            setChanged();
        }
        if (boundaryHighlightTicks > 0) {
            boundaryHighlightTicks--;
            if (boundaryHighlightTicks == 0) setChanged();
        }
        if (paused || redstonePaused) {
            return;
        }

        int interiorW = sizeX - 2;
        int interiorD = sizeZ - 2;
        if (interiorW <= 0 || interiorD <= 0) {
            return;
        }
        long area = (long) interiorW * interiorD;

        int miningTicks = Math.max((int) (diggingMineTicks / progressSpeed), 1);
        int moveTicks = Math.max((int) (diggingMoveTicks / progressSpeed), 1);
        int totalTicks = miningTicks + moveTicks;
        if (skipEmptyTargetsWithoutEnergy() && tick % totalTicks == 0) {
            int scanned = 0;
            boolean found = false;
            while (scanned++ < 32) {
                long column = tick / totalTicks % area;
                int row = (int) (column / interiorW);
                int col = row % 2 == 0 ? (int) (column % interiorW) : interiorW - 1 - (int) (column % interiorW);
                int x = worldPosition.getX() + offsetX + 1 + col;
                int z = worldPosition.getZ() + offsetZ + 1 + row;
                if (findMinableY(x, z) != INVALID_Y) {
                    skippedBlocks = 0;
                    found = true;
                    break;
                }
                tick += totalTicks;
                if (++skippedBlocks >= area) {
                    paused = true;
                    skippedBlocks = 0;
                    break;
                }
            }
            setChanged();
            if (!found) return;
        }
        if (energy < energyUse) return;
        energy -= energyUse;
        active = true;

        if ((tick % totalTicks) == totalTicks - 1) {
            long pos = (tick / totalTicks) % area;
            int row = (int) (pos / interiorW);
            int col = row % 2 == 0 ? (int) (pos % interiorW) : (interiorW - 1 - (int) (pos % interiorW));

            int mx = worldPosition.getX() + offsetX + 1 + col;
            int mz = worldPosition.getZ() + offsetZ + 1 + row;
            int my = findMinableY(mx, mz);

            if (my == INVALID_Y) {
                skippedBlocks++;
                if (skippedBlocks >= area * 2) {
                    paused = true;
                    skippedBlocks = 0;
                }
            } else {
                BlockPos miningPos = new BlockPos(mx, my, mz);
                BlockState state = level.getBlockState(miningPos);
                skippedBlocks = 0;
                laserX = (float) (offsetX + 1 + col + 0.5);
                laserZ = (float) (offsetZ + 1 + row + 0.5);
                laserY = my;
                if (level instanceof ServerLevel server) {
                    PacketDistributor.sendToPlayersTrackingChunk(
                            server,
                            ChunkPos.containing(worldPosition),
                            new MoveLaserPayload(worldPosition, laserX, laserY, laserZ));
                }
                digBlock(state, miningPos);
            }
        }
        tick++;
        // Persist tick + energy so a mid-operation chunk unload doesn't lose progress.
        setChanged();
    }

    public boolean isValidBlock(BlockState state, BlockPos pos) {
        return !state.is(Tags.Blocks.RELOCATION_NOT_SUPPORTED);
    }

    public void digBlock(BlockState state, BlockPos miningPos) {
        if (!(level instanceof ServerLevel server)) return;
        List<ItemStack> drops = Block.getDrops(state, server, miningPos, null);
        if (!canFitAllDrops(drops)) {
            paused = true;
            setChanged();
            return;
        }
        clearMinedBlock(miningPos, state);
        for (ItemStack drop : drops) {
            addToOutputs(drop);
        }
        setChanged();
    }

    protected void clearMinedBlock(BlockPos pos, BlockState state) {
        if (state.getFluidState().isEmpty()) {
            level.removeBlock(pos, false);
        } else {
            level.setBlock(pos, fluidReplacement(), 3);
        }
    }

    protected BlockState fluidReplacement() {
        return replaceFluidWithExfluid()
                ? FTBICBlocks.EXFLUID.get().defaultBlockState()
                : Blocks.AIR.defaultBlockState();
    }

    protected boolean replaceFluidWithExfluid() {
        return FTBICConfig.MACHINES.QUARRY_REPLACE_FLUID_EXFLUID.get();
    }

    protected FakePlayer getFakePlayer(ServerLevel server) {
        if (placerId.equals(Util.NIL_UUID)) {
            return FakePlayerFactory.getMinecraft(server);
        }
        return FakePlayerFactory.get(server, new GameProfile(placerId, placerName));
    }

    protected boolean canBreak(ServerLevel server, BlockPos pos, BlockState state) {
        return !CommonHooks.fireBlockBreak(server, GameType.SURVIVAL, getFakePlayer(server), pos, state)
                .isCanceled();
    }

    protected boolean canFitAllDrops(List<ItemStack> drops) {
        if (drops.isEmpty()) return true;
        if (outputItems.length == 0) return false;
        ItemStack[] sim = new ItemStack[outputItems.length];
        for (int i = 0; i < outputItems.length; i++) sim[i] = outputItems[i].copy();
        for (ItemStack drop : drops) {
            ItemStack remaining = drop.copy();
            for (int i = 0; i < sim.length && !remaining.isEmpty(); i++) {
                if (sim[i].isEmpty()) {
                    sim[i] = remaining;
                    remaining = ItemStack.EMPTY;
                } else if (ItemStack.isSameItemSameComponents(sim[i], remaining)
                        && sim[i].getCount() < sim[i].getMaxStackSize()) {
                    int move = Math.min(remaining.getCount(), sim[i].getMaxStackSize() - sim[i].getCount());
                    sim[i].grow(move);
                    remaining.shrink(move);
                }
            }
            if (!remaining.isEmpty()) return false;
        }
        return true;
    }

    protected ItemStack addToOutputs(ItemStack stack) {
        if (stack.isEmpty() || outputItems.length == 0) return stack;
        for (int i = 0; i < outputItems.length && !stack.isEmpty(); i++) {
            if (outputItems[i].isEmpty()) {
                outputItems[i] = stack;
                return ItemStack.EMPTY;
            }
            if (ItemStack.isSameItemSameComponents(outputItems[i], stack)
                    && outputItems[i].getCount() < outputItems[i].getMaxStackSize()) {
                int move = Math.min(stack.getCount(), outputItems[i].getMaxStackSize() - outputItems[i].getCount());
                outputItems[i].grow(move);
                stack.shrink(move);
            }
        }
        return stack;
    }

    protected boolean skipEmptyTargetsWithoutEnergy() {
        return false;
    }

    private int findMinableY(int x, int z) {
        if (!(level instanceof ServerLevel server)) return INVALID_Y;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, 0, z);
        int bottom = level.getMinY();
        for (int y = worldPosition.getY() - 1; y >= bottom; y--) {
            pos.setY(y);
            if (!level.isLoaded(pos)) continue;
            BlockState state = level.getBlockState(pos);
            if (state.isAir()) continue;
            if (state.getBlock() == Blocks.BEDROCK) continue;
            if (state.getBlock() == FTBICBlocks.EXFLUID.get()) continue;
            if (state.getDestroySpeed(level, pos) < 0F) continue;
            if (!isValidBlock(state, pos)) continue;
            return canBreak(server, pos.immutable(), state) ? y : INVALID_Y;
        }
        return INVALID_Y;
    }

    public boolean hasAnchorLandmark() {
        if (level == null) return false;
        Direction back = getFacing(Direction.NORTH).getOpposite();
        BlockPos anchorPos = worldPosition.relative(back);
        return level.isLoaded(anchorPos) && level.getBlockState(anchorPos).getBlock() == FTBICBlocks.LANDMARK.get();
    }

    private List<BlockPos> findLandmarks() {
        List<BlockPos> marks = new ArrayList<>();
        Block landmark = FTBICBlocks.LANDMARK.get();
        int qx = worldPosition.getX();
        int qy = worldPosition.getY();
        int qz = worldPosition.getZ();
        int minX = qx - LANDMARK_SEARCH_RADIUS;
        int maxX = qx + LANDMARK_SEARCH_RADIUS;
        int minZ = qz - LANDMARK_SEARCH_RADIUS;
        int maxZ = qz + LANDMARK_SEARCH_RADIUS;
        int minY = Math.max(level.getMinY(), qy - LANDMARK_SEARCH_DEPTH);
        int maxY = Math.min(level.getMaxY(), qy + LANDMARK_SEARCH_HEIGHT);
        if (minY > maxY) return marks;

        for (int cx = SectionPos.blockToSectionCoord(minX); cx <= SectionPos.blockToSectionCoord(maxX); cx++) {
            for (int cz = SectionPos.blockToSectionCoord(minZ); cz <= SectionPos.blockToSectionCoord(maxZ); cz++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(cx, cz);
                if (chunk == null) continue;
                int x0 = Math.max(minX, SectionPos.sectionToBlockCoord(cx));
                int x1 = Math.min(maxX, SectionPos.sectionToBlockCoord(cx, 15));
                int z0 = Math.max(minZ, SectionPos.sectionToBlockCoord(cz));
                int z1 = Math.min(maxZ, SectionPos.sectionToBlockCoord(cz, 15));
                for (int sy = SectionPos.blockToSectionCoord(minY); sy <= SectionPos.blockToSectionCoord(maxY); sy++) {
                    LevelChunkSection section = chunk.getSection(chunk.getSectionIndexFromSectionY(sy));
                    if (!section.maybeHas(state -> state.is(landmark))) continue;
                    int y0 = Math.max(minY, SectionPos.sectionToBlockCoord(sy));
                    int y1 = Math.min(maxY, SectionPos.sectionToBlockCoord(sy, 15));
                    for (int y = y0; y <= y1; y++) {
                        for (int x = x0; x <= x1; x++) {
                            for (int z = z0; z <= z1; z++) {
                                if (x == qx && z == qz) continue;
                                if (section.getBlockState(x & 15, y & 15, z & 15)
                                        .is(landmark)) {
                                    marks.add(new BlockPos(x, y, z));
                                }
                            }
                        }
                    }
                }
            }
        }
        return marks;
    }

    public void resize() {
        if (level == null) return;
        int qx = worldPosition.getX();
        int qz = worldPosition.getZ();
        List<BlockPos> marks = hasAnchorLandmark() ? findLandmarks() : List.of();

        int x0, x1, z0, z1;
        boolean defaultArea = marks.size() <= 1;
        if (defaultArea) {
            Direction back = getFacing(Direction.NORTH).getOpposite();
            int ox = back.getStepX();
            int oz = back.getStepZ();
            if (back.getAxis() == Direction.Axis.X) {
                x0 = qx + (ox == 1 ? 1 : -5);
                x1 = qx + (ox == 1 ? 5 : -1);
                z0 = qz - 2;
                z1 = qz + 2;
            } else {
                x0 = qx - 2;
                x1 = qx + 2;
                z0 = qz + (oz == 1 ? 1 : -5);
                z1 = qz + (oz == 1 ? 5 : -1);
            }
        } else {
            int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
            int minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;
            for (BlockPos l : marks) {
                minX = Math.min(minX, l.getX());
                maxX = Math.max(maxX, l.getX());
                minZ = Math.min(minZ, l.getZ());
                maxZ = Math.max(maxZ, l.getZ());
            }
            x0 = minX;
            x1 = maxX;
            z0 = minZ;
            z1 = maxZ;
        }

        offsetX = x0 - qx;
        offsetZ = z0 - qz;
        sizeX = Math.max(1, x1 - x0 + 1);
        sizeZ = Math.max(1, z1 - z0 + 1);

        boundaryHighlightTicks = 60;

        laserX = offsetX + sizeX / 2.0F;
        laserZ = offsetZ + sizeZ / 2.0F;
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
        if (player.isSecondaryUseActive() && player.getItemInHand(hand).isEmpty()) {
            if (level != null && !level.isClientSide()) {
                resize();
                player.sendSystemMessage(Component.literal(String.format(
                        "Area: %d × %d (corner %d,%d..%d,%d)",
                        sizeX,
                        sizeZ,
                        worldPosition.getX() + offsetX,
                        worldPosition.getZ() + offsetZ,
                        worldPosition.getX() + offsetX + sizeX - 1,
                        worldPosition.getZ() + offsetZ + sizeZ - 1)));
            }
            return InteractionResult.SUCCESS;
        }
        return super.rightClick(player, hand, hit);
    }

    @Override
    public void onPlacedBy(@Nullable LivingEntity entity, ItemStack stack) {
        super.onPlacedBy(entity, stack);
        if (level != null && !level.isClientSide()) {
            if (hasAnchorLandmark()) {
                resize();
            } else {
                resetToFacingDefault();
            }
        }
    }

    private void resetToFacingDefault() {
        Direction back = getFacing(Direction.NORTH).getOpposite();
        int ox = back.getStepX();
        int oz = back.getStepZ();
        if (back.getAxis() == Direction.Axis.X) {
            offsetX = ox == 1 ? 1 : -5;
            offsetZ = -2;
            sizeX = 5;
            sizeZ = 5;
        } else {
            offsetX = -2;
            offsetZ = oz == 1 ? 1 : -5;
            sizeX = 5;
            sizeZ = 5;
        }
        boundaryHighlightTicks = 60;
        laserX = offsetX + sizeX / 2.0F;
        laserZ = offsetZ + sizeZ / 2.0F;
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
}
