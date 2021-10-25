package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBICConfig;

public class FTBICUtils {
	public static String formatPower(int power) {
		return String.format(FTBICConfig.ENERGY_FORMAT_1, power);
	}

	public static String formatPower(int power, int cap) {
		return String.format(FTBICConfig.ENERGY_FORMAT_2, power, cap);
	}
}
