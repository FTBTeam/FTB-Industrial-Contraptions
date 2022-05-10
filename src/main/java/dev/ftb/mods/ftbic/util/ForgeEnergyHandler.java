package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBICConfig;
import net.minecraft.util.Mth;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public class ForgeEnergyHandler implements EnergyHandler {
	public final LazyOptional<IEnergyStorage> optional;
	public final IEnergyStorage storage;

	public ForgeEnergyHandler(LazyOptional<IEnergyStorage> o, IEnergyStorage s) {
		optional = o;
		storage = s;
	}

	@Override
	public boolean isEnergyHandlerInvalid() {
		return !optional.isPresent();
	}

	@Override
	public double getEnergyCapacity() {
		return storage.getMaxEnergyStored() / FTBICConfig.ENERGY.ZAP_TO_FE_CONVERSION_RATE.get();
	}

	@Override
	public double getEnergy() {
		return storage.getEnergyStored() / FTBICConfig.ENERGY.ZAP_TO_FE_CONVERSION_RATE.get();
	}

	@Override
	public void setEnergyRaw(double e) {
		if (storage.getEnergyStored() <= 0 || storage.extractEnergy(storage.getEnergyStored(), false) > 0) {
			storage.receiveEnergy(Mth.ceil(e * FTBICConfig.ENERGY.ZAP_TO_FE_CONVERSION_RATE.get()), false);
		}
	}

	@Override
	public double insertEnergy(double maxInsert, boolean simulate) {
		return storage.receiveEnergy(Mth.ceil(maxInsert * FTBICConfig.ENERGY.ZAP_TO_FE_CONVERSION_RATE.get()), simulate) / FTBICConfig.ENERGY.ZAP_TO_FE_CONVERSION_RATE.get();
	}
}
