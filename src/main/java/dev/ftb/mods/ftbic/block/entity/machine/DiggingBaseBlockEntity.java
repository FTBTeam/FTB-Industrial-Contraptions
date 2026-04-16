package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import java.util.List;

public class DiggingBaseBlockEntity extends BasicMachineBlockEntity {
	public static final int INVALID_Y = Integer.MIN_VALUE;

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
	public long diggingMineTicks;
	public long diggingMoveTicks;

	public DiggingBaseBlockEntity(ElectricBlockInstance type, BlockPos pos, BlockState state) {
		super(type, pos, state);
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
		output.putByte("OffsetX", (byte) offsetX);
		output.putByte("OffsetZ", (byte) offsetZ);
		output.putByte("SizeX", (byte) sizeX);
		output.putByte("SizeZ", (byte) sizeZ);
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
		offsetX = input.getByteOr("OffsetX", (byte) 0);
		offsetZ = input.getByteOr("OffsetZ", (byte) 0);
		sizeX = Mth.clamp(input.getByteOr("SizeX", (byte) 0), 0, 64);
		sizeZ = Mth.clamp(input.getByteOr("SizeZ", (byte) 0), 0, 64);
		skippedBlocks = input.getShortOr("SkippedBlocks", (short) 0);
		boundaryHighlightTicks = input.getIntOr("BoundaryHighlight", 0);
		laserX = input.getFloatOr("LaserX", 0.5F);
		laserZ = input.getFloatOr("LaserZ", 0.5F);
		laserY = input.getIntOr("LaserY", Integer.MIN_VALUE);
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

		if (energy < energyUse) return;
		energy -= energyUse;
		active = true;

		int miningTicks = Math.max((int) (diggingMineTicks / progressSpeed), 1);
		int moveTicks = Math.max((int) (diggingMoveTicks / progressSpeed), 1);
		int totalTicks = miningTicks + moveTicks;

		if ((tick % totalTicks) == totalTicks - 1) {
			int interiorW = sizeX - 2;
			int interiorD = sizeZ - 2;
			if (interiorW <= 0 || interiorD <= 0) return;
			long area = (long) interiorW * (long) interiorD;
			long pos = (tick / totalTicks) % area;
			int row = (int) (pos / interiorW);
			int col = row % 2 == 0 ? (int) (pos % interiorW) : (interiorW - 1 - (int) (pos % interiorW));

			int mx = worldPosition.getX() + offsetX + 1 + col;
			int mz = worldPosition.getZ() + offsetZ + 1 + row;
			int my = findMinableY(mx, mz);

			if (my == INVALID_Y) {
				dev.ftb.mods.ftbic.FTBIC.LOGGER.info("DiggingBase.tick at {} skipping col=({},{}) row={} (no minable Y)", worldPosition, mx, mz, row);
				skippedBlocks++;
				if (skippedBlocks >= area * 2) {
					paused = true;
					skippedBlocks = 0;
				}
			} else {
				BlockPos miningPos = new BlockPos(mx, my, mz);
				BlockState state = level.getBlockState(miningPos);
				dev.ftb.mods.ftbic.FTBIC.LOGGER.info("DiggingBase.tick at {} mining {} at {} (col={} row={} of {}x{})", worldPosition, state.getBlock(), miningPos, col, row, interiorW, interiorD);
				skippedBlocks = 0;
				laserX = (float) (offsetX + 1 + col + 0.5);
				laserZ = (float) (offsetZ + 1 + row + 0.5);
				laserY = my;
				level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
				digBlock(state, miningPos);
			}
		}
		tick++;
		// Persist tick + energy so a mid-operation chunk unload doesn't lose progress.
		setChanged();
	}

