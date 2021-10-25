package dev.ftb.mods.ftbic.block;

import net.minecraft.util.StringRepresentable;

public enum ElectricBlockState implements StringRepresentable {
	OFF("off"),
	ON("on"),
	BURNT("burnt");

	private final String name;

	ElectricBlockState(String n) {
		name = n;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
