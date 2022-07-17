package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftblibrary.snbt.config.DoubleValue;

import java.util.function.Supplier;

public enum EnergyTier {
	LV("lv", 1, FTBICConfig.ENERGY.LV_TRANSFER_RATE),
	MV("mv", 2, FTBICConfig.ENERGY.MV_TRANSFER_RATE),
	HV("hv", 3, FTBICConfig.ENERGY.HV_TRANSFER_RATE),
	EV("ev", 4, FTBICConfig.ENERGY.EV_TRANSFER_RATE),
	IV("xv", 4, FTBICConfig.ENERGY.IV_TRANSFER_RATE);

	public static final EnergyTier[] VALUES = values();

	public final String name;
	private final int up;
	public final Supplier<Double> transferRate;

	EnergyTier(String n, int u, DoubleValue t) {
		name = n;
		up = u;
		transferRate = t::get;
	}

	public EnergyTier up() {
		return VALUES[up];
	}
}
