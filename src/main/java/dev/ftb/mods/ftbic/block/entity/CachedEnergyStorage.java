package dev.ftb.mods.ftbic.block.entity;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.IEnergyStorage;

public class CachedEnergyStorage {
	public static final CachedEnergyStorage[] EMPTY = new CachedEnergyStorage[0];

	public double distance;
	public BlockEntity blockEntity;
	public IEnergyStorage energyStorage;

	public int hashCode() {
		return blockEntity.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o == this || o instanceof CachedEnergyStorage && blockEntity == ((CachedEnergyStorage) o).blockEntity;
	}

	public boolean isInvalid() {
		return blockEntity.isRemoved();
	}

	@Override
	public String toString() {
		return String.format("%s@%d,%d,%d", blockEntity.getType().getRegistryName(), blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ());
	}

	public boolean shouldReceiveEnergy() {
		return energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored();
	}
}
