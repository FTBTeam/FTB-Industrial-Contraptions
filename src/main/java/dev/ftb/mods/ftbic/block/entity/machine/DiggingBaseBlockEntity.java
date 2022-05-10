package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.net.MoveLaserMessage;
import dev.ftb.mods.ftbic.screen.sync.SyncedData;
import dev.ftb.mods.ftbic.util.FTBChunksIntegration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class DiggingBaseBlockEntity extends BasicMachineBlockEntity {
	private static final int INVALID_Y = -10000;

	public boolean paused = false;
	public long tick = 0L;
	public float laserX = 0.5F;
	public int laserY = INVALID_Y;
	public float laserZ = 0.5F;
	public int offsetX = 1;
	public int offsetZ = -2;
	public int sizeX = 5;
	public int sizeZ = 5;
	public int skippedBlocks = 0;

	// Client side stuff
	public float prevLaserX = 0.5F;
	public float prevLaserZ = 0.5F;
	public float moveLaserX = 0.5F;
	public int moveLaserY = 0;
	public float moveLaserZ = 0.5F;

	public long diggingMineTicks;
	public long diggingMoveTicks;

	public DiggingBaseBlockEntity(ElectricBlockInstance type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);
		tag.putFloat("LaserX", laserX);
		tag.putInt("LaserY", laserY);
		tag.putFloat("LaserZ", laserZ);
		tag.putBoolean("Paused", paused);
		tag.putLong("Tick", tick);
		tag.putByte("OffsetX", (byte) offsetX);
		tag.putByte("OffsetZ", (byte) offsetZ);
		tag.putByte("SizeX", (byte) sizeX);
		tag.putByte("SizeZ", (byte) sizeZ);

		if (skippedBlocks > 0) {
			tag.putShort("SkippedBlocks", (short) skippedBlocks);
		}
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);
		laserX = tag.getFloat("LaserX");
		laserY = tag.getInt("LaserY");
		laserZ = tag.getFloat("LaserZ");
		paused = tag.getBoolean("Paused");
		tick = tag.getLong("Tick");
		offsetX = tag.getByte("OffsetX");
		offsetZ = tag.getByte("OffsetZ");
		sizeX = Mth.clamp(tag.getByte("SizeX"), 1, 64);
		sizeZ = Mth.clamp(tag.getByte("SizeZ"), 1, 64);
		skippedBlocks = tag.getShort("SkippedBlocks");
	}

	@Override
	public void writeNetData(CompoundTag tag) {
		super.writeNetData(tag);
		tag.putFloat("LaserX", laserX);
		tag.putInt("LaserY", laserY);
		tag.putFloat("LaserZ", laserZ);
		tag.putBoolean("Paused", paused);

		tag.putLong("Tick", tick);
		tag.putByte("OffsetX", (byte) offsetX);
		tag.putByte("OffsetZ", (byte) offsetZ);
		tag.putByte("SizeX", (byte) sizeX);
		tag.putByte("SizeZ", (byte) sizeZ);
		tag.putDouble("Speed", progressSpeed);
	}

	@Override
	public void readNetData(CompoundTag tag) {
		super.readNetData(tag);
		prevLaserX = moveLaserX = laserX = tag.getFloat("LaserX");
		moveLaserY = laserY = tag.getInt("LaserY");
		prevLaserZ = moveLaserZ = laserZ = tag.getFloat("LaserZ");
		paused = tag.getBoolean("Paused");
		tick = tag.getLong("Tick");
		offsetX = tag.getByte("OffsetX");
		offsetZ = tag.getByte("OffsetZ");
		sizeX = tag.getByte("SizeX");
		sizeZ = tag.getByte("SizeZ");
	}

	@Override
	public void handleProcessing() {
		active = !paused;

		if (level != null && level.isClientSide()) {
			prevLaserX = laserX;
			prevLaserZ = laserZ;

			laserX = moveLaserX;
			laserY = moveLaserY;
			laserZ = moveLaserZ;

			if (!paused) {
				double x = worldPosition.getX() + laserX;
				double minY = laserY + 0.5D;
				double maxY = worldPosition.getY() + 0.5D;
				double z = worldPosition.getZ() + laserZ;
				FTBIC.PROXY.playLaserSound(level.getGameTime(), x, minY, maxY, z);
			}
		}

		if (!paused && level != null && !level.isClientSide()) {
			if (energy >= energyUse) {
				energy -= energyUse;
			} else {
				return;
			}

			int miningTicks = Math.max((int) (diggingMineTicks / progressSpeed), 1);
			int moveTicks = Math.max((int) (diggingMoveTicks / progressSpeed), 1);
			int totalTicks = miningTicks + moveTicks;
			int ltick = (int) (tick % (long) totalTicks);

			if (ltick <= moveTicks) {
				long s = (long) sizeX * (long) sizeZ * 2L;
				int lpos0 = (int) (((tick / (long) totalTicks) - 1L) % s);
				int lpos1 = (int) ((tick / (long) totalTicks) % s);

				if (lpos0 < 0) {
					lpos0 += s;
				}

				if (lpos0 >= s / 2L) {
					lpos0 = (int) (s - lpos0 - 1);
				}

				if (lpos1 >= s / 2L) {
					lpos1 = (int) (s - lpos1 - 1);
				}

				int row0 = lpos0 / sizeX;
				int col0 = row0 % 2 == 0 ? (lpos0 % sizeX) : (sizeX - 1 - (lpos0 % sizeX));
				int row1 = lpos1 / sizeX;
				int col1 = row1 % 2 == 0 ? (lpos1 % sizeX) : (sizeX - 1 - (lpos1 % sizeX));
				float lerp = ltick / (float) moveTicks;

				laserX = (offsetX + Mth.lerp(lerp, col0, col1)) + 0.5F;
				laserZ = (offsetZ + Mth.lerp(lerp, row0, row1)) + 0.5F;
				laserY = getY(worldPosition.getX() + Mth.floor(laserX), worldPosition.getZ() + Mth.floor(laserZ));
				sendLaserMove();
			}

			if (ltick == totalTicks - 1) {
				laserY = getY(worldPosition.getX() + Mth.floor(laserX), worldPosition.getZ() + Mth.floor(laserZ));

				if (laserY == INVALID_Y) {
					skippedBlocks++;

					if (skippedBlocks >= sizeX * sizeZ * 2) {
						paused = true;
						skippedBlocks = 0;
						syncBlock();
					}
				} else {
					BlockPos miningPos = new BlockPos(worldPosition.getX() + laserX, laserY, worldPosition.getZ() + laserZ);
					BlockState state = level.getBlockState(miningPos);
					skippedBlocks = 0;

					if (!level.isClientSide()) {
						level.getProfiler().push("ftbic_" + electricBlockInstance.id);
						double lx = worldPosition.getX() + laserX;
						double ly = laserY + 0.5D;
						double lz = worldPosition.getZ() + laserZ;
						digBlock(state, miningPos, lx, ly, lz);
						level.getProfiler().pop();

						if (paused) {
							syncBlock();
						}
					}
				}
			}

			tick++;
		}
	}

	public boolean isValidBlock(BlockState state, BlockPos pos) {
		return false;
	}

	public void digBlock(BlockState state, BlockPos miningPos, double lx, double ly, double lz) {
	}

	private int getY(int x, int z) {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, 0, z);

		if (level instanceof ServerLevel && FTBChunksIntegration.instance.isProtected((ServerLevel) level, pos, placerId)) {
			return INVALID_Y;
		}

		for (int y = worldPosition.getY(); y >= level.getMinBuildHeight(); y--) {
			pos.setY(y);

			if (level.isLoaded(pos)) {
				BlockState state = level.getBlockState(pos);

				if (state.getBlock() != Blocks.BEDROCK && state.getBlock() != FTBICBlocks.EXFLUID.get() && !state.isAir() && isValidBlock(state, pos)) {
					return y;
				}
			}
		}

		return INVALID_Y;
	}

	@Override
	public AABB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public boolean savePlacer() {
		return true;
	}

	public void resize() {
		Block landmark = FTBICBlocks.LANDMARK.get();
		Direction front = getFacing(Direction.NORTH);
		Direction back = front.getOpposite();
		Direction left = front.getClockWise();
		Direction right = front.getCounterClockWise();

		int offBack = 6;
		int offLeft = 3;
		int offRight = 3;

		for (int i = 2; i <= 64; i++) {
			BlockState state = level.getBlockState(worldPosition.relative(back, i));

			if (state.getBlock() == landmark) {
				offBack = i;
				break;
			}
		}

		for (int i = 1; i <= 64; i++) {
			BlockState state = level.getBlockState(worldPosition.relative(left, i));

			if (state.getBlock() == landmark) {
				offLeft = i;
				break;
			}
		}

		for (int i = 1; i <= 64; i++) {
			BlockState state = level.getBlockState(worldPosition.relative(right, i));

			if (state.getBlock() == landmark) {
				offRight = i;
				break;
			}
		}

		offBack -= 1;
		offLeft -= 1;
		offRight -= 1;

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

		syncBlock();
	}

	@Override
	public void onPlacedBy(@Nullable LivingEntity entity, ItemStack stack) {
		super.onPlacedBy(entity, stack);
		Direction dir = getFacing(Direction.NORTH).getOpposite();
		laserX = dir.getStepX() + 0.5F;
		laserZ = dir.getStepZ() + 0.5F;

		BlockEntity e = level.getBlockEntity(worldPosition.below());

		if (e instanceof DiggingBaseBlockEntity) {
			DiggingBaseBlockEntity q = (DiggingBaseBlockEntity) e;
			offsetX = q.offsetX;
			offsetZ = q.offsetZ;
			sizeX = q.sizeX;
			sizeZ = q.sizeZ;
			syncBlock();
		} else {
			resize();
		}
	}

	@Override
	public void addSyncData(SyncedData data) {
		super.addSyncData(data);
		data.addBoolean(SyncedData.PAUSED, () -> paused);
	}

	public void moveLaser(float x, int y, float z) {
		moveLaserX = x;
		moveLaserY = y;
		moveLaserZ = z;
		// FTBIC.LOGGER.info(String.format("Moved laser %d, %d, %d to %f, %f, %f", worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), x, y, z));
	}

	private void sendLaserMove() {
		LevelChunk chunk = level == null ? null : level.getChunkAt(worldPosition);

		if (chunk != null) {
			new MoveLaserMessage(worldPosition, laserX, laserY, laserZ).sendToChunkListeners(chunk);
		}
	}

	public float[] getLaserColor() {
		return new float[]{1F, 1F, 1F};
	}
}