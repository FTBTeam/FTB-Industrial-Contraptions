package dev.ftb.mods.ftbic.util;

import net.minecraft.core.Direction;

public interface EnergyHandler {
	default double getMaxInputEnergy() {
		return 0D;
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
		if (isBurnt()) {
			return 0D;
		}

		double max = getMaxInputEnergy();

		if (max <= 0D) {
			return 0;
		}

		if (maxInsert > max && canBurn()) {
			setBurnt(true);
			return maxInsert;
		}

		double energy = getEnergy();
		double energyReceived = Math.min(getEnergyCapacity() - energy, Math.min(max, maxInsert));

		if (!simulate && energyReceived > 0D) {
			setEnergyRaw(energy + energyReceived);
			energyChanged(energy);
		}

		return energyReceived;
	}

	default void energyChanged(double prevEnergy) {
	}

	default boolean canBurn() {
		return false;
	}

	default void setBurnt(boolean burnt) {
	}

	default boolean isBurnt() {
		return false;
	}

	default boolean isValidEnergyInputSide(Direction direction) {
		return true;
	}
}
