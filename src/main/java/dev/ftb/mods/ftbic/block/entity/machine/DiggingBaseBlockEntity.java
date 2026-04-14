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

/**
 * Base for machines that dig through a 3D region at-or-below the machine position.
 * Each "work tick" (diggingMineTicks / progressSpeed) the machine mines one block at the scan head
 * and moves the head to the next column. Drops go into the output inventory — any overflow is popped
 * into the world.
 *
 * Phase 2 baseline: fixed 5×5 radius (offset -2..+2 in X and Z), no Landmark-bounded regions. The
 * reference tracked a laser beam for client rendering — that's deferred to Phase 4 client work.
 */
public class DiggingBaseBlockEntity extends BasicMachineBlockEntity {
	public static final int INVALID_Y = Integer.MIN_VALUE;

	public boolean paused = false;
	public long tick = 0L;
	public int offsetX = -2;
	public int offsetZ = -2;
	public int sizeX = 5;
	public int sizeZ = 5;
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
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		paused = input.getBooleanOr("Paused", false);
		tick = input.getLongOr("Tick", 0L);
		offsetX = input.getByteOr("OffsetX", (byte) -2);
		offsetZ = input.getByteOr("OffsetZ", (byte) -2);
		sizeX = Mth.clamp(input.getByteOr("SizeX", (byte) 5), 1, 64);
		sizeZ = Mth.clamp(input.getByteOr("SizeZ", (byte) 5), 1, 64);
		skippedBlocks = input.getShortOr("SkippedBlocks", (short) 0);
	}

	@Override
	public void tick() {
		super.tick();
		if (paused || level == null || level.isClientSide()) {
			return;
		}

		if (energy < energyUse) return;
		energy -= energyUse;
		active = true;

		int miningTicks = Math.max((int) (diggingMineTicks / progressSpeed), 1);
		int moveTicks = Math.max((int) (diggingMoveTicks / progressSpeed), 1);
		int totalTicks = miningTicks + moveTicks;

		if ((tick % totalTicks) == totalTicks - 1) {
			// Time to dig. Compute current column via lazy grid walk.
			long area = (long) sizeX * (long) sizeZ;
			long pos = (tick / totalTicks) % area;
			int row = (int) (pos / sizeX);
			int col = row % 2 == 0 ? (int) (pos % sizeX) : (sizeX - 1 - (int) (pos % sizeX));

			int mx = worldPosition.getX() + offsetX + col;
			int mz = worldPosition.getZ() + offsetZ + row;
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
				digBlock(state, miningPos);
			}
		}
		tick++;
		// Persist tick + energy so a mid-operation chunk unload doesn't lose progress.
		setChanged();
	}

	/** Subclasses override to filter what counts as mineable (default: everything). */
	public boolean isValidBlock(BlockState state, BlockPos pos) {
		return true;
	}

	/** Default implementation: mine the block, give it to the loot table, store drops in output slots. */
	public void digBlock(BlockState state, BlockPos miningPos) {
		if (!(level instanceof ServerLevel server)) return;
		List<ItemStack> drops = Block.getDrops(state, server, miningPos, null);
		level.removeBlock(miningPos, false);
		for (ItemStack drop : drops) {
			ItemStack leftover = addToOutputs(drop);
			if (!leftover.isEmpty()) {
				Block.popResource(level, worldPosition.above(), leftover);
			}
		}
		setChanged();
	}

	/** Adds a stack into the first output slot that can hold it; returns leftover. */
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
		for (int y = worldPosition.getY() - 1; y >= level.getMinY(); y--) {
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

	public void resize() {
		Block landmark = FTBICBlocks.LANDMARK.get();
		Direction front = getFacing(Direction.NORTH);
		Direction back = front.getOpposite();
		Direction left = front.getClockWise();
		Direction right = front.getCounterClockWise();

		int offBack = 6, offLeft = 3, offRight = 3;
		for (int i = 2; i <= 64; i++) {
			if (level.getBlockState(worldPosition.relative(back, i)).getBlock() == landmark) { offBack = i; break; }
		}
		for (int i = 1; i <= 64; i++) {
			if (level.getBlockState(worldPosition.relative(left, i)).getBlock() == landmark) { offLeft = i; break; }
		}
		for (int i = 1; i <= 64; i++) {
			if (level.getBlockState(worldPosition.relative(right, i)).getBlock() == landmark) { offRight = i; break; }
		}

		offBack -= 1; offLeft -= 1; offRight -= 1;

		if (back.getAxis() == Direction.Axis.X) {
			sizeX = offBack;
			sizeZ = (offLeft + offRight + 1);
			offsetX = back.getStepX() == 1 ? 1 : -sizeX;
			offsetZ = -(back.getStepX() == 1 ? offLeft : offRight);
		} else if (back.getAxis() == Direction.Axis.Z) {
			sizeX = (offLeft + offRight + 1);
			sizeZ = offBack;
			offsetX = -(back.getStepZ() == 1 ? offRight : offLeft);
			offsetZ = back.getStepZ() == 1 ? 1 : -sizeZ;
		}
		setChanged();
	}

	@Override
	public void onPlacedBy(net.minecraft.world.entity.@org.jetbrains.annotations.Nullable LivingEntity entity, ItemStack stack) {
		super.onPlacedBy(entity, stack);
		if (level != null && !level.isClientSide()) {
			resize();
		}
	}
}