	private static final net.minecraft.tags.TagKey<Block> RELOCATION_NOT_PERMITTED =
			net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK,
					net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "relocation_not_permitted"));

	public boolean isValidBlock(BlockState state, BlockPos pos) {
		return !state.is(RELOCATION_NOT_PERMITTED);
	}

	public void digBlock(BlockState state, BlockPos miningPos) {
		if (!(level instanceof ServerLevel server)) return;
		List<ItemStack> drops = Block.getDrops(state, server, miningPos, null);
		if (!canFitAllDrops(drops)) {
			paused = true;
			setChanged();
			return;
		}
		level.removeBlock(miningPos, false);
		for (ItemStack drop : drops) {
			addToOutputs(drop);
		}
		setChanged();
	}

	private boolean canFitAllDrops(List<ItemStack> drops) {
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

	private int findMinableY(int x, int z) {
		if (level == null) return INVALID_Y;
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, 0, z);
		int bottom = level.getMinY();
		for (int y = worldPosition.getY() - 1; y >= bottom; y--) {
			pos.setY(y);
			if (!level.isLoaded(pos)) continue;
			BlockState state = level.getBlockState(pos);
			if (state.isAir()) continue;
			if (state.getBlock() == Blocks.BEDROCK) continue;
			if (state.getBlock() == FTBICBlocks.EXFLUID.get()) continue;
			if (!isValidBlock(state, pos)) continue;
			return y;
		}
		return INVALID_Y;
	}

	public boolean hasAnchorLandmark() {
		if (level == null) return false;
		Direction back = getFacing(Direction.NORTH).getOpposite();
		BlockPos anchorPos = worldPosition.relative(back);
		return level.getBlockState(anchorPos).getBlock() == FTBICBlocks.LANDMARK.get();
	}

	public void resize() {
		if (level == null) return;
		Block landmark = FTBICBlocks.LANDMARK.get();
		int radius = 128;
		int qx = worldPosition.getX();
		int qy = worldPosition.getY();
		int qz = worldPosition.getZ();
		java.util.List<BlockPos> marks = new java.util.ArrayList<>();
		boolean useLandmarks = hasAnchorLandmark();
		if (useLandmarks) {
			BlockPos.MutableBlockPos mut = new BlockPos.MutableBlockPos();
			int yMin = Math.max(level.getMinY(), qy - 128);
			int yMax = Math.min(level.getMaxY() - 1, qy + 4);
			for (int dx = -radius; dx <= radius; dx++) {
				for (int dz = -radius; dz <= radius; dz++) {
					if (dx == 0 && dz == 0) continue;
					for (int y = yMin; y <= yMax; y++) {
						mut.set(qx + dx, y, qz + dz);
						if (level.getBlockState(mut).getBlock() == landmark) {
							marks.add(mut.immutable());
							dev.ftb.mods.ftbic.FTBIC.LOGGER.info("  resize scan -> FOUND landmark at {}", mut);
						}
					}
				}
			}
		} else {
			dev.ftb.mods.ftbic.FTBIC.LOGGER.info("  resize: no anchor landmark behind quarry, using facing default");
		}

		int x0, x1, z0, z1;
		boolean defaultArea = marks.isEmpty() || marks.size() == 1;
		if (defaultArea) {
			Direction back = getFacing(Direction.NORTH).getOpposite();
			int ox = back.getStepX();
			int oz = back.getStepZ();
			if (back.getAxis() == Direction.Axis.X) {
				x0 = qx + (ox == 1 ? 1 : -5);
				x1 = qx + (ox == 1 ? 5 : -1);
				z0 = qz - 2; z1 = qz + 2;
			} else {
				x0 = qx - 2; x1 = qx + 2;
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

		dev.ftb.mods.ftbic.FTBIC.LOGGER.info(
				"DiggingBase.resize {} at {} side={} -> {} landmarks found | offset=({},{}) size={}x{} -> rect X[{}..{}] Z[{}..{}]",
				net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(getBlockState().getBlock()),
				worldPosition,
				level.isClientSide() ? "CLIENT" : "SERVER",
				marks.size(),
				offsetX, offsetZ, sizeX, sizeZ,
				qx + offsetX, qx + offsetX + sizeX - 1,
				qz + offsetZ, qz + offsetZ + sizeZ - 1);
		laserX = offsetX + sizeX / 2.0F;
		laserZ = offsetZ + sizeZ / 2.0F;
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
	}

	@Override
	public net.minecraft.world.InteractionResult rightClick(net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand, net.minecraft.world.phys.BlockHitResult hit) {
		if (player.isSecondaryUseActive() && player.getItemInHand(hand).isEmpty()) {
			if (level != null && !level.isClientSide()) {
				resize();
				player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
						String.format("Area: %d × %d (corner %d,%d..%d,%d)",
								sizeX, sizeZ,
								worldPosition.getX() + offsetX, worldPosition.getZ() + offsetZ,
								worldPosition.getX() + offsetX + sizeX - 1, worldPosition.getZ() + offsetZ + sizeZ - 1)));
			}
			return net.minecraft.world.InteractionResult.SUCCESS;
		}
		return super.rightClick(player, hand, hit);
	}

	@Override
	public void onPlacedBy(net.minecraft.world.entity.@org.jetbrains.annotations.Nullable LivingEntity entity, ItemStack stack) {
		super.onPlacedBy(entity, stack);
		dev.ftb.mods.ftbic.FTBIC.LOGGER.info("DiggingBase.onPlacedBy at {} side={} placer={} facing={}",
				worldPosition,
				level == null ? "null" : (level.isClientSide() ? "CLIENT" : "SERVER"),
				entity, getFacing(Direction.NORTH));
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
