package dev.ftb.mods.ftbic.world;

import java.util.Arrays;
import java.util.List;

public enum ResourceType {
	ORE(1),
	INGOT(2),
	CHUNK(3),
	DUST(4),
	NUGGET(5),
	PLATE(6),
	ROD(7),
	GEAR(8),
	WIRE(9),
	BLOCK(10);

	public static final List<ResourceType> VALUES = Arrays.stream(ResourceType.values()).toList();

	final int index;
	final int bit;

	ResourceType(int index) {
		this.index = index;
		this.bit = 1 << index;
	}
}
