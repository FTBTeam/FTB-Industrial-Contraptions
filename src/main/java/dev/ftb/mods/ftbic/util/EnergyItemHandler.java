package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.registry.ModDataComponents;
import net.minecraft.world.item.ItemStack;

public interface EnergyItemHandler {
	double getEnergyCapacity(ItemStack stack);

	default double getEnergy(ItemStack stack) {
		if (isCreativeEnergyItem()) {
			return getEnergyCapacity(stack) / 2D;
		}
		Double stored = stack.get(ModDataComponents.ENERGY.get());
		return stored == null ? 0D : stored;
	}

	default void setEnergy(ItemStack stack, double energy) {
		double prev = getEnergy(stack);

		if (prev != energy) {
			setEnergyRaw(stack, energy);
			energyChanged(stack, prev);
		}
	}

	default void setEnergyRaw(ItemStack stack, double energy) {
		if (!isCreativeEnergyItem()) {
			stack.set(ModDataComponents.ENERGY.get(), energy);
		}
	}

	default double insertEnergy(ItemStack stack, double maxInsert, boolean simulate) {
		if (isCreativeEnergyItem()) {
			return maxInsert;
		} else if (!canInsertEnergy()) {
			return 0;
		}

		double energy = getEnergy(stack);
		double energyReceived = Math.min(getEnergyCapacity(stack) - energy, maxInsert);

		if (!simulate && energyReceived > 0D) {
			setEnergyRaw(stack, energy + energyReceived);
			energyChanged(stack, energy);
		}

		return energyReceived;
	}

	default double extractEnergy(ItemStack stack, double maxExtract, boolean simulate) {
		if (isCreativeEnergyItem()) {
			return maxExtract;
		} else if (!canExtractEnergy()) {
			return 0;
		}

		double energy = getEnergy(stack);
		double energyExtracted = Math.min(energy, maxExtract);

		if (!simulate && energyExtracted > 0D) {
			setEnergyRaw(stack, energy - energyExtracted);
			energyChanged(stack, energy);
		}

		return energyExtracted;
	}

	default boolean canInsertEnergy() {
		return true;
	}

	default boolean canExtractEnergy() {
		return false;
	}

	default boolean isCreativeEnergyItem() {
		return false;
	}

	default void energyChanged(ItemStack stack, double prevEnergy) {
	}

	static String formatEnergy(double value) {
		if (value >= 1_000_000D) return String.format("%.1fM", value / 1_000_000D);
		if (value >= 1_000D) return String.format("%.1fk", value / 1_000D);
		return String.format("%.0f", value);
	}
}
