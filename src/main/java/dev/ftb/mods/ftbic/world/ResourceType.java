package dev.ftb.mods.ftbic.world;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public enum ResourceType {
	ORE(1, "ore"),
	INGOT(2, "ingot"),
	RAW(3, "raw", true),
	DUST(4, "dust"),
	NUGGET(5, "nugget"),
	PLATE(6, "plate"),
	ROD(7, "rod"),
	GEAR(8, "gear"),
	WIRE(9, "wire"),
	BLOCK(10, "block");

	public static final List<ResourceType> VALUES = Arrays.stream(ResourceType.values()).sorted(Comparator.comparing(e -> e.index)).toList();

	final int index;
	final int bit;
	private final String token;
	private final boolean prefix;

	ResourceType(int index, String token) {
		this(index, token, false);
	}

	ResourceType(int index, String token, boolean prefix) {
		this.index = index;
		this.bit = 1 << index;
		this.token = token;
		this.prefix = prefix;
	}

	public String idFor(ResourceElements element) {
		String name = element.getName().toLowerCase(Locale.ROOT);
		return prefix ? token + "_" + name : name + "_" + token;
	}

	public String token() {
		return token;
	}
}
