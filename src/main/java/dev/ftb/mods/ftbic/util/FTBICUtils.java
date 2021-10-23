package dev.ftb.mods.ftbic.util;

public class FTBICUtils {
	public static String formatPower(int power) {
		return String.format("%,d", power);
	}

	public static String formatPower(int power, int cap) {
		return String.format("%,d / %,d", power, cap);
	}
}
