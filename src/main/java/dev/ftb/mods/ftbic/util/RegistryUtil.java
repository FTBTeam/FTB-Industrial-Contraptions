package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBIC;

public class RegistryUtil {

	/**
	 * Returns the provide string key, prefixed with "<modid>:"
	 * @param key The key to prefix
	 * @return the prefixed string
	 */
	public static String withModId(String key) {
		return FTBIC.MOD_ID + ":" + key;
	}
}
