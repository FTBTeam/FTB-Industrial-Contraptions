package dev.ftb.mods.ftbic.util;

import org.jetbrains.annotations.Nullable;

public interface EnergyHandler {
	@Nullable
	default EnergyTier getInputEnergyTier() {
		return null;
	}

	@Nullable
	default EnergyTier getOutputEnergyTier() {
		return null;
	}

	double getEnergyCapacity();

	double getEnergy();

	void setEnergy(double energy);

	default double insertEnergy(double maxInsert, boolean simulate) {
		EnergyTier tier = getInputEnergyTier();

		if (tier == null) {
			return 0;
		}

		double energy = getEnergy();
		double energyReceived = Math.min(getEnergyCapacity() - energy, Math.min(tier.itemTransferRate, maxInsert));

		if (!simulate && energyReceived > 0D) {
			setEnergy(energy + energyReceived);
		}

		return energyReceived;
	}

	default double extractEnergy(double maxExtract, boolean simulate) {
		EnergyTier tier = getOutputEnergyTier();

		if (tier == null) {
			return 0;
		}

		double energy = getEnergy();
		double energyExtracted = Math.min(energy, Math.min(tier.itemTransferRate, maxExtract));

		if (!simulate && energyExtracted > 0D) {
			setEnergy(energy - energyExtracted);
		}

		return energyExtracted;
	}
}
