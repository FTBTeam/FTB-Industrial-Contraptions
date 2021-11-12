package dev.ftb.mods.ftbic.util;

import org.jetbrains.annotations.Nullable;

public interface EnergyHandler {
	@Nullable
	default EnergyTier getInputEnergyTier() {
		return null;
	}

	double getEnergyCapacity();

	double getEnergy();

	default void setEnergy(double energy) {
		double prev = getEnergy();

		if (prev != energy) {
			setEnergyRaw(energy);
			energyChanged(prev);
		}
	}

	void setEnergyRaw(double energy);

	default double insertEnergy(double maxInsert, boolean simulate) {
		EnergyTier tier = getInputEnergyTier();

		if (tier == null) {
			return 0;
		}

		double energy = getEnergy();
		double energyReceived = Math.min(getEnergyCapacity() - energy, Math.min(tier.itemTransferRate, maxInsert));

		if (!simulate && energyReceived > 0D) {
			setEnergyRaw(energy + energyReceived);
			energyChanged(energy);
		}

		return energyReceived;
	}

	default void energyChanged(double prevEnergy) {
	}

	default void setBurnt(boolean burnt) {
	}

	default boolean isBurnt() {
		return false;
	}
}
