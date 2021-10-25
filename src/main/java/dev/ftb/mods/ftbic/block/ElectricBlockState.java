package dev.ftb.mods.ftbic.block;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public enum ElectricBlockState implements StringRepresentable {
	OFF("off"),
	ON("on"),
	BURNT("burnt");

	public static final EnumProperty<ElectricBlockState> ON_OFF = EnumProperty.create("state", ElectricBlockState.class, ON, OFF);
	public static final EnumProperty<ElectricBlockState> ON_OFF_BURNT = EnumProperty.create("state", ElectricBlockState.class, ON, OFF, BURNT);

	private final String name;

	ElectricBlockState(String n) {
		name = n;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
