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
	public boolean laserOn = false;
	public long miningTick = 0L;

	public QuarryBlockEntity() {
		super(FTBICElectricBlocks.QUARRY);
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);
		tag.putDouble("LaserX", laserX);
		tag.putInt("LaserY", laserY);
		tag.putDouble("LaserZ", laserZ);
		tag.putBoolean("LaserOn", laserOn);
		tag.putLong("MiningTick", miningTick);
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);
		laserX = tag.getDouble("LaserX");
		laserY = tag.getInt("LaserY");
		laserZ = tag.getDouble("LaserZ");
		laserOn = tag.getBoolean("LaserOn");
		miningTick = tag.getLong("MiningTick");
	}

	@Override
	public void writeNetData(CompoundTag tag) {
		super.writeNetData(tag);
		tag.putDouble("LaserX", laserX);
		tag.putInt("LaserY", laserY);
		tag.putDouble("LaserZ", laserZ);

		if (laserOn) {
			tag.putBoolean("LaserOn", true);
		}

		tag.putLong("MiningTick", miningTick);
	}

	@Override
	public void readNetData(CompoundTag tag) {
		super.readNetData(tag);
		laserX = tag.getDouble("LaserX");
		laserY = tag.getInt("LaserY");
		laserZ = tag.getDouble("LaserZ");
		laserOn = tag.getBoolean("LaserOn");
		miningTick = tag.getLong("MiningTick");
	}

	@Override
	public void tick() {
		active = laserOn;
		prevLaserX = laserX;
		prevLaserZ = laserZ;

		super.tick();

		Direction direction = getFacing(Direction.NORTH);

		double pos = miningTick / Math.PI / 2D / (double) FTBICConfig.QUARRY_BASE_TICKS;
		laserX = direction.getStepX() * -7D + 0.5D + Math.cos(pos) * 5D;
		laserZ = direction.getStepZ() * -7D + 0.5D + Math.sin(pos) * 5D;

		laserY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, worldPosition.getX() + Mth.floor(laserX), worldPosition.getZ() + Mth.floor(laserZ)) - worldPosition.getY();

		if (!level.isClientSide() && miningTick % FTBICConfig.QUARRY_BASE_TICKS == 0L) {
			BlockPos miningPos = worldPosition.offset(laserX, laserY - 1, laserZ);

			if (level.getBlockState(miningPos).getBlock() != Blocks.BEDROCK) {
				level.removeBlock(miningPos, false);
			}
		}

		if (laserOn && level != null && level.isClientSide()) {
			double x = worldPosition.getX() + laserX;
			double y = worldPosition.getY() + laserY;
			double z = worldPosition.getZ() + laserZ;
			FTBIC.PROXY.playLaserSound(level.getGameTime(), x, y, z);
		}

		miningTick++;
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
}