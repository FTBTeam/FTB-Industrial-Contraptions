package dev.ftb.mods.ftbic.item;

public enum BatteryType {
	RECHARGEABLE(false, false),
	SINGLE_USE(true, false),
	CREATIVE(false, true);

	public final boolean singleUse;
	public final boolean creative;

	BatteryType(boolean s, boolean c) {
		singleUse = s;
		creative = c;
	}
}
