package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBICConfig;

public enum PowerTier {
	LV("lv", FTBICConfig.LV_TRANSFER_RATE),
	MV("mv", FTBICConfig.MV_TRANSFER_RATE),
	HV("hv", FTBICConfig.HV_TRANSFER_RATE),
	EV("ev", FTBICConfig.EV_TRANSFER_RATE);

	public static final PowerTier[] VALUES = values();

	public final String name;
	public final int transferRate;
	public final int batteryTransferRate;

	PowerTier(String n, int t) {
		name = n;
		transferRate = t;
		batteryTransferRate = (int) (transferRate * FTBICConfig.BATTERY_TRANSFER_EFFICIENCY);
	}

	public PowerTier up() {
		switch (this) {
			case LV:
				return MV;
			case MV:
				return HV;
			default:
				return EV;
		}
	}
}
