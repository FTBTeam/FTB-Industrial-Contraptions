package dev.ftb.mods.ftbic.screen.sync;

import java.util.function.BooleanSupplier;

public class BooleanValue extends SyncedDataValue<Boolean> {
	private final BooleanSupplier value;

	public BooleanValue(BooleanSupplier v) {
		value = v;
	}

	@Override
	public void write(int[] data) {
		data[index] = value.getAsBoolean() ? 1 : 0;
	}

	@Override
	public Boolean read(int[] data) {
		return data[index] != 0;
	}
}