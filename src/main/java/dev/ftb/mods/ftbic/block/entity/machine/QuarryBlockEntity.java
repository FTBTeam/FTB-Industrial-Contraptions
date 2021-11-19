package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class QuarryBlockEntity extends ElectricBlockEntity {
	public BlockPos laserPos = BlockPos.ZERO;

	public QuarryBlockEntity() {
		super(FTBICElectricBlocks.QUARRY);
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);
		tag.putInt("LaserX", laserPos.getX());
		tag.putInt("LaserY", laserPos.getY());
		tag.putInt("LaserZ", laserPos.getZ());
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);
		laserPos = new BlockPos(tag.getInt("LaserX"), tag.getInt("LaserY"), tag.getInt("LaserZ"));
	}

	@Override
	public void writeNetData(CompoundTag tag) {
		super.writeNetData(tag);
		tag.putInt("LaserX", laserPos.getX());
		tag.putInt("LaserY", laserPos.getY());
		tag.putInt("LaserZ", laserPos.getZ());
	}

	@Override
	public void readNetData(CompoundTag tag) {
		super.readNetData(tag);
		laserPos = new BlockPos(tag.getInt("LaserX"), tag.getInt("LaserY"), tag.getInt("LaserZ"));
	}

	@Override
	public void tick() {
		active = true;

		super.tick();

		if (level != null && level.isClientSide()) {
			double x = worldPosition.getX() + laserPos.getX() + 0.5D;
			double y = worldPosition.getY() + laserPos.getY() + 0.5D;
			double z = worldPosition.getZ() + laserPos.getZ() + 0.5D;
			FTBIC.PROXY.playLaserSound(level.getGameTime(), getBlockState(), x, y, z);
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
}