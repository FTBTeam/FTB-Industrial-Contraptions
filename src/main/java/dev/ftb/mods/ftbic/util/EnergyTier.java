package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBICConfig;

public enum EnergyTier {
	LV("lv", 1, FTBICConfig.LV_TRANSFER_RATE),
	MV("mv", 2, FTBICConfig.MV_TRANSFER_RATE),
	HV("hv", 3, FTBICConfig.HV_TRANSFER_RATE),
	EV("ev", 4, FTBICConfig.EV_TRANSFER_RATE),
	IV("xv", 4, FTBICConfig.IV_TRANSFER_RATE);

	public static final EnergyTier[] VALUES = values();

	public final String name;
	private final int up;
	public final double transferRate;
	public final double itemTransferRate;

	EnergyTier(String n, int u, double t) {
		name = n;
		up = u;
		transferRate = t;
		itemTransferRate = transferRate * FTBICConfig.ITEM_TRANSFER_EFFICIENCY;
	}

	public EnergyTier up() {
		return VALUES[up];
	}
}
