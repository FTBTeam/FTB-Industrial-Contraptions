package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class QuarryBlockEntity extends ElectricBlockEntity {
	public double laserX = 0.5D;
	public int laserY = 0;
	public double laserZ = 0.5D;
	public double prevLaserX = 0.5D;
	public double prevLaserZ = 0.5D;
	public boolean paused = true;
	public long miningTick = 0L;
	public int offsetX = 1;
	public int offsetZ = -2;
	public int sizeX = 5;
	public int sizeZ = 5;

	public QuarryBlockEntity() {
		super(FTBICElectricBlocks.QUARRY);
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);
		tag.putDouble("LaserX", laserX);
		tag.putInt("LaserY", laserY);
		tag.putDouble("LaserZ", laserZ);

		if (paused) {
			tag.putBoolean("Paused", true);
		}

		tag.putLong("MiningTick", miningTick);
		tag.putByte("OffsetX", (byte) offsetX);
		tag.putByte("OffsetZ", (byte) offsetZ);
		tag.putByte("SizeX", (byte) sizeX);
		tag.putByte("SizeZ", (byte) sizeZ);
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);
		laserX = tag.getDouble("LaserX");
		laserY = tag.getInt("LaserY");
		laserZ = tag.getDouble("LaserZ");
		paused = tag.getBoolean("Paused");
		miningTick = tag.getLong("MiningTick");
		offsetX = tag.getByte("OffsetX");
		offsetZ = tag.getByte("OffsetZ");
		sizeX = Math.max(tag.getByte("SizeX"), 1);
		sizeZ = Math.max(tag.getByte("SizeZ"), 1);
	}

	@Override
	public void writeNetData(CompoundTag tag) {
		super.writeNetData(tag);
		tag.putDouble("LaserX", laserX);
		tag.putInt("LaserY", laserY);
		tag.putDouble("LaserZ", laserZ);

		if (paused) {
			tag.putBoolean("Paused", true);
		}

		tag.putLong("MiningTick", miningTick);
		tag.putByte("OffsetX", (byte) offsetX);
		tag.putByte("OffsetZ", (byte) offsetZ);
		tag.putByte("SizeX", (byte) sizeX);
		tag.putByte("SizeZ", (byte) sizeZ);
	}

	@Override
	public void readNetData(CompoundTag tag) {
		super.readNetData(tag);
		laserX = tag.getDouble("LaserX");
		laserY = tag.getInt("LaserY");
		laserZ = tag.getDouble("LaserZ");
		paused = tag.getBoolean("Paused");
		miningTick = tag.getLong("MiningTick");
		offsetX = tag.getByte("OffsetX");
		offsetZ = tag.getByte("OffsetZ");
		sizeX = tag.getByte("SizeX");
		sizeZ = tag.getByte("SizeZ");
	}

	@Override
	public void tick() {
		active = !paused;
		prevLaserX = laserX;
		prevLaserZ = laserZ;

		super.tick();

		if (!paused && level != null) {
			Direction direction = getFacing(Direction.NORTH);

			int moveTicks = Math.max((int) (FTBICConfig.QUARRY_MOVE_TICKS * 0.5), 1);
			int totalTicks = Math.max((int) (FTBICConfig.QUARRY_MINE_TICKS * 0.15), 1) + moveTicks;
			int ltick = (int) (miningTick % (long) totalTicks);

			if (ltick <= moveTicks) {
				long s = (long) sizeX * (long) sizeZ * 2L;
				int lpos0 = (int) (((miningTick / totalTicks) - 1L) % s);
				int lpos1 = (int) ((miningTick / totalTicks) % s);

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

				laserX = (offsetX + Mth.lerp(lerp, col0, col1)) + 0.5D;
				laserZ = (offsetZ + Mth.lerp(lerp, row0, row1)) + 0.5D;
				laserY = getY(worldPosition.getX() + Mth.floor(laserX), worldPosition.getZ() + Mth.floor(laserZ)) - worldPosition.getY();
			} else if (ltick == totalTicks - 1 && !level.isClientSide() && worldPosition.getY() + laserY > 0) {
				BlockPos miningPos = worldPosition.offset(laserX, laserY, laserZ);

				if (level.getBlockState(miningPos).getBlock() != Blocks.BEDROCK) {
					level.removeBlock(miningPos, false);
				}
			}

			// TODO: Pause quarry if all blocks from previous loop have been bedrock

			miningTick++;

			if (level != null && level.isClientSide()) {
				double x = worldPosition.getX() + laserX;
				double y = worldPosition.getY() + laserY;
				double z = worldPosition.getZ() + laserZ;
				FTBIC.PROXY.playLaserSound(level.getGameTime(), x, y, z);
			}
		}
	}

	private int getY(int x, int z) {
		return level.getHeight(Heightmap.Types.OCEAN_FLOOR, x, z) - 1;
	}

	@Override
	public boolean tickClientSide() {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public AABB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public double getViewDistance() {
		return 256D;
	}

	@Override
	public boolean savePlacer() {
		return true;
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide()) {
			paused = !paused;
			syncBlock();
		}

		return InteractionResult.SUCCESS;
	}

	public void resize() {
		Block landmark = FTBICBlocks.LANDMARK.get();
		Direction front = getFacing(Direction.NORTH);
		Direction back = front.getOpposite();
		Direction left = front.getClockWise();
		Direction right = front.getCounterClockWise();

		int offBack = 2;
		int offLeft = 1;
		int offRight = 1;

		for (int i = 2; i < 120; i++) {
			BlockState state = level.getBlockState(worldPosition.relative(back, i));

			if (state.getBlock() == landmark) {
				offBack = i;
				break;
			}
		}

		for (int i = 2; i < 60; i++) {
			BlockState state = level.getBlockState(worldPosition.relative(left, i));

			if (state.getBlock() == landmark) {
				offLeft = i;
				break;
			}
		}

		for (int i = 1; i < 60; i++) {
			BlockState state = level.getBlockState(worldPosition.relative(right, i));

			if (state.getBlock() == landmark) {
				offRight = i;
				break;
			}
		}

		syncBlock();
	}

	@Override
	public void onPlacedBy(@Nullable LivingEntity entity, ItemStack stack) {
		super.onPlacedBy(entity, stack);
		resize();
	}
}