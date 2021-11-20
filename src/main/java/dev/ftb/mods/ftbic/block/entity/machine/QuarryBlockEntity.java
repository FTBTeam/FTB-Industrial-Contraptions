package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

			int moveTicks = (int) (FTBICConfig.QUARRY_MOVE_TICKS * 0.25D);
			int totalTicks = (int) ((FTBICConfig.QUARRY_MINE_TICKS * 0.25D) + moveTicks);
			int ltick = (int) (miningTick % (long) totalTicks);

			if (ltick == moveTicks) {
				long s = (long) sizeX * (long) sizeZ * 2L;
				int lpos = (int) ((miningTick / totalTicks) % s);

				if (lpos >= s / 2L) {
					lpos = (int) (s - lpos - 1);
				}

				int row = lpos / sizeX;
				int col = row % 2 == 0 ? (lpos % sizeX) : (sizeX - 1 - (lpos % sizeX));
				laserX = (offsetX + col) + 0.5D;
				laserZ = (offsetZ + row) + 0.5D;
				laserY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, worldPosition.getX() + Mth.floor(laserX), worldPosition.getZ() + Mth.floor(laserZ)) - worldPosition.getY() - 1;
			}

			if (ltick == totalTicks - 1 && !level.isClientSide() && worldPosition.getY() + laserY > 0) {
				BlockPos miningPos = worldPosition.offset(laserX, laserY, laserZ);

				if (level.getBlockState(miningPos).getBlock() != Blocks.BEDROCK) {
					level.removeBlock(miningPos, false);
				}
			}

			miningTick++;

			if (level != null && level.isClientSide()) {
				double x = worldPosition.getX() + laserX;
				double y = worldPosition.getY() + laserY;
				double z = worldPosition.getZ() + laserZ;
				FTBIC.PROXY.playLaserSound(level.getGameTime(), x, y, z);
			}
		}
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
}