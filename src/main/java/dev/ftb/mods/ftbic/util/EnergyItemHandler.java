package dev.ftb.mods.ftbic.util;

import net.minecraft.nbt.DoubleTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItem;

public interface EnergyItemHandler extends IForgeItem {
	double getEnergyCapacity(ItemStack stack);

	default double getEnergy(ItemStack stack) {
		return isCreativeEnergyItem() ? getEnergyCapacity(stack) / 2D : stack.hasTag() ? stack.getTag().getDouble("Energy") : 0D;
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
			stack.addTagElement("Energy", DoubleTag.valueOf(energy));
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

	@Override
	default boolean showDurabilityBar(ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains("Energy");
	}

	@Override
	default double getDurabilityForDisplay(ItemStack stack) {
		return 1D - getEnergy(stack) / getEnergyCapacity(stack);
	}

	@Override
	default int getRGBDurabilityForDisplay(ItemStack stack) {
		return 0xFFFF0000;
	}
}
