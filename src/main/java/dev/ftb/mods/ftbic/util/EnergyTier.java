package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBICConfig;

public enum EnergyTier {
	LV("lv", 1),
	MV("mv", 2),
	HV("hv", 3),
	EV("ev", 4),
	IV("iv", 4);

	public static final EnergyTier[] VALUES = values();

	public final String name;
	private final int up;

	EnergyTier(String n, int u) {
		name = n;
		up = u;
	}

	public double transferRate() {
		return switch (this) {
			case LV -> FTBICConfig.ENERGY.LV_TRANSFER_RATE.get();
			case MV -> FTBICConfig.ENERGY.MV_TRANSFER_RATE.get();
			case HV -> FTBICConfig.ENERGY.HV_TRANSFER_RATE.get();
			case EV -> FTBICConfig.ENERGY.EV_TRANSFER_RATE.get();
			case IV -> FTBICConfig.ENERGY.IV_TRANSFER_RATE.get();
		};
	}

	public EnergyTier up() {
		return VALUES[up];
	}
}
