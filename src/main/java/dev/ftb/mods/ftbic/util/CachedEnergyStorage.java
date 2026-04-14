package dev.ftb.mods.ftbic.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class CachedEnergyStorage {
	public static final CachedEnergyStorage[] EMPTY = new CachedEnergyStorage[0];

	public CachedEnergyStorageOrigin origin;
	public int distance;
	public BlockEntity blockEntity;
	public EnergyHandler energyHandler;

	@Override
	public int hashCode() {
		return blockEntity.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o == this || o instanceof CachedEnergyStorage e && blockEntity == e.blockEntity;
	}

	public boolean isInvalid() {
		return origin.cableBurnt || energyHandler.isEnergyHandlerInvalid();
	}

	@Override
	public String toString() {
		return String.format("%s@%d,%d,%d",
				BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType()),
				blockEntity.getBlockPos().getX(),
				blockEntity.getBlockPos().getY(),
				blockEntity.getBlockPos().getZ());
	}

	public boolean shouldReceiveEnergy() {
		return energyHandler.getEnergy() < energyHandler.getEnergyCapacity();
	}
}
