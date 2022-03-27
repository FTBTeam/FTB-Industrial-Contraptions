package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBICConfig;

public enum EnergyTier {
	LV("lv", 1, FTBICConfig.ENERGY.LV_TRANSFER_RATE.get()),
	MV("mv", 2, FTBICConfig.ENERGY.MV_TRANSFER_RATE.get()),
	HV("hv", 3, FTBICConfig.ENERGY.HV_TRANSFER_RATE.get()),
	EV("ev", 4, FTBICConfig.ENERGY.EV_TRANSFER_RATE.get()),
	IV("xv", 4, FTBICConfig.ENERGY.IV_TRANSFER_RATE.get());

	public static final EnergyTier[] VALUES = values();

	public final String name;
	private final int up;
	public final double transferRate;

	EnergyTier(String n, int u, double t) {
		name = n;
		up = u;
		transferRate = t;
	}

	public EnergyTier up() {
		return VALUES[up];
	}
}
