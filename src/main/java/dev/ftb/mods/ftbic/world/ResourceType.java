package dev.ftb.mods.ftbic.world;

import java.util.Arrays;
import java.util.List;

public enum ResourceType {
	ORE(1),
	INGOT(2),
	CHUNK(4),
	DUST(8),
	NUGGET(16),
	PLATE(32),
	ROD(64),
	GEAR(128);

	public static final List<ResourceType> VALUES = Arrays.stream(ResourceType.values()).toList();

	final int bit;

	ResourceType(int bit) {
		this.bit = bit;
	}
}
