package dev.ftb.mods.ftbic.util;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public final class CachedEnergyStorage {
	public static final CachedEnergyStorage[] EMPTY = new CachedEnergyStorage[0];

	public CachedEnergyStorageOrigin origin;
	public int distance;
	public BlockEntity blockEntity;
	public ZapEnergyHandler energyHandler;
	public BlockCapabilityCache<EnergyHandler, Direction> feHandlerCache;

	@Override
	public int hashCode() {
		return blockEntity.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o == this || o instanceof CachedEnergyStorage e && blockEntity == e.blockEntity;
	}

	public boolean isInvalid() {
		if (origin.cableBurnt) {
			return true;
		}
		if (feHandlerCache != null) {
			return blockEntity == null || blockEntity.isRemoved();
		}
		return energyHandler.isEnergyHandlerInvalid();
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
		if (feHandlerCache != null) {
			var fe = feHandlerCache.getCapability();
			return fe != null && fe.getAmountAsLong() < fe.getCapacityAsLong();
		}
		return energyHandler.getEnergy() < energyHandler.getEnergyCapacity();
	}

	public double insertZaps(double zaps) {
		if (zaps <= 0D) {
			return 0D;
		}
		if (feHandlerCache != null) {
			var fe = feHandlerCache.getCapability();
			if (fe == null) {
				return 0D;
			}
			int feOffer = ZapFEConversion.zapsToFEFloor(zaps);
			if (feOffer <= 0) {
				return 0D;
			}
			try (Transaction tx = Transaction.openRoot()) {
				int feAccepted = fe.insert(feOffer, tx);
				if (feAccepted > 0) {
					tx.commit();
					return Math.min(ZapFEConversion.feToZapsCeil(feAccepted), zaps);
				}
			}
			return 0D;
		}
		return energyHandler.insertEnergy(zaps, false);
	}
}
