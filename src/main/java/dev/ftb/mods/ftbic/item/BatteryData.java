package dev.ftb.mods.ftbic.item;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BatteryData implements ICapabilityProvider, IEnergyStorage {
	private final BatteryItem batteryItem;
	private final ItemStack item;
	private final LazyOptional<?> thisOptional;

	public BatteryData(BatteryItem b, ItemStack is) {
		batteryItem = b;
		item = is;
		thisOptional = LazyOptional.of(() -> this);
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
		return capability == CapabilityEnergy.ENERGY ? thisOptional.cast() : LazyOptional.empty();
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (batteryItem.batteryType.creative) {
			return maxReceive;
		} else if (!canReceive()) {
			return 0;
		}

		int energyReceived = Math.min(getMaxEnergyStored() - getEnergyStored(), Math.min(batteryItem.tier.batteryTransferRate, maxReceive));

		if (!simulate) {
			setEnergyStored(getEnergyStored() + energyReceived);
		}

		return energyReceived;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		if (batteryItem.batteryType.creative) {
			return maxExtract;
		}

		int energyExtracted = Math.min(getEnergyStored(), Math.min(batteryItem.tier.batteryTransferRate, maxExtract));

		if (!simulate) {
			setEnergyStored(getEnergyStored() - energyExtracted);
		}

		return energyExtracted;
	}

	public void setEnergyStored(int energy) {
		item.getOrCreateTag().putInt("Energy", energy);
	}

	@Override
	public int getEnergyStored() {
		return batteryItem.batteryType.creative ? batteryItem.capacity / 2 : item.getOrCreateTag().getInt("Energy");
	}

	@Override
	public int getMaxEnergyStored() {
		return batteryItem.capacity;
	}

	@Override
	public boolean canExtract() {
		return true;
	}

	@Override
	public boolean canReceive() {
		return !batteryItem.batteryType.singleUse;
	}
}
