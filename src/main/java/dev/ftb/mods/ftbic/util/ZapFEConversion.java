package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBICConfig;

public final class ZapFEConversion {
	private ZapFEConversion() {}

	public static double rate() {
		return FTBICConfig.ENERGY.ZAP_TO_FE_CONVERSION_RATE.get();
	}

	public static int zapsToFEFloor(double zaps) {
		if (zaps <= 0D) return 0;
		double fe = zaps * rate();
		if (fe >= (double) Integer.MAX_VALUE) return Integer.MAX_VALUE;
		return (int) Math.floor(fe);
	}

	public static double feToZapsCeil(int fe) {
		if (fe <= 0) return 0D;
		return Math.ceil(fe / rate());
	}

	public static double feToZapsFloor(int fe) {
		if (fe <= 0) return 0D;
		return Math.floor(fe / rate());
	}
}
